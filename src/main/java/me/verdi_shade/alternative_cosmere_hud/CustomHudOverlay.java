package me.verdi_shade.alternative_cosmere_hud;

import com.mojang.blaze3d.systems.RenderSystem;
import leaf.cosmere.allomancy.common.Allomancy;
import leaf.cosmere.api.Manifestations;
import leaf.cosmere.api.Metals;
import leaf.cosmere.api.manifestation.Manifestation;
import leaf.cosmere.api.spiritweb.ISpiritweb;
import leaf.cosmere.client.gui.GuiUtils;
import leaf.cosmere.feruchemy.common.Feruchemy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.verdi_shade.alternative_cosmere_hud.Config.extras_x_offset;
import static me.verdi_shade.alternative_cosmere_hud.Config.extras_y_offset;
import static me.verdi_shade.alternative_cosmere_hud.HudPersistentData.METAL_TYPE_GROUPS;
import static me.verdi_shade.alternative_cosmere_hud.SpiritwebHandling.*;


@SuppressWarnings({"removal"})
public class CustomHudOverlay {

    private static final ResourceLocation TEX_FERUCHEMY_HUD_LEFT_FRAME = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/feruchemy_hud_left_frame.png");
    private static final ResourceLocation TEX_FERUCHEMY_HUD_RIGHT_FRAME = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/feruchemy_hud_right_frame.png");
    private static final ResourceLocation TEX_ALLOMANCY_HUD_LEFT_FRAME = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/allomancy_hud_left_frame.png");
    private static final ResourceLocation TEX_ALLOMANCY_HUD_RIGHT_FRAME = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/allomancy_hud_right_frame.png");

    private static final ResourceLocation TEX_FERUCHEMY_HUD_LEFT_SELECTED = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/feruchemy_hud_left_selected.png");
    private static final ResourceLocation TEX_FERUCHEMY_HUD_RIGHT_SELECTED = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/feruchemy_hud_right_selected.png");
    private static final ResourceLocation TEX_ALLOMANCY_HUD_LEFT_SELECTED = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/allomancy_hud_left_selected.png");
    private static final ResourceLocation TEX_ALLOMANCY_HUD_RIGHT_SELECTED = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/allomancy_hud_right_selected.png");

    private static final ResourceLocation TEX_FERUCHEMY_HUD_LEFT_BACKGROUND_MASK = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/feruchemy_hud_left_background_mask.png");
    private static final ResourceLocation TEX_FERUCHEMY_HUD_RIGHT_BACKGROUND_MASK = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/feruchemy_hud_right_background_mask.png");
    private static final ResourceLocation TEX_ALLOMANCY_HUD_LEFT_BACKGROUND_MASK = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/allomancy_hud_left_background_mask.png");
    private static final ResourceLocation TEX_ALLOMANCY_HUD_RIGHT_BACKGROUND_MASK = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/allomancy_hud_right_background_mask.png");

    private static final ResourceLocation TEX_FERUCHEMY_HUD_LEFT_EXTRA = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/feruchemy_hud_left_extra.png");
    private static final ResourceLocation TEX_FERUCHEMY_HUD_RIGHT_EXTRA = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/feruchemy_hud_right_extra.png");
    private static final ResourceLocation TEX_ALLOMANCY_HUD_LEFT_EXTRA = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/allomancy_hud_left_extra.png");
    private static final ResourceLocation TEX_ALLOMANCY_HUD_RIGHT_EXTRA = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/allomancy_hud_right_extra.png");


    private static final ResourceLocation[] textures_array = {
            TEX_ALLOMANCY_HUD_LEFT_FRAME, TEX_ALLOMANCY_HUD_RIGHT_FRAME,
            TEX_FERUCHEMY_HUD_LEFT_FRAME, TEX_FERUCHEMY_HUD_RIGHT_FRAME,

            TEX_ALLOMANCY_HUD_LEFT_SELECTED, TEX_ALLOMANCY_HUD_RIGHT_SELECTED,
            TEX_FERUCHEMY_HUD_LEFT_SELECTED, TEX_FERUCHEMY_HUD_RIGHT_SELECTED,

            TEX_ALLOMANCY_HUD_LEFT_BACKGROUND_MASK, TEX_ALLOMANCY_HUD_RIGHT_BACKGROUND_MASK,
            TEX_FERUCHEMY_HUD_LEFT_BACKGROUND_MASK, TEX_FERUCHEMY_HUD_RIGHT_BACKGROUND_MASK,

            TEX_ALLOMANCY_HUD_LEFT_EXTRA, TEX_ALLOMANCY_HUD_RIGHT_EXTRA,
            TEX_FERUCHEMY_HUD_LEFT_EXTRA, TEX_FERUCHEMY_HUD_RIGHT_EXTRA
    };

    private static final int FRAME_OFFSET = 0;
    private static final int SELECTED_OFFSET = 4;
    private static final int BACKGROUND_MASK_OFFSET = 8;
    private static final int EXTRA_OFFSET = 12;
    public static int iconSize = 16;
    public static int hudPieceX = 10;
    public static int hudPieceY = 10;
    public static int hudPieceSize = 42;


    private static final List<Map<String, ResourceLocation>> maps = Stream.of(false, true)
            .map(isFeruchemy -> {
                String mod_id = isFeruchemy ? Feruchemy.MODID : Allomancy.MODID;
                return Arrays.stream(METAL_TYPE_GROUPS)
                        .flatMap(Arrays::stream) // Flatten the 2D array of metals
                        .distinct()
                        .collect(Collectors.toMap(
                                Metals.MetalType::getName, // The key
                                metal -> new ResourceLocation(mod_id, "textures/icon/" + mod_id + "/" + metal.getName() + ".png") // The value
                        ));
            })
            .toList();

    private static boolean isFeruchemy(Manifestation manifestation){
        return manifestation.getManifestationType().equals(Manifestations.ManifestationTypes.FERUCHEMY);
    }

    private static void render_metallic_icon(GuiGraphics guiGraphics, int x, int y, int size, Manifestation manifestation, Metals.MetalType metal) {
        boolean isFeruchemy = isFeruchemy(manifestation);
        ResourceLocation resource = maps.get(isFeruchemy ? 1 : 0).get(metal.getName());
        Color metalColor = metal.getColor();
        // render_icon(guiGraphics, resource, x, y, size, metalColor, 1.0f);
        AtomicBoolean hasMetal = new AtomicBoolean(false);
        hasMetal.set(getSpiritWeb().hasManifestation(manifestation));

        float r = metalColor.getRed()/255.f, g = metalColor.getGreen()/255.f, b = metalColor.getBlue()/255.f;
        float alpha  = 1.0f;
        if (!hasMetal.get()) {
            r *= 0.1f;
            g *= 0.1f;
            b *= 0.1f;
            alpha = 0.25f;
        }
        render_icon(guiGraphics, resource, x, y, size, r, g, b, alpha);
    }

    private static void render_icon(GuiGraphics guiGraphics, ResourceLocation resource, int x, int y, int size, Color color, float alpha){
        float r = color.getRed()/255.f, g = color.getGreen()/255.f, b = color.getBlue()/255.f;
        render_icon(guiGraphics, resource, x, y, size, r, g, b, alpha);
    }

    private static void render_icon(GuiGraphics guiGraphics, ResourceLocation resource, int x, int y, int size, float r, float g, float b, float alpha){
        RenderSystem.setShaderTexture(0, resource);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        RenderSystem.setShaderColor(0f, 0f, 0f, alpha);
        guiGraphics.blit(resource, x+1, y+1, 0, 0, size, size, size, size);
        RenderSystem.setShaderColor(r, g, b, alpha);
        guiGraphics.blit(resource, x, y, 0, 0, size, size, size, size);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1.0f);
    }

    private static void render_hud_piece(GuiGraphics guiGraphics, ResourceLocation resource, int x, int y, int size){
        guiGraphics.blit(resource, x, y, 0, 0, size, size, size, size);
    }

    private static Color get_strength_color(Manifestation manifestation){
        boolean isFeruchemy = isFeruchemy(manifestation);
        int curr = manifestation.getMode(getSpiritWeb());
        Color barColor = Config.barColor;
        //Color color = new Color(0xFFDDDDDD).brighter();
        if (curr != 0) {
            Color shift_color = curr > 0 ? GuiUtils.POSITIVE_USE_COLOR : GuiUtils.NEGATIVE_USE_COLOR;
            float intensity = (isFeruchemy && curr < 0) ? 1f / 16f : 1f / 5f;
            intensity = Math.min(Math.abs(curr) * intensity, 1.0f);
            barColor = GuiUtils.shiftColor(barColor, intensity, shift_color);
        }
        return barColor;
    }

    private static void render_reserves_overlay(
            GuiGraphics guiGraphics, ResourceLocation resource, int x, int y, int size, Manifestation manifestation
    ){
        if (!hasManifestation(manifestation)) return;

        Color barColor = get_strength_color(manifestation);
        float r = barColor.getRed()/255.f, g = barColor.getGreen()/255.f, b = barColor.getBlue()/255.f;

        float reserve_percentage = 1f-manifestation.getInvestitureHud(getSpiritWeb());
        int cut_offset = (int) (size*reserve_percentage);
        RenderSystem.setShaderColor(r, g, b, 1.0f);
        guiGraphics.blit(resource, x, y+cut_offset, 0, cut_offset, size, size-cut_offset, size, size);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private static void render_reserves_overlay(
            GuiGraphics guiGraphics, ResourceLocation resource, int x, int y, int size,
            boolean isFeruchemy, Metals.MetalType metal){
        Manifestation manifestation = SpiritwebHandling.getSpecificManifestation(isFeruchemy, metal);
        render_reserves_overlay(guiGraphics, resource, x, y, size, manifestation);
    }

    private static void render_background(GuiGraphics guiGraphics, ResourceLocation resource, int x, int y, int size) {
        Color color = Config.backgroundColor;
        float r = color.getRed()/255.f, g = color.getGreen()/255.f, b = color.getBlue()/255.f;
        RenderSystem.setShaderColor(r, g, b, 1.0f);
        render_hud_piece(guiGraphics, resource, x, y, size);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private static void draw_centered_string(GuiGraphics guiGraphics, Font font, String text, int x, int y, int color){
        int textWidth = font.width(text);
        int textHeight = font.lineHeight;

        int startX = x - (textWidth / 2);
        int startY = y - (textHeight / 2);

        guiGraphics.drawString(font, text, startX, startY, color);
    }

    private static void draw_right_aligned_string(GuiGraphics guiGraphics, Font font, String text, int x, int y, int color){
        guiGraphics.drawString(font, text, x - font.width(text), y, color);
    }

    private static void draw_side_aligned_string(GuiGraphics guiGraphics, Font font, String text, int x, int y, int color, boolean isLeft){
        if (isLeft)
            draw_right_aligned_string(guiGraphics, font, capitalizeString(text), x, y, color);
        else
            guiGraphics.drawString(font, capitalizeString(text), x, y, color);
    }

    private static String capitalizeString(String string){
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    private static void renderMainHUD(GuiGraphics guiGraphics, int f, int lr, int xOffset,
                                      Manifestation manifestation, Metals.MetalType metalType){
        int arr_offset = f+lr;
        int x = hudPieceX + xOffset;
        int iconX = x + (lr == 0 ? (hudPieceSize-iconSize)*7/8 : (hudPieceSize-iconSize)/8);
        int iconY = hudPieceY+(hudPieceSize-iconSize)/2-1;

        render_background(guiGraphics, textures_array[BACKGROUND_MASK_OFFSET+arr_offset], x, hudPieceY, hudPieceSize);
        if ((HudPersistentData.isPushSelected() ? 1 : 0) == lr)
            render_hud_piece(guiGraphics, textures_array[SELECTED_OFFSET+arr_offset], x, hudPieceY, hudPieceSize);
        render_reserves_overlay(guiGraphics, textures_array[BACKGROUND_MASK_OFFSET+arr_offset], x, hudPieceY, hudPieceSize, manifestation);
        render_metallic_icon(guiGraphics, iconX, iconY, iconSize, manifestation, metalType);
        render_hud_piece(guiGraphics, textures_array[FRAME_OFFSET+arr_offset], x, hudPieceY, hudPieceSize);

        Font font = Minecraft.getInstance().font;

        if (hasManifestation(manifestation)) {
            render_hud_piece(guiGraphics, textures_array[EXTRA_OFFSET+arr_offset], x, hudPieceY, hudPieceSize);
            ISpiritweb spiritweb = getSpiritWeb();
            int mode = Math.abs(manifestation.getMode(spiritweb));
            int reserve_percentage = Math.round((manifestation.getInvestitureHud(spiritweb))*100);
            int xx = x + (lr == 0 ? extras_x_offset: hudPieceSize - extras_x_offset);
            draw_centered_string(guiGraphics, font, mode + "", xx, hudPieceY + extras_y_offset, get_strength_color(manifestation).getRGB());
            draw_centered_string(guiGraphics, font, reserve_percentage + "%", xx, hudPieceY + hudPieceSize - extras_y_offset, Config.barColor.getRGB());
        }

        if (ControllingMetallicHUD.getPopupTimer() > 0) {
            int xx = x + (lr == 0 ? -2 + hudPieceSize : 3);
            draw_side_aligned_string(guiGraphics, font, capitalizeString(metalType.getName()), xx, hudPieceY+hudPieceSize,
                    ControllingMetallicHUD.getPowerPopupIntendedTransparency(), lr == 0);
        }
    }

    public static final IGuiOverlay HUD_OVERLAY = (gui, guiGraphics, partialTick, width, height) -> {
        Minecraft minecraft = Minecraft.getInstance();

        // Stop rendering the overlay. F1 (hide GUI) or F3 (debug screen) (tho F3 is not working)
        if (minecraft.options.hideGui) // || gui.getDebugOverlay().showDebugScreen()) {
            return;

        ISpiritweb spiritweb = getSpiritWeb();
        if (spiritweb == null) return;

        if (!HudPersistentData.hasRegisteredPowers()) return;


        Metals.MetalType leftMetalType = HudPersistentData.getLeftMetalType();
        Metals.MetalType rightMetalType = HudPersistentData.getRightMetalType();
        boolean fl = HudPersistentData.isLeftFeruchemy();
        boolean fr = HudPersistentData.isRightFeruchemy();

        // Handling Misting / Ferring edge case
        if (rightMetalType == null && leftMetalType != null){
            rightMetalType = leftMetalType;
            leftMetalType = null;
            fr = HudPersistentData.isLeftFeruchemy();
            fl = HudPersistentData.isRightFeruchemy(); // redundant
        }

        int xOffset = 0;

        if (leftMetalType != null) {
            Manifestation manifestation = getSpecificManifestation(fl, leftMetalType);
            renderMainHUD(guiGraphics, fl ? 2 : 0, 0, xOffset, manifestation, leftMetalType);
            xOffset += hudPieceSize;
        }

        if (rightMetalType != null) {
            Manifestation manifestation = getSpecificManifestation(fr, rightMetalType);
            renderMainHUD(guiGraphics, fr ? 2 : 0, 1, xOffset, manifestation, rightMetalType);
        }
    };
}