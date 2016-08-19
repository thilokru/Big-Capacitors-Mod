package com.mhfs.api.manual.gui.pages;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.mhfs.api.manual.gui.GuiManualChapter;
import com.mhfs.api.manual.util.IPage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;

public class TablePage implements IPage {
	
	private String[][] data;
	private int[] cellwidths;
	
	public TablePage(String[][] data) {
		this.data = data;
		cellwidths = new int[data.length];
	}

	@Override
	public void onInit(Minecraft mc, GuiManualChapter screen, int xPos, int yPos, int width, int height) {
		for(int x = 0; x < data.length; x++){
			int maxWidth = 0;
			for(int y = 0; x < data[x].length; y++){
				maxWidth = Math.max(maxWidth, mc.fontRendererObj.getStringWidth(data[x][y]));
			}
			cellwidths[x] = maxWidth;
		}
	}

	@Override
	public void drawPage(Minecraft mc, GuiManualChapter screen, int xPos, int yPos, int width, int height, int mouseX, int mouseY) {
		
		FontRenderer fr = mc.fontRendererObj;
		int lineWidth = 1;
		int totalHeight = (data[0].length + 1) * lineWidth + fr.FONT_HEIGHT * data[0].length;
		
		//Vertical
		int xTemp = xPos;
		int totalCellWidth = 0;
		
		drawLine(Tessellator.getInstance().getBuffer(), xTemp, yPos, xTemp, yPos + totalHeight, Color.BLACK);
		
		for(int cellWidth : cellwidths) {
			xTemp += cellWidth;
			totalCellWidth += cellWidth;
			
			drawLine(Tessellator.getInstance().getBuffer(), xTemp, yPos, xTemp, yPos + totalHeight, Color.BLACK);
		}
		
		//Horizontal
		int yTemp = yPos;
		drawLine(Tessellator.getInstance().getBuffer(), xPos, yTemp, xPos + totalCellWidth, yTemp, Color.BLACK);
		
		for(int i = 0; i < data[0].length; i++) {
			yTemp += fr.FONT_HEIGHT + lineWidth;
			drawLine(Tessellator.getInstance().getBuffer(), xPos, yTemp, xPos + totalCellWidth, yTemp, Color.BLACK);
		}
		
		//Cells
		xTemp = xPos + lineWidth;
		for(int x = 0; x < data.length; x++) {
			yTemp = yPos + lineWidth;
			for(int y = 0; y < data[x].length; y++) {
				//Actual text render
				fr.drawString(data[x][y], xTemp, yTemp, Color.BLACK.getRGB());
				
				yTemp += fr.FONT_HEIGHT + lineWidth;
			}
			xTemp += cellwidths[x] + lineWidth;
		}
	}
	
	private void drawLine(VertexBuffer buffer, int x1, int y1, int x2, int y2, Color color){
		buffer.begin(GL11.GL_LINES, new VertexFormat());
		buffer.color(color.getGreen(), color.getGreen(), color.getBlue(), 255);
		buffer.pos(x1, y1, 0).endVertex();
		buffer.pos(x2, y2, 0).endVertex();
		buffer.finishDrawing();
	}

	@Override
	public void drawMouseRelated(Minecraft mc, GuiManualChapter screen, int mouseX, int mouseY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnload(Minecraft mc, GuiManualChapter screen, int xPos, int yPos, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(GuiButton button, Minecraft mc, GuiManualChapter screen) {
		// TODO Auto-generated method stub

	}

}
