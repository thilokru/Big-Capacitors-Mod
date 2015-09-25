package com.mhfs.capacitors.blocks;

import com.mhfs.capacitors.BigCapacitorsMod;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class BlockFluidDestilledWater extends BlockFluidClassic{
	
	@SideOnly(Side.CLIENT)
    private IIcon stillIcon;
    @SideOnly(Side.CLIENT)
    private IIcon flowingIcon;
   
    public BlockFluidDestilledWater(Fluid fluid, Material material) {
            super(fluid, material);
            setCreativeTab(BigCapacitorsMod.instance.creativeTab);
    }
   
    @Override
    public IIcon getIcon(int side, int meta) {
            return (side == 0 || side == 1)? stillIcon : flowingIcon;
    }
   
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
            stillIcon = register.registerIcon("minecraft:water_still");
            flowingIcon = register.registerIcon("minecraft:water_flow");
    }

}
