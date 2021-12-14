package pl.kkapalka.salesman.logic.calcMode;

import pl.kkapalka.salesman.logic.calculation.SalesmanSolutionPoolSingleThreaded;
import pl.kkapalka.salesman.logic.calculation.SalesmanSolutionPoolMultiThreaded;

public enum CalculationModeSelector {
    SINGLE_THREADED {
        @Override
        public SalesmanSolutionCalculator createCalculator(SalesmanCalculatorCallback callback) {
            return new SalesmanSolutionPoolSingleThreaded(callback);
        }
    },
    MULTI_THREADED {
        @Override
        public SalesmanSolutionCalculator createCalculator(SalesmanCalculatorCallback callback) {
            return new SalesmanSolutionPoolMultiThreaded(callback);
        }
    };

    public abstract SalesmanSolutionCalculator createCalculator(SalesmanCalculatorCallback callback);
}
