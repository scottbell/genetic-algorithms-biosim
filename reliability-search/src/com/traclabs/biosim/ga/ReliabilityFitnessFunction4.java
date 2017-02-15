package com.traclabs.biosim.ga;

import com.traclabs.biosim.client.control.AnalyticalController4;
import com.traclabs.biosim.client.util.BioHolderInitializer;
import com.traclabs.biosim.idl.simulation.food.PlantType;
import com.traclabs.biosim.idl.simulation.food.Shelf;


public class ReliabilityFitnessFunction4 extends ReliabilityFitnessFunction {
	private static final long serialVersionUID = 7059702764229139010L;
	public static final int NEEDED_CHROMOSOME_SIZE = 37;
	
	public ReliabilityFitnessFunction4(String configurationFile) {
		super(configurationFile);
	}
	
	public ReliabilityFitnessFunction4() {
		super(AnalyticalController4.DEFAULT_CONFIGURATION_FILE);
	}

	@Override
	protected int getChromosomeSizeRequired() {
		return NEEDED_CHROMOSOME_SIZE;
	}
	@Override
	protected void plantShelf(){
		float shelfSizeOne = getFloatValueFromAllele(20);
		float shelfSizeTwo = getFloatValueFromAllele(22);
		float shelfSizeThree = getFloatValueFromAllele(24);
		float shelfSizeFour = getFloatValueFromAllele(26);
		float shelfSizeFive = getFloatValueFromAllele(28);
		float shelfSizeSix = getFloatValueFromAllele(30);
		float shelfSizeSeven = getFloatValueFromAllele(32);
		float shelfSizeEight = getFloatValueFromAllele(34);
		float shelfSizeNine = getFloatValueFromAllele(36);
		
    	PlantType plantTypeOne = PlantType.from_int(getIntValueFromAllele(19));
    	PlantType plantTypeTwo = PlantType.from_int(getIntValueFromAllele(21));
    	PlantType plantTypeThree = PlantType.from_int(getIntValueFromAllele(23));
    	PlantType plantTypeFour = PlantType.from_int(getIntValueFromAllele(25));
    	PlantType plantTypeFive = PlantType.from_int(getIntValueFromAllele(27));
    	PlantType plantTypeSix = PlantType.from_int(getIntValueFromAllele(29));
    	PlantType plantTypeSeven = PlantType.from_int(getIntValueFromAllele(31));
    	PlantType plantTypeEight = PlantType.from_int(getIntValueFromAllele(33));
    	PlantType plantTypeNine = PlantType.from_int(getIntValueFromAllele(35));
    	
    	Shelf shelfOne = BioHolderInitializer.getBioHolder().theBiomassPSModules.get(0).getShelf(0);
    	shelfOne.replant(plantTypeOne, shelfSizeOne);
    	Shelf shelfTwo = BioHolderInitializer.getBioHolder().theBiomassPSModules.get(0).getShelf(1);
    	shelfTwo.replant(plantTypeTwo, shelfSizeTwo);
    	Shelf shelfThree = BioHolderInitializer.getBioHolder().theBiomassPSModules.get(0).getShelf(2);
    	shelfThree.replant(plantTypeThree, shelfSizeThree);
    	Shelf shelfFour = BioHolderInitializer.getBioHolder().theBiomassPSModules.get(0).getShelf(3);
    	shelfFour.replant(plantTypeFour, shelfSizeFour);
    	Shelf shelfFive = BioHolderInitializer.getBioHolder().theBiomassPSModules.get(0).getShelf(4);
    	shelfFive.replant(plantTypeFive, shelfSizeFive);
    	Shelf shelfSix = BioHolderInitializer.getBioHolder().theBiomassPSModules.get(0).getShelf(5);
    	shelfSix.replant(plantTypeSix, shelfSizeSix);
    	Shelf shelfSeven = BioHolderInitializer.getBioHolder().theBiomassPSModules.get(0).getShelf(6);
    	shelfSeven.replant(plantTypeSeven, shelfSizeSeven);
    	Shelf shelfEight = BioHolderInitializer.getBioHolder().theBiomassPSModules.get(0).getShelf(7);
    	shelfEight.replant(plantTypeEight, shelfSizeEight);
    	Shelf shelfNine = BioHolderInitializer.getBioHolder().theBiomassPSModules.get(0).getShelf(8);
    	shelfNine.replant(plantTypeNine, shelfSizeNine);
	}
}