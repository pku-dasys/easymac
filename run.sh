#!/usr/bin/env bash
args=$@
sbt -v "test:runMain adder.Launcher $args"
