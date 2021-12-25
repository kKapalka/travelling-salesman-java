# JavaFX Travelling Salesman Genetic Solver

Implementation of a solver of the Travelling Salesman problem, using genetic algorithm, with GUI, created with Java and JavaFX.

Changelog:

* 25.12.2021
  * Created delegate classes, which will contain all logic pertaining to specific pages of the application
  * Extracted all 'Settings' page logic into SettingsControllerDelegate
  * Moved the city name creation logic inside HelperMethods
  * Removed every 'hello' from the application
  * Written comments about SalesmanSolutionPoolMultiThreaded and simplified the SalesmanSolutionCallback interface

* 22.12.2021
  * Fixed a minor bug where the number of threads could be set if user was using single-threaded solver
  * Produced an .exe file, and included it with the repository
  * Modified the way travel costs are displayed - instead of pure integers, they now have a single decimal point

* 21.12.2021
  * Initial data on city travel costs are automatically loaded onto 'Cities' page
  * Upon resizing of the city grid, the data on 'Cities' page gets updated
  * User can enable/disable symmetry constraint, forcing the travel cost from city A to B to be the same as cost of travel from city B to A. This button will not generate new travel costs by itself.
  * User can use 'Generate cities' button to calculate new travel costs
  * Cities now have names, from A to Z and from AA to ZZ. Upon dragging the grid containing travel cost of cities, the names of visible cities will change accordingly
  * Data regarding minimum and average travel costs are now transmitted onto the graph
  * Added internal clock, and graph refresh rate variables, to cut on the amount of data transmitted onto the graph
  * Added forced clear after 30 graph elements appear
  * Created results page, where all specimens of the final generation are listed, and upon inquiry, detailed route is displayed on the side
  * Now tab selection is disabled while calculations are going on
  * Details page includes the travel from last to first city

* 20.12.2021
  * User can now set his own values for:
    * Number of threads used for multithreaded solver (between 2 and 10)
    * Number of specimens per generation (5 - 999)
    * Number of cities (5 - 999)
    * Joining point (1 - number of cities)
  * On manual change of number of cities, the new city grid is automatically generated
  * Minor bugfixes 
  * Cleanup of numeric input callbacks
  * Modification of number of cities can invalidate the position of a joining point
  * Upon starting calculations, all inputs are disabled. Upon stopping, all inputs are enabled


* 14.12.2021
  * Solver can now be started and stopped at will
  * Transferred the logic for displaying generation data inside the controller itself, instead of inside solvers. This way data can be used for the purpose of display.
  * Upon finishing the calculations, travel path costs of the top 50 unique specimen of last generation are displayed in console. If needed, their whole inner data can me access, including the gene and the calculated travel path.
  * Did minor readability improvements.

* 13.12.2021
  * Moved the data about joining point to CityNetworkSingleton since it is a constant
  * Fixed import statements for readability
  * Moved firing of the solution calculation to onButtonClick event
  * Rewrote single-threaded solver so that it can be stopped at will
  * Rewrote multi-threaded solver so that it can handle a variable amount of threads 
  * Found and resolved an edge case where the calculations wouldn't stop if numbers of threads used for multi-threaded solver equals 1
  * Started to work on the GUI. It will contain 3 tabs: Cities, where user can look up the city connection chart; Progress graph, where user can watch how the result has been changing over time; Results, which will contain top 50 solutions from the last generation upon cancelling. To start calculating, user will press 'Start calculations', and to stop, he will press 'Stop calculations'. Upon pressing on a specific gene, a modal window will show up with the overall path and the travel cost.


* 12.12.2021
  * Initialized the project, with JavaFX and Maven
  * Created the CityNetworkSingleton, containing the data pertaining to cities themselves, and SalesmanSolution, containing a solution gene and calculating the travel distance.
  * Created javadoc comments describing the classes' inner workings.
  * Produced a single-threaded and multi-threaded problem solver, and compared their efficiency. Results are weird - ramping up the number of threads used is not always good.
  * Added a constraint that while generating a new population every member must have different travel cost (and if there's less than 50 such members, then the best one is duplicated). Lost efficiency, ensured gene variability. Introduced loss.
  * created HelperMethods, and fixed the mutation rate for single-threaded solver (2 -> 10 for 100 total solutions per generation)