package green.sailor.anti18.mixin;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin {

    // redirected call to eatFood, which normally updates the hunger manager
    // but since we nullify the hunger manager out, it won't do anything
    // instead, we directly apply the Regeneration II effect
    // multiplied by the hunger value.
    // this gives us a nice health regeneration
    @Redirect(
            method = "eatFood",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/HungerManager;eat(Lnet/minecraft/item/Item;Lnet/minecraft/item/ItemStack;)V"
            )
    )
    void redirectHMEat(HungerManager hungerManager, Item item_1, ItemStack itemStack_1) {
        PlayerEntity us = (PlayerEntity)(Object) this;
        FoodComponent fc = item_1.getFoodComponent();

        // if this is null, then somebody is fucking with stuff.
        // that's not good!!
        // but, we check anyway, to make sure Bad People can't crash us!!
        if (fc != null) {
            int hunger = fc.getHunger();

            StatusEffectInstance se = new StatusEffectInstance(StatusEffects.REGENERATION, 20 * hunger, 1);
            us.addStatusEffect(se);
        }
    }

    // wasn't really sure where to put this...
    // there's several places, including Item#use, FoodComponent#isAlwaysEdible
    // but... this seems like the least misusable.
    // since for those others you could easily just eat to get regen II whenever
    // but with this, you can only eat when youre not at full HP!
    @Inject(method = "canConsume", at = @At("HEAD"), cancellable = true)
    void fixedCanConsume(boolean boolean_1, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity us = (PlayerEntity)(Object) this;
        boolean can = !us.abilities.invulnerable && (boolean_1 || us.getHealth() < us.getMaxHealth());
        cir.setReturnValue(can);
    }

}
