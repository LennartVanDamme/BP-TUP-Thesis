package tup.main;

public class MainFirstModel {

	public static void main(String[] args) {

		TUPSolver solver = new TUPSolver(1);
		solver.readInstance("/Users/Lennart/git/BP-TUP-Thesis/instances/umps10.txt");
		solver.solve(60 * 60 * 3);
		solver.solution();
		solver.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/secondConstraintImpl/umps10Annealing="
				+ solver.annealinglvl + "&q1=" + solver.problem.q1 + "&q2=" + solver.problem.q2);
		solver.writeViolatedConstraints("/Users/Lennart/git/BP-TUP-Thesis/violatedConstraintsFiles/umps10");
		TUPSolver solver1 = new TUPSolver(4);
		solver1.readInstance("/Users/Lennart/git/BP-TUP-Thesis/instances/umps10.txt");
		solver1.solve(60 * 60 * 3);
		solver1.solution();
		solver1.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/secondConstraintImpl/umps10Annealing="
				+ solver1.annealinglvl + "&q1=" + solver1.problem.q1 + "&q2=" + solver1.problem.q2);
		solver1.writeViolatedConstraints("/Users/Lennart/git/BP-TUP-Thesis/violatedConstraintsFiles/umps10"
				+ solver1.annealinglvl + "&q1=" + solver1.problem.q1 + "&q2=" + solver1.problem.q2);
		TUPSolver solver2 = new TUPSolver(7);
		solver2.readInstance("/Users/Lennart/git/BP-TUP-Thesis/instances/umps10.txt");
		solver2.solve(60 * 60 * 3);
		solver2.solution();
		solver2.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/secondConstraintImpl/umps10Annealing="
				+ solver2.annealinglvl + "&q1=" + solver2.problem.q1 + "&q2=" + solver2.problem.q2);
		solver2.writeViolatedConstraints("/Users/Lennart/git/BP-TUP-Thesis/violatedConstraintsFiles/umps10"
				+ solver2.annealinglvl + "&q1=" + solver2.problem.q1 + "&q2=" + solver2.problem.q2);
		TUPSolver solver3 = new TUPSolver(9);
		solver3.readInstance("/Users/Lennart/git/BP-TUP-Thesis/instances/umps10.txt");
		solver3.solve(60 * 60 * 3);
		solver3.solution();
		solver3.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/secondConstraintImpl/umps10Annealing="
				+ solver3.annealinglvl + "&q1=" + solver3.problem.q1 + "&q2=" + solver3.problem.q2);
		solver3.writeViolatedConstraints("/Users/Lennart/git/BP-TUP-Thesis/violatedConstraintsFiles/umps10"
				+ solver3.annealinglvl + "&q1=" + solver3.problem.q1 + "&q2=" + solver3.problem.q2);
		// solver6.readInstance("/Users/Lennart/git/BP-TUP-Thesis/instances/umps8.txt");
		// solver6.problem.q1 = 4;
		// solver6.problem.q2 = 2;
		// solver6.solve(60);
		// solver6.solution();
		/*
		 * TUPSolver solver7 = new TUPSolver(9); solver7.readInstance(
		 * "/Users/Lennart/git/BP-TUP-Thesis/instances/umps30.txt");
		 * solver7.gamesPerRound = solver7.problem.nTeams / 2;
		 * solver7.problem.q1 = 5; solver7.problem.q2 = 5; solver7.solve(18000);
		 * solver7.solution(); solver7.writeSolution(
		 * "/Users/Lennart/git/BP-TUP-Thesis/outputFiles/extra/umps30Annealing="
		 * + solver7.annealingLvl + "&q1=" + solver7.problem.q1 + "&q2=" +
		 * solver7.problem.q2);
		 */

	}

	public static String extracFile(String filename) {
		String exitString = filename.substring(0, filename.length() - 4);
		return exitString;
	}

}
