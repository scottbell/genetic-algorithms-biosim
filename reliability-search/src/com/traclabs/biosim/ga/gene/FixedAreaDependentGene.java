package com.traclabs.biosim.ga.gene;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.InvalidConfigurationException;
import org.jgap.RandomGenerator;

public class FixedAreaDependentGene extends AreaDependentGene {
	private static final long serialVersionUID = 3811565798702629043L;

	public FixedAreaDependentGene(Configuration a_config, double lowerBound,
			TotalAreaDependencyGene dependency)
			throws InvalidConfigurationException {
		super(a_config, lowerBound, dependency);
	}

	public void setToRandomValue(RandomGenerator a_numberGenerator) {
		setAllele(new Double(getDependency().getAreaRemaining()));
	}
	
	public void applyMutation(int index, double a_percentage) {
		setAllele(new Double(getDependency().getAreaRemaining()));
	}

	protected Gene newGeneInternal() {
		try {
			FixedAreaDependentGene result = new FixedAreaDependentGene(
					getConfiguration(), getLowerBound(), getDependency());
			result.setApplicationData(getApplicationData());
			return result;
		} catch (InvalidConfigurationException iex) {
			throw new IllegalStateException(iex.getMessage());
		}
	}
}
