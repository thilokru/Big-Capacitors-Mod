package com.mhfs.capacitors.oregen;

import java.util.Random;

import com.mhfs.capacitors.Blocks;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import cpw.mods.fml.common.IWorldGenerator;

public class OreGen implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.dimensionId == 0) {
			generate(Blocks.blockMany, 3, 20, 8, 40,
					world, random, chunkX, chunkZ);
			generate(Blocks.blockMany, 4, 20, 8, 40,
					world, random, chunkX, chunkZ);
		}
	}

	private void generate(Block block, int maxVeins, int maxOres,
			int maxHeight, int meta, World world, Random random, int chunkX, int chunkZ) {
		for (int k = 0; k < maxVeins; k++) {
			int x = chunkX * 16 + random.nextInt(16);
			int y = random.nextInt(maxHeight);
			int z = chunkZ * 16 + random.nextInt(16);

			WorldGenMinable gen = new WorldGenMinable(block, meta, maxOres, Blocks.stone);
			gen.generate(world, random, x, y, z);
		}
	}

}
