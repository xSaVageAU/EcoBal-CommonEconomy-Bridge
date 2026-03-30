package savage.ecobalbridge;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/**
 * Centralized configuration for the EcoBal to Common Economy API bridge.
 * <p>
 * This class holds constants used across the bridge implementation, allowing for
 * easy adjustments to how the currency is represented in external mods.
 */
public final class BridgeConfig {
    
    private BridgeConfig() {
        // Prevent instantiation of utility class
    }

    /**
     * The namespace used for the currency and account identifiers.
     */
    public static final String NAMESPACE = "ecobal";

    /**
     * The display name of the currency as it appears in other mods.
     */
    public static final String CURRENCY_NAME = "EcoBal Dollar";

    /**
     * The ID used for the secondary part of the currency identifier (e.g., ecobal:dollar).
     */
    public static final String CURRENCY_ID = "dollar";

    /**
     * The item used as an icon to represent the entire "EcoBal Bridge" in GUIs.
     */
    public static final Item PROVIDER_ICON = Items.DIAMOND;

    /**
     * The item used as an icon to represent the "EcoBal Dollar" currency in GUIs.
     */
    public static final Item CURRENCY_ICON = Items.GOLD_NUGGET;

    /**
     * Internal scaling used to convert between EcoBal's double values and Common Economy's BigInteger.
     * <p>
     * A scale of 100 ensures that the BigInteger represents "cents" (2 decimal places).
     * Example: $1.50 (EcoBal) = 150 (Common Economy API).
     */
    public static final long SCALE = 100;
}
