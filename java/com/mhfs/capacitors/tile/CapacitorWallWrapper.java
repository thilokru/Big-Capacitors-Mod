package com.mhfs.capacitors.tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cofh.api.energy.IEnergyStorage;

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
import static net.minecraftforge.common.util.ForgeDirection.*;

public class CapacitorWallWrapper implements IEnergyStorage {

	private static final int MAX_DISTANCE = 10;
	private Set<BlockPos> containedBlocks;
	private ForgeDirection orientation;
	private boolean grounded;
	private int id;

	private long charge;
	private long maxCharge;

	private Set<Integer> linkedWalls;

	public CapacitorWallWrapper(World world, BlockPos init) {
		id = generateID();
		this.orientation = ForgeDirection.getOrientation(init.getTileEntity(world).getBlockMetadata());
		this.containedBlocks = searchWallFrom(new HashSet<BlockPos>(), init, world);
		searchLinkedWalls(world);
		for (BlockPos pos : containedBlocks) {
			if (pos.equals(init))
				continue;
			TileCapacitor cap = (TileCapacitor) pos.getTileEntity(world);
			CapacitorWallWrapper ent = cap.getEntityCapacitor();
			if (ent != null && ent != this) {
				ent.join(this, world);
				return;
			}
		}
		setupCapacity(world);
		updateBlocks(world);
	}

	private CapacitorWallWrapper() {
	}

	private int generateID() {
		int id;
		do {
			id = (int) (Math.random() * 1000000);
		} while (BigCapacitorsMod.instance.worldCapacitors.containsKey(id));
		return id;
	}

	private HashSet<BlockPos> searchWallFrom(HashSet<BlockPos> res, BlockPos pos, IBlockAccess world) {
		res.add(pos);
		BlockCapacitor block = BigCapacitorsMod.instance.capacitorIron;
		ArrayList<BlockPos> result = block.getConnectedCapacitors(world, pos.x, pos.y, pos.z);
		for (BlockPos coord : result) {
			if (!res.contains(coord)) {
				searchWallFrom(res, coord, world);
			}
		}
		return res;
	}

	public int getSize() {
		return containedBlocks.size();
	}

	public boolean canExtractEnergy(ForgeDirection direction) {
		if (direction.getOpposite() == orientation && !grounded) {
			return true;
		}
		return false;
	}

	public void join(CapacitorWallWrapper old, World world) {
		this.containedBlocks.addAll(old.containedBlocks);
		BigCapacitorsMod.instance.worldCapacitors.remove(old.hashCode());

		for (BlockPos pos : old.containedBlocks) {
			TileCapacitor tile = (TileCapacitor) pos.getTileEntity(world);
			tile.onEntityChange(this);
		}
		BlockPos init = containedBlocks.iterator().next();
		this.containedBlocks = searchWallFrom(new HashSet<BlockPos>(), init, world);
		updateBlocks(world);
	}

	public void leave(BlockPos destroied, World world, EntityPlayer player) {
		containedBlocks.remove(destroied);
		if (containedBlocks.size() == 0 && FMLCommonHandler.instance().getSide().isClient()) {
			BigCapacitorsMod.instance.worldCapacitors.remove(this.hashCode());
			deleteFromOthers(world);
			return;
		}
		if (checkSplit(destroied, world, player)) {
			Set<BlockPos> setCopy = new HashSet<BlockPos>();
			setCopy.addAll(containedBlocks);
			for (BlockPos pos : setCopy) {
				((TileCapacitor) pos.getTileEntity(world)).onEntityChange(null);
			}
			for (BlockPos pos : setCopy) {
				((TileCapacitor) pos.getTileEntity(world)).updateEntity();
			}
			deleteFromOthers(world);
		}
		notifyLinkedWalls(world);
		setupCapacity(world);
		updateEnergy(world);
	}
	
	private void deleteFromOthers(World world){
		for (int id : linkedWalls) {
			CapacitorWallWrapper wrapper = BigCapacitorsMod.instance.worldCapacitors.get(id);
			wrapper.linkedWalls.remove(this.hashCode());
			wrapper.setupCapacity(world);
		}
	}
	
	private void notifyLinkedWalls(World world){
		for(int id:linkedWalls){
			CapacitorWallWrapper wrapper = BigCapacitorsMod.instance.worldCapacitors.get(id);
			wrapper.setupCapacity(world);
		}
	}

	public void searchLinkedWalls(World world) {
		linkedWalls = new HashSet<Integer>();
		for (BlockPos pos : containedBlocks) {
			INNER:for (int i = 1; i < MAX_DISTANCE; i++) {
				BlockPos res = pos.clone().goTowards(orientation, i);
				TileEntity ent = res.getTileEntity(world);
				if (ent != null && ent instanceof TileCapacitor) {
					ForgeDirection fOrientation = ForgeDirection.getOrientation(ent.getBlockMetadata());
					if (fOrientation.getOpposite() == orientation) {
						CapacitorWallWrapper wrapper = ((TileCapacitor)ent).getEntityCapacitor();
						if(wrapper == null)break INNER;
						linkedWalls.add(wrapper.id);
						break INNER;
					}
				}
			}
		}
	}

	public boolean setupCapacity(World world) {
		if (world.isRemote)
			return false;
		if(this.grounded){
			this.maxCharge = 0;
			this.charge = 0;
			return false;
		}
		long oldCapacity = maxCharge;
		Set<BlockPos> oneWall = this.containedBlocks;
		Set<BlockPos> otherWall = new HashSet<BlockPos>();
		for (int id : linkedWalls) {
			CapacitorWallWrapper wall = BigCapacitorsMod.instance.worldCapacitors.get(id);
			if(wall == null)continue;
			if(!wall.grounded)continue;
			otherWall.addAll(wall.containedBlocks);
		}

		int distance = Integer.MAX_VALUE;

		for (BlockPos pos : oneWall) {
			for (int i = 1; i < MAX_DISTANCE; i++) {
				BlockPos res = pos.clone().goTowards(orientation, i);
				TileEntity ent = res.getTileEntity(world);
				if (ent != null && ent instanceof TileCapacitor) {
					ForgeDirection fOrientation = ForgeDirection.getOrientation(ent.getBlockMetadata());
					if (fOrientation.getOpposite() == orientation) {
						distance = Math.min(distance, i - 1);
						break;
					}
				}
			}
		}

		int surface = 0;
		double dielectricity = 0.0;
		double maxVoltage = Double.MAX_VALUE;
		Map<Block, Double> dielectricities = BigCapacitorsMod.instance.dielectricities;
		Map<Block, Double> voltages = BigCapacitorsMod.instance.voltages;

		for (BlockPos chk : oneWall) {
			double tmpVoltage = 0;
			BlockPos work = chk.clone();
			work.goTowards(orientation, distance + 1);
			if (otherWall.contains(work)) {
				surface++;
				work = chk.clone();
				for (int i = 0; i < distance; i++) {
					work.goTowards(orientation, 1);
					Block block = work.getBlock(world);
					if (dielectricities.containsKey(block)) {
						dielectricity += dielectricities.get(block);
					} else {
						dielectricity += dielectricities.get(Blocks.air);
					}
					if (voltages.containsKey(block)) {
						tmpVoltage += voltages.get(block);
					} else {
						tmpVoltage += voltages.get(Blocks.air);
					}
				}
				if (tmpVoltage < maxVoltage) {
					maxVoltage = tmpVoltage;
				}
			}
		}

		dielectricity /= (surface * (distance));
		// Because its MV
		maxVoltage *= 1000000;

		// E = 0.5*C*U² = (EpsilonNull * EpsilonR * A * U² / (distance * 2))
		long capacity = (long) ((BigCapacitorsMod.energyConstant * Math.pow(maxVoltage, 2) * dielectricity * surface) / (distance) * 2);

		maxCharge = capacity;

		return capacity != oldCapacity;
	}

	/**
	 * @param destroied
	 * @param world
	 * @param player
	 * @param player
	 * @return whether the Entity was destroied.
	 */
	private boolean checkSplit(BlockPos destroied, World world, EntityPlayer player) {
		List<ForgeDirection> toCheck = new ArrayList<ForgeDirection>();
		if (orientation == UP || orientation == DOWN) {
			toCheck.add(NORTH);
			toCheck.add(SOUTH);
			toCheck.add(EAST);
			toCheck.add(WEST);
		} else {
			toCheck.add(UP);
			toCheck.add(DOWN);
			toCheck.add(DOWN.getRotation(orientation));
			toCheck.add(UP.getRotation(orientation));
		}

		List<ForgeDirection> remove = new ArrayList<ForgeDirection>();

		for (ForgeDirection dir : toCheck) {
			TileEntity ent = destroied.clone().goTowards(dir, 1).getTileEntity(world);
			if (ent == null || !(ent instanceof TileCapacitor)) {
				remove.add(dir);
			}
		}
		toCheck.remove(remove);
		if (toCheck.size() <= 1)
			return false;

		BlockPos test = destroied.clone().goTowards(toCheck.get(1), 1);
		if (containedBlocks.containsAll(searchWallFrom(new HashSet<BlockPos>(), test, world)))
			return false;

		return true;
	}

	public NBTTagCompound getNBTRepresentation() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("id", id);
		tag.setLong("charge", charge);
		tag.setBoolean("grounded", grounded);
		if (orientation != null)
			tag.setInteger("orientation", orientation.ordinal());
		tag.setTag("blocks", HashSetHelper.blockPosSetToNBT(containedBlocks));
		tag.setTag("linked", HashSetHelper.intSetToNBT(linkedWalls));
		return tag;
	}

	public static CapacitorWallWrapper fromNBT(NBTTagCompound tag) {
		CapacitorWallWrapper cap = new CapacitorWallWrapper();
		cap.id = tag.getInteger("id");
		cap.charge = tag.getLong("charge");
		cap.grounded = tag.getBoolean("grounded");
		if (tag.hasKey("orientation")) {
			cap.orientation = ForgeDirection.getOrientation(tag.getInteger("orientation"));
		}
		cap.containedBlocks = HashSetHelper.nbtToBlockPosSet(tag.getCompoundTag("blocks"));
		cap.linkedWalls = HashSetHelper.nbtToIntSet(tag.getCompoundTag("linked"));
		return cap;
	}

	@Override
	public int hashCode() {
		return id;
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

	public ForgeDirection getOrientation() {
		return orientation;
	}

	public boolean isGrounded() {
		return grounded;
	}

	public void onGroundSwitch(World world) {
		if(world.isRemote)return;
		this.grounded = !grounded;
		notifyLinkedWalls(world);
		this.setupCapacity(world);
		updateEnergy(world);
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		long pot = Math.min(maxCharge - charge, maxReceive);
		int receive;
		if (pot > Integer.MAX_VALUE) {
			receive = Integer.MAX_VALUE;
		} else {
			receive = (int) pot;
		}

		if (!simulate) {
			charge += receive;
		}
		return receive;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		long pot = Math.min(charge, maxExtract);
		int extract;
		if (pot > Integer.MAX_VALUE) {
			extract = Integer.MAX_VALUE;
		} else {
			extract = (int) pot;
		}

		if (!simulate) {
			charge -= extract;
		}
		return extract;
	}

	@Override
	public int getEnergyStored() {
		if (charge > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		}
		return (int) charge;
	}

	@Override
	public int getMaxEnergyStored() {
		if (maxCharge > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		}
		return (int) maxCharge;
	}

	public long getAllEnergyStored() {
		return charge;
	}

	public long getWholeCapacity() {
		return maxCharge;
	}

	private EnergyUpdateMessage getMessage() {
		return new EnergyUpdateMessage(charge, maxCharge, grounded, hashCode());
	}

	public void updateEnergy(World world) {
		if(world.isRemote)return;
		BigCapacitorsMod.instance.network.sendToAll(getMessage());
	}

	public void onPacket(EnergyUpdateMessage message) {
		this.grounded = message.isGrounded();
		this.charge = message.getEnergy();
		this.maxCharge = message.getCapacity();
	}
}
