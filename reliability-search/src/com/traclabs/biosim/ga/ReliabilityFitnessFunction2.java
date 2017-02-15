package com.traclabs.biosim.ga;

import com.traclabs.biosim.client.control.AnalyticalController2;
import com.traclabs.biosim.client.util.BioHolderInitializer;
import com.traclabs.biosim.idl.simulation.food.PlantType;
import com.traclabs.biosim.idl.simulation.food.Shelf;


public class ReliabilityFitnessFunction2 extends ReliabilityFitnessFunction {
	private static final long serialVersionUID = 2569223095012920896L;

	public static final int NEEDED_CHROMOSOME_SIZE = 20;
	
	public ReliabilityFitnessFunction2(String configurationFile) {
		super(configurationFile);
	}
	
	public ReliabilityFitnessFunction2() {
		super(AnalyticalController2.DEFAULT_CONFIGURATION_FILE);
	}

	@Override
	protected int getChromosomeSizeRequired() {
		return NEEDED_CHROMOSOME_SIZE;
	}

	@Override
	protected void plantShelf(){
		float shelfSize = getFloatValueFromAllele(0);
    	PlantType plantType = PlantType.from_int(getIntValueFromAllele(19));
    	Shelf shelf = BioHolderInitializer.getBioHolder().theBiomassPSModules.get(0).getShelf(0);
    	shelf.replant(plantType, shelfSize);
	}
}