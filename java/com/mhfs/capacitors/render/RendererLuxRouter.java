package com.mhfs.capacitors.render;

import java.util.Iterator;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import com.mhfs.capacitors.misc.BlockPos;
import com.mhfs.capacitors.tile.lux.TileLuxRouter;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class RendererLuxRouter extends RendererOBJ {

	public RendererLuxRouter(ResourceLocation model, ResourceLocation texture) {
		super(model, texture);
	}

	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float partialTick) {
		super.renderTileEntityAt(entity, x, y, z, partialTick);
		TileLuxRouter router = (TileLuxRouter) entity;
		BlockPos local = router.getPosition();
		Set<BlockPos> destinations = router.getConnections();
		Set<BlockPos> poweredConnections = router.getPoweredConnections();
		Iterator<BlockPos> it = destinations.iterator();
		while (it.hasNext()) {
			BlockPos foreign = it.next();
			renderConnection(local.getVektor(foreign), x, y, z, poweredConnections.contains(foreign));
		}
	}

	private void renderConnection(BlockPos vektor, double x, double y, double z, boolean powered) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);

		GL11.glPushMatrix();

		GL11.glTranslated(x, y, z);
		GL11.glTranslated(0.5, 0.5, 0.5);
		
		double length = vektor.getLength();
		double tan = Math.atan(vektor.y / Math.sqrt(vektor.x * vektor.x + vektor.z * vektor.z));
		if(tan < 0){
			tan += Math.PI;
		}
		double beta = (tan / Math.PI)*180;		
		
		if(vektor.y < 0){
			double angel = 90 + beta;
			GL11.glRotated(angel, -vektor.z, 0, vektor.x);
		}else{
			double angel = 90 - beta;
			GL11.glRotated(angel, vektor.z, 0, -vektor.x);
		}
		
		float thickness = powered?0.1F:0.05F;
		
		GL11.glColor4d(1, 1, 1, 0.5);
		
		Tessellator tes = Tessellator.instance;
		tes.startDrawingQuads();
		tes.addVertex(-thickness, 0, 0);
		tes.addVertex(thickness, 0, 0);
		tes.addVertex(thickness, length, 0);
		tes.addVertex(-thickness, length, 0);

		tes.addVertex(-thickness, 0, 0);
		tes.addVertex(-thickness, length, 0);
		tes.addVertex(thickness, length, 0);
		tes.addVertex(thickness, 0, 0);

		tes.addVertex(0, 0, -thickness);
		tes.addVertex(0, 0, thickness);
		tes.addVertex(0, length, thickness);
		tes.addVertex(0, length, -thickness);

		tes.addVertex(0, 0, -thickness);
		tes.addVertex(0, length, -thickness);
		tes.addVertex(0, length, thickness);
		tes.addVertex(0, 0, thickness);
		tes.draw();

		GL11.glPopMatrix();

		GL11.glColor3f(1, 1, 1);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

}
