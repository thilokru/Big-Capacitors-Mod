package com.mhfs.api.lux;

import java.util.concurrent.Callable;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class LuxAPI {
	
	@CapabilityInject(IRouting.class)
	public static Capability<IRouting> ROUTING_CAPABILITY;
	
	@CapabilityInject(ILuxHandler.class)
	public static Capability<ILuxHandler> LUX_FLOW_CAPABILITY;
	
	static{
		
		CapabilityManager.INSTANCE.register(IRouting.class, new RoutingStorage(), new Callable<IRouting>(){
			public IRouting call() throws Exception {
				return null;
			}
		});
		
		CapabilityManager.INSTANCE.register(ILuxHandler.class, new LuxHandlerStorage(), new Callable<ILuxHandler>(){
			public ILuxHandler call() throws Exception {
				return null;
			}
		});
	}
}
