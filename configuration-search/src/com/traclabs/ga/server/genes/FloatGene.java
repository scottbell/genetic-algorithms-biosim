package com.traclabs.ga.server.genes;

import java.util.Random;

import com.traclabs.ga.idl.genes.Gene;
import com.traclabs.ga.idl.genes.GeneHelper;
import com.traclabs.ga.idl.genes.GeneType;
import com.traclabs.ga.util.OrbUtils;

/**
 * @author Scott Bell
 */

public abstract class FloatGene extends GeneImpl {
    private float[] myFloatValues;

    private Random myRandomGen;

    protected String[] myDescriptors;

    protected GeneType[] myGeneTypes;

    private String[] myStringValues;

    public FloatGene(int pNumberOfValues) {
        myRandomGen = new Random();
        myFloatValues = new float[pNumberOfValues];
        myDescriptors = new String[pNumberOfValues];
        myGeneTypes = new GeneType[pNumberOfValues];
        myStringValues = new String[pNumberOfValues];
        for (int i = 0; i < myFloatValues.length; i++) {
            int randomInt = myRandomGen.nextInt(Integer.MAX_VALUE);
            if (randomInt < 0)
                randomInt *= -1;
            myFloatValues[i] = (float) randomInt;
            myStringValues[i] = new String();
            myDescriptors[i] = new String();
            myGeneTypes[i] = GeneType.NO_TYPE;
        }
    }

    protected FloatGene(float[] pValues) {
        myFloatValues = pValues;
        myRandomGen = new Random();
        myStringValues = new String[pValues.length];
        myDescriptors = new String[pValues.length];
        myGeneTypes = new GeneType[pValues.length];
        for (int i = 0; i < myStringValues.length; i++) {
            myStringValues[i] = new String();
            myDescriptors[i] = new String();
            myGeneTypes[i] = GeneType.NO_TYPE;
        }
    }

    protected float[] getValues() {
        return myFloatValues;
    }

    public int getCardinality() {
        return myFloatValues.length;
    }

    public GeneType[] getGeneTypes() {
        return myGeneTypes;
    }

    public String[] getDescriptors() {
        return myDescriptors;
    }

    public String[] getStringValues() {
        return myStringValues;
    }

    public boolean getBoolean(int index) {
        Float indexFloat = new Float(myFloatValues[index]);
        String indexString = indexFloat.toString();
        int mostSigDigit = Integer.parseInt(indexString.charAt(0) + "");
        if ((mostSigDigit < 0) || (mostSigDigit > 9)) {
            System.err.println("Error deriving boolean!");
            return false;
        } else if (mostSigDigit < 5) {
            return false;
        } else {
            return true;
        }
    }

    public float getFloat(int index) {
        return myFloatValues[index];
    }

    public int getInt(int index) {
        Float indexFloat = new Float(myFloatValues[index]);
        return indexFloat.intValue();
    }

    public void setValue(int index, String pValue) {
        myStringValues[index] = pValue;
    }

    public String getStringRep() {
        StringBuffer outBuffer = new StringBuffer();
        for (int i = 0; i < myFloatValues.length; i++) {
            outBuffer.append("value[" + i + "] = " + myFloatValues[i] + "\n");
        }
        return outBuffer.toString();
    }

    protected abstract FloatGene createGene(float[] pValues);

    private static FloatGene createCrossedGene(FloatGene gene1,
            FloatGene gene2) {
        float[] gene1Values = gene1.getValues();
        float[] gene2Values = gene2.getValues();
        float[] newValues = new float[gene1Values.length];
        for (int i = 0; i < (gene1Values.length / 2); i++) {
            newValues[i] = gene1Values[i];
        }
        for (int i = (gene1Values.length / 2); i < gene2Values.length; i++) {
            newValues[i] = gene2Values[i];
        }
        return gene1.createGene(newValues);
    }

    public Gene cross(Gene pGeneToCross) {
        FloatGene myNewGene = FloatGene.createCrossedGene(this,
                (FloatGene) (OrbUtils.corbaObjToPoa(pGeneToCross)));
        //System.out.println("Cross resulted in: \n"+myNewGene.getStringRep());
        return GeneHelper.narrow(OrbUtils.poaToCorbaObj(myNewGene));
    }

    public Gene mutate(int pMutationIntensity) {
        float[] newValues = new float[myFloatValues.length];
        System.arraycopy(myFloatValues, 0, newValues, 0, myFloatValues.length);
        for (int i = 0; i < pMutationIntensity; i++) {
            int randomIndex = myRandomGen.nextInt(myFloatValues.length);
            int randomNumber = myRandomGen.nextInt(Integer.MAX_VALUE);
            if (randomNumber <= 0)
                randomNumber = 1;
            newValues[randomIndex] = (new Integer(randomNumber)).floatValue();
        }
        FloatGene newFloatGene = this.createGene(newValues);
        //System.out.println("Mutate resulted in:
        // \n"+newFloatGeneImpl.getStringRep());
        return GeneHelper.narrow(OrbUtils.poaToCorbaObj(newFloatGene));
    }

    public Gene invert() {
        float[] newValues = new float[myFloatValues.length];
        for (int i = 0; i < myFloatValues.length; i++) {
            newValues[i] = myFloatValues[myFloatValues.length - 1 - i];
        }
        FloatGene newFloatGene = this.createGene(newValues);
        //System.out.println("Inversion resulted in:
        // \n"+newFloatGeneImpl.getStringRep());
        return GeneHelper.narrow(OrbUtils.poaToCorbaObj(newFloatGene));
    }
}

