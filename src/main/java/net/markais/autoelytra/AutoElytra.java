package net.markais.autoelytra;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class AutoElytra implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("AutoElytra");

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing AutoElytra!");
		ClientTickEvents.END_CLIENT_TICK.register(e -> { onTick(); });
		AutoElytraCommands.initCommands();

		LOGGER.info(AutoFlyConfig.getInstance().getTest());
		AutoFlyConfig.getInstance().setTest(1337);

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
