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

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

import com.antonilol.mc_arduino.Utils.TimeSource;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class Commands {
	public static void register() {
		
		register(true,
			literal("serial").then(
				literal("list")
				.executes(c -> {
					// TODO hover messages and click to (dis)connect
					c.getSource().sendFeedback(new TranslatableText("commands.mc_arduino.serial.list.title"));
					String connected = Comms.getInstance().getPort();
					for (String port : Comms.getSerialPorts()) {
						if (port.equals(connected)) {
							c.getSource().sendFeedback(new TranslatableText("commands.mc_arduino.serial.list.connectedEntry", port).formatted(Formatting.GREEN));
						} else {
							c.getSource().sendFeedback(new TranslatableText("commands.mc_arduino.serial.list.entry", port));
						}
					}
					return Command.SINGLE_SUCCESS;
				})
			).then(
				literal("disconnect")
				.executes(c -> {
					try {
						Comms.getInstance().clearDisplay();
					} catch (NullPointerException e) {
						// happens when comms.serial == null
					}
					String connected = Comms.getInstance().getPort();
					if (Comms.getInstance().disconnect()) {
						c.getSource().sendFeedback(new TranslatableText("commands.mc_arduino.serial.disconnect.success", connected));
					} else {
						TranslatableText t;
						if (connected == null) {
							t = new TranslatableText("commands.mc_arduino.serial.disconnect.failUnknownPort");
						} else {
							t = new TranslatableText("commands.mc_arduino.serial.disconnect.fail", connected);
						}
						c.getSource().sendFeedback(t);
					}
					return Command.SINGLE_SUCCESS;
				})
			).then(
				literal("connect").then(
					argument("port", new SerialPortArgumentType())
					.suggests(new SerialPortArgumentType())
					.executes(c -> {
						String port = SerialPortArgumentType.getString(c, "port");
						if (port != null) {
							if (Comms.getInstance().connect(port)) {
								c.getSource().sendFeedback(new TranslatableText("commands.mc_arduino.serial.connect.success", port));
								return Command.SINGLE_SUCCESS;
							}
						}
						final TranslatableText t;
						if (port == null) {
							t = new TranslatableText("commands.mc_arduino.serial.connect.failUnknownPort");
						} else {
							t = new TranslatableText("commands.mc_arduino.serial.connect.fail", port);
						}
						c.getSource().sendFeedback(t);
						return Command.SINGLE_SUCCESS;
					})
				)
			)
		);
		
		register(false,
			literal("display").then(
				literal("setTimeSource").then(
					literal("minecraft")
					.executes(c -> {
						Main.timeSource = TimeSource.MINECRAFT;
						return Command.SINGLE_SUCCESS;
					})
				).then(
					literal("real_life")
					.executes(c -> {
						Main.timeSource = TimeSource.REAL_LIFE;
						return Command.SINGLE_SUCCESS;
					})
				)
			)
		);

		register(false,
			literal("version")
			.executes(c -> {
				c.getSource().sendFeedback(new TranslatableText("commands.mc_arduino.version.line1", Main.VERSION));
				c.getSource().sendFeedback(new TranslatableText("commands.mc_arduino.version.line2")); // TODO is it possible to add a clickable link here?
				return Command.SINGLE_SUCCESS;
			})
		);
		
		ClientCommandManager.DISPATCHER.register(mainNode);
	}
	
	private static LiteralArgumentBuilder<FabricClientCommandSource> mainNode = literal("mc_arduino");
	
	private static void register(boolean separate, LiteralArgumentBuilder<FabricClientCommandSource> node) {
		mainNode = mainNode.then(node);
		if (separate) {
			ClientCommandManager.DISPATCHER.register(node);
		}
	}
}

