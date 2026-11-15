package com.rimevel.just_rocks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.melvin.rocktool.RockType;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.Locale;

public record RockFeatureConfiguration(
    RockType rockType,
    MaterialConfiguration material,
    IntProvider width,
    IntProvider length,
    IntProvider height,
    double orientation,
    double embedDepth,
    double maximumSurfaceRelief,
    double clearance,
    DecorationRulesConfiguration decorationRules
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

                MaterialConfiguration.CODEC.optionalFieldOf("material",  new MaterialConfiguration.Uniform(
                                Blocks.STONE.defaultBlockState()
                        ))
                        .forGetter(RockFeatureConfiguration::material),

                IntProvider.codec(4, Integer.MAX_VALUE).optionalFieldOf("width", ConstantInt.of(4)).forGetter(RockFeatureConfiguration::width),

                IntProvider.codec(4, Integer.MAX_VALUE).optionalFieldOf("length", ConstantInt.of(4)).forGetter(RockFeatureConfiguration::length),

                IntProvider.codec(4, Integer.MAX_VALUE).optionalFieldOf("height", ConstantInt.of(4)).forGetter(RockFeatureConfiguration::height),

                Codec.DOUBLE.optionalFieldOf("orientation", 0.0).forGetter(RockFeatureConfiguration::orientation),

                Codec.DOUBLE.optionalFieldOf("embed_depth", 0.0).forGetter(RockFeatureConfiguration::embedDepth),

                Codec.DOUBLE.optionalFieldOf("maximum_surface_relief", 12.0)
                        .forGetter(RockFeatureConfiguration::maximumSurfaceRelief),

                Codec.DOUBLE.optionalFieldOf("clearance", 0.0).forGetter(RockFeatureConfiguration::clearance),

                DecorationRulesConfiguration.CODEC.optionalFieldOf("decoration_rules", DecorationRulesConfiguration.EMPTY).forGetter(RockFeatureConfiguration::decorationRules)
            ).apply(instance, RockFeatureConfiguration::new)
    );
}
