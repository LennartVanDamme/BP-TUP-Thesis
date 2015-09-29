package tup.main;

public class Main {

	public static void main(String[] args) {
		
		TUPSolver solver = new TUPSolver();
    	solver.readInstance("/Users/Lennart/git/BP-TUP-Thesis/instances/umps8.txt");
    	solver.gamesPerRound = solver.problem.nTeams / 2;
    	solver.solve(10);

	}

}
