package tup.main;

import java.io.File;

public class MainFirstModel {

	public static void main(String[] args) {

		File directory = new File("/Users/Lennart/git/BP-TUP-Thesis/instances/");
		File[] files = directory.listFiles();
		for (File file : files) {
			TUPSolver3 tupSolver3 = new TUPSolver3(1);
			tupSolver3.readInstance(file.getAbsolutePath());
			tupSolver3.solve(60*60*3);
			tupSolver3.solution();
			tupSolver3.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/annealing1/secImpl/"
					+ extracFile(file.getName())+"Output.txt");
			TUPSolver3 tupSolver32 = new TUPSolver3(4);
			tupSolver32.readInstance(file.getAbsolutePath());
			tupSolver32.solve(60*60*3);
			tupSolver32.solution();
			tupSolver32.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/annealing4/secImpl/"
					+ extracFile(file.getName())+"Output.txt");
			TUPSolver3 tupSolver33 = new TUPSolver3(9);
			tupSolver33.readInstance(file.getAbsolutePath());
			tupSolver33.solve(60*60*3);
			tupSolver33.solution();
			tupSolver33.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/annealing9/secImpl/"
					+ extracFile(file.getName())+"Output.txt");
		}

	}

	public static String extracFile(String filename) {
		String exitString = filename.substring(0, filename.length() - 4);
		return exitString;
	}

}
