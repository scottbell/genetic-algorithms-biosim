package com.traclabs.ga.server.handlers;

import org.apache.log4j.Logger;

import com.traclabs.ga.util.OrbUtils;

/**
 * The GA Server.
 * 
 * @author Scott Bell
 */

public class GAHandlerServer {
    private Logger myLogger;

    private static final int HANDLER_OA_PORT = 16314;

    public GAHandlerServer() {
        OrbUtils.initializeLog();
        myLogger = Logger.getLogger(this.getClass());
    }

    /**
     * Instantiates the server and binds it to the name server.
     * 
     * @param args
     *            aren't used for anything
     */
    public static void main(String args[]) {
        GAHandlerServer myGAHandlerServer = new GAHandlerServer();
        if (args.length > 0) {
            if (args[0].equals("-debug"))
            {
                Logger.getLogger(GAHandlerServer.class).info("Enabling debug mode");
                OrbUtils.initializeForDebug(HANDLER_OA_PORT);
            }
        }
        myGAHandlerServer.initServer();
    }

    public void initServer() {
        try {
            GAHandlerImpl myGAHandlerImpl = new GAHandlerImpl();
            // bind the Object Reference in Naming
            OrbUtils.getGANamingContext().rebind(
                    OrbUtils.getGANamingContext().to_name("GAHandler"),
                    OrbUtils.poaToCorbaObj(myGAHandlerImpl));
            myGAHandlerImpl.startLookingForNodes();
            // wait for invocations from clients
            OrbUtils.getORB().run();
            System.out.println("GAHandler server ready and waiting ...");
        } catch (org.omg.CORBA.UserException e) {
            System.err
                    .println("GAHandler had problems registering with nameservice, trying again..");
            e.printStackTrace();
            OrbUtils.sleepAwhile();
            initServer();
        }
    }
}

