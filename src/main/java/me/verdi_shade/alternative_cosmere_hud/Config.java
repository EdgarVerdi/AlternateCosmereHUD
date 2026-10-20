package me.verdi_shade.alternative_cosmere_hud;

import leaf.cosmere.client.gui.GuiUtils;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

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

    private static final ForgeConfigSpec.ConfigValue<String> BACKGROUND_COLOR = BUILDER
            .comment("Background Color of UI")
            .define("background_color", getColorHex(GuiUtils.BACKGROUND_COLOR));

    private static final ForgeConfigSpec.ConfigValue<String> BAR_COLOR = BUILDER
            .comment("Default color used in the vertical bar above the background, showing how much of the metal is in reserves")
            .define("bar_color", "#BBBBBB");

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int extras_x_offset;
    public static int extras_y_offset;
    public static Color backgroundColor;
    public static Color barColor;

    private static String getColorHex(Color color){
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    @SuppressWarnings({"removal"}) // supression must be here
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        extras_x_offset = EXTRAS_X_OFFSET.get();
        extras_y_offset = EXTRAS_Y_OFFSET.get();
        backgroundColor = Color.decode(BACKGROUND_COLOR.get());
        barColor = Color.decode(BAR_COLOR.get());
    }
}
