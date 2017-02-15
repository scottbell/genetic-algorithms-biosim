package com.traclabs.ga.server.nodes;

import java.util.Timer;
import java.util.TimerTask;

import com.traclabs.biosim.client.control.HandController;
import com.traclabs.biosim.client.util.BioHolder;
import com.traclabs.biosim.client.util.BioHolderInitializer;
import com.traclabs.biosim.server.framework.BiosimServer;
import com.traclabs.ga.idl.genes.Gene;
import com.traclabs.ga.idl.handlers.GAHandler;
import com.traclabs.ga.idl.handlers.GAHandlerHelper;
import com.traclabs.ga.idl.nodes.Result;
import com.traclabs.ga.idl.nodes.ResultHelper;
import com.traclabs.ga.util.OrbUtils;

/**
 * @author Scott Bell
 */

public abstract class BiosimNode extends GANodeImpl {
    protected Gene myGene;

    protected BiosimServer myServer;
    
    protected BioHolder myBioHolder;
    
    private HandController myHandController;

    private static final int POLL_BIOSIM_DELAY = 2000;

    private Timer myBiosimDoneTimer;

    private GAHandler myHandler;

    private boolean hasCollectedReferences = false;

    private boolean biosimDone = false;
    
    private String xmlLocation = null;
    
    private boolean runWithControl = false;

    public BiosimNode(String pXMLLocation) {
        //Create BioSim
        xmlLocation = pXMLLocation;
    }
    
    public void setID(int pID){
        super.setID(pID);
        myServer = new BiosimServer(myID, 0, xmlLocation);
        BioHolderInitializer.setFileAndID(getID(), xmlLocation);
        myBioHolder = BioHolderInitializer.getBioHolder();
    }

    public boolean isDone() {
        return biosimDone;
    }

    public void runGene(Gene pGene) {
        biosimDone = false;
        myGene = pGene;
        myLogger.info("BiosimNodeImpl (" + myID
                + "): Received gene from handler, starting run...");
        if (myGene.getCardinality() != getGeneCardinalityRequired()) {
            myLogger.error("BiosimNodeImpl ("
                            + myID
                            + "): Couldn't run gene!  Node needs a cardinality of "
                            + getGeneCardinalityRequired()
                            + " for its genes.  This gene had "
                            + myGene.getCardinality());
            ResultImpl newResultImpl = new ResultImpl(null, -1);
            myHandler.evaluateResult((Result) (OrbUtils
                    .poaToCorbaObj(newResultImpl)));
            return;
        }
        myBioHolder.theBioDriver.setPauseSimulation(true);
        myBioHolder.theBioDriver.startSimulation();
        initializeSimulation();
        if (runWithControl){
            Thread controllerThread = new Thread(new HandControllerRunner());
            controllerThread.start();
        }
        else{
            myBiosimDoneTimer = new Timer();
            myBiosimDoneTimer.schedule(new PollBiosimTask(), 0, POLL_BIOSIM_DELAY);
            myBioHolder.theBioDriver.setPauseSimulation(false);
        }
    }

    private boolean endConditionMet() {
        return myBioHolder.theBioDriver.isDone();
    }

    private void pollForHandler() {
        try {
            myHandler = GAHandlerHelper.narrow(OrbUtils.getGANamingContext()
                    .resolve_str("GAHandler"));
        } catch (org.omg.CORBA.UserException e) {
            System.err.println("BiosimNodeImpl" + myID
                    + ": Couldn't locate GAHandler, polling...");
            OrbUtils.sleepAwhile();
            OrbUtils.resetInit();
            pollForHandler();
        }
    }

    protected abstract float calculateUtility();

    private void checkIfBiosimDone() {
        if (endConditionMet()) {
            ResultImpl newResultImpl = new ResultImpl(myGene,
                    calculateUtility());
            myBiosimDoneTimer.cancel();
            biosimDone = true;
            pollForHandler();
            myHandler.evaluateResult(ResultHelper.narrow(OrbUtils
                    .poaToCorbaObj(newResultImpl)));
        }

    }

    /**
     * Initializes the various servers with data
     */
    protected abstract void initializeSimulation();

    protected abstract int getGeneCardinalityRequired();

    private class PollBiosimTask extends TimerTask {
        public void run() {
            checkIfBiosimDone();
        }
    }

    /**
     * @return Returns the runWithControl.
     */
    protected boolean isRunWithControl() {
        return runWithControl;
    }
    /**
     * @param runWithControl The runWithControl to set.
     */
    protected void setRunWithControl(boolean pRunWithControl) {
        runWithControl = pRunWithControl;
        if (runWithControl)
            myHandController = new HandController();
    }
    
    private class HandControllerRunner implements Runnable {
        public void run() {
            myHandController.runSim();
            ResultImpl newResultImpl = new ResultImpl(myGene, calculateUtility());
            biosimDone = true;
            pollForHandler();
            myHandler.evaluateResult(ResultHelper.narrow(OrbUtils.poaToCorbaObj(newResultImpl)));
        }
    }
    /**
     * @return Returns the myHandController.
     */
    protected HandController getHandController() {
        return myHandController;
    }
}

