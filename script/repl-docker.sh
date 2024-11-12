#!/bin/bash

if [ -z ${var+x} ]; then
  export PORT=5555;
fi

echo $PORT > .nrepl-port

docker build -t clojure-repl-img .

docker run -p $PORT:$PORT -it --rm clojure-repl-img

