package com.antonilol.mc_arduino;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class Commands {
	public static void register(Comms comms) {
		
		LiteralArgumentBuilder<FabricClientCommandSource> serial =

		literal("serial").then(
			literal("list").executes(c -> {
				// TODO hover messages and click to (dis)connect
				c.getSource().sendFeedback(new TranslatableText("commands.mc_arduino.serial.list.title"));
				String connected = comms.getPort();
				for (String port : Comms.getSerialPorts()) {
					if (port.equals(connected)) {
					c.getSource().sendFeedback(new TranslatableText("commands.mc_arduino.serial.list.connectedEntry", port).formatted(Formatting.GREEN));
					} else {
					c.getSource().sendFeedback(new TranslatableText("commands.mc_arduino.serial.list.entry", port));
					}
					
				}
				return 1;
			})
		).then(
			literal("disconnect").executes(c -> {
				try {
					comms.clearDisplay();
				} catch (NullPointerException e) {
					// happens when comms.serial == null
				}
				String connected = comms.getPort();
				if (comms.disconnect()) {
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
				return 1;
			})
		).then(
			literal("connect").then(
				argument("port", new SerialPortArgumentType())
				.suggests(new SerialPortArgumentType())
				.executes(c -> {
					String port = SerialPortArgumentType.getString(c, "port");
					if (port != null) {
						if (comms.connect(port)) {
							c.getSource().sendFeedback(new TranslatableText("commands.mc_arduino.serial.connect.success", port));
						return 1;
						}
					}
					final TranslatableText t;
					if (port == null) {
						t = new TranslatableText("commands.mc_arduino.serial.connect.failUnknownPort");
					} else {
						t = new TranslatableText("commands.mc_arduino.serial.connect.fail", port);
					}
					c.getSource().sendFeedback(t);
					return 1;
				})
			)
		);

		LiteralArgumentBuilder<FabricClientCommandSource> version =

		literal("version").executes(c -> {
			c.getSource().sendFeedback(new TranslatableText("commands.mc_arduino.version.line1", Main.VERSION));
			c.getSource().sendFeedback(new TranslatableText("commands.mc_arduino.version.line2")); // TODO is it possible to add a clickable link here?
			return 1;
		});
		
		CommandDispatcher<FabricClientCommandSource> d = ClientCommandManager.DISPATCHER;

		d.register(serial);
		
		d.register(
			literal("mc_arduino")
				.then(serial)
				.then(version)
		);
		
	}
}

// vim: set ts=8 sw=8 tw=0 noet :
