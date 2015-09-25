package com.mhfs.capacitors.network;

import java.util.HashMap;

import net.minecraft.block.Block;

import com.google.gson.Gson;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class ConfigUpdateMessage implements IMessage{
	
	private HashMap<Block, Double> dielectrica, voltages;
	
	public ConfigUpdateMessage(HashMap<Block, Double> dielectrica, HashMap<Block, Double> voltages){
		this.dielectrica = dielectrica;
		this.voltages = voltages;
	}
	
	/**
	 * For receiving purpouses only!
	 */
	public ConfigUpdateMessage(){}

	@SuppressWarnings("unchecked")
	@Override
	public void fromBytes(ByteBuf buf) {
		dielectrica = new HashMap<Block, Double>();
		int size = buf.readInt();
		byte[] bytes = new byte[size];
		buf.readBytes(bytes);
		String json = new String(bytes);
		Gson gson = new Gson();
		
		HashMap<String, Double> transmitted = gson.fromJson(json, new HashMap<String, Double>().getClass());
		for(String name:transmitted.keySet()){
			Block block = Block.getBlockFromName(name);
			dielectrica.put(block, transmitted.get(name));
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		Gson gson = new Gson();
		HashMap<String, Double> toSend = new HashMap<String, Double>();
		for(Block block:dielectrica.keySet()){
			String name = Block.blockRegistry.getNameForObject(block);
			toSend.put(name, dielectrica.get(block));
		}
		
		String json = gson.toJson(toSend);
		byte[] bytes = json.getBytes();
		buf.writeInt(bytes.length);
		buf.writeBytes(json.getBytes());
		
		toSend = new HashMap<String, Double>();
		for(Block block:voltages.keySet()){
			String name = Block.blockRegistry.getNameForObject(block);
			toSend.put(name, voltages.get(block));
		}
		
		json = gson.toJson(toSend);
		bytes = json.getBytes();
		buf.writeInt(bytes.length);
		buf.writeBytes(json.getBytes());
	}
	
	public HashMap<Block, Double> getDielectrica(){
		return dielectrica;
	}
	
	public HashMap<Block, Double> getVoltages(){
		return voltages;
	}

}
