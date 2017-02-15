package com.traclabs.ga.server.genes;

import com.traclabs.ga.idl.genes.Gene;
import com.traclabs.ga.idl.genes.GenePOA;
import com.traclabs.ga.idl.genes.GeneType;

/**
 * @author Scott Bell
 */

public abstract class GeneImpl extends GenePOA {
    public abstract Gene cross(Gene pGeneToCross);

    public abstract Gene mutate(int pMutationIntensity);

    public abstract Gene invert();

    public abstract int getCardinality();

    public abstract boolean getBoolean(int index);

    public abstract float getFloat(int index);

    public abstract int getInt(int index);

    public abstract String getStringRep();

    public abstract String[] getDescriptors();

    public abstract GeneType[] getGeneTypes();

    public abstract void setValue(int index, String stringValue);

    public abstract String[] getStringValues();
}

