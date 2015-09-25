package com.mhfs.capacitors.tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.blocks.BlockCapacitor;
import com.mhfs.capacitors.misc.HashSetHelper;
import com.mhfs.capacitors.network.EnergyUpdateMessage;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class EntityCapacitor {

	private final static int MAX_DISTANCE = 10;

	private HashSet<BlockPos> containedBlocks;
	private ForgeDirection allowedExtract;
	private int distance;
	private CapacitorEnergyStorage storage;
	private int id;

	public EntityCapacitor(World world, BlockPos init) {
		id = generateID();
		this.storage = new CapacitorEnergyStorage(0);
		setVariables(world, init);// Everything is allright
		for (BlockPos pos : containedBlocks) {
			if (pos.equals(init))
				continue;
			TileCapacitor cap = (TileCapacitor) pos.getTileEntity(world);
			EntityCapacitor ent = cap.getEntityCapacitor();
			if (ent != null && ent != this) {
				ent.join(this, world);
				return;
			}
		}
		setupCapacity(world);
	}

	private EntityCapacitor() {
	}

	private int generateID() {
		int id;
		do {
			id = (int) (Math.random() * 1000000);
		} while (BigCapacitorsMod.instance.worldCapacitors.containsKey(id));
		return id;
	}

	private void setVariables(IBlockAccess world, BlockPos init) {
		this.containedBlocks = searchWallFrom(new HashSet<BlockPos>(), init,
				world);
		BlockPos otherWall = null;
		double distance = Double.MAX_VALUE;
		for (BlockPos pos : containedBlocks) {
			BlockPos candidate = findOtherWall(pos, world);
			if (candidate != null) {
				double candidateDistance = pos.getDistance(candidate);
				if (candidateDistance < distance) {
					distance = candidateDistance;
					otherWall = candidate;
				}
			}
		}
		this.distance = (int) (distance - 1);
		if (otherWall != null) {
			containedBlocks.addAll(searchWallFrom(new HashSet<BlockPos>(),
					otherWall, world));
		}
	}

	private HashSet<BlockPos> searchWallFrom(HashSet<BlockPos> res,
			BlockPos pos, IBlockAccess world) {
		res.add(pos);
		BlockCapacitor block = BigCapacitorsMod.instance.capacitorIron;
		ArrayList<BlockPos> result = block.getConnectedCapacitors(world, pos.x,
				pos.y, pos.z);
		for (BlockPos coord : result) {
			if (!res.contains(coord)) {
				searchWallFrom(res, coord, world);
			}
		}
		return res;
	}

	public boolean setupCapacity(World world) {
		if(world.isRemote)return false;
		if (distance > EntityCapacitor.MAX_DISTANCE)
			return false;
		if (containedBlocks.size() == 0)
			return false;
		long oldCapacity = storage.getAllEnergyStored();
		HashSet<BlockPos> oneWall = new HashSet<BlockPos>();
		HashSet<BlockPos> otherWall = new HashSet<BlockPos>();
		Iterator<BlockPos> it = containedBlocks.iterator();
		ForgeDirection facing;
		BlockPos pos;
		do{
			pos = it.next();
		}while(pos.getBlock(world) != BigCapacitorsMod.instance.capacitorIron);
		facing = BigCapacitorsMod.instance.capacitorIron.getOrientation(world,
				pos.x, pos.y, pos.z);
		oneWall.add(pos);
		while (it.hasNext()) {
			pos = it.next();
			ForgeDirection dir =BigCapacitorsMod.instance.capacitorIron
					.getOrientation(world, pos.x, pos.y, pos.z);
			if (facing == dir) {
				oneWall.add(pos);
			}else if (facing.getOpposite() == dir){
				otherWall.add(pos);
			}
		}

		int surface = 0;
		double dielectricity = 0.0;
		double maxVoltage = Double.MAX_VALUE;
		HashMap<Block, Double> dielectricities = BigCapacitorsMod.instance.dielectricities;
		HashMap<Block, Double> voltages = BigCapacitorsMod.instance.voltages;

		for (BlockPos chk : oneWall) {
			double tmpVoltage = 0;
			BlockPos work = chk.clone();
			work.goTowards(facing, distance + 1);
			if (otherWall.contains(work)) {
				surface++;
				work = chk.clone();
				for (int i = 0; i < distance; i++) {
					work.goTowards(facing, 1);
					Block block = work.getBlock(world);
					if (dielectricities.containsKey(block)) {
						dielectricity += dielectricities.get(block);
					} else {
						dielectricity += dielectricities.get(Blocks.air);
					}
					if(voltages.containsKey(block)){
						tmpVoltage += voltages.get(block);
					}else{
						tmpVoltage += voltages.get(Blocks.air);
					}
				}
				if(tmpVoltage < maxVoltage){
					maxVoltage = tmpVoltage;
				}
			}
		}

		dielectricity /= (surface * (distance));
		//Because its MV
		maxVoltage *= 1000000;
		
		//E = 0.5*C*U² = (EpsilonNull * EpsilonR * A * U² / (distance * 2))
		long capacity = (long) ((BigCapacitorsMod.energyConstant * Math.pow(maxVoltage, 2) * dielectricity * surface) / (distance) * 2);

		storage.setCapacity(capacity);
		
		return capacity != oldCapacity;
	}

	private BlockPos findOtherWall(BlockPos pos, IBlockAccess world) {
		BlockPos orig = pos.clone();
		BlockCapacitor cap = BigCapacitorsMod.instance.capacitorIron;
		ForgeDirection facing = cap.getOrientation(world, pos.x, pos.y, pos.z);
		ForgeDirection wallOrientation = facing.getOpposite();
		boolean found = false;
		for (int d = 0; d < MAX_DISTANCE; d++) {
			orig.goTowards(facing, 1);
			Block block = orig.getBlock(world);
			if (block instanceof BlockCapacitor) {
				ForgeDirection capDir = cap.getOrientation(world, orig.x,
						orig.y, orig.z);
				if (capDir == wallOrientation) {
					found = true;
					break;
				} else {
					break;
				}
			}
		}
		return found ? orig : null;
	}

	public CapacitorEnergyStorage getStorage() {
		return storage;
	}

	public int getSize() {
		return containedBlocks.size();
	}

	public boolean canExtractEnergy(ForgeDirection direction) {
		if (allowedExtract == null) {
			allowedExtract = direction;
		}
		return allowedExtract == direction;
	}

	public void join(EntityCapacitor old, World world) {
		this.containedBlocks.addAll(old.containedBlocks);
		int totalEnergy = this.storage.getEnergyStored()
				+ old.storage.getEnergyStored();
		BigCapacitorsMod.instance.worldCapacitors.remove(old.hashCode());

		for (BlockPos pos : old.containedBlocks) {
			TileCapacitor tile = (TileCapacitor) pos.getTileEntity(world);
			tile.onEntityChange(this);
		}
		BlockPos init = containedBlocks.iterator().next();
		setVariables(world, init);
		setupCapacity(world);
		this.storage.setEnergyStored(totalEnergy);
		updateBlocks(world);
		updateEnergy(world);
	}

	public void leave(BlockPos destroied, World world, EntityPlayer player) {
		containedBlocks.remove(destroied);
		if (containedBlocks.size() == 0
				&& FMLCommonHandler.instance().getSide().isClient()) {
			BigCapacitorsMod.instance.worldCapacitors.remove(this.hashCode());
			return;
		}
		if(checkSplit(destroied, world, player))return;
		updateBlocks(world);
		setupCapacity(world);
	}
	
	/**
	 * @param destroied
	 * @param world
	 * @param player 
	 * @param player
	 * @return whether the Entity was destroied.
	 */
	private boolean checkSplit(BlockPos destroied, World world, EntityPlayer player) {
		BlockCapacitor cap = BigCapacitorsMod.instance.capacitorIron;
		ForgeDirection facing = cap.getOrientation(world, destroied.x, destroied.y, destroied.z);
		if(facing == allowedExtract){
			this.containedBlocks.remove(destroied);
			return false;
		}else{
			if(player != null){
				boolean grounded = ((TileCapacitor)destroied.getTileEntity(world)).getOrientation() == allowedExtract;
				if(!grounded)player.attackEntityFrom(BigCapacitorsMod.instance.damageElectric, storage.getAllEnergyStored() / 10000);
			}
			this.storage.setEnergyStored(0);
			this.containedBlocks.remove(destroied);
			for(BlockPos pos:containedBlocks){
				((TileCapacitor)pos.getTileEntity(world)).onEntityChange(null);
			}
			BigCapacitorsMod.instance.worldCapacitors.remove(this.hashCode());
			return true;
		}
	}

	public NBTTagCompound getNBTRepresentation() {
		NBTTagCompound tag = new NBTTagCompound();
		storage.writeToNBT(tag);
		tag.setInteger("id", id);
		tag.setInteger("distance", distance);
		if (allowedExtract != null)
			tag.setInteger("energyIn", allowedExtract.ordinal());
		tag.setTag("blocks", HashSetHelper.blockPosSetToNBT(containedBlocks));
		return tag;
	}

	public static EntityCapacitor fromNBT(NBTTagCompound tag) {
		EntityCapacitor cap = new EntityCapacitor();
		cap.id = tag.getInteger("id");
		cap.storage = new CapacitorEnergyStorage(0);
		cap.distance = tag.getInteger("distance");
		if (tag.hasKey("energyIn")) {
			cap.allowedExtract = ForgeDirection.getOrientation(tag
					.getInteger("energyIn"));
		}
		cap.containedBlocks = HashSetHelper.nbtToBlockPosSet(tag
				.getCompoundTag("blocks"));
		cap.storage = CapacitorEnergyStorage.readFromNBT(tag);
		return cap;
	}

	@Override
	public int hashCode() {
		return id;
	}

	public EnergyUpdateMessage getMessage() {
		return new EnergyUpdateMessage(storage.getAllEnergyStored(),
				storage.getWholeCapacity(), allowedExtract, hashCode());
	}

	public void onPacket(EnergyUpdateMessage message) {
		this.storage.setCapacity(message.getCapacity());
		this.storage.setEnergyStored(message.getEnergy());
		this.allowedExtract = message.getAllowedExtract();
	}

	public void updateBlocks(World world) {
		for (BlockPos pos : containedBlocks) {
			TileEntity entity = pos.getTileEntity(world);
			if (entity != null) {
				entity.markDirty();
			}
			world.markBlockForUpdate(pos.x, pos.y, pos.z);
		}
	}

	public void updateEnergy(World world) {
		if(world.isRemote)return;
		BigCapacitorsMod.instance.network.sendToAll(getMessage());
	}

	public ForgeDirection getExtractEnergy() {
		return allowedExtract;
	}

	public void onGroundSwitch(World world) {
		allowedExtract = allowedExtract.getOpposite();
		updateBlocks(world);
		storage.setEnergyStored(0);
		updateEnergy(world);
	}
}
