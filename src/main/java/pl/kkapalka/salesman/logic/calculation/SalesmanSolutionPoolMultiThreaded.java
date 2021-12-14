package pl.kkapalka.salesman.logic.calculation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.Collections;
import pl.kkapalka.salesman.logic.calcMode.SalesmanSolutionCalculator;
import pl.kkapalka.salesman.models.SalesmanSolution;
import pl.kkapalka.salesman.models.CityNetworkSingleton;
import java.util.concurrent.atomic.AtomicInteger;

import static pl.kkapalka.salesman.HelperMethods.distinctByKey;

public class SalesmanSolutionPoolMultiThreaded implements SalesmanSolutionCallback, SalesmanSolutionCalculator {
    private final SalesmanThreadPooled[] salesmanThreads;
    private ArrayList<SalesmanSolution> salesmanSolutionArrayList;
    AtomicInteger waitingThreads = new AtomicInteger(0);
    public int generation = 0;
    boolean calculating = false;

    public SalesmanSolutionPoolMultiThreaded() {
        salesmanThreads = new SalesmanThreadPooled[CityNetworkSingleton.getTotalThreadAmount()];
        for(int i=0;i<salesmanThreads.length; i++) {
            salesmanThreads[i] = new SalesmanThreadPooled(this, CityNetworkSingleton.getTotalThreadAmount());
        }
        salesmanSolutionArrayList = new ArrayList<>();
        generation = 0;
    }

    public void startCalculations() {
        for (SalesmanThreadPooled salesmanThread : salesmanThreads) {
            salesmanThread.start();
        }
    }
    public void stopCalculations() {
        for (SalesmanThreadPooled salesmanThread : salesmanThreads) {
            salesmanThread.cease();
            try {
                salesmanThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
        this.waitingThreads.incrementAndGet();
        if(this.waitingThreads.get() == salesmanThreads.length) {
            produceGeneration();
        }
    }

    public void produceGeneration() {
        this.waitingThreads.set(0);
        generation++;
        int halfPopulation = CityNetworkSingleton.getTotalSolutionsPerGeneration() / 2;
        this.salesmanSolutionArrayList = Arrays.stream(this.salesmanThreads)
                .flatMap(thread -> Stream.of(thread.solutionArray))
                .sorted(SalesmanSolution::compareTo)
                .filter(distinctByKey(SalesmanSolution::getTotalTravelCost))
                .limit(halfPopulation)
                .collect(Collectors.toCollection(ArrayList::new));

        for(int i= salesmanSolutionArrayList.size(); i < halfPopulation; i++) {
            salesmanSolutionArrayList.add(salesmanSolutionArrayList.get(0));
        }
        System.out.println(salesmanSolutionArrayList.get(0).getTotalTravelCost());
        Collections.shuffle(salesmanSolutionArrayList);
        for (int i=0;i<salesmanThreads.length; i++) {
            salesmanThreads[i].onSolutionRedistribution(salesmanSolutionArrayList.subList(i * (halfPopulation / salesmanThreads.length), (i + 1) * (halfPopulation / salesmanThreads.length)).toArray(SalesmanSolution[]::new));
        }
        for(SalesmanThreadPooled thread: salesmanThreads) {
            thread.onResume();
        }
    }
}
