Play 22 Rest With Auth
=============

A sample reactive Play 2.2 REST application with Authentication

The Authorization is OAuth2 simple bearer token for app to app authorization.

A simulator view will provide means to:

* create a new bearer token (via basic auth with simulated consumer credentials)
* invalidate the bearer token (via basic auth with simulated consumer credentials)
* add a callback url to the system (via bearer auth with the generated bearer token)
* get the callback url (via bearer auth with the generated bearer token)
* trigger a call from the app to the simulated callback (via bearer auth with the generated bearer token)


About
--------------

Rest with a simple authentication

Also provides an example of consumer callback registration
 

This sample uses
--------------

* sbt   0.13.0        http://www.scala-sbt.org/0.13.0/docs/Community/ChangeSummary_0.13.0.html
* Scala 2.10.2  
* Playframework 2.2   http://www.playframework.com/documentation/2.2.0/Highlights22
* AngularJs 1.1       http://angularjs.org/


Misc
--------------

* Pretty good read on Play Actions and Async  https://groups.google.com/forum/#!topic/play-framework-dev/30MqnKDp0Fs


Auth
--------------

* The authentication is rfc6749 4.4 lient Credentials Grant 
* http://tools.ietf.org/html/rfc6749#section-4.4
* Twitter's implementation is the model for this example
* https://dev.twitter.com/docs/auth/application-only-auth

