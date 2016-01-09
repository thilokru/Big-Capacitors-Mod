package com.mhfs.capacitors.tile.lux;

import com.mhfs.capacitors.misc.BlockPos;

public interface LuxHandler {
	
	/**
	 * When a new drain joins the network, it calls this method. It creates a sucction based
	 * routing table, by applying the following roules:
	 * - each connection is equal
	 * - the shortest route must be prefered.
	 * 
	 * Each hop has to call this method on all connected LuxHandlers with the value (the sucction)
	 * being reduced by one.
	 * If a route exists, which is better (higher sucction), this method call returns.
	 * @param world the world this happens in
	 * @param value
	 * @param requester the postion of the drain
	 * @param lastHop the position of the last hop
	 */
	public void drainSetup(BlockPos requester, BlockPos lastHop, int value);
	
	/**
	 * If a node (a router, e.g) joins the network, it needs to retrieve its configuration.
	 * This method call forces the node on which it is called to call the drainSetup method
	 * on the new node based on his best routes.
	 * 
	 * If the new node enables new connections, this allows a fast reconfiguration.
	 * @param requester
	 */
	public void handlerSetupRequest(BlockPos requester);
	
	/**
	 * WARNING! THIS IS STILL WIP!
	 * If a node is destroied (a LuxHandler, e.g), this method must be called. It spreads
	 * the news like the {@link LuxHandler.drainSetup()}. Effectively the whole network should
	 * reconfigure, but this is not working yet. Need more ideas.
	 * @param handler
	 * @param level
	 */
	public void handleDisconnect(BlockPos handler, int level);
	
	/**
	 * This method is called when energy should be sent to its destination. This is used e.g to
	 * setup parameters for rendering the "rays" between nodes.
	 * @param dst
	 * @param amount amount of energy transfered. {@link LuxDrain.getNeed()} etc.
	 */
	public void energyFlow(BlockPos dst, long amount);
}
