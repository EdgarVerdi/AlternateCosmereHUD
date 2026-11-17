package me.verdi_shade.alternative_cosmere_hud;

import leaf.cosmere.api.Metals;
import leaf.cosmere.api.manifestation.Manifestation;
import leaf.cosmere.api.spiritweb.ISpiritweb;
import leaf.cosmere.common.cap.entity.SpiritwebCapability;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.util.LazyOptional;

import static leaf.cosmere.allomancy.common.registries.AllomancyManifestations.ALLOMANCY_POWERS;
import static leaf.cosmere.feruchemy.common.registries.FeruchemyManifestations.FERUCHEMY_POWERS;

public class SpiritwebHandling {

    private static ISpiritweb spiritweb = null;

    public static ISpiritweb getSpiritWeb() {
        if (spiritweb != null)
            return spiritweb;

        LazyOptional<ISpiritweb> lazy_spiritweb = SpiritwebCapability.get(Minecraft.getInstance().player);
        if (lazy_spiritweb.isPresent()) {
            spiritweb = lazy_spiritweb.orElse(null);
            return spiritweb;
        }

        System.out.println("wow, spiritweb was null! oh no!");
        throw new RuntimeException("Oh Shit!");
    }

    public static Manifestation getSpecificManifestation(boolean is_feruchemy, Metals.MetalType metalType){
        return (is_feruchemy ? FERUCHEMY_POWERS : ALLOMANCY_POWERS).get(metalType).getManifestation();
    }

    public static boolean hasManifestation(Manifestation manifestation){
        return getSpiritWeb().hasManifestation(manifestation);
    }

    public static boolean hasSpecificManifestation(boolean is_feruchemy, Metals.MetalType metalType){
        return hasManifestation(getSpecificManifestation(is_feruchemy, metalType));
    }
}
