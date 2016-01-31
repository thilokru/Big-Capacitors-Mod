package com.mhfs.capacitors.tile.lux;

import com.mhfs.capacitors.misc.BlockPos;

/**
 * This Interface should be implemented on TileEntities. Therefore no method provides an instance
 * of the world.
 * @author Thilo
 *
 */
public interface LuxHandler extends IRouting{
	
	/**
	 * This method is called when energy should be sent to its destination. This is used e.g to
	 * setup parameters for rendering the "rays" between nodes.
	 * If this Method is called on the destination, it must return. It may accept the power, or,
	 * but it's not recommended, discard it.
	 * @param lastHop
	 * @param dst
	 * @param amount amount of energy transfered. {@link LuxDrain.getNeed()} etc.
	 */
	public void energyFlow(BlockPos lastHop, BlockPos dst, long amount);

}
