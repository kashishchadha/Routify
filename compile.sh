#!/bin/bash

echo "Compiling Interactive Network Routing Simulator..."
echo

mkdir -p out

javac -d out -encoding UTF-8 src/models/*.java src/algorithms/*.java src/simulation/*.java src/gui/*.java src/App.java

if [ $? -eq 0 ]; then
    echo
    echo "Compilation successful!"
    echo
    echo "To run the application, use:"
    echo "  java -cp out App"
else
    echo
    echo "Compilation failed!"
fi

