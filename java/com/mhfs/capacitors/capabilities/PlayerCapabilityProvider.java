package com.mhfs.capacitors.capabilities;

import com.mhfs.capacitors.misc.Multiblock;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class PlayerCapabilityProvider implements ICapabilityProvider{
	
	private IMBBean bean;
	
	public PlayerCapabilityProvider(){
		bean = new Bean();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if(capability == CapabilityMBBean.CAPABILTY_MB_BEAN){
			return true;
		}
		return false;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityMBBean.CAPABILTY_MB_BEAN){
			return CapabilityMBBean.CAPABILTY_MB_BEAN.cast(bean);
		}
		return null;
	}
	
	public static class Bean implements IMBBean {
		
		private Multiblock mb;
		private EnumFacing facing;

		@Override
		public void setFacing(EnumFacing facing) {
			this.facing = facing;
		}

		@Override
		public EnumFacing getFacing() {
			return facing;
		}

		@Override
		public void setMB(Multiblock mb) {
			this.mb = mb;
		}

		@Override
		public Multiblock getMB() {
			return mb;
		}
		
	}
}
