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
    BlockState blockState
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

                BlockState.CODEC.optionalFieldOf("material", Blocks.STONE.defaultBlockState()).forGetter(RockFeatureConfiguration::blockState)
            ).apply(instance, RockFeatureConfiguration::new)
    );
}
