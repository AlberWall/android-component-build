#!/usr/bin/env bash

pwd

echo "start build libcommon"
../gradlew -p ../libCommon/ buildJar

echo "start build libA"
../gradlew buildJar