package tup.main;

import java.io.File;

import com.sun.xml.internal.ws.runtime.config.TubeFactoryConfig;

import java.io.File;

public class Main {

	public static void main(String[] args) {
		
		File dir = new File("/Users/Lennart/git/BP-TUP-Thesis/instances/");
		File [] instanceFiles = dir.listFiles();
		
		/*for(File instanceFile : instanceFiles){
			TUPSolver solver = new TUPSolver(4);
	    	solver.readInstance(instanceFile.getAbsolutePath());
	    	solver.gamesPerRound = solver.problem.nTeams / 2;
	    	solver.problem.q1 = solver.problem.nUmpires;
            solver.problem.q2 = solver.problem.nUmpires / 2;
	    	solver.solve(900);
	    	solver.solution();
	    	solver.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/annealing4/15min/"+extracFile(instanceFile.getName())+"q1="+
	    			solver.problem.q1+"&q2="+solver.problem.q2+"(15min)");
		}*/
		
		/*for(File instanceFile : instanceFiles){
			TUPSolver solver = new TUPSolver(7);
	    	solver.readInstance(instanceFile.getAbsolutePath());
	    	solver.gamesPerRound = solver.problem.nTeams / 2;
	    	solver.problem.q1 = solver.problem.nUmpires;
            solver.problem.q2 = solver.problem.nUmpires / 2;
	    	solver.solve(900);
	    	solver.solution();
	    	solver.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/annealing7/15min/"+extracFile(instanceFile.getName())+"q1="+
	    			solver.problem.q1+"&q2="+solver.problem.q2+"(15min)");
		}*/
	
		for(File instanceFile : instanceFiles){
			TUPSolver solver = new TUPSolver(7);
	    	solver.readInstance(instanceFile.getAbsolutePath());
	    	solver.gamesPerRound = solver.problem.nTeams / 2;
	    	solver.problem.q1 = solver.problem.nUmpires;
            solver.problem.q2 = solver.problem.nUmpires / 2;
	    	solver.solve(300);
	    	solver.solution();
	    	solver.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/annealing7/5min/"+extracFile(instanceFile.getName())+"q1="+
	    			solver.problem.q1+"&q2="+solver.problem.q2+"(5min)");
		}
		
		/*for(File instanceFile : instanceFiles){
			TUPSolver solver = new TUPSolver(9);
	    	solver.readInstance(instanceFile.getAbsolutePath());
	    	solver.gamesPerRound = solver.problem.nTeams / 2;
	    	solver.problem.q1 = solver.problem.nUmpires;
            solver.problem.q2 = solver.problem.nUmpires / 2;
	    	solver.solve(900);
	    	solver.solution();
	    	solver.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/annealing9/15min/"+extracFile(instanceFile.getName())+"q1="+
	    			solver.problem.q1+"&q2="+solver.problem.q2+"(15min)");
		}*/
		
		for(File instanceFile : instanceFiles){
			TUPSolver solver = new TUPSolver(9);
	    	solver.readInstance(instanceFile.getAbsolutePath());
	    	solver.gamesPerRound = solver.problem.nTeams / 2;
	    	solver.problem.q1 = solver.problem.nUmpires;
            solver.problem.q2 = solver.problem.nUmpires / 2;
	    	solver.solve(300);
	    	solver.solution();
	    	solver.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/annealing9/5min/"+extracFile(instanceFile.getName())+"q1="+
	    			solver.problem.q1+"&q2="+solver.problem.q2+"(5min)");
		}
		
	}
	
	public static String extracFile(String filename){
		String exitString = filename.substring(0, filename.length()-4);
		return exitString;
	}

}
