package com.mhfs.api.lux;

import com.mhfs.capacitors.misc.BlockPos;

public interface IRouting {

	/**
	 * When a new drain joins the network, it calls this method. It creates a sucction based
	 * routing table, by applying the following roules:
	 * - each connection is equal
	 * - the shortest route must be prefered.
	 * 
	 * Each hop has to call this method the next tick on all connected LuxHandlers with the value (the sucction)
	 * being reduced by one.
	 * If a route exists, which is better (higher sucction), this method call returns.
	 * 
	 * The {@link LuxHandler.handlerDisconnect()} method will clear the routing table. This requires
	 * the drains to call this method regularly (e.g. every 20 ticks)
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
	 * If a node is destroied (a LuxHandler, e.g), this method must be called. It spreads
	 * the news like the {@link LuxHandler.drainSetup()}. Effectively the whole network should
	 * reconfigure, but this is not working yet. Need more ideas.
	 * @param handler
	 * @param level
	 */
	public void handleDisconnect(BlockPos handler, int level);
	
	/**
	 * This method can be called on a router to connect to the router at x,y,z.
	 * After setting up, the method will be called on the foreign router, which must
	 * return if the connection is already configured.
	 * @param x
	 * @param y
	 * @param z
	 * @param handleIntern 
	 * @return
	 */
	public void connect(BlockPos foreign);
	
	public BlockPos getPosition();
}
