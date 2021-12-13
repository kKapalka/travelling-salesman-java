# JavaFX Travelling Salesman Genetic Solver

Implementation of a solver of the Travelling Salesman problem, using genetic algorithm, with GUI, created with Java and JavaFX.

Changelog:

* 13.12.2021
  * Moved the data about joining point to CityNetworkSingleton since it is a constant
  * Fixed import statements for readability
  * Moved firing of the solution calculation to onButtonClick event
  * Rewrote single-threaded solver so that it can be stopped at will
  * Rewrote multi-threaded solver so that it can handle a variable amount of threads 
  * Found and resolved an edge case where the calculations wouldn't stop if numbers of threads used for multi-threaded solver equals 1
  * Started to work on the GUI. It will contain 3 tabs: Cities, where user can look up the city connection chart; Progress graph, where user can watch how the result has been changing over time; Results, which will contain top 50 solutions from the last generation upon cancelling. To start calculating, user will press 'Start calculations', and to stop, he will press 'Stop calculations'. Upon pressing on a specific gene, a modal window will show up with the overall path and the travel costl.


* 12.12.2021
  * Initialized the project, with JavaFX and Maven
  * Created the CityNetworkSingleton, containing the data pertaining to cities themselves, and SalesmanSolution, containing a solution gene and calculating the travel distance.
  * Created javadoc comments describing the classes' inner workings.
  * Produced a single-threaded and multi-threaded problem solver, and compared their efficiency. Results are weird - ramping up the number of threads used is not always good.
  * Added a constraint that while generating a new population every member must have different travel cost (and if there's less than 50 such members, then the best one is duplicated). Lost efficiency, ensured gene variability. Introduced loss.
  * created HelperMethods, and fixed the mutation rate for single-threaded solver (2 -> 10 for 100 total solutions per generation)