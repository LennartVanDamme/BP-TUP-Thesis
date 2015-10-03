package tup.main;

import java.io.File;

//import java.io.File;

public class Main {

	public static void main(String[] args) {
		
		File dir = new File("/Users/Lennart/git/BP-TUP-Thesis/instances/");
		File [] instanceFiles = dir.listFiles();
		
		/*// Instances ran 1 min
		for(File instanceFile : instanceFiles){
			TUPSolver solver = new TUPSolver();
			solver.readInstance(instanceFile.getAbsolutePath());
	    	solver.gamesPerRound = solver.problem.nTeams / 2;
	    	solver.solve(60);
	    	solver.solution();
	    	solver.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/1min(off)/"+instanceFile.getName());
		}
		
		for(File instanceFile : instanceFiles){
			TUPSolver solver = new TUPSolver();
	    	solver.readInstance(instanceFile.getAbsolutePath());
	    	solver.gamesPerRound = solver.problem.nTeams / 2;
	    	solver.problem.q1 = solver.problem.nUmpires / 2;
            solver.problem.q2 = solver.problem.nUmpires / 4;
            solver.solve(60);
	    	solver.solution();
	    	solver.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/1min(half-half)/"+instanceFile.getName());
		}
		
		for(File instanceFile : instanceFiles){
			TUPSolver solver = new TUPSolver();
	    	solver.readInstance(instanceFile.getAbsolutePath());
	    	solver.gamesPerRound = solver.problem.nTeams / 2;
	    	solver.problem.q1 = solver.problem.nUmpires;
	    	solver.problem.q2 = solver.problem.nUmpires / 4;
	    	solver.solve(60);
	    	solver.solution();
	    	solver.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/1min(max-half)/"+instanceFile.getName());
		}
		
		for(File instanceFile : instanceFiles){
			TUPSolver solver = new TUPSolver();
	    	solver.readInstance(instanceFile.getAbsolutePath());
	    	solver.gamesPerRound = solver.problem.nTeams / 2;
	    	solver.problem.q1 = solver.problem.nUmpires;
            solver.problem.q2 = solver.problem.nUmpires / 2;
	    	solver.solve(60);
	    	solver.solution();
	    	solver.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/1min(max)/"+instanceFile.getName());
		}*/
		
		// Instances ran 5 min
		for(File instanceFile : instanceFiles){
			TUPSolver solver = new TUPSolver();
	    	solver.readInstance(instanceFile.getAbsolutePath());
	    	solver.gamesPerRound = solver.problem.nTeams / 2;
	    	solver.solve(600);
	    	solver.solution();
	    	solver.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/10min(off)/"+instanceFile.getName());
		}
		
		for(File instanceFile : instanceFiles){
			TUPSolver solver = new TUPSolver();
	    	solver.readInstance(instanceFile.getAbsolutePath());
	    	solver.gamesPerRound = solver.problem.nTeams / 2;
	    	solver.problem.q1 = solver.problem.nUmpires / 2;
            solver.problem.q2 = solver.problem.nUmpires / 4;
	    	solver.solve(600);
	    	solver.solution();
	    	solver.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/10min(half-half)/"+instanceFile.getName());
		}
		
		for(File instanceFile : instanceFiles){
			TUPSolver solver = new TUPSolver();
	    	solver.readInstance(instanceFile.getAbsolutePath());
	    	solver.gamesPerRound = solver.problem.nTeams / 2;
	    	solver.problem.q1 = solver.problem.nUmpires;
            solver.problem.q2 = solver.problem.nUmpires / 4;
	    	solver.solve(600);
	    	solver.solution();
	    	solver.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/10min(max-half)/"+instanceFile.getName());
		}
		
		for(File instanceFile : instanceFiles){
			TUPSolver solver = new TUPSolver();
	    	solver.readInstance(instanceFile.getAbsolutePath());
	    	solver.gamesPerRound = solver.problem.nTeams / 2;
	    	solver.problem.q1 = solver.problem.nUmpires;
            solver.problem.q2 = solver.problem.nUmpires / 2;
	    	solver.solve(600);
	    	solver.solution();
	    	solver.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/10min(max)/"+instanceFile.getName());
		}
		
		/*// Instances ran 30 min
		for(File instanceFile : instanceFiles){
			TUPSolver solver = new TUPSolver();
	    	solver.readInstance(instanceFile.getAbsolutePath());
	    	solver.gamesPerRound = solver.problem.nTeams / 2;
	    	solver.problem.q1 = solver.problem.nUmpires;
            solver.problem.q2 = solver.problem.nUmpires / 2;
	    	solver.solve(1800);
	    	solver.solution();
	    	solver.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/30min(max)/"+instanceFile.getName());
		}*/
		
		/*for(File instanceFile : instanceFiles){
			TUPSolver solver = new TUPSolver();
	    	solver.readInstance(instanceFile.getAbsolutePath());
	    	solver.gamesPerRound = solver.problem.nTeams / 2;
	    	solver.problem.q1 = solver.problem.nUmpires;
            solver.problem.q2 = solver.problem.nUmpires / 2;
	    	solver.solve(60);
	    	solver.solution();
	    	solver.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/1min(max)/"+instanceFile.getName());
		}
		
		for(File instanceFile : instanceFiles){
			TUPSolver solver = new TUPSolver();
	    	solver.readInstance(instanceFile.getAbsolutePath());
	    	solver.gamesPerRound = solver.problem.nTeams / 2;
	    	solver.problem.q1 = solver.problem.nUmpires;
            solver.problem.q2 = solver.problem.nUmpires / 2;
	    	solver.solve(60);
	    	solver.solution();
	    	solver.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/1min(max)/"+instanceFile.getName());
		}
		
		for(File instanceFile : instanceFiles){
			TUPSolver solver = new TUPSolver();
	    	solver.readInstance(instanceFile.getAbsolutePath());
	    	solver.gamesPerRound = solver.problem.nTeams / 2;
	    	solver.problem.q1 = solver.problem.nUmpires;
            solver.problem.q2 = solver.problem.nUmpires / 2;
	    	solver.solve(60);
	    	solver.solution();
	    	solver.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/1min(max)/"+instanceFile.getName());
		}*/
		
	}

}
