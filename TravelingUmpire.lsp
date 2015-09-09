/******** TUP *********/

use io;

/* Reads instance data. */
function input() {
	local usage = "Usage: localsolver TUP_localsolver.lsp " + "inFilename=inputFile [q1=C4] [q2=C5] [solFileName=outputFile] [lsTimeLimit=timeLimit]";
	if(inFileName == nil) throw usage;
	readInputFile();
}

/* Declares the optimazation model. */
function model() {
	nUmpires = nTeams/2;
	
	/* Stellen de edges voor tussen de wedstrijden. */
	umpireDescision [k in 1..nUmpires] <- list(nUmpires*nUmpires);

	/* Elke wedstrijd moet een umpire hebben */

	/* Elke umpire moet werken in een ronde */

	/* Elke umpire moet elk team tenminste één maal thuis gezien hebben */

	/* Een umpire mag niet op dezelfde plaats zijn voor q1 ronden */

	/* Een umpire mag hetzelfde team niet zin in q2 ronden */

	/* In totaal zijn er 2n-2 rondes */
	for [rounds in 1..2*nTeams-2] {


	}

	//Objective
	minimize totalDistanceTraveld;
	
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
	/* inlezen van het aantal teams */
	local str = inFile.readString();
	str = str.substring(7,(str.length()-1)-7);
	nTeams = str.toInt();
	println("Het aantal teams is : " + nTeams);
	/* extra lijn lezen om naar distance matrix te komen*/
	local dump = inFile.readln();
	for [i in 0..nTeams-1] {
		str = inFile.readln();
		/* string van distance matrix zonder haakjes */
		str = str.substring(1, str.length()-2);
		local distanceValues = splitLine(str);
		for [j in 0..distanceValues.count()-1]{
			D[i][j] = distanceValues[j].toInt();
		}
	}
	for [i in 0..2]{
		dump = inFile.readln();
	} 
	for [i in 0..(2*nTeams-2)-1] {
		str = inFile.readln();
		/* string van opponents matrix zonder haakjes */
		str = str.substring(1, str.length()-2);
		local opponentsValue = splitLine(str);
		for [j in 0..opponentsValue.count()-1] {
			OPP[i][j] = opponentsValue[j].toInt();
		}
	}
	inFile.close();
}

function splitLine(line){
    local lineSplit = split(line," ");
    local lineFiltered = {};
    for[elem in lineSplit : elem != ""] {
		add(lineFiltered, elem);
    }
    return lineFiltered;
}