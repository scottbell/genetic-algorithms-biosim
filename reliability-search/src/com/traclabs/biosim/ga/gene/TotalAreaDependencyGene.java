package com.traclabs.biosim.ga.gene;

import java.util.ArrayList;
import java.util.Collection;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DoubleGene;

public class TotalAreaDependencyGene extends DoubleGene{
	private static final long serialVersionUID = 4637594535493493693L;
	private float myAreaUsed = 0f;
	private Collection<AreaDependentGene> myDependentGenes = new ArrayList<AreaDependentGene>();

	public TotalAreaDependencyGene(Configuration a_config, double lowerBound, double upperBound) throws InvalidConfigurationException {
		super(a_config, lowerBound, upperBound);
	}

	public void addAreaUsed(float areaUsed) {
		myAreaUsed += areaUsed;
	}
	
	public float getAreaRemaining(){
		return (float)(doubleValue() - myAreaUsed);
	}
	
	protected Gene newGeneInternal() {
		try {
			TotalAreaDependencyGene newGene = new TotalAreaDependencyGene(
					getConfiguration(), getLowerBound(), getUpperBound());
			for (AreaDependentGene dependentGene : myDependentGenes) {
				dependentGene.setDependency(newGene);
			}
			newGene.setApplicationData(getApplicationData());
			return newGene;
		} catch (InvalidConfigurationException iex) {
			throw new IllegalStateException(iex.getMessage());
		}
	}
	
	public void addDependentGene(AreaDependentGene gene){
		myDependentGenes.add(gene);
	}
}
