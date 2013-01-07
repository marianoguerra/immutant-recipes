# load-stuff

example to show how to load files located in the resources folder and from
custom configuration added to the deployment descriptor

## Usage

git clone, then run "lein immutant deploy" and try it

	$ curl http://localhost:8080/load-stuff/immutant-config
	  {:root "/home/mariano/src/immutant-recipes/load-stuff", :my-version "1.0"}
	$ curl http://localhost:8080/load-stuff/resource-file
	 hello world!
	$ curl http://localhost:8080/load-stuff/leiningen-config
	  ... lot of things here ...

## License

Copyright © 2013 marianoguerra

Distributed under the Eclipse Public License, the same as Clojure.
