package pl.kkapalka.salesman.logic;
import pl.kkapalka.salesman.models.SalesmanSolution;
import java.util.Random;

public class SalesmanSolutionPoolSingleThreaded {
    SalesmanSolution[] salesmanSolutions = new SalesmanSolution[100];
    Random random = new Random();
    Thread thread;

    public SalesmanSolutionPoolSingleThreaded() {
        for(int i=0;i<salesmanSolutions.length; i++) {
            salesmanSolutions[i] = new pl.kkapalka.salesman.models.SalesmanSolution(false);
        }
    }

    public void startCalculations() {
        Long start = System.currentTimeMillis();
        for(int i=0;i<100000;i++) {
            performRankingSelection();
            createNewGeneration();
        }
        Long stop = System.currentTimeMillis();
        System.out.println(stop - start);
    }

    private void performRankingSelection() {
        salesmanSolutions = java.util.Arrays.stream(salesmanSolutions)
                .sorted(java.util.Comparator.comparing(pl.kkapalka.salesman.models.SalesmanSolution::getTotalTravelCost))
                .toArray(SalesmanSolution[]::new);
        for(int i = (salesmanSolutions.length / 2); i < salesmanSolutions.length; i++) {
            salesmanSolutions[i] = null;
        }
        System.out.println(salesmanSolutions[0].getTotalTravelCost());
    }

    private void createNewGeneration() {
        for(int i=0;i<salesmanSolutions.length / 2; i+=2) {
            salesmanSolutions[i + (salesmanSolutions.length / 2)] = salesmanSolutions[i].produceOffspring(salesmanSolutions[i+1], 50);
            salesmanSolutions[i + (salesmanSolutions.length / 2) + 1] = salesmanSolutions[i+1].produceOffspring(salesmanSolutions[i], 50);
        }
        salesmanSolutions[random.nextInt(salesmanSolutions.length)].mutate();
        salesmanSolutions[random.nextInt(salesmanSolutions.length)].mutate();
    }
}
