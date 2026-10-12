package me.verdi_shade.alternative_cosmere_hud;

import com.mojang.blaze3d.systems.RenderSystem;
import leaf.cosmere.allomancy.common.Allomancy;
import leaf.cosmere.api.Metals;
import leaf.cosmere.api.spiritweb.ISpiritweb;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.verdi_shade.alternative_cosmere_hud.HudPersistentData.METAL_TYPE_GROUPS;


@SuppressWarnings({"removal"})
public class CustomHudOverlay {

    private static final ResourceLocation TEX_FERUCHEMY_HUD_LEFT = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/feruchemy_hud_left.png");
    private static final ResourceLocation TEX_FERUCHEMY_HUD_RIGHT = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/feruchemy_hud_right.png");
    private static final ResourceLocation TEX_ALLOMANCY_HUD_LEFT = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/allomancy_hud_left.png");
    private static final ResourceLocation TEX_ALLOMANCY_HUD_RIGHT = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/allomancy_hud_right.png");
    private static final ResourceLocation TEX_X = new ResourceLocation(AlternativeCosmereHUD.MODID, "textures/gui/x.png");

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
        render_icon(guiGraphics, resource, x, y, size, metalColor, 1.0f);
        lazySpiritweb.ifPresent(spiritweb -> {
            if (!spiritweb.hasManifestation(HudPersistentData.getSpecificManifestation(isFeruchemy, metalType)))
                render_icon(guiGraphics, TEX_X, x, y, size, Color.white, 1.0f);
        });
    }

    private static void render_icon(GuiGraphics guiGraphics, ResourceLocation resource, int x, int y, int size, Color color, float alpha){
        float r = color.getRed()/255.f, g = color.getGreen()/255.f, b = color.getBlue()/255.f;

        RenderSystem.setShaderTexture(0, resource);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);


        RenderSystem.setShaderColor(0f, 0f, 0f, alpha);
        guiGraphics.blit(resource, x+1, y+1, 0, 0, size, size, size, size);
        RenderSystem.setShaderColor(r, g, b, alpha);
        guiGraphics.blit(resource, x, y, 0, 0, size, size, size, size);
        RenderSystem.setShaderColor(0f, 0f, 0f, alpha);
    }

    private static void render_hud_piece(GuiGraphics guiGraphics, ResourceLocation resource, int x, int y, int size){
        guiGraphics.blit(resource, x, y, 0, 0, size, size, size, size);
    }

    public static final IGuiOverlay HUD_OVERLAY = (gui, guiGraphics, partialTick, width, height) -> {
        Minecraft minecraft = Minecraft.getInstance();

        // Stop rendering the overlay. F1 (hide GUI) or F3 (debug screen) (tho F3 is not working)
        if (minecraft.options.hideGui) // || gui.getDebugOverlay().showDebugScreen()) {
            return;

        LazyOptional<ISpiritweb> spirit_web = SpiritwebCapability.get(Minecraft.getInstance().player);

        int iconSize = 16;
        if (HudPersistentData.isFeruchemy()) {
            int feruchemyX = 10; int feruchemyY = 10; int feruchemySize = 41;
            render_hud_piece(guiGraphics, TEX_FERUCHEMY_HUD_LEFT, feruchemyX, feruchemyY, feruchemySize);
            render_hud_piece(guiGraphics, TEX_FERUCHEMY_HUD_RIGHT, feruchemyX, feruchemyY, feruchemySize);
            render_metallic_icon(guiGraphics, feruchemyX+feruchemySize/8, feruchemyY+4*feruchemySize/8, iconSize, HudPersistentData.isFeruchemy(), HudPersistentData.getLeftMetalType(), spirit_web);
            render_metallic_icon(guiGraphics, feruchemyX+4*feruchemySize/8, feruchemyY+feruchemySize/8, iconSize, HudPersistentData.isFeruchemy(), HudPersistentData.getRightMetalType(), spirit_web);
        } else {
            int allomancyX = 10; int allomancyY = 10; int allomancySize = 30;
            render_hud_piece(guiGraphics, TEX_ALLOMANCY_HUD_LEFT, allomancyX, allomancyY, allomancySize);
            render_hud_piece(guiGraphics, TEX_ALLOMANCY_HUD_RIGHT, allomancyX + allomancySize, allomancyY, allomancySize);
            int iconY = allomancyY+allomancySize/4;
            render_metallic_icon(guiGraphics, allomancyX+allomancySize/4, iconY, iconSize, HudPersistentData.isFeruchemy(), HudPersistentData.getLeftMetalType(), spirit_web);
            render_metallic_icon(guiGraphics, allomancyX+5*allomancySize/4, iconY, iconSize, HudPersistentData.isFeruchemy(), HudPersistentData.getRightMetalType(), spirit_web);
        }

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