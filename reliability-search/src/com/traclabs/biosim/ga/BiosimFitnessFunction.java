package com.traclabs.biosim.ga;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.IChromosome;

import com.traclabs.biosim.client.control.BiosimController;
import com.traclabs.biosim.client.util.BioHolder;
import com.traclabs.biosim.client.util.BioHolderInitializer;
import com.traclabs.biosim.server.framework.BiosimServer;
import com.traclabs.biosim.util.OrbUtils;

/**
 * @author Scott Bell
 */

public abstract class BiosimFitnessFunction extends FitnessFunction implements Serializable{
	private static int idCounter = 0;
	
    private static BiosimServer myServer;
    
    protected static BioHolder myBioHolder;
    
    private static BiosimController myController;
    
    private String xmlLocation = null;
    
    private IChromosome myChromosome;
    
    private int myID = idCounter++;
    
    private static Logger myLogger = Logger.getLogger(BiosimFitnessFunction.class);
    
	private static final String OUTPUT_FILE_NAME = "RunResult.txt";
	
	private static PrintStream myPrintStream;

    public BiosimFitnessFunction(String pXMLLocation) {
        //Create BioSim
        xmlLocation = pXMLLocation;
        OrbUtils.startStandaloneNameServer();
        OrbUtils.sleepAwhile();
        OrbUtils.initializeServerForStandalone();
        OrbUtils.initializeClientForStandalone();
        myServer = new BiosimServer(myID, 0, xmlLocation);
        BioHolderInitializer.setFileAndID(myID, xmlLocation);
        myBioHolder = BioHolderInitializer.getBioHolder();
    	myController = createController();
    	myController.collectReferences();
		try {
			myPrintStream = new PrintStream(new FileOutputStream(OUTPUT_FILE_NAME,true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    }
    
    protected abstract BiosimController createController();

	protected abstract int getChromosomeSizeRequired();
    
    public IChromosome getChromosome(){
    	return myChromosome;
    }

	protected double evaluate(IChromosome chromosome) {
        this.myChromosome = chromosome;
        if (notCorrectCardinality(chromosome))
        	return -1;
        myLogger.info(BiosimFitnessFunction.class + " (" + myID
                + "): Received chromosome, starting run...");
		myPrintStream.print("Simulation started, configuration is "+ myChromosome + ',');
		myPrintStream.flush();
        initializeSimulation();
        if (isRunWithControl()){
            myController.runSim();
        }
        else{
            myBioHolder.theBioDriver.setPauseSimulation(true);
            myBioHolder.theBioDriver.startSimulation();
            while (!myBioHolder.theBioDriver.isDone())
            	myBioHolder.theBioDriver.advanceOneTick();
        }
        double utility = calculateUtility();
		myPrintStream.println("Simulation ended, fitness is "+  utility  +  "  Total Number of Ticks is  " + myBioHolder.theBioDriver.getTicks() );
        myPrintStream.println();
		myPrintStream.flush();
        return utility;
	}

    private boolean notCorrectCardinality(IChromosome chromosome) {
        if (chromosome.size() != getChromosomeSizeRequired()) {
            myLogger.error(BiosimFitnessFunction.class + " ("
                            + myID
                            + "): Couldn't run chromosome! Needs a cardinality of "
                            + getChromosomeSizeRequired()
                            + " for its chromosome.  This chromosome had "
                            + chromosome.size());
            return true;
        }
        return false;
	}

	protected abstract double calculateUtility();

    /**
     * Initializes the various servers with data
     */
    protected abstract void initializeSimulation();

    /**
     * @return Returns the runWithControl.
     */
    protected abstract boolean isRunWithControl();
    
    protected float getFloatValueFromAllele(int index){
    	Gene gene = myChromosome.getGene(index);
    	Double allele = (Double)gene.getAllele();
    	return allele.floatValue();
    }
    
    protected int getIntValueFromAllele(int index){
    	 return ((Integer) myChromosome.getGene(index).getAllele()).intValue();
    }
}

