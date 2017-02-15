package com.traclabs.ga.server.nodes.reliability;

import com.traclabs.biosim.idl.simulation.environment.SimEnvironment;
import com.traclabs.biosim.idl.simulation.framework.SingleFlowRateControllable;
import com.traclabs.biosim.idl.simulation.framework.Store;
import com.traclabs.ga.server.nodes.BiosimNode;

/**
 * @author Scott Bell
 */

public class ReliabilitySizingNode extends BiosimNode {
    private Thread myServerMonitor;

    private static final int NEEDED_GENE_CARDINALITY = 17;

    private static final String XML_LOCATION = "com/traclabs/biosim/server/framework/configuration/reliability/config.xml";

    private boolean hasCollectedReferences = false;

    private String[] myStringReps;

	private float[] myWeights;

	private float[] myMaxes;
	
	private float myMaxesSum = 0f;

    public ReliabilitySizingNode() {
        super(XML_LOCATION);
        myStringReps = new String[NEEDED_GENE_CARDINALITY];
        myMaxes = new float[NEEDED_GENE_CARDINALITY];
        myWeights = new float[NEEDED_GENE_CARDINALITY];
    }

    protected int getGeneCardinalityRequired() {
        return NEEDED_GENE_CARDINALITY;
    }
    
    private void initializeStore(int capacityGeneIndex, Store store){
        float storeCapacity = myGene.getFloat(capacityGeneIndex) % store.getInitialCapacity();
        store.setCurrentCapacity(storeCapacity);
        myStringReps[capacityGeneIndex] = Float.toString(storeCapacity);
        initializeMax(capacityGeneIndex, store.getInitialCapacity());
        
        float storeLevel = myGene.getFloat(capacityGeneIndex + 1)% storeCapacity;
        myStringReps[capacityGeneIndex + 1] = Float.toString(storeLevel);
        store.setCurrentLevel(storeCapacity);
        initializeMax(capacityGeneIndex + 1, store.getInitialLevel());
    }
    
    private void initializeFlowrate(int flowrateGeneIndex, SingleFlowRateControllable flowrateControllable){
        float flowrate = myGene.getFloat(flowrateGeneIndex) % flowrateControllable.getMaxFlowRate(0);
        myStringReps[flowrateGeneIndex] = Float.toString(flowrate);
        flowrateControllable.setDesiredFlowRate(flowrate, 0);
        initializeMax(flowrateGeneIndex, flowrateControllable.getMaxFlowRate(0));
    }
    
    private void initializeEnvironment(int environmentGeneIndex, SimEnvironment environment){
        float environmentVolume = myGene.getFloat(environmentGeneIndex) % environment.getInitialVolume();
        environment.setCurrentVolumeAtSeaLevel(environmentVolume);
        myStringReps[environmentGeneIndex] = Float.toString(environmentVolume);
        initializeMax(environmentGeneIndex, environment.getInitialVolume());
    }
    
    /**
     * Initializes the various servers with data
     */
    protected void initializeSimulation() {
        //reset servers
    	myBioHolder.theBioDriver.reset();
    	
    	initializeWeights();

    	initializeStore(0, myBioHolder.theDirtyWaterStores.get(0));
    	initializeStore(2, myBioHolder.thePotableWaterStores.get(0));
    	initializeStore(4, myBioHolder.theGreyWaterStores.get(0));
    	initializeStore(4, myBioHolder.theGreyWaterStores.get(0));
    	initializeStore(6, myBioHolder.theO2Stores.get(0));
    	initializeStore(8, myBioHolder.theFoodStores.get(0));
    	initializeStore(10, myBioHolder.thePowerStores.get(0));
        
    	initializeEnvironment(12, myBioHolder.theSimEnvironments.get(0));
        
        initializeFlowrate(13, myBioHolder.theWaterRSModules.get(0).getPowerConsumerDefinition());
        initializeFlowrate(14, myBioHolder.theVCCRModules.get(0).getPowerConsumerDefinition());
        initializeFlowrate(15, myBioHolder.theOGSModules.get(0).getPowerConsumerDefinition());
        initializeFlowrate(16, myBioHolder.theInjectors.get(0).getO2ProducerDefinition());
       
        for (int i = 0; i < myStringReps.length; i++){
        	if (myStringReps[i] == null){
        		myLogger.warn("myStringReps["+i+"] is null!");
        		myLogger.warn("setting it to an empty string");
        		myStringReps[i] = new String();
        	}
            myGene.setValue(i, myStringReps[i]);
        }
        
        
        myMaxesSum = calculateArraySum(myMaxes);
    }
    
    /**
     *  
     */
    private void initializeWeights() {
        //first 6 are all water
        for (int i = 0; i < 6; i++)
        	myWeights[i] = 1.063733f;
        
        //O2 Store Capacity
        myWeights[6] = 0.00108770f;
        //O2 Store Level
        myWeights[7] = 0.03200000f;
        
        //Food Store Capacity
        myWeights[8] = 2.36000000f;
        //Food Store Level
        myWeights[9] = 1.0f;
        
        //Power Store Capacity
        myWeights[10] = 0.68700000f;
        //Power Store Level
        myWeights[11] = 0.0f;
        
        //Environment Capacity
        myWeights[12] = 0.1331f;
        
        //WaterRS Capacity
        myWeights[13] = 0.25624732f;
        
        //VCCR Capacity
        myWeights[14] = 0.33278798f;
        
        //OGS Capacity
        myWeights[15] = 0.13785374f;
        
        //Injector Capacity
        myWeights[16] = 0.00021060f;
    }
    
    private static float calculateArraySum(float[] pArray) {
        float total = 0f;
        for (int i = 0; i < pArray.length; i++)
            total += pArray[i];
        return total;
    }
    
    private void initializeMax(int geneIndex, float max){
    	myMaxes[geneIndex] = myWeights[geneIndex] * max;
    }
    
    protected float calculateUtility() {
        float totalValues = 0f;
        String[] values = myGene.getStringValues();
        for (int i = 1; i < getGeneCardinalityRequired(); i++) {
            totalValues += Float.parseFloat(values[i]) * myWeights[i];
        }
        float valueProportion = 1 - (totalValues / myMaxesSum);
        int ticks = myBioHolder.theBioDriver.getTicks();
        float utility = valueProportion + ticks;
        myLogger.debug("ticks = " + ticks);
        myLogger.debug("utility = " + utility);
        return utility;
    }
}

