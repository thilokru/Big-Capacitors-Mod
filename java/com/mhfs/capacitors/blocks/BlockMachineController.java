package com.mhfs.capacitors.blocks;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.tile.IActivatable;
import com.mhfs.capacitors.tile.TileCrusherController;
import com.mhfs.capacitors.tile.TileTokamak;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class BlockMachineController extends BlockAdvContainer{
	
	public final static String name = "blockMachineController";

	public BlockMachineController(Material material) {
		super(material, name, true);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return null;
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(world.getTileEntity(pos) == null){
			if(BigCapacitorsMod.instance.fusionReactorMulti.complete(pos, world)){
				TileEntity te = new TileTokamak();
				te.setWorldObj(world);
				world.setTileEntity(pos, te);
			}else if(BigCapacitorsMod.instance.crusherMulti.getCompletedRotation(pos, world) != null){
				TileCrusherController te = new TileCrusherController();
				te.setWorldObj(world);
				te.setMultiblockRotation(BigCapacitorsMod.instance.crusherMulti.getCompletedRotation(pos, world));
				world.setTileEntity(pos, te);
			}
			return false;
		}
		if (!world.isRemote && world.getTileEntity(pos) instanceof IActivatable)
			return ((IActivatable) world.getTileEntity(pos)).onBlockActivated(player, heldItem);
		return true;
	}
}
