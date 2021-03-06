package com.mhfs.capacitors.gui.manual;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

public class CraftingPage implements IPage {

	private String[] in;
	private String out;
	private int outAmnt;
	
	private ItemStack tempToDraw;
	
	private static ResourceLocation background = new ResourceLocation("big_capacitors:textures/other/crafting_page.png");
	private final static int ITEM_SIZE = 16;
	private final static int TEXTURE_WIDTH = 52;
	private final static int TEXTURE_FRAME_SIZE = 2;

	public CraftingPage(String[] input, String output, int outputAmount) {
		this.in = input;
		this.out = output;
		this.outAmnt = outputAmount;
	}

	@Override
	public void onInit(Minecraft mc, GuiManualChapter screen, int xPos,
			int yPos, int width, int height) {
	}

	@Override
	public void drawPage(Minecraft mc, GuiManualChapter screen, int xPos,
			int yPos, int width, int height, int mouseX, int mouseY) {
		FontRenderer fr = mc.fontRenderer;
		TextureManager tm = mc.getTextureManager();
		RenderItem ri = RenderItem.getInstance();
		int x = (int) (xPos + width / 2 - TEXTURE_WIDTH / 2);
		int y = yPos + GuiManual.MARGIN;
		
		doGLStuff();
		drawCraftingGrid(mc, screen, x, y);
		
		x = (int) (xPos + width / 2 - ITEM_SIZE / 2);
		y += ITEM_SIZE / 2 + TEXTURE_FRAME_SIZE;
		ItemStack outputStack = new ItemStack(
				(Item) Item.itemRegistry.getObject(out), outAmnt);
		ri.renderItemAndEffectIntoGUI(fr, tm, outputStack, x, y);
		fr.setUnicodeFlag(false);
		ri.renderItemOverlayIntoGUI(fr, tm, outputStack, x, y);
		fr.setUnicodeFlag(true);
		checkDraw(x, y, mouseX, mouseY, outputStack);
		
		x = (xPos + width / 2 - TEXTURE_WIDTH / 2);
		y += ITEM_SIZE * 2 + 2;
		
		int xID = 0;
		int yID = 0;		
		for (String itemName : in) {
			if (!itemName.equals("")) {
				ItemStack inputStack = null;
				if (itemName.startsWith("oredict")) {
					String name = itemName.split(":")[1];
					inputStack = OreDictionary.getOres(name).get(0);
				} else {
					inputStack = new ItemStack(
							(Item) Item.itemRegistry.getObject(itemName));
				}
				doGLStuff();
				int tmpX = xID * (ITEM_SIZE + TEXTURE_FRAME_SIZE) + x;
				int tmpY = yID * (ITEM_SIZE + TEXTURE_FRAME_SIZE) + y;
				ri.renderItemAndEffectIntoGUI(fr, tm, inputStack, tmpX, tmpY);
				checkDraw(tmpX, tmpY, mouseX, mouseY, inputStack);
			}
			xID++;
			if (xID == 3) {
				xID = 0;
				yID++;
			}
		}
	}
	
	private void checkDraw(int x, int y, int mouseX, int mouseY, ItemStack stack){
		if(x <= mouseX && x + ITEM_SIZE >= mouseX){
			if(y <= mouseY && y + ITEM_SIZE >= mouseY){
				tempToDraw = stack;
			}
		}
	}

	private void drawCraftingGrid(Minecraft mc, GuiManualChapter screen, int x,
			int y) {
		mc.renderEngine.bindTexture(background);
		screen.drawTexturedModalRect(x, y, 0, 0, 52, 96);
	}
	
	private void doGLStuff(){
		GL11.glColor4f(1F, 1F, 1F, 1F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void onUnload(Minecraft mc, GuiManualChapter screen, int xPos,
			int yPos, int width, int height) {
	}

	@Override
	public void actionPerformed(GuiButton button, Minecraft mc,
			GuiManualChapter screen) {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void drawMouseRelated(Minecraft mc, GuiManualChapter screen, int mouseX, int mouseY) {
		if(tempToDraw == null)return;
		List toolTip = tempToDraw.getTooltip(mc.thePlayer, false);
		screen.drawHoverText(toolTip, mouseX, mouseY);
		tempToDraw = null;
	}

}
