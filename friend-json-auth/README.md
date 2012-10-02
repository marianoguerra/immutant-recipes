# friend-json-auth

an immutant project to show how to integrate friend for authentication/authorization
using a custom workflow to do authentication via a REST/JSON API

it also uses ring-router for routing.

## Usage

clone the project, deploy it with "lein immutant deploy"

visit http://localhost:8080/friend-json-auth/s/login.html

login with user "jane" password "user\_password" or "root" and "admin\_password"

login, logout, try invalid credentials, what you can visit the same urls as
the friend-acl example on this repo.

* visit http://localhost:8080/friend-acl/api/auth to see auth details
* visit http://localhost:8080/friend-acl/api/ping to see a service anyone can access
* visit http://localhost:8080/friend-acl/api/user-only-ping to see a service that only users with the "user" role can access (for example jane)
* visit http://localhost:8080/friend-acl/api/admin-only-ping to see a service that only users with the "admin" role can access (for example root)

## License

Copyright © 2012 marianoguerra

Distributed under the Eclipse Public License, the same as Clojure.
