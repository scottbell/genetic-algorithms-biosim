package com.traclabs.biosim.ga.ui;

import java.util.List;

import org.apache.log4j.Logger;

import ptolemy.plot.PlotLive;

import com.traclabs.biosim.ga.ReliabilityConfiguration;
import com.traclabs.biosim.ga.Result;

public class GaPlotLive extends PlotLive {
	private double sum = 0;

	private double bestFitness = -1;

	private static final long serialVersionUID = -6641434919960094581L;

	private Logger myLogger;

	public GaPlotLive() {
		setPointsPersistence(1000);
		setMarksStyle("dots");
		addLegend(0, "Actual  Fitness");
		addLegend(1, "Average Fitness");
		addLegend(2, "Best Fitness");
		myLogger = Logger.getLogger(this.getClass());
	}

	public synchronized void addPoints() {
		List<Result> bestResultsSoFar = ReliabilityConfiguration
				.getBestSoltionsSoFar();
		while (!bestResultsSoFar.isEmpty()) {
			Result currentResult = bestResultsSoFar.remove(0);
			double fitness = currentResult.getChromosome().getFitnessValue();
			int evolutionIteration = currentResult.getEvolutionIteration();
			sum += fitness;
			double average = sum / (evolutionIteration + 1);
			if (bestFitness <= fitness)
				bestFitness = fitness;
			addPoint(0, evolutionIteration, fitness, true);
			addPoint(1, evolutionIteration, average, true);
			addPoint(2, evolutionIteration, bestFitness, true);
			repaint();
			myLogger.info("adding point with fitness of " + fitness
					+ " and average of " + average);
		}
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {}
	}
}
