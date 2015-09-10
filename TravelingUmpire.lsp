/******** TUP *********/

use io;

/* Reads instance data. */
function input() {
	local usage = "Usage: localsolver TUP_localsolver.lsp " + "inFilename=inputFile [q1=C4] [q2=C5] [solFileName=outputFile] [lsTimeLimit=timeLimit]";
	if(inFileName == nil) throw usage;
	readInputFile();
	rounds = 2*nTeams-2;
	nUmpires = nTeams/2;
}

/* Declares the optimazation model. */
function model() {
	
	/* Descision variable x[u][r][v] is 1 umpire u is assigned in round r to venue v. */
	x[u in 1..nUmpires][r in 1..rounds][v in 1..nTeams] <- bool();

	/* Every match is officiated by excatly one umpire */

	for [r in 1..rounds][v in 1..nTeams]{
		constraint sum[u in 1..nUmpires](x[u]) == nUmpires;
	}
	/* Every umpire works in every round */
	for [u in 1..umpires]{
		constraint sum[r in 1..rounds](x[r]) == rounds;
	}

	/* 	Constraints:
		- Every umpire must visit the home venue of every team at least once
		- No umpire is at the same venue for more then q1 rounds
		- No umpire can see the same team in q2 rounds
	*/

	//Objective
	minimize totalDistanceTraveled;
	
}

/* Parametrizes the solver */ 
function param() {
	if(lsTimeLimit == nil) lsTimeLimit = 60;
	if(q1 == nil) q1=1;
	if(q2 == nil) q2=1;
}

/* Writes the solution to a file */
function output() {

}

/* Specific read methode for TUP instance files*/
function readInputFile() {
	local inFile = io.openRead(inFileName);
	local str = inFile.readString(); /* read the number of teams */
	str = str.substring(7,(str.length()-1)-7);
	nTeams = str.toInt();
	println("Het aantal teams is : " + nTeams); /* Optional: remove */
	while(true){
		str = inFile.readln();
		if (str.startsWith("dist=")){
			readDistanceMatrix(inFile);
		} else if (str.startsWith("opponents")) {
			readOpponentMatrix(inFile);
			break;
		}
	}
	println("Distance matrix:"); /* Optional: remove */
	for [value in D] println(value); /* Optional: remove */
	println("Opponents matrix"); /* Optional: remove */
	for[value in OPP] println(value); /* Optional: remove */
	inFile.close();
}
 
 /* Used to split a string and return a map */
function splitLine(line){
    local lineSplit = split(line," ");
    local lineFiltered = {};
    for[elem in lineSplit : elem != ""] {
		add(lineFiltered, elem);
    }
    return lineFiltered;
}

function readDistanceMatrix(inFile){
	for [i in 0..nTeams-1] {
		str = inFile.readln();
		str = str.substring(1, str.length()-2); /* Get rid of the brackets in the matrix */
		local distanceValues = splitLine(str);
		for [j in 0..distanceValues.count()-1]{
			D[i][j] = distanceValues[j].toInt();
		}
	}
}

function readOpponentMatrix(inFile){ 
	for [i in 0..(2*nTeams-2)-1] {
		str = inFile.readln();
		str = str.substring(1, str.length()-2); /* Get rid of the brackets in the matrix */
		local opponentsValue = splitLine(str);
		for [j in 0..opponentsValue.count()-1] {
			OPP[i][j] = opponentsValue[j].toInt();
		}
	}
}



