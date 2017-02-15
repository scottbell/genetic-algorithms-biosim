package com.traclabs.biosim.ga;

import com.traclabs.biosim.client.control.AnalyticalController1;
import com.traclabs.biosim.client.control.BiosimController;
import com.traclabs.biosim.client.control.EnvironmentController;
import com.traclabs.biosim.client.util.BioHolderInitializer;
import com.traclabs.biosim.idl.simulation.food.Shelf;


public class ReliabilityFitnessFunction extends BiosimFitnessFunction {
	
	private static final long serialVersionUID = -6208267893657430179L;
	
	public static final int NEEDED_CHROMOSOME_SIZE = 19;
	
	protected static EnvironmentController myEnvironmentController;
	
	public ReliabilityFitnessFunction(String configurationFile) {
		super(configurationFile);
	}
	
	public ReliabilityFitnessFunction() {
		super(AnalyticalController1.DEFAULT_CONFIGURATION_FILE);
	}
    
    /**
     * Initializes the various servers with data
     */
    protected void initializeSimulation() {
    	plantShelf();
    	
    	myEnvironmentController.setCO2Segment1Time(getIntValueFromAllele(1));
    	myEnvironmentController.setCO2Segment1SetPoint(getFloatValueFromAllele(2));
    	myEnvironmentController.setCO2Segment1LowRate(getFloatValueFromAllele(3));
    	myEnvironmentController.setCO2Segment1HighRate(getFloatValueFromAllele(4));
    	
    	myEnvironmentController.setCO2Segment2Time(getIntValueFromAllele(5));
    	myEnvironmentController.setCO2Segment2SetPoint(getFloatValueFromAllele(6));
    	myEnvironmentController.setCO2Segment2LowRate(getFloatValueFromAllele(7));
    	myEnvironmentController.setCO2Segment2HighRate(getFloatValueFromAllele(8));
    	
    	myEnvironmentController.setCO2Segment3Time(getIntValueFromAllele(9));
    	myEnvironmentController.setCO2Segment3SetPoint(getFloatValueFromAllele(10));
    	myEnvironmentController.setCO2Segment3LowRate(getFloatValueFromAllele(11));
    	myEnvironmentController.setCO2Segment3HighRate(getFloatValueFromAllele(12));
    	
    	myEnvironmentController.setO2SetPoint(getFloatValueFromAllele(13));
    	myEnvironmentController.setO2LowRate(getFloatValueFromAllele(14));
    	myEnvironmentController.setO2HighRate(getFloatValueFromAllele(15));

    	myEnvironmentController.setTotalPressureLowRate(getFloatValueFromAllele(16));
    	myEnvironmentController.setTotalPressureHighRate(getFloatValueFromAllele(17));
    	
    	BioHolderInitializer.getBioHolder().theCrewGroups.get(0).getCrewPeople()[0].setArrivalTick(getIntValueFromAllele(18));
    }
    

    protected double calculateUtility() {

    	//Mission Length
    	double ticks_moded;
    	float ticks = myBioHolder.theBioDriver.getTicks();
    	float arrivalDate = myBioHolder.theCrewGroups.get(0).getCrewPerson("Nigil").getArrivalTick();
    	float survivalticks= ticks - arrivalDate;
    	if (survivalticks <  0){
    	        ticks_moded= 0;
    	} else {
    	if (survivalticks > 336){
    	//       ticks_moded = (double) (-(5 * survivalticks /24664) + 5.125);  //weighted w/wo volume
    			ticks_moded = (double) (-(10 * survivalticks /24664 )+ 10.25);   //unweighted
    	       	}
    	else{
    	//		ticks_moded= 5 * survivalticks /336;							   //weighted w/wo volume
    	        ticks_moded= 10 * survivalticks /336;							   //unweighted
    		}
    	}

    	//Crop Area
    	double cropArea_moded; 
    	if (survivalticks < 0) {
    		cropArea_moded = 0;
    	}	
    	else {
    	//For Configuration 1 and 2, use the following crop area
    	//double cropArea = myBioHolder.theBiomassPSModules.get(0).getShelf(0).getCropAreaUsed();
    	//For Configuration 4 and 5, use the the following crop area
    	double cropArea= (getFloatValueFromAllele(20) + getFloatValueFromAllele(22) + getFloatValueFromAllele(24) + getFloatValueFromAllele(26) + getFloatValueFromAllele(28) + getFloatValueFromAllele(30) + getFloatValueFromAllele(32) + getFloatValueFromAllele(34) + getFloatValueFromAllele(36));
    	
    	//cropArea_moded= (double) (-2.5*cropArea/200 + 2.5);                 //weighted without volume
    	//cropArea_moded= (double) (-10*cropArea/1200 + 10/6);         //weighted with volume
    	cropArea_moded= (double) (-10*cropArea/200 + 10);               			      //unweighted
    	}
    	
    	//Crew Arrival
    	double arrivalDate_moded;
    	if (survivalticks < 0) {
    	arrivalDate_moded = 0;
    	}	else {
    	//arrivalDate_moded= (double) (-2.5* arrivalDate/504 + 2.5);  	      //weighted without volume
    	//arrivalDate_moded= (double) (-10* arrivalDate/3024 + 10/6);  //weighted with volume
    	arrivalDate_moded= (-10* arrivalDate/504 + 10);				  //unweighted
    	}
    	
    	//Volume
    	double volume_moded;
    	//for configuration 1,2,4, 
    	//volume_moded=0;
    	//for configuration 5
    	if (survivalticks < 0) {
        	volume_moded = 0;
        	}	else {
    	double height = getFloatValueFromAllele(37);
    	double cropArea= (getFloatValueFromAllele(20) + getFloatValueFromAllele(22) + getFloatValueFromAllele(24) + getFloatValueFromAllele(26) + getFloatValueFromAllele(28) + getFloatValueFromAllele(30) + getFloatValueFromAllele(32) + getFloatValueFromAllele(34) + getFloatValueFromAllele(36));
    	double volume = height * cropArea;    	
    	if (volume < 20){
    		volume_moded=0;
    		}
    	if (volume > 50 ){
    	//	volume_moded = 11.11/6 - ( volume /270 ) ;  //weighted 
    	    volume_moded = 11.11 - ( volume /45 ) ;       //not weighted
    	}
    	if (volume >500){
    		volume_moded=0;
    		}
    	else  {
    	//	volume_moded=10/6;                        //weighted
    	    volume_moded=10;                              //not weighted
    		  }
    	}
		
    	double fitness = ticks_moded + cropArea_moded + arrivalDate_moded +volume_moded;
    	
    	//double fitness = ticks_moded + cropArea_moded + arrivalDate_moded;
    	
    	return fitness;
    	
    }
    
	@Override
	protected int getChromosomeSizeRequired() {
		return NEEDED_CHROMOSOME_SIZE;
	}

	@Override
	protected BiosimController createController() {
		myEnvironmentController = new EnvironmentController();
		return myEnvironmentController;
	}

	@Override
	protected boolean isRunWithControl() {
		return true;
	}
	
	protected void plantShelf(){
    	Shelf shelf = BioHolderInitializer.getBioHolder().theBiomassPSModules.get(0).getShelf(0);
    	shelf.replant(shelf.getCropType(), getFloatValueFromAllele(0));
	}
	
}
