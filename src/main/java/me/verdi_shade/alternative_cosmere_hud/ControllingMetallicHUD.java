package me.verdi_shade.alternative_cosmere_hud;

import leaf.cosmere.api.manifestation.Manifestation;
import leaf.cosmere.api.spiritweb.ISpiritweb;
import leaf.cosmere.common.Cosmere;
import leaf.cosmere.common.cap.entity.SpiritwebCapability;
import leaf.cosmere.common.network.packets.ChangeManifestationModeMessage;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// Notice the bus is FORGE here!
@Mod.EventBusSubscriber(modid = AlternativeCosmereHUD.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ControllingMetallicHUD {

    private static final boolean[] actionWasDown = new boolean[2];
    private static final boolean[] actionScrolled = new boolean[2];
    private static final KeyMapping[] actions = {
            ModKeybinds.KEY_ACTION_ONE,
            ModKeybinds.KEY_ACTION_TWO
    };

    private static int powerPopupTimer = 0;
    public static final int DEFAULT_POWER_POPUP_TIMER = 40;

    public static int getPopupTimer() { return powerPopupTimer; }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        ControllingMetallicHUD.tickPopup();

        for (int i = 0; i < 2; i++) {
            boolean actionIsDown = actions[i].isDown();
            if (actionIsDown)
                resetPowerPopupTime();
            else if (actionWasDown[i]) {
                if (actionScrolled[i])
                    actionScrolled[i] = false;
                else if (i == 0)
                    HudPersistentData.toggleMetallicArt();
                else // i == 1
                    HudPersistentData.togglePushSelected();
            }
            while (actions[i].consumeClick()){}
            actionWasDown[i] = actionIsDown;
        }
    }

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        // getScrollDelta() is positive for scrolling UP, negative for DOWN
        double scrollDirection = event.getScrollDelta();
        boolean scrolled_consumed = false;

        for (int i = 0; i < 2; i++) {
            if (!actions[i].isDown()) continue;
            scrolled_consumed = true;
            if (scrollDirection == 0) continue;
            actionScrolled[i] = true;

            if (i==0)
                HudPersistentData.directSelectedPair(scrollDirection);
            else //if (i==1)
                change_manifestation_strength(scrollDirection > 0 ? 1 : -1);

        }

        if (scrolled_consumed)
            event.setCanceled(true); // Cancel event to prevent hotbar scrolling

    }

    private static void change_manifestation_strength(int modifier){
        LazyOptional<ISpiritweb> lazy_spiritweb =  SpiritwebCapability.get(Minecraft.getInstance().player);
        if (!lazy_spiritweb.isPresent()) return;
        ISpiritweb spiritweb = lazy_spiritweb.orElse(null);
        Manifestation manifest = HudPersistentData.getSelectedManifestation();

        if (!spiritweb.hasManifestation(manifest)) return;

        int next_val = spiritweb.getMode(manifest)+modifier;
        if (next_val > manifest.modeMax(spiritweb) || next_val < manifest.modeMin(spiritweb)) return;

        Cosmere.packetHandler().sendToServer(new ChangeManifestationModeMessage(manifest, modifier));
    }

    public static void tickPopup() {
        if (powerPopupTimer > 0)
            powerPopupTimer--;
    }

    public static void resetPowerPopupTime(){
        powerPopupTimer = DEFAULT_POWER_POPUP_TIMER;
    }

    public static int getPowerPopupIntendedTransparency(){
        if (powerPopupTimer > DEFAULT_POWER_POPUP_TIMER/2)
            return 255;
        return 255*2*powerPopupTimer / DEFAULT_POWER_POPUP_TIMER;
    }
}