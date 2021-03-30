<!--
       Licensed to ObjectStyle LLC under one
     or more contributor license agreements.  See the NOTICE file
     distributed with this work for additional information
     regarding copyright ownership.  The ObjectStyle LLC licenses
     this file to you under the Apache License, Version 2.0 (the
     “License”); you may not use this file except in compliance
     with the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing,
     software distributed under the License is distributed on an
     “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
     KIND, either express or implied.  See the License for the
     specific language governing permissions and limitations
     under the License.
  -->

[![build test deploy 1.x](https://github.com/bootique/bootique-tapestry/actions/workflows/maven-1x.yml/badge.svg)](https://github.com/bootique/bootique-tapestry/actions/workflows/maven-1x.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.bootique.tapestry/bootique-tapestry.svg?colorB=brightgreen)](https://search.maven.org/artifact/io.bootique.tapestry/bootique-tapestry/)

# bootique-tapestry
Provides [Apache Tapestry](http://tapestry.apache.org/) integration with [Bootique](http://bootique.io).
See usage example [bootique-tapestry-demo](https://github.com/bootique-examples/bootique-tapestry-demo).

## Quick configuration:

Add the module to your Bootique app:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.bootique.bom</groupId>
            <artifactId>bootique-bom</artifactId>
            <version>1.0</version>
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
