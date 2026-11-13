package com.rimevel.just_rocks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.melvin.rocktool.RockMaterialField;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public sealed interface MaterialConfiguration permits MaterialConfiguration.Uniform, MaterialConfiguration.Palette {
    String type();

    RockMaterialField create(long worldSeed);

    public static final Codec<MaterialConfiguration> CODEC =
            Codec.STRING.dispatch(
                    "type",
                    MaterialConfiguration::type,
                    type -> switch (type) {
                        case "uniform" -> Uniform.CODEC;
                        case "palette" -> Palette.CODEC;
                        default -> Uniform.CODEC;
                    }
            );

    record Uniform(BlockState blockState) implements MaterialConfiguration {
        public static final MapCodec<Uniform> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        BlockState.CODEC.fieldOf("block").forGetter(Uniform::blockState)
                ).apply(instance, Uniform::new));

        @Override
        public String type() {
            return "uniform";
        }

        @Override
        public RockMaterialField create(long worldSeed) {
            return (rockInstance, x, y, z) -> blockState;
        }
    }

    record Palette(
            long seedOffset,
            double patchScale,
            double patchInfluence,
            List<Band> bands
    ) implements MaterialConfiguration {
        public static final MapCodec<Palette> CODEC =
                RecordCodecBuilder.mapCodec(instance ->
                        instance.group(
                                Codec.LONG.optionalFieldOf("seed_offset", 0L)
                                        .forGetter(Palette::seedOffset),

                                Codec.doubleRange(1.0, Double.MAX_VALUE)
                                        .optionalFieldOf("patch_scale", 7.0)
                                        .forGetter(Palette::patchScale),

                                Codec.doubleRange(0.0, 1.0)
                                        .optionalFieldOf("patch_influence", 0.65)
                                        .forGetter(Palette::patchInfluence),

                                Band.CODEC.listOf()
                                        .fieldOf("bands")
                                        .forGetter(Palette::bands)
                        ).apply(instance, Palette::new)
                );

        @Override
        public String type() {
            return "palette";
        }

        @Override
        public RockMaterialField create(long worldSeed) {
            RockMaterialField.Band[] rockBands = bands.stream().map(Band::create)
                    .toArray(RockMaterialField.Band[]::new);

            return new RockMaterialField.Palette(
                    worldSeed ^ seedOffset,
                    patchScale,
                    patchInfluence,
                    rockBands
            );
        }
    }

    record Band(
            double from,
            double to,
            List<Component> components
    ) {
        public static final Codec<Band> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.doubleRange(0.0, 1.0).fieldOf("from").forGetter(Band::from),

                        Codec.doubleRange(0.0, 1.0).fieldOf("to").forGetter(Band::to),

                        Component.CODEC.listOf().fieldOf("components").forGetter(Band::components)
                ).apply(instance, Band::new));

        public RockMaterialField.Band create() {
            RockMaterialField.Component[] rockComponents = components.stream().map(Component::create)
                    .toArray(RockMaterialField.Component[]::new);

            return new RockMaterialField.Band(from, to, rockComponents);
        }
    }

    record Component(
            BlockState blockState,
            double weight
    ) {
        public static final Codec<Component> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                    BlockState.CODEC.fieldOf("block").forGetter(Component::blockState),

                    Codec.doubleRange(0.0, Double.MAX_VALUE).fieldOf("weight").forGetter(Component::weight)
                ).apply(instance, Component::new));

        public RockMaterialField.Component create() {
            return new RockMaterialField.Component(
                    blockState,
                    weight
            );
        }
    }
}
