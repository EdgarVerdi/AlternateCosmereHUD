package me.verdi_shade.alternative_cosmere_hud;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// The 'bus = Mod.EventBusSubscriber.Bus.MOD' is critical here!
@Mod.EventBusSubscriber(modid = AlternativeCosmereHUD.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("alternative_cosmere_hud", CustomHudOverlay.HUD_OVERLAY);
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(ModKeybinds.KEY_ACTION_ONE);
        event.register(ModKeybinds.KEY_ACTION_TWO);
    }
}