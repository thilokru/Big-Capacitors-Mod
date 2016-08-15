package com.mhfs.capacitors.tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.Blocks;
import com.mhfs.capacitors.blocks.BlockCapacitor;
import com.mhfs.capacitors.misc.HashSetHelper;
import com.mhfs.capacitors.misc.Helper;
import com.mhfs.capacitors.network.WallUpdateMessage;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;

public class CapacitorWallWrapper {

	private static final int MAX_DISTANCE = 10;
	private Set<BlockPos> containedBlocks;
	private EnumFacing orientation;
	private boolean grounded;
	private UUID id;
	private int boundTiles;

	private long charge;
	private long maxCharge;

	public CapacitorWallWrapper(TileCapacitor cap, UUID id) {
		this.id = id;
		this.orientation = EnumFacing.getFront(cap.getBlockMetadata());
		this.containedBlocks = searchWallFrom(new HashSet<BlockPos>(), cap.getPos(), cap.getWorld());
		for (BlockPos pos : containedBlocks) {
			if (pos.equals(cap.getPos()))
				continue;
			CapacitorWallWrapper ent = cap.getEntityCapacitor();
			if (ent == null) {
				cap.onEntityChange(id);
			}else if(ent != this){
				ent.checkJoin(cap.getWorld(), false);
				return;
			}
		}
		setupCapacity(cap.getWorld());
		updateBlocks(cap.getWorld());
	}

	private CapacitorWallWrapper() {
	}

	/**
	 * Searches capacitors which are connected to the capacitor at {@link pos}.
	 * To be connected, the following requirements must be met:
	 * First, the capacitor must face in the same direction as the capacitor at {@link pos}.
	 * Secondly, there must be a way to get to it via connected capacitor. A capacitor adjacent to the one at {@link pos}
	 * which is facing the same way, is connected.
	 * @param res the set of coordinates to add to. Must be a {@link HashSet} so that all elements are unique
	 * @param pos the position to search from.
	 * @param world the world this is set in.
	 * @return the fille set.
	 */
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

	/**
	 * @return How many blocks this wrapper wraps.
	 */
	public int getSize() {
		return containedBlocks.size();
	}

	/**
	 * Convenience method to check whether the side ({@link direction}) is the opposite of the
	 * side the wrapper is facing. Also the wrapper must not be grounded. 
	 * @param direction the direction from which energy shall be extracted.
	 * @return whethet energy may be exctracted.
	 */
	public boolean canExtractEnergy(EnumFacing direction) {
		if (direction.getOpposite() == orientation && !grounded) {
			return true;
		}
		return false;
	}

	/**
	 * Searches connected capacitor blocks for other CWWs. In that case they'll merge.
	 * @param world the world
	 * @param isFirstTick whether this is the first tick, aka. whether the world has just been loaded.
	 */
	public void checkJoin(World world, boolean isFirstTick) {
		this.charge = Math.min(this.charge, this.maxCharge);

		Set<CapacitorWallWrapper> controled = new HashSet<CapacitorWallWrapper>();
		long combinedCharge = isFirstTick ? this.charge : 0;
		BlockPos init = containedBlocks.iterator().next();
		this.containedBlocks = searchWallFrom(new HashSet<BlockPos>(), init, world);

		for (BlockPos pos : containedBlocks) {
			TileCapacitor tile = (TileCapacitor) world.getTileEntity(pos);
			if(tile == null)continue;
			if (tile.getEntityCapacitor() != null) {
				if (!controled.contains(tile.getEntityCapacitor())) {
					if(isFirstTick){
						combinedCharge = Math.max(combinedCharge, tile.getEntityCapacitor().charge);
					}else{
						combinedCharge += tile.getEntityCapacitor().charge;
					}
					if(tile.getEntityCapacitor().isGrounded()){
						this.grounded = true;
					}
					controled.add(tile.getEntityCapacitor());
				}
			}
			tile.onEntityChange(id);
		}

		this.setupCapacity(world);
		this.charge = combinedCharge;
		updateEnergy(world);
	}
	
	/**
	 * If a block which is member of this wrapper is destroyed, this method is called to handle the consequences.
	 * @param destroied the destroyed block
	 * @param world the world this is set in
	 * @param player the player who destroyed the block. May be null, if this block was destroyed by an explosion or similar.
	 */
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

	/**
	 * This methods determines the capacity this capacitor has.
	 * If the new capacity is lower than the charge, the charge will be set to the capacity.
	 * This will cause synchronization.
	 * @param world the world this is set in.
	 * @return whether the capaciy has changed.
	 */
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
		//Determines the distance between the walls. If there are more than one, the closest one is chosen.
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
		
		//Creates a set of BlockPos, which contains all relevant Positions of the opposite wall.
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
		//Determines the quality of the insulation and its dielectricity. Based on this the capacity will be calculated.
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
					String blockName = Block.REGISTRY.getNameForObject(block).toString();
					String name =  metadata == 0 ? blockName : blockName + " " + metadata;
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
		// Averages the dielectricity (Might not be physically accurate. TODO: Research)
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

	/**
	 * Creates a NBT Representation for this wrapper. This is used to save and synchronize this Wrapper.
	 * @return the representing NBTTagCompound
	 */
	public NBTTagCompound getNBTRepresentation() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setLong("charge", charge);
		tag.setBoolean("grounded", grounded);
		if (orientation != null)
			tag.setInteger("orientation", orientation.ordinal());
		tag.setTag("blocks", HashSetHelper.blockPosSetToNBT(containedBlocks));

		return tag;
	}

	/**
	 * Creates a wrapper based on the values saved in the NBTTag
	 * @param tag the tag the data is loaded from.
	 * @return the created wrapper.
	 */
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
				Helper.sendUpdate(entity);
			}
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
	
	public void onTileBind(){
		this.boundTiles++;
	}
	
	public void onTileUnbind(){
		this.boundTiles--;
	}
	
	public boolean hasBoundTiles(){
		return this.boundTiles > 0;
	}

	public UUID getID() {
		return id;
	}
}
