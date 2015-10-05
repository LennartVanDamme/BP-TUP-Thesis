package tup.main;

import java.io.File;

import com.sun.xml.internal.ws.runtime.config.TubeFactoryConfig;

//import java.io.File;

public class Main {

	public static void main(String[] args) {
		
		TUPSolver solver = new TUPSolver();
		solver.readInstance("/Users/Lennart/git/BP-TUP-Thesis/instances/umps8A.txt");
    	solver.gamesPerRound = solver.problem.nTeams / 2;
    	solver.problem.q1 = solver.problem.nUmpires;
        solver.problem.q2 = solver.problem.nUmpires / 4;
    	solver.solve(600);
    	solver.solution();
    	solver.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/10min(max-half)/umps8A.txt");
    	
    	TUPSolver solver1 = new TUPSolver();
    	solver1.readInstance("/Users/Lennart/git/BP-TUP-Thesis/instances/umps8B.txt");
    	solver1.gamesPerRound = solver.problem.nTeams / 2;
    	solver1.problem.q1 = solver.problem.nUmpires;
    	solver1.problem.q2 = solver.problem.nUmpires / 4;
    	solver1.solve(600);
    	solver1.solution();
    	solver1.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/10min(max-half)/umps8B.txt");
    	
    	TUPSolver solver2 = new TUPSolver();
    	solver2.readInstance("/Users/Lennart/git/BP-TUP-Thesis/instances/umps8C.txt");
    	solver2.gamesPerRound = solver.problem.nTeams / 2;
    	solver2.problem.q1 = solver.problem.nUmpires;
    	solver2.problem.q2 = solver.problem.nUmpires / 4;
    	solver2.solve(600);
    	solver2.solution();
    	solver2.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/10min(max-half)/umps8C.txt");
		
		/*File dir = new File("/Users/Lennart/git/BP-TUP-Thesis/instances/");
		File [] instanceFiles = dir.listFiles();
		
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
		}*/
		
	}

}
