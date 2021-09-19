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
}

// vim: set ts=8 sw=8 tw=0 noet :
