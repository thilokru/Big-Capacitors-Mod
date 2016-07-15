package com.mhfs.capacitors.tile.lux;

import java.util.HashSet;
import java.util.Set;

import com.mhfs.api.lux.LuxAPI;
import com.mhfs.api.lux.LuxHandlerImpl;
import com.mhfs.api.lux.RoutingImpl;
import static com.mhfs.capacitors.misc.Helper.markForUpdate;
import com.mhfs.capacitors.render.IConnected;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TileLuxRouter extends TileEntity implements IConnected {

	private RoutingImpl routingHandler;
	private LuxHandlerImpl luxHandler;

	public TileLuxRouter() {
		super();
		this.routingHandler = new RoutingImpl(this);
		this.luxHandler = new LuxHandlerImpl(this);
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		LuxAPI.ROUTING_CAPABILITY.readNBT(routingHandler, null, tag.getTag("routing"));
		LuxAPI.LUX_FLOW_CAPABILITY.readNBT(luxHandler, null, tag.getTag("lux"));
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("routing", LuxAPI.ROUTING_CAPABILITY.writeNBT(routingHandler, null));
		tag.setTag("lux", LuxAPI.LUX_FLOW_CAPABILITY.writeNBT(luxHandler, null));
		this.resetConnectionState();
		return tag;
	}
	
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == LuxAPI.LUX_FLOW_CAPABILITY){
			return LuxAPI.LUX_FLOW_CAPABILITY.cast(this.luxHandler);
		}else if(cap == LuxAPI.ROUTING_CAPABILITY){
			return LuxAPI.ROUTING_CAPABILITY.cast(this.routingHandler);
		}
		return super.getCapability(cap, side);
	}
	
	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == LuxAPI.LUX_FLOW_CAPABILITY || cap == LuxAPI.ROUTING_CAPABILITY)
			return true;
		return super.hasCapability(cap, side);
	}
	
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}

	public Packet<?> getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new SPacketUpdateTileEntity(this.pos, 1, tag);
	}

	public Set<BlockPos> getActiveConnections() {
		return luxHandler.getActive();
	}

	public Set<BlockPos> getConnections() {
		Set<BlockPos> set = new HashSet<BlockPos>();
		for(BlockPos pos : routingHandler.getConnections()){
			set.add(pos);
		}
		return set;
	}

	public void resetConnectionState() {
		luxHandler.resetActive();
		markForUpdate(this);
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	public void onDestroy() {
		routingHandler.disonnectSink();
	}
}
