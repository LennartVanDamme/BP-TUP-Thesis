/*
Model is nog inconsistent
Constraints 4 en 5 moeten nog toegevoegd worden
*/
package tup.main;

import java.io.File;
import java.io.IOException;

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

	// gamesToEdge[e][0] geeft het eerste game en
	// gamesToEdgde[e][1] geeft het game in de volgende
	// ronde verbonden met het eerste gameß
	int[][] gamesToEdge; 

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
			System.out.println("edgenr: " + e + "\t[" + gamesToEdge[e][0] + ',' + gamesToEdge[e][1] + ']');
		}
	}

	public void instantiateGamesToEdge() {
		gamesToEdge = new int[totalAmountEdges][2];
		int ronde = 1;
		int hulp = edgesPerRound;
		for (int e = 0; e < roundEdges; e++) {
			if (hulp == 0) {
				hulp = edgesPerRound;
				ronde++;
			}
			System.out.println(e + "%" + gamesPerRound + "=" + (e % gamesPerRound));
			gamesToEdge[e][0] = e / gamesPerRound;
			gamesToEdge[e][1] = ronde * gamesPerRound + e % gamesPerRound;
			hulp--;
		}
		for (int e = 0; e < gamesPerRound; e++) {
			gamesToEdge[roundEdges + e][0] = -1;
			gamesToEdge[roundEdges + e][1] = -1;
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
//					umpireDistanceTraveled[u].addOperand(model.prod(umpireAssignment[u][e],
//							problem.distGames[gamesToEdge[e][0]][gamesToEdge[e][1]]));
//					timesteamVisited[u][problem.games[gamesToEdge[e][0]][0]]
//							.addOperand(model.iif(umpireAssignment[u][e], 1, 0));
					int gameOfEdge = gamesToEdge[e][0];
					int teamOfGame = problem.games[gameOfEdge][0];
					timesteamVisited[u][teamOfGame].addOperand(umpireAssignment[u][e]);
				}
			}

			/*
			 * Constraint 1 : Every game is umpired by exactly one umpire
			 */
			for (int u = 0; u < problem.nUmpires; u++) {
				for (int e = 0; e < totalAmountEdges; e++) {
					model.addConstraint(model.leq(umpireAssignment[u][e], 1));
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
					model.addConstraint(model.eq(workedInRound, 1));
				}
			}

			/*
			 * Constraint 3
			 */

			// for (int u = 0; u < problem.nUmpires; u++) {
			// for (int t = 0; t < problem.nTeams; t++) {
			// model.constraint(model.geq(timesteamVisited[u][t], 1));
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

}
