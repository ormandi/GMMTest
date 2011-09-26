This is a basic mixture model (MM) simulator. Generally it receives a mixture model as input, then it generates samples from the MM and tries to estimate the parameteres of the given MM. During the simulation in every snapshotth iteration it takes a picture about the current state of the simulation. Finally it stitches together the pictures to a move.

This is a demo implementation, it is not optimized and has a lot of prerequirements like having bash, gnuplot, convert commands installed on the target machine! 

Getting Started
===============

* __download__: Download this package by typing: git clone git://github.com/RobertOrmandi/GMMTest.git 

* __build__: Type ant in the source directory i.e.: cd GMMTest; ant

* __running__: Go to the bin directory and starta simulation by typing: cd bin; java -jar gmmtest.jar 0.5,0.2,0.3 -2.0,1.0,2.0 0.2,1.0,0.5 simulation.gif ./gmmSimulation/ 1000 20000 1000 123456789 This will perform a simulation where you have 3 componenets with the following weights, means and variances repectively: [0.5,0.2,0.3] [-2.0,1.0,2.0] [0.2,1.0,0.5], the output will be gnerated in the simulation.gif, the simulation will use the ./gmmSimulation/ directory which must not be existed. It will generate a snapshot in every 1000th iteration. The simulation will generate 20000 samples. The batch size is 1000 and the seed is 123456789. 


