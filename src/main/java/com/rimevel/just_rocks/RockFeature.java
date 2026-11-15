package com.rimevel.just_rocks;

import net.melvin.rocktool.RockPlacementHelper;
import net.melvin.rocktool.RockVariantCache;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
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
        RandomSource random = context.random();

        placeRock(level, origin, config, random);

        return true;
    }

    private static void placeRock(
            WorldGenLevel level,
            BlockPos origin,
            RockFeatureConfiguration config,
            RandomSource random
    ) {
        RockVariantCache variants = new RockVariantCache(level.getSeed());

        var request = getRequest(origin, config, level, random);

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

    private static RockPlacementHelper.@NotNull Request getRequest(BlockPos origin, RockFeatureConfiguration config, WorldGenLevel level, RandomSource random) {
        long objectId = origin.asLong();

        var request = new RockPlacementHelper.Request(
                objectId,
                config.rockType(),
                -1,
                origin.getX(),
                origin.getZ(),
                config.orientation(),
                config.material().create(level.getSeed()),
                config.embedDepth(),
                config.maximumSurfaceRelief(),
                config.clearance(),
                config.width().sample(random),
                config.length().sample(random),
                config.height().sample(random),
                config.decorationRules().create()
        );
        return request;
    }
}
