package me.verdi_shade.alternative_cosmere_hud;

import leaf.cosmere.client.gui.GuiUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.awt.*;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = AlternativeCosmereHUD.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue EXTRAS_X_OFFSET = BUILDER
            .comment("The extra info diamond's x value")
            .defineInRange("extras_x_offset", 13, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue EXTRAS_Y_OFFSET = BUILDER
            .comment("The extra info diamond's y value")
            .defineInRange("extras_y_offset", 8, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue BACKGROUND_COLOR = BUILDER
            .comment("Background Color of UI")
            .defineInRange("background_color", GuiUtils.BACKGROUND_COLOR.getRGB(), 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue BAR_COLOR = BUILDER
            .comment("Default color used in the vertical bar above the background, showing how much of the metal is in reserves")
            .defineInRange("bar_color", 0xFFBBBBBB, 0, Integer.MAX_VALUE);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int extras_x_offset;
    public static int extras_y_offset;
    public static Color backgroundColor;
    public static Color barColor;

    @SuppressWarnings({"removal"})
    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
    }

    @SuppressWarnings({"removal"})
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        extras_x_offset = EXTRAS_X_OFFSET.get();
        extras_y_offset = EXTRAS_Y_OFFSET.get();
        backgroundColor = new Color(BACKGROUND_COLOR.get());
        barColor = new Color(BAR_COLOR.get());
    }
}
