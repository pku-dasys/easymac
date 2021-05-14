#!/usr/bin/env bash
#sbt "test:runMain multiplier.test"
#folder = "/home/jxzhang/projects/adder-generator/benchmarks"

#files = $(ls $folder)

#for f in $files
#do 
#	sbt "test:runMain ppadder.test --prefix-adder-file /home/jxzhang/projects/adder-generator/benchmarks/${f}"
#done

sbt "test:runMain ppadder.test --prefix-adder-file /home/jxzhang/projects/adder-generator/benchmarks/32output0"
