package com.traclabs.biosim.ga;

import org.jgap.IChromosome;

public class Result {
	private IChromosome chromosome;
	private int evolutionIteration;
	public Result(IChromosome bestSolution, int evolutionIteration) {
		super();
		this.chromosome = bestSolution;
		this.evolutionIteration = evolutionIteration;
	}
	public IChromosome getChromosome() {
		return chromosome;
	}
	public int getEvolutionIteration() {
		return evolutionIteration;
	}
}
