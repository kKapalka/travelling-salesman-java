package pl.kkapalka.salesman.logic;
import pl.kkapalka.salesman.models.SalesmanSolution;

public interface SalesmanSolutionCallback {
    void transferSolutions(SalesmanSolution[] solutionArray);
}
