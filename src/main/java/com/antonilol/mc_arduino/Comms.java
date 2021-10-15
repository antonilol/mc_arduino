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
	/**
	 * 7 bit numbers, the 7 bits represent the
	 * segments on the display (0b0GFEDCBA)
	 */
	public static final byte[] numbers = {63, 6, 91, 79, 102, 109, 125, 7, 127, 111};
	
	/**
	 * Decimal point/separator (.)
	 */
	public static final byte DP = (byte) 0x80;
	
	/**
	 * Minus/dash (-)
	 */
	public static final byte MINUS = 0x40;
	
	/**
	 * Sent <b>to</b> the arduino to indicate that the next
	 * 4 bytes (1 per display) are for the display
	 */
	public static final byte _7SEG = 0;
	
	/**
	 * Sent <b>to</b> the arduino to indicate that the next
	 * 4 bytes (1: the led, 2: r value, 3: g value,
	 * 4: b value) are for the led strip
	 */
	public static final byte LEDSTRIP = 1;
	
	/**
	 * Sent <b>to</b> the arduino to indicate that the led
	 * strip has to be cleared
	 */
	public static final byte CLEARLEDSTRIP = 2;
	
	/**
	 * Sent <b>by</b> the arduino to indicate that it has
	 * been succesfully connected and data can be
	 * sent to it
	 */
	public static final byte CONNECTED = 3;
	
	
	private Serial serial = null;
	private String connectedPort = null;
	private boolean connected = false;
	
	private static Comms instance;
	
	public Comms() {
		instance = this;
	}
	
	public void onMessage(byte[] msg) {
		// TODO same message receiver as on the arduino
		if (!connected && msg[0] == CONNECTED) {
			connected = true;
		}
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
			e.printStackTrace();
			return false;
		}
		connectedPort = port;
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
			connected = false;
			return true;
		}
		return false;
	}

	public boolean connected() {
		return connected && connectedPort != null;
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
		message = Utils.concat(message, disp);
		serial.write(message);
		return true;
	}

	public static byte[] timeToDisplay(Time t) {
		byte[] d = {numbers[t.getH() / 10], Utils.or(numbers[t.getH() % 10], DP), numbers[t.getM() / 10], numbers[t.getM() % 10]};
		return d;
	}

	public static byte[] intToDisplay(int n) throws NumberOutOfRangeException {
		if (n < -999 || n > 9999) {
			throw new NumberOutOfRangeException();
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
	
	public boolean clearDisplay() {
		byte[] d = {0, 0, 0, 0};
		return updateDisplay(d);
	}

	/**
	 * Updates the LED strip
	 * <br>
	 * TODO physically connect led strip to my arduino
	 * <br>
	 * this method is unused for now
	 * @param led the LED
	 * @param r r value
	 * @param g g value
	 * @param b b value
	 * @return {@code true} if the arduino is connected
	 * and the message was succesfully sent
	 */
	public boolean updateLedStrip(byte led, byte r, byte g, byte b) {
		if (!connected()) {
			return false;
		}
		byte[] message = {LEDSTRIP, led, r, g, b};
		serial.write(message);
		return true;
	}
	
	/**
	 * Clears the LED strip
	 * <br>
	 * TODO physically connect led strip to my arduino
	 * <br>
	 * this method is unused for now
	 * @return {@code true} if the arduino is connected
	 * and the message was succesfully sent
	 */
	public boolean clearLedStrip() {
		if (!connected()) {
			return false;
		}
		byte[] message = {CLEARLEDSTRIP};
		return serial.write(message);
	}
	
	/**
	 * Gets the instance of {@link Comms}
	 * @return the instance of {@link Comms}
	 */
	public static Comms getInstance() {
		return instance;
	}
}

