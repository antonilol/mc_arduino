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

import java.io.IOException;

import com.antonilol.mc_arduino.Utils.NumberOutOfRangeException;

public class Comms {
	// 7 bit numbers, the 7 bits represent the segments on the display (A=1, ..., G=64)
	public static final byte[] numbers = {63, 6, 91, 79, 102, 109, 125, 7, 127, 111};
	
	public static final byte DP = (byte) 0x80;
	public static final byte	MINUS = 0x40;
	
	// TODO more letters
	// move to new class?
	public static final byte	L = 0x38;
	public static final byte	O = 0x3f;

	
	public static final byte	_7SEG = 0;
	public static final byte	LEDSTRIP = 1;
	
	
	private Serial serial = null;
	private String connectedPort = null;
	
	public Comms() {
	}

	public boolean connect(String port) {
		if (connected()) {
			if (!disconnect()) {
				return false;
			}
		}
		try {
			serial = new Serial(port);
		} catch (Exception e) {
			return false;
		}
		connectedPort = port;
		//displayUpdateNeeded = true;
		return true;
	}

	public boolean disconnect() {
		if (connected()) {
			try {
				serial.dispose();
				serial = null;
			} catch (IOException e) {
				return false;
			}
			connectedPort = null;
			return true;
		}
		return false;
	}

	public boolean connected() {
		return connectedPort != null;
	}

	public static String[] getSerialPorts() {
		return SerialPortList.getPortNames();
	}

	public String getPort() {
		return connectedPort;
	}

	public boolean updateDisplay(byte[] disp) {
		if (!connected()) {
			return false;
		}
		byte[] message = {_7SEG};
		try {
			message = Utils.concat(message, disp);
		} catch (IOException e) {
			return false;
		}
		serial.write(message);
		return true;
	}

	public byte[] timeToDisplay(Time t) {
		byte[] d = {numbers[t.getH() / 10], Utils.or(numbers[t.getH() % 10], DP), numbers[t.getM() / 10], numbers[t.getM() % 10]};
		return d;
	}

	public byte[] intToDisplay(int n) throws Utils.NumberOutOfRangeException {
		if (n < -999 || n > 9999) {
			throw new Utils.NumberOutOfRangeException();
		}
		String v = Integer.toString(n);
		byte[] d = {0, 0, 0, 0};

		for (int i=0;i < v.length(); i++) {
			char c = v.charAt(i);
			if (c == '-') {
				d[4-v.length()+i] = MINUS;
			} else if (c >= '0' && c <= '9') {
				d[4-v.length()+i] = numbers[c - '0'];
			}
		}

		return d;
	}
	
	public void clearDisplay() {
		byte[] d = {0, 0, 0, 0};
		updateDisplay(d);
	}

	// TODO physically connect ledstrip to my arduino
	// this methods are unused now
	public boolean updateLedStrip(byte led, byte r, byte g, byte b) throws NumberOutOfRangeException {
		if (led == 0xff) {
			throw new Utils.NumberOutOfRangeException();
		}
		if (!connected()) {
			return false;
		}
		byte[] message = {LEDSTRIP, led, r, g, b};
		serial.write(message);
		return true;
	}
	
	public boolean clearLedStrip() {
		if (!connected()) {
			return false;
		}
		byte[] message = {LEDSTRIP, -1, 0, 0, 0};
		serial.write(message);
		return true;
	}
}

