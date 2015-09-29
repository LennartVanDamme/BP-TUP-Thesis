package tup.main;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		String inputFileName = null;
		String outputFileName = null;
		int limit = -1;
		while(inputFileName == null){
			System.out.print("Geef input filenaam: ");
			inputFileName = sc.nextLine();
		}
		while(outputFileName == null){
			System.out.print("Geef output filenaam: ");
			outputFileName = sc.nextLine();
		}
		while(limit < 0){
			System.out.print("Tijdslimiet: ");
			limit = sc.nextInt();
		}
		
		TUPSolver solver = new TUPSolver();
    	solver.readInstance("/Users/Lennart/git/BP-TUP-Thesis/instances/"+inputFileName+".txt");
    	solver.gamesPerRound = solver.problem.nTeams / 2;
    	solver.solve(limit);
    	solver.writeSolution("/Users/Lennart/git/BP-TUP-Thesis/outputFiles/"+outputFileName+".txt");

	}

}
