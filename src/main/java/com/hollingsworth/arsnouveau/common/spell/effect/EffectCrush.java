package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EffectCrush extends AbstractEffect {

    public static EffectCrush INSTANCE = new EffectCrush();

    private EffectCrush() {
        super(GlyphLib.EffectCrushID, "Crush");
    }

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        for(BlockPos p : SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, spellStats.getBuffCount(AugmentAOE.INSTANCE), spellStats.getBuffCount(AugmentPierce.INSTANCE))){
            BlockState state = world.getBlockState(p);
            if(state.getBlock().is(Tags.Blocks.COBBLESTONE) || state.getBlock().is(Tags.Blocks.STONE)){
                world.setBlockAndUpdate(p, Blocks.GRAVEL.defaultBlockState());
            }else if(state.getBlock().is(Tags.Blocks.GRAVEL)){
                world.setBlockAndUpdate(p, Blocks.SAND.defaultBlockState());
            }
        }
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        dealDamage(world, shooter, (float) ((rayTraceResult.getEntity().isSwimming() ? DAMAGE.get() * 3.0 : DAMAGE.get()) + AMP_VALUE.get() * spellStats.getAmpMultiplier()), spellStats, rayTraceResult.getEntity(), DamageSource.DROWN);
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 3.0);
        addAmpConfig(builder, 1.0);
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentAOE.INSTANCE, AugmentPierce.INSTANCE,
                AugmentFortune.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "Turns stone into gravel, and gravel into sand. Will also harm entities and deals bonus damage to entities that are swimming.";
    }

    @Override
    public Item getCraftingReagent() {
        return Items.GRINDSTONE;
    }

    @Override
    public int getManaCost() {
        return 30;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_EARTH);
    }
}
