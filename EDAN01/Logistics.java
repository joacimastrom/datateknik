import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.search.*;
import java.util.ArrayList;
import java.util.Arrays;


public class Logistics {

  public static void main(String[] args) {

    calc(6, 1, 1, new int[]{6}, 7, new int[]{1, 1, 2, 2, 3, 4, 4}, new int[]{2, 3, 3, 4, 5, 5, 6}, new int[]{4, 2, 5, 10, 3, 4, 11});
    calc(6, 1, 2, new int[]{5, 6}, 7, new int[]{1, 1, 2, 2, 3, 4, 4}, new int[]{2, 3, 3, 4, 5, 5, 6}, new int[]{4, 2, 5, 10, 3, 4, 11});
    calc(6, 1, 2, new int[]{5, 6}, 9, new int[]{1, 1, 1, 2, 2, 3, 3, 3, 4}, new int[]{2, 3, 4, 3, 5, 4, 5, 6, 6}, new int[]{6, 1, 5, 5, 3, 5, 6, 4, 2});

  }

  public static void calc(int size, int start, int nbrDest, int[] dests, int vecs, int[] from, int[] to, int[] cost) {
    Store store = new Store();
    IntVar[] vectors = new IntVar[vecs];
    IntVar[][] adj = new IntVar[size+1][size+1];
   ArrayList<Integer> destList = new ArrayList<>();
   for (int i : dests) {
     destList.add(i);
   }

    for (int i = 0; i <= size ; i++) {
        for (int j = 0; j < size + 1; j++) {
            adj[i][j] = new IntVar(store, 0, 0);
        }
    }

    for (int i = 0; i < vecs; i++) {
      IntVar c = new IntVar(store, 0, 1);
      vectors[i] = c;
      adj[from[i]][to[i]] = c;
      adj[to[i]][from[i]] = c;
    }

    for (int i = 1; i<=size; i++) {
      if (i == start || destList.contains(i)) {
        store.impose(new SumInt(store, adj[i], ">=", new IntVar(store, 1, 1)));
      } else {
        store.impose(new SumInt(store, adj[i], "!=", new IntVar(store, 1, 1)));
      }
    }

    IntVar tot = new IntVar(store, "tot", 0, 1000);
    store.impose(new SumWeight(vectors, cost, tot));

    Search<IntVar> label = new DepthFirstSearch<IntVar>();
    SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(vectors,
    null,
    new IndomainMin<IntVar>());

    label.setSolutionListener(new PrintOutListener<IntVar>());

    boolean Result = label.labeling(store, select, tot);

    if (Result) {
      System.out.println("\n*** Yes");
      System.out.println("Solution : "+ java.util.Arrays.asList(tot));
    }
    else System.out.println("\n*** No");
  }

}
