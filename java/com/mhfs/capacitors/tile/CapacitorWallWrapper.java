package com.mhfs.capacitors.tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.Blocks;
import com.mhfs.capacitors.blocks.BlockCapacitor;
import com.mhfs.capacitors.misc.HashSetHelper;
import com.mhfs.capacitors.network.WallUpdateMessage;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class CapacitorWallWrapper {

	private static final int MAX_DISTANCE = 10;
	private Set<BlockPos> containedBlocks;
	private EnumFacing orientation;
	private boolean grounded;

	private long charge;
	private long maxCharge;

	public CapacitorWallWrapper(World world, BlockPos init) {
		this.orientation = EnumFacing.getFront(world.getTileEntity(init).getBlockMetadata());
		this.containedBlocks = searchWallFrom(new HashSet<BlockPos>(), init, world);
		for (BlockPos pos : containedBlocks) {
			if (pos.equals(init))
				continue;
			TileCapacitor cap = (TileCapacitor) world.getTileEntity(pos);
			CapacitorWallWrapper ent = cap.getEntityCapacitor();
			if (ent == null) {
				cap.onEntityChange(this);
			}else if(ent != this){
				ent.checkJoin(world, false);
				return;
			}
		}
		setupCapacity(world);
		updateBlocks(world);
	}

	private CapacitorWallWrapper() {
	}

	private HashSet<BlockPos> searchWallFrom(HashSet<BlockPos> res, BlockPos pos, IBlockAccess world) {
		res.add(pos);
		BlockCapacitor block = Blocks.capacitorIron;
		ArrayList<BlockPos> result = block.getConnectedCapacitors(world, pos);
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

	public boolean canExtractEnergy(EnumFacing direction) {
		if (direction.getOpposite() == orientation && !grounded) {
			return true;
		}
		return false;
	}

	public void checkJoin(World world, boolean isFirstTick) {
		this.charge = Math.min(this.charge, this.maxCharge);

		Set<CapacitorWallWrapper> controled = new HashSet<CapacitorWallWrapper>();
		long combinedCharge = isFirstTick ? this.charge : 0;
		BlockPos init = containedBlocks.iterator().next();
		this.containedBlocks = searchWallFrom(new HashSet<BlockPos>(), init, world);

		for (BlockPos pos : containedBlocks) {
			TileCapacitor tile = (TileCapacitor) world.getTileEntity(pos);
			if (tile.getEntityCapacitor() != null) {
				if (!controled.contains(tile.getEntityCapacitor())) {
					if(isFirstTick){
						combinedCharge = Math.max(combinedCharge, tile.getEntityCapacitor().charge);
					}else{
						combinedCharge += tile.getEntityCapacitor().charge;
					}
					controled.add(tile.getEntityCapacitor());
				}
			}
			tile.onEntityChange(this);
		}

		this.setupCapacity(world);
		this.charge = combinedCharge;
		updateEnergy(world);
	}

	public void leave(BlockPos destroied, World world, EntityPlayer player) {
		for(BlockPos pos:containedBlocks){
			TileCapacitor tile = (TileCapacitor) world.getTileEntity(pos);
			if (tile != null) {
				tile.onEntityChange(null);
			}				
		}
		if(player != null){
			if(!this.isGrounded()){
				player.attackEntityFrom(BigCapacitorsMod.instance.damageElectric, charge / 10000);
			}
		}
	}

	public boolean setupCapacity(World world) {
		if (this.grounded) {
			this.maxCharge = 0;
			this.charge = 0;
			return false;
		}
		long oldCapacity = maxCharge;
		Set<BlockPos> oneWall = this.containedBlocks;
		Set<BlockPos> otherWall = new HashSet<BlockPos>();

		int distance = Integer.MAX_VALUE;

		for (BlockPos pos : oneWall) {
			for (int i = 1; i < MAX_DISTANCE; i++) {
				BlockPos res = pos.offset(orientation, i);
				TileEntity ent = world.getTileEntity(res);
				if (ent != null && ent instanceof TileCapacitor) {
					EnumFacing fOrientation = EnumFacing.getFront(ent.getBlockMetadata());
					if (fOrientation.getOpposite() == orientation) {
						distance = Math.min(distance, i - 1);
						break;
					}
				}
			}
		}
		
		for(BlockPos orig : oneWall){
			BlockPos dest = orig.offset(orientation, distance + 1);
			TileEntity ent = world.getTileEntity(dest);
			if(ent != null && ent instanceof TileCapacitor){
				EnumFacing fOrientation = EnumFacing.getFront(ent.getBlockMetadata());
				TileCapacitor cap = (TileCapacitor) ent;
				if(cap == null || cap.getEntityCapacitor() == null)return false;
				if (fOrientation.getOpposite() == orientation && cap.getEntityCapacitor().isGrounded()) {
					otherWall.add(dest);
				}
			}
		}

		int surface = 0;
		double dielectricity = 0.0;
		double maxVoltage = Double.MAX_VALUE;
		Map<String, Double> dielectricities = BigCapacitorsMod.instance.dielectricities;
		Map<String, Double> voltages = BigCapacitorsMod.instance.voltages;

		for (BlockPos chk : oneWall) {
			double tmpVoltage = 0;
			BlockPos work = chk.offset(orientation, distance + 1);
			if (otherWall.contains(work)) {
				surface++;
				work = chk;
				for (int i = 0; i < distance; i++) {
					work = work.offset(orientation);
					IBlockState state = world.getBlockState(work);
					Block block = state.getBlock();
					int metadata = block.getMetaFromState(state);
					String name = Block.blockRegistry.getNameForObject(block) + " " + metadata;
					if (dielectricities.containsKey(name)) {
						dielectricity += dielectricities.get(name);
					} else {
						dielectricity += dielectricities.get("minecraft:air");
					}
					if (voltages.containsKey(name)) {
						tmpVoltage += voltages.get(name);
					} else {
						tmpVoltage += voltages.get("minecraft:air");
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
		if(charge > maxCharge){
			charge = maxCharge;
			this.updateEnergy(world);
		}

		return capacity != oldCapacity;
	}

	public NBTTagCompound getNBTRepresentation() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setLong("charge", charge);
		tag.setBoolean("grounded", grounded);
		if (orientation != null)
			tag.setInteger("orientation", orientation.ordinal());
		tag.setTag("blocks", HashSetHelper.blockPosSetToNBT(containedBlocks));

		return tag;
	}

	public static CapacitorWallWrapper fromNBT(NBTTagCompound tag) {
		CapacitorWallWrapper cap = new CapacitorWallWrapper();
		cap.charge = tag.getLong("charge");
		cap.grounded = tag.getBoolean("grounded");
		if (tag.hasKey("orientation")) {
			cap.orientation = EnumFacing.getFront(tag.getInteger("orientation"));
		}
		cap.containedBlocks = HashSetHelper.nbtToBlockPosSet(tag.getCompoundTag("blocks"));
		
		return cap;
	}

	public void updateBlocks(World world) {
		for (BlockPos pos : containedBlocks) {
			TileEntity entity = world.getTileEntity(pos);
			if (entity != null) {
				entity.markDirty();
			}
			world.markBlockForUpdate(pos);
		}
	}

	public EnumFacing getOrientation() {
		return orientation;
	}

	public boolean isGrounded() {
		return grounded;
	}

	public void onGroundSwitch(World world) {
		this.grounded = !grounded;
		this.setupCapacity(world);
		updateEnergy(world);
	}

	public long getEnergyStored() {
		return charge;
	}

	public long getMaxEnergyStored() {
		return maxCharge;
	}

	private WallUpdateMessage getMessage() {
		return new WallUpdateMessage(this);
	}

	public void updateEnergy(World world) {
		if (world.isRemote)
			return;
		BigCapacitorsMod.instance.network.sendToAll(getMessage());
	}
	
	public BlockPos getRandomBlock(){
		return containedBlocks.iterator().next();
	}

	public void sync(CapacitorWallWrapper wrapper) {
		this.charge = wrapper.charge;
		this.grounded = wrapper.grounded;
		this.maxCharge = wrapper.maxCharge;
		this.orientation = wrapper.orientation;
		this.containedBlocks = wrapper.containedBlocks;
	}
	
	public boolean isMember(BlockPos pos){
		return containedBlocks.contains(pos);
	}

	public int drain(int amount, boolean simulate) {
		long pot = Math.min(charge, amount);
		int extract;
		if (pot > Integer.MAX_VALUE) {
			extract = Integer.MAX_VALUE;
		} else {
			extract = (int) pot;
		}
		if(!simulate){
			charge -= extract;
		}
		return extract;
	}

	public int fill(int amount, boolean simulate) {
		long pot = Math.min(maxCharge - charge, amount);
		int receive;
		if (pot > Integer.MAX_VALUE) {
			receive = Integer.MAX_VALUE;
		} else {
			receive = (int) pot;
		}
		if(!simulate){
			charge += receive;
		}
		return receive;
	}
}
