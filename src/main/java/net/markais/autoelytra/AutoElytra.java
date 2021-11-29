package net.markais.autoelytra;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class AutoElytra implements ModInitializer {

	private boolean isConnected = false;

	@Override
	public void onInitialize() {
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> { onConnect(); });
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> { onDisconnect(); });
		ClientTickEvents.END_CLIENT_TICK.register(e -> { onUpdate(); });
		ChatCommands.initCommands();
		Utils.LOGGER.info("AutoElytra initialized!");
	}

	private void onConnect() {
		PlayerController.connect();
		isConnected = true;
		Utils.LOGGER.info("Connected");
	}

	private void onDisconnect() {
		isConnected = false;
		Utils.LOGGER.info("Disconnected");
	}

	private void onUpdate() {
		if (!isConnected) return;
		PlayerController.update();
		FlyManager.getInstance().update();
	}

}
