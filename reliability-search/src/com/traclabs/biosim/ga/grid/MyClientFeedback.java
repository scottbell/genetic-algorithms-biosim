package com.traclabs.biosim.ga.grid;

public interface MyClientFeedback {
	public void setProgressMinimum(int min);

	public void setProgressMaximum(int max);

	public void setProgressValue(int val);

	public void beginWork();

	public void sendingFragmentRequest(ReliabilityRequest req);

	public void receivedFragmentResult(ReliabilityRequest req, ReliabilityResult res, int idx);
	
	public void endWork();

	public void completeFrame(int idx);

	public void error(String msg, Exception ex);
}
