package tup.main;

public class Main {

	public static void main(String[] args) {

		TUPSolver solver6 = new TUPSolver(7);
		solver6.readInstance("/Users/Lennart/git/BP-TUP-Thesis/instances/umps30.txt");
		solver6.gamesPerRound = solver6.problem.nTeams / 2;
		solver6.problem.q1 = 5;
		solver6.problem.q2 = 5;
		solver6.solve(18000);
		solver6.solution();
		solver6.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/extra/umps30Annealing="
				+ solver6.annealingLvl + "&q1=" + solver6.problem.q1 + "&q2=" + solver6.problem.q2);

		TUPSolver solver7 = new TUPSolver(9);
		solver7.readInstance("/Users/Lennart/git/BP-TUP-Thesis/instances/umps30.txt");
		solver7.gamesPerRound = solver7.problem.nTeams / 2;
		solver7.problem.q1 = 5;
		solver7.problem.q2 = 5;
		solver7.solve(18000);
		solver7.solution();
		solver7.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/extra/umps30Annealing="
				+ solver7.annealingLvl + "&q1=" + solver7.problem.q1 + "&q2=" + solver7.problem.q2);

	}

	public static String extracFile(String filename) {
		String exitString = filename.substring(0, filename.length() - 4);
		return exitString;
	}

}
