package tup.main;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class FinalMain {
	
	

	public static void main(String[] args) throws IOException {
		
		TUPSolver2 solver = new TUPSolver2(1);
		
		String path = null;
		String outputPath = null;
		int limit = 0;
		for(String argument : args){
			if(argument.startsWith("input=")){
				path = argument.substring(6);
			} else if(argument.startsWith("output=")){
				outputPath = argument.substring(7);
			} else if(argument.startsWith("limit=")){
				limit = Integer.parseInt(argument.substring(6));
			}
		}
		File directoryMaxConstraint = new File(path+"max/");
		for(File file : directoryMaxConstraint.listFiles()){
			solver = new TUPSolver2(1);
			solver.readInstance(file.getAbsolutePath());
			solver.instantiateArrays();
			solver.solve(limit);
			solver.createSolution();
			solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
					+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
			solver = new TUPSolver2(4);
			solver.readInstance(file.getAbsolutePath());
			solver.instantiateArrays();
			solver.solve(limit);
			solver.createSolution();
			solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
					+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
			solver = new TUPSolver2(9);
			solver.readInstance(file.getAbsolutePath());
			solver.instantiateArrays();
			solver.solve(limit);
			solver.createSolution();
			solver.writeSolution(outputPath + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
					+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
		
			
		}
		File specialCases = new File(path+"special/");
		for(File file : specialCases.listFiles()){
			if(file.getName().contains("12")){
				solver = new TUPSolver2(1);
				solver.readInstance(file.getAbsolutePath());
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(1);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 += 1;
				solver.problem.q2 -= 1;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(1);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 -= 1;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(1);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 -= 2;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(4);
				solver.readInstance(file.getAbsolutePath());
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(4);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 += 1;
				solver.problem.q2 -= 1;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(4);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 -= 1;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(4);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 -= 2;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(9);
				solver.readInstance(file.getAbsolutePath());
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(9);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 += 1;
				solver.problem.q2 -= 1;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(9);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 -= 1;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(9);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 -= 2;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
			}else{
				solver = new TUPSolver2(1);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 += 1;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(1);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 += 1;
				solver.problem.q2 -= 1;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(1);
				solver.readInstance(file.getAbsolutePath());
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(1);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q2 -= 1;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath+ "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(1);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 -= 1;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath+ "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(1);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 -= 1;
				solver.problem.q2 -= 1;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(1);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 -= 2;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath+ "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(1);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 -= 2;
				solver.problem.q2 -= 1;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(4);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 += 1;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(4);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 += 1;
				solver.problem.q2 -= 1;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath+ "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(4);
				solver.readInstance(file.getAbsolutePath());
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(4);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q2 -= 1;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(4);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 -= 1;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(4);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 -= 1;
				solver.problem.q2 -= 1;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(4);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 -= 2;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(4);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 -= 2;
				solver.problem.q2 -= 1;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(9);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 += 1;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(9);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 += 1;
				solver.problem.q2 -= 1;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(9);
				solver.readInstance(file.getAbsolutePath());
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(9);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q2 -= 1;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(9);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 -= 1;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(9);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 -= 1;
				solver.problem.q2 -= 1;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(9);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 -= 2;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				solver = new TUPSolver2(9);
				solver.readInstance(file.getAbsolutePath());
				solver.problem.q1 -= 2;
				solver.problem.q2 -= 1;
				solver.instantiateArrays();
				solver.solve(limit);
				solver.createSolution();
				solver.writeSolution(outputPath + "/" + extracFile(file.getName()) + "_q1=" + solver.problem.q1 + "&q2="
						+ solver.problem.q2 + "&Annealing=" + solver.annealingLvl + "Output.txt");
				
				
				
			}
			
		}
	}

	public static String extracFile(String filename) {
		String exitString = filename.substring(0, filename.length() - 4);
		return exitString;
	}

}
