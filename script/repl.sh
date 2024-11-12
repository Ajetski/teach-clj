#!/bin/bash

if [ -z ${PORT+""} ]; then
  export PORT=5555;
fi

clj -M:repl --port $PORT
