package pl.kkapalka.salesman.logic;

import pl.kkapalka.salesman.models.SalesmanSolution;
import java.util.concurrent.atomic.AtomicBoolean;
import pl.kkapalka.salesman.models.CityNetworkSingleton;
import java.util.Random;

public class SalesmanThread extends Thread {

    SalesmanSolution[] solutionArray = new SalesmanSolution[20];
    Random random = new Random();
    final SalesmanSolutionCallback callback;
    AtomicBoolean waiting = new AtomicBoolean(false);
    AtomicBoolean complete = new AtomicBoolean(false);

    public SalesmanThread(SalesmanSolutionCallback callback) {
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
            }
        }

    }

    public void cease() {
        this.complete.set(true);
    }
    private void onCalculationFinished() {
        this.waiting.set(true);
        callback.transferSolutions(solutionArray);
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
        for(int i=0;i<solutionArray.length / 2; i+=2) {
            solutionArray[i + 10] = solutionArray[i].produceOffspring(solutionArray[i+1], CityNetworkSingleton.getJoiningPoint());
            solutionArray[i + 11] = solutionArray[i+1].produceOffspring(solutionArray[i], CityNetworkSingleton.getJoiningPoint());
        }
        solutionArray[random.nextInt(solutionArray.length)].mutate();
        solutionArray[random.nextInt(solutionArray.length)].mutate();
    }
}
