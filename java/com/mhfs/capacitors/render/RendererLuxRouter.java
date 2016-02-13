package com.mhfs.capacitors.render;

import java.util.Iterator;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import com.mhfs.capacitors.tile.lux.TileLuxRouter;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;

public class RendererLuxRouter<T extends TileEntity> extends TileEntitySpecialRenderer<T> {

	public void renderTileEntityAt(T entity, double x, double y, double z, float partialTicks, int destroyStage) {
		TileLuxRouter router = (TileLuxRouter) entity;
		BlockPos local = router.getPosition();
		Set<BlockPos> destinations = router.getConnections();
		Iterator<BlockPos> it = destinations.iterator();
		while (it.hasNext()) {
			BlockPos foreign = it.next();
			renderConnection(foreign.subtract(local), x, y, z);
		}
	}

	private void renderConnection(Vec3i vektor, double x, double y, double z) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);

		GL11.glPushMatrix();

		GL11.glTranslated(x, y, z);
		GL11.glTranslated(0.5, 0.5, 0.5);
		
		double length = Math.sqrt(Math.pow(vektor.getX(), 2) + Math.pow(vektor.getY(), 2) + Math.pow(vektor.getZ(), 2));
		double tan = Math.atan(vektor.getY() / Math.sqrt(vektor.getX() * vektor.getX() + vektor.getZ() * vektor.getZ()));
		if(tan < 0){
			tan += Math.PI;
		}
		double beta = (tan / Math.PI)*180;		
		
		if(vektor.getY() < 0){
			double angel = 90 + beta;
			GL11.glRotated(angel, -vektor.getZ(), 0, vektor.getX());
		}else{
			double angel = 90 - beta;
			GL11.glRotated(angel, vektor.getZ(), 0, -vektor.getX());
		}
		
		float thickness = 0.05F;
		
		GL11.glColor4d(1, 1, 1, 0.5);
		
		Tessellator tes = Tessellator.getInstance();
		WorldRenderer wr = tes.getWorldRenderer();
		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		wr.pos(-thickness, 0, 0).endVertex();
		wr.pos(thickness, 0, 0).endVertex();
		wr.pos(thickness, length, 0).endVertex();
		wr.pos(-thickness, length, 0).endVertex();

		wr.pos(-thickness, 0, 0).endVertex();
		wr.pos(-thickness, length, 0).endVertex();
		wr.pos(thickness, length, 0).endVertex();
		wr.pos(thickness, 0, 0).endVertex();

		wr.pos(0, 0, -thickness).endVertex();
		wr.pos(0, 0, thickness).endVertex();
		wr.pos(0, length, thickness).endVertex();
		wr.pos(0, length, -thickness).endVertex();

		wr.pos(0, 0, -thickness).endVertex();
		wr.pos(0, length, -thickness).endVertex();
		wr.pos(0, length, thickness).endVertex();
		wr.pos(0, 0, thickness).endVertex();
		tes.draw();

		GL11.glPopMatrix();

		GL11.glColor3f(1, 1, 1);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

}
