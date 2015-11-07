package tup.main;

import java.io.File;
import java.io.IOException;

public class MainSecondModel {

	public static void main(String[] args) throws IOException {
		
//		File directory = new File("/Users/Lennart/git/BP-TUP-Thesis/instances/");
//		File[] files = directory.listFiles();
//		for (File file : files) {
//			TUPSolver3 tupSolver3 = new TUPSolver3(4);
//			tupSolver3.readInstance(file.getAbsolutePath());
//			tupSolver3.solve(60*60*3);
//			tupSolver3.writeSolution("Users/Lennart/git/BP-TUP-Thesis/outputFiles/annealing1/secImpl/"
//					+ extracFile(file.getName())+"Output.txt");
//		}
		
		TUPSolver2 solver = new TUPSolver2(1);
		solver.readInstance("/Users/Lennart/git/BP-TUP-Thesis/instances/umps10.txt");
		solver.instantiateArrays();
		solver.solve(60);
		solver.printViolatedConstraints();
		
		
	}
	
	public static String extracFile(String filename) {
		String exitString = filename.substring(0, filename.length() - 4);
		return exitString;
	}

}
