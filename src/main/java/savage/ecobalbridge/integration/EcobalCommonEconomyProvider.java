package savage.ecobalbridge.integration;

import com.mojang.authlib.GameProfile;
import eu.pb4.common.economy.api.EconomyAccount;
import eu.pb4.common.economy.api.EconomyCurrency;
import eu.pb4.common.economy.api.EconomyProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import savage.ecobalbridge.BridgeConfig;

import java.util.Collection;
import java.util.Collections;

/**
 * The main Economy Provider for the EcoBal bridge.
 * <p>
 * Registers as an {@link EconomyProvider} with the Common Economy API and
 * manages currency and account instance mapping.
 */
public class EcobalCommonEconomyProvider implements EconomyProvider {
    /**
     * Singleton instance of the provider.
     */
    public static final EcobalCommonEconomyProvider INSTANCE = new EcobalCommonEconomyProvider();
    private final EcobalEconomyCurrency currency = new EcobalEconomyCurrency(this);

    private EcobalCommonEconomyProvider() {}

    @Override
    public Component name() {
        return Component.literal("EcoBal Economy Bridge");
    }

    @Override
    public ItemStack icon() {
        return BridgeConfig.PROVIDER_ICON.getDefaultInstance();
    }

    @Override
    public Collection<EconomyCurrency> getCurrencies(MinecraftServer server) {
        return Collections.singletonList(currency);
    }

    /**
     * Resolves a currency by its ID. 
     * Supports both the registered namespace ID and a simple "dollar" fallback.
     *
     * @param server The current Minecraft server instance.
     * @param id The currency ID string.
     * @return The EcoBal currency instance if matched, otherwise null.
     */
    @Override
    public EconomyCurrency getCurrency(MinecraftServer server, String id) {
        if (id.equals(EcobalEconomyCurrency.ID.toString()) || id.equalsIgnoreCase(BridgeConfig.CURRENCY_ID)) {
            return currency;
        }
        return null;
    }

    @Override
    public String defaultAccount(MinecraftServer server, GameProfile profile, EconomyCurrency currency) {
        if (currency == this.currency) {
            return profile.id().toString();
        }
        return null;
    }

    @Override
    public Collection<EconomyAccount> getAccounts(MinecraftServer server, GameProfile profile) {
        return Collections.singletonList(new EcobalEconomyAccount(profile, currency, this));
    }

    @Override
    public EconomyAccount getAccount(MinecraftServer server, GameProfile profile, String id) {
        if (id.equals(profile.id().toString())) {
            return new EcobalEconomyAccount(profile, currency, this);
        }
        return null;
    }
}
