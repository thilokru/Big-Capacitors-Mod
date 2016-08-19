package com.mhfs.api.manual.gui;

import java.io.IOException;
import java.util.List;

import com.mhfs.api.manual.util.IKeyboardHandler;
import com.mhfs.api.manual.util.IPage;
import com.mhfs.capacitors.misc.TextureHelper.SubTexture;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class GuiManualChapter extends GuiManual {
	
	private GuiScreen parent;
	private List<IPage> chapter;
	private int page;
	protected GuiButton forward, backward, exit;
	protected SubTexture background;

	public GuiManualChapter(GuiScreen parent, ResourceLocation textureLocation, List<IPage> chapter){
		super(textureLocation);
		this.parent = parent;
		this.chapter = chapter;
		this.background = GuiManual.TEXTURES.getTextureInfo("background");
		page = 0;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		int buttonY = 2 + this.background.getHeight() - GuiManual.MARGIN * 2;
		int buttonX = this.width / 2 + this.background.getWidth() / 2 - 16 - GuiManual.MARGIN + 4;
		forward = new GuiManualButton(-3, buttonX, buttonY, GuiManualButton.Mode.FORWARD);
		this.buttonList.add(forward);

		buttonX = this.width / 2 - (this.background.getWidth() / 2) + 16 - GuiManual.MARGIN;
		backward = new GuiManualButton(-2, buttonX, buttonY, GuiManualButton.Mode.BACKWARD);
		this.buttonList.add(backward);
		
		buttonX = (this.width - GuiManual.TEXTURES.getTextureInfo("button_up_inactive").getWidth()) / 2;
		buttonY = 2 + this.background.getHeight() - 6;
		exit = new GuiManualButton(-1, buttonX, buttonY, GuiManualButton.Mode.UP);
		exit.visible = true;
		exit.enabled = true;
		this.buttonList.add(exit);
		this.updateButtons();
		onLoadPage();
	}
	
	public void drawScreen(int mouseX, int mouseY, float partialTickTime){
		this.drawBackground();
		this.preTextRender();
		super.drawScreen(mouseX, mouseY, partialTickTime);
		int xPos = (int) (((this.width - this.background.getWidth())/2) + GuiManual.BOOK_BORDER + GuiManual.MARGIN);
		int yPos = 2 + GuiManual.BOOK_BORDER + GuiManual.MARGIN;
		int height = this.background.getHeight() - GuiManual.BOOK_BORDER * 2 - GuiManual.MARGIN * 2;
		int width = (this.background.getWidth() / 2) - GuiManual.BOOK_BORDER - (GuiManual.MARGIN * 2);
		chapter.get(page).drawPage(this.mc, this, xPos, yPos, width, height, mouseX, mouseY);
		if(page + 1 < chapter.size()){
			xPos = this.width / 2 + GuiManual.MARGIN;
			chapter.get(page + 1).drawPage(mc, this, xPos, yPos, width, height, mouseX, mouseY);
		}
		this.postTextRender();
		chapter.get(page).drawMouseRelated(this.mc, this, mouseX, mouseY);
		if(page + 1 < chapter.size()){
			xPos = this.width / 2 + GuiManual.MARGIN;
			chapter.get(page + 1).drawMouseRelated(this.mc, this, mouseX, mouseY);
		}
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		if(button.id == forward.id){
			onUnloadPage();
			page += 2;
			onLoadPage();
			updateButtons();
		}else if(button.id == backward.id){
			onUnloadPage();
			page -= 2;
			onLoadPage();
			updateButtons();
		}else if(button.id == exit.id){
			onUnloadPage();
			this.mc.displayGuiScreen(parent);
		}
		chapter.get(page).actionPerformed(button, mc, this);
		if(page + 1 < chapter.size()){
			chapter.get(page + 1).actionPerformed(button, mc, this);
		}
	}
	
	private void onLoadPage(){
		int xPos = (int) (((this.width - this.background.getWidth())/2) + GuiManual.BOOK_BORDER);
		int yPos = (int) (2 + GuiManual.BOOK_BORDER);
		int height = this.background.getHeight() - GuiManual.BOOK_BORDER * 2;
		int width = this.background.getWidth() / 2 - GuiManual.BOOK_BORDER - GuiManual.MARGIN;
		chapter.get(page).onInit(mc, this, xPos, yPos, width, height);
		if(page + 1 < chapter.size()){
			xPos = this.width / 2 + GuiManual.MARGIN;
			chapter.get(page + 1).onInit(mc, this, xPos, yPos, width, height);
		}
	}
	
	private void onUnloadPage(){
		int xPos = (int) (((this.width - this.background.getWidth())/2) + GuiManual.BOOK_BORDER);
		int yPos = (int) (2 + GuiManual.BOOK_BORDER);
		int height = this.background.getHeight() - GuiManual.BOOK_BORDER * 2;
		int width = this.background.getWidth() / 2 - GuiManual.BOOK_BORDER - GuiManual.MARGIN;
		chapter.get(page).onUnload(mc, this, xPos, yPos, width, height);
		if(page + 1 < chapter.size()){
			xPos = this.width / 2 + GuiManual.MARGIN;
			chapter.get(page + 1).onUnload(mc, this, xPos, yPos, width, height);
		}
	}
	
	private void updateButtons(){
		this.backward.enabled = true;
		this.backward.visible = true;
		this.forward.enabled = true;
		this.forward.visible = true;
		
		if(page == 0){
			this.backward.enabled = false;
			this.backward.visible = false;
		}
		
		if(page + 2 > chapter.size() - 1){
			this.forward.enabled = false;
			this.forward.visible = false;
		}
		
		if(parent == null){
			exit.enabled = false;
			exit.visible = false;
		}
	}
	
	public void addButton(GuiButton button){
		this.buttonList.add(button);
	}
	
	public void removeButton(GuiButton button){
		this.buttonList.remove(button);
	}
	
	public void drawHoverText(List<String> text, int x, int y){
		this.drawHoveringText(text, x, y, this.fontRendererObj);
	}
	
	protected void keyTyped(char key, int keyCode) throws IOException{
		super.keyTyped(key, keyCode);
		IPage toTest = chapter.get(page);
		if(toTest instanceof IKeyboardHandler){
			((IKeyboardHandler)toTest).onKeyDown(this, key, keyCode);
		}
		if(page + 1 < chapter.size()){
			toTest = chapter.get(page + 1);
			if(toTest instanceof IKeyboardHandler){
				((IKeyboardHandler)toTest).onKeyDown(this, key, keyCode);
			}
		}
	}
}
