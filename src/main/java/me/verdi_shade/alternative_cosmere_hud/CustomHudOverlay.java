package me.verdi_shade.alternative_cosmere_hud;

import com.mojang.blaze3d.systems.RenderSystem;
import leaf.cosmere.allomancy.common.Allomancy;
import leaf.cosmere.api.Manifestations;
import leaf.cosmere.api.Metals;
import leaf.cosmere.api.manifestation.Manifestation;
import leaf.cosmere.api.spiritweb.ISpiritweb;
import leaf.cosmere.client.gui.GuiUtils;
import leaf.cosmere.common.cap.entity.SpiritwebCapability;
import leaf.cosmere.feruchemy.common.Feruchemy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.common.util.LazyOptional;
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

    private static void render_metallic_icon(GuiGraphics guiGraphics, int x, int y, int size, Manifestation manifestation, Metals.MetalType metal, LazyOptional<ISpiritweb> lazySpiritweb) {
        boolean isFeruchemy = isFeruchemy(manifestation);
        ResourceLocation resource = maps.get(isFeruchemy ? 1 : 0).get(metal.getName());
        Color metalColor = metal.getColor();
        // render_icon(guiGraphics, resource, x, y, size, metalColor, 1.0f);
        AtomicBoolean hasMetal = new AtomicBoolean(false);
        lazySpiritweb.ifPresent(spiritweb -> hasMetal.set(spiritweb.hasManifestation(manifestation)));

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

    private static Color get_strength_color(Manifestation manifestation, ISpiritweb spiritweb){
        boolean isFeruchemy = isFeruchemy(manifestation);
        int curr = manifestation.getMode(spiritweb);
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
            GuiGraphics guiGraphics, ResourceLocation resource, int x, int y, int size,
            LazyOptional<ISpiritweb> lazySpiritweb, Manifestation manifestation
    ){
        lazySpiritweb.ifPresent(spiritweb -> {
            if (!spiritweb.hasManifestation(manifestation))
                return;

            Color barColor = get_strength_color(manifestation, spiritweb);
            float r = barColor.getRed()/255.f, g = barColor.getGreen()/255.f, b = barColor.getBlue()/255.f;

            float reserve_percentage = 1f-manifestation.getInvestitureHud(spiritweb);
            int cut_offset = (int) (size*reserve_percentage);
            RenderSystem.setShaderColor(r, g, b, 1.0f);
            guiGraphics.blit(resource, x, y+cut_offset, 0, cut_offset, size, size-cut_offset, size, size);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        });
    }

    private static void render_reserves_overlay(
            GuiGraphics guiGraphics, ResourceLocation resource, int x, int y, int size,
            LazyOptional<ISpiritweb> lazySpiritweb, boolean isFeruchemy, Metals.MetalType metal){
        Manifestation manifestation = HudPersistentData.getSpecificManifestation(isFeruchemy, metal);
        render_reserves_overlay(guiGraphics, resource, x, y, size, lazySpiritweb, manifestation);
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

    public static final IGuiOverlay HUD_OVERLAY = (gui, guiGraphics, partialTick, width, height) -> {
        Minecraft minecraft = Minecraft.getInstance();

        // Stop rendering the overlay. F1 (hide GUI) or F3 (debug screen) (tho F3 is not working)
        if (minecraft.options.hideGui) // || gui.getDebugOverlay().showDebugScreen()) {
            return;

        LazyOptional<ISpiritweb> spirit_web = SpiritwebCapability.get(Minecraft.getInstance().player);

        int iconSize = 16;
        int hudPieceX = 10; int hudPieceY = 10; int hudPieceSize = 42;
        int f = HudPersistentData.isFeruchemy() ? 2 : 0;

        Manifestation leftManifestation = HudPersistentData.getLeftManifestation();
        Manifestation rightManifestation = HudPersistentData.getRightManifestation();

        render_background(guiGraphics, textures_array[f+BACKGROUND_MASK_OFFSET], hudPieceX, hudPieceY, hudPieceSize);
        render_background(guiGraphics, textures_array[f+BACKGROUND_MASK_OFFSET+1], hudPieceX + hudPieceSize, hudPieceY, hudPieceSize);

        if (!HudPersistentData.isPushSelected())
            render_hud_piece(guiGraphics, textures_array[f+SELECTED_OFFSET], hudPieceX, hudPieceY, hudPieceSize);
        else
            render_hud_piece(guiGraphics, textures_array[f+SELECTED_OFFSET+1], hudPieceX + hudPieceSize, hudPieceY, hudPieceSize);

        render_reserves_overlay(guiGraphics, textures_array[f+BACKGROUND_MASK_OFFSET],
                hudPieceX, hudPieceY, hudPieceSize, spirit_web, leftManifestation);
        render_reserves_overlay(guiGraphics, textures_array[f+BACKGROUND_MASK_OFFSET+1],
                hudPieceX+hudPieceSize, hudPieceY, hudPieceSize, spirit_web, rightManifestation);

        int iconX1 = hudPieceX+(hudPieceSize-iconSize)*7/8;
        int iconX2 = hudPieceX+(hudPieceSize-iconSize)/8 + hudPieceSize;
        int iconY = hudPieceY+(hudPieceSize-iconSize)/2-1;
        render_metallic_icon(guiGraphics, iconX1, iconY, iconSize, leftManifestation, HudPersistentData.getLeftMetalType(), spirit_web);
        render_metallic_icon(guiGraphics, iconX2, iconY, iconSize, rightManifestation, HudPersistentData.getRightMetalType(), spirit_web);

        render_hud_piece(guiGraphics, textures_array[f+FRAME_OFFSET], hudPieceX, hudPieceY, hudPieceSize);
        render_hud_piece(guiGraphics, textures_array[f+FRAME_OFFSET+1], hudPieceX + hudPieceSize, hudPieceY, hudPieceSize);

        spirit_web.ifPresent(spiritweb -> {
            int x_off = extras_x_offset;
            int y_off = extras_y_offset;
            if (spiritweb.hasManifestation(leftManifestation)) {
                render_hud_piece(guiGraphics, textures_array[f + EXTRA_OFFSET], hudPieceX, hudPieceY, hudPieceSize);
                int mode = Math.abs(leftManifestation.getMode(spiritweb));
                int reserve_percentage = Math.round((1f-leftManifestation.getInvestitureHud(spiritweb))*100);
                draw_centered_string(guiGraphics, minecraft.font, mode + "", hudPieceX + x_off, hudPieceY + y_off, get_strength_color(leftManifestation, spiritweb).getRGB());
                draw_centered_string(guiGraphics, minecraft.font, reserve_percentage + "%", hudPieceX + x_off, hudPieceY + hudPieceSize - y_off, Config.barColor.getRGB());
            }
            if (spiritweb.hasManifestation(rightManifestation)) {
                render_hud_piece(guiGraphics, textures_array[f + EXTRA_OFFSET + 1], hudPieceX + hudPieceSize, hudPieceY, hudPieceSize);
                int mode = Math.abs(rightManifestation.getMode(spiritweb));
                int reserve_percentage = Math.round((1f-rightManifestation.getInvestitureHud(spiritweb))*100);
                draw_centered_string(guiGraphics, minecraft.font, mode + "", hudPieceX + 2 * hudPieceSize - x_off, hudPieceY + y_off, get_strength_color(rightManifestation, spiritweb).getRGB());
                draw_centered_string(guiGraphics, minecraft.font, reserve_percentage + "%", hudPieceX + 2 * hudPieceSize - x_off, hudPieceY + hudPieceSize - y_off, Config.barColor.getRGB());
            }
        });

        //render_additional_data(minecraft, guiGraphics);
    };

    private static void render_additional_data(Minecraft minecraft, GuiGraphics guiGraphics){
        int step = 10; int acc = 0;
        int textX = 100;
        RenderSystem.setShaderColor(0f, 1f, 1f, 1f);
        guiGraphics.drawString(minecraft.font, HudPersistentData.isFeruchemy() ? "Feruchemy" : "Allomancy", textX, (++acc)*step, 0x00FFFF, true);
        guiGraphics.drawString(minecraft.font, "Allomancy Dir: " + (HudPersistentData.isAllomancyPushSelected() ? "Push" : "Pull"), textX, (++acc)*step, 0x00FFFF, true);
        guiGraphics.drawString(minecraft.font, "Feruchemy Dir: " + (HudPersistentData.isFeruchemyPushSelected() ? "Push" : "Pull"), textX, (++acc)*step, 0x00FFFF, true);
        guiGraphics.drawString(minecraft.font, "Allomancy Pair: " + HudPersistentData.getAllomancySelectedPair(), textX, (++acc)*step, 0x00FFFF, true);
        guiGraphics.drawString(minecraft.font, "Feruchemy Pair: " + HudPersistentData.getFeruchemySelectedPair(), textX, (++acc)*step, 0x00FFFF, true);
        //guiGraphics.drawString(minecraft.font, HudPersistentData.getSelectedMetalType().toString(), textX, (++acc)*step, 0x00FFFF, true);
        guiGraphics.drawString(minecraft.font, HudPersistentData.getSelectedManifestation().getName(), textX, (++acc)*step, 0x00FFFF, true);
        RenderSystem.setShaderColor(0f, 0f, 0f, 1f);
    }
}