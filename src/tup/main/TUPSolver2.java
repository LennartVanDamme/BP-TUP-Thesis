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
import java.util.HashMap;
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

	int gamesPerRound, annealingLvl, edgesPerRound, totalAmountOfEdges;

	int numberConstraint1 = 0;
	int numberConstraint2 = 0;
	int numberConstraint3 = 0;
	int numberConstraint4 = 0;
	int numberConstraint5 = 0;
	// gamesToEdge[e][0] geeft het eerste game en
	// gamesToEdgde[e][1] geeft het game in de volgende
	// ronde verbonden met het eerste gameß
	int[][] edgeToGames;
	ArrayList<ArrayList<Integer>> gameNrToEgdeNrs = new ArrayList<ArrayList<Integer>>();
	HashMap<TUPKey, Integer> gameToEdgeMap = new HashMap<TUPKey, Integer>();

	// homeGamesOfTeamInRound[team][ronde] = game als het team in die ronde
	// thuis
	// speelt
	// homeGamesOfTeamInRound[team][ronde] = -1 als het team in die ronde niet
	// thus
	// speelt
	int[][] homeGamesOfTeamInRound;

	// used for flow
	ArrayList<Integer> edgesLeavingSourceNode = new ArrayList<Integer>();
	ArrayList<Integer> edgesEnteringSinkNode = new ArrayList<Integer>();

	// index = game --> retrun list of edges
	ArrayList<ArrayList<Integer>> edgesEnteringGames = new ArrayList<ArrayList<Integer>>();
	ArrayList<ArrayList<Integer>> edgesLeavingGames = new ArrayList<ArrayList<Integer>>();

	int[][] constraint4Matrix;
	int[][] constraint5Matrix;

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
			totalAmountOfEdges = edgesPerRound * (problem.nRounds + 1);
			problem.q1 = problem.nUmpires;
			problem.q2 = problem.nUmpires / 2;
			System.out.println("Q1" + problem.q1);
			System.out.println("Q2" + problem.q2);

		} catch (IOException ioe) {
			System.out.println("IOException: " + ioe.getMessage());
			ioe.printStackTrace();
		}
	}

	public void printoutGamesToEdge() {
		for (int e = 0; e < totalAmountOfEdges; e++) {
			System.out.println("edgenr: " + e + "\t[" + edgeToGames[e][0] + ',' + edgeToGames[e][1] + ']');
		}
	}

	public void instantiateArrays() {
		edgeToGames = new int[totalAmountOfEdges][2];
		homeGamesOfTeamInRound = new int[problem.nTeams][problem.nRounds];
		for (int team = 0; team < problem.nTeams; team++) {
			for (int round = 0; round < problem.nRounds; round++) {
				homeGamesOfTeamInRound[team][round] = -1;
			}
		}
		for (int game = 0; game < problem.nGames; game++) {
			homeGamesOfTeamInRound[problem.games[game][0] - 1][problem.gameToRound[game]] = game;
			edgesEnteringGames.add(new ArrayList<>());
			edgesLeavingGames.add(new ArrayList<>());
		}
		for (int e = 0; e < edgesPerRound; e++) {
			int game = e / gamesPerRound;
			edgeToGames[e][0] = -1;
			edgeToGames[e][1] = game;
			edgesEnteringGames.get(game).add(e);
			edgesLeavingSourceNode.add(e);
		}
		int ronde = 1;
		int hulp = 0;
		for (int e = edgesPerRound; e < (totalAmountOfEdges - edgesPerRound); e++) {
			if (hulp == edgesPerRound) {
				ronde++;
				hulp = 0;
			}
			hulp++;
			int firstGame = (e - edgesPerRound) / gamesPerRound;
			int secondGame = ronde * gamesPerRound + e % gamesPerRound;
			edgeToGames[e][0] = firstGame;
			edgeToGames[e][1] = secondGame;
			edgesLeavingGames.get(firstGame).add(e);
			// System.out.println(firstGame);
			edgesEnteringGames.get(secondGame).add(e);
		}
		for (int e = (totalAmountOfEdges - edgesPerRound); e < totalAmountOfEdges; e++) {
			int game = (problem.nRounds - 1) * gamesPerRound + e % gamesPerRound;
			// System.out.println(game);
			edgeToGames[e][0] = game;
			edgeToGames[e][1] = -1;
			edgesLeavingGames.get(game).add(e);
			edgesEnteringSinkNode.add(e);
		}

	}

	/*
	 * Constraint 1 : Every game is umpired by exactly one umpire
	 */

	private void makeConstraint1() {

		for (int game = 0; game < problem.nGames; game++) {

			List<Integer> edges = edgesEnteringGames.get(game);

			LSExpression constraint1 = model.sum();
			constraint1.setName("Constraint 1 voor game " + game);

			// for every umpire
			for (int u = 0; u < problem.nUmpires; u++) {

				// add all entering edges to constraint
				for (Integer edge : edges) {
					constraint1.addOperand(umpireAssignment[u][edge]);
				}

			}
			model.constraint(model.eq(constraint1, 1));
			numberConstraint1++;
		}

	}

	/*
	 * Constraint 2 : Every umpire works exacly once in each round
	 */

	private void makeConstraint2() {

		// Flowrepresentatie beginend bij de source node
		for (int u = 0; u < problem.nUmpires; u++) {

			/* ------------- Constraint 2 for source node --------------- */
			LSExpression flowConstraintSourceNode = model.sum();
			flowConstraintSourceNode.setName("Flow constraint voor de source node bij umpire " + u);

			for (Integer edge : edgesLeavingSourceNode) {
				flowConstraintSourceNode.addOperand(umpireAssignment[u][edge]);
			}

			LSExpression constraint2Sourcenode = model.eq(model.sub(0, flowConstraintSourceNode), -1);

			model.constraint(constraint2Sourcenode);
			numberConstraint2++;

			/* ------------- Constraint 2 for sink node --------------- */
			LSExpression flowConstraintSinkNode = model.sum();
			flowConstraintSinkNode.setName("Flow constraint voor de sink node bij umpire " + u);

			for (Integer edge : edgesEnteringSinkNode) {
				flowConstraintSinkNode.addOperand(umpireAssignment[u][edge]);
			}

			LSExpression constraint2Sinknode = model.eq(model.sub(flowConstraintSinkNode, 0), 1);

			model.constraint(constraint2Sinknode);
			numberConstraint2++;

			/* ------------- Constraint 2 for all games --------------- */

			for (int game = 0; game < problem.nGames; game++) {

				List<Integer> edgesEnteringGame = edgesEnteringGames.get(game);

				LSExpression sumEnteringEdges = model.sum();
				for (Integer edge : edgesEnteringGame) {
					sumEnteringEdges.addOperand(umpireAssignment[u][edge]);
				}

				List<Integer> edgesLeavingGame = edgesLeavingGames.get(game);

				LSExpression sumLeavingEdges = model.sum();
				for (Integer edge : edgesLeavingGame) {
					sumLeavingEdges.addOperand(umpireAssignment[u][edge]);
				}

				LSExpression constraint2 = model.sub(sumEnteringEdges, sumLeavingEdges);
				constraint2.setName("Constraint 2 voor game " + game + " bij umpire " + u);

				model.constraint(model.eq(constraint2, 0));
				numberConstraint2++;
			}
		}

	}

	/*
	 * Constraint 3 : Every team has to be seen at least once
	 */

	private void makeConstraint3() {

		for (int u = 0; u < problem.nUmpires; u++) {

			for (int team = 0; team < problem.nTeams; team++) {

				LSExpression constraint3 = model.sum();
				constraint3.setName("Constraint 3 voor umpire " + u + " en team " + team);

				for (int ronde = 0; ronde < problem.nRounds; ronde++) {

					if (homeGamesOfTeamInRound[team][ronde] != -1) {
						List<Integer> edges = edgesEnteringGames.get(homeGamesOfTeamInRound[team][ronde]);
						for (Integer edge : edges) {
							constraint3.addOperand(umpireAssignment[u][edge]);
						}
					}
				}
				model.addConstraint(model.geq(constraint3, 1));
				numberConstraint3++;
			}
		}

	}

	// Constraint 4 : An umpire can not revisit the same home team in
	// q1 rounds
	private void makeConstraint4() {

		// Voor alle umpires
		for (int u = 0; u < problem.nUmpires; u++) {

			for (int startRonde = 0; startRonde < problem.nRounds - problem.q1 + 1; startRonde++) {
				int eindRonde = startRonde + problem.q1;

				for (int team = 0; team < problem.nTeams; team++) {

					List<Integer> gamesOfTeamInWindow = new ArrayList<Integer>();

					for (int ronde = startRonde; ronde < eindRonde; ronde++) {

						for (int gameInRound = 0; gameInRound < gamesPerRound; gameInRound++) {
							int game = ronde * gamesPerRound + gameInRound;
							if (problem.games[game][0] - 1 == team) {
								gamesOfTeamInWindow.add(game);
							}

						}
					}

					LSExpression constraint4 = model.sum();
					for (Integer game : gamesOfTeamInWindow) {
						List<Integer> edges = edgesEnteringGames.get(game);
						for (Integer edge : edges) {
							constraint4.addOperand(umpireAssignment[u][edge]);
						}
					}
					constraint4.setName("Constraint 4 voor umpire " + u + " voor window [" + startRonde + ","
							+ eindRonde + "] bij team " + team);
					model.constraint(model.leq(constraint4, 1));
					numberConstraint4++;
				}
			}

		}

	}

	private void makeConstraint5() {

		for (int u = 0; u < problem.nUmpires; u++) {

			for (int startRonde = 0; startRonde < problem.nRounds - problem.q2 + 1; startRonde++) {
				int eindRonde = startRonde + problem.q2;

				for (int team = 0; team < problem.nTeams; team++) {

					List<Integer> gamesInWindow = new ArrayList<Integer>();

					for (int ronde = startRonde; ronde < eindRonde; ronde++) {

						for (int gameInRonde = 0; gameInRonde < gamesPerRound; gameInRonde++) {
							int game = ronde * gamesPerRound + gameInRonde;
							if (problem.games[game][0] - 1 == team || problem.games[game][1] - 1 == team) {
								gamesInWindow.add(game);
							}
						}
					}

					LSExpression constraint5 = model.sum();
					constraint5.setName("Constraint 4 voor umpire " + u + " voor window [" + startRonde + ","
							+ eindRonde + "] bij team " + team);
					for (Integer game : gamesInWindow) {
						List<Integer> edges = edgesEnteringGames.get(game);
						for (Integer edge : edges) {
							constraint5.addOperand(umpireAssignment[u][edge]);
						}
					}
					model.constraint(model.leq(constraint5, 1));

				}
			}
		}
	}

	public void solve(int limit) {
		try {

			model = solver.getModel();

			umpireAssignment = new LSExpression[problem.nUmpires][totalAmountOfEdges];
			totalDistanceTraveled = model.sum();

			for (int u = 0; u < problem.nUmpires; u++) {
				for (int e = 0; e < totalAmountOfEdges; e++) {
					umpireAssignment[u][e] = model.boolVar();
				}
			}

			// Totale afstand die de umpires moeten afleggen
			for (int e = edgesPerRound; e < (totalAmountOfEdges - edgesPerRound); e++) {
				for (int u = 0; u < problem.nUmpires; u++) {
					totalDistanceTraveled.addOperand(model.prod(umpireAssignment[u][e],
							problem.distGames[edgeToGames[e][0]][edgeToGames[e][1]]));
				}
			}

			model.minimize(totalDistanceTraveled);

			makeConstraint1();
			makeConstraint2();
			makeConstraint3();
			makeConstraint4();
			makeConstraint5();

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
		// System.out.println("Nodes leaving source node:
		// "+edgesLeavingSourceNode.toString());
		// System.out.println("Nodes entering sink node:
		// "+edgesEnteringSinkNode.toString());
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("/Users/Lennart/Desktop/edgeToGame.txt")));
		for (int i = 0; i < edgeToGames.length; i++) {
			writer.write(
					"Edgenr: " + i + " FirstGame: " + edgeToGames[i][0] + " SecondGame: " + edgeToGames[i][1] + "\n");
		}
		writer.close();
		writer = new BufferedWriter(new FileWriter(new File("/Users/Lennart/Desktop/edgesEnteringGames.txt")));
		BufferedWriter writer2 = new BufferedWriter(
				new FileWriter(new File("/Users/Lennart/Desktop/edgesLeavingGames.txt")));
		for (int game = 0; game < problem.nGames; game++) {
			String output1 = new String("Edges Entering game " + game + "--> ");
			String output2 = new String("Edges Leaving game " + game + "--> ");
			List<Integer> edgesNrsLeaving = edgesLeavingGames.get(game);
			List<Integer> edgesNrsEntering = edgesEnteringGames.get(game);
			output1 += edgesNrsEntering.toString() + "\n";
			output2 += edgesNrsLeaving.toString() + "\n";
			writer.write(output1);
			writer2.write(output2);
		}
		writer.close();
		writer2.close();

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

	public void createSolution() {
		solution = new Solution(problem);
		for (int u = 0; u < problem.nUmpires; u++) {
			for (int e = 0; e < totalAmountOfEdges; e++) {
				if (umpireAssignment[u][e].getValue() == 1) {
					// System.out.println("Umpire "+u+" toegekent aan edge "+e);
					int firstGame = edgeToGames[e][0];
					int secondGame = edgeToGames[e][1];
					if (firstGame != -1) {
						solution.assignment[problem.gameToRound[firstGame]][u] = firstGame;
					}
					if (secondGame != -1) {
						solution.assignment[problem.gameToRound[secondGame]][u] = secondGame;
					}
				}
			}
		}
		solution.printAssignmentDetail();
		solution.calculateConsecutiveViolations();
		solution.calculateHomeVisitViolations();
		solution.calculateTravelDistance();
		solution.calculateScore();
		// System.out.println(solution.toString());
	}

	public void writeSolution(String path) {
		try {
			BufferedWriter br = new BufferedWriter(new FileWriter(new File(path)));
			br.write(solution.toString());
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void printGames() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("/Users/Lennart/Desktop/games.txt")));
		for (int ronde = 0; ronde < problem.nRounds; ronde++) {
			writer.write("RONDE " + (ronde + 1) + "\n");
			for (int game = 0; game < gamesPerRound; game++) {
				int gameNr = ronde * gamesPerRound + game;
				writer.write("[" + problem.games[gameNr][0] + "," + problem.games[gameNr][1] + "] ");
			}
			writer.write("\n");
		}
		writer.close();
	}

}
