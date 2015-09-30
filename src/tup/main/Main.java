package tup.main;

//import java.io.File;

public class Main {

	public static void main(String[] args) {
		
		/*File dir = new File("/Users/Lennart/git/BP-TUP-Thesis/instances/");
		File [] instanceFiles = dir.listFiles();
		for(File instanceFile : instanceFiles){
			TUPSolver solver = new TUPSolver();
	    	solver.readInstance(instanceFile.getAbsolutePath());
	    	solver.gamesPerRound = solver.problem.nTeams / 2;
	    	solver.solve(60);
	    	solver.solution();
	    	solver.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/"+instanceFile.getName());
		}*/
		
		TUPSolver solver = new TUPSolver();
		solver.readInstance("/Users/Lennart/git/BP-TUP-Thesis/instances/umps8.txt");
		solver.gamesPerRound = solver.problem.nTeams / 2;
		solver.solve(20);
		solver.solution();
		for(int u = 0; u <= solver.problem.nUmpires-1; u++){
			System.out.println("timesTeamVisistedHome umpire"+u);
			for(int t = 0; t <= solver.problem.nTeams-1; t++){
				System.out.print("\t"+solver.timesTeamVisitedHome[u][t].getValue()+"\n");
			}
		}
		for(int u = 0; u <= solver.problem.nUmpires-1; u++){
			System.out.println("teamsSeenPerRoundByUmpire umpire:"+u);
			for(int r = 0; r <= solver.problem.nRounds-1; r++){
				System.out.println("Round: "+r+"\tHometeam: "+solver.teamsSeenPerRoundByUmpire[u][r][0].getValue()+"\tAwayteam: "
			+solver.teamsSeenPerRoundByUmpire[u][r][1].getValue());
			}
			System.out.println();
		}
		
	}

}
