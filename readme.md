# JavaFX Travelling Salesman Genetic Solver

Implementation of a solver of the Travelling Salesman problem, using genetic algorithm, with GUI, created with Java and JavaFX.

Changelog:

* 12.12.2021
  * Initialized the project, with JavaFX and Maven
  * Created the CityNetworkSingleton, containing the data pertaining to cities themselves, and SalesmanSolution, containing a solution gene and calculating the travel distance.
  * Created javadoc comments describing the classes' inner workings.
  * Produced a single-threaded and multi-threaded problem solver, and compared their efficiency. Results are weird - ramping up the number of threads used is not always good.
  * Added a constraint that while generating a new population every member must have different travel cost (and if there's less than 50 such members, then the best one is duplicated). Lost efficiency, ensured gene variability. Introduced loss.