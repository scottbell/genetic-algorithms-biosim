package com.traclabs.biosim.ga;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.DefaultFitnessEvaluator;
import org.jgap.Gene;
import org.jgap.InvalidConfigurationException;
import org.jgap.event.EventManager;
import org.jgap.impl.BestChromosomesSelector;
import org.jgap.impl.ChromosomePool;
import org.jgap.impl.DoubleGene;
import org.jgap.impl.GaussianMutationOperator;
import org.jgap.impl.IntegerGene;
import org.jgap.impl.MutationOperator;
import org.jgap.impl.TwoWayMutationOperator;


import com.traclabs.biosim.ga.util.MersenneRandomGenerator;
import com.traclabs.biosim.util.OrbUtils;


public class ReliabilityConfiguration extends Configuration implements
		Serializable {
	public static enum ReliabilityConfigurationType {ONE, TWO, THREE, FOUR, FIVE, SIX};
	public static int MAX_ALLOWED_EVOLUTIONS = 100;

	private static int POPULATION_SIZE =20;
	
	private static int MUTATION_RATE = 45;

	private static final long serialVersionUID = 9163467124180413946L;

	private static Logger myLogger = Logger
			.getLogger(ReliabilityConfiguration.class);

	private static List<Result> myResultsSoFar = new Vector<Result>();

	protected Gene[] myGenes;

	public ReliabilityConfiguration() {
		initializeLog();
		try {
			long seed = 1000;
			//System.currentTimeMillis();
			setRandomGenerator(new MersenneRandomGenerator(seed));
			setEventManager(new EventManager());
			BestChromosomesSelector bestChromsSelector = new BestChromosomesSelector(
					this, 0.95d);
			bestChromsSelector.setDoubletteChromosomesAllowed(false);
			addNaturalSelector(bestChromsSelector, true);
			setMinimumPopSizePercent(0);
			setKeepPopulationSizeConstant(true);
			setFitnessEvaluator(new DefaultFitnessEvaluator());
			setChromosomePool(new ChromosomePool());
			addGeneticOperator(new GaussianMutationOperator(this, MUTATION_RATE));
			addGeneticOperator(new TwoWayMutationOperator(this, MUTATION_RATE));
			addGeneticOperator(new MutationOperator(this, MUTATION_RATE));
			setPopulationSize(POPULATION_SIZE);
			myGenes = createGeneArray();
			setupGene();

		} catch (InvalidConfigurationException e) {
			myLogger.error("Had problems configuring for GA run");
			e.printStackTrace();
		}
	}
	
	public Gene[] getGenes(){
		return myGenes;
	}

	protected void setupGene() {
		try {
			myGenes[0] = new DoubleGene(this, 0, 200); // Crop area

			myGenes[1] = new IntegerGene(this, 0, 100); // CO2 level 1 time
			myGenes[2] = new DoubleGene(this, 0.01, 0.1); // CO2 level 1 set point
			myGenes[3] = new DoubleGene(this, 0, 2); // CO2 level 1 low rate
			myGenes[4] = new DoubleGene(this, 0, 1); // CO2 level 1 high rate

			myGenes[5] = new IntegerGene(this, 100, 300); // CO2 level 2 time
			myGenes[6] = new DoubleGene(this, 0.01, 0.1); // CO2 level 2 set point
			myGenes[7] = new DoubleGene(this, 0, 2); // CO2 level 2 low rate
			myGenes[8] = new DoubleGene(this, 0, 1); // CO2 level 2 high rate

			myGenes[9] = new IntegerGene(this, 300, 500); // CO2 level 3 time
			myGenes[10] = new DoubleGene(this, 0.01, 0.1); // CO2 level 3 set point												// point
			myGenes[11] = new DoubleGene(this, 0, 2); // CO2 level 3 low rate
			myGenes[12] = new DoubleGene(this, 0, 1); // CO2 level 3 high rate

			myGenes[13] = new DoubleGene(this, 20, 30); // O2 set point
			myGenes[14] = new DoubleGene(this, 0, 10); // O2 low rate
			myGenes[15] = new DoubleGene(this, 90, 100); // O2 high rate49.0,

			myGenes[16] = new DoubleGene(this, 0, 2); // total pressure low rate
			myGenes[17] = new DoubleGene(this, 0, 2); // total pressure high rate

			myGenes[18] = new IntegerGene(this, 0, 504); // crew arrival date
			Chromosome sampleChromosome = new Chromosome(this, myGenes);
			setSampleChromosome(sampleChromosome);
		} catch (InvalidConfigurationException e) {
			myLogger.error("Had problems configuring for GA run");
			e.printStackTrace();
		}
	}
	
	protected float getCropAreaTotalLowerBound(){
		return 0f;
	}
	
	protected float getCropAreaTotalUpperBound(){
		return 200f;
	}


	protected Gene[] createGeneArray() {
		return new Gene[ReliabilityFitnessFunction.NEEDED_CHROMOSOME_SIZE];
	}

	private static void initializeLog() {
		Properties logProps = new Properties();
		logProps.setProperty("log4j.rootLogger", "INFO, rootAppender");
		logProps.setProperty("log4j.appender.rootAppender",
				"org.apache.log4j.ConsoleAppender");
		logProps.setProperty("log4j.appender.rootAppender.layout",
				"org.apache.log4j.PatternLayout");
		logProps.setProperty(
				"log4j.appender.rootAppender.layout.ConversionPattern",
				"%5p [%c] - %m%n");
		PropertyConfigurator.configure(logProps);
	}


	private static boolean doingTests() {
		OrbUtils.initializeLog();
		return false;
	}

	public static List<Result> getBestSoltionsSoFar() {
		return myResultsSoFar;
	}

}
