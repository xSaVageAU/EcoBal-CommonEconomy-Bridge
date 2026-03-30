package savage.ecobalbridge;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import eu.pb4.common.economy.api.CommonEconomy;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import savage.ecobalbridge.integration.EcobalCommonEconomyProvider;

public class EcobalCommoneconomyBridge implements ModInitializer {
	public static final String MOD_ID = "ecobal-commoneconomy-bridge";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/**
	 * Captured server instance for cross-thread access during economy operations.
	 */
	public static MinecraftServer server;

	@Override
	public void onInitialize() {
		// Register the provider using the centralized namespace.
		CommonEconomy.register(BridgeConfig.NAMESPACE, EcobalCommonEconomyProvider.INSTANCE);
		
		ServerLifecycleEvents.SERVER_STARTING.register(s -> server = s);
		ServerLifecycleEvents.SERVER_STOPPING.register(s -> server = null);

		LOGGER.info("EcoBal CommonEconomy Bridge initialized!");
	}

	public static void setServer(MinecraftServer server) {
		EcobalCommoneconomyBridge.server = server;
	}

	public static MinecraftServer getServer() {
		return EcobalCommoneconomyBridge.server;
	}
}