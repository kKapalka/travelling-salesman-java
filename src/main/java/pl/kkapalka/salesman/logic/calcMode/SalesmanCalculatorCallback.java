package pl.kkapalka.salesman.logic.calcMode;

import pl.kkapalka.salesman.models.SalesmanSolution;

public interface SalesmanCalculatorCallback {
    void onTransmitGraphData(Long minimumCost, Double averageCost);
    void onCollectLastGeneration(SalesmanSolution[] solutions);
}
