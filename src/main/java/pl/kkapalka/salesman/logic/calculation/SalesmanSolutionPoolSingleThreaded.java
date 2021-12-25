package pl.kkapalka.salesman.logic.calculation;
import pl.kkapalka.salesman.logic.calcMode.SalesmanSolutionCalculator;
import pl.kkapalka.salesman.logic.calcMode.SalesmanCalculatorCallback;
import pl.kkapalka.salesman.models.SalesmanSolution;
import pl.kkapalka.salesman.models.CityNetworkSingleton;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.ArrayList;
import static pl.kkapalka.salesman.HelperMethods.distinctByKey;

public class SalesmanSolutionPoolSingleThreaded implements SalesmanSolutionCalculator, SalesmanSolutionCallback {
    private final SalesmanThreadUnpooled thread;
    SalesmanCalculatorCallback callback;
    int internalClock = 0;

    public SalesmanSolutionPoolSingleThreaded(SalesmanCalculatorCallback callback) {
        this.callback = callback;
        thread = new SalesmanThreadUnpooled(this);
    }

    public void startCalculation() {
        internalClock = CityNetworkSingleton.getInstance().getChartRefreshRate();
        thread.start();
    }

    public void stopCalculation() {
        thread.cease();
        ArrayList<SalesmanSolution> sortedList = Arrays.stream(thread.salesmanSolutions)
                .sorted(SalesmanSolution::compareTo)
                .filter(distinctByKey(SalesmanSolution::getTotalTravelCost))
                .limit(CityNetworkSingleton.getInstance().getTotalSolutionsPerGeneration() / 2).collect(Collectors.toCollection(ArrayList::new));
        for(int i= sortedList.size(); i < thread.salesmanSolutions.length / 2; i++) {
            sortedList.add(sortedList.get(0));
        }
        callback.onCollectLastGeneration(sortedList.stream().sorted(SalesmanSolution::compareTo).collect(Collectors.toList()), internalClock);
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void transferSolutions(SalesmanSolution bestSolution) {
        if(internalClock == CityNetworkSingleton.getInstance().getChartRefreshRate()) {
            internalClock = -1;
            callback.onTransmitGraphData(bestSolution.getTotalTravelCost(), Arrays.stream(thread.salesmanSolutions)
                    .mapToLong(SalesmanSolution::getTotalTravelCost).average().getAsDouble());
        }
        internalClock++;
    }
}
