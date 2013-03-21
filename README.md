# Archimedes, a Clojure Library For Tinkerpop Blueprints

Archimedes is a Clojure library for working with graphs that conform to the [Tinkerpop Blueprints](http://tinkerpop.com) interface.


## Project Goals

 * Provide an API that makes [Tinkerpop Blueprints](http://tinkerpop.com) really easy to use from Clojure
 * Be reasonably feature complete
 * Don't introduce any significant amount of performance overhead


## Community

[Archimedes has a mailing list](https://groups.google.com/forum/#!forum/clojure-titanium).
Feel free to join it and ask any questions you may have.


## Project Maturity

Archimedes is *very* young and incomplete. We put it on GitHub to gather feedback. For now, please consider using
a mature library such as [Neocons](http://clojureneo4j.info) instead.

As the project matures, we will update this section.



## Artifacts

Archimedes artifacts are [released to Clojars](https://clojars.org/zmaril/archimedes). If you are using Maven, add the following repository
definition to your `pom.xml`:

``` xml
<repository>
  <id>clojars.org</id>
  <url>http://clojars.org/repo</url>
</repository>
```

### The Most Recent Release

With Leiningen:

    [zmaril/archimedes "1.0.0-alpha1"]


With Maven:

    <dependency>
      <groupId>zmaril</groupId>
      <artifactId>archimedes</artifactId>
      <version>1.0.0-alpha1</version>
    </dependency>



## Documentation & Examples

Archimedes documentation guides are not ready yet.

### Code Examples

Our [test suite](test/archimedes) has many code examples.


### Mailing List

Don't hesitate to join our [mailing list](https://groups.google.com/forum/#!forum/clojure-titanium) and ask questions!


## Supported Clojure Versions

Archimedes is built from the ground up for Clojure 1.4 and up. The most recent stable release
is always recommended.


## Continuous Integration

TBD: add it to travis-ci.org.



## Development

Archimedes uses [Leiningen 2](https://github.com/technomancy/leiningen/blob/master/doc/TUTORIAL.md). Make sure you have it installed and then run tests against
supported Clojure versions using

    lein2 all test

Then create a branch and make your changes on it. Once you are done with your changes and all tests pass, submit a pull request
on Github.



## License

Copyright (C) 2013 Zack Maril

Licensed under the [Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html) (the same as Clojure).

[Titan](http://thinkaurelius.github.com/titan/) is licensed under the [Apache Public License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

