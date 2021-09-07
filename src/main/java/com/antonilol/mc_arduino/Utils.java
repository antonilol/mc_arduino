package com.antonilol.mc_arduino;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

// public static utility methods
public class Utils {
	public static byte[] concat(byte[] a, byte[] b) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(a);
		outputStream.write(b);
		byte c[] = outputStream.toByteArray();
		return c;
	}
	
	public static byte or(byte a, byte b) {
		return (byte) (a | b);
	}
	
	public static class NumberOutOfRangeException extends Exception {
		private static final long serialVersionUID = 1L;
		
		public NumberOutOfRangeException() {
			
		}
	}
	
	public static String pad(int n, int length) {
		String inputString = Integer.toString(n);
		final int l = inputString.length();
		
		if (l >= length) {
			return inputString;
		}
		StringBuilder sb = new StringBuilder(length);
		while (sb.length() < length - l) {
			sb.append('0');
		}
		sb.append(inputString);

		return sb.toString();
	}
	
	// minecraft commands do not accept a "/" in command arguments for some reason
	// this method puts "quotes" around it but it can do way more like replacing the 
	//  slashes with underscores (had this before). actually this method can change
	//  the name in any way and still nothing fails as long as it does not return
	//  the same name for different input values.
	// this workaround can be removed if an argument (probably a custom one) type
	//  allows slashes
	
	// DONE, made my own type
	
	/*
	public static String changePortName(String original) {
		return original;//"\"" + original + "\"";
	}
	
	public static String[] changePortNames(String[] original) {
		String[] output = original.clone();
		
		int i = 0;
		for (String port : original) {
			output[i++] = changePortName(port);
		}
		
		return output;
	}
	
	public static String findChangedPortName(String[] ports, String changedPort) {
		for (String port : ports) {
			if (changePortName(port).equals(changedPort)) {
				return port;
			}
		}
		return null;
	}
	*/
}

// vim: set ts=8 sw=8 tw=0 noet :
