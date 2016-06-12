package com.mhfs.capacitors.handlers;

import java.util.List;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.gui.manual.GuiManualChapter;
import com.mhfs.capacitors.gui.manual.IPage;
import com.mhfs.capacitors.knowledge.IKnowledgeRegistry;
import com.mhfs.capacitors.misc.IChapterRelated;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		/**if (FMLCommonHandler.instance().getSide().isClient()){
			SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();
			soundHandler.playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("big_capacitors:pageTurn"), 1.0F));
			Block block = world.getBlock(x, y, z);
			if(block instanceof IChapterRelated){
				IChapterRelated rel = (IChapterRelated)block;
				if(rel.getChapter() == null){
					return getDisplayChapter(KnowledgeRegistry.INSTANCE.getIndex());
				}
				return getDisplayChapter(KnowledgeRegistry.INSTANCE.getChapter(rel.getChapter()));
			}
			return getDisplayChapter(KnowledgeRegistry.INSTANCE.getIndex());
		}**/
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		IKnowledgeRegistry reg = BigCapacitorsMod.instance.knowledge;
		SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();
		soundHandler.playSound(PositionedSoundRecord.create(new ResourceLocation("big_capacitors:pageTurn"), 1.0F));
		if(ID == 1){
			BlockPos pos = new BlockPos(x, y, z);
			Block block = world.getBlockState(pos).getBlock();
			if(block instanceof IChapterRelated){
				IChapterRelated rel = (IChapterRelated)block;
				if(rel.getChapter(world, pos) == null){
					return getDisplayChapter(reg.getIndex());
				}
				return getDisplayChapter(reg.getChapter(rel.getChapter(world, pos)));
			}
		}
		return getDisplayChapter(reg.getIndex());
	}
	
	private Object getDisplayChapter(List<IPage> chapter){
		return new GuiManualChapter(null, chapter);
	}

}
