package com.mhfs.api.lux;

import net.minecraft.util.math.BlockPos;

/**
 * This Interface should be implemented on TileEntities. Therefore no method provides an instance
 * of the world.
 * @author Thilo
 *
 */
public interface ILuxHandler extends IRouting{
	
	/**
	 * This method is called when energy should be sent to its destination. This is used e.g to
	 * setup parameters for rendering the "rays" between nodes.
	 * If this Method is called on the destination, it must return. It may accept the power, or,
	 * but it's not recommended, discard it. You must not call this method when amount == 0;
	 * @param lastHop
	 * @param dst
	 * @param amount amount of energy transfered. {@link LuxDrain.getNeed()} etc.
	 */
	public void energyFlow(BlockPos lastHop, BlockPos dst, long amount);

}
