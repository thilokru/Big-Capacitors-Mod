package com.mhfs.capacitors.tile;

import com.mhfs.capacitors.capabilities.CapabilityMBBean;
import com.mhfs.capacitors.capabilities.IMBBean;
import com.mhfs.capacitors.capabilities.PlayerCapabilityProvider;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class TileMultiblockRender extends AdvTileEntity {
	
	private IMBBean bean;
	
	public TileMultiblockRender(){
		this.bean = new PlayerCapabilityProvider.Bean();
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setTag("bean", CapabilityMBBean.CAPABILTY_MB_BEAN.writeNBT(bean, null));
		return tag;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		CapabilityMBBean.CAPABILTY_MB_BEAN.readNBT(bean, null, tag.getTag("bean"));
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(capability == CapabilityMBBean.CAPABILTY_MB_BEAN) return true;
		return super.hasCapability(capability, facing);
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityMBBean.CAPABILTY_MB_BEAN) {
			return CapabilityMBBean.CAPABILTY_MB_BEAN.cast(bean);
		}
		return super.getCapability(capability, facing);
	}
	
	public void onRightClick(World world, EntityPlayer player, EnumHand hand) {
		IMBBean playerBean = player.getCapability(CapabilityMBBean.CAPABILTY_MB_BEAN, null);
		this.bean.setFacing(playerBean.getFacing());
		this.bean.setMB(playerBean.getMB());
		this.markForUpdate();
	}
	
	@Override
	public boolean hasFastRenderer(){
		return true;
	}
}
