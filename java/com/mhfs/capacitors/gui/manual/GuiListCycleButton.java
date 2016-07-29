package com.mhfs.capacitors.gui.manual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiListCycleButton<T> extends GuiButton{
	
	private int index;
	private T[] values;

	public GuiListCycleButton(int buttonId, int x, int y, T[] values, Minecraft mc) {
		super(buttonId, x, y, "Kp");
		this.values = values;
		int width = 0;
		for(T entry : values){
			width = Math.max(width, mc.fontRendererObj.getStringWidth(entry.toString()));
		}
		this.width = width;
		this.index = 0;
		this.displayString = values[index].toString();
	}
	
	public void onPress(){
		this.index++;
		if(this.index >= values.length){
			this.index = 0;
		}
		this.displayString = values[index].toString();
	}
	
	public T getSelected(){
		return this.values[this.index];
	}
}
