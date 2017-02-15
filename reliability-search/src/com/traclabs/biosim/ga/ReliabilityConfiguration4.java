package com.traclabs.biosim.ga;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.jgap.Gene;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DoubleGene;
import org.jgap.impl.IntegerGene;

public class ReliabilityConfiguration4 extends ReliabilityConfiguration {
	private static final long serialVersionUID = -5470504978740036284L;

	private static Logger myLogger = Logger
			.getLogger(ReliabilityConfiguration4.class);

	private static List<Result> myResultsSoFar = new Vector<Result>();
	
	@Override
	protected void setupGene() {
		try {
			myGenes[19] = new IntegerGene(this, 0, 8); // Crop Type 1
			myGenes[20] = new DoubleGene(this, 0, 22);    // Crop Area 1
			myGenes[21] = new IntegerGene(this, 0, 8); // Crop Type 2
			myGenes[22] = new DoubleGene(this, 0, 22);    // Crop Area 2
			myGenes[23] = new IntegerGene(this, 0, 8); // Crop Type 3
			myGenes[24] = new DoubleGene(this, 0, 22);    // Crop Area 3
			myGenes[25] = new IntegerGene(this, 0, 8); // Crop Type 4
			myGenes[26] = new DoubleGene(this, 0, 22);    // Crop Area 4
			myGenes[27] = new IntegerGene(this, 0, 8); // Crop Type 5
			myGenes[28] = new DoubleGene(this, 0, 22);    // Crop Area 5
			myGenes[29] = new IntegerGene(this, 0, 8); // Crop Type 6
			myGenes[30] = new DoubleGene(this, 0, 22);    // Crop Area 6
			myGenes[31] = new IntegerGene(this, 0, 8); // Crop Type 7
			myGenes[32] = new DoubleGene(this, 0, 22);    // Crop Area 7
			myGenes[33] = new IntegerGene(this, 0, 8); // Crop Type 8
			myGenes[34] = new DoubleGene(this, 0, 22);    // Crop Area 8
			myGenes[35] = new IntegerGene(this, 0, 8); // Crop Type 9
			myGenes[36] = new DoubleGene(this, 0, 22);    // Crop Area 9
					
		} catch (InvalidConfigurationException e) {
			myLogger.error("Had problems configuring for GA run");
			e.printStackTrace();
		}
		super.setupGene();
	}

	@Override
	protected Gene[] createGeneArray() {
		return new Gene[ReliabilityFitnessFunction4.NEEDED_CHROMOSOME_SIZE];
	}

}
