# static-files

An example immutant project to show how to serve static files.

## Usage

clone it and then deploy it with

	   lein immutant deploy

visit http://localhost:8080/static-files/index.html

you should see the content of the resources/s/index.html file, it will
also make an ajax request to /api/ping which should return "pong" and be
displayed in the page

visit other page like http://localhost:8080/static-files/wat.html you should
get the not found error

visit the ping api url http://localhost:8080/static-files/api/ping and you should
get a response like

    pong

## License

Copyright © 2012 marianoguerra

Distributed under the Eclipse Public License, the same as Clojure.
