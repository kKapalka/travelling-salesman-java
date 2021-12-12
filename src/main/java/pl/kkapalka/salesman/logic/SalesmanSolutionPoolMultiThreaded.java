package pl.kkapalka.salesman.logic;
import java.util.ArrayList;
import pl.kkapalka.salesman.models.SalesmanSolution;
import java.util.concurrent.atomic.AtomicInteger;

import static pl.kkapalka.salesman.HelperMethods.distinctByKey;

public class SalesmanSolutionPoolMultiThreaded implements SalesmanSolutionCallback {
    private SalesmanThread[] salesmanThreads = new SalesmanThread[5];
    private ArrayList<SalesmanSolution> salesmanSolutionArrayList = new ArrayList<>();
    AtomicInteger waitingThreads = new AtomicInteger(0);
    public int generation = 0;

    public SalesmanSolutionPoolMultiThreaded() {
        for(int i=0;i<salesmanThreads.length; i++) {
            salesmanThreads[i] = new SalesmanThread(this);
        }
    }

    public void startCalculations() {
        for (SalesmanThread salesmanThread : salesmanThreads) {
            salesmanThread.start();
        }
    }
    public void stopCalculations() {
        for (SalesmanThread salesmanThread : salesmanThreads) {
            salesmanThread.cease();
            try {
                salesmanThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("generation "+generation);
    }

    @Override
    public synchronized void transferSolutions(SalesmanSolution[] solutionArray) {
        this.waitingThreads.incrementAndGet();
        if(this.waitingThreads.get() == salesmanThreads.length) {
            produceGeneration();
        }
    }

    public void produceGeneration() {
        this.waitingThreads.set(0);
        generation++;
        this.salesmanSolutionArrayList = java.util.Arrays.stream(this.salesmanThreads)
                .flatMap(thread -> java.util.stream.Stream.of(thread.solutionArray))
                .sorted(pl.kkapalka.salesman.models.SalesmanSolution::compareTo)
                .filter(distinctByKey(pl.kkapalka.salesman.models.SalesmanSolution::getTotalTravelCost))
                .limit(salesmanThreads.length * 10L)
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

        for(int i= salesmanSolutionArrayList.size(); i < salesmanThreads.length * 10L; i++) {
            salesmanSolutionArrayList.add(salesmanSolutionArrayList.get(0));
        }
        System.out.println(salesmanSolutionArrayList.get(0).getTotalTravelCost());
        java.util.Collections.shuffle(salesmanSolutionArrayList);
        for (int i=0;i<salesmanThreads.length; i++) {
            salesmanThreads[i].onSolutionRedistribution(salesmanSolutionArrayList.subList(i * 10, (i + 1) * 10).toArray(SalesmanSolution[]::new));
        }
        for(SalesmanThread thread: salesmanThreads) {
            thread.onResume();
        }
    }
}
