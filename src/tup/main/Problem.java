package tup.main;

import java.util.*;

public class Problem {

	public String name;

	// basic info
	public int nTeams; // number of teams
	public int[][] dist; // distance matrix
	public int[][] opponents; // game schedule, opponent of each team in each
								// round
	public int q1;
	public int q2;

	public int[][] distGames; // distance matrix [game]x[game]

	// additional info
	public int nUmpires; // number of umpires
	public int nGames; // number of games
	public int nRounds; // number of rounds

	public int[][] games; // games
							// [gameID=nGames][index0=hometeam,index1=awayteam]
	public int[] gameToRound; // index=gameID, value=roundNr

	public int[][] roundTeamToGame; // [roundNr][teamNr], value = gameID
	public boolean[][] possibleVisits;

	public int teamTravelDistance;

	public Problem(int nTeams, int[][] dist, int[][] opponents, int q1, int q2, String name) {
		this.nTeams = nTeams;
		this.dist = dist;
		this.opponents = opponents;
		this.q1 = q1;
		this.q2 = q2;
		this.name = name;

		nUmpires = nTeams / 2;
		nRounds = nTeams * 2 - 2;
		nGames = nRounds * nTeams / 2;

		games = new int[nGames][2];
		gameToRound = new int[nGames];

		roundTeamToGame = new int[nRounds][nTeams];

		/*
		 * Hier wordt de opponents matrix gelezen en worden de games gecreëerd.
		 * Als de waarde in de opponents matric groter is dan 0 dan speelt dit
		 * team een wedstrijd in het stadium van TEAM teagen TEAM. gameToRounds
		 * wordt geinitialiseerd. roundTeamToGame wordt geinitialiseerd.
		 */
		int game = 0;
		for (int round = 0; round < nRounds; round++) {
			for (int team = 1; team <= nTeams; team++) {
				if (opponents[round][team - 1] > 0) {
					games[game][0] = team;
					games[game][1] = opponents[round][team - 1];
					gameToRound[game] = round;
					roundTeamToGame[round][team - 1] = game;
					game++;
				} else {
					roundTeamToGame[round][team - 1] = -1;
				}
			}
		}

		calcultateTeamTravelDistance();

		possibleVisits = new boolean[nRounds][nTeams];
		for (int i = nRounds - 1; i >= 0; i--) {
			for (int j = 0; j < nTeams; j++) {
				if (opponents[i][j] > 0 || (i < nRounds - 1 && possibleVisits[i + 1][j])) {
					possibleVisits[i][j] = true;
				}
			}
		}

		distGames = new int[nGames][nGames];
		for (int i = 0; i < nGames; i++)
			for (int j = 0; j < nGames; j++)
				distGames[i][j] = dist[games[i][0] - 1][games[j][0] - 1];
	}

	public void setTournament(int[][] opponents) {
		games = new int[nGames][2];
		gameToRound = new int[nGames];

		roundTeamToGame = new int[nRounds][nTeams];
		this.opponents = this.opponents;
		int m = 0;
		for (int round = 0; round < nRounds; round++) {
			for (int team = 1; team <= nTeams; team++) {
				if (this.opponents[round][team - 1] > 0) {
					games[m][0] = team;
					games[m][1] = this.opponents[round][team - 1];
					gameToRound[m] = round;
					roundTeamToGame[round][team - 1] = m;
					roundTeamToGame[round][Math.abs(this.opponents[round][team-1])-1] = m;
					m++;
				} 
				//else {
				//	roundTeamToGame[round][team - 1] = -1;
				//}
			}
		}

		calcultateTeamTravelDistance();

		possibleVisits = new boolean[nRounds][nTeams];
		for (int i = nRounds - 1; i >= 0; i--) {
			for (int j = 0; j < nTeams; j++) {
				if (this.opponents[i][j] > 0 || (i < nRounds - 1 && possibleVisits[i + 1][j])) {
					possibleVisits[i][j] = true;
				}
			}
		}
	}

	@Override
	public String toString() {
		return "Problem [nTeams=" + nTeams + ",\n dist=" + Arrays.deepToString(dist) + ",\n opponents="
				+ Arrays.deepToString(opponents) + ",\n distGames=" + Arrays.deepToString(distGames) + ",\n nUmpires="
				+ nUmpires + ",\n nGames=" + nGames + ",\n nRounds=" + nRounds + ",\n q1=" + q1 + ",\n q2=" + q2
				+ ",\n games=" + Arrays.deepToString(games) + ",\n gameToRound= " + Arrays.toString(gameToRound)
				+ ",\n roundTeamToGame=" + Arrays.deepToString(roundTeamToGame) + ",\n possibleVisists= "
				+ Arrays.deepToString(possibleVisits) + "]";
	}

	/**
	 * TTP distance
	 */
	private void calcultateTeamTravelDistance() {
		teamTravelDistance = 0;
		int prevLoc;
		for (int team = 0; team < nTeams; team++) {
			prevLoc = team;
			for (int round = 0; round < nRounds; round++) {
				if (prevLoc == team && opponents[round][team] < 0) {
					int toLoc = (-opponents[round][team]) - 1;
					teamTravelDistance += dist[team][toLoc];
					prevLoc = toLoc;
				}
				if (prevLoc != team && opponents[round][team] < 0) {
					int toLoc = (-opponents[round][team]) - 1;
					teamTravelDistance += dist[prevLoc][toLoc];
					prevLoc = toLoc;
				}
				if (prevLoc != team && opponents[round][team] > 0) {
					teamTravelDistance += dist[prevLoc][team];
					prevLoc = team;
				}
			}
			if (prevLoc != team) {
				teamTravelDistance += dist[prevLoc][team];
				prevLoc = team;
			}
		}
	}
}
