package pl.kkapalka.salesman.logic.calculation;
import pl.kkapalka.salesman.logic.calcMode.SalesmanSolutionCalculator;
import pl.kkapalka.salesman.logic.calcMode.SalesmanCalculatorCallback;
import pl.kkapalka.salesman.models.SalesmanSolution;

public class SalesmanSolutionPoolSingleThreaded implements SalesmanSolutionCalculator, SalesmanSolutionCallback {
    private SalesmanThreadUnpooled thread;
    int generation;
    boolean calculating = false;
    SalesmanCalculatorCallback callback;

    public SalesmanSolutionPoolSingleThreaded(SalesmanCalculatorCallback callback) {
        this.callback = callback;
        thread = new SalesmanThreadUnpooled(this);
    }

    public void startCalculations() {
        thread.start();
    }

    public void stopCalculations() {
        thread.cease();
    }

    public void toggleCalculation() {
        if(calculating) {
            stopCalculations();
            calculating = false;
        } else {
            startCalculations();
            calculating = true;
        }
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
