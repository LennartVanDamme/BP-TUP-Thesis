package tup.main;

public class MainSecondModel {

	public static void main(String[] args) {
		
		TUPSolver2 solver = new TUPSolver2(1);
		solver.readInstance("/Users/Lennart/git/BP-TUP-Thesis/instances/umps10.txt");
		solver.instantiateGamesToEdge();
		solver.printoutGamesToEdge();
		
	}

}
