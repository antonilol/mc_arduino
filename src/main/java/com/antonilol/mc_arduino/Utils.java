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

