package com.mhfs.capacitors.world;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.network.WallUpdateMessage;
import com.mhfs.capacitors.tile.CapacitorWallWrapper;
import com.mhfs.capacitors.tile.TileCapacitor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

public class CapacitorWorldData extends WorldSavedData {
	
	public final static String NAME = "big_capacitors.capacitors.worldData";
	private final static Gson gson = new Gson();
	
	private Map<UUID, CapacitorWallWrapper> cwwMap;

	public CapacitorWorldData(){
		this(NAME);
	}
	
	public CapacitorWorldData(String name) {
		super(name);
		this.cwwMap = new HashMap<UUID, CapacitorWallWrapper>();
	}
	
	public CapacitorWallWrapper getCWW(UUID id) {
		this.markDirty();
		return cwwMap.get(id);
	}
	
	public UUID newCCW(TileCapacitor entity){
		UUID id = null;
		do {
			id = UUID.randomUUID();
		} while(cwwMap.containsKey(id));
		CapacitorWallWrapper wrapper = new CapacitorWallWrapper(entity, id);
		cwwMap.put(id, wrapper);
		for(EntityPlayer player : entity.getWorld().playerEntities) {
			sendWrapper(wrapper, player);
		}
		this.markDirty();
		return id;
	}
	
	public void onCWWBind(UUID id) {
		CapacitorWallWrapper wrapper = cwwMap.get(id);
		if(wrapper == null) return;
		wrapper.onTileBind();
		this.markDirty();
	}
	
	public void onCWWUnbind(UUID id) {
		CapacitorWallWrapper wrapper = cwwMap.get(id);
		if(wrapper == null) return;
		wrapper.onTileUnbind();
		if(!wrapper.hasBoundTiles()) {
			cwwMap.remove(id, wrapper);
		}
		this.markDirty();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		cwwMap = new HashMap<UUID, CapacitorWallWrapper>();
		int entires = nbt.getInteger("entries");
		for(int i = 0; i < entires; i++) {
			NBTTagCompound subTag = nbt.getCompoundTag(i + "");
			UUID id = subTag.getUniqueId("uuid");
			CapacitorWallWrapper wrapper = gson.fromJson(subTag.getString("cww"), CapacitorWallWrapper.class);
			cwwMap.put(id, wrapper);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		int i = 0;
		for(UUID id:cwwMap.keySet()) {
			if(!cwwMap.get(id).hasBoundTiles()) continue;
			NBTTagCompound subTag = new NBTTagCompound();
			subTag.setUniqueId("uuid", id);
			subTag.setString("cww", gson.toJson(cwwMap.get(id)));
			nbt.setTag("" + i, subTag);
			i++;
		}
		nbt.setInteger("entries", i);
		return nbt;
	}

	public void add(CapacitorWallWrapper wrapper) {
		if(!cwwMap.containsKey(wrapper.getID())) {
			cwwMap.put(wrapper.getID(), wrapper);
		}
	}

	public void sendToClient(EntityPlayer player) {
		for(UUID id : cwwMap.keySet()) {
			sendWrapper(cwwMap.get(id), player);
		}
	}
	
	private void sendWrapper(CapacitorWallWrapper wrapper, EntityPlayer player) {
		BigCapacitorsMod.instance.network.sendTo(new WallUpdateMessage(wrapper), (EntityPlayerMP) player);
	}

}
