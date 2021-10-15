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

/**
 * public static utility methods
 * @author antonilol
 */
public class Utils {
	/**
	 * Thrown when a number argument was outside the range
	 * @author antonilol
	 */
	public static class NumberOutOfRangeException extends Exception {
		private static final long serialVersionUID = 1L;
		
		public NumberOutOfRangeException() {
			
		}
	}
	
	/**
	 * Concatenates two {@code byte} arrays
	 * @param a {@code byte} array 1
	 * @param b {@code byte} array 2
	 * @return a {@code byte} array with the elements from {@code a} and {@code b}
	 */
	public static byte[] concat(byte[] a, byte[] b) {
		byte[] c = new byte[a.length + b.length];
		
		int i = 0;
		for (; i < a.length; i++) {
			c[i] = a[i];
		}
		
		for (int j = 0; i < c.length; i++, j++) {
			c[i] = b[j];
		}
		
		return c;
	}
	
	/**
	 * Binary {@code or} on two {@code byte}s
	 * @param a {@code byte} 1
	 * @param b {@code byte} 2
	 * @return {@code byte} 1 {@code or} {@code byte} 2
	 */
	public static byte or(byte a, byte b) {
		return (byte) (a | b);
	}
	
	/**
	 * Pads the input number with zeros from the left
	 * and returns it as a {@link java.lang.String String}
	 * @param n input number
	 * @param length minimum ouput length
	 * @return the padded string
	 */
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

