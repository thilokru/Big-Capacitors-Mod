package com.mhfs.capacitors.oregen;

import java.util.Random;

import com.mhfs.capacitors.Blocks;
import com.mhfs.capacitors.blocks.BlockMany;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

public class OreGen implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.getDimension() == 0) {
			IBlockState oreState = Blocks.blockMany.getBlockState().getBaseState().withProperty(BlockMany.TYPE, 3);
			generate(oreState, 20, 8, 40,
					world, random, chunkX, chunkZ);
			oreState = Blocks.blockMany.getBlockState().getBaseState().withProperty(BlockMany.TYPE, 4);
			generate(oreState, 20, 8, 40,
					world, random, chunkX, chunkZ);
		}
	}

	private void generate(IBlockState state, int maxVeins, int maxOres,
			int maxHeight, World world, Random random, int chunkX, int chunkZ) {
		for (int k = 0; k < maxVeins; k++) {
			int x = chunkX * 16 + random.nextInt(16);
			int y = random.nextInt(maxHeight);
			int z = chunkZ * 16 + random.nextInt(16);

			WorldGenMinable gen = new WorldGenMinable(state, maxOres);//new WorldGenMinable(block, meta, maxOres, Blocks.stone);
			gen.generate(world, random, new BlockPos(x, y, z));
		}
	}

}
