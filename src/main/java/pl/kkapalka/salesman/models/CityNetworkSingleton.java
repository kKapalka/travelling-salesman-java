package pl.kkapalka.salesman.models;

import java.util.Random;

public final class CityNetworkSingleton {
    private static CityNetworkSingleton instance;
    
    private Integer CITY_GRID_SIZE = 100;
    private Integer JOINING_POINT = 50;
    private Integer TOTAL_SOLUTIONS_PER_GENERATION = 100;
    private Integer TOTAL_THREAD_AMOUNT = 5;
    private final Random random = new Random();

    private Integer[][] cityTravelCostGrid;
    private Boolean symmetryConstaint = true;

    private CityNetworkSingleton() {
        this.cityTravelCostGrid = generateNewGrid();
    }

    /**
     * Singleton getter
     * @return singleton
     */
    public synchronized static CityNetworkSingleton getInstance() {
        if(instance == null) {
            instance = new CityNetworkSingleton();
        }
        return instance;
    }

    /**
     * Method for flipping the switch responseible for symmetry constraint.
     * If active, then cost of travel from X to Y is the same as from Y to X
     * @return new symmetry constraint value
     */
    public Boolean flipSymmetryConstraint() {
        this.symmetryConstaint = !this.symmetryConstaint;
        return this.symmetryConstaint;
    }

    /**
     * Method for manually inserting a value of travel cost from city X to Y.
     * If symmetry constraint is active, then also sets the value for travelling from city Y to X.
     * @throws IllegalStateException if X is the same as Y
     */
    public void updateTravelCost(int x, int y, int value) throws IllegalStateException {
        if(x == y) {
            throw new IllegalStateException("Travel cost from city X to X must always be zero!");
        }
        cityTravelCostGrid[x][y] = value;
        if(symmetryConstaint) {
            cityTravelCostGrid[x][y] = value;
        }
    }

    /**
     * Method for generating new grid of travel costs between cities.
     * We assume that every city is connected to one another for the purpose of the algorithm.
     * This method generates a new square 2D array, sets 0 on a diagonal where X is equal to Y,
     * and in other places, sets a value between 100 and 900 (10.0 and 90.0, multiplied by 10).
     * If symmetry constraint is active, then [X,Y] element has the same value as [Y,X]
     * @return new distance grid
     */
    public Integer[][] generateNewGrid() {
        cityTravelCostGrid = new Integer[CITY_GRID_SIZE][CITY_GRID_SIZE];
        for(int i=0;i<CITY_GRID_SIZE; i++) {
            cityTravelCostGrid[i][i] = 0;
            for(int j = i+1; j<CITY_GRID_SIZE; j++) {
                int travelCost = random.nextInt(800) + 100;
                cityTravelCostGrid[i][j] = travelCost;
                cityTravelCostGrid[j][i] = symmetryConstaint ? travelCost : random.nextInt(800) + 100;
            }
        }
        return cityTravelCostGrid;
    }

    /**
     * Getter for the constant value of the city grid size - the total amount of cities
     * @return city grid size
     */
    public Integer getCityGridSize() {
        return CITY_GRID_SIZE;
    }

    /**
     * Setter for the constant value of the city grid size - the total amount of cities
     * @param cityGridSize city grid size
     */
    public void setCityGridSize(Integer cityGridSize) {
        this.CITY_GRID_SIZE = cityGridSize;
        this.generateNewGrid();
    }

    /**
     * Getter for the city travel cost grid
     * @return city travel cost grid
     */
    public Integer[][] getCityTravelCostGrid() {
        return cityTravelCostGrid;
    }

    /**
     * Getter for the joining point - the index of a gene, where the offspring stops
     * getting information from one parent and starts from another
     * @return joining point
     */
    public Integer getJoiningPoint() {
        return JOINING_POINT;
    }

    /**
     * Setter for the joining point
     * @param joiningPoint joining point
     */
    public void setJoiningPoint(Integer joiningPoint) {
        this.JOINING_POINT = joiningPoint;
    }
    /**
     * Getter for the amount of solutions per each generation
     * @return total solutions per generation
     */
    public Integer getTotalSolutionsPerGeneration() {
        return TOTAL_SOLUTIONS_PER_GENERATION;
    }


    /**
     * Setter for the totalSolutionsPerGeneration
     * @param totalSolutionsPerGeneration totalSolutionsPerGeneration
     */
    public void setTotalSolutionsPerGeneration(Integer totalSolutionsPerGeneration) {
        this.TOTAL_SOLUTIONS_PER_GENERATION = totalSolutionsPerGeneration;
    }

    /**
     * Getter for the amount of threads for multi-threaded solver
     * @return total thread amount
     */
    public Integer getTotalThreadAmount() {
        return TOTAL_THREAD_AMOUNT;
    }

    /**
     * Setter for the totalSolutionsPerGeneration
     * @param totalThreadAmount totalThreadAmount
     */
    public void setTotalThreadAmount(Integer totalThreadAmount) {
        this.TOTAL_THREAD_AMOUNT = totalThreadAmount;
    }

    /**
     * Getter for the symmetry constraint value
     * @return symmetry constraint
     */
    public Boolean getSymmetryConstaint() {
        return symmetryConstaint;
    }
}
