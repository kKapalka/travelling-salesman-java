package pl.kkapalka.salesman.logic.calculation;

import pl.kkapalka.salesman.models.SalesmanSolution;
import java.util.Random;
import pl.kkapalka.salesman.models.CityNetworkSingleton;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.ArrayList;

import static pl.kkapalka.salesman.HelperMethods.distinctByKey;

public class SalesmanThreadUnpooled extends Thread{

    SalesmanSolution[] salesmanSolutions;
    Random random = new Random();
    AtomicBoolean complete = new AtomicBoolean(false);
    SalesmanSolutionCallback callback;

    public SalesmanThreadUnpooled(SalesmanSolutionCallback callback) {
        this.salesmanSolutions = new SalesmanSolution[CityNetworkSingleton.getTotalSolutionsPerGeneration()];
        this.callback = callback;
    }

    public void cease() {
        this.complete.set(true);
    }

    @Override
    public void run() {
        for(int i=0;i<salesmanSolutions.length; i++) {
            salesmanSolutions[i] = new SalesmanSolution(false);
        }
        while(!complete.get()) {
            performRankingSelection();
            createNewGeneration();
        }
    }

    private void performRankingSelection() {
        ArrayList<SalesmanSolution> sortedList = Arrays.stream(salesmanSolutions)
                .sorted(SalesmanSolution::compareTo)
                .filter(distinctByKey(SalesmanSolution::getTotalTravelCost))
                .limit(50).collect(Collectors.toCollection(java.util.ArrayList::new));
        for(int i= sortedList.size(); i < salesmanSolutions.length / 2; i++) {
            sortedList.add(sortedList.get(0));
        }
        System.out.println(sortedList.get(0).getTotalTravelCost());
        java.util.Collections.shuffle(sortedList);
        for(int i = 0; i < salesmanSolutions.length/2; i++) {
            salesmanSolutions[i] = sortedList.get(i);
        }
        callback.transferSolutions();
    }

    private void createNewGeneration() {
        int halfLength = salesmanSolutions.length / 2;
        for(int i=0;i<salesmanSolutions.length / 2; i+=2) {
            salesmanSolutions[i + halfLength] = salesmanSolutions[i].produceOffspring(salesmanSolutions[i+1], pl.kkapalka.salesman.models.CityNetworkSingleton.getJoiningPoint());
            salesmanSolutions[i + halfLength + 1] = salesmanSolutions[i+1].produceOffspring(salesmanSolutions[i], pl.kkapalka.salesman.models.CityNetworkSingleton.getJoiningPoint());
        }

        if(salesmanSolutions.length % 2 != 0) {
            salesmanSolutions[salesmanSolutions.length - 1] = salesmanSolutions[1].produceOffspring(salesmanSolutions[salesmanSolutions.length - 2], pl.kkapalka.salesman.models.CityNetworkSingleton.getJoiningPoint());
        }
        for(int i=0;i< salesmanSolutions.length / 10; i++) {
            salesmanSolutions[random.nextInt(salesmanSolutions.length)].mutate();
        }
    }
}
