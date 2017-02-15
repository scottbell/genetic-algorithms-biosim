package com.traclabs.biosim.ga.grid;

import org.homedns.dade.jcgrid.client.GridClient;
import org.homedns.dade.jcgrid.client.GridNodeClientConfig;
import org.homedns.dade.jcgrid.message.GridMessageWorkRequest;
import org.homedns.dade.jcgrid.message.GridMessageWorkResult;

public class ReliabilityClient extends Thread {

	private GridNodeClientConfig myGridClientConfiguration;

	private MyClientFeedback myClientFeedback;

	private ReliabilityRequest myWorkRequest;

	public ReliabilityClient(GridNodeClientConfig configuration, MyClientFeedback feedback,
			ReliabilityRequest request) {
		this.myGridClientConfiguration = configuration;
		this.myClientFeedback = feedback;
		this.myWorkRequest = request;
	}

	public void run() {
		try {
			// Start Client
			GridClient gridClient = new GridClient();
			gridClient.setNodeConfig(myGridClientConfiguration);
			gridClient.start();
			try {
				// Splitting the work
				ReliabilityRequest[] workList;
				workList = myWorkRequest.split();
				myClientFeedback.setProgressMaximum(0);
				myClientFeedback.setProgressMaximum(workList.length - 1);
				myClientFeedback.beginWork();
				// Sending work requests
				for (int i = 0; i < workList.length; i++) {
					ReliabilityRequest req = workList[i];
					myClientFeedback.sendingFragmentRequest(req);
					gridClient.send(new GridMessageWorkRequest(req));
					if (this.isInterrupted())
						break;
				}
				// Receiving work results
				for (int i = 0; i < workList.length; i++) {
					myClientFeedback.setProgressValue(i + workList.length);
					GridMessageWorkResult gridMessageWorkResult = (GridMessageWorkResult) gridClient
							.recv();
					ReliabilityResult workResult = (ReliabilityResult) gridMessageWorkResult.getWorkResult();
					int idx = workResult.getRID();
					myClientFeedback.receivedFragmentResult(workList[idx],
							workResult, idx);
					myClientFeedback.completeFrame(idx);
					if (this.isInterrupted()) {
						break;
					}
				}
			} finally {
				try {
					gridClient.stop();
				} catch (Exception ex) {
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			myClientFeedback.error("Error while doing the work", ex);
		}
		myClientFeedback.endWork();
	}
}
