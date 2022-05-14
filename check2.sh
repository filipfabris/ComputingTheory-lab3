#! /bin/bash


for dire in ./test/*
do

  difs="$(java -classpath 'C:\Users\filip\IdeaProjects\ComputingTheory-lab3\out\production\ComputingTheory-lab3'  Program < $dire/primjer.in | diff $dire/primjer.out -)"

	if [ "$difs" = "" ];
	then
		echo "$dire : [OK]"
	else
		echo "$dire : "
    echo "$difs"
	fi

done



