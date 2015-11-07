/*
Model is nog inconsistent
Constraints 4 en 5 moeten nog toegevoegd worden
*/
package tup.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import localsolver.LSException;
import localsolver.LSExpression;
import localsolver.LSModel;
import localsolver.LSPhase;
import localsolver.LSSolution;
import localsolver.LocalSolver;

public class TUPSolver2 {

	LocalSolver solver;
	LSModel model;
	LSExpression totalDistanceTraveled;
	LSExpression[][] timesteamVisited;
	LSExpression[] umpireDistanceTraveled;
	LSExpression[][] umpireAssignment;
	LSExpression constantTrue;
	LSSolution lsSolution;

	Problem problem;
	Solution solution;

	int gamesPerRound, annealingLvl, roundEdges, edgesPerRound, totalAmountEdges;

	int numberConstraint1 = 0;
	int numberConstraint2 = 0;
	int numberConstraint3 = 0;
	int numberConstraint4 = 0;
	int numberConstraint5 = 0;
	// gamesToEdge[e][0] geeft het eerste game en
	// gamesToEdgde[e][1] geeft het game in de volgende
	// ronde verbonden met het eerste gameß
	int[][] edgeToGame;
	int[] gameToEdgeNr;

	public TUPSolver2(int annealingLvl) {
		solver = new LocalSolver();
		model = solver.getModel();
		this.annealingLvl = annealingLvl;
		constantTrue = model.createConstant(1);

	}

	public void readInstance(String path) {
		try {
			ProblemReader problemReader = new ProblemReader();
			File instanceFile = new File(path);
			problem = problemReader.readProblemFromFile(instanceFile, 1, 1, "test");
			problem.setTournament(problem.opponents);
			gamesPerRound = problem.nTeams / 2;
			edgesPerRound = gamesPerRound * gamesPerRound;
			roundEdges = edgesPerRound * (problem.nRounds - 1);
			totalAmountEdges = roundEdges + gamesPerRound;

		} catch (IOException ioe) {
			System.out.println("IOException: " + ioe.getMessage());
			ioe.printStackTrace();
		}
	}

	public void printoutGamesToEdge() {
		for (int e = 0; e < roundEdges; e++) {
			System.out.println("edgenr: " + e + "\t[" + edgeToGame[e][0] + ',' + edgeToGame[e][1] + ']');
		}
	}

	public void instantiateArrays() {
		edgeToGame = new int[totalAmountEdges][2];
		gameToEdgeNr = new int[problem.nGames];
		int ronde = 1;
		int hulp = edgesPerRound;
		for (int e = 0; e < roundEdges; e++) {
			if (hulp == 0) {
				hulp = edgesPerRound;
				ronde++;
			}
			int firstGame = e / gamesPerRound;
			edgeToGame[e][0] = firstGame;
			edgeToGame[e][1] = ronde * gamesPerRound + e % gamesPerRound;
			gameToEdgeNr[firstGame] = e;
			hulp--;
		}
		for (int e = 0; e < gamesPerRound; e++) {
			edgeToGame[roundEdges + e][0] = ronde * gamesPerRound + e % gamesPerRound;
			edgeToGame[roundEdges + e][1] = -1;
			gameToEdgeNr[ronde * gamesPerRound + e % gamesPerRound] = roundEdges + e;
		}
	}

	public void solve(int limit) {
		try {
			umpireAssignment = new LSExpression[problem.nUmpires][totalAmountEdges];
			umpireDistanceTraveled = new LSExpression[problem.nUmpires];
			timesteamVisited = new LSExpression[problem.nUmpires][problem.nTeams];

			for (int u = 0; u < problem.nUmpires; u++) {
				umpireDistanceTraveled[u] = model.sum();

			}

			for (int u = 0; u < problem.nUmpires; u++) {
				for (int e = 0; e < totalAmountEdges; e++) {
					umpireAssignment[u][e] = model.boolVar();
				}
			}

			for (int u = 0; u < problem.nUmpires; u++) {
				for (int t = 0; t < problem.nTeams; t++) {
					timesteamVisited[u][t] = model.sum();
				}
			}

			for (int u = 0; u < problem.nUmpires; u++) {
				for (int e = 0; e < totalAmountEdges; e++) {

					// umpireDistanceTraveled[u].addOperand(model.prod(umpireAssignment[u][e],
					// problem.distGames[gamesToEdge[e][0]][gamesToEdge[e][1]]));
					// timesteamVisited[u][problem.games[gamesToEdge[e][0]][0]]
					// .addOperand(model.iif(umpireAssignment[u][e], 1, 0));
					int gameOfEdge = edgeToGame[e][0];
					int teamOfGame = problem.games[gameOfEdge][0] - 1;
					timesteamVisited[u][teamOfGame].addOperand(umpireAssignment[u][e]);
				}
			}

			/*
			 * Constraint 1 : Every game is umpired by exactly one umpire
			 */

			// Voor elke game
			for (int game = 0; game < problem.nGames - gamesPerRound; game++) {
				int edgeMax = gameToEdgeNr[game];
				// Voor elke umpire
				for (int u = 0; u < problem.nUmpires; u++) {
					// De som van de assignemtns aan edges die het game verlaten
					// moet gelijk zijn aan 1
					LSExpression constraint1 = model.sum();
					String constraintName = new String(
							"Constraint 1 voor umpire " + u + " bij game " + game + " (Edgenummers:");
					for (int e = edgeMax; e > edgeMax - gamesPerRound; e--) {
						constraintName += " " + e;
						constraint1.addOperand(umpireAssignment[u][e]);
					}
					constraint1.setName(constraintName);
					model.constraint(model.eq(constraint1, 1));
					numberConstraint1++;
				}
			}
			// Voor de laatste games
			for (int game = problem.nGames; game > problem.nGames - gamesPerRound; game--) {
				int edgeOfGame = gameToEdgeNr[game - 1];
				// Voor iedere umpire
				for (int u = 0; u < problem.nUmpires; u++) {
					LSExpression constraint1 = model.eq(umpireAssignment[u][edgeOfGame], 1);
					constraint1.setName("Constraint 1 voor umpire " + u + " voor game " + game);
					model.constraint(constraint1);
					numberConstraint1++;
				}
			}

			/*
			 * Constraint 2 : Every umpire works exacly once in each round
			 */

			for (int u = 0; u < problem.nUmpires; u++) {
				for (int r = 0; r < problem.nRounds - 1; r++) {
					LSExpression workedInRound = model.sum();
					for (int edge = 0; edge < edgesPerRound; edge++) {
						workedInRound.addOperand(umpireAssignment[u][(r * gamesPerRound) + edge]);
					}
					workedInRound.setName("Constraint 2 voor umpire "+u+ " in ronde "+r);
					model.addConstraint(model.eq(workedInRound, 1));
					numberConstraint2++;
				}
			}

			/*
			 * Constraint 3 : Every team had to be seen at least once
			 */

			for (int u = 0; u < problem.nUmpires; u++) {
				for (int t = 0; t < problem.nTeams; t++) {
					LSExpression constraint3 = model.geq(timesteamVisited[u][t], 1);
					constraint3.setName("Constraint 3 voor umpire "+u+ " en team "+(t+1));
					model.constraint(constraint3);
					numberConstraint3++;
				}
			}

			// Constraint 4 : A home team can not revisit the same home team in
			// q1 rounds

			// voor elke umpire
			// for(int u = 0; u < problem.nUmpires; u++){
			//
			// // maken van de windows
			// for(int startRondeWindow = 0; startRondeWindow
			// <problem.nRounds-problem.q1+1; startRondeWindow++){
			// int eindRondeWindow = startRondeWindow+problem.q1;
			//
			// for(int t = 0; t < problem.nTeams; t++){
			// List<Integer> edgesWithHomeTeamInWindow = new LinkedList<>();
			//
			// for(int rondeInWindow = startRondeWindow; rondeInWindow <
			// eindRondeWindow; rondeInWindow++){
			// // De games in die ronde controlleren
			// // Als het homeTeam van die game gelijk is aan het team t
			// // Dan slaan we de edge nummer op van de edge die vertrekt uit
			// die game
			// }
			//
			// LSExpression constraint4 = model.sum();
			// for(int edge : edgesWithHomeTeamInWindow){
			// constraint4.addOperand(umpireAssignment[u][edge]);
			// }
			// model.constraint(model.leq(constraint4, 1));
			// }
			//
			//
			// }
			// }

			totalDistanceTraveled = model.sum(umpireDistanceTraveled);
			model.minimize(totalDistanceTraveled);

			model.close();

			LSPhase phase = solver.createPhase();
			phase.setTimeLimit(limit);

			solver.getParam().setAnnealingLevel(annealingLvl);

			solver.solve();
			lsSolution = solver.getSolution();

		} catch (LSException e) {
			System.out.println("LSException: " + e.getMessage());
			System.exit(-1);
		}
	}

	public void printOutArrays() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("/Users/Lennart/Desktop/edgeToGame.txt")));
		for (int i = 0; i < edgeToGame.length; i++) {
			writer.write(
					"Edgenr: " + i + " FirstGame: " + edgeToGame[i][0] + " SecondGame: " + edgeToGame[i][1] + "\n");
		}
		writer.close();
		writer = new BufferedWriter(new FileWriter(new File("/Users/Lennart/Desktop/gameToEdgeNr.txt")));
		for (int i = 0; i < gameToEdgeNr.length; i++) {
			writer.write("Game nr: " + i + " edge: " + gameToEdgeNr[i] + "\n");
		}
		writer.close();

	}

	public void printViolatedConstraints() {
		int NBConstraint1 = 0;
		int NBConstraint2 = 0;
		int NBConstraint3 = 0;
		int NBConstraint4 = 0;
		int NBConstraint5 = 0;
		try {
			BufferedWriter writer = new BufferedWriter(
					new FileWriter("/Users/Lennart/Desktop/violatedConstraints.txt"));
			writer.write("Total number of constraints: "+model.getNbConstraints()+"\n");
			for (int i = 0; i < model.getNbConstraints(); i++) {
				if (model.getConstraint(i).isViolated()) {
					String violatedName = model.getConstraint(i).getName();
					if (violatedName.contains("Constraint 1")) {
						NBConstraint1++;
					} else if (violatedName.contains("Constraint 2")) {
						NBConstraint2++;
					} else if (violatedName.contains("Constraint 3")) {
						NBConstraint3++;
					} else if (violatedName.contains("Constraint 4")) {
						NBConstraint4++;
					} else if (violatedName.contains("Constraint 5")) {
						NBConstraint5++;
					}
					writer.write(violatedName);
					writer.write("\t" + model.getConstraint(i).getIntValue() + "\n");
				}
			}
			writer.write("Number of constraint 1 violations: " + NBConstraint1 + "\tTotal Constrain 1: "
					+ numberConstraint1 + "\n");
			writer.write("Number of constraint 2 violations: " + NBConstraint2 + "\tTotal Constrain 2: "
					+ numberConstraint2 + "\n");
			writer.write("Number of constraint 3 violations: " + NBConstraint3 + "\tTotal Constrain 3: "
					+ numberConstraint3 + "\n");
			writer.write("Number of constraint 4 violations: " + NBConstraint4 + "\tTotal Constrain 4: "
					+ numberConstraint4 + "\n");
			writer.write("Number of constraint 5 violations: " + NBConstraint5 + "\tTotal Constrain 5: "
					+ numberConstraint5 + "\n");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
