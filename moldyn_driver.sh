#!/bin/bash

if [ $# -lt 1 ]
then 
echo "Start the testing driver!"
echo "Usage: moldyn_driver.sh (D|N) timeout"
echo "D: Debug mode"
echo "N: Not debug mode"
echo "timeout: time in seconds (after which the testcase will be killed if not finished)"
exit 1
fi

ant #Compile the all the java files and test cases

echo "##################################################################" >> ERROR.txt
echo "benchmarks.JFGMoldynBenchSizeA HD starts at "`date`  >> ERROR.txt

./test_moldyn.sh test_moldyn HD $1 &
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.JFGMoldynBenchSizeA HD stops at "`date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt

echo "##################################################################" >> ERROR.txt
echo "benchmarks.JFGMoldynBenchSizeA PR starts at "`date`  >> ERROR.txt

./test_moldyn.sh test_moldyn PR $1 &
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.JFGMoldynBenchSizeA PR stops at "`date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt

echo "##################################################################" >> ERROR.txt
echo "benchmarks.JFGMoldynBenchSizeA RT starts at "`date`  >> ERROR.txt

./test_moldyn.sh test_moldyn RT $1 &
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.JFGMoldynBenchSizeA RT stops at "`date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt

echo "##################################################################" >> ERROR.txt
echo "benchmarks.JFGMoldynBenchSizeA PL starts at "`date`  >> ERROR.txt

./test_moldyn.sh test_moldyn PL $1 &
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.JFGMoldynBenchSizeA PL stops at "`date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt
