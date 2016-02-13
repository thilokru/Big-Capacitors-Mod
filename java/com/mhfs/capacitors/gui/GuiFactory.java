package com.mhfs.capacitors.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.mhfs.capacitors.BigCapacitorsMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;

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

		private static List<IConfigElement> getConfigElements() {
			List<IConfigElement> list = new ArrayList<IConfigElement>();
			list.add(new DummyCategoryElement("Dielectricity", "config.dielec", DielecEntry.class));
			list.add(new DummyCategoryElement("Dielectric Strength", "config.strength", StregthEntry.class));
			return list;
		}

		public static class DielecEntry extends CategoryEntry {
			public DielecEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
				super(owningScreen, owningEntryList, prop);
			}

			@Override
			protected GuiScreen buildChildScreen() {
				ConfigCategory category = BigCapacitorsMod.instance.config.getCategory("dielectricities");
				List<IConfigElement> elements = new ConfigElement(category).getChildElements();
				String configPath = GuiConfig.getAbridgedConfigPath(BigCapacitorsMod.instance.config.toString());
				return new GuiConfig(this.owningScreen, elements, this.owningScreen.modID, category.getName(), true, false, configPath);
			}
		}

		public static class StregthEntry extends CategoryEntry {
			public StregthEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
				super(owningScreen, owningEntryList, prop);
			}
			
			@Override
			protected GuiScreen buildChildScreen() {
				ConfigCategory category = BigCapacitorsMod.instance.config.getCategory("voltages");
				List<IConfigElement> elements = new ConfigElement(category).getChildElements();
				String configPath = GuiConfig.getAbridgedConfigPath(BigCapacitorsMod.instance.config.toString());
				return new GuiConfig(this.owningScreen, elements, this.owningScreen.modID, category.getName(), true, false, configPath);
			}
		}
	}

}
