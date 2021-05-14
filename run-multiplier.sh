#!/usr/bin/env bash
dir = "/home/jxzhang/projects/multiplier-generator/benchmarks"
sbt "test:runMain ppadder.test --compressor-file ${dir}/mult/6output0 --prefix-adder-file ${dir}/ppa/11output0"
