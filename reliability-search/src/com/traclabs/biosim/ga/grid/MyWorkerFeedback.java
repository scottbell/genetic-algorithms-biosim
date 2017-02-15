package com.traclabs.biosim.ga.grid;

import org.homedns.dade.jcgrid.WorkRequest;
import org.homedns.dade.jcgrid.WorkResult;
import org.homedns.dade.jcgrid.worker.GridWorkerFeedback;


public class MyWorkerFeedback implements GridWorkerFeedback {

	public void start() {
	}

	public void beginWorkingFor(String sessionName, WorkRequest req) {
		System.out.println("Begin work for session " + sessionName);
	}

	public void endWorkingFor(WorkResult result) {
		System.out.println("Result received: " + ((ReliabilityResult) result).getFittest());
	}

	public void stop() {
	}
}
