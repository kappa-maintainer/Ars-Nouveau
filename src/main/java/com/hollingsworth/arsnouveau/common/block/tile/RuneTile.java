package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.RuneBlock;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.items.IItemHandler;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class RuneTile extends AnimatedTile implements IPickupResponder, IAnimatable {
    public Spell recipe = Spell.EMPTY;
    public boolean isTemporary;
    public boolean isCharged;
    public int ticksUntilCharge;
    public UUID uuid;
    public ParticleColor color = ParticleUtil.defaultParticleColor();
    
    public RuneTile() {
        super(BlockRegistry.RUNE_TILE);
        isCharged = true;
        isTemporary = false;
        ticksUntilCharge = 0;
    }

    public void setRecipe(List<AbstractSpellPart> recipe) {
        this.recipe = new Spell(recipe);
    }

    public void castSpell(Entity entity){

        if(!this.isCharged || recipe.isEmpty() || !(level instanceof ServerWorld) || !(recipe.recipe.get(0) instanceof MethodTouch))
            return;
        try {

            PlayerEntity playerEntity = uuid != null ? level.getPlayerByUUID(uuid) : FakePlayerFactory.getMinecraft((ServerWorld) level);
            playerEntity = playerEntity == null ?  FakePlayerFactory.getMinecraft((ServerWorld) level) : playerEntity;
            EntitySpellResolver resolver = new EntitySpellResolver(new SpellContext(recipe, playerEntity).withCastingTile(this).withType(SpellContext.CasterType.RUNE));
            resolver.onCastOnEntity(ItemStack.EMPTY, playerEntity, entity, Hand.MAIN_HAND);
            if (this.isTemporary) {
                level.destroyBlock(worldPosition, false);
                return;
            }
            this.isCharged = false;

            level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).cycle(RuneBlock.POWERED));
            ticksUntilCharge = 20 * 2;
        }catch (Exception e){
            PortUtil.sendMessage(entity, new TranslationTextComponent("ars_nouveau.rune.error"));
            e.printStackTrace();
            level.destroyBlock(worldPosition, false);
        }
    }

    public void setParsedSpell(List<AbstractSpellPart> spell){
        if(spell.size() <= 1){
            this.recipe = Spell.EMPTY;
            return;
        }
        spell.set(0, MethodTouch.INSTANCE);
        this.recipe = new Spell(spell);
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.putString("spell", recipe.serialize());
        tag.putBoolean("charged", isCharged);
        tag.putBoolean("temp", isTemporary);
        tag.putInt("cooldown", ticksUntilCharge);
        if(uuid != null)
            tag.putUUID("uuid", uuid);
        if(color != null)
            tag.putString("color", color.toWrapper().serialize());
        return super.save(tag);
    }

    @Override
    public void load( BlockState state, CompoundNBT tag) {
        this.recipe = Spell.deserialize(tag.getString("spell"));
        this.isCharged = tag.getBoolean("charged");
        this.isTemporary = tag.getBoolean("temp");
        this.ticksUntilCharge = tag.getInt("cooldown");
        if(tag.contains("uuid"))
            this.uuid = tag.getUUID("uuid");
        this.color = ParticleColor.IntWrapper.deserialize(tag.getString("color")).toParticleColor();
        super.load(state, tag);
    }

    @Override
    public void tick() {

        if(level == null)
            return;
        if(!level.isClientSide) {
            if (ticksUntilCharge > 0) {
                ticksUntilCharge -= 1;
                return;
            }
        }
        if(this.isCharged)
            return;
        if(!level.isClientSide && this.isTemporary){
            level.destroyBlock(this.worldPosition, false);
        }
        if(!level.isClientSide){
            BlockPos fromPos = ManaUtil.takeManaNearbyWithParticles(worldPosition, level, 10, 100);
            if(fromPos != null) {
                this.isCharged = true;
                level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).cycle(RuneBlock.POWERED));
            }else
                ticksUntilCharge = 20 * 3;
        }
    }

    @Override
    public List<IItemHandler> getInventory() {
        return BlockUtil.getAdjacentInventories(level, worldPosition);
    }

    @Override
    public @Nonnull ItemStack onPickup(ItemStack stack) {
        return BlockUtil.insertItemAdjacent(level, worldPosition, stack);
    }

    @Override
    public void registerControllers(AnimationData data) {

    }
    AnimationFactory factory = new AnimationFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}