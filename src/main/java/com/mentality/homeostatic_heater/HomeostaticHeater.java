package com.mentality.homeostatic_heater;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/** Registers the heater. Homeostatic compatibility is data-driven; see resources/data. */
public final class HomeostaticHeater implements ModInitializer {
    public static final String MOD_ID = "homeostatic_heater";
    public static final CoalHeaterBlock COAL_HEATER = new CoalHeaterBlock(
            AbstractBlock.Settings.create().strength(3.5F).requiresTool()
                    .luminance(state -> state.get(CoalHeaterBlock.LIT) ? 13 : 0));
    // Old registry IDs remain available so heaters placed with 1.0.0/1.0.1 survive the rename.
    public static final CoalHeaterBlock LEGACY_MENTALITY_HEATER = new CoalHeaterBlock(
            AbstractBlock.Settings.create().strength(3.5F).requiresTool()
                    .luminance(state -> state.get(CoalHeaterBlock.LIT) ? 13 : 0));
    public static final CoalHeaterBlock LEGACY_STORMHEATER = new CoalHeaterBlock(
            AbstractBlock.Settings.create().strength(3.5F).requiresTool()
                    .luminance(state -> state.get(CoalHeaterBlock.LIT) ? 13 : 0));
    public static final BlockEntityType<CoalHeaterBlockEntity> COAL_HEATER_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, id("coal_heater"),
                    FabricBlockEntityTypeBuilder.create(CoalHeaterBlockEntity::new, COAL_HEATER).build());
    public static final BlockEntityType<CoalHeaterBlockEntity> LEGACY_MENTALITY_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("mentality_heater", "coal_heater"),
                    FabricBlockEntityTypeBuilder.create(CoalHeaterBlockEntity::new, LEGACY_MENTALITY_HEATER).build());
    public static final BlockEntityType<CoalHeaterBlockEntity> LEGACY_STORMHEATER_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("stormheater", "coal_heater"),
                    FabricBlockEntityTypeBuilder.create(CoalHeaterBlockEntity::new, LEGACY_STORMHEATER).build());

    @Override
    public void onInitialize() {
        Registry.register(Registries.BLOCK, id("coal_heater"), COAL_HEATER);
        Registry.register(Registries.BLOCK, new Identifier("mentality_heater", "coal_heater"), LEGACY_MENTALITY_HEATER);
        Registry.register(Registries.BLOCK, new Identifier("stormheater", "coal_heater"), LEGACY_STORMHEATER);
        Registry.register(Registries.ITEM, id("coal_heater"),
                new BlockItem(COAL_HEATER, new Item.Settings()));
        Registry.register(Registries.ITEM, new Identifier("mentality_heater", "coal_heater"),
                new BlockItem(LEGACY_MENTALITY_HEATER, new Item.Settings()));
        Registry.register(Registries.ITEM, new Identifier("stormheater", "coal_heater"),
                new BlockItem(LEGACY_STORMHEATER, new Item.Settings()));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> entries.add(COAL_HEATER));
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static BlockEntityType<CoalHeaterBlockEntity> entityTypeFor(net.minecraft.block.BlockState state) {
        if (state.isOf(LEGACY_MENTALITY_HEATER)) return LEGACY_MENTALITY_ENTITY;
        if (state.isOf(LEGACY_STORMHEATER)) return LEGACY_STORMHEATER_ENTITY;
        return COAL_HEATER_ENTITY;
    }
}
