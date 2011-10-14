#Heuristic is HD or PR or RT
#!/bin/bash

ant -f run.xml $1    #compile all the source files and instrument the target file

if [ "$2" == "HD" ]
then 
	while :
	do
		java -cp src/benchmarks/tmpclasses/:classes javato.activetesting.por.Heuristic HD #find next path to explore
		path=`cat tmppath.txt`; #get the next path to explore
		if [ "$path" == "DONE" ]
			then
				exit 1;
			fi
		if [ "$3" == "D" ]
		then
		java -Djavato.isDebug=true -cp src/benchmarks/tmpclasses/:classes benchmarks.JGFMolDynBenchSizeA 3  $path #execute the program
		else 
		java -Djavato.isDebug=false -cp src/benchmarks/tmpclasses/:classes benchmarks.JGFMolDynBenchSizeA 3  $path #execute the program
		fi
	done
fi
if [ "$2" == "PR" ]
then 
	while :
	do
		java -cp src/benchmarks/tmpclasses/:classes javato.activetesting.por.Heuristic PR #find next path to explore
		path=`cat tmppath.txt`; #get the next path to explore
		if [ "$path" == "DONE" ]
			then
				exit 1;
			fi
		
		if [ "$3" == "D" ]
		then
		java -Djavato.isDebug=true -cp src/benchmarks/tmpclasses/:classes benchmarks.JGFMolDynBenchSizeA 3  $path #execute the program
		else 
		java -Djavato.isDebug=false -cp src/benchmarks/tmpclasses/:classes benchmarks.JGFMolDynBenchSizeA 3  $path #execute the program
		fi
	done
fi
if [ "$2" == "RT" ]
then 
	while :
	do
		java -cp src/benchmarks/tmpclasses/:classes javato.activetesting.por.Heuristic RT #find next path to explore
		path=`cat tmppath.txt`; #get the next path to explore
		if [ "$path" == "DONE" ]
			then
				exit 1;
			fi
		
		if [ "$3" == "D" ]
		then
		java -Djavato.isDebug=true -cp src/benchmarks/tmpclasses/:classes benchmarks.JGFMolDynBenchSizeA 3  $path #execute the program
		else 
		java -Djavato.isDebug=false -cp src/benchmarks/tmpclasses/:classes benchmarks.JGFMolDynBenchSizeA 3  $path #execute the program
		fi
	done
fi
if [ "$2" == "PL" ]
then 
	while :
	do
		java -cp src/benchmarks/tmpclasses/:classes javato.activetesting.por.Heuristic PL #find next path to explore
		path=`cat tmppath.txt`; #get the next path to explore
		if [ "$path" == "DONE" ]
			then
				exit 1;
			fi
		
		if [ "$3" == "D" ]
		then
		java -Djavato.isDebug=true -cp src/benchmarks/tmpclasses/:classes benchmarks.JGFMolDynBenchSizeA 3  $path #execute the program
		else 
		java -Djavato.isDebug=false -cp src/benchmarks/tmpclasses/:classes benchmarks.JGFMolDynBenchSizeA 3  $path #execute the program
		fi
	done
fi
