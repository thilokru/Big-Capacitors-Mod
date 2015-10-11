package com.mhfs.capacitors.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class BlockFluidBase extends BlockFluidClassic {

	@SideOnly(Side.CLIENT)
	private IIcon stillIcon, flowingIcon;
	private String still, flowing;
	
	public BlockFluidBase(Fluid fluid, Material material, String still, String flowing) {
		super(fluid, material);
		this.still = still;
		this.flowing = flowing;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return (side == 0 || side == 1) ? stillIcon : flowingIcon;
	}
	
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		stillIcon = register.registerIcon(still);
		flowingIcon = register.registerIcon(flowing);
	}
}
