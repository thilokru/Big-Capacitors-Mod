package com.mhfs.capacitors.network;

import java.util.HashMap;

import com.google.gson.Gson;
import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.misc.Lo;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ConfigUpdateMessage implements IMessage, IMessageHandler<ConfigUpdateMessage, IMessage>{
	
	private HashMap<String, Double> dielectrica, voltages;
	
	public ConfigUpdateMessage(HashMap<String, Double> dielectricitiesFromConfig, HashMap<String, Double> voltagesFromConfig){
		this.dielectrica = dielectricitiesFromConfig;
		this.voltages = voltagesFromConfig;
	}
	
	/**
	 * For receiving purpouses only!
	 */
	public ConfigUpdateMessage(){}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void fromBytes(ByteBuf buf) {
		int size = buf.readInt();
		byte[] bytes = new byte[size];
		buf.readBytes(bytes);
		String json = new String(bytes);
		Gson gson = new Gson();
		Class<? extends HashMap> clazz = new HashMap<String, Double>().getClass();
		
		dielectrica = gson.fromJson(json, clazz);
		
		size = buf.readInt();
		bytes = new byte[size];
		buf.readBytes(bytes);
		json = new String(bytes);
		
		voltages = gson.fromJson(json, clazz);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		Gson gson = new Gson();		
		String json = gson.toJson(dielectrica);
		byte[] bytes = json.getBytes();
		buf.writeInt(bytes.length);
		buf.writeBytes(json.getBytes());
		
		json = gson.toJson(voltages);
		bytes = json.getBytes();
		buf.writeInt(bytes.length);
		buf.writeBytes(json.getBytes());
	}
	
	public HashMap<String, Double> getDielectrica(){
		return dielectrica;
	}
	
	public HashMap<String, Double> getVoltages(){
		return voltages;
	}
	
	@Override
	public IMessage onMessage(ConfigUpdateMessage message,
			MessageContext ctx) {
		Lo.g.info("Receiving server configuration...");
		BigCapacitorsMod.instance.dielectricities = message.getDielectrica();
		BigCapacitorsMod.instance.voltages = message.getVoltages();
		return null;
	}

}
