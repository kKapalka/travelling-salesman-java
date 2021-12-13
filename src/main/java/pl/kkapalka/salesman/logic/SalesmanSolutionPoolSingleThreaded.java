package pl.kkapalka.salesman.logic;
import pl.kkapalka.salesman.models.SalesmanSolution;
import pl.kkapalka.salesman.models.CityNetworkSingleton;
import java.util.stream.Collectors;
import java.util.Random;
import java.util.Arrays;
import java.util.ArrayList;

import static pl.kkapalka.salesman.HelperMethods.distinctByKey;

public class SalesmanSolutionPoolSingleThreaded {
    SalesmanSolution[] salesmanSolutions = new SalesmanSolution[100];
    Random random = new Random();
    Thread thread;

    public SalesmanSolutionPoolSingleThreaded() {
        for(int i=0;i<salesmanSolutions.length; i++) {
            salesmanSolutions[i] = new SalesmanSolution(false);
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
        ArrayList<SalesmanSolution> sortedList = Arrays.stream(salesmanSolutions)
                .sorted(SalesmanSolution::compareTo)
                .filter(distinctByKey(SalesmanSolution::getTotalTravelCost))
                .limit(50).collect(Collectors.toCollection(ArrayList::new));
        for(int i= sortedList.size(); i < salesmanSolutions.length / 2; i++) {
            sortedList.add(sortedList.get(0));
        }
        System.out.println(sortedList.get(0).getTotalTravelCost());
        java.util.Collections.shuffle(sortedList);
        for(int i = 0; i < salesmanSolutions.length/2; i++) {
            salesmanSolutions[i] = sortedList.get(i);
        }
    }

    private void createNewGeneration() {
        for(int i=0;i<salesmanSolutions.length / 2; i+=2) {
            salesmanSolutions[i + (salesmanSolutions.length / 2)] = salesmanSolutions[i].produceOffspring(salesmanSolutions[i+1], CityNetworkSingleton.getJoiningPoint());
            salesmanSolutions[i + (salesmanSolutions.length / 2) + 1] = salesmanSolutions[i+1].produceOffspring(salesmanSolutions[i], CityNetworkSingleton.getJoiningPoint());
        }
        for(int i=0;i< salesmanSolutions.length / 10; i++) {
            salesmanSolutions[random.nextInt(salesmanSolutions.length)].mutate();
        }
    }
}
