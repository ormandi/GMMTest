Estimating Mixture Models
=========================

This Java project provides an evaluation interface for estimating Mixture Models (MM) in an online manner.
The estimation is done using a version of the Expectation Maximization algorithm. An example implementation is provided for Gaussian Mixture Models, however, the
framework allows plugging in other distributions as well.

It receives a mixture model as input, then it generates samples from the MM
and tries to estimate the parameters of the given MM. During the
simulation in every ``1000``<sup>th</sup> iteration (by default) it takes a snapshot of the current state of the simulation.
Finally, it combines these images into an animated ``.gif`` file. This is a demo implementation, it is not optimized.

Requirements
============
* apache-ant, Java SDK
* bash
* gnuplot
* convert (imagemagick)

Getting Started
===============

* __downloading__: Clone or download.

* __building__: Use Apache Ant inside the ``GMMTest`` directory to compile the sources.

``
cd GMMTest
ant
``

* __running__: Switch to the bin directory and start a simulation by typing:

``
cd bin
java -jar gmmtest.jar 0.5,0.2,0.3 -2.0,1.0,2.0 0.2,1.0,0.5 simulation.gif ./gmmSimulation/ 1000 20000 densityEstimator.SmoothGMM 3 batchSize=1000,alpha=0.2 123456789
``

This will perform a simulation where you have 3 Gaussian components with
the following weights, means and variances, repectively: ``[0.5, 0.2, 0.3]``
``[-2.0,1.0,2.0]`` ``[0.2,1.0,0.5]``. The output will be
generated in ``simulation.gif``, the simulation will use the
``./gmmSimulation/`` directory (for intermediate operations), which must not have existed
beforehand. It will generate a snapshot in every ``1000``<sup>th</sup> iteration. The
simulation will generate ``20000`` samples. The simulation will use the 
``densityEstimator.SmoothGMM`` for estimating the mixture model using ``3`` components.
The parameter setting of the estimator classi is given in the next parameter (``batchSize=1000,alpha=0.2``). 
The format of the parameter setting argument is slightly intuitive it consists of comma separeted
parameter-value pairs in form ``parameterName=parameterValue``.
In the above presented sample the estimater class has two parameters. 
The first one is the batch size which has a value ``1000``.
The second one is the smoothing parameter named alpha which is set to ``0.2``.
The pseudorandom number generator is initialized with seed ``123456789``. This final parameter is optional.
