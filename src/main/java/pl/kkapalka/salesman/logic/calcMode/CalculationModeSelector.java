package pl.kkapalka.salesman.logic.calcMode;

import pl.kkapalka.salesman.models.CityNetworkSingleton;
import pl.kkapalka.salesman.logic.calculation.SalesmanSolutionPoolSingleThreaded;
import pl.kkapalka.salesman.logic.calculation.SalesmanSolutionPoolMultiThreaded;

public enum CalculationModeSelector {
    SINGLE_THREADED {
        @Override
        public SalesmanSolutionCalculator createCalculator() {
            return new SalesmanSolutionPoolSingleThreaded();
        }
    },
    MULTI_THREADED {
        @Override
        public SalesmanSolutionCalculator createCalculator() {
            return new SalesmanSolutionPoolMultiThreaded(CityNetworkSingleton.getTotalThreadAmount());
        }
    };

    public abstract SalesmanSolutionCalculator createCalculator();
}
