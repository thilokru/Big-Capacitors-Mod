package com.mhfs.capacitors.capabilities;

import java.util.concurrent.Callable;

import com.mhfs.capacitors.misc.Multiblock;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityMBBean {
	
	@CapabilityInject(IMBBean.class)
	public static Capability<IMBBean> CAPABILTY_MB_BEAN;

	static {
		CapabilityManager.INSTANCE.register(IMBBean.class, new Serializer(), new Callable<IMBBean>(){
			public IMBBean call() throws Exception {return null;}
		});
	}
	
	private static class Serializer implements IStorage<IMBBean> {
		public NBTBase writeNBT(Capability<IMBBean> capability, IMBBean instance, EnumFacing side) { 
			NBTTagCompound tag = new NBTTagCompound(); 
			if(instance.getMB() != null)
				tag.setString("mb", Multiblock.getRegistryName(instance.getMB()).toString());
			if(instance.getFacing() != null)
				tag.setInteger("facing", instance.getFacing().getIndex());
			return tag;
		}
		
		public void readNBT(Capability<IMBBean> capability, IMBBean instance, EnumFacing side, NBTBase nbt) {
			NBTTagCompound tag = (NBTTagCompound)nbt;
			if(tag.hasKey("mb")) {
				instance.setMB(Multiblock.getMultiblock(new ResourceLocation(tag.getString("mb")), null));
			} else {
				instance.setMB(null);
			}
			
			if(tag.hasKey("facing")) {
				instance.setFacing(EnumFacing.getFront(tag.getInteger("facing")));
			} else {
				instance.setFacing(null);
			}
		}
	}
}
