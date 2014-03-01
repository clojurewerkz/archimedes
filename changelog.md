## Changes in 1.0.0-alpha5 

* Bug fix for `transact!` and it's feature sniffing. Whenever
  `*graph*` gets rebound now, archimedes sniffs graph to see whether
  it should use a simple or threaded `transact!`. I suspect that using
  `with-graph` could break the feature sniffing (transact!  could be
  set for one set of features while the provided graph could have a
  totally different set of features). Using the apporiate function by
  hand is one solution to this. 
* `*element-id-key*` and `*edge-label-key*` have been introduced to
  allow developers to change how `to-map` represents vertices and
  edges. These values can be changed via `set-element-id-key!` and
  `set-edge-label-key!`.

## Changes in 1.0.0-alpha4

* Bug fix for using Ogre.

## Changes in 1.0.0-alpha3

* Fixed bugs in `get-vertex`, `set-property`, and `get-all-edges`.
* Renamed `count-edges` to `count` and `delete!` to `remove!`.
* Archimedes is now a Clojurewerkz project.
* Depends on Ogre 2.3.0.1 now.

## Changes in 1.0.0-alpha2

Added in `get-graph` which returns the graph held inside of the
var. Update dependancy on Blueprints to `2.3.0` and Ogre to
`2.3.0.0`.

## version < 1.0.0

Mostly working towards first release.
