package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

public class EntityTags {
    public static final ITag.INamedTag<EntityType<?>> DRYGMY_BLACKLIST = EntityTypeTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "drygmy_blacklist"));
    public static final ITag.INamedTag<EntityType<?>> DISINTEGRATION_WHITELIST = EntityTypeTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "disintegration_whitelist"));
    public static final ITag.INamedTag<EntityType<?>> DISINTEGRATION_BLACKLIST = EntityTypeTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "disintegration_blacklist"));
}
