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
for ((i=1; i< 12; i++))
do
echo "########################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestRace$i HD starts at " `date` >> ERROR.txt

./test.sh test_race$i TestRace$i HD $1 &                   #Execute the test
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmark.testcases.TestRace$i HD stops at " `date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt

done

for ((i=1; i<9; i++))
do

if [ $i -ne 5 ]
then
echo "######################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock$i HD starts at " `date` >> ERROR.txt

./test.sh test_$i TestDeadlock$i HD $1 &
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock$i HD stops at " `date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt
fi
done

echo "#####################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock1a HD starts at " `date` >> ERROR.txt

./test.sh test_1a TestDeadlock1a HD $1 &
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock1a HD stops at " `date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt

echo "#####################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock1b HD starts at " `date`  >> ERROR.txt

./test.sh test_1a TestDeadlock1b HD $1 &
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock1b HD stops at " `date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt

echo "###################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock2a HD starts at "`date` >> ERROR.txt

./test.sh test_2a TestDeadlock2a HD $1 &
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock2a HD stops at "`date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt

echo "##################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock4b HD starts at"`date`  >> ERROR.txt

./test.sh test_4b TestDeadlock4b HD $1 &
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER
echo "benchmarks.testcases.TestDeadlock4b HD stops at " `date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt

############################################################################################
for ((i=1; i<12; i++))
do
echo "########################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestRace$i PR starts at " `date` >> ERROR.txt

./test.sh test_race$i TestRace$i PR $1 &                   #Execute the test
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmark.testcases.TestRace$i PR stops at " `date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt
done

for ((i=1; i<9; i++))
do

if [ $i -ne 5 ]
then
echo "######################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock$i PR starts at " `date` >> ERROR.txt

./test.sh test_$i TestDeadlock$i PR $1 &
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock$i PR stops at " `date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt
fi
done

echo "#####################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock1a PR starts at " `date` >> ERROR.txt

./test.sh test_1a TestDeadlock1a PR $1 &
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock1a PR stops at " `date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt

echo "#####################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock1b PR starts at " `date`  >> ERROR.txt

./test.sh test_1a TestDeadlock1b PR $1 &
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock1b PR stops at " `date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt

echo "###################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock2a PR starts at "`date` >> ERROR.txt

./test.sh test_2a TestDeadlock2a PR $1 &
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock2a PR stops at "`date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt

echo "##################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock4b PR starts at"`date`  >> ERROR.txt

./test.sh test_4b TestDeadlock4b PR $1 &
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER
echo "benchmarks.testcases.TestDeadlock4b PR stops at " `date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt

####################################################################################################
for ((i=1; i<12; i++))
do
echo "########################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestRace$i RT starts at " `date` >> ERROR.txt

./test.sh test_race$i TestRace$i RT $1 &                   #Execute the test
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmark.testcases.TestRace$i RT stops at " `date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt
done

for ((i=1; i<9; i++))
do

if [ $i -ne 5 ]
then
echo "######################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock$i RT starts at " `date` >> ERROR.txt

./test.sh test_$i TestDeadlock$i RT $1 &
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock$i RT stops at " `date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt
fi
done

echo "#####################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock1a RT starts at " `date` >> ERROR.txt

./test.sh test_1a TestDeadlock1a RT $1 &
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock1a RT stops at " `date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt

echo "#####################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock1b RT starts at " `date`  >> ERROR.txt

./test.sh test_1a TestDeadlock1b RT $1 &
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock1b RT stops at " `date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt

echo "###################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock2a RT starts at "`date` >> ERROR.txt

./test.sh test_2a TestDeadlock2a RT $1 &
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock2a RT stops at "`date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt

echo "##################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock4b RT starts at"`date`  >> ERROR.txt

./test.sh test_4b TestDeadlock4b RT $1 &
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER
echo "benchmarks.testcases.TestDeadlock4b RT stops at " `date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt

#############################################################################################
for ((i=1; i<12; i++))
do
echo "########################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestRace$i PL starts at " `date` >> ERROR.txt

./test.sh test_race$i TestRace$i PL $1 &                   #Execute the test
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmark.testcases.TestRace$i PL stops at " `date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt
done

for ((i=1; i<9; i++))
do

if [ $i -ne 5 ]
then
echo "######################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock$i PL starts at " `date` >> ERROR.txt

./test.sh test_$i TestDeadlock$i PL $1 &
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock$i PL stops at " `date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt
fi
done

echo "#####################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock1a PL starts at " `date` >> ERROR.txt

./test.sh test_1a TestDeadlock1a PL $1 &
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock1a PL stops at " `date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt

echo "#####################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock1b PL starts at " `date`  >> ERROR.txt

./test.sh test_1a TestDeadlock1b PL $1 &
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock1b PL stops at " `date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt

echo "###################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock2a PL starts at "`date` >> ERROR.txt

./test.sh test_2a TestDeadlock2a PL $1 &
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER

echo "benchmarks.testcases.TestDeadlock2a PL stops at "`date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt

echo "##################################################################" >> ERROR.txt
echo "benchmarks.testcases.TestDeadlock4b PL starts at"`date`  >> ERROR.txt

./test.sh test_4b TestDeadlock4b PL $1 &
pid=$!
(sleep $2; kill $pid) &
KILLER=$!
wait $pid
kill -HUP $KILLER
echo "benchmarks.testcases.TestDeadlock4b PL stops at " `date` >> ERROR.txt
echo -n "Path explored: " >> ERROR.txt
cat done.txt | wc -l >> ERROR.txt
