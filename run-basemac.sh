#!/usr/bin/env bash
#sbt "test:runMain multiplier.test"
sbt "test:runMain basemac.test --input1-bit 25 --input2-bit 18"
