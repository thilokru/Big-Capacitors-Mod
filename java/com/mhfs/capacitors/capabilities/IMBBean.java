package com.mhfs.capacitors.capabilities;

import com.mhfs.capacitors.misc.Multiblock;

import net.minecraft.util.EnumFacing;

public interface IMBBean {
	
	public void setFacing(EnumFacing facing);
	
	public EnumFacing getFacing();
	
	public void setMB(Multiblock mb);
	
	public Multiblock getMB();
}
