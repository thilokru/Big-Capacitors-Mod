package com.mhfs.capacitors.gui.manual;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

public class CraftingPage implements IPage {

	private String[] in;
	private String out;

	private ItemStack tempToDraw;

	private static ResourceLocation background = new ResourceLocation("big_capacitors:textures/other/crafting_page.png");
	private final static int ITEM_SIZE = 16;
	private final static int TEXTURE_WIDTH = 52;
	private final static int TEXTURE_FRAME_SIZE = 2;

	public CraftingPage(String[] input, String output) {
		this.in = input;
		this.out = output;
	}

	@Override
	public void onInit(Minecraft mc, GuiManualChapter screen, int xPos, int yPos, int width, int height) {
	}

	@Override
	public void drawPage(Minecraft mc, GuiManualChapter screen, int xPos, int yPos, int width, int height, int mouseX, int mouseY) {
		FontRenderer fr = mc.fontRendererObj;
		RenderItem ri = mc.getRenderItem();
		int x = (int) (xPos + width / 2 - TEXTURE_WIDTH / 2);
		int y = yPos + GuiManual.MARGIN;

		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glDisable(GL11.GL_LIGHTING);
		drawCraftingGrid(mc, screen, x, y);

		x = (int) (xPos + width / 2 - ITEM_SIZE / 2);
		y += ITEM_SIZE / 2 + TEXTURE_FRAME_SIZE;

		ItemStack outputStack = parseFromString(out);
		RenderHelper.enableGUIStandardItemLighting();
		ri.renderItemAndEffectIntoGUI(outputStack, x, y);
		fr.setUnicodeFlag(false);
		ri.renderItemOverlays(fr, outputStack, x, y);
		fr.setUnicodeFlag(true);
		checkDraw(x, y, mouseX, mouseY, outputStack);

		x = (xPos + width / 2 - TEXTURE_WIDTH / 2);
		y += ITEM_SIZE * 2 + 2;

		int xID = 0;
		int yID = 0;
		for (String desc : in) {
			if (!desc.equals("")) {
				ItemStack inputStack = parseFromString(desc);
				if(inputStack == null)continue;
				int tmpX = xID * (ITEM_SIZE + TEXTURE_FRAME_SIZE) + x;
				int tmpY = yID * (ITEM_SIZE + TEXTURE_FRAME_SIZE) + y;
				ri.renderItemAndEffectIntoGUI(inputStack, tmpX, tmpY);
				checkDraw(tmpX, tmpY, mouseX, mouseY, inputStack);
			}
			xID++;
			if (xID == 3) {
				xID = 0;
				yID++;
			}
		}
	}

	private ItemStack parseFromString(String input) {
		String[] args = input.split(" ");
		ResourceLocation resourcelocation = new ResourceLocation(args[0]);
		
		//Determine Item type
		String itemName = args[0];
		if (itemName.startsWith("oredict")) {
			String name = itemName.split(":")[1];
			ItemStack stack = OreDictionary.getOres(name).get(0);
			if(args.length == 2){
				stack.stackSize = Integer.parseInt(args[1]);
			}
			return stack;
		} else {
			Item item = (Item) Item.REGISTRY.getObject(resourcelocation);
			if(item == null)return null;
			
			//Parse meta and count (optional)
			int count = 1;
			int meta = 0;
			if(args.length == 2){
				count = Integer.parseInt(args[1]);
			}else if(args.length == 3){
				meta = Integer.parseInt(args[2]);
				
			}
			
			return new ItemStack(item, count, meta);
		}
	}

	private void checkDraw(int x, int y, int mouseX, int mouseY, ItemStack stack) {
		if (x <= mouseX && x + ITEM_SIZE >= mouseX) {
			if (y <= mouseY && y + ITEM_SIZE >= mouseY) {
				tempToDraw = stack;
			}
		}
	}

	private void drawCraftingGrid(Minecraft mc, GuiManualChapter screen, int x, int y) {
		mc.renderEngine.bindTexture(background);
		screen.drawTexturedModalRect(x, y, 0, 0, 52, 96);
	}

	@Override
	public void onUnload(Minecraft mc, GuiManualChapter screen, int xPos, int yPos, int width, int height) {
	}

	@Override
	public void actionPerformed(GuiButton button, Minecraft mc, GuiManualChapter screen) {
	}

	@Override
	public void drawMouseRelated(Minecraft mc, GuiManualChapter screen, int mouseX, int mouseY) {
		if (tempToDraw == null)
			return;
		List<String> toolTip = tempToDraw.getTooltip(mc.thePlayer, false);
		screen.drawHoverText(toolTip, mouseX, mouseY);
		tempToDraw = null;
	}

}
