# ring-router-example

example to show how to use ring-router on immutant

## Usage

git clone, then run "lein immutant deploy" and try it

	curl -X POST http://localhost:8080/ring-router-example/echo -d "42"
	{"error":"invalid content type"}

	curl -X POST http://localhost:8080/ring-router-example/echo -d "42" -H "Content-Type: application/json"
	42

	curl -X POST http://localhost:8080/ring-router-example/echo -d '{"name": "bob"}' -H "Content-Type: application/json"
	{"name":"bob"}

	curl -X GET http://localhost:8080/ring-router-example/ping
	"pong"

	curl -X GET http://localhost:8080/ring-router-example/hello/mariano
	"hello mariano!"

	curl -X GET http://localhost:8080/ring-router-example/hello/world
	"hello world!"

	curl -X GET http://localhost:8080/ring-router-example/wat
	{"reason":"not found","error":"not-found"}

## License

Copyright © 2012 marianoguerra

Distributed under the Eclipse Public License, the same as Clojure.

