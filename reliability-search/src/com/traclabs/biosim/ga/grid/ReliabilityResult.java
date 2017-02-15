package com.traclabs.biosim.ga.grid;

import org.homedns.dade.jcgrid.WorkResult;
import org.jgap.IChromosome;

public class ReliabilityResult extends WorkResult {
	private static final long serialVersionUID = 7068240651210256922L;

	private IChromosome myFittestChromosome;

	private long myUnitDone;

	public ReliabilityResult(String name, int id, IChromosome fittestChrom,
			long unitdone) {
		super(name, id);
		myFittestChromosome = fittestChrom;
		myUnitDone = unitdone;
	}

	public IChromosome getFittest() {
		return myFittestChromosome;
	}

	public long getUnitDone() {
		return myUnitDone;
	}
}
