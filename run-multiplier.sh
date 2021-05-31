#!/usr/bin/env bash
folder="/home/jxzhang/projects/multiplier-generator/benchmarks"
resfolder="/home/jxzhang/projects/multiplier-generator/RTL/mult"

sbt "test:runMain wtmultiplier.test --wallace-file ${folder}/wt/16output0 --prefix-adder-file ${folder}/ppa/31output0"
mv ${resfolder}/WTMultiplier.v WTMultiplier0.v

sbt "test:runMain wtmultiplier.test --wallace-file ${folder}/wt/16output0 --prefix-adder-file ${folder}/ppa/31output1"
mv ${resfolder}/WTMultiplier.v WTMultiplier1.v

sbt "test:runMain wtmultiplier.test --wallace-file ${folder}/wt/16output0 --prefix-adder-file ${folder}/ppa/31output2"
mv ${resfolder}/WTMultiplier.v WTMultiplier2.v

sbt "test:runMain wtmultiplier.test --wallace-file ${folder}/wt/16output0 --prefix-adder-file ${folder}/ppa/31output3"
mv ${resfolder}/WTMultiplier.v WTMultiplier3.v

sbt "test:runMain wtmultiplier.test --wallace-file ${folder}/wt/16output0 --prefix-adder-file ${folder}/ppa/31output4"
mv ${resfolder}/WTMultiplier.v WTMultiplier4.v

sbt "test:runMain wtmultiplier.test --wallace-file ${folder}/wt/16output0 --prefix-adder-file ${folder}/ppa/31output5"
mv ${resfolder}/WTMultiplier.v WTMultiplier5.v

sbt "test:runMain wtmultiplier.test --wallace-file ${folder}/wt/16output0 --prefix-adder-file ${folder}/ppa/31output6"
mv ${resfolder}/WTMultiplier.v WTMultiplier6.v

sbt "test:runMain wtmultiplier.test --wallace-file ${folder}/wt/16output0 --prefix-adder-file ${folder}/ppa/31output7"
mv ${resfolder}/WTMultiplier.v WTMultiplier7.v

sbt "test:runMain wtmultiplier.test --wallace-file ${folder}/wt/16output0 --prefix-adder-file ${folder}/ppa/31output8"
mv ${resfolder}/WTMultiplier.v WTMultiplier8.v

sbt "test:runMain wtmultiplier.test --wallace-file ${folder}/wt/16output0 --prefix-adder-file ${folder}/ppa/31output9"
mv ${resfolder}/WTMultiplier.v WTMultiplier9.v
