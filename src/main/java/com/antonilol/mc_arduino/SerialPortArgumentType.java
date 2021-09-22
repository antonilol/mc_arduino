package com.antonilol.mc_arduino;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

public class SerialPortArgumentType implements ArgumentType<String>, SuggestionProvider<FabricClientCommandSource> {
	
    private static Collection<String> EXAMPLES = new ArrayList<String>();
    static {
        EXAMPLES.add("/dev/ttyUSB5"); // linux
        EXAMPLES.add("COM3"); // win
        EXAMPLES.add("/dev/tty.usbmodem123"); // mac
    }
 
    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
	
    // public SerialPortArgumentType() { }

    public static String getString(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }
    
    private static boolean isAllowed(final char c) {
        return c >= '0' && c <= '9'
            || c >= 'A' && c <= 'Z'
            || c >= 'a' && c <= 'z'
            || c == '_' || c == '-'
            || c == '.' || c == '+'
            || c == '/';
    }

    @Override
    public String parse(final StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        while (reader.canRead() && isAllowed(reader.peek())) {
            reader.skip();
        }
        return reader.getString().substring(start, reader.getCursor());
    }

	@Override
	public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
		for (String port : Comms.getSerialPorts()) {
			builder.suggest(port);
		}

		return builder.buildFuture();
	}
}