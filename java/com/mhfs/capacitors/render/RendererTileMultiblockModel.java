package com.mhfs.capacitors.render;

import java.util.Set;

import org.lwjgl.opengl.GL11;

import com.mhfs.capacitors.capabilities.CapabilityMBBean;
import com.mhfs.capacitors.capabilities.IMBBean;
import com.mhfs.capacitors.misc.DefinedBlock;
import com.mhfs.capacitors.misc.Multiblock;
import com.mhfs.capacitors.tile.TileMultiblockRender;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.animation.FastTESR;

public class RendererTileMultiblockModel extends FastTESR<TileMultiblockRender> {
	
	protected static BlockRendererDispatcher blockRenderer;

	@Override
	public void renderTileEntityFast(TileMultiblockRender te, double x, double y, double z, float partialTicks, int destroyStage, VertexBuffer vb) {
		IMBBean bean = te.getCapability(CapabilityMBBean.CAPABILTY_MB_BEAN, null);
		Multiblock mb = bean.getMB();
		EnumFacing facing = bean.getFacing();
		
		if(blockRenderer == null) blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
		if(Math.random() > 0.95){
			x += boundedRand(0.01);
			y += boundedRand(0.01);
			z += boundedRand(0.01);
		}
		if(mb == null) return;
		Set<DefinedBlock> blocks = mb.getBlocks(te.getPos(), facing);
		GL11.glEnable(GL11.GL_BLEND);
		for (DefinedBlock block : blocks) {
			if(block.getBlockType().equals(Blocks.AIR)) continue;
			vb.setTranslation(x, y, z);
			@SuppressWarnings("deprecation")
			IBlockState state = block.getBlockType().getStateFromMeta(block.getMetadata());
			IBakedModel model = blockRenderer.getBlockModelShapes().getModelForState(state);
        	blockRenderer.getBlockModelRenderer().renderModel(te.getWorld(), model, state, block, vb, false);
		}
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	private double boundedRand(double maxFromZero){
		return Math.random() * (2 * maxFromZero) - maxFromZero;
	}
}
