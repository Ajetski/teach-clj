FROM clojure:tools-deps
ENV PORT 5555

COPY . /usr/src/app
WORKDIR /usr/src/app

EXPOSE $PORT
CMD ["bash", "script/repl.sh"]
