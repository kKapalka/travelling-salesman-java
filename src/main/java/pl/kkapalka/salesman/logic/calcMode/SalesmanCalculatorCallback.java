package pl.kkapalka.salesman.logic.calcMode;

import pl.kkapalka.salesman.models.SalesmanSolution;
import java.util.List;

public interface SalesmanCalculatorCallback {
    void onTransmitGraphData(Long minimumCost, Double averageCost);
    void onCollectLastGeneration(List<SalesmanSolution> solutions, int internalClock);
}
