package com.mhfs.api.lux;

import java.util.Set;

import net.minecraft.util.math.BlockPos;

public interface ILuxHandler{
	
	/**
	 * This method is called when energy should be sent to its destination. This is used e.g to
	 * setup parameters for rendering the "rays" between nodes.
	 * If this Method is called on the destination, it must return. It may accept the power, or,
	 * but it's not recommended, discard it. You must not call this method when amount == 0;
	 * If lastHop is null, the local tile is starting the transmission.
	 * @param lastHop
	 * @param dst
	 * @param amount amount of energy transfered. {@link LuxDrain.getNeed()} etc.
	 */
	public void energyFlow(BlockPos lastHop, BlockPos dst, long amount);
	
	/**
	 * This is equivalent to the recieveEnergy(face, Integer.MAX_VALUE, true) method call.
	 * @return The amount of energy the drain can accept.
	 */
	public long getNeed();
	
	/**
	 * @return all currently active connections
	 */
	public Set<BlockPos> getActive();

	/**
	 * Resets the connection states. Should be called each tick.
	 */
	public void resetActive();
	
	//Methods for serialization, therefore deprecated!
	
	/**
	 * @deprecated as it should only be used for serialization
	 * @return all currently active connections
	 */
	@Deprecated
	public BlockPos[] getActiveConnections();
	
	/**
	 * @deprecated as it should only be used for serialization
	 * @param active the active connection to be added
	 */
	@Deprecated
	public void addActiveConnection(BlockPos active);
}
