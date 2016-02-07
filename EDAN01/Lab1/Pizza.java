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

		IntVar[] bought = new IntVar[n2];
		IntVar[] free = new IntVar[n2];
		for (int i = 0; i<n2; i++){
			bought[i] = new IntVar(store, "bought" + i, 0, 1);
			free[i] = new IntVar(store, "free" + i, 0, 1);
			store.impose(new XneY(bought[i], free[i]));

		}
		int maxPrice = 0;
		for (int p : price) {
			maxPrice += price;
		}

		// IntVar pizzaCost = new IntVar(store, "pizzaCost", 0, maxPrice);


		// IntVar coup1 = new IntVar(store, "coup1", buy[1], buy[1]+free[1]);
		// IntVar coup2 = new IntVar(store, "coup2", buy[2], buy[2]+free[2]);
		// IntVar coup3 = new IntVar(store, "coup3", buy[3], buy[3]+free[3]);
		// IntVar coup4 = new IntVar(store, "coup4", buy[4], buy[4]+free[4]);
		// IntVar coup5 = new IntVar(store, "coup5", buy[5], buy[5]+free[5]);
		// IntVar coup6 = new IntVar(store, "coup6", buy[6], buy[6]+free[6]);
		// IntVar coup7 = new IntVar(store, "coup7", buy[7], buy[7]+free[7]);

		// IntVar maxPizza = new IntVar(store, "maxPizza", n, n);

		// IntVar[] vouchers = {coup1, coup2, coup3, coup4, coup5, coup6, coup7};
		// for (int i = 0; i< m; i++) {
		// 	store.impose(new XltC(vouchers[i], 2));
		// }

		// store.impose(new SumInt(store, vouchers, "==", maxPizza));


		store.impose(new LinearInt(store, bought, price, "<=", maxPrice));


	}


}
