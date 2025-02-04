package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EffectGrow  extends AbstractEffect {
    public static EffectGrow INSTANCE = new EffectGrow();

    private EffectGrow() {
        super(GlyphLib.EffectGrowID, "Grow");
    }

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        for(BlockPos blockpos : SpellUtil.calcAOEBlocks(shooter,  rayTraceResult.getBlockPos(), rayTraceResult, spellStats)){
            ItemStack stack = new ItemStack(Items.BONE_MEAL, 64);
            if(BlockUtil.destroyRespectsClaim(shooter, world, blockpos) && world instanceof ServerWorld) {
                BoneMealItem.applyBonemeal(stack, world, blockpos, FakePlayerFactory.getMinecraft((ServerWorld) world));
            }
        }
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        if(!(rayTraceResult instanceof BlockRayTraceResult))
            return false;
        BlockPos pos = ((BlockRayTraceResult) rayTraceResult).getBlockPos();
        return world.getBlockState(pos).getBlock() instanceof IGrowable
                && ((IGrowable) world.getBlockState(pos).getBlock()).isValidBonemealTarget(world, pos, world.getBlockState(pos), world.isClientSide);
    }

    @Override
    public int getManaCost() {
        return 70;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.BONE_BLOCK;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentPierce.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Causes plants to accelerate in growth as if they were bonemealed.";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_EARTH);
    }
}
