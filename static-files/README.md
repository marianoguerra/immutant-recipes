# static-files

An example immutant project to show how to serve static files.

## Usage

clone it and then deploy it with

	   lein immutant deploy

visit http://localhost:8080/static-files/s/index.html

you should see the content of the public/s/index.html file which is

	   hello from static resource 

visit other page like http://localhost:8080/static-files/s/wat.html you should
get the not found error

    file not found

visit the ping api url http://localhost:8080/static-files/api/ping and you should
get a JSON response like

    "pong"

## License

Copyright © 2012 marianoguerra

Distributed under the Eclipse Public License, the same as Clojure.
