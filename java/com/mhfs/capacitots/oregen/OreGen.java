package com.mhfs.capacitots.oregen;

import java.util.Random;

import com.mhfs.capacitors.BigCapacitorsMod;

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
			generate(BigCapacitorsMod.instance.blockRutilOre, 20, 8, 40,
					world, random, chunkX, chunkZ);
			generate(BigCapacitorsMod.instance.blockWitheriteOre, 20, 8, 40,
					world, random, chunkX, chunkZ);
		}
	}

	private void generate(Block block, int maxVeins, int maxOres,
			int maxHeight, World world, Random random, int chunkX, int chunkZ) {
		for (int k = 0; k < maxVeins; k++) {
			int x = chunkX * 16 + random.nextInt(16);
			int y = random.nextInt(maxHeight);
			int z = chunkZ * 16 + random.nextInt(16);

			WorldGenMinable gen = new WorldGenMinable(block, maxOres);
			gen.generate(world, random, x, y, z);
		}
	}

}
