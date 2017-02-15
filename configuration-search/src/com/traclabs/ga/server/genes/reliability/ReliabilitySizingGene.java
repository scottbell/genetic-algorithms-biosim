package com.traclabs.ga.server.genes.reliability;

import com.traclabs.ga.idl.genes.GeneType;
import com.traclabs.ga.server.genes.FloatGene;

/**
 * @author Scott Bell
 */

public class ReliabilitySizingGene extends FloatGene {
    public ReliabilitySizingGene() {
        super(17);
        setDescriptors();
        setGeneTypes();
    }

    public ReliabilitySizingGene(float[] pValues) {
        super(pValues);
        setDescriptors();
        setGeneTypes();
    }

    protected FloatGene createGene(float[] pValues) {
        return new ReliabilitySizingGene(pValues);
    }

    private void setDescriptors() {
        myDescriptors[0] = "Dirty Water Store Capacity";
        myDescriptors[1] = "Dirty Water Store Level";
        myDescriptors[2] = "Potable Water Store Capacity";
        myDescriptors[3] = "Potable Water Store Level";
        myDescriptors[4] = "Grey Store Capacity";
        myDescriptors[5] = "Grey Store Level";
        myDescriptors[6] = "O2 Store Capacity";
        myDescriptors[7] = "O2 Store Level";
        myDescriptors[8] = "Food Store Capacity";
        myDescriptors[9] = "Food Store Level";
        myDescriptors[10] = "Power Store Capacity";
        myDescriptors[11] = "Power Store Level";
        myDescriptors[12] = "Environment Capacity";
        myDescriptors[13] = "WaterRS Power Input";
        myDescriptors[14] = "VCCR Power Input";
        myDescriptors[15] = "OGS Power Input";
        myDescriptors[16] = "Injector Oxygen Output";
    }

    private void setGeneTypes() {
    	for (int i = 0; i < myGeneTypes.length; i++)
    		myGeneTypes[i] = GeneType.INT_TYPE;
    }
}

