package com.traclabs.biosim.ga.grid;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.homedns.dade.jcgrid.client.GridNodeClientConfig;
import org.homedns.dade.jcgrid.cmd.MainCmd;

import com.traclabs.biosim.ga.ReliabilityConfiguration;
import com.traclabs.biosim.ga.ReliabilityConfiguration2;
import com.traclabs.biosim.ga.ReliabilityConfiguration3;
import com.traclabs.biosim.ga.ReliabilityConfiguration4;
import com.traclabs.biosim.ga.ReliabilityConfiguration5;
import com.traclabs.biosim.ga.ReliabilityConfiguration6;
import com.traclabs.biosim.ga.ReliabilityConfiguration.ReliabilityConfigurationType;

public class JGAPClient {
	private static final String OUTPUT_FILE_NAME = "ClientResult.log";

	private static Logger log = Logger.getLogger(JGAPClient.class);

	private GridNodeClientConfig myGridClientConfiguration;

	private PrintStream myPrintStream;
	
	private ReliabilityConfigurationType myConfigurationType;

	public JGAPClient(GridNodeClientConfig config, ReliabilityConfigurationType configurationType) {
		this.myGridClientConfiguration = config;
		this.myConfigurationType = configurationType;
		try {
			myPrintStream = new PrintStream(new FileOutputStream(OUTPUT_FILE_NAME,true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public class RenderingFeedback implements MyClientFeedback {
		public RenderingFeedback() {
		}

		public void error(String msg, Exception ex) {
			System.err.println(msg);
		}

		public void sendingFragmentRequest(ReliabilityRequest req) {
			System.err.println("Sending work");
		}

		public void receivedFragmentResult(ReliabilityRequest req,
				ReliabilityResult res, int idx) {
			System.err.println("Receiving work. Solution " + res.getFittest());
			myPrintStream.println("Receiving work. Solution "+ res.getFittest());
			myPrintStream.flush();
		}

		public void beginWork() {
		}

		public void endWork() {
		}

		public void setProgressMaximum(int max) {
		}

		public void setProgressMinimum(int min) {
		}

		public void setProgressValue(int val) {
		}

		public void setRenderingTime(ReliabilityRequest req, long dt) {
		}

		public void completeFrame(int idx) {
		}

		public void outputResults(ReliabilityResult res) {
		}
	}

	public void doWork(GridNodeClientConfig config, ReliabilityRequest request)
			throws Exception {
		RenderingFeedback renderingFeedback = new RenderingFeedback();
		ReliabilityClient gaClient = new ReliabilityClient(
				myGridClientConfiguration, renderingFeedback, request);
		gaClient.start();
		gaClient.join();
	}

	public static void main(String[] args) {
		try {
			MainCmd.setUpLog4J("client", true);
			GridNodeClientConfig config = new GridNodeClientConfig();
			// Command line options
			Options options = new Options();
			options.addOption("t", true, "Reliability configuration to run");
			CommandLine cmd = MainCmd.parseCommonOptions(options, config, args);
			ReliabilityConfigurationType configurationType = ReliabilityConfigurationType.ONE;
			if (cmd.hasOption("t")){
				try{
					configurationType = ReliabilityConfigurationType.valueOf(cmd.getOptionValue("t"));
				}
				catch(IllegalArgumentException e){}
			}
			System.out.println("using configuration "+configurationType);
			// Setup and start client
			JGAPClient client = new JGAPClient(config, configurationType);
			client.doWork();
			client.finish();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	private void finish() {
		myPrintStream.close();
	}

	public void doWork() throws Exception {
		myGridClientConfiguration.setSessionName("Reliability");
		
		ReliabilityConfiguration myReiliabilityConfiguration = null;;
		if (myConfigurationType.equals(ReliabilityConfigurationType.ONE))
			myReiliabilityConfiguration = new ReliabilityConfiguration();
		else if (myConfigurationType.equals(ReliabilityConfigurationType.TWO))
			myReiliabilityConfiguration = new ReliabilityConfiguration2();
		else if (myConfigurationType.equals(ReliabilityConfigurationType.THREE))
				myReiliabilityConfiguration = new ReliabilityConfiguration3();
		else if (myConfigurationType.equals(ReliabilityConfigurationType.FOUR))
				myReiliabilityConfiguration = new ReliabilityConfiguration4();
		else if (myConfigurationType.equals(ReliabilityConfigurationType.FIVE))
				myReiliabilityConfiguration = new ReliabilityConfiguration5();
		else if (myConfigurationType.equals(ReliabilityConfigurationType.SIX))
				myReiliabilityConfiguration = new ReliabilityConfiguration6();
		// Creating work requests
		ReliabilityRequest req = new ReliabilityRequest(
				myGridClientConfiguration.getSessionName(), 0,
				myReiliabilityConfiguration);
		doWork(myGridClientConfiguration, req);
	}
}
