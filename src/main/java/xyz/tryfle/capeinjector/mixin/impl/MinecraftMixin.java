package xyz.tryfle.capeinjector.mixin.impl;

//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//import xyz.tryfle.capeinjector.CapeInjector;
//import xyz.tryfle.capeinjector.bus.TickEvent;
//
//@Mixin(targets = "MinecraftClient")
//public class MinecraftMixin {
//
//    @Inject(method = "tick", at = @At("HEAD"))
//    private void onRunTick(CallbackInfo ci) {
//        CapeInjector.INSTANCE.getBus().post(new TickEvent());
//    }
//}