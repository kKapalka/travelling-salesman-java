package pl.kkapalka.salesman.logic;
import pl.kkapalka.salesman.models.SalesmanSolution;
import java.util.Random;

import static pl.kkapalka.salesman.HelperMethods.distinctByKey;

public class SalesmanSolutionPoolSingleThreaded {
    SalesmanSolution[] salesmanSolutions = new SalesmanSolution[100];
    Random random = new Random();
    Thread thread;

    public SalesmanSolutionPoolSingleThreaded() {
        for(int i=0;i<salesmanSolutions.length; i++) {
            salesmanSolutions[i] = new pl.kkapalka.salesman.models.SalesmanSolution(false);
        }
    }

    public void startCalculations() {
        Long start = System.currentTimeMillis();
        for(int i=0;i<100000;i++) {
            performRankingSelection();
            createNewGeneration();
        }
        Long stop = System.currentTimeMillis();
        System.out.println(stop - start);
    }

    private void performRankingSelection() {
        java.util.ArrayList<SalesmanSolution> sortedList = java.util.Arrays.stream(salesmanSolutions)
                .sorted(pl.kkapalka.salesman.models.SalesmanSolution::compareTo)
                .filter(distinctByKey(pl.kkapalka.salesman.models.SalesmanSolution::getTotalTravelCost)).collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
        for(int i= sortedList.size(); i < salesmanSolutions.length / 2; i++) {
            sortedList.add(sortedList.get(0));
        }
        for(int i = 0; i < salesmanSolutions.length/2; i++) {
            salesmanSolutions[i] = sortedList.get(i);
        }
        System.out.println(salesmanSolutions[0].getTotalTravelCost());
    }

    private void createNewGeneration() {
        for(int i=0;i<salesmanSolutions.length / 2; i+=2) {
            salesmanSolutions[i + (salesmanSolutions.length / 2)] = salesmanSolutions[i].produceOffspring(salesmanSolutions[i+1], 50);
            salesmanSolutions[i + (salesmanSolutions.length / 2) + 1] = salesmanSolutions[i+1].produceOffspring(salesmanSolutions[i], 50);
        }
        for(int i=0;i< salesmanSolutions.length / 10; i++) {
            salesmanSolutions[random.nextInt(salesmanSolutions.length)].mutate();
        }
    }
}
