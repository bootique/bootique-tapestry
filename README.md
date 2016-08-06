[![Build Status](https://travis-ci.org/bootique/bootique-tapestry.svg)](https://travis-ci.org/bootique/bootique-tapestry)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.bootique.tapestry/bootique-tapestry/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.bootique.tapestry/bootique-tapestry/)

# bootique-tapestry
Provides [Apache Tapestry](http://tapestry.apache.org/) integration with [Bootique](http://bootique.io).

## Quick configuration:

Add the module to your Bootique app:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.bootique.bom</groupId>
            <artifactId>bootique-bom</artifactId>
            <version>0.19</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
...
<dependency>
    <groupId>io.bootique.tapestry</groupId>
    <artifactId>bootique-tapestry</artifactId>
</dependency>
```

Configure Tapestry module in your app ```.yml``` (or via any other Bootique-compatible mechanism). E.g.:
```yml
tapestry:
  name: myapp          # The name of the T5 app module. Default is "tapestry".
  appPackage: com.foo  # default base package for the Tapestry app
```

Tapestry app can use its own injection and modules. Additionally services defined in Bootique are available via Tapestry 
injection.
