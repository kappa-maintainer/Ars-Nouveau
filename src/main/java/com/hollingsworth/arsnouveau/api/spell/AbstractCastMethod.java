package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractCastMethod extends AbstractSpellPart {

    public abstract void onCast(@Nullable ItemStack stack, LivingEntity playerEntity, World world, List<AbstractAugment> augments, SpellContext context, SpellResolver resolver);

    public abstract void onCastOnBlock(ItemUseContext context, List<AbstractAugment> augments, SpellContext spellContext, SpellResolver resolver);

    public abstract void onCastOnBlock(BlockRayTraceResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments, SpellContext spellContext, SpellResolver resolver);

    public abstract void onCastOnEntity(@Nullable ItemStack stack, LivingEntity caster, Entity target, Hand hand, List<AbstractAugment> augments, SpellContext spellContext, SpellResolver resolver);

    public abstract boolean wouldCastSuccessfully(@Nullable ItemStack stack, LivingEntity playerEntity, World world, List<AbstractAugment> augments, SpellResolver resolver);

    public abstract boolean wouldCastOnBlockSuccessfully(ItemUseContext context, List<AbstractAugment> augments, SpellResolver resolver);

    public abstract boolean wouldCastOnBlockSuccessfully(BlockRayTraceResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments, SpellResolver resolver);

    public abstract boolean wouldCastOnEntitySuccessfully(@Nullable ItemStack stack, LivingEntity caster, Entity target, Hand hand, List<AbstractAugment> augments, SpellResolver resolver);

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        super.buildAugmentLimitsConfig(builder, getDefaultAugmentLimits());
    }

    public AbstractCastMethod(String tag, String description){
        super(tag,description);
    }


}
