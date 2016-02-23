include "globals.mzn";

var int: endTime;

% specify nr of adds/mults
int: adders = 2;
int: multers = 4;

int: nodes = 28;

array[1..2] of int: endNodes = [27, 28];

%specify tree
array[1..30] of int: from = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 12, 13, 13, 14, 15, 16, 17, 18, 18, 19, 19, 20, 21, 22, 23, 24, 25];
array[1..30] of int: to = [8, 8, 9, 9, 10, 10, 11, 11, 26, 12, 13, 27, 14, 16, 15, 17, 18, 18, 19, 19, 22, 20, 23, 21, 24, 24, 25, 25, 26, 27];
array[1..nodes] of int: opTimes = [2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1];


% X-led
array[1..nodes] of var 0..100: ops;

% Y-led
array[1..nodes] of var 1..adders+multers: resources;
constraint forall(i in 1..nodes)(if opTimes[i] == 1 then resources[i] <= adders else resources[i] > adders endif);

% DX
array[1..nodes] of var 1..2: totTimes;
constraint forall(i in 1..nodes)(if opTimes[i] == 1 then totTimes[i] < 2 else totTimes[i] > 1 endif);

% DY
array[1..nodes] of var 1..1: totCost;

%diffn for adders
constraint diffn(ops, resources, totTimes, totCost);

%set dependencies
constraint forall(i in index_set(from))((ops[from[i]+1] + opTimes[from[i]+1] <= ops[to[i]+1]));

%set endNodes
constraint forall(i in index_set(endNodes))(ops[endNodes[i]] + opTimes[endNodes[i]] <= endTime);

solve :: seq_search([
      int_search(ops, first_fail, indomain_min, complete),
      int_search(resources, first_fail, indomain_min, complete)])
   minimize endTime;


output ["Clock cycles: ", show(endTime), show(resources), show(ops)];

% mzn2fzn -G jacop arfGen.mzn
% fzn-jacop -s -a arfGen.fzn
