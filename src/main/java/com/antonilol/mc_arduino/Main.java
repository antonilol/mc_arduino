package com.antonilol.mc_arduino;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;

public class Main implements ClientModInitializer {
	
	public static final String MOD_ID = "mc_arduino"; // is this needed? i saw other people do it in tutorials
	public static final String VERSION = "1.0.0"; // updated by updateVersion script with sed :)
	private Time prevTime = null;
	private XP prevXP = null;
	private boolean cleared = true;
	
	private Comms comms = new Comms();
	
	@Override
	public void onInitializeClient() {
		
		new Commands(comms);
		
		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			ClientPlayerEntity player = client.player;

			if (player != null) {
				
				final XP xp = XP.fromPlayer(player);
				final Time time = Time.fromMinecraftTime(player.clientWorld.getTimeOfDay());
				
				if (!time.equalsIgnoreSeconds(prevTime)) {
					comms.updateDisplay(comms.timeToDisplay(time));
					cleared = false;
				}
				
				prevTime = time;
				
				if (!xp.equals(prevXP)) {
					prevXP = xp;
					// TODO xp bar for ledstrip
				}
			} else {
				if (!cleared) {
					comms.clearDisplay();
					cleared = true;
				}
			}
		});
	}
}

// vim: set ts=8 sw=8 tw=0 noet :
