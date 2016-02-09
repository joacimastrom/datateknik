import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.search.*;

public class Pizza {

	public static void main(String[] args) {
		// int n = 4;
		// int[] price = { 10, 5, 20, 15 };
		// int m = 2;
		// int[] buy = {1, 2};
		// int[] free = {1, 1};
		//
		// System.out.println("4st pizzor, nr 1");
		// calculate(n, price, m, buy, free);

		int n2 = 4;
		int[] price2 = { 10, 15, 20, 15 };
		int m2 = 7;
		int[] buy2 = { 1, 2, 2, 8, 3, 1, 4 };
		int[] free2 = { 1, 1, 2, 9, 1, 0, 1 };

		System.out.println("4st pizzor, nr 2");
		calculate(n2, price2, m2, buy2, free2);

		// int n3 = 10;
		// int[] price3 = {70, 10, 60, 60, 30, 100, 60, 40, 60, 20 };
		// int m3 = 4;
		// int[] buy3 = { 1, 2, 1, 1 };
		// int[] free3 = { 1, 1, 1, 0 };
		//
		// System.out.println("10st pizzor");
		// calculate(n3, price3, m3, buy3, free3);
	}

	static void calculate(int n, int[] price, int m, int[] buy, int[] free) {
		Store store = new Store();


		IntVar[] usedCoup = new IntVar[m];
	//	IntVar[] coupBuy = new IntVar[m];
	//	IntVar[] coupFree = new IntVar[m];
		IntVar[][] coupBought = new IntVar[m][n];
		IntVar[][] coupFree = new IntVar[m][n];
		IntVar[][] totPiz = new IntVar[n][m];
		IntVar[][] coupBoughtPrice = new IntVar[m][n];
		IntVar[][] coupFreePrice = new IntVar[m][n];
		IntVar one = new IntVar(store, "one", 1, 1);
		for (int i = 0; i < m; i++){
			for (int j = 0; j < n; j++){
				coupBought[i][j] = new IntVar(store, "coupBought" + i + j, 0, 1);
				coupFree[i][j] = new IntVar(store, "coupFree" + i + j, 0, 1);
				totPiz[j][i] = new IntVar(store, "totPiz" + j + i, 0, 1);
				store.impose(new XplusYlteqZ(coupBought[i][j], coupFree [i][j], one));
				coupBoughtPrice[i][j] = new IntVar(store, "coupBoughtPrice" + i + j, 0, 100);
				coupFreePrice[i][j] = new IntVar(store, "coupFreePrice" + i + j, 0, 100);
				PrimitiveConstraint c1 = new XeqY(coupBought[i][j], 1);
				PrimitiveConstraint c2 = new XeqY(coupBoughtPrice[i][j], price[j]);
				PrimitiveConstraint c3 = new Xeqy(coupBoughtPrice[i][j], 100);
				PrimitiveConstraint c4 = new XeqY(coupFree[i][j], 1);
				PrimitiveConstraint c5 = new XeqY(coupFreePrice[i][j], price[j]);
				PrimitiveConstraint c6 = new XeqY(coupFreePrice[i][j], 0);
				store.impose(new IfThenElse(c1, c2, c3));
				store.impose(new IfThenElse(c4, c5, c6));
				store.impose(new XplusYeqZ(coupBought[i][j], coupFree[i][j], totPiz[j][i]));
			}
			store.impose(new XlteqY(max(coupFreePrice[i]), min(coupBoughtPrice[i])));
		}

		for (int i = 0; i<n; i++){
			store.impose(new Sum(totPiz[i], one));
		}

		for (int i = 0; i<m; i++) {
			store.impose(new LinearInt(store, coupBought[i], {1, 1, 1, 1}, "=", buy[i]));
			store.impose(new LinearInt(store, coupFree[i], {1, 1, 1, 1} "<=", free[i]);




		}

		// IntVar[] bought = new IntVar[n];
		// IntVar[] fre = new IntVar[n];
		// for (int i = 0; i<m; i++){
		// 	usedCoup[i] = new IntVar(store, "coup" + i, 0, 1);
		// 	coupBuy = new IntVar(store, "coupBuy"+i, buy[i], buy[i]);
		// 	coupFree = new Intvar(store, "coupFree"+i, 0, free[i]);
		// }
		// for (int i = 0; i<n; i++){
		// 	bought[i] = new IntVar(store, "bought" + i, 0, 1);
		// 	fre[i] = new IntVar(store, "free" + i, 0, 1);
		// 	store.impose(new XneqY(bought[i], fre[i]));
		//
		// }
		int maxPrice = 0;
		for (int p : price) {
			maxPrice = maxPrice + p;
		}

		store.impose(new LinearInt(store, bought, price, "<=", maxPrice));
		store.impose(new LinearInt );


	}


}
