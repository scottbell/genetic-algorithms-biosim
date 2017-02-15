package com.traclabs.ga.server.nodes;

import org.apache.log4j.Logger;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import com.traclabs.ga.idl.nodes.GANode;
import com.traclabs.ga.idl.nodes.GANodeHelper;
import com.traclabs.ga.server.framework.GAInitializer;
import com.traclabs.ga.util.OrbUtils;

/**
 * The GA Server.
 * 
 * @author Scott Bell
 */

public class GANodeServer {
    private int numberOfNodes = 1;

    private int numberOfExistingNodes = 0;
    
    private Logger myLogger;
    
    public GANodeServer() {
        OrbUtils.initializeLog();
        myLogger = Logger.getLogger(this.getClass());
    }
    
    private GANodeImpl createNode(int pID){
        String nodeClassName = GAInitializer.getNodeClassName();
        GANodeImpl newGANodeImpl = null;
        try{
            Object newObject = Class.forName(nodeClassName).newInstance();
            if (!(newObject instanceof GANodeImpl)){
            	myLogger.error(nodeClassName + " not of classtype "+ GANodeImpl.class);
            	myLogger.error("Check your configuration file!");
            	return null;
            }
            newGANodeImpl = (GANodeImpl)newObject;
            newGANodeImpl.setID(pID);
            newGANodeImpl.setProperties(GAInitializer.getNodeProperties());
        }
        catch (Exception e){
            e.printStackTrace();
            myLogger.error("Had problems creating node of the type: "+nodeClassName);
            myLogger.error(e);
        }
        return newGANodeImpl;
    }

    /**
     * Instantiates the server and binds it to the name server.
     * 
     * @param args
     *            aren't used for anything
     */
    public static void main(String args[]) {
        GANodeServer myServer = new GANodeServer();
        myServer.registerNodes(args);
    }

    public void registerNodes(String[] myArgs) {
        parseArgs(myArgs);
        findExistingNodes();
        int totalNodes = numberOfNodes + numberOfExistingNodes;
        for (int i = numberOfExistingNodes; i < totalNodes; i++) {
            // create servant and register it with ORB
            int id = i;
            GANodeImpl myGANodeImpl = createNode(id);
            // bind the Object Reference in Naming, ignore if already there
            try {
                NameComponent idComponent = new NameComponent(i + "", "");
                NameComponent[] idComponents = { idComponent };
                OrbUtils.getNodesNamingContext().bind_new_context(idComponents);
            } catch (org.omg.CosNaming.NamingContextPackage.AlreadyBound e) {
            } catch (Exception e) {
                myLogger.error("Problem w/ nameserver...");
                e.printStackTrace();
            }
            try {
                NamingContextExt myIDContext = NamingContextExtHelper
                        .narrow(OrbUtils.getNodesNamingContext().resolve_str(
                                i + ""));
                OrbUtils.getNodeNamingContext(id).rebind(
                        OrbUtils.getNodeNamingContext(id).to_name("GANode"),
                        OrbUtils.poaToCorbaObj(myGANodeImpl));
                myLogger.info("GANode (" + id
                        + ") server ready and waiting ...");
            } catch (Exception e) {
                myLogger.error("Problem w/ creating & registering...");
                e.printStackTrace();
            }

        }
        // wait for invocations from clients
        OrbUtils.getORB().run();
    }

    private void findExistingNodes() {
        int nodeCount = 0;
        try {
            for (nodeCount = 0; true; nodeCount++) {
                GANode currentNode = GANodeHelper.narrow(OrbUtils
                        .getNodeNamingContext(nodeCount).resolve_str("GANode"));
            }
        } catch (org.omg.CORBA.UserException e) {
            numberOfExistingNodes = nodeCount;
            myLogger.info("Found " + numberOfExistingNodes
                    + " existing nodes");
            return;
        } catch (Exception e) {
            e.printStackTrace();
            myLogger.error("Had issues connecting to nameserver, trying again...");
            OrbUtils.resetInit();
            OrbUtils.sleepAwhile();
            findExistingNodes();
        }
        myLogger.info("Found " + numberOfExistingNodes + " existing nodes");
    }

    private void parseArgs(String[] myArgs) {
        if (myArgs.length > 0) {
            try {
                numberOfNodes = Integer.parseInt(myArgs[0]);
            } catch (Exception e) {
                myLogger.error("Problem parsing argument, assuming "
                        + numberOfNodes + " node(s)");
            }
        } else {
            myLogger.info("No arguments, assuming " + numberOfNodes
                    + " node(s)");
        }
    }
}

