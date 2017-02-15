package com.traclabs.biosim.ga.grid;

import org.homedns.dade.jcgrid.WorkRequest;

import com.traclabs.biosim.ga.ReliabilityConfiguration;

public class ReliabilityRequest extends WorkRequest {
	private static final int NUMBER_OF_RUNS = 1000;

	private static final long serialVersionUID = 8904694852739404349L;

	private ReliabilityConfiguration myConfiguration;

	public ReliabilityRequest(String name, int id, ReliabilityConfiguration config) {
		super(name, id);
		this.myConfiguration = config;
	}

	public ReliabilityRequest[] split() throws Exception {
		ReliabilityRequest[] result = new ReliabilityRequest[NUMBER_OF_RUNS];
		for (int i = 0; i < NUMBER_OF_RUNS; i++) {
			result[i] = new ReliabilityRequest("JGAP-Grid Request " + i, i, myConfiguration);
		}
		return result;
	}

	public ReliabilityConfiguration getConfiguration() {
		return myConfiguration;
	}
}
