package me.verdi_shade.alternative_cosmere_hud;

import leaf.cosmere.api.Metals;
import leaf.cosmere.api.manifestation.Manifestation;

import java.util.stream.IntStream;

import static me.verdi_shade.alternative_cosmere_hud.SpiritwebHandling.getSpiritWeb;
import static me.verdi_shade.alternative_cosmere_hud.SpiritwebHandling.hasSpecificManifestation;

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

    private static final int[] all_groups_to_available_groups_allomancy = IntStream.range(0, GROUP_NUM).toArray();
    private static final int[] available_groups_to_all_groups_allomancy = IntStream.range(0, GROUP_NUM).toArray();
    private static int available_allomancy_group_num = GROUP_NUM;
    private static final int[] all_groups_to_available_groups_feruchemy = IntStream.range(0, GROUP_NUM).toArray();
    private static final int[] available_groups_to_all_groups_feruchemy = IntStream.range(0, GROUP_NUM).toArray();
    private static int available_feruchemy_group_num = GROUP_NUM;

    private static boolean twinborn_mode = false;
    private static Metals.MetalType twinborn_allomancy_metal = Metals.MetalType.IRON;
    private static Metals.MetalType twinborn_feruchemy_metal = Metals.MetalType.IRON;

    public static void update_availability_arrays(){
        if (getSpiritWeb() == null) return;

        int allomancy_index = 0;
        int feruchemy_index = 0;
        boolean separate = false;

        Metals.MetalType allomancy_metal = null;
        Metals.MetalType feruchemy_metal = null;

        for (int i = 0; i < GROUP_NUM; i++) {
            Metals.MetalType[] metal_pair = METAL_TYPE_GROUPS[i];
            boolean left, right;
            all_groups_to_available_groups_allomancy[i] = allomancy_index;
            left = hasSpecificManifestation(false, metal_pair[0]);
            right = hasSpecificManifestation(false, metal_pair[1]);
            if (left || right) available_groups_to_all_groups_allomancy[allomancy_index++] = i;
            if (left) {if (allomancy_metal == null) allomancy_metal = metal_pair[0]; else separate = true;}
            if (right) {if (allomancy_metal == null) allomancy_metal = metal_pair[1];else separate = true;}

            all_groups_to_available_groups_feruchemy[i] = feruchemy_index;
            left = hasSpecificManifestation(true, metal_pair[0]);
            right = hasSpecificManifestation(true, metal_pair[1]);
            if (left || right) available_groups_to_all_groups_feruchemy[feruchemy_index++] = i;
            if (left) {if (feruchemy_metal == null) feruchemy_metal = metal_pair[0]; else separate = true;}
            if (right) {if (feruchemy_metal == null) feruchemy_metal = metal_pair[1];else separate = true;}
        }

        allomancy_selected_pair = available_groups_to_all_groups_allomancy[all_groups_to_available_groups_allomancy[allomancy_selected_pair]];
        feruchemy_selected_pair = available_groups_to_all_groups_feruchemy[all_groups_to_available_groups_feruchemy[feruchemy_selected_pair]];

        twinborn_mode = !separate;
        twinborn_allomancy_metal = allomancy_metal;
        twinborn_feruchemy_metal = feruchemy_metal;

        available_allomancy_group_num = allomancy_index;
        available_feruchemy_group_num = feruchemy_index;

        if (available_allomancy_group_num == 0) is_feruchemy = true;
        if (available_feruchemy_group_num == 0) is_feruchemy = false;
    }

    public static boolean hasRegisteredPowers(){
        return available_allomancy_group_num + available_feruchemy_group_num > 0;
    }

    public static void toggleMetallicArt(){
        if (twinborn_mode || available_allomancy_group_num == 0 || available_feruchemy_group_num == 0) return;
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
        return modulo(val, GROUP_NUM);
    }

    private static int modulo(int val, int n){
        return (val%n+n)%n;
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
        int availability_index = all_groups_to_available_groups_allomancy[allomancy_selected_pair];
        int next_availability_index= modulo(availability_index +1, available_allomancy_group_num);
        allomancy_selected_pair = available_groups_to_all_groups_allomancy[next_availability_index];
    }

    public static void decreaseAllomancySelectedPair(){
        int availability_index = all_groups_to_available_groups_allomancy[allomancy_selected_pair];
        int next_availability_index= modulo(availability_index -1, available_allomancy_group_num);
        allomancy_selected_pair = available_groups_to_all_groups_allomancy[next_availability_index];
    }

    public static void directAllomancySelectedPair(double val){
        if (val > 0)
            HudPersistentData.incrementAllomancySelectedPair();
        else if (val < 0)
            HudPersistentData.decreaseAllomancySelectedPair();
    }

    public static void incrementFeruchemySelectedPair(){
        int availability_index = all_groups_to_available_groups_feruchemy[feruchemy_selected_pair];
        int next_availability_index= modulo(availability_index +1, available_feruchemy_group_num);
        feruchemy_selected_pair = available_groups_to_all_groups_feruchemy[next_availability_index];
    }

    public static void decreaseFeruchemySelectedPair(){
        int availability_index = all_groups_to_available_groups_feruchemy[feruchemy_selected_pair];
        int next_availability_index= modulo(availability_index -1, available_feruchemy_group_num);
        feruchemy_selected_pair = available_groups_to_all_groups_feruchemy[next_availability_index];
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
        Metals.MetalType metalType;
        boolean isFeruchemy;
        if (twinborn_mode) {
            metalType = v == 0 ? twinborn_allomancy_metal : twinborn_feruchemy_metal;
            isFeruchemy = v == 1;
        } else {
            metalType = METAL_TYPE_GROUPS[getSelectedPair()][v];
            isFeruchemy = isFeruchemy();
        }
        return SpiritwebHandling.getSpecificManifestation(isFeruchemy, metalType);
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
        return twinborn_mode ? twinborn_allomancy_metal : METAL_TYPE_GROUPS[getSelectedPair()][0];
    }

    public static Metals.MetalType getRightMetalType(){
        return twinborn_mode ? twinborn_feruchemy_metal : METAL_TYPE_GROUPS[getSelectedPair()][1];
    }

    public static Metals.MetalType getSelectedMetalType(){
        if (twinborn_mode)
            return getSelectedInPair() == 0 ? twinborn_allomancy_metal : twinborn_feruchemy_metal;
        else
            return METAL_TYPE_GROUPS[getSelectedPair()][getSelectedInPair()];
    }

    public static boolean isLeftFeruchemy(){
        return !twinborn_mode && isFeruchemy();
    }

    public static boolean isRightFeruchemy(){
        return twinborn_mode || isFeruchemy();
    }
}
