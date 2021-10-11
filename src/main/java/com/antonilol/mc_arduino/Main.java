/*
 * Copyright (c) 2021 Antoni Spaanderman
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.antonilol.mc_arduino;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.StartTick;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class Main implements ClientModInitializer, StartTick {
	
	public static final String MOD_ID = "mc_arduino"; // is this needed? i saw other people do it in tutorials
	public static final String VERSION = "1.0.1"; // updated by updateVersion script with sed :)
	private Time prevTime = null;
	private XP prevXP = null;
	private boolean cleared = true;
	
	private Comms comms = new Comms();
	
	private void onTimeUpdate(Time time) {
		comms.updateDisplay(comms.timeToDisplay(time));
		cleared = false;
	}
	
	private void onXPUpdate(XP xp) {
		
		// TODO xp bar for ledstrip
	}
	
	@Override
	public void onInitializeClient() {
		
		Commands.register(comms);
		
		ClientTickEvents.START_CLIENT_TICK.register(this);
	}

	@Override
	public void onStartTick(MinecraftClient client) {
		ClientPlayerEntity player = client.player;

		if (player != null) {
			final XP xp = XP.fromPlayer(player);
			final Time time = Time.fromMinecraftTime(player.clientWorld.getTimeOfDay());
			
			if (!time.equalsIgnoreSeconds(prevTime)) {
				onTimeUpdate(time);
				prevTime = time;
			}

			if (!xp.equals(prevXP)) {
				onXPUpdate(xp);
				prevXP = xp;
			}
		} else {
			if (!cleared) {
				comms.clearDisplay();
				cleared = true;
			}
		}
	}
}

