package tup.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import localsolver.*;

/**
 * Created by Lennart on 22/09/15.
 */
public class TUPSolver {
	
	LocalSolver localsolver;
	LSModel model;
	LSExpression totalDistanceTraveled;
	LSExpression[][] umpireAssignment; // index0 = umpireIndex; index1=gameIndex
	LSExpression[] umpireDistanceTraveled;
	LSExpression[][] timesTeamVisitedHome;
	LSExpression[][][] teamsSeenPerRoundByUmpire;
	LSExpression distanceGame;
	Problem problem;
	Solution solution;
	LSSolution lsSolution;
	
	int gamesPerRound;
    
	public TUPSolver(){
		this.localsolver = new LocalSolver();
		this.model = localsolver.getModel();
	}
	
	public void readInstance(String path){
    	try{
    		ProblemReader problemReader = new ProblemReader();
    		File instanceFile = new File(path);
    		this.problem = problemReader.readProblemFromFile(instanceFile, 1, 1, "test");
    		this.problem.setTournament(problem.opponents);
    		
    	} catch(IOException ioe){
    		System.out.println("IOException: " + ioe.getMessage());
    		ioe.printStackTrace();
    	}
    }

    public void solve(int limit){
        try{

            this.model = localsolver.getModel();
            umpireAssignment = new LSExpression[this.problem.nUmpires][this.problem.nGames];
            umpireDistanceTraveled = new LSExpression[this.problem.nUmpires];
            timesTeamVisitedHome = new LSExpression[this.problem.nUmpires][this.problem.nTeams];
            teamsSeenPerRoundByUmpire = new LSExpression[this.problem.nUmpires][this.problem.nRounds][2];
            
            for(int u = 0; u <= this.problem.nUmpires-1; u++){
            	for(int t = 0; t <= this.problem.nTeams-1; t++){
            		timesTeamVisitedHome[u][t] = this.model.sum();
            	}
            }
            
            for(int u = 0; u <= this.problem.nUmpires-1; u++){
            	for(int r = 0; r <= this.problem.nRounds-1; r++){
            		teamsSeenPerRoundByUmpire[u][r][0] = this.model.sum();
            		teamsSeenPerRoundByUmpire[u][r][1] = this.model.sum();
            	}
            }
            

            // Every umpire has traveled 0 miles at the start 
            for (int u = 0; u <= this.problem.nUmpires-1; u++){
            	umpireDistanceTraveled[u] = this.model.sum();
            }

            // Decisionvariables are boolean values
            for (int u = 0; u <= this.problem.nUmpires-1; u++){
                for (int g = 0; g <= this.problem.nGames-1; g++){
                    this.umpireAssignment[u][g] = model.boolVar();

                }
            }
            
            for (int u = 0; u <= this.problem.nUmpires-1; u++){
            	//System.out.println("Umpire: "+u);
            	for(int r = 0; r <= this.problem.nRounds-2; r++){
            		//System.out.println("Ronde: "+r);
            		for (int i = r*gamesPerRound; i < r*gamesPerRound+gamesPerRound; i++){
            			for(int j = (r+1)*gamesPerRound; j < (r+1)*gamesPerRound+gamesPerRound; j++ ){
            				umpireDistanceTraveled[u].addOperand(this.model.prod(this.problem.distGames[i][j], this.model.and(umpireAssignment[u][i], umpireAssignment[u][j])));
            			}
            		}
            	}
            }
            
            // We take in account the times an umpire visist a home venue
            for (int u = 0; u <= this.problem.nUmpires-1; u++) {
				for (int g = 0; g <= this.problem.nGames-1; g++) {
					timesTeamVisitedHome[u][this.problem.games[g][0]-1].addOperand(this.model.prod(1, umpireAssignment[u][g]));
					teamsSeenPerRoundByUmpire[u][this.problem.gameToRound[g]][0].addOperand(this.model.prod((this.problem.games[g][0]-1), umpireAssignment[u][g]));
					teamsSeenPerRoundByUmpire[u][this.problem.gameToRound[g]][1].addOperand(this.model.prod((this.problem.games[g][1]-1), umpireAssignment[u][g])); 
				}
				
			}
            
            /**
             * First constraint for TUP
             * Every game umpired by exactly one umpire
             */
            for(int g = 0; g <= this.problem.nGames-1; g++){
            	LSExpression gameUmpired = this.model.sum();
            	for(int u = 0; u <= this.problem.nUmpires-1; u++){
            		gameUmpired.addOperand(umpireAssignment[u][g]);
            	}
            	this.model.constraint(this.model.eq(gameUmpired, 1));
            }

            /**
             * Second constraint for TUP
             * Every umpire works in every round
             */
            for (int u = 0; u <= this.problem.nUmpires-1; u++){
                for (int r = 0; r <= this.problem.nRounds-1; r++){
                    LSExpression timesWorked = model.sum();
                    for (int g = 0; g <= gamesPerRound-1; g++){
                        timesWorked.addOperand(umpireAssignment[u][r*(gamesPerRound)+g]);
                    }
                    model.constraint(model.eq(timesWorked, 1));
                }
            }
            
            /**
             * Third constraint for TUP
             * Every umpire must visit the home venue of every team at least once
             */
            for (int u = 0; u <= this.problem.nUmpires-1; u++){
                for (int t = 0; t <= this.problem.nTeams-1; t++){
                    model.constraint(model.geq(timesTeamVisitedHome[u][t], 1));
                }
            }
            
            /**
             * Fourth constraint for TUP
             * No umpire is at the same venue more than q1 rounds
             */
            for(int u = 0; u <= this.problem.nUmpires-1; u++){
        		for (int r = 0; r <= this.problem.nRounds-1; r++){
        			int teller = 1;
        			while(teller < this.problem.q1 && r+teller < this.problem.nRounds){
        				model.constraint(model.neq(teamsSeenPerRoundByUmpire[u][r][0], teamsSeenPerRoundByUmpire[u][r+teller][0]));
        				teller++;
        			}
        		}
        	}
            
            /**
             * Fifth constraint for TUP
             * No umpire sees the same team more than once in q2 rounds
             */
            for(int u = 0; u <= this.problem.nUmpires-1; u++){
            	for(int r = 0; r <= this.problem.nRounds-1; r++){
            		int teller = 1;
            		while (teller < this.problem.q2 && r+teller < this.problem.nRounds){
            			model.constraint(model.neq(teamsSeenPerRoundByUmpire[u][r][0], teamsSeenPerRoundByUmpire[u][r+teller][0]));
            			model.constraint(model.neq(teamsSeenPerRoundByUmpire[u][r][0], teamsSeenPerRoundByUmpire[u][r+teller][1]));
            			model.constraint(model.neq(teamsSeenPerRoundByUmpire[u][r][1], teamsSeenPerRoundByUmpire[u][r+teller][0]));
            			model.constraint(model.neq(teamsSeenPerRoundByUmpire[u][r][1], teamsSeenPerRoundByUmpire[u][r+teller][1]));
            			teller++;
            		}
            	}
            }
            
            totalDistanceTraveled = model.sum(umpireDistanceTraveled);
            model.minimize(totalDistanceTraveled);

            model.close();

            LSPhase phase = localsolver.createPhase();
            phase.setTimeLimit(limit);

            localsolver.solve();
            lsSolution = localsolver.getSolution();

        } catch (LSException e){
            System.out.println("LSException: " + e.getMessage());
            System.exit(-1);
        }
    }

    public void writeSolution(String outputFileName){
    	try{
    		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFileName));
    		
    		bufferedWriter.write("Totale travel distance: " + totalDistanceTraveled.getValue()+"\n");
    		for(int u = 0; u <= this.problem.nUmpires-1; u++ ){
    			bufferedWriter.write("Games umpired door umpire"+(u+1)+"\n\t");
    			for(int g = 0; g <= this.problem.nGames-1; g++){
    				if(umpireAssignment[u][g].getValue()==1){
    					bufferedWriter.write(this.problem.games[g][0]+","+this.problem.games[g][1]+"\t");
    				}
    			}
    			bufferedWriter.write("\n\n");
    		}
    		bufferedWriter.write("Solution Status from LocalSolver: "+lsSolution.getStatus().toString()+"\n\n");
    		bufferedWriter.write(solution.toString());
    		bufferedWriter.close();
    		
    	} catch(IOException ie){
    		ie.printStackTrace();
    	}
    }
    
    public void solution(){
    	this.solution = new Solution(this.problem);
    	for(int u = 0; u <= this.problem.nUmpires-1; u++){
    		for(int g = 0; g <= this.problem.nGames-1; g++){
    			if(umpireAssignment[u][g].getValue()==1){
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
