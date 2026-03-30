package savage.ecobalbridge.integration;

import eu.pb4.common.economy.api.EconomyCurrency;
import eu.pb4.common.economy.api.EconomyProvider;
import me.andy.ecobal.api.EconomyManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import savage.ecobalbridge.BridgeConfig;

import java.math.BigInteger;

/**
 * Common Economy API representation of the EcoBal currency.
 * <p>
 * Handles the display naming, ID representation, and conversion logic between
 * EcoBal's doubles and Common Economy API's BigInteger format.
 */
public class EcobalEconomyCurrency implements EconomyCurrency {
    
    /**
     * The primary identifier for this currency within the registered namespace.
     */
    public static final Identifier ID = Identifier.fromNamespaceAndPath(BridgeConfig.NAMESPACE, BridgeConfig.CURRENCY_ID);
    
    private final EconomyProvider provider;

    public EcobalEconomyCurrency(EconomyProvider provider) {
        this.provider = provider;
    }

    /**
     * @return The display name of the currency (e.g., "EcoBal Dollar").
     */
    @Override
    public Component name() {
        return Component.literal(BridgeConfig.CURRENCY_NAME);
    }

    @Override
    public Identifier id() {
        return ID;
    }

    /**
     * Formats a raw BigInteger value into a human-readable currency string.
     * Uses the configured scale to convert values back into EcoBal's decimal format.
     *
     * @param value The raw BigInteger value (e.g., 150 cents).
     * @param full Whether to use the full formatting.
     * @return A formatted currency string (e.g., "$1.50").
     */
    @Override
    public String formatValue(BigInteger value, boolean full) {
        // Scaling ensures calculations match EcoBal's double-based decimal format.
        double balance = value.doubleValue() / (double) BridgeConfig.SCALE;
        return EconomyManager.formatBalance(balance);
    }

    @Override
    public Component formatValueComponent(BigInteger value, boolean full) {
        return Component.literal(formatValue(value, full));
    }

    /**
     * Parses a currency string into the raw BigInteger format.
     *
     * @param value The string entered by the user (e.g., "1.50").
     * @return The correctly scaled BigInteger representing the value.
     */
    @Override
    public BigInteger parseValue(String value) {
        try {
            double d = Double.parseDouble(value.replaceAll("[^0-9.-]", ""));
            return BigInteger.valueOf((long)(d * BridgeConfig.SCALE));
        } catch (NumberFormatException e) {
            return BigInteger.ZERO;
        }
    }

    @Override
    public EconomyProvider provider() {
        return provider;
    }

    @Override
    public ItemStack icon() {
        return BridgeConfig.CURRENCY_ICON.getDefaultInstance();
    }
}
