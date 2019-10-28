package green.sailor.anti18.mixin;

import net.minecraft.entity.player.HungerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(HungerManager.class)
public class HungerManagerMixin {

    // this simply cancels the update method
    // this means hunger is never decremented (so you can still sprint), regen is never applied (you still gotta eat!)
    // etc etc
    // all the values are still updated and stuff, but they don't do anything without an update method to meddle with
    // the player!!
    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    void onUpdate(CallbackInfo ci) {
        ci.cancel();
    }

}
