import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.search.*;

public class Pizza {

    public static void main(String[] args) {
        int n = 4;
        int[] price = {10, 5, 20, 15};
        int m = 2;
        int[] buy = {1, 2};
        int[] free = {1, 1};

        System.out.println("4st pizzor, nr 1 " );
        calculate(n, price, m, buy, free);

        int n2 = 4;
        int[] price2 = {10, 15, 20, 15};
        int m2 = 7;
        int[] buy2 = {1, 2, 2, 8, 3, 1, 4};
        int[] free2 = {1, 1, 2, 9, 1, 0, 1};

        System.out.println("4st pizzor, nr 2");
        calculate(n2, price2, m2, buy2, free2);

        int n3 = 10;
        int[] price3 = {70, 10, 60, 60, 30, 100, 60, 40, 60, 20};
        int m3 = 4;
        int[] buy3 = {1, 2, 1, 1};
        int[] free3 = {1, 1, 1, 0};

        System.out.println("10st pizzor");
        calculate(n3, price3, m3, buy3, free3);
    }

    static void calculate(int n, int[] price, int m, int[] buy, int[] free) {
        Store store = new Store();
        IntVar[] boughtPizzas = new IntVar[n];
        IntVar[] freePizzas = new IntVar[n];
        IntVar[][] voucherBought = new IntVar[m][n];
        IntVar[][] voucherFree = new IntVar[m][n];


//      samma pizza kan bara fås en gång
        for (int i = 0; i < n; i++) {
            boughtPizzas[i] = new IntVar(store, 0, 1);
            freePizzas[i] = new IntVar(store, 0, 1);
            store.impose(new XneqY(boughtPizzas[i], freePizzas[i]));
        }

//      man måste få tag i alla (n st) pizzor
        IntVar gottenPizzas = new IntVar(store, n, n);

//      mergar de två vektorerna bought och free pizzas
        IntVar[] totalPizzas = new IntVar[boughtPizzas.length + freePizzas.length];
        int index = 0;
        for (int i = 0; i < boughtPizzas.length; i++) {
            totalPizzas[index] = boughtPizzas[i];
            index++;
            totalPizzas[index] = freePizzas[i];
            index++;
        }
        store.impose(new SumInt(store, totalPizzas, "==", gottenPizzas));


//      man kan ej få en pizza gratis om man har köpt den
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                voucherBought[i][j] = new IntVar(store, 0, 1);
                voucherFree[i][j] = new IntVar(store, 0, 1);
                store.impose(new Not(new XplusYeqC(voucherBought[i][j], voucherFree[i][j], 2)));
            }
        }

//      1-2. en pizza kan inte bli använd på 2 olika vouchers
//      3-4. fixar relationen mellan matrisen och boughtvektorn. De skall stämma överrens.
        for (int i = 0; i < n; i++) {
            store.impose(new SumInt(store, getRow(voucherBought, i), "<=", new IntVar(store, 1, 1)));
            store.impose(new SumInt(store, getRow(voucherFree, i), "<=", new IntVar(store, 1, 1)));

            store.impose(new SumInt(store, getRow(voucherBought, i), "==", boughtPizzas[i]));
            store.impose(new SumInt(store, getRow(voucherFree, i), "==", freePizzas[i]));
        }


//        om antalet köpta pizzor är tillräckligt, så får man upp till antalet gratispizzor, annars inga (0).
        for (int i = 0; i < m; i++) {
            PrimitiveConstraint nbrPaidPizzas = new SumInt(store, voucherBought[i], ">=", new IntVar(store, buy[i], buy[i]));
            PrimitiveConstraint nbrFreePizzas = new SumInt(store, voucherFree[i], "<=", new IntVar(store, free[i], free[i]));
            PrimitiveConstraint zero = new SumInt(store, voucherFree[i], "==", new IntVar(store, 0, 0));
            store.impose(new IfThenElse(nbrPaidPizzas, nbrFreePizzas, zero));
        }


//        då pizzorna är sorterade i prisordning, så får inga gratispizzor någonsin vara dyrare
//        än den billigaste av de som köpts. (för varje enskilld voucher)
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = j - 1; k >= 0; k--) {
                    PrimitiveConstraint c1 = new XeqC(voucherBought[i][j], 1);
                    PrimitiveConstraint c2 = new XeqC(voucherFree[i][k], 1);
                    store.impose(new Not(new And(c1, c2)));
                }
            }
        }


        int totPrice = 0;
        for (int i = 0; i < price.length; i++) {
            totPrice += price[i];
        }
        IntVar cost = new IntVar(store, 0, totPrice);

        sort(price);
//        weightar köpta pizzorna mot priset för att få totalkostnaden.
        store.impose(new SumWeight(boughtPizzas, price, cost));

//        mergar mastriserna voucherbought o voucherfree för att kunna söka över
        IntVar[][] mergedMatrix = new IntVar[voucherBought.length + voucherFree.length][n];
        index = 0;
        for (int i = 0; i < voucherBought.length; i++) {
            for (int e = 0; e < voucherBought[0].length; e++) {
                mergedMatrix[index][e] = voucherBought[i][e];
            }
            index++;
        }
        for (int i = 0; i < voucherFree.length; i++) {
            for (int e = 0; e < voucherFree[0].length; e++) {
                mergedMatrix[index][e] = voucherFree[i][e];
            }
            index++;

        }


        Search<IntVar> search = new DepthFirstSearch<IntVar>();

        SelectChoicePoint<IntVar> select = new SimpleMatrixSelect<IntVar>(mergedMatrix, null, new IndomainMin<IntVar>());


        boolean result = search.labeling(store, select, cost);

        if (result)

        {
            System.out.print("Solution found with " + n + " pizzas and " + m + "vouchers with a totalcost of " + cost + "\n");

        } else

        {
            System.out.println("No solution found. \n");
        }

    }

    //får fram hela kolumnen i en matris
    private static IntVar[] getRow(IntVar[][] matrix, int row) {
        IntVar[] col = new IntVar[matrix.length];
        for (int j = 0; j < matrix.length; j++) {
            col[j] = matrix[j][row];
        }
        return col;
    }

    //    sorterar med högst först.
    public static void sort(int[] array) {

        int o = array.length;
        int temp = 0;

        for (int i = 0; i < o; i++) {
            for (int j = 1; j < (o - i); j++) {

                if (array[j - 1] < array[j]) {
                    temp = array[j - 1];
                    array[j - 1] = array[j];
                    array[j] = temp;
                }

            }
        }
    }
}
