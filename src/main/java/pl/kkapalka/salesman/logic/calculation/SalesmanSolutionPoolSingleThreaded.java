package pl.kkapalka.salesman.logic.calculation;
import pl.kkapalka.salesman.logic.calcMode.SalesmanSolutionCalculator;

public class SalesmanSolutionPoolSingleThreaded implements SalesmanSolutionCalculator, SalesmanSolutionCallback {
    private SalesmanThreadUnpooled thread;
    int generation;
    boolean calculating = false;

    public SalesmanSolutionPoolSingleThreaded() {
        thread = new SalesmanThreadUnpooled(this);
    }

    public void startCalculations() {
        thread.start();
    }

    public void stopCalculations() {
        thread.cease();
        System.out.println("generation "+generation);
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
        generation++;
    }
}
