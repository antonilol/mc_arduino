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
	
	/**
	 * The version of this mod
	 * <br>
	 * Updated by <a href="https://github.com/antonilol/mc_arduino/blob/master/updateVersion">updateVersion</a>
	 */
	public static final String VERSION = "1.0.2";
	
	private Time prevTime = null;
	private XP prevXP = null;
	private boolean cleared = true;
	
	private boolean onTimeUpdate(Time time) {
		boolean status = Comms.getInstance().updateDisplay(Comms.timeToDisplay(time));
		cleared = !status;
		return status;
	}
	
	private boolean onXPUpdate(XP xp) {
		
		// TODO xp bar for led strip
		
		return true;
	}
	
	@Override
	public void onInitializeClient() {
		new Comms();
		
		Commands.register();
		
		ClientTickEvents.START_CLIENT_TICK.register(this);
	}

	@Override
	public void onStartTick(MinecraftClient client) {
		ClientPlayerEntity player = client.player;

		if (player != null) {
			
			final Time time = Time.fromMinecraftTime(player.clientWorld.getTimeOfDay());
			
			if (!time.equalsIgnoreSeconds(prevTime)) {
				if (onTimeUpdate(time)) {
					prevTime = time;
				}
			}
			
			final XP xp = XP.fromPlayer(player);

			if (!xp.equals(prevXP)) {
				if (onXPUpdate(xp)) {
					prevXP = xp;
				}
			}
			
		} else if (!cleared) {
			cleared = Comms.getInstance().clearDisplay();
		}
		
	}
}

