package com.mhfs.capacitors.blocks;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.tile.TileTokamak;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockTokamak extends BlockContainer{
	
	public final static String name = "blockTokamak";

	public BlockTokamak(Material material) {
		super(material);
		GameRegistry.registerBlock(this, name);
		this.setUnlocalizedName(name);
		this.setCreativeTab(BigCapacitorsMod.instance.creativeTab);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntity te = new TileTokamak();
		te.setWorldObj(world);
		return te;
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote)
			return ((TileTokamak) world.getTileEntity(pos)).onBlockActivated(player);
		return true;
	}
	
	public int getRenderType() {
		return 3;
	}
}
