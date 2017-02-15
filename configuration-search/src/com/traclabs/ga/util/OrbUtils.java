package com.traclabs.ga.util;

import java.io.File;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jacorb.naming.NameServer;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;


/**
 * The OrbUtils class provides basic CORBA utilities to server components
 * 
 * @author Scott Bell
 */

public class OrbUtils {
    //Flag to make sure OrbUtils only runs initialize once
    private static boolean initializeOrbRunOnce = false;

    //The root POA for transformation methods and other things
    private static POA myRootPOA = null;

    //The server ORB used resolving references
    private static ORB myOrb = null;

    //The root biosim naming context reference
    private static NamingContextExt myNodesNamingContext = null;

    private static NamingContextExt myGANamingContext = null;

    private static NamingContextExt myRootContext = null;
    
    private static final int DEBUG_NAMESERVER_PORT = 16309;
    
    private static final int DEBUG_SERVER_OA_PORT = 16310;

    private static final int DEBUG_CLIENT_OA_PORT = 16311;

    private static Properties myORBProperties;

    /**
     * Shouldn't be called (everything static!)
     */
    private OrbUtils() {
    }

    /**
     * Returns the ORB
     * 
     * @return the ORB
     */
    public static ORB getORB() {
        initialize();
        return myOrb;
    }

    /**
     * Returns the root POA
     * 
     * @return the root POA
     */
    public static POA getRootPOA() {
        initialize();
        return myRootPOA;
    }

    /**
     * Returns the naming context associated with this ID
     * 
     * @return the naming context
     */
    public static NamingContextExt getGANamingContext() {
        initialize();
        return myGANamingContext;
    }

    /**
     * Returns the naming context associated with this ID
     * 
     * @return the naming context
     */
    public static NamingContextExt getBiosimNamingContext(int pID) {
        initialize();
        NamingContextExt idContext = null;
        try {
            NamingContextExt comContext = NamingContextExtHelper
                    .narrow(myRootContext.resolve_str("com"));
            NamingContextExt traclabsContext = NamingContextExtHelper
                    .narrow(comContext.resolve_str("traclabs"));
            NamingContextExt myBiosimNamingContext = NamingContextExtHelper
                    .narrow(traclabsContext.resolve_str("biosim"));
            idContext = NamingContextExtHelper.narrow(myBiosimNamingContext
                    .resolve_str("" + pID));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return idContext;
    }

    /**
     * Returns the naming context associated with this ID
     * 
     * @return the naming context
     */
    public static NamingContextExt getNodeNamingContext(int pID)
            throws org.omg.CORBA.UserException, Exception {
        initialize();
        NamingContextExt myIDContext = NamingContextExtHelper
                .narrow(myNodesNamingContext.resolve_str(pID + ""));
        return myIDContext;
    }

    /**
     * Returns the naming context associated with this ID
     * 
     * @return the naming context
     */
    public static NamingContextExt getNodesNamingContext() {
        initialize();
        return myNodesNamingContext;
    }

    /**
     * Forces OrbUtils to retrieve the RootPoa and Naming Service again on next
     * request.
     */
    public static void resetInit() {
        initializeOrbRunOnce = false;
    }
    
    private static void initialize(){
        while (!initializeLoop()){
            sleepAwhile();
        }
    }

    /**
     * Done only once, this method initializes the ORB, resolves the root POA,
     * and grabs the naming context.
     */
    private static boolean initializeLoop() {
        if (initializeOrbRunOnce)
            return true;
        try {
            String[] nullArgs = null;
            // create and initialize the ORB
            myOrb = ORB.init(nullArgs, myORBProperties);
            // get reference to rootpoa & activate the POAManager
            myRootPOA = POAHelper.narrow(myOrb
                    .resolve_initial_references("RootPOA"));
            myRootPOA.the_POAManager().activate();

            //Attempt to create com.traclabs context, if already there, don't
            // bother
            myRootContext = NamingContextExtHelper.narrow(myOrb
                    .resolve_initial_references("NameService"));
            NameComponent comComponent = new NameComponent("com", "");
            NameComponent[] comComponentArray = { comComponent };
            myRootContext.bind_new_context(comComponentArray);

            NamingContextExt comContext = NamingContextExtHelper
                    .narrow(myRootContext.resolve_str("com"));
            NameComponent traclabsComponent = new NameComponent("traclabs", "");
            NameComponent[] traclabsComponentArray = { traclabsComponent };
            comContext.bind_new_context(traclabsComponentArray);
        } catch (org.omg.CosNaming.NamingContextPackage.AlreadyBound e) {
        } catch (Exception e) {
            Logger.getLogger(OrbUtils.class).info("OrbUtils: nameserver not found, polling again: "+e);
            return false;
        }

        //Attempt to create ga context, if already there, don't bother
        try {
            NamingContextExt comContext = NamingContextExtHelper
                    .narrow(myRootContext.resolve_str("com"));
            NamingContextExt traclabsContext = NamingContextExtHelper
                    .narrow(comContext.resolve_str("traclabs"));
            NameComponent gaComponent = new NameComponent("ga", "");
            NameComponent[] gaComponentArray = { gaComponent };
            traclabsContext.bind_new_context(gaComponentArray);
        } catch (org.omg.CosNaming.NamingContextPackage.AlreadyBound e) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            NamingContextExt comContext = NamingContextExtHelper
                    .narrow(myRootContext.resolve_str("com"));
            NamingContextExt traclabsContext = NamingContextExtHelper
                    .narrow(comContext.resolve_str("traclabs"));
            myGANamingContext = NamingContextExtHelper.narrow(traclabsContext
                    .resolve_str("ga"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //Attempt to create nodes context, if already there, don't bother
            NameComponent nodesComponent = new NameComponent("nodes", "");
            NameComponent[] nodesComponentArray = { nodesComponent };
            myGANamingContext.bind_new_context(nodesComponentArray);
        } catch (org.omg.CosNaming.NamingContextPackage.AlreadyBound e) {
        } catch (Exception e) {
            Logger.getLogger(OrbUtils.class).info("OrbUtils: nameserver not found, polling again: "+e);
            return false;
        }

        try {
            myNodesNamingContext = NamingContextExtHelper
                    .narrow(myGANamingContext.resolve_str("nodes"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        initializeOrbRunOnce = true;
        return true;
    }

    /**
     * Sleeps for a few seconds. Used when we can't find the naming service and
     * need to poll again after a few seconds.
     */
    public static void sleepAwhile() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
    }

    /**
     * This method take a POA servant object and transforms it into a CORBA
     * object
     * 
     * @param poa
     *            the POA object to transform
     * @return the transformed CORBA object
     */
    public static org.omg.CORBA.Object poaToCorbaObj(
            org.omg.PortableServer.Servant poa) {
        org.omg.CORBA.Object newObject = null;
        try {
            initialize();
            newObject = myRootPOA.servant_to_reference(poa);
        } catch (org.omg.PortableServer.POAPackage.ServantNotActive e) {
            e.printStackTrace();
        } catch (org.omg.PortableServer.POAPackage.WrongPolicy e) {
            e.printStackTrace();
        }
        return newObject;
    }

    /**
     * This method take a CORBA object and transforms it into a POA servant
     * object
     * 
     * @param pObject
     *            the CORBA object to transform
     * @return the transformed POA servant object
     */
    public static org.omg.PortableServer.Servant corbaObjToPoa(
            org.omg.CORBA.Object pObject) {
        org.omg.PortableServer.Servant newPoa = null;
        try {
            initialize();
            newPoa = myRootPOA.reference_to_servant(pObject);
        } catch (org.omg.PortableServer.POAPackage.ObjectNotActive e) {
            e.printStackTrace();
        } catch (org.omg.PortableServer.POAPackage.WrongPolicy e) {
            e.printStackTrace();
        } catch (org.omg.PortableServer.POAPackage.WrongAdapter e) {
            e.printStackTrace();
        }
        return newPoa;
    }
    /**
     * @return Returns the myRootContext.
     */
    public static NamingContextExt getRootContext() {
        initialize();
        return myRootContext;
    }
    
    /**
     * @param pORBProperties
     *            The myORBProperties to set.
     */
    public static void setMyORBProperties(Properties pORBProperties) {
        myORBProperties = pORBProperties;
    }
    
    public static void initializeServerForDebug(){
        initializeForDebug(DEBUG_SERVER_OA_PORT);
    }
    
    public static void initializeClientForDebug(){
        initializeForDebug(DEBUG_CLIENT_OA_PORT);
    }
    
    public static void initializeForDebug(int OAPort){
        Properties newORBProperties = new Properties();
        newORBProperties.setProperty("OAPort", Integer.toString(OAPort));
        newORBProperties.setProperty("ORBInitRef.NameService", "corbaloc::localhost:" + DEBUG_NAMESERVER_PORT + "/NameService");
        setMyORBProperties(newORBProperties);
        resetInit();
    }
    
    public static void startDebugNameServer(){
        Thread myNamingServiceThread = new Thread(new NamingServiceThread());
        myNamingServiceThread.start();
    }
    
    public static void initializeLog(){
        Properties logProps = new Properties();
        logProps.setProperty("log4j.rootLogger", "INFO, rootAppender");
        logProps.setProperty("log4j.appender.rootAppender",
                "org.apache.log4j.ConsoleAppender");
        logProps.setProperty("log4j.appender.rootAppender.layout",
                "org.apache.log4j.PatternLayout");
        logProps.setProperty(
                "log4j.appender.rootAppender.layout.ConversionPattern",
                "%5p [%c] - %m%n");
        PropertyConfigurator.configure(logProps);
    }
    
    private static class NamingServiceThread implements Runnable {
        public void run() {
            String[] portArgs = {"-DOAPort="+Integer.toString(DEBUG_NAMESERVER_PORT)};
            NameServer.main(portArgs);
        }
    }
    
    public static String resolveXMLLocation(String xmlLocation) {
    	if (xmlLocation == null)
    		return null;
    	//first see if we can find it in the classpath
    	String fullFileLocation = findXmlInClasspath(xmlLocation);
    	if (fullFileLocation != null)
    		return fullFileLocation;
        //next look for it as a raw file
        File xmlFile = new File(xmlLocation);
        if (xmlFile.exists())
        	return xmlFile.toString();
        //maybe it's in the classpath, but user forgot default location
    	fullFileLocation = findXmlInClasspath("com/traclabs/ga/server/framework" + xmlLocation);
    	if (fullFileLocation != null)
    		return fullFileLocation;
        //give up
        return null;
	}
    
    private static String findXmlInClasspath(String xmlLocation){
    	Logger.getLogger(OrbUtils.class).debug("Looking for "+xmlLocation+" in classpath");
        URL foundURL = OrbUtils.class.getClassLoader().getResource(xmlLocation);
        if (foundURL != null){
        	String urlString = foundURL.toString();
        	if (urlString.length() > 0)
        		return urlString;
        }
        return null;
    }
}