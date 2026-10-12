package me.verdi_shade.alternative_cosmere_hud;

import leaf.cosmere.allomancy.common.manifestation.AllomancyManifestation;
import leaf.cosmere.api.Metals;
import leaf.cosmere.api.manifestation.Manifestation;

import static leaf.cosmere.allomancy.common.registries.AllomancyManifestations.ALLOMANCY_POWERS;
import static leaf.cosmere.feruchemy.common.registries.FeruchemyManifestations.FERUCHEMY_POWERS;

public class HudPersistentData {
    private static boolean is_feruchemy = false;
    private static boolean is_allomancy_push_selected = false;
    private static boolean is_feruchemy_push_selected = false;
    private static int allomancy_selected_pair = 0;
    private static int feruchemy_selected_pair = 0;
    public static final Metals.MetalType[][] METAL_TYPE_GROUPS = {
            {Metals.MetalType.IRON, Metals.MetalType.STEEL},
            {Metals.MetalType.TIN, Metals.MetalType.PEWTER},
            {Metals.MetalType.ZINC, Metals.MetalType.BRASS},
            {Metals.MetalType.COPPER, Metals.MetalType.BRONZE},
            {Metals.MetalType.GOLD, Metals.MetalType.ELECTRUM},
            {Metals.MetalType.CADMIUM, Metals.MetalType.BENDALLOY},
            {Metals.MetalType.ALUMINUM, Metals.MetalType.DURALUMIN},
            {Metals.MetalType.CHROMIUM, Metals.MetalType.NICROSIL},
            {Metals.MetalType.ATIUM, Metals.MetalType.ATIUM},
    };
    private static final int GROUP_NUM = METAL_TYPE_GROUPS.length;

    public static void toggleMetallicArt(){
        HudPersistentData.is_feruchemy = !HudPersistentData.is_feruchemy;
    }

    public static boolean isFeruchemy() {
        return is_feruchemy;
    }

    public static void toggleAllomancyPushSelected(){
        HudPersistentData.is_allomancy_push_selected = !HudPersistentData.is_allomancy_push_selected;
    }

    public static boolean isAllomancyPushSelected() {
        return HudPersistentData.is_allomancy_push_selected;
    }

    public static int getAllomancySelectedInPair(){
        return HudPersistentData.isAllomancyPushSelected() ? 1 : 0;
    }

    public static void toggleFeruchemyPushSelected(){
        HudPersistentData.is_feruchemy_push_selected = !HudPersistentData.is_feruchemy_push_selected;
    }

    public static boolean isFeruchemyPushSelected() {
        return HudPersistentData.is_feruchemy_push_selected;
    }

    public static int getFeruchemySelectedInPair(){
        return HudPersistentData.isFeruchemyPushSelected() ? 1 : 0;
    }

    public static void togglePushSelected(){
        if (isFeruchemy())
            toggleFeruchemyPushSelected();
        else
            toggleAllomancyPushSelected();
    }

    public static boolean isPushSelected() {
        return isFeruchemy() ? isFeruchemyPushSelected() : isAllomancyPushSelected();
    }

    public static int getSelectedInPair(){
        return HudPersistentData.isPushSelected() ? 1 : 0;
    }

    public static int getAllomancySelectedPair() {
        return HudPersistentData.allomancy_selected_pair;
    }

    public static int getFeruchemySelectedPair() {
        return HudPersistentData.feruchemy_selected_pair;
    }

    public static int getSelectedPair(){
        return isFeruchemy() ? getFeruchemySelectedPair() : getAllomancySelectedPair();
    }

    private static int modulo(int val){
        return (val%GROUP_NUM+GROUP_NUM)%GROUP_NUM;
    }

    public static void setFeruchemySelectedPair(int selected_pair){
        HudPersistentData.feruchemy_selected_pair = modulo(selected_pair);
    }

    public static void setAllomancySelectedPair(int selected_pair){
        HudPersistentData.allomancy_selected_pair = modulo(selected_pair);
    }

    public static void setSelectedPair(int selected_pair) {
        if (isFeruchemy())
            setFeruchemySelectedPair(selected_pair);
        else
            setAllomancySelectedPair(selected_pair);
    }

    public static void incrementAllomancySelectedPair(){
        HudPersistentData.allomancy_selected_pair = modulo(HudPersistentData.allomancy_selected_pair+1);
    }

    public static void decreaseAllomancySelectedPair(){
        HudPersistentData.allomancy_selected_pair = modulo(HudPersistentData.allomancy_selected_pair-1);
    }

    public static void directAllomancySelectedPair(double val){
        if (val > 0)
            HudPersistentData.incrementAllomancySelectedPair();
        else if (val < 0)
            HudPersistentData.decreaseAllomancySelectedPair();
    }

    public static void incrementFeruchemySelectedPair(){
        HudPersistentData.feruchemy_selected_pair = modulo(HudPersistentData.feruchemy_selected_pair +1);
    }

    public static void decreaseFeruchemySelectedPair(){
        HudPersistentData.feruchemy_selected_pair = modulo(HudPersistentData.feruchemy_selected_pair -1);
    }

    public static void directFeruchemySelectedPair(double val){
        if (val > 0)
            HudPersistentData.incrementFeruchemySelectedPair();
        else if (val < 0)
            HudPersistentData.decreaseFeruchemySelectedPair();
    }

    public static void incrementSelectedPair(){
        if (isFeruchemy())
            incrementFeruchemySelectedPair();
        else
            incrementAllomancySelectedPair();
    }

    public static void decreaseSelectedPair(){
        if (isFeruchemy())
            decreaseFeruchemySelectedPair();
        else
            decreaseAllomancySelectedPair();
    }

    public static void directSelectedPair(double val){
        if (isFeruchemy())
            directFeruchemySelectedPair(val);
        else
            directAllomancySelectedPair(val);
    }

    public static Manifestation getManifestation(int v){
        Metals.MetalType metalType = METAL_TYPE_GROUPS[getSelectedPair()][v];
        return getSpecificManifestation(isFeruchemy(), metalType);
    }

    public static Manifestation getLeftManifestation(){
        return getManifestation(0);
    }

    public static Manifestation getRightManifestation(){
        return getManifestation(1);
    }

    public static Manifestation getSelectedManifestation(){
        return getManifestation(getSelectedInPair());
    }

    public static Metals.MetalType getLeftMetalType(){
        return METAL_TYPE_GROUPS[getSelectedPair()][0];
    }

    public static Metals.MetalType getRightMetalType(){
        return METAL_TYPE_GROUPS[getSelectedPair()][1];
    }

    public static Metals.MetalType getSelectedMetalType(){
        return METAL_TYPE_GROUPS[getSelectedPair()][getSelectedInPair()];
    }

    public static Manifestation getSpecificManifestation(boolean is_feruchemy, Metals.MetalType metalType){
        return (is_feruchemy ? FERUCHEMY_POWERS : ALLOMANCY_POWERS).get(metalType).getManifestation();
    }

}
