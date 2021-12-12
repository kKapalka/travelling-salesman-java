package pl.kkapalka.salesman.models;
import java.util.ArrayList;
import java.util.Random;

public class SalesmanSolution implements Comparable {

    private Random random = new Random();

    /**
     * Gene storing the information about travel route.
     * Its size is equal to size of city grid - each index represents each city.
     * Its values are from 0 until 256 - those represent the travel priority. Lower value represents higher priority.
     */
    private int[] travelRouteGene;

    /**
     * Travel cost
     */
    private long totalTravelCost;

    /**
     * Constructor for new SalesmanSolution class.
     * Takes in parameter letting it know if it should be pre-initialized or not.
     * If pre-initialized, it automatically attempts to calculate the total travel cost between cities based on the algorithm
     * @param emptyOnBirth if true, the gene starts out empty
     */
    public SalesmanSolution(boolean emptyOnBirth) {
        this.travelRouteGene = new int[CityNetworkSingleton.getCityGridSize()];
        if(!emptyOnBirth) {
            for (short i=0;i<travelRouteGene.length; i++) {
                this.travelRouteGene[i] = random.nextInt(255);
            }
            this.calculateTotalTravelCost();
        }
    }

    /**
     * This method is responsible for populating the solution's gene with information from parents
     * First parent populates the array up to the joining point, and second parent - from its point onward
     * @param parent1 parent 1
     * @param parent2 parent 2
     * @param joiningPoint joining point
     */
    void takeInParentInformation(SalesmanSolution parent1, SalesmanSolution parent2, int joiningPoint) {
        System.arraycopy(parent1.getTravelRouteGene(), 0, travelRouteGene, 0, joiningPoint);
        System.arraycopy(parent2.getTravelRouteGene(), joiningPoint, travelRouteGene, joiningPoint, 100 - joiningPoint);
        calculateTotalTravelCost();
    }

    /**
     * Method responsible for two parent solutions to get together and produce an offspring.
     * First, it generates an offspring, with empty array.
     * Then, the offspring takes in the parent information
     * @param other other solution
     * @param joiningPoint joining point
     * @return offspring
     * @throws IllegalStateException if the joining point is outside of bounds (0 - cityGridSize)
     */
    public SalesmanSolution produceOffspring(SalesmanSolution other, int joiningPoint) throws IllegalStateException {
        if(joiningPoint >= CityNetworkSingleton.getCityGridSize() || joiningPoint < 0) {
            throw new IllegalStateException("Joining point must have value between 0 and " + CityNetworkSingleton.getCityGridSize());
        }
        SalesmanSolution offspring = new SalesmanSolution(true);
        offspring.takeInParentInformation(this, other, joiningPoint);
        return offspring;
    }

    /**
     * Method responsible for mutating the gene. If it fires, a random element is set to a random value.
     * On firing the mutation method, the travel cost is re-calculated.
     */
    public void mutate() {
        int index = random.nextInt(CityNetworkSingleton.getCityGridSize());
        while(this.travelRouteGene[index] == 0) {
            index = random.nextInt(CityNetworkSingleton.getCityGridSize());
        }
        this.travelRouteGene[index] = random.nextInt(255);
        this.calculateTotalTravelCost();
    }

    /**
     * Method responsible for calculating travel cost.
     * First, the ranking is created, based on the gene - a 2d array, where early elements store the indices of cities which are first to be visited.
     * Then, based on the ranking, the visit order is created.
     * And afterwards, all the travel costs between each consecutive city are summed up (including the travel from last city to the first).
     * This value is then stored in the gene for later use.
     */
    public void calculateTotalTravelCost() {
        Integer[][] travelCostArray = CityNetworkSingleton.getInstance().getCityTravelCostGrid();
        ArrayList<Integer>[] regroupedCityRanking = (ArrayList<Integer>[]) new ArrayList[256];
        for(int i=0; i<this.travelRouteGene.length; i++) {
            if(regroupedCityRanking[this.travelRouteGene[i]] == null ) {
                regroupedCityRanking[this.travelRouteGene[i]] = new ArrayList<>();
            }
            regroupedCityRanking[this.travelRouteGene[i]].add(i);
        }
        int[] route = new int[CityNetworkSingleton.getCityGridSize()];
        int routeIterator = 0;
        for (java.util.ArrayList<Integer> integers : regroupedCityRanking) {
            if (integers != null) {
                for (Integer integer : integers) {
                    route[routeIterator++] = integer;
                }
            }
        }
        Long sum = 0L;
        for(int i=1;i<CityNetworkSingleton.getCityGridSize();i++) {
            sum += travelCostArray[route[i-1]][route[i]];
        }
        sum += travelCostArray[CityNetworkSingleton.getCityGridSize()-1][0];
        this.totalTravelCost = sum;
    }

    /**
     * Getter for the travel route gene
     * @return travel route gene
     */
    public int[] getTravelRouteGene() {
        return travelRouteGene;
    }

    /**
     * Getter for the total travel cost
     * @return total travel cost
     */
    public long getTotalTravelCost() {
        return totalTravelCost;
    }

    @Override
    public int compareTo(Object o) {
        int res = 0;
        if(o instanceof SalesmanSolution) {
            SalesmanSolution other = (SalesmanSolution) o;
            if(this.totalTravelCost == 0) {
                this.calculateTotalTravelCost();
            }
            if(other.totalTravelCost == 0) {
                other.calculateTotalTravelCost();
            }
            if(this.totalTravelCost == other.totalTravelCost) {
                res = 0;
            } else if(this.totalTravelCost < other.getTotalTravelCost()) {
                res = -1;
            } else {
                res = 1;
            }
        }
        return res;
    }
}
