package com.mhfs.capacitors.misc;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public class Helper {
	public static ResourceLocation getTextureFromFluid(Fluid fluid) {
		ResourceLocation still = fluid.getStill();
		return new ResourceLocation(still.getResourceDomain(), "textures/" + still.getResourcePath() + ".png");
	}

	public static void playPageSound(SoundHandler sh) {
		sh.playSound(PositionedSoundRecord.getMasterRecord(new SoundEvent(new ResourceLocation("big_capacitors:pageTurn")), 1.0F));
	}

	public static void sendUpdate(TileEntity entity) {
		entity.markDirty();
		if(entity.getWorld().isRemote) return;
		World world = entity.getWorld();
		IBlockState state = world.getBlockState(entity.getPos());
		world.notifyBlockUpdate(entity.getPos(), state, state, 0);
	}
	
	/**
	 * The given vector will be rotated accordingly.
	 * If the vector is valid for the facing north, the vector will be rotated around
	 * the Y-axis accordingly. 
	 * @param vector
	 * @param target
	 * @return the rotated vector
	 */
	public static Vec3i rotateVector(Vec3i vector, EnumFacing facing) {
		switch (facing) {
		case NORTH:
			return vector;
		case SOUTH:
			return new Vec3i(-vector.getX(), vector.getY(), -vector.getZ());
		case WEST:
			return new Vec3i(vector.getZ(), vector.getY(), -vector.getX());
		case EAST:
			return new Vec3i(-vector.getZ(), vector.getY(), vector.getX());
		default:
			Lo.g.error("RotatableMultiblock shall be rotated towards UP or DOWN, which should not be done! This is SEVERE!");
			return vector;
		}
	}
	
	/**
	 * The given vector will be rotated accordingly.
	 * If the vector is valid for the facing north, the vector will be rotated around
	 * the Y-axis accordingly. 
	 * @param vector
	 * @param target
	 * @return the rotated vector
	 */
	public static Vec3d rotateVector(Vec3d vector, EnumFacing facing) {
		switch (facing) {
		case NORTH:
			return vector;
		case SOUTH:
			return new Vec3d(-vector.xCoord, vector.yCoord, -vector.zCoord);
		case WEST:
			return new Vec3d(vector.zCoord, vector.yCoord, -vector.xCoord);
		case EAST:
			return new Vec3d(-vector.zCoord, vector.yCoord, vector.xCoord);
		default:
			Lo.g.error("RotatableMultiblock shall be rotated towards UP or DOWN, which should not be done! This is SEVERE!");
			return vector;
		}
	}
}
