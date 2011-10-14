#!/bin/bash

if [ $# -lt 1 ]
then 
echo "Start the testing driver!"
echo "Usage: driver.sh (D|N) timeout"
echo "D: debug mode"
echo "N: not debug mode"
echo "timeout: time in seconds (after which the testcase will be killed if not finished)"
exit 1
fi

ant #Compile the all the java files and test cases

###################################################################################################
for ((i=1; i<12; i++))
do
echo "########################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestRace$i starts at " `date` >> ERROR.txt

./test.sh test_race$i TestRace$i HD &                   #Execute the test
pid=$!
(sleep $1; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmark.testcases.TestRace$i stops at " `date` >> ERROR.txt
done

for ((i=1; i<9; i++))
do

if [ $i -ne 5 ]
then
echo "######################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock$i starts at " `date` >> ERROR.txt

./test.sh test_$i TestDeadlock$i HD &
pid=$!
(sleep $1; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock$i stops at " `date` >> ERROR.txt
fi
done

echo "#####################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock1a starts at " `date` >> ERROR.txt

./test.sh test_1a TestDeadlock1a HD &
pid=$!
(sleep $1; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock1a stops at " `date` >> ERROR.txt

echo "#####################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock1b starts at " `date`  >> ERROR.txt

./test.sh test_1a TestDeadlock1b HD &
pid=$!
(sleep $1; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock1b stops at " `date` >> ERROR.txt

echo "###################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock2a starts at "`date` >> ERROR.txt

./test.sh test_2a TestDeadlock2a HD &
pid=$!
(sleep $1; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock2a stops at "`date` >> ERROR.txt

echo "##################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock4b starts at"`date`  >> ERROR.txt

./test.sh test_4b TestDeadlock4b HD &
pid=$!
(sleep $1; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER
echo "benchmarks.testcases.TestDeadlock4b stops at " `date` >> ERROR.txt


############################################################################################
for ((i=1; i<12; i++))
do
echo "########################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestRace$i starts at " `date` >> ERROR.txt

./test.sh test_race$i TestRace$i PR &                   #Execute the test
pid=$!
(sleep $1; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmark.testcases.TestRace$i stops at " `date` >> ERROR.txt
done

for ((i=1; i<9; i++))
do

if [ $i -ne 5 ]
then
echo "######################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock$i starts at " `date` >> ERROR.txt

./test.sh test_$i TestDeadlock$i PR &
pid=$!
(sleep $1; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock$i stops at " `date` >> ERROR.txt
fi
done

echo "#####################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock1a starts at " `date` >> ERROR.txt

./test.sh test_1a TestDeadlock1a PR &
pid=$!
(sleep $1; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock1a stops at " `date` >> ERROR.txt

echo "#####################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock1b starts at " `date`  >> ERROR.txt

./test.sh test_1a TestDeadlock1b PR &
pid=$!
(sleep $1; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock1b stops at " `date` >> ERROR.txt

echo "###################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock2a starts at "`date` >> ERROR.txt

./test.sh test_2a TestDeadlock2a PR &
pid=$!
(sleep $1; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock2a stops at "`date` >> ERROR.txt

echo "##################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock4b starts at"`date`  >> ERROR.txt

./test.sh test_4b TestDeadlock4b PR &
pid=$!
(sleep $1; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER
echo "benchmarks.testcases.TestDeadlock4b stops at " `date` >> ERROR.txt

####################################################################################################
for ((i=1; i<12; i++))
do
echo "########################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestRace$i starts at " `date` >> ERROR.txt

./test.sh test_race$i TestRace$i RT &                   #Execute the test
pid=$!
(sleep $1; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmark.testcases.TestRace$i stops at " `date` >> ERROR.txt
done

for ((i=1; i<9; i++))
do

if [ $i -ne 5 ]
then
echo "######################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock$i starts at " `date` >> ERROR.txt

./test.sh test_$i TestDeadlock$i RT &
pid=$!
(sleep $1; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock$i stops at " `date` >> ERROR.txt
fi
done

echo "#####################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock1a starts at " `date` >> ERROR.txt

./test.sh test_1a TestDeadlock1a RT &
pid=$!
(sleep $1; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock1a stops at " `date` >> ERROR.txt

echo "#####################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock1b starts at " `date`  >> ERROR.txt

./test.sh test_1a TestDeadlock1b RT &
pid=$!
(sleep $1; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock1b stops at " `date` >> ERROR.txt

echo "###################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock2a starts at "`date` >> ERROR.txt

./test.sh test_2a TestDeadlock2a RT &
pid=$!
(sleep $1; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock2a stops at "`date` >> ERROR.txt

echo "##################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock4b starts at"`date`  >> ERROR.txt

./test.sh test_4b TestDeadlock4b RT &
pid=$!
(sleep $1; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER
echo "benchmarks.testcases.TestDeadlock4b stops at " `date` >> ERROR.txt
