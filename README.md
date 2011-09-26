This is a basic mixture model (MM) simulator. Generally it receives a mixture model as input, then it generates samples from the MM and tries to estimate the parameteres of the given MM. During the simulation in every snapshotth iteration it takes a picture about the current state of the simulation. Finally it stitches together the pictures to a move.

This is a demo implementation, it is not optimized and has a lot of prerequirements like having bash, gnuplot, convert commands installed on the target machine! 

Getting Started
===============

* __download__: Download this package from github.com

* __build__: Type ant in the source directory

* __running__: In the bin directory type java -jar gmmtest.jar


