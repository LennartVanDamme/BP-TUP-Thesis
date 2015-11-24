package tup.main;

import java.io.File;
import java.io.IOException;

public class MainSecondModel {

	public static void main(String[] args) throws IOException {
		
		File directory = new File("/Users/Lennart/git/BP-TUP-Thesis/instances/");
		File[] files = directory.listFiles();
		for (File file : files) {
			if ((file.getName().contains("s8") || file.getName().contains("10") || file.getName().contains("14")
					|| file.getName().contains("16"))){
				TUPSolver2 tupSolver2 = new TUPSolver2(7);
				tupSolver2.readInstance(file.getAbsolutePath());
				tupSolver2.instantiateArrays();
				tupSolver2.solve(60*60*3);
				tupSolver2.createSolution();
				tupSolver2.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/edgeBasedModel/"
						+ extracFile(file.getName())+"Output_q1:"+tupSolver2.problem.q1+"_q2:"+tupSolver2.problem.q2+".txt");
			}
			
		}
		
		
	}
	
	public static String extracFile(String filename) {
		String exitString = filename.substring(0, filename.length() - 4);
		return exitString;
	}

}
