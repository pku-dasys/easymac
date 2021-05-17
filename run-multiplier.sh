#!/usr/bin/env bash
folder="/home/jxzhang/projects/multiplier-generator/benchmarks"
sbt "test:runMain wtmultiplier.test --wallace-file ${folder}/wt/4output0 --prefix-adder-file ${folder}/ppa/7output0"
