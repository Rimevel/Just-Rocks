package com.rimevel.just_rocks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.melvin.rocktool.RockType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.Locale;

public record RockFeatureConfiguration(
    RockType rockType,
    BlockState blockState,
    int width,
    int length,
    int height,
    double orientation,
    double embedDepth,
    double maximumSurfaceRelief,
    double clearance
) implements FeatureConfiguration {
    public static final Codec<RockType> ROCK_TYPE_CODEC =
            Codec.STRING.comapFlatMap(
                    name -> {
                        try {
                            return DataResult.success(RockType.valueOf(name.toUpperCase(Locale.ROOT)));
                        } catch (IllegalArgumentException e) {
                            return DataResult.error(
                                    () -> "Unknown rock type: " + name
                            );
                        }
                    },
                    type -> type.name().toLowerCase(Locale.ROOT)
            );

    public static final Codec<RockFeatureConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                ROCK_TYPE_CODEC.fieldOf("rock_type").forGetter(RockFeatureConfiguration::rockType),

                BlockState.CODEC.optionalFieldOf("material", Blocks.STONE.defaultBlockState())
                        .forGetter(RockFeatureConfiguration::blockState),

                Codec.INT.optionalFieldOf("width", 0).forGetter(RockFeatureConfiguration::width),

                Codec.INT.optionalFieldOf("length", 0).forGetter(RockFeatureConfiguration::length),

                Codec.INT.optionalFieldOf("height", 0).forGetter(RockFeatureConfiguration::height),

                Codec.DOUBLE.optionalFieldOf("orientation", 0.0).forGetter(RockFeatureConfiguration::orientation),

                Codec.DOUBLE.optionalFieldOf("embed_depth", 0.0).forGetter(RockFeatureConfiguration::embedDepth),

                Codec.DOUBLE.optionalFieldOf("maximum_surface_relief", 12.0)
                        .forGetter(RockFeatureConfiguration::maximumSurfaceRelief),

                Codec.DOUBLE.optionalFieldOf("clearance", 0.0).forGetter(RockFeatureConfiguration::clearance)
            ).apply(instance, RockFeatureConfiguration::new)
    );
}
