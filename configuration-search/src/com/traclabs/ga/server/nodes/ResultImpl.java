package com.traclabs.ga.server.nodes;

import com.traclabs.ga.idl.genes.Gene;
import com.traclabs.ga.idl.nodes.ResultPOA;

/**
 * @author Scott Bell
 */

public class ResultImpl extends ResultPOA {
    private Gene myGene;

    private float myUtility;

    public ResultImpl(Gene pGene, float pUtility) {
        myGene = pGene;
        myUtility = pUtility;
    }

    public Gene getGene() {
        return myGene;
    }

    public float getUtility() {
        return myUtility;
    }
}

