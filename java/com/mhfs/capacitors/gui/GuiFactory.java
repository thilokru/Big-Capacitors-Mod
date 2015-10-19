package com.mhfs.capacitors.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.mhfs.capacitors.BigCapacitorsMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import cpw.mods.fml.client.IModGuiFactory;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfigEntries.CategoryEntry;

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
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
		// TODO Auto-generated method stub
		return null;
	}

	public static class BigCapacitorsConfigGui extends GuiConfig {
		public BigCapacitorsConfigGui(GuiScreen parent) {
			super(parent, getConfigElements(), BigCapacitorsMod.modid, true, false, "Config");
		}

		@SuppressWarnings("rawtypes")
		private static List<IConfigElement> getConfigElements() {
			List<IConfigElement> list = new ArrayList<IConfigElement>();
			list.add(new DummyCategoryElement<Object>("Dielectricity", "config.dielec", DielecEntry.class));
			list.add(new DummyCategoryElement<Object>("Dielectric Strength", "config.strength", StregthEntry.class));
			return list;
		}

		public static class DielecEntry extends CategoryEntry {
			public DielecEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement<?> prop) {
				super(owningScreen, owningEntryList, prop);
			}

			@SuppressWarnings("rawtypes")
			@Override
			protected GuiScreen buildChildScreen() {
				ConfigCategory category = BigCapacitorsMod.instance.config.getCategory("dielectricities");
				List<IConfigElement> elements = new ConfigElement<Object>(category).getChildElements();
				String configPath = GuiConfig.getAbridgedConfigPath(BigCapacitorsMod.instance.config.toString());
				return new GuiConfig(this.owningScreen, elements, this.owningScreen.modID, category.getName(), true, false, configPath);
			}
		}

		public static class StregthEntry extends CategoryEntry {
			public StregthEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement<?> prop) {
				super(owningScreen, owningEntryList, prop);
			}

			@SuppressWarnings("rawtypes")
			@Override
			protected GuiScreen buildChildScreen() {
				ConfigCategory category = BigCapacitorsMod.instance.config.getCategory("voltages");
				List<IConfigElement> elements = new ConfigElement<Object>(category).getChildElements();
				String configPath = GuiConfig.getAbridgedConfigPath(BigCapacitorsMod.instance.config.toString());
				return new GuiConfig(this.owningScreen, elements, this.owningScreen.modID, category.getName(), true, false, configPath);
			}
		}
	}

}
