package com.traclabs.biosim.ga.gene;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.InvalidConfigurationException;
import org.jgap.RandomGenerator;
import org.jgap.impl.DoubleGene;

public class AreaDependentGene extends DoubleGene {
	private static final long serialVersionUID = 3811565798702629043L;

	private TotalAreaDependencyGene myDependency;

	public AreaDependentGene(Configuration a_config, double lowerBound,
			TotalAreaDependencyGene dependency)
			throws InvalidConfigurationException {
		super(a_config, lowerBound, lowerBound);
		this.myDependency = dependency;
	}

	public void applyMutation(int index, double a_percentage) {
		double dependencyUpperValue = myDependency.getAreaRemaining();
		double range = (dependencyUpperValue - getLowerBound()) * a_percentage;
		double newValue = doubleValue() + range;
		setAllele(new Double(newValue));
	}

	public void setToRandomValue(RandomGenerator a_numberGenerator) {
		double dependencyUpperValue = myDependency.getAreaRemaining();
		Double areaUsed = new Double((dependencyUpperValue - getLowerBound())
				* a_numberGenerator.nextDouble() + getLowerBound());
		setAllele(areaUsed);
		myDependency.addAreaUsed(areaUsed.floatValue());
	}

	public TotalAreaDependencyGene getDependency() {
		return myDependency;
	}

	public void setDependency(TotalAreaDependencyGene dependency) {
		this.myDependency = dependency;
	}

	protected Gene newGeneInternal() {
		try {
			AreaDependentGene result = new AreaDependentGene(
					getConfiguration(), getLowerBound(), getDependency());
			result.setApplicationData(getApplicationData());
			return result;
		} catch (InvalidConfigurationException iex) {
			throw new IllegalStateException(iex.getMessage());
		}
	}
}
