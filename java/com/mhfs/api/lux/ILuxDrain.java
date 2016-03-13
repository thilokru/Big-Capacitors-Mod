package com.mhfs.api.lux;

public interface ILuxDrain extends ILuxHandler{

	/**
	 * This is equivalent to the recieveEnergy(face, Integer.MAX_VALUE, true) method call.
	 * @return The amount of energy the drain can accept.
	 */
	public long getNeed();
}
