package net.markais.autoelytra;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class AutoElytra implements ModInitializer {


	@Override
	public void onInitialize() {
		Utils.LOGGER.info("Initializing AutoElytra!");
		ClientTickEvents.END_CLIENT_TICK.register(e -> { onTick(); });
		ChatCommands.initCommands();

		Utils.LOGGER.info(AutoFlyConfig.getInstance().getLandingMode());
		AutoFlyConfig.getInstance().setLandingMode(InteractionMode.NEVER);

//		AutoElytraConfig config = AutoElytraConfig.load();
//
//		LOGGER.info(config.number);
//
//		config.number = 69;
//
//		try {
//			config.save();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	private static void onTick() {

	}

}
