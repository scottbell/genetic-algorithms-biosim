package com.traclabs.ga.server.handlers;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.traclabs.ga.idl.genes.Gene;
import com.traclabs.ga.idl.genes.GeneHelper;
import com.traclabs.ga.idl.handlers.GAHandlerPOA;
import com.traclabs.ga.idl.nodes.GANode;
import com.traclabs.ga.idl.nodes.GANodeHelper;
import com.traclabs.ga.idl.nodes.Result;
import com.traclabs.ga.server.framework.GAInitializer;
import com.traclabs.ga.server.genes.GeneImpl;
import com.traclabs.ga.util.OrbUtils;

/**
 * @author Scott Bell
 */

public class GAHandlerImpl extends GAHandlerPOA {
    private List<GANode> myGANodes;

    private List<Float> utilityReceived;

    private Timer myTimer;

    private static final int POLL_NODES_DELAY = 2000;

    private List<Result> myBestResults;

    private Result myWorstResult;

    private static final float GENES_TO_KEEP_CONSTANT = 0.75f;

    private Random myRandomGen;

    private int bestGeneSize = 0;

    private int bestGenesAdded = 0;

    private static final int MUTATION_STRENGTH = 1;

    private static final float MUTATION_PROBABILITY = 0.8f;

    private static final float CROSS_PROBABILITY = 0.9f;

    private static final float INVERSION_PROBABILITY = 1.0f;

    private static final String DELIMITER = ",";
    
    private Logger myLogger;

    public GAHandlerImpl() {
        myLogger = Logger.getLogger(this.getClass()); 
        myGANodes = new Vector<GANode>();
        myBestResults = new Vector<Result>();
        utilityReceived = new Vector<Float>();
        myRandomGen = new Random();
        myTimer = new Timer();
    }

    private GeneImpl createGene(){
        String geneClassName = GAInitializer.getGeneClassName();
        GeneImpl newGeneImpl = null;
        try{
            Object newObject = Class.forName(geneClassName).newInstance();
            newGeneImpl = (GeneImpl)newObject;	
        }
        catch (Exception e){
            myLogger.error("Had problems creating genes of the type: "+geneClassName);
            myLogger.error(e);
        }
        return newGeneImpl;
    }

    public void startLookingForNodes() {
        myTimer.schedule(new PollTask(), 0, POLL_NODES_DELAY);
    }

    private void seeIfWeCaughtNodes() {
        if (myGANodes.size() < 1) {
            myLogger.info("GAHandlerImpl: No GA Nodes found, polling for nodes");
        } else {
            myLogger.info("GAHandlerImpl: " + myGANodes.size()
                    + " GA Nodes found, starting GA run");
            myTimer.cancel();
            bestGeneSize = Math.round(myGANodes.size() * GENES_TO_KEEP_CONSTANT);
            if (bestGeneSize <= 0)
                bestGeneSize = 1;
            myLogger.info("GAHandlerImpl: Keeping the best "
                    + bestGeneSize + " genes");
            startGA();
        }
    }

    private void pollForNodes() {
        try {
            for (int i = 0; i >= 0; i++) {
                GANode currentNode = GANodeHelper.narrow(OrbUtils
                        .getNodeNamingContext(i).resolve_str("GANode"));
                myGANodes.add(currentNode);
            }
        } catch (org.omg.CORBA.UserException e) {
            seeIfWeCaughtNodes();
        } catch (Exception e) {
            OrbUtils.resetInit();
        }
    }

    public Result[] getBestResults() {
        Result[] newResultArray = (Result[]) (myBestResults.toArray());
        return newResultArray;
    }

    public Result getBestResult() {
        if (myBestResults.size() > 0)
            return (Result) (myBestResults.get(0));
        else
            return null;
    }

    public Result getWorstResult() {
        return myWorstResult;
    }

    private void startGA() {
        myLogger.info("GAHandlerImpl: GA started");
        myLogger.info("GAHandlerImpl: creating " + myGANodes.size()
                + " gene(s)");
        for (Iterator iter = myGANodes.iterator(); iter.hasNext();) {
            try {
                GANode currentNode = (GANode) (iter.next());
                currentNode.runGene(GeneHelper.narrow(OrbUtils
                        .poaToCorbaObj(createGene())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void evaluateResult(Result pResult) {
        myLogger.debug("GAHandlerImpl: Got back "+pResult.getUtility()+"utility ");
        utilityReceived.add(new Float(pResult.getUtility()));
        checkIfBest(pResult);
        checkIfWorst(pResult);
        printResult(pResult);
        for (Iterator iter = myGANodes.iterator(); iter.hasNext();) {
            GANode currentNode = (GANode) (iter.next());
            if (currentNode.isDone()) {
                int randomIndex = myRandomGen.nextInt(myBestResults.size());
                float randomOperation = myRandomGen.nextFloat();
                Result randomBestResult = (Result) (myBestResults
                        .get(randomIndex));
                Gene randomBestGene = randomBestResult.getGene();
                myLogger.debug("Sending "+randomBestResult.getUtility());
                if (randomOperation <= MUTATION_PROBABILITY) {
                    randomBestGene.mutate(5);
                    currentNode.runGene(randomBestGene.mutate(5));
                } else if (randomOperation <= CROSS_PROBABILITY) {
                    if ((randomIndex + 1) >= myBestResults.size()) {
                        if ((randomIndex - 1) < 0) {
                            currentNode.runGene(randomBestGene.cross(pResult
                                    .getGene()));
                        } else {
                            Gene previousBestResult = ((Result) (myBestResults
                                    .get(randomIndex - 1))).getGene();
                            currentNode.runGene(randomBestGene
                                    .cross(previousBestResult));
                        }
                    } else {
                        Gene nextBestResult = ((Result) (myBestResults
                                .get(randomIndex + 1))).getGene();
                        currentNode.runGene(randomBestGene
                                .cross(nextBestResult));
                    }

                } else if (randomOperation <= INVERSION_PROBABILITY) {
                    currentNode.runGene(randomBestGene.invert());
                } else {
                    currentNode.runGene(randomBestGene
                            .mutate(MUTATION_STRENGTH));
                }
            }
        }
    }

    private void checkIfBest(Result pResult) {
        for (int i = 0; i < myBestResults.size(); i++) {
            Result currentResult = (Result) (myBestResults.get(i));
            if (pResult.getUtility() > currentResult.getUtility()) {
                myLogger.debug("Adding "+pResult.getUtility());
                myBestResults.add(i, pResult);
                myLogger.debug("("+(i+1)+" place gene)");
                if (myBestResults.size() > bestGeneSize) {
                    Result removedResult = (Result) (myBestResults
                            .remove(myBestResults.size() - 1));
                    myLogger.debug("Removing"+removedResult.getUtility());
                }
                return;
            }
        }
        if (myBestResults.size() < bestGeneSize) {
            myBestResults.add(pResult);
            myLogger.debug("("+(myBestResults.size())+" place gene)");
            printBestResults();
        }
    }

    private void checkIfWorst(Result pResult) {
        if (myWorstResult == null)
            myWorstResult = pResult;
        else if (pResult.getUtility() < myWorstResult.getUtility())
            myWorstResult = pResult;
    }

    public float[] getUtilitiesReceived() {
        float[] allTicks = new float[utilityReceived.size()];
        for (int i = 0; i < utilityReceived.size(); i++) {
            Float currentFloat = (Float) (utilityReceived.get(i));
            allTicks[i] = currentFloat.floatValue();
        }
        return allTicks;
    }

    private void printBestResults() {
        for (int i = 0; i < myBestResults.size(); i++) {
            Result currentResult = (Result) (myBestResults.get(i));
            myLogger.debug("myBestResults[" + i + "] = "
                    + currentResult.getUtility());
        }
    }

    private void printResult(Result pResult) {
        System.out.print("Utility" + DELIMITER + pResult.getUtility());
        String[] myDescriptors = pResult.getGene().getDescriptors();
        String[] myStringValues = pResult.getGene().getStringValues();
        for (int i = 0; i < myDescriptors.length; i++) {
            String desciption = myDescriptors[i];
            System.out.print(DELIMITER + desciption);
        }
        for (int i = 0; i < myStringValues.length; i++) {
            String value = myStringValues[i];
            System.out.print(DELIMITER + value);
        }
        System.out.println();
    }

    private class PollTask extends TimerTask {
        public void run() {
            pollForNodes();
        }
    }
}