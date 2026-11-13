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

public class RockFeature extends Feature<RockFeatureConfiguration> {
    public RockFeature() {
        super(RockFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<RockFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RockFeatureConfiguration config = context.config();

        placeRock(level, origin, config);

        return true;
    }

    private static void placeRock(
            WorldGenLevel level,
            BlockPos origin,
            RockFeatureConfiguration config
    ) {
        RockVariantCache variants = new RockVariantCache(level.getSeed());

        var request = getRequest(origin, config);

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

    private static RockPlacementHelper.@NotNull Request getRequest(BlockPos origin, RockFeatureConfiguration config) {
        RockMaterialField material = (rock, x, y, z) ->
                config.blockState();

        long objectId = origin.asLong();

        var request = new RockPlacementHelper.Request(
                objectId,
                config.rockType(),
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
