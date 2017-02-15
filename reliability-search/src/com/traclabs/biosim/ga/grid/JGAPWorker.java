package com.traclabs.biosim.ga.grid;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.homedns.dade.jcgrid.GridNodeGenericConfig;
import org.homedns.dade.jcgrid.cmd.MainCmd;
import org.homedns.dade.jcgrid.worker.GridNodeWorkerConfig;
import org.homedns.dade.jcgrid.worker.GridWorker;

import com.traclabs.biosim.ga.ReliabilityConfiguration.ReliabilityConfigurationType;

public class JGAPWorker {
	private static Logger myLogger = Logger.getLogger(JGAPWorker.class);

	public JGAPWorker(GridNodeWorkerConfig a_config, ReliabilityConfigurationType configurationType) throws Exception {
		// Start all required Workers

		GridWorker[] gw = new GridWorker[a_config.getWorkerCount()];
		for (int i = 0; i < a_config.getWorkerCount(); i++) {
			gw[i] = new GridWorker();
			gw[i].setNodeConfig((GridNodeGenericConfig) a_config.clone());
			((GridNodeGenericConfig) gw[i].getNodeConfig())
					.setSessionName(a_config.getSessionName() + "_" + i);
			((GridNodeGenericConfig) gw[i].getNodeConfig())
					.setWorkingDir(a_config.getWorkingDir() + "_" + i);
			gw[i].setWorker(new ReliabilityWorker(configurationType));
			gw[i].setWorkerFeedback(new MyWorkerFeedback());
			gw[i].start();
		}
		// Wait shutdown

		for (int i = 0; i < a_config.getWorkerCount(); i++)
			gw[i].waitShutdown();
	}

	public static void main(String[] args) throws Exception {
		MainCmd.setUpLog4J("worker", true);
		GridNodeWorkerConfig config = new GridNodeWorkerConfig();
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
		// start worker
		new JGAPWorker(config, configurationType);
	}
}
