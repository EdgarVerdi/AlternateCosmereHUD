package me.verdi_shade.alternative_cosmere_hud;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {
    public static final String CATEGORY = "key.category.alternative_cosmere_hud";

    public static final KeyMapping KEY_ACTION_ONE = new KeyMapping(
            "key.mymod.action_one", // The translation key for the controls menu
            KeyConflictContext.IN_GAME, // Only works while actively playing
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Z,
            CATEGORY
    );

    public static final KeyMapping KEY_ACTION_TWO = new KeyMapping(
            "key.mymod.action_two",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_X,
            CATEGORY
    );
}