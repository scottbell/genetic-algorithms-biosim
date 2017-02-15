package com.traclabs.biosim.ga;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.jgap.Chromosome;
import org.jgap.Gene;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DoubleGene;
import org.jgap.impl.IntegerGene;

import com.traclabs.biosim.ga.gene.AreaDependentGene;
import com.traclabs.biosim.ga.gene.FixedAreaDependentGene;
import com.traclabs.biosim.ga.gene.TotalAreaDependencyGene;

public class ReliabilityConfiguration3 extends ReliabilityConfiguration {

	private static final long serialVersionUID = -1855701809268299605L;

	private static Logger myLogger = Logger
			.getLogger(ReliabilityConfiguration3.class);

	private static List<Result> myResultsSoFar = new Vector<Result>();
	
	@Override
	protected void setupGene() {
		try {
			myGenes[0] = new TotalAreaDependencyGene(this, 0, 200); // Crop area
			myGenes[0].setApplicationData("Crop Area");
			
			myGenes[1] = new IntegerGene(this, 0, 100); // CO2 level 1 time
			myGenes[1].setApplicationData("CO2 level 1 time");
			myGenes[2] = new DoubleGene(this, 0.01, 0.1); // CO2 level 1 set point
			myGenes[2].setApplicationData("CO2 level 1 set point");
			myGenes[3] = new DoubleGene(this, 0, 2); // CO2 level 1 low rate
			myGenes[3].setApplicationData("CO2 level 1 low rate");
			myGenes[4] = new DoubleGene(this, 0, 1); // CO2 level 1 high rate
			myGenes[4].setApplicationData("CO2 level 1 high rate");

			myGenes[5] = new IntegerGene(this, 250, 350); // CO2 level 2 time
			myGenes[5].setApplicationData("CO2 level 2 time");
			myGenes[6] = new DoubleGene(this, 0.01, 0.1); // CO2 level 2 set point
			myGenes[6].setApplicationData("CO2 level 2 set point");
			myGenes[7] = new DoubleGene(this, 0, 2); // CO2 level 2 low rate
			myGenes[7].setApplicationData("CO2 level 2 low rate");
			myGenes[8] = new DoubleGene(this, 0, 1); // CO2 level 2 high rate
			myGenes[8].setApplicationData("CO2 level 2 high rate");

			myGenes[9] = new IntegerGene(this, 350, 450); // CO2 level 3 time
			myGenes[9].setApplicationData("CO2 level 3 time");
			myGenes[10] = new DoubleGene(this, 0.01, 0.1); // CO2 level 3 set point
			myGenes[10].setApplicationData("CO2 level 3 set");
			
			myGenes[11] = new DoubleGene(this, 0, 2); // CO2 level 3 low rate
			myGenes[11].setApplicationData("CO2 level 3 low rate");
			myGenes[12] = new DoubleGene(this, 0, 1); // CO2 level 3 high rate
			myGenes[12].setApplicationData("CO2 level 3 high rate");

			myGenes[13] = new DoubleGene(this, 20, 30); // O2 set point
			myGenes[13].setApplicationData("O2 set point");
			myGenes[14] = new DoubleGene(this, 0, 10); // O2 low rate
			myGenes[14].setApplicationData("O2 low rate");
			myGenes[15] = new DoubleGene(this, 90, 100); // O2 high rate
			myGenes[15].setApplicationData("O2 high rate");

			myGenes[16] = new DoubleGene(this, 0, 2); // total pressure low rate
			myGenes[16].setApplicationData("total pressure low rate");
			myGenes[17] = new DoubleGene(this, 0, 2); // total pressure high rate
			myGenes[17].setApplicationData("total pressure high rate");

			myGenes[18] = new IntegerGene(this, 0, 21 * 24); // crew arrival date
			myGenes[18].setApplicationData("crew arrival date");
			
			TotalAreaDependencyGene totalAreaDependencyGene = (TotalAreaDependencyGene)(myGenes[0]);
			myGenes[19] = new IntegerGene(this, 0, 8);
			myGenes[19].setApplicationData("Crop Type 1");
			myGenes[20] = new AreaDependentGene(this, 0, totalAreaDependencyGene);    // Crop Area 1
			totalAreaDependencyGene.addDependentGene((AreaDependentGene)myGenes[20]);
			myGenes[20].setApplicationData("Crop Area 1");
			
			myGenes[21] = new IntegerGene(this, 0, 8); // Crop Type 2
			myGenes[21].setApplicationData("Crop Type 2");
			myGenes[22] = new AreaDependentGene(this, 0, totalAreaDependencyGene);    // Crop Area 2
			totalAreaDependencyGene.addDependentGene((AreaDependentGene)myGenes[22]);
			myGenes[22].setApplicationData("Crop Area 2");
			
			myGenes[23] = new IntegerGene(this, 0, 8); // Crop Type 3
			myGenes[23].setApplicationData("Crop Type 3");
			myGenes[24] = new AreaDependentGene(this, 0, totalAreaDependencyGene);    // Crop Area 3
			totalAreaDependencyGene.addDependentGene((AreaDependentGene)myGenes[24]);
			myGenes[24].setApplicationData("Crop Area 3");
			
			myGenes[25] = new IntegerGene(this, 0, 8); // Crop Type 4
			myGenes[25].setApplicationData("Crop Type 4");
			myGenes[26] = new AreaDependentGene(this, 0, totalAreaDependencyGene);    // Crop Area 4
			totalAreaDependencyGene.addDependentGene((AreaDependentGene)myGenes[26]);
			myGenes[26].setApplicationData("Crop Area 4");
			
			myGenes[27] = new IntegerGene(this, 0, 8); // Crop Type 5
			myGenes[27].setApplicationData("Crop Type 5");
			myGenes[28] = new AreaDependentGene(this, 0, totalAreaDependencyGene);    // Crop Area 5
			totalAreaDependencyGene.addDependentGene((AreaDependentGene)myGenes[28]);
			myGenes[28].setApplicationData("Crop Area 5");
			
			myGenes[29] = new IntegerGene(this, 0, 8); // Crop Type 6
			myGenes[29].setApplicationData("Crop Type 6");
			myGenes[30] = new AreaDependentGene(this, 0, totalAreaDependencyGene);    // Crop Area 6
			totalAreaDependencyGene.addDependentGene((AreaDependentGene)myGenes[30]);
			myGenes[30].setApplicationData("Crop Area 6");
			
			myGenes[31] = new IntegerGene(this, 0, 8); // Crop Type 7
			myGenes[31].setApplicationData("Crop Type 7");
			myGenes[32] = new AreaDependentGene(this, 0, totalAreaDependencyGene);    // Crop Area 7
			totalAreaDependencyGene.addDependentGene((AreaDependentGene)myGenes[32]);
			myGenes[32].setApplicationData("Crop Area 7");
			
			myGenes[33] = new IntegerGene(this, 0, 8); // Crop Type 8
			myGenes[33].setApplicationData("Crop Type 8");
			myGenes[34] = new AreaDependentGene(this, 0, totalAreaDependencyGene);    // Crop Area 8
			totalAreaDependencyGene.addDependentGene((AreaDependentGene)myGenes[34]);
			myGenes[34].setApplicationData("Crop Area 8");
			
			myGenes[35] = new IntegerGene(this, 0, 8); // Crop Type 9
			myGenes[35].setApplicationData("Crop Type 9");
			myGenes[36] = new FixedAreaDependentGene(this, 0, totalAreaDependencyGene);    // Crop Area 9
			totalAreaDependencyGene.addDependentGene((AreaDependentGene)myGenes[36]);
			myGenes[36].setApplicationData("Crop Area 9");
			
			Chromosome sampleChromosome = new Chromosome(this, myGenes);
			setSampleChromosome(sampleChromosome);
		} catch (InvalidConfigurationException e) {
			myLogger.error("Had problems configuring for GA run");
			e.printStackTrace();
		}
	}
	
	protected float getCropAreaTotalLowerBound(){
		return 200f;
	}
	
	protected float getCropAreaTotalUpperBound(){
		return 200f;
	}

	@Override
	protected Gene[] createGeneArray() {
		return new Gene[ReliabilityFitnessFunction3.NEEDED_CHROMOSOME_SIZE];
	}

}
