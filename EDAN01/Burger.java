
import org.jacop.constraints.*;
import org.jacop.core.*;
import org.jacop.search.*;

public class Burger {
  public static void main (String[] args) {
    long T1, T2, T;
    T1 = System.currentTimeMillis();

    calculate();

    T2 = System.currentTimeMillis();
    T = T2-T1;
    System.out.println("\n\t*** Execution time = " + T + " ms");
  }

  static void calculate() {

    Store store = new Store();

    int maxSodium = 3000;
    int maxFat = 150;
    int maxCal = 3000;

    int sodium[] = { 50, 330, 310, 1, 260, 3, 160, 3 };
    int fat[] = { 17, 9, 6, 2, 0, 0, 0, 0 };
    int cal[] = { 220, 260, 70, 10, 5, 3, 20, 9 };
    int cost[] = { 25, 15, 10, 9, 3, 4, 2, 4 };

    IntVar beef = new IntVar(store, "beef", 1, 5);
    IntVar bun = new IntVar(store, "bun", 1, 5);
    IntVar cheese = new IntVar(store, "cheese", 1, 5);
    IntVar onion = new IntVar(store, "onion", 1, 5);
    IntVar pickle = new IntVar(store, "pickle", 1, 5);
    IntVar lettuce = new IntVar(store, "lettuce", 1, 5);
    IntVar ketchup = new IntVar(store, "ketchup", 1, 5);
    IntVar tomato = new IntVar(store, "tomato", 1, 5);

    IntVar ingredients[] = {beef, bun, cheese, onion, pickle, lettuce, ketchup, tomato};

    IntVar zero = new IntVar(store, "zero", 0, 0);

    IntVar burgerCost = new IntVar(store, "Max Cost", 72, 360);
    IntVar negBurgerCost = new IntVar(store, "cost", -360, -72);
    store.impose(new XplusYeqZ(burgerCost, negBurgerCost, zero));



    store.impose(new XeqY(ketchup, lettuce));
    store.impose(new XeqY(pickle, tomato));

    store.impose(new Linear(store, ingredients, sodium, "<=", maxSodium));
    store.impose(new Linear(store, ingredients, fat, "<=", maxFat));
    store.impose(new Linear(store, ingredients, cal, "<=", maxCal));
    store.impose(new SumWeight(ingredients, cost, burgerCost));

    System.out.println("Number of variables: "+ store.size() +
    "\nNumber of constraints: " + store.numberConstraints());

    Search<IntVar> label = new DepthFirstSearch<IntVar>();
    SelectChoicePoint<IntVar> select = new SimpleSelect<IntVar>(ingredients,
    new SmallestDomain<IntVar>(),
    new IndomainMin<IntVar>());

    label.setSolutionListener(new PrintOutListener<IntVar>());
    label.getSolutionListener().searchAll(true);

    boolean Result = label.labeling(store, select, negBurgerCost);

    if (Result) {
      System.out.println("\n*** Yes");
      System.out.println("Solution : "+ java.util.Arrays.asList(ingredients));
    }
    else System.out.println("\n*** No");
  }


}
