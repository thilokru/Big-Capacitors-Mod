package com.mhfs.capacitors.gui.manual;

import java.util.HashMap;
import java.util.Map;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.misc.Multiblock;
import com.mhfs.capacitors.network.GuiActionMessage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class MultiblockPage implements IPage{
	
	private Multiblock mb;
	private Map<EnumFacing, ResourceLocation> actions;
	private GuiListCycleButton<EnumFacing> orientation;
	private GuiButton startRender;

	public MultiblockPage(String mbLoc, String orientatedActions, IResourceManager manager) {
		if(orientatedActions.trim().length() == 0) {
			throw new IllegalArgumentException(String.format("Multiblock '%s' has no gui actions specified. They are required!", mbLoc));
		}
		
		mb = Multiblock.getMultiblock(new ResourceLocation(mbLoc), manager);
		actions = new HashMap<EnumFacing, ResourceLocation>();
		
		String[] pairs = orientatedActions.split(";");
		if(pairs.length == 1) {
			ResourceLocation rl = new ResourceLocation(pairs[0]);
			for (EnumFacing facing : EnumFacing.values()) {
				actions.put(facing, rl);
			}
			return;
		} else {
			for (String pair : pairs) {
				String[] sections = pair.split(" ");
				if(sections.length != 2) {
					throw new IllegalArgumentException(String.format("Invalid action descriptor '%s'. Too many spaces!", pair));
				}
				EnumFacing facing = EnumFacing.byName(sections[0]);
				ResourceLocation location = new ResourceLocation(sections[1]);
				actions.put(facing, location);
			}
		}
 	}

	@Override
	public void onInit(Minecraft mc, GuiManualChapter screen, int xPos, int yPos, int width, int height) {
		EnumFacing[] validDirections = actions.keySet().toArray(new EnumFacing[0]);
		this.orientation = new GuiListCycleButton<EnumFacing>(0, xPos + GuiManual.MARGIN, yPos + GuiManual.MARGIN, validDirections, mc);
		screen.addButton(this.orientation);
		
		this.startRender = new GuiButton(1, xPos, yPos + GuiManual.MARGIN, I18n.format("manual.show"));
		this.startRender.width = mc.fontRendererObj.getStringWidth(this.startRender.displayString);
		this.startRender.xPosition = this.orientation.xPosition + this.orientation.width + GuiManual.MARGIN;
		screen.addButton(startRender);
	}

	@Override
	public void drawPage(Minecraft mc, GuiManualChapter screen, int xPos, int yPos, int width, int height, int mouseX, int mouseY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawMouseRelated(Minecraft mc, GuiManualChapter screen, int mouseX, int mouseY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnload(Minecraft mc, GuiManualChapter screen, int xPos, int yPos, int width, int height) {
		screen.removeButton(orientation);
		screen.removeButton(startRender);		
	}

	@Override
	public void actionPerformed(GuiButton button, Minecraft mc, GuiManualChapter screen) {
		if(button == orientation){
			orientation.onPress();
		} else if (button == startRender) {
			BigCapacitorsMod.instance.network.sendToServer(new GuiActionMessage(actions.get(orientation.getSelected())));
			mc.displayGuiScreen(null);
		}
		
	}

}
