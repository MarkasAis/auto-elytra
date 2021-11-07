package net.markais.autoelytra;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AutoElytra implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("AutoElytra");

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing AutoElytra!");
		ClientTickEvents.END_CLIENT_TICK.register(e -> { onTick(); });
		AutoElytraCommands.initCommands();
	}

	private static void onTick() {

	}

}
