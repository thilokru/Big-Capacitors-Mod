package com.mhfs.capacitors.blocks;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.tile.IActivatable;
import com.mhfs.capacitors.tile.TileCrusherController;
import com.mhfs.capacitors.tile.TileTokamak;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class BlockMachineController extends BlockAdvContainer {

	public final static String name = "blockMachineController";
	public final static PropertyInteger USED_TE = PropertyInteger.create("used_te", 0, 2);

	public BlockMachineController(Material material) {
		super(material, name, true);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		IBlockState state = this.getStateFromMeta(meta);
		switch (state.getValue(USED_TE)) {
		case 0:
			return null;
		case 1:
			return new TileTokamak();
		case 2:
			return new TileCrusherController();
		default:
			return null;
		}
	}

	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.getTileEntity(pos) == null) {
			if (BigCapacitorsMod.instance.fusionReactorMulti.complete(pos, world)) { // TE type 1
				createFusionReactor(world, pos, state);
			} else if (BigCapacitorsMod.instance.crusherMulti.getCompletedRotation(pos, world) != null) { // TE type 2
				createCrusher(world, pos, state);
			}
			return true;
		}
		if (!world.isRemote && world.getTileEntity(pos) instanceof IActivatable)
			return ((IActivatable) world.getTileEntity(pos)).onBlockActivated(player, heldItem);
		return true;
	}

	private void createFusionReactor(World world, BlockPos pos, IBlockState state) {
		world.removeTileEntity(pos);
		TileEntity te = new TileTokamak();
		te.setWorldObj(world);
		world.setTileEntity(pos, te);
		world.setBlockState(pos, state.withProperty(USED_TE, 2));
	}

	private void createCrusher(World world, BlockPos pos, IBlockState state) {
		world.removeTileEntity(pos);
		TileCrusherController te = new TileCrusherController();
		te.setWorldObj(world);
		te.setMultiblockRotation(BigCapacitorsMod.instance.crusherMulti.getCompletedRotation(pos, world));
		world.setTileEntity(pos, te);
		world.setBlockState(pos, state.withProperty(USED_TE, 3));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(USED_TE, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(USED_TE);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { USED_TE });
	}
}
