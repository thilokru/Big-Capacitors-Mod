package com.mhfs.capacitors.render;

import java.util.Iterator;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3i;
import net.minecraft.world.World;

public class RendererLux<T extends TileEntity> extends TileEntitySpecialRenderer<T> {

	public void renderTileEntityAt(T entity, double x, double y, double z, float partialTicks, int destroyStage) {
		IConnected router = (IConnected) entity;
		BlockPos local = entity.getPos();
		Set<BlockPos> destinations = router.getConnections();
		Iterator<BlockPos> it = destinations.iterator();
		while (it.hasNext()) {
			BlockPos foreign = it.next();
			if (foreign == null)
				continue;
			renderConnection(foreign.subtract(local), x, y, z);
		}
	}

	public static void createParticles(World world, BlockPos pos) {
		IConnected tile = (IConnected) world.getTileEntity(pos);
		BlockPos local = new BlockPos(pos);
		if (tile == null)
			return;

		Set<BlockPos> toRender = tile.getActiveConnections();
		if (toRender == null)
			return;

		for (BlockPos towards : toRender) {
			BlockPos vektor = towards.subtract(local);
			double length = Math.sqrt(Math.pow(vektor.getX(), 2) + Math.pow(vektor.getY(), 2) + Math.pow(vektor.getZ(), 2)) * 20;
			double xMotion = vektor.getX() / length;
			double yMotion = vektor.getY() / length;
			double zMotion = vektor.getZ() / length;
			world.spawnParticle(EnumParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, xMotion, yMotion, zMotion);
		}
		tile.resetConnectionState();
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
		if (tan < 0) {
			tan += Math.PI;
		}
		double beta = (tan / Math.PI) * 180;

		if (vektor.getY() < 0) {
			double angel = 90 + beta;
			GL11.glRotated(angel, -vektor.getZ(), 0, vektor.getX());
		} else {
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
