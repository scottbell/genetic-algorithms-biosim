package com.traclabs.biosim.ga;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.jgap.Gene;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.IntegerGene;

public class ReliabilityConfiguration2 extends ReliabilityConfiguration {

	private static final long serialVersionUID = -1855701809268299605L;

	private static Logger myLogger = Logger
			.getLogger(ReliabilityConfiguration2.class);

	private static List<Result> myResultsSoFar = new Vector<Result>();
	
	@Override
	protected void setupGene() {
		try {
			myGenes[19] = new IntegerGene(this, 0, 8);
		} catch (InvalidConfigurationException e) {
			myLogger.error("Had problems configuring for GA run");
			e.printStackTrace();
		}
		super.setupGene();
	}

	@Override
	protected Gene[] createGeneArray() {
		return new Gene[ReliabilityFitnessFunction2.NEEDED_CHROMOSOME_SIZE];
	}

}
