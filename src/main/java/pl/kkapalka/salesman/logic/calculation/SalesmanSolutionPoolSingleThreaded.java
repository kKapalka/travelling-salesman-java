package pl.kkapalka.salesman.logic.calculation;
import pl.kkapalka.salesman.logic.calcMode.SalesmanSolutionCalculator;
import pl.kkapalka.salesman.logic.calcMode.SalesmanCalculatorCallback;
import pl.kkapalka.salesman.models.SalesmanSolution;

import java.util.ArrayList;

import static pl.kkapalka.salesman.HelperMethods.distinctByKey;

public class SalesmanSolutionPoolSingleThreaded implements SalesmanSolutionCalculator, SalesmanSolutionCallback {
    private final SalesmanThreadUnpooled thread;
    SalesmanCalculatorCallback callback;

    public SalesmanSolutionPoolSingleThreaded(SalesmanCalculatorCallback callback) {
        this.callback = callback;
        thread = new SalesmanThreadUnpooled(this);
    }

    public void startCalculation() {
        thread.start();
    }

    public void stopCalculation() {
        thread.cease();
        ArrayList<SalesmanSolution> sortedList = java.util.Arrays.stream(thread.salesmanSolutions)
                .sorted(SalesmanSolution::compareTo)
                .filter(distinctByKey(SalesmanSolution::getTotalTravelCost))
                .limit(50).collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
        for(int i= sortedList.size(); i < thread.salesmanSolutions.length / 2; i++) {
            sortedList.add(sortedList.get(0));
        }
        callback.onCollectLastGeneration(sortedList.stream().sorted(SalesmanSolution::compareTo).toArray(SalesmanSolution[]::new));
    }

    @Override
    public synchronized void transferSolutions() {
    }

    @Override
    public void transferSolutions(SalesmanSolution bestSolution) {
        callback.onTransmitGraphData(bestSolution.getTotalTravelCost(), java.util.Arrays.stream(thread.salesmanSolutions)
                .mapToLong(SalesmanSolution::getTotalTravelCost).average().getAsDouble());
    }
}
