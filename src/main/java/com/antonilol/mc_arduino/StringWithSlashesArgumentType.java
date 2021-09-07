package com.antonilol.mc_arduino;

import java.util.ArrayList;
import java.util.Collection;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class StringWithSlashesArgumentType implements ArgumentType<String> {
	
    private static Collection<String> EXAMPLES = new ArrayList<String>();
    static {
        EXAMPLES.add("word");
        EXAMPLES.add("words_with_underscores");
        EXAMPLES.add("/and/slashes");
    }
 
    @Override
    public Collection<String> getExamples() { // Brigadier has support to show examples for what the argument should look like, this should contain a Collection of only the argument this type will return. This is mainly used to calculate ambiguous commands which share the exact same 
        return EXAMPLES;
    }
	
    private StringWithSlashesArgumentType() {
    }

    public static StringWithSlashesArgumentType string() {
        return new StringWithSlashesArgumentType();
    }

    public static String getString(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }
    
    private static boolean isAllowedInSingleWordWithSlashes(final char c) {
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
        while (reader.canRead() && isAllowedInSingleWordWithSlashes(reader.peek())) {
            reader.skip();
        }
        return reader.getString().substring(start, reader.getCursor());
    }
/*
    @Override
    public String toString() {
        return "string()";
    }

    public static String escapeIfRequired(final String input) {
        for (final char c : input.toCharArray()) {
            if (!isAllowedInSingleWordWithSlashes(c)) {
                return escape(input);
            }
        }
        return input;
    }

    private static String escape(final String input) {
        final StringBuilder result = new StringBuilder("\"");

        for (int i = 0; i < input.length(); i++) {
            final char c = input.charAt(i);
            if (c == '\\' || c == '"') {
                result.append('\\');
            }
            result.append(c);
        }

        result.append("\"");
        return result.toString();
    }*/
}