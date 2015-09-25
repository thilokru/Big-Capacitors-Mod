package com.mhfs.capacitors.gui;

import java.util.Set;

import com.mhfs.capacitors.BigCapacitorsMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.GuiConfig;

public class GuiFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft minecraftInstance) {

	}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return BigCapacitorsConfigGui.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(
			RuntimeOptionCategoryElement element) {
		// TODO Auto-generated method stub
		return null;
	}

	public static class BigCapacitorsConfigGui extends GuiConfig {
		public BigCapacitorsConfigGui(GuiScreen parent) {
			super(parent,
	        		new ConfigElement<Object>(BigCapacitorsMod.instance.config.getCategory("dielectricities")).getChildElements(),
	        		BigCapacitorsMod.modid, GuiConfig.getAbridgedConfigPath(BigCapacitorsMod.instance.config.toString()), true, false, "Dielectricities");
		}
	}

}
