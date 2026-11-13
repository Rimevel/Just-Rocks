package com.rimevel.just_rocks;

import net.melvin.rocktool.RockMaterialField;
import net.melvin.rocktool.RockPlacementHelper;
import net.melvin.rocktool.RockType;
import net.melvin.rocktool.RockVariantCache;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TestFeature extends Feature<NoneFeatureConfiguration> {
    public TestFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        placeRock(level, origin);

        return true;
    }

    public static void placeRock(
            WorldGenLevel level,
            BlockPos origin
    ) {
        RockVariantCache variants = new RockVariantCache(level.getSeed());

        var request = getRequest(origin);

        var placements = RockPlacementHelper.plan(
                List.of(request),
                variants,
                (x, z) -> origin.getY()
        );

        ChunkPos center = new ChunkPos(origin);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {

                int chunkX = center.x + dx;
                int chunkZ = center.z + dz;

                ChunkAccess targetChunk = level.getChunk(
                        chunkX,
                        chunkZ,
                        ChunkStatus.EMPTY,
                        false
                );

                if (targetChunk == null) {
                    continue;
                }

                RockPlacementHelper.placeInChunk(
                        targetChunk,
                        placements,
                        level.getMinBuildHeight(),
                        level.getMaxBuildHeight() - 1
                );
            }
        }
    }

    private static RockPlacementHelper.@NotNull Request getRequest(BlockPos origin) {
        RockMaterialField material = (rock, x, y, z) ->
                Blocks.STONE.defaultBlockState();

        long objectId = origin.asLong();

        var request = new RockPlacementHelper.Request(
                objectId,
                RockType.FRACTURED_BOULDER,
                -1,
                origin.getX(),
                origin.getZ(),
                0.0,
                material,
                0.0,
                12.0,
                0.0,
                6.0,
                6.0,
                8.0
        );
        return request;
    }
}
