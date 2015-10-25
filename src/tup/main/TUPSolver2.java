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
	int[][] gamesToEdge;

	public TUPSolver2(int annealingLvl) {
		this.solver = new LocalSolver();
		this.model = this.solver.getModel();
		this.annealingLvl = annealingLvl;
		constantTrue = this.model.createConstant(1);

	}

	public void readInstance(String path) {
		try {
			ProblemReader problemReader = new ProblemReader();
			File instanceFile = new File(path);
			this.problem = problemReader.readProblemFromFile(instanceFile, 1, 1, "test");
			this.problem.setTournament(problem.opponents);
			this.gamesPerRound = this.problem.nTeams / 2;
			this.edgesPerRound = this.gamesPerRound * this.gamesPerRound;
			this.roundEdges = this.edgesPerRound * (this.problem.nRounds - 1);
			this.totalAmountEdges = this.roundEdges + this.gamesPerRound;

		} catch (IOException ioe) {
			System.out.println("IOException: " + ioe.getMessage());
			ioe.printStackTrace();
		}
	}

	public void printoutGamesToEdge() {
		for (int e = 0; e < this.roundEdges; e++) {
			System.out.println("edgenr: " + e + "\t[" + this.gamesToEdge[e][0] + ',' + this.gamesToEdge[e][1] + ']');
		}
	}

	public void instantiateGamesToEdge() {
		this.gamesToEdge = new int[this.totalAmountEdges][2];
		int ronde = 1;
		int hulp = this.edgesPerRound;
		for (int e = 0; e < this.roundEdges; e++) {
			if (hulp == 0) {
				hulp = this.edgesPerRound;
				ronde++;
			}
			this.gamesToEdge[e][0] = e / this.gamesPerRound;
			this.gamesToEdge[e][1] = ronde * this.gamesPerRound + e % this.gamesPerRound;
			hulp--;
		}
		for (int e = 0; e < this.gamesPerRound; e++) {
			this.gamesToEdge[this.roundEdges + e][0] = -1;
			this.gamesToEdge[this.roundEdges + e][1] = -1;
		}
	}

	public void solve(int limit) {
		try {
			umpireAssignment = new LSExpression[this.problem.nUmpires][this.totalAmountEdges];
			umpireDistanceTraveled = new LSExpression[this.problem.nUmpires];
			timesteamVisited = new LSExpression[this.problem.nUmpires][this.problem.nTeams];

			for (int u = 0; u < this.problem.nUmpires; u++) {
				umpireDistanceTraveled[u] = this.model.sum();

			}

			for (int u = 0; u < this.problem.nUmpires; u++) {
				for (int e = 0; e < this.totalAmountEdges; e++) {
					umpireAssignment[u][e] = this.model.boolVar();
				}
			}

			for (int u = 0; u < this.problem.nUmpires; u++) {
				for (int t = 0; t < this.problem.nTeams; t++) {
					timesteamVisited[u][t] = this.model.sum();
				}
			}

			for (int u = 0; u < this.problem.nUmpires; u++) {
				for (int e = 0; e < this.roundEdges; e++) {
					umpireDistanceTraveled[u].addOperand(this.model.prod(umpireAssignment[u][e],
							this.problem.distGames[gamesToEdge[e][0]][gamesToEdge[e][1]]));
					timesteamVisited[u][this.problem.games[this.gamesToEdge[e][0]][0]]
							.addOperand(this.model.iif(umpireAssignment[u][e], 1, 0));
				}
			}

			/*
			 * Constraint 1 : Every game is umpired by exactly one umpire
			 */
			for (int u = 0; u < this.problem.nUmpires; u++) {
				for (int e = 0; e < this.totalAmountEdges; e++) {
					this.model.addConstraint(this.model.leq(umpireAssignment[u][e], 1));
				}
			}

			/*
			 * Constraint 2 : Every umpire works exacly once in each round
			 */

			for (int u = 0; u < this.problem.nUmpires; u++) {
				for (int r = 0; r < this.problem.nRounds - 1; r++) {
					LSExpression workedInRound = this.model.sum();
					for (int edge = 0; edge < this.edgesPerRound; edge++) {
						workedInRound.addOperand(umpireAssignment[u][(r * this.gamesPerRound) + edge]);
					}
					this.model.addConstraint(this.model.eq(workedInRound, 1));
				}
			}

			/*
			 * Constraint 3
			 */

			// for (int u = 0; u < this.problem.nUmpires; u++) {
			// for (int t = 0; t < this.problem.nTeams; t++) {
			// this.model.constraint(this.model.geq(timesteamVisited[u][t], 1));
			// }
			// }

			totalDistanceTraveled = this.model.sum(umpireDistanceTraveled);
			this.model.minimize(totalDistanceTraveled);

			this.model.close();

			LSPhase phase = solver.createPhase();
			phase.setTimeLimit(limit);

			this.solver.getParam().setAnnealingLevel(annealingLvl);

			solver.solve();
			lsSolution = solver.getSolution();

		} catch (LSException e) {
			System.out.println("LSException: " + e.getMessage());
			System.exit(-1);
		}
	}

}
