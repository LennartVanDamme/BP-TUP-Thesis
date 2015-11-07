package tup.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import localsolver.*;

/**
 * Created by Lennart on 22/09/15.
 */
public class TUPSolver3 {

	LocalSolver localsolver;
	LSModel model;
	LSExpression totalDistanceTraveled;
	LSExpression[][] umpireAssignment; // index0 = umpireIndex; index1=gameIndex
	LSExpression[] umpireDistanceTraveled;
	LSExpression[][] timesTeamVisitedHome;
	// LSExpression[][][] teamsSeenPerRoundByUmpire;
	LSExpression distanceGame;
	Problem problem;
	Solution solution;
	LSSolution lsSolution;

	int[][] homeTeamToGame;
	int[][] awayTeamToGame;

	int gamesPerRound, annealinglvl, gameOfHomeTeamInNextRound, gameOfAwayTeamInNextRound;
	int numberConstraint1 = 0;
	int numberConstraint5 = 0;
	int numberConstraint2 = 0;
	int numberConstraint3 = 0;
	int numberConstraint4 = 0;

	public TUPSolver3(int annealinglvl) {
		localsolver = new LocalSolver();
		model = localsolver.getModel();
		this.annealinglvl = annealinglvl;
	}

	public void readInstance(String path) {
		try {
			ProblemReader problemReader = new ProblemReader();
			File instanceFile = new File(path);
			problem = problemReader.readProblemFromFile(instanceFile, 1, 1, "test");
			problem.setTournament(problem.opponents);
			gamesPerRound = problem.nTeams / 2;
			problem.q1 = problem.nUmpires;
			System.out.println("q1 = " + problem.q1);

			problem.q2 = problem.nUmpires / 2;
			System.out.println("q2 = " + problem.q2);

		} catch (IOException ioe) {
			System.out.println("IOException: " + ioe.getMessage());
			ioe.printStackTrace();
		}
	}

	public void solve(int limit) {
		try {

			model = localsolver.getModel();

			// Beslissingsvariabele die gebruikt wordt in het model.
			// umpireAssignment[u][g] = 1 als een umpire u toegekent is aan een
			// game g.
			// Anders is umpireAssignment[u][g] = 0.
			umpireAssignment = new LSExpression[problem.nUmpires][problem.nGames];

			// Deze LSExpression houdt bij hoeveel afstand elke umpire aflegt.
			umpireDistanceTraveled = new LSExpression[problem.nUmpires];

			// Deze LSExpression wordt gebruikt om bij te houden hoeveel maal
			// een team bezocht is door een umpire.
			timesTeamVisitedHome = new LSExpression[problem.nUmpires][problem.nTeams];

			// Deze LSExpression houdt bij welk team gezien is door een umpire u
			// in een ronde r.
			// teamSeenPerRoundUmpire[u][r][0] = homeTeam gezien in ronde r door
			// umpire u.
			// teamSeenPerRoundUmpire[u][r][1] = awayTeam gezien in ronde r door
			// umpire u.
			// teamsSeenPerRoundByUmpire = new
			// LSExpression[problem.nUmpires][problem.nRounds][2];

			// De beslissingsvariabele is een booleaanse variabele en dient
			// gedefinieerd te worden als booleaanse variabele.
			for (int u = 0; u < problem.nUmpires; u++) {
				for (int g = 0; g < problem.nGames; g++) {
					umpireAssignment[u][g] = model.boolVar();
					umpireAssignment[u][g].setName("Umpire assignment of umpire " + u + "to game " + g);
				}
			}

			// We definieren de afstand die een umpire aflegd als een som.
			for (int u = 0; u <= this.problem.nUmpires - 1; u++) {
				umpireDistanceTraveled[u] = this.model.sum();
			}

			// De keren dat een umpire een team visit wordt gezien al een
			// sommatie.
			for (int u = 0; u < problem.nUmpires; u++) {
				for (int t = 0; t < problem.nTeams; t++) {
					timesTeamVisitedHome[u][t] = model.sum();
					timesTeamVisitedHome[u][t].setName("Team " + t + " visists by umpire " + u);
				}
			}

			// for(int u = 0; u <= this.problem.nUmpires-1; u++){
			// for(int r = 0; r <= this.problem.nRounds-1; r++){
			// teamsSeenPerRoundByUmpire[u][r][0] = this.model.sum();
			// teamsSeenPerRoundByUmpire[u][r][1] = this.model.sum();
			// }
			// }

			for (int u = 0; u <= this.problem.nUmpires - 1; u++) {
				for (int r = 0; r <= this.problem.nRounds - 2; r++) {
					for (int i = r * gamesPerRound; i < r * gamesPerRound + gamesPerRound; i++) {
						for (int j = (r + 1) * gamesPerRound; j < (r + 1) * gamesPerRound + gamesPerRound; j++) {
							umpireDistanceTraveled[u].addOperand(this.model.prod(this.problem.distGames[i][j],
									this.model.and(umpireAssignment[u][i], umpireAssignment[u][j])));
						}
					}
				}
			}

			// We take in account the times an umpire visist a home venue
			for (int u = 0; u < problem.nUmpires; u++) {
				for (int g = 0; g < problem.nGames; g++) {
					timesTeamVisitedHome[u][problem.games[g][0] - 1].addOperand(umpireAssignment[u][g]);
				}
			}

			/**
			 * First constraint for TUP Every game umpired by exactly one umpire
			 */

			for (int g = 0; g < problem.nGames; g++) {
				LSExpression gameUmpired = model.sum();
				for (int u = 0; u < problem.nUmpires; u++) {
					gameUmpired.addOperand(umpireAssignment[u][g]);
					numberConstraint1++;
				}
				gameUmpired.setName("Constraint 1 for game " + g);
				model.addConstraint(model.eq(gameUmpired, 1));
			}

			/**
			 * Second constraint for TUP Every umpire works in every round
			 */

			for (int u = 0; u < problem.nUmpires; u++) {
				for (int r = 0; r < problem.nRounds; r++) {
					LSExpression timesWorked = model.sum();
					for (int g = 0; g <= gamesPerRound - 1; g++) {
						timesWorked.addOperand(umpireAssignment[u][r * (gamesPerRound) + g]);
						numberConstraint2++;
					}
					timesWorked.setName("Constraint 2 for round  " + r + " and umpire " + u);
					model.addConstraint(model.eq(timesWorked, 1));
				}
			}

			// Third constraint for TUP Every umpire must visit the home venue
			// of every team at least once
			// So the times an umpire visits a team must be greater of equal
			// than 1

			for (int u = 0; u < problem.nUmpires; u++) {
				for (int t = 0; t < problem.nTeams; t++) {
					LSExpression teamVisited = model.geq(timesTeamVisitedHome[u][t], 1);
					teamVisited.setName("Constraint 3 for umpire " + u + " at team " + t);
					model.addConstraint(teamVisited);
					numberConstraint3++;
				}
			}

			// Bij constraint 4 mag een umpire een venue pas na q1-1 ronden
			// terug zien.
			// We gaan voor elke game kijken wat het home team is en in welke
			// ronde het game plaats vindt.
			// We gaan dan naar de volgende ronden kijken en het gameID van het
			// homeTeam in die ronde opvragen door gebruik te maken
			// van de matrix roundTeamToGame.
			// Als we deze gameID hebben, dan weten we dus in welke game het
			// team speelt in ronde r.
			// Door nu te zeggen dat de sommatie van de toekenningen over de
			// ronde kleiner of gelijk moet zijn aan 1, is constraint 4
			// gedefinieerd.

			// voor iedere umpire
			for (int u = 0; u < problem.nUmpires; u++) {

				// voor ieder window
				for (int startRonde = 0; startRonde < problem.nRounds - problem.q1 + 1; startRonde++) {
					int eindRonde = startRonde + problem.q1;

					// voor ieder team
					for (int team = 0; team < problem.nTeams; team++) {
						List<Integer> gamesWithHomeTeamInWindow = new ArrayList<>();

						// het window doorzoeken naar games met hometeam = team
						for (int r = startRonde; r < eindRonde; r++) {
							for (int gir = 0; gir < problem.nUmpires; gir++) {
								int game = r * problem.nUmpires + gir;
								if (problem.games[game][0] - 1 == team) {
									gamesWithHomeTeamInWindow.add(game);
								}
							}
						}

						LSExpression constraint4 = model.sum();
						for (int game : gamesWithHomeTeamInWindow) {
							constraint4.addOperand(umpireAssignment[u][game]);
						}
						model.addConstraint(model.leq(constraint4, 1));
						numberConstraint4++;
					}

				}

			}

			// new constraint 5
			// voor iedere umpire
			for (int u = 0; u < problem.nUmpires; u++) {

				// voor ieder window
				for (int startRonde = 0; startRonde < problem.nRounds - problem.q2 + 1; startRonde++) {
					int eindRonde = startRonde + problem.q2;

					// voor ieder team
					for (int team = 0; team < problem.nTeams; team++) {
						List<Integer> gamesWithTeamInWindow = new ArrayList<>();

						// het window doorzoeken naar games met hometeam = team
						for (int r = startRonde; r < eindRonde; r++) {
							for (int gir = 0; gir < problem.nUmpires; gir++) {
								int game = r * problem.nUmpires + gir;
								if (problem.games[game][0] - 1 == team || problem.games[game][1] - 1 == team) {
									gamesWithTeamInWindow.add(game);
								}
							}
						}

						LSExpression constraint5 = model.sum();
						for (int game : gamesWithTeamInWindow) {
							constraint5.addOperand(umpireAssignment[u][game]);
						}
						model.addConstraint(model.leq(constraint5, 1));
						numberConstraint5++;
					}

				}

			}

			/**
			 * Fourth constraint for TUP No umpire is at the same venue more
			 * than q1 rounds
			 */
			/*
			 * for(int u = 0; u <= this.problem.nUmpires-1; u++){ for (int r =
			 * 0; r <= this.problem.nRounds-1; r++){ int teller = 1;
			 * while(teller < this.problem.q1 && r+teller <
			 * this.problem.nRounds){
			 * model.constraint(model.neq(teamsSeenPerRoundByUmpire[u][r][0],
			 * teamsSeenPerRoundByUmpire[u][r+teller][0])); teller++; } } }
			 */

			/**
			 * Fifth constraint for TUP No umpire sees the same team more than
			 * once in q2 rounds
			 */
			/*
			 * for(int u = 0; u <= this.problem.nUmpires-1; u++){ for(int r = 0;
			 * r <= this.problem.nRounds-1; r++){ int teller = 1; while (teller
			 * < this.problem.q2 && r+teller < this.problem.nRounds){
			 * model.constraint(model.neq(teamsSeenPerRoundByUmpire[u][r][0],
			 * teamsSeenPerRoundByUmpire[u][r+teller][0]));
			 * model.constraint(model.neq(teamsSeenPerRoundByUmpire[u][r][0],
			 * teamsSeenPerRoundByUmpire[u][r+teller][1]));
			 * model.constraint(model.neq(teamsSeenPerRoundByUmpire[u][r][1],
			 * teamsSeenPerRoundByUmpire[u][r+teller][0]));
			 * model.constraint(model.neq(teamsSeenPerRoundByUmpire[u][r][1],
			 * teamsSeenPerRoundByUmpire[u][r+teller][1])); teller++; } } }
			 */

			totalDistanceTraveled = model.sum(umpireDistanceTraveled);
			model.minimize(totalDistanceTraveled);

			model.close();

			LSPhase phase = localsolver.createPhase();
			phase.setTimeLimit(limit);

			LSParam params = this.localsolver.getParam();
			params.setAnnealingLevel(annealinglvl);

			localsolver.solve();
			lsSolution = localsolver.getSolution();

		} catch (LSException e) {
			System.out.println("LSException: " + e.getMessage());
			System.exit(-1);
		}
	}

	public void writeSolution(String outputFileName) {
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFileName));

			bufferedWriter.write("Totale travel distance: " + totalDistanceTraveled.getValue() + "\n");
			for (int u = 0; u <= this.problem.nUmpires - 1; u++) {
				bufferedWriter.write("Games umpired door umpire" + (u + 1) + "\n\t");
				for (int g = 0; g <= this.problem.nGames - 1; g++) {
					if (umpireAssignment[u][g].getValue() == 1) {
						bufferedWriter.write(this.problem.games[g][0] + "," + this.problem.games[g][1] + "\t");
					}
				}
				bufferedWriter.write("\n\n");
			}
			bufferedWriter.write("Solution Status from LocalSolver: " + lsSolution.getStatus().toString() + "\n\n");
			bufferedWriter.write(solution.toString());
			bufferedWriter.write("Violated Constrainsts\n");
			for (int i = 0; i < model.getNbConstraints(); i++) {
				if (model.getConstraint(i).isViolated()) {
					String output = model.getConstraint(i).getName() + "\tValue: " + model.getConstraint(i).getValue();
					bufferedWriter.write(output);
				}
			}
			bufferedWriter.close();

		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	public void writeViolatedConstraints(String outputFile) {
		int NBConstraint1 = 0;
		int NBConstraint2 = 0;
		int NBConstraint3 = 0;
		int NBConstraint4 = 0;
		int NBConstraint5 = 0;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
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

	public void solution() {
		this.solution = new Solution(this.problem);
		for (int u = 0; u <= this.problem.nUmpires - 1; u++) {
			for (int g = 0; g <= this.problem.nGames - 1; g++) {
				if (umpireAssignment[u][g].getValue() == 1) {
					solution.assignment[this.problem.gameToRound[g]][u] = g;
				}
			}
		}
		solution.printAssignmentDetail();
		solution.calculateConsecutiveViolations();
		solution.calculateHomeVisitViolations();
		solution.calculateTravelDistance();
		solution.calculateScore();
		System.out.println(solution.toString());
	}

}
