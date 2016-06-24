package com.mhfs.api.lux;

import net.minecraft.util.math.BlockPos;

public interface IRouting {

	/**
	 * When a new drain joins the network, it calls this method. It creates a distance based
	 * routing table, by applying the following rules:
	 * - each connection is equal
	 * - the shortest route must be preferred.
	 * 
	 * Each hop has to call this method the next tick on all connected LuxHandlers with the distance
	 * being increased by one.
	 * If a route exists, which is better (lower distance), this method call returns.
	 * 
	 * The {@link LuxHandler.handlerDisconnect()} method will clear the routing table. This requires
	 * the drains to call this method regularly (e.g. every 20 ticks)
	 * @param world the world this happens in
	 * @param distance the distance
	 * @param requester the position of the drain
	 * @param nextHop the position of the last hop (routing happens in reverse)
	 * @return 
	 */
	public void drainSetup(BlockPos requester, BlockPos nextHop, int distance);
	
	/**
	 * When a drain wishes to no longer be routed to, e.g it changed from receiving to transmitting,
	 * it shall call this method. It will propagate through the network and if a route to this node
	 * is present, the route must be removed. Also, this method must be called on all connected nodes.
	 * @param requester the node which wishes to be forgotten
	 */
	public void drainDisconnect(BlockPos requester);
	
	/**
	 * If a node (a router, e.g) joins the network, it needs to retrieve its configuration.
	 * This method call forces the node on which it is called to call the drainSetup method
	 * on the new node based on his best routes.
	 * 
	 * If the new node enables new connections, this allows a fast reconfiguration.
	 * @param requester
	 */
	public void handlerSetupRequest(IRouting requester);
	
	/**
	 * If a node is destroyed (a LuxHandler, e.g), this method must be called on each adjacent node.
	 * If this node has routes going through the destroyed one, it must invalidate the route and notify adjacent
	 * nodes via {@link #invalidateRoute(BlockPos, BlockPos)}
	 * @param handler
	 * @param level
	 */
	public void handleDisconnect(BlockPos handler);
	
	/**
	 * Invalidates a route to the destination if the next hop is equal to the given parameter.
	 * If a route has been invalidated successfully, this method must be called on adjacent hops to ensure
	 * no trace of the now invalid route is left.
	 * @param destination
	 * @param lastHop
	 */
	public void invalidateRoute(BlockPos destination, BlockPos lastHop);
	
	/**
	 * If you want to route something to the destination, you should call this method
	 * to get the next hop.
	 * @param destination The destination
	 * @return the hop you should take for the shortest route
	 */
	public BlockPos route(BlockPos destination);
	
	/**
	 * If a link to another IRouting Tile is established, this method should be called
	 * to perform the top-layer handshakes.
	 * @param handleIntern the other routers IRouting-thingy
	 * @return
	 */
	public void onConnect(IRouting handleIntern);
	
	/**
	 * @return the position associated with this IRouting instance.
	 */
	public BlockPos getPosition();
	
	//INTERNAL, Only used for serialization
	/**
	 * 
	 * @deprecated should only be used for deserialization
	 * @param route the route to add to the router
	 */
	@Deprecated
	public void addRoutingEntry(Route route);
	
	/**
	 * @deprecated should only be used for serialization
	 * @return all routes known to the router
	 */
	@Deprecated
	public Route[] getEntries();
	
	/**
	 * @deprecated should only be used for deserialization
	 * @param connection
	 */
	@Deprecated
	public void addConnection(BlockPos connection);
	
	/**
	 * @deprecated should only be used for serialization
	 * @return all connections
	 */
	@Deprecated
	public BlockPos[] getConnections();
	
	/**
	 * @deprecated should only be used for serialization
	 * resets all entries: connections and routes
	 */
	@Deprecated
	public void resetEntries();
}
