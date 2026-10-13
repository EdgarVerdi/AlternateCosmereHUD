package me.verdi_shade.alternative_cosmere_hud;

import com.mojang.blaze3d.systems.RenderSystem;
import leaf.cosmere.allomancy.common.Allomancy;
import leaf.cosmere.api.Metals;
import leaf.cosmere.api.manifestation.Manifestation;
import leaf.cosmere.api.spiritweb.ISpiritweb;
import leaf.cosmere.client.gui.GuiUtils;
import leaf.cosmere.common.cap.entity.SpiritwebCapability;
import leaf.cosmere.feruchemy.common.Feruchemy;
import net.minecraft.client.Minecraft;
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

import static me.verdi_shade.alternative_cosmere_hud.HudPersistentData.METAL_TYPE_GROUPS;


@SuppressWarnings({"removal"})
public class CustomHudOverlay {

    private static final ResourceLocation TEX_FERUCHEMY_HUD_LEFT = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/feruchemy_hud_left.png");
    private static final ResourceLocation TEX_FERUCHEMY_HUD_RIGHT = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/feruchemy_hud_right.png");
    private static final ResourceLocation TEX_ALLOMANCY_HUD_LEFT = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/allomancy_hud_left.png");
    private static final ResourceLocation TEX_ALLOMANCY_HUD_RIGHT = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/allomancy_hud_right.png");

    private static final ResourceLocation TEX_FERUCHEMY_HUD_LEFT_SELECTED = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/feruchemy_hud_left_selected.png");
    private static final ResourceLocation TEX_FERUCHEMY_HUD_RIGHT_SELECTED = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/feruchemy_hud_right_selected.png");
    private static final ResourceLocation TEX_ALLOMANCY_HUD_LEFT_SELECTED = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/allomancy_hud_left_selected.png");
    private static final ResourceLocation TEX_ALLOMANCY_HUD_RIGHT_SELECTED = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/allomancy_hud_right_selected.png");

    private static final ResourceLocation TEX_FERUCHEMY_HUD_LEFT_RESERVES_OVERLAY = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/feruchemy_hud_left_reserves_overlay.png");
    private static final ResourceLocation TEX_FERUCHEMY_HUD_RIGHT_RESERVES_OVERLAY = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/feruchemy_hud_right_reserves_overlay.png");
    private static final ResourceLocation TEX_ALLOMANCY_HUD_LEFT_RESERVES_OVERLAY = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/allomancy_hud_left_reserves_overlay.png");
    private static final ResourceLocation TEX_ALLOMANCY_HUD_RIGHT_RESERVES_OVERLAY = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/allomancy_hud_right_reserves_overlay.png");

    private static final ResourceLocation[] textures_array = {
            TEX_ALLOMANCY_HUD_LEFT, TEX_ALLOMANCY_HUD_RIGHT,
            TEX_FERUCHEMY_HUD_LEFT, TEX_FERUCHEMY_HUD_RIGHT,

            TEX_ALLOMANCY_HUD_LEFT_SELECTED, TEX_ALLOMANCY_HUD_RIGHT_SELECTED,
            TEX_FERUCHEMY_HUD_LEFT_SELECTED, TEX_FERUCHEMY_HUD_RIGHT_SELECTED,

            TEX_ALLOMANCY_HUD_LEFT_RESERVES_OVERLAY, TEX_ALLOMANCY_HUD_RIGHT_RESERVES_OVERLAY,
            TEX_FERUCHEMY_HUD_LEFT_RESERVES_OVERLAY, TEX_FERUCHEMY_HUD_RIGHT_RESERVES_OVERLAY
    };

    private static final int SELECTED_OFFSET = 4;
    private static final int RESERVES_OVERLAY_OFFSET = 8;

    private static final List<Map<String, ResourceLocation>> maps = Stream.of(false, true)
            .map(isFeruchemy -> {
                String modid = isFeruchemy ? Feruchemy.MODID : Allomancy.MODID;
                return Arrays.stream(METAL_TYPE_GROUPS)
                        .flatMap(Arrays::stream) // Flatten the 2D array of metals
                        .distinct()
                        .collect(Collectors.toMap(
                                Metals.MetalType::getName, // The key
                                metal -> new ResourceLocation(modid, "textures/icon/" + modid + "/" + metal.getName() + ".png") // The value
                        ));
            })
            .toList();

    private static void render_metallic_icon(GuiGraphics guiGraphics, int x, int y, int size, boolean isFeruchemy, Metals.MetalType metalType, LazyOptional<ISpiritweb> lazySpiritweb){
        ResourceLocation resource = maps.get(isFeruchemy ? 1 : 0).get(metalType.getName());
        Color metalColor = metalType.getColor();
        // render_icon(guiGraphics, resource, x, y, size, metalColor, 1.0f);
        AtomicBoolean hasMetal = new AtomicBoolean(false);
        lazySpiritweb.ifPresent(spiritweb -> hasMetal.set(spiritweb.hasManifestation(HudPersistentData.getSpecificManifestation(isFeruchemy, metalType))));

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
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
    }

    private static void render_hud_piece(GuiGraphics guiGraphics, ResourceLocation resource, int x, int y, int size){
        guiGraphics.blit(resource, x, y, 0, 0, size, size, size, size);
    }

    private static void render_reserves_overlay(GuiGraphics guiGraphics, ResourceLocation resource, int x, int y, int size, LazyOptional<ISpiritweb> lazySpiritweb, boolean isFeruchemy, Metals.MetalType metal){
        lazySpiritweb.ifPresent(spiritweb -> {
            Manifestation manifestation = HudPersistentData.getSpecificManifestation(isFeruchemy, metal);
            if (!spiritweb.hasManifestation(manifestation))
                return;

            int curr = manifestation.getMode(spiritweb);
            //Color color = new Color(0xFFDDDDDD).brighter();
            Color color = Color.white;
            float intensity = 0.5f;

            if (curr > 0)
                color = GuiUtils.POSITIVE_USE_COLOR;
            else if (curr < 0)
                color = GuiUtils.NEGATIVE_USE_COLOR;

            if (isFeruchemy) {
                if (curr < 0)       intensity = Math.min(Math.abs(curr) * (1f / 16f), 1.0f);
                else if (curr > 0)  intensity = Math.min(Math.abs(curr) * (1f / 5f ), 1.0f);
            } else if (curr != 0)   intensity = Math.min(Math.abs(curr) * 0.2f, 1.0f);

            float r = color.getRed()/255.f, g = color.getGreen()/255.f, b = color.getBlue()/255.f;


            float reserve_percentage = 1f- spiritweb.getSelectedManifestation().getInvestitureHud(spiritweb);
            int cut_offset = (int) (size*reserve_percentage);
            RenderSystem.setShaderColor(r, g, b, intensity);
            guiGraphics.blit(resource, x, y+cut_offset, 0, cut_offset, size, size-cut_offset, size, size);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        });
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

        render_hud_piece(guiGraphics, textures_array[f], hudPieceX, hudPieceY, hudPieceSize);
        render_hud_piece(guiGraphics, textures_array[f+1], hudPieceX + hudPieceSize, hudPieceY, hudPieceSize);

        if (!HudPersistentData.isPushSelected())
            render_hud_piece(guiGraphics, textures_array[f+SELECTED_OFFSET], hudPieceX, hudPieceY, hudPieceSize);
        else
            render_hud_piece(guiGraphics, textures_array[f+SELECTED_OFFSET+1], hudPieceX + hudPieceSize, hudPieceY, hudPieceSize);

        render_reserves_overlay(guiGraphics, textures_array[f+RESERVES_OVERLAY_OFFSET],
                hudPieceX, hudPieceY, hudPieceSize, spirit_web,
                HudPersistentData.isFeruchemy(), HudPersistentData.getLeftMetalType());
        render_reserves_overlay(guiGraphics, textures_array[f+RESERVES_OVERLAY_OFFSET+1],
                hudPieceX+hudPieceSize, hudPieceY, hudPieceSize, spirit_web,
                HudPersistentData.isFeruchemy(), HudPersistentData.getRightMetalType());


        int iconX1 = hudPieceX+(hudPieceSize-iconSize)*7/8;
        int iconX2 = hudPieceX+(hudPieceSize-iconSize)/8 + hudPieceSize;
        int iconY = hudPieceY+(hudPieceSize-iconSize)/2-1;
        render_metallic_icon(guiGraphics, iconX1, iconY, iconSize, HudPersistentData.isFeruchemy(), HudPersistentData.getLeftMetalType(), spirit_web);
        render_metallic_icon(guiGraphics, iconX2, iconY, iconSize, HudPersistentData.isFeruchemy(), HudPersistentData.getRightMetalType(), spirit_web);


        int step = 10; int acc = 0;
        int textY = 100;
        RenderSystem.setShaderColor(0f, 1f, 1f, 1f);
        guiGraphics.drawString(minecraft.font, HudPersistentData.isFeruchemy() ? "Feruchemy" : "Allomancy", textY, (++acc)*step, 0x00FFFF, true);
        guiGraphics.drawString(minecraft.font, "Allomancy Dir: " + (HudPersistentData.isAllomancyPushSelected() ? "Push" : "Pull"), textY, (++acc)*step, 0x00FFFF, true);
        guiGraphics.drawString(minecraft.font, "Feruchemy Dir: " + (HudPersistentData.isFeruchemyPushSelected() ? "Push" : "Pull"), textY, (++acc)*step, 0x00FFFF, true);
        guiGraphics.drawString(minecraft.font, "Allomancy Pair: " + HudPersistentData.getAllomancySelectedPair(), textY, (++acc)*step, 0x00FFFF, true);
        guiGraphics.drawString(minecraft.font, "Feruchemy Pair: " + HudPersistentData.getFeruchemySelectedPair(), textY, (++acc)*step, 0x00FFFF, true);
        //guiGraphics.drawString(minecraft.font, HudPersistentData.getSelectedMetalType().toString(), textY, (++acc)*step, 0x00FFFF, true);
        guiGraphics.drawString(minecraft.font, HudPersistentData.getSelectedManifestation().getName(), textY, (++acc)*step, 0x00FFFF, true);
        RenderSystem.setShaderColor(0f, 0f, 0f, 1f);
    };
}