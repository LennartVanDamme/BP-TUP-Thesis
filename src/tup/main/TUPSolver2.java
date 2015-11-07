/*
Model is nog inconsistent
Constraints 4 en 5 moeten nog toegevoegd worden
*/
package tup.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
	ArrayList<ArrayList<Integer>> gameNrToEgdeNrs = new ArrayList<ArrayList<Integer>>();

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
		for (int i = 0; i < problem.nGames; i++) {
			gameNrToEgdeNrs.add(new ArrayList<Integer>());
		}
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
			gameNrToEgdeNrs.get(firstGame).add(e);
			hulp--;
		}
		for (int e = 0; e < gamesPerRound; e++) {
			edgeToGame[roundEdges + e][0] = ronde * gamesPerRound + e % gamesPerRound;
			edgeToGame[roundEdges + e][1] = -1;
			gameNrToEgdeNrs.get(ronde * gamesPerRound + e % gamesPerRound).add(roundEdges + e);
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
				for (int e = 0; e < roundEdges; e++) {
					// Als toegekent aan een edge --> dan is halen we de games
					// op die de edge verbinden
					// Anders is de distance 0
					LSExpression distance = model.iif(umpireAssignment[u][e],
							problem.distGames[edgeToGame[e][0]][edgeToGame[e][1]], 0);
					umpireDistanceTraveled[u].addOperand(distance);
				}
			}

			for (int u = 0; u < problem.nUmpires; u++) {
				for (int e = 0; e < totalAmountEdges; e++) {
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
				// Geeft een lijst met de vertrekkende edges vanuit de gameNode
				List<Integer> edgesOfGame = gameNrToEgdeNrs.get(game);
				LSExpression constraint1 = model.sum();
				// Voor elke umpire
				for (int u = 0; u < problem.nUmpires; u++) {
					// Voor elke edge vertrekkende uit die game
					for (Integer edge : edgesOfGame) {
						constraint1.addOperand(umpireAssignment[u][edge]);
					}
				}
				constraint1.setName("Consstraint 1 voor game: " + (game + 1));
				// Alle assignments voor die game over alle umpires moet gelijk
				// zijn aan 1
				model.constraint(model.eq(constraint1, 1));
				numberConstraint1++;
			}

			/*
			 * Constraint 2 : Every umpire works exacly once in each round
			 */
			// Voor iedere umpire
			for (int u = 0; u < problem.nUmpires; u++) {
				// Voor elke ronde
				for (int r = 0; r < problem.nRounds - 1; r++) {

					LSExpression constraint2 = model.sum();
					constraint2.setName("Constraint 2 voor umpire " + (u + 1) + " in ronde " + (r + 1));

					// De games overlopen die in deze ronde plaats vinden.
					for (int game = 0; game < gamesPerRound; game++) {
						int gameNr = (r * gamesPerRound) + game;

						// geeft lijst met edge geconnecteerd aan gameNr

						List<Integer> edgesOfGame = gameNrToEgdeNrs.get(gameNr);
						for (Integer edge : edgesOfGame) {
							constraint2.addOperand(umpireAssignment[u][edge]);
						}

					}
					model.constraint(model.eq(constraint2, 1));
					numberConstraint2++;
				}
			}

			/*
			 * Constraint 3 : Every team had to be seen at least once
			 */

			for (int u = 0; u < problem.nUmpires; u++) {
				for (int t = 0; t < problem.nTeams; t++) {
					LSExpression constraint3 = model.geq(timesteamVisited[u][t], 1);
					constraint3.setName("Constraint 3 voor umpire " + u + " en team " + (t + 1));
					model.constraint(constraint3);
					numberConstraint3++;
				}
			}

			// Constraint 4 : An umpire can not revisit the same home team in
			// q1 rounds

			// Voor elke umpire
			for (int u = 0; u < problem.nUmpires; u++) {

				// Voor elke ronde
				for (int startRonde = 0; startRonde < problem.nRounds - 1; startRonde++) {
					int eindRonde = Math.min(problem.nRounds - startRonde, problem.q1);

					// Voor elk team
					for (int team = 0; team < problem.nTeams; team++) {
						
						ArrayList<Integer> gamesOfTeam = new ArrayList<Integer>();
						List<Integer> edgesOfTeam = new ArrayList<Integer>();

						// Het window doorlopen
						for (int ronde = startRonde; ronde < eindRonde; ronde++) {
							
							for (int game = 0; game < gamesPerRound; game++) {
								
								// Geeft het gameNr
								int gameNrinRonde = ronde * gamesPerRound + game;
								// Kijken of het thuisteam van gamNrinRonde gelijk is aan het team
								if (problem.games[gameNrinRonde][0] - 1 == team) {
									// Opslaan van de gameNrinRonde in de lijst van games van het team
									gamesOfTeam.add(gameNrinRonde);
								}
							}
						}
						
						// Zo krijgen we ge volgorde van games in de opeenvolgende ronde
						Collections.sort(gamesOfTeam);
						
						for (int game = 0; game < gamesOfTeam.size()-1; game++) {
							
							// Dit geeft de edges van Game game
							List <Integer> edgesOfGame = gameNrToEgdeNrs.get(game);
							
							// Kijken of het team van de van het tweede game gelijk is aan het huidige team
							
							// Voorbeeld     O (game)
							//             / | \
							//            O  O  O (gameOfTeamInNextRound)
							for(Integer edge : edgesOfGame){
								int gameOfTeamInNextRound = edgeToGame[edge][1];
								if(team == problem.games[gameOfTeamInNextRound][0]-1){
									edgesOfTeam.add(edge);
								}
							}
						}
						
						LSExpression constraint4 = model.sum();
						String constraintName = new String("Constraint 4 voor umpire "+(u+1) + " Edges --> ");
						for(Integer edge: edgesOfTeam){
							constraint4.addOperand(umpireAssignment[u][edge]);
							constraintName += edge.toString()+" ";
						}
						constraint4.setName(constraintName);
						model.constraint(model.leq(constraint4, 1));
						numberConstraint4++;
					}
				}

			}
			
			// Constraint 5 : 

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
		writer = new BufferedWriter(new FileWriter(new File("/Users/Lennart/Desktop/gameNrToEgdeNrs.txt")));
		for (int game = 0; game < problem.nGames; game++) {
			String output = new String();
			output += "EdgeNr voor game" + (game + 1) + " --> ";
			List<Integer> edgeNrs = gameNrToEgdeNrs.get(game);
			for (Integer edgeNr : edgeNrs) {
				output += edgeNr.toString() + " ";
			}
			output += "\n";
			writer.write(output);
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
			writer.write("Total number of constraints: " + model.getNbConstraints() + "\n");
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
