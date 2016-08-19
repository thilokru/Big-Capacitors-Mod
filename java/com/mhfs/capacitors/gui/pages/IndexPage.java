package com.mhfs.capacitors.gui.pages;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import com.mhfs.api.manual.gui.GuiButtonLink;
import com.mhfs.api.manual.gui.GuiManual;
import com.mhfs.api.manual.gui.GuiManualChapter;
import com.mhfs.api.manual.knowledge.IManual;
import com.mhfs.api.manual.util.IKeyboardHandler;
import com.mhfs.api.manual.util.IPage;
import com.mhfs.capacitors.BigCapacitorsMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;

public class IndexPage implements IPage, IKeyboardHandler {

	private String[] entries;
	private List<GuiButton> myButtons;
	private GuiTextField searchField;
	private Minecraft mc;
	private int xPos, yPos;

	public IndexPage(String[] entries) {
		this.entries = entries;
		Arrays.sort(entries);
	}

	@Override
	public void onInit(Minecraft mc, GuiManualChapter screen, int xPos,
			int yPos, int width, int height) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.mc = mc;

		searchField = new BetterTextBox(-1, mc.fontRendererObj, xPos, yPos - 5);
		searchField.setEnableBackgroundDrawing(false);
		searchField.setTextColor(Color.BLACK.getRGB());
		searchField.setVisible(false);

		createButtons(entries, screen);
	}

	private void createButtons(String[] names, GuiManualChapter screen) {
		myButtons = new ArrayList<GuiButton>();
		int tmpY = yPos + GuiManual.MARGIN;
		int id = 0;
		for (String name : names) {
			GuiButton button = new GuiButtonLink(id, xPos, tmpY, name);
			screen.addButton(button);
			myButtons.add(button);
			tmpY += mc.fontRendererObj.FONT_HEIGHT + 2;
			id++;
		}
	}

	@Override
	public void onUnload(Minecraft mc, GuiManualChapter screen, int xPos,
			int yPos, int width, int height) {
		for (GuiButton button : myButtons) {
			screen.removeButton(button);
		}
	}

	@Override
	public void actionPerformed(GuiButton button, Minecraft mc,
			GuiManualChapter screen) {
		String buttonText = button.displayString;
		IManual manual = BigCapacitorsMod.instance.knowledge;
		List<IPage> chapter = manual.getChapter(buttonText);
		if (chapter == null)
			return;
		mc.displayGuiScreen(new GuiManualChapter(screen, manual.getTextureLocation(), chapter));
	}

	@Override
	public void drawPage(Minecraft mc, GuiManualChapter screen, int xPos,
			int yPos, int width, int height, int mouseX, int mouseY) {
		mc.fontRendererObj.setUnicodeFlag(false);
		searchField.drawTextBox();
		mc.fontRendererObj.setUnicodeFlag(true);
		searchField.updateCursorCounter();
	}

	@Override
	public void drawMouseRelated(Minecraft mc, GuiManualChapter screen,
			int mouseX, int mouseY) {
	}

	@Override
	public void onKeyDown(GuiManualChapter screen, char key, int keyCode) {
		searchField.setFocused(true);
		searchField.setVisible(true);
		searchField.textboxKeyTyped(key, keyCode);
		if (searchField.getText().equals("")) {
			searchField.setVisible(false);
		}
		for (GuiButton button : myButtons) {
			screen.removeButton(button);
		}
		ArrayList<String> filtered = new ArrayList<String>();
		for (String s : entries) {
			if (s.startsWith(searchField.getText())) {
				filtered.add(s);
			}
		}
		createButtons(filtered.toArray(new String[0]), screen);
	}

	public class BetterTextBox extends GuiTextField {

		public BetterTextBox(int id, FontRenderer fr, int x, int y) {
			super(id, fr, x, y, 50, 10);
			this.setMaxStringLength(12);
		}

		@Override
		public void drawTextBox() {
			if (!this.getVisible())
				return;
			GL11.glColor4f(1, 1, 1, 1);
			GuiManual.TEXTURES.drawTextureAt(mc, "search_box", this.xPosition - 2, this.yPosition - 2);

			FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
			fr.setUnicodeFlag(true);
			fr.drawString(this.getText(), this.xPosition, this.yPosition,
					Color.BLACK.getRGB());
			fr.setUnicodeFlag(false);
		}

	}

}
