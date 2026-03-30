package savage.ecobalbridge.integration;

import com.mojang.authlib.GameProfile;
import eu.pb4.common.economy.api.EconomyAccount;
import eu.pb4.common.economy.api.EconomyCurrency;
import eu.pb4.common.economy.api.EconomyProvider;
import eu.pb4.common.economy.api.EconomyTransaction;
import me.andy.ecobal.api.EconomyManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import savage.ecobalbridge.BridgeConfig;

import java.math.BigInteger;
import java.util.UUID;

/**
 * Common Economy API representation of an individual player's EcoBal account.
 * <p>
 * Bridges balance management calls (deposit, withdraw, set) to the {@link EconomyManager}.
 */
public class EcobalEconomyAccount implements EconomyAccount {
    private final GameProfile profile;
    private final EconomyCurrency currency;
    private final EconomyProvider provider;

    public EcobalEconomyAccount(GameProfile profile, EconomyCurrency currency, EconomyProvider provider) {
        this.profile = profile;
        this.currency = currency;
        this.provider = provider;
    }

    @Override
    public Component name() {
        return Component.literal(profile.name());
    }

    @Override
    public UUID owner() {
        return profile.id();
    }

    @Override
    public Identifier id() {
        return Identifier.fromNamespaceAndPath(BridgeConfig.NAMESPACE, profile.id().toString());
    }

    @Override
    public EconomyCurrency currency() {
        return currency;
    }

    @Override
    public EconomyProvider provider() {
        return provider;
    }

    /**
     * @return The player's current balance, scaled up to a BigInteger (e.g., $1.50 becomes 150).
     */
    @Override
    public BigInteger balance() {
        double balance = EconomyManager.getPlayerBalance(owner());
        return BigInteger.valueOf((long) (balance * BridgeConfig.SCALE));
    }

    @Override
    public void setBalance(BigInteger value) {
        double balance = value.doubleValue() / (double) BridgeConfig.SCALE;
        EconomyManager.setBalance(owner(), balance);
    }

    @Override
    public EconomyTransaction increaseBalance(BigInteger value) {
        double amount = value.doubleValue() / (double) BridgeConfig.SCALE;
        EconomyManager.silentDeposit(owner(), amount);
        BigInteger next = balance();
        return new EconomyTransaction.Simple(true, Component.literal("Success"), next, next.subtract(value), value, this);
    }

    @Override
    public EconomyTransaction decreaseBalance(BigInteger value) {
        double amount = value.doubleValue() / (double) BridgeConfig.SCALE;
        boolean success = EconomyManager.silentWithdraw(owner(), amount);
        BigInteger current = balance();
        if (success) {
            return new EconomyTransaction.Simple(true, Component.literal("Success"), current, current.add(value), value.negate(), this);
        }
        return new EconomyTransaction.Simple(false, Component.literal("Insufficient funds"), current, current, BigInteger.ZERO, this);
    }

    @Override
    public EconomyTransaction canDecreaseBalance(BigInteger value) {
        double current = EconomyManager.getPlayerBalance(owner());
        double amount = value.doubleValue() / (double) BridgeConfig.SCALE;
        BigInteger currentBI = BigInteger.valueOf((long) (current * BridgeConfig.SCALE));

        if (current >= amount) {
            return new EconomyTransaction.Simple(true, Component.literal("Success"), 
                BigInteger.valueOf((long) ((current - amount) * BridgeConfig.SCALE)), 
                currentBI, value.negate(), this);
        }
        return new EconomyTransaction.Simple(false, Component.literal("Insufficient funds"), currentBI, currentBI, BigInteger.ZERO, this);
    }

    @Override
    public EconomyTransaction canIncreaseBalance(BigInteger value) {
        BigInteger current = balance();
        return new EconomyTransaction.Simple(true, Component.literal("Success"), current.add(value), current, value, this);
    }
}
