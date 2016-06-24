package com.mhfs.api.lux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mhfs.capacitors.misc.Helper;

import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class RoutingImpl implements IRouting {

	@CapabilityInject(IRouting.class)
	public static Capability<IRouting> ROUTING_CAPABILITY;

	private TileEntity local;
	private Map<BlockPos, DistanceSpec> routes;
	private List<BlockPos> connections;

	public RoutingImpl(TileEntity local) {
		this.local = local;
		this.routes = new HashMap<BlockPos, DistanceSpec>();
		this.connections = new ArrayList<BlockPos>();
	}

	@Override
	public <T> void drainSetup(BlockPos requester, BlockPos nextHop, int distance) {
		DistanceSpec available = routes.get(requester);
		if (available != null && available.nextHop != nextHop && available.distance <= distance)
			return;
		routes.put(requester, new DistanceSpec(nextHop, distance));
		Helper.markForUpdate(local);
	}

	@Override
	public void handlerSetupRequest(IRouting requester) {
		for (BlockPos drain : routes.keySet()) {
			DistanceSpec spec = routes.get(drain);
			requester.drainSetup(drain, local.getPos(), spec.distance + 1);
		}
	}

	@Override
	public void handleDisconnect(BlockPos handler) {
		List<BlockPos> routesToDelete = new ArrayList<BlockPos>();
		for (BlockPos pos : routes.keySet()) {
			DistanceSpec spec = routes.get(pos);
			if (spec.nextHop.equals(handler)) {
				routesToDelete.add(pos);
			}
		}
		for (BlockPos pos : routesToDelete) {
			for (BlockPos hop : connections) {
				TileEntity remoteTile = local.getWorld().getTileEntity(hop);
				IRouting remoteCap = remoteTile.getCapability(ROUTING_CAPABILITY, null);
				if (remoteCap == null)
					continue;
				remoteCap.invalidateRoute(pos, this.getPosition());
			}
			this.routes.remove(pos);
		}
		this.connections.remove(handler);
		Helper.markForUpdate(local);
	}

	@Override
	public void invalidateRoute(BlockPos destination, BlockPos lastHop) {
		DistanceSpec spec = routes.get(destination);
		if(spec == null)return;
		if(!spec.nextHop.equals(lastHop)){
			TileEntity remoteTile = this.local.getWorld().getTileEntity(lastHop);
			IRouting remoteCap = remoteTile.getCapability(ROUTING_CAPABILITY, null);
			remoteCap.drainSetup(destination, this.getPosition(), this.routes.get(destination).distance);
			return;
		}
		this.routes.remove(destination);
		for (BlockPos hop : connections) {
			TileEntity remoteTile = local.getWorld().getTileEntity(hop);
			IRouting remoteCap = remoteTile.getCapability(ROUTING_CAPABILITY, null);
			if (remoteCap == null)
				continue;
			remoteCap.invalidateRoute(destination, this.getPosition());
		}
		Helper.markForUpdate(local);
	}
	
	/**
	 * May be used in the future. Is left here to be remembered.
	 */
	@SuppressWarnings("unused")
	private void requestUpdates(){
		List<BlockPos> clone = new ArrayList<BlockPos>();
		clone.addAll(connections);
		for (BlockPos hop : clone) {
			TileEntity remoteTile = local.getWorld().getTileEntity(hop);
			if(remoteTile == null)continue;
			IRouting remoteCap = remoteTile.getCapability(ROUTING_CAPABILITY, null);
			remoteCap.handlerSetupRequest(this);
		}
	}

	@Override
	public BlockPos route(BlockPos destinaiton) {
		DistanceSpec spec = routes.get(destinaiton);
		if (spec != null) {
			return spec.nextHop;
		}
		return null;
	}

	@Override
	public void addRoutingEntry(Route route) {
		this.routes.put(route.destination, new DistanceSpec(route.nextHop, route.distance));
	}

	@Override
	public Route[] getEntries() {
		Route[] routeArray = new Route[routes.entrySet().size()];
		int i = 0;
		for(Entry<BlockPos, DistanceSpec> ent : routes.entrySet()){
			routeArray[i] = new Route(ent.getKey(), ent.getValue().nextHop, ent.getValue().distance);
			i++;
		}
		return routeArray;
	}

	@Override
	public void onConnect(IRouting foreign) {
		connections.add(foreign.getPosition());
		foreign.handlerSetupRequest(this);
		Helper.markForUpdate(local);
	}

	@Override
	public BlockPos getPosition() {
		return local.getPos();
	}

	@Override
	public void addConnection(BlockPos connection) {
		this.connections.add(connection);
	}

	@Override
	public BlockPos[] getConnections() {
		return connections.toArray(new BlockPos[0]);
	}
	
	@Override
	public void resetEntries() {
		this.connections.clear();
		this.routes.clear();
	}
	
	public void advertise(){
		for (BlockPos hop : connections) {
			TileEntity remoteTile = local.getWorld().getTileEntity(hop);
			if(remoteTile == null)continue;
			IRouting remoteCap = remoteTile.getCapability(ROUTING_CAPABILITY, null);
			if (remoteCap == null)
				continue;
			remoteCap.drainSetup(getPosition(), getPosition(), 0);
		}
	}

	public void goOffline() {
		List<BlockPos> clone = new ArrayList<BlockPos>();
		clone.addAll(connections);
		for (BlockPos hop : clone) {
			TileEntity remoteTile = local.getWorld().getTileEntity(hop);
			if(remoteTile == null)continue;
			IRouting remoteCap = remoteTile.getCapability(ROUTING_CAPABILITY, null);
			if (remoteCap == null)
				continue;
			remoteCap.handleDisconnect(getPosition());
		}
	}

	public Set<BlockPos> getDestinations() {
		return routes.keySet();
	}

}
