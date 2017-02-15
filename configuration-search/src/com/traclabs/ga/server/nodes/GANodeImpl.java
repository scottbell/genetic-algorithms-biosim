package com.traclabs.ga.server.nodes;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.traclabs.ga.idl.genes.Gene;
import com.traclabs.ga.idl.nodes.GANodePOA;

/**
 * @author Scott Bell
 */

public abstract class GANodeImpl extends GANodePOA {
    
    public Logger myLogger;
    
    private Properties myProperties;
    
    protected int myID = 0;
    
    public GANodeImpl(){
        myLogger = Logger.getLogger(this.getClass());
        myProperties = new Properties();
    }
    
    public void setID(int pID){
        myID = pID;
    }
    
    public int getID(){
        return myID;
    }
    
    public void setProperties(Properties pProperties){
        myProperties = pProperties;
    }
    
    public Properties getProperties(){
        return myProperties;
    }
    
    public abstract void runGene(Gene pGene);

    public abstract boolean isDone();

    protected abstract int getGeneCardinalityRequired();
}

