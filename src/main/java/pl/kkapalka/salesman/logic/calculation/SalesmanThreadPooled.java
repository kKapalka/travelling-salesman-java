package pl.kkapalka.salesman.logic.calculation;

import pl.kkapalka.salesman.models.SalesmanSolution;
import java.util.concurrent.atomic.AtomicBoolean;
import pl.kkapalka.salesman.models.CityNetworkSingleton;
import java.util.Random;

public class SalesmanThreadPooled extends Thread {

    SalesmanSolution[] solutionArray;
    Random random = new Random();
    final SalesmanSolutionCallback callback;
    AtomicBoolean waiting = new AtomicBoolean(false);
    AtomicBoolean complete = new AtomicBoolean(false);

    public SalesmanThreadPooled(SalesmanSolutionCallback callback, int threadNumber) {
        this.solutionArray = new SalesmanSolution[CityNetworkSingleton.getTotalSolutionsPerGeneration() / threadNumber];
        this.callback = callback;
    }

    @Override
    public void run() {
        for(int i=0;i<solutionArray.length; i++) {
            solutionArray[i] = new SalesmanSolution(false);
        }
        onCalculationFinished();
        while(!complete.get()) {
            while(!waiting.get()) {
                createNewGeneration();
                onCalculationFinished();
                if(complete.get()) {
                    break;
                }
            }
        }
    }

    public void cease() {
        this.complete.set(true);
    }

    private void onCalculationFinished() {
        this.waiting.set(true);
        callback.transferSolutions();
    }

    public void onResume() {
        this.waiting.set(false);
    }
    public void onSolutionRedistribution(SalesmanSolution[] inputSolutionArray) throws IllegalStateException{
        if(inputSolutionArray.length != solutionArray.length / 2) {
            throw new IllegalStateException("Amount of solutions redistributed should always be half of the total solution pool per thread!");
        }
        System.arraycopy(inputSolutionArray, 0, solutionArray, 0, inputSolutionArray.length);
    }

    private void createNewGeneration() {
        int halfLength = solutionArray.length / 2;
        for(int i=1;i<halfLength; i+=2) {
            solutionArray[i + halfLength] = solutionArray[i].produceOffspring(solutionArray[i-1], CityNetworkSingleton.getJoiningPoint());
            solutionArray[i + halfLength - 1] = solutionArray[i-1].produceOffspring(solutionArray[i], CityNetworkSingleton.getJoiningPoint());
        }
        if(solutionArray.length % 2 != 0) {
            solutionArray[solutionArray.length - 1] = solutionArray[1].produceOffspring(solutionArray[solutionArray.length - 2], CityNetworkSingleton.getJoiningPoint());
        }
        for(int i=0;i<solutionArray.length; i+=10) {
            solutionArray[random.nextInt(solutionArray.length)].mutate();
        }
    }
}
