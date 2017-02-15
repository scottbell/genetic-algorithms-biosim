package com.traclabs.biosim.ga.grid;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.homedns.dade.jcgrid.cmd.MainCmd;
import org.homedns.dade.jcgrid.server.GridServer;

public class JGAPServer {

	private static Logger log = Logger.getLogger(JGAPServer.class);

	public JGAPServer(String[] args) throws Exception {
		GridServer gs = new GridServer();
		Options options = new Options();
		CommandLine cmd = MainCmd.parseCommonOptions(options, gs
				.getNodeConfig(), args);
		// Start Server
		gs.start();
	}

	public static void main(String[] args) throws Exception {
		MainCmd.setUpLog4J("server", true);
		// start server
		new JGAPServer(args);
	}
}
