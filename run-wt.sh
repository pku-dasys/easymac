#!/usr/bin/env bash
#sbt "test:runMain multiplier.test"
folder="/home/jxzhang/projects/multiplier-generator/benchmarks/wt"
sbt "test:runMain wallace.test --wallace-file ${folder}/8output0"
