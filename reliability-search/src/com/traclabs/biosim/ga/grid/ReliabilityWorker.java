 package com.traclabs.biosim.ga.grid;

import org.homedns.dade.jcgrid.WorkRequest;
import org.homedns.dade.jcgrid.WorkResult;
import org.homedns.dade.jcgrid.worker.Worker;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.event.EventManager;
import org.jgap.impl.JGAPFactory;

import com.traclabs.biosim.ga.ReliabilityConfiguration;
import com.traclabs.biosim.ga.ReliabilityFitnessFunction;
import com.traclabs.biosim.ga.ReliabilityFitnessFunction2;
import com.traclabs.biosim.ga.ReliabilityFitnessFunction3;
import com.traclabs.biosim.ga.ReliabilityFitnessFunction4;
import com.traclabs.biosim.ga.ReliabilityFitnessFunction5;
import com.traclabs.biosim.ga.ReliabilityFitnessFunction6;
import com.traclabs.biosim.ga.ReliabilityConfiguration.ReliabilityConfigurationType;

public class ReliabilityWorker implements Worker {
	private ReliabilityConfigurationType myConfigurationType;
	private ReliabilityFitnessFunction myReliabilityFitnessFunction;
	
	
	public ReliabilityWorker(ReliabilityConfigurationType configurationType) {
		this.myConfigurationType = configurationType;
		if (myConfigurationType.equals(ReliabilityConfigurationType.ONE))
			myReliabilityFitnessFunction = new ReliabilityFitnessFunction();
		else if (myConfigurationType.equals(ReliabilityConfigurationType.TWO))
			myReliabilityFitnessFunction = new ReliabilityFitnessFunction2();
		else if (myConfigurationType.equals(ReliabilityConfigurationType.THREE))
			myReliabilityFitnessFunction = new ReliabilityFitnessFunction3();
		else if (myConfigurationType.equals(ReliabilityConfigurationType.FOUR))
			myReliabilityFitnessFunction = new ReliabilityFitnessFunction4();
		else if (myConfigurationType.equals(ReliabilityConfigurationType.FIVE))
			myReliabilityFitnessFunction = new ReliabilityFitnessFunction5();
		else if (myConfigurationType.equals(ReliabilityConfigurationType.SIX))
			myReliabilityFitnessFunction = new ReliabilityFitnessFunction6();
	}


	public WorkResult doWork(WorkRequest workRequest, String workDir) throws Exception {
		ReliabilityRequest reliabilityRequest = ((ReliabilityRequest) workRequest);
		ReliabilityConfiguration configuration = reliabilityRequest.getConfiguration();
		ReliabilityConfiguration.reset();
		configuration.setEventManager(new EventManager()); // because it is not serialized!
		configuration.setJGAPFactory(new JGAPFactory(false)); // because it is not serialized!
		configuration.setFitnessFunction(myReliabilityFitnessFunction);
		Genotype genotype = Genotype.randomInitialGenotype(configuration);
		genotype.evolve(ReliabilityConfiguration.MAX_ALLOWED_EVOLUTIONS);
		IChromosome fittest = genotype.getFittestChromosome();
		ReliabilityResult res = new ReliabilityResult(reliabilityRequest.getSessionName(), reliabilityRequest.getRID(), fittest, 1);
		return res;
	}
}
