package com.traclabs.biosim.ga.util;

import org.jgap.RandomGenerator;

import com.traclabs.biosim.util.MersenneTwister;

public class MersenneRandomGenerator implements RandomGenerator{
	private static final long serialVersionUID = 5044671546462940584L;
	private MersenneTwister myRandomTwister;
	
	public MersenneRandomGenerator(){
		myRandomTwister = new MersenneTwister();
	}
	
	public MersenneRandomGenerator(long seed){
		myRandomTwister = new MersenneTwister(seed);
	}
	
	public boolean nextBoolean() {
		return myRandomTwister.nextBoolean();
	}

	public double nextDouble() {
		return myRandomTwister.nextDouble();
	}

	public float nextFloat() {
		return myRandomTwister.nextFloat();
	}

	public int nextInt() {
		return myRandomTwister.nextInt();
	}

	public int nextInt(int a_ceiling) {
		return myRandomTwister.nextInt(a_ceiling);
	}

	public long nextLong() {
		return myRandomTwister.nextLong();
	}

}
