#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
rm -rf out
javac --release 17 -d out $(find src -name "*.java")
java -cp out com.group12.ripperdoc.Main "$@"
