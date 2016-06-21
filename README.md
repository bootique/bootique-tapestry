[![Build Status](https://travis-ci.org/nhl/bootique-tapestry.svg)](https://travis-ci.org/nhl/bootique-tapestry)

# bootique-tapestry
Provides [Apache Tapestry](http://tapestry.apache.org/) integration with [Bootique](http://bootique.io).

## Quick configuration hints:

Add the module to your Bootique app:

```xml
<dependency>
	<groupId>com.nhl.bootique.tapestry</groupId>
	<artifactId>bootique-tapestry</artifactId>
</dependency>
```

Configure Tapestry module in your app ```.yml``` (or via any other Bootique-compatible mechanism). E.g.:
```yml
tapestry:
  name: myapp          # The name of the T5 app module. Default is "tapestry".
  appPackage: com.foo  # default base package for the Tapestry app
```

Tapestry app can use its own injection and modules. Additionally services defined in Bootique are available via Tapestry injection.
