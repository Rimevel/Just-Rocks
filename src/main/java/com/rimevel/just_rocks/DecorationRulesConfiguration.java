package com.rimevel.just_rocks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.melvin.rocktool.RockDecorationRules;
import net.melvin.rocktool.RockType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Set;

public record DecorationRulesConfiguration (
        TopSoilConfiguration topSoil,
        List<SurfacePlantConfiguration> plants
) {
    public static final Codec<DecorationRulesConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    TopSoilConfiguration.CODEC.fieldOf("top_soil")
                            .forGetter(DecorationRulesConfiguration::topSoil),

                    SurfacePlantConfiguration.CODEC.listOf().fieldOf("plants").forGetter(DecorationRulesConfiguration::plants)
            ).apply(instance, DecorationRulesConfiguration::new)
    );

    public record TopSoilConfiguration(
            BlockState surface,
            BlockState subsurface,
            int depth,
            int maximumRelief,
            double coverage,
            Set<RockType> types
    ) {
        private static final Codec<Set<RockType>> ROCK_TYPES_CODEC =
                RockFeatureConfiguration.ROCK_TYPE_CODEC.listOf().xmap(
                        Set::copyOf,
                        List::copyOf
                );

        public static final MapCodec<TopSoilConfiguration> CODEC =
                RecordCodecBuilder.mapCodec(instance ->
                        instance.group(
                                BlockState.CODEC.fieldOf("surface")
                                        .forGetter(TopSoilConfiguration::surface),

                                BlockState.CODEC.fieldOf("subsurface")
                                        .forGetter(TopSoilConfiguration::subsurface),

                                Codec.intRange(1, 8)
                                        .optionalFieldOf("depth", 2)
                                        .forGetter(TopSoilConfiguration::depth),

                                Codec.intRange(0, Integer.MAX_VALUE)
                                        .optionalFieldOf("maximum_relief", 1)
                                        .forGetter(TopSoilConfiguration::maximumRelief),

                                Codec.doubleRange(0.0, 1.0)
                                        .optionalFieldOf("coverage", 1.0)
                                        .forGetter(TopSoilConfiguration::coverage),

                                ROCK_TYPES_CODEC
                                        .optionalFieldOf("types", Set.of())
                                        .forGetter(TopSoilConfiguration::types)

                        ).apply(instance, TopSoilConfiguration::new)
                );
    }

    public record SurfacePlantConfiguration(
            BlockState state,
            double chance,
            int maximumRelief
    ) {

        public static final Codec<SurfacePlantConfiguration> CODEC =
                RecordCodecBuilder.create(instance ->
                        instance.group(
                                BlockState.CODEC.fieldOf("block")
                                        .forGetter(SurfacePlantConfiguration::state),

                                Codec.doubleRange(0.0, 1.0)
                                        .optionalFieldOf("chance", 0.12)
                                        .forGetter(SurfacePlantConfiguration::chance),

                                Codec.intRange(0, Integer.MAX_VALUE)
                                        .optionalFieldOf("maximum_relief", 1)
                                        .forGetter(SurfacePlantConfiguration::maximumRelief)

                        ).apply(instance, SurfacePlantConfiguration::new)
                );
    }

    public RockDecorationRules create() {
        RockDecorationRules.Builder builder = RockDecorationRules.builder();

        if (topSoil.types().isEmpty()) {
            builder.topSoil(
                    topSoil.surface(),
                    topSoil.subsurface(),
                    topSoil.depth(),
                    topSoil.maximumRelief(),
                    topSoil.coverage()
            );
        } else {
            builder.topSoilFor(
                    topSoil.types(),
                    topSoil.surface(),
                    topSoil.subsurface(),
                    topSoil.depth(),
                    topSoil.maximumRelief(),
                    topSoil.coverage()
            );
        }

        for (SurfacePlantConfiguration plant : plants) {
            builder.plant(
                    plant.state(),
                    plant.chance(),
                    plant.maximumRelief()
            );
        }

        return builder.build();
    }
}
