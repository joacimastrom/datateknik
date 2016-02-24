import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.search.*;

public class UrbanPlanner {
	public static void main(String[] args) {
		long T1, T2, T;
		T1 = System.currentTimeMillis();
		inputs(Integer.parseInt(args[0]));
		T2 = System.currentTimeMillis();
		T = T2 - T1;
		System.out.println("Execution time = " + T + " ms");
	}

	private static void solve(int n, int n_commercial, int n_residential, int[] point_distribution){
		Store store = new Store();

		// Create binary matrix and vector, comm = 0, res = 1
		IntVar[] vec = new IntVar[n*n];
		IntVar[][] rowsAndCols = new IntVar[n*2][n];
		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++){
				IntVar c = new IntVar(store, 0, 1);
				vec[i*n + j] = c;
				rowsAndCols[i][j] = c;
				rowsAndCols[n+j][i] = c;
			}
		}

		// Set number of res
		IntVar residential = new IntVar(store, n_residential, n_residential);
		store.impose(new SumInt(store, vec, "=", residential));

		// Symmetry breaking constraints
		for(int i = 1; i<n;i++){
			store.impose(new LexOrder(rowsAndCols[i-1],rowsAndCols[i],false));
			store.impose(new LexOrder(rowsAndCols[n+i-1],rowsAndCols[n+i],false));
		}

		// Setup score system and point distribution
		IntVar rowcolsums[] = new IntVar[2*n];
		for (int i = 0; i < 2*n; i++) {
			rowcolsums[i] = new IntVar(store);
			for(int j = 0; j<point_distribution.length; j++){
				rowcolsums[i].addDom(point_distribution[j],point_distribution[j]);
			}
			IntVar sum = new IntVar(store, 0, n);
			store.impose(new SumInt(store, rowsAndCols[i], "=", sum));
			store.impose(new Element(sum, point_distribution, rowcolsums[i], -1));
		}

		// Setup search variables with mirrored max for minimal search
		IntVar max = new IntVar(store, 0, 100);
		IntVar negativeMax = new IntVar(store, -100, 0);
		IntVar zero = new IntVar(store, 0, 0);
		store.impose(new SumInt(store, rowcolsums, "=", max));
		store.impose(new XplusYeqZ(max, negativeMax, zero));

		Search<IntVar> label = new DepthFirstSearch<IntVar>();
		SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(vec,
		null,
		new IndomainMin<IntVar>());
		boolean Result = label.labeling(store, select, negativeMax);

		if (Result) {
			System.out.println("Solution:");
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
						System.out.print(rowsAndCols[i][j].value() + " ");
				}
				System.out.println();
			}
			System.out.println("Total points: " + max.value());
		} else {
			System.out.println("No solution found");
		}
	}

	public static void inputs(int i){
		int n = 0;
		int n_commercial = 0;
		int n_residential = 0;

		switch(i) {
			case 1:
			n = 5;
			n_commercial = 13;
			n_residential = 12;
			int[] point_distribution1 = {-5, -4, -3, 3, 4, 5};
			solve(n, n_commercial, n_residential, point_distribution1);
			break;
			case 2:
			n = 5;
			n_commercial = 7;
			n_residential = 18;
			int[] point_distribution2 = {-5, -4, -3, 3, 4, 5};
			solve(n, n_commercial, n_residential, point_distribution2);
			break;
			case 3:
			n = 7;
			n_commercial = 20;
			n_residential = 29;
			int[] point_distribution3 = {-7, -6, -5, -4, 4, 5, 6, 7};
			solve(n, n_commercial, n_residential, point_distribution3);
			break;
		}
	}
}
