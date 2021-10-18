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

import java.util.TimeZone;

import com.antonilol.mc_arduino.Utils.NumberOutOfRangeException;

public class Time {
	public static Time fromMillis(long millis) {
		final int t_h = (int) (millis / 3600_000 % 24);
		final int t_m = (int) (millis /   60_000 % 60);
		final int t_s = (int) (millis /    1_000 % 60);
		
		try {
			return new Time(t_h, t_m, t_s);
		} catch (NumberOutOfRangeException e) {
			// impossible
			return new Time();
		}
	}
	public static Time fromMinecraftTime(long minecraftTime) {
		final long s_total = ((minecraftTime + 6000L) % 24000L) * 36;
		final int t_h = (int) (s_total / 36_000     );
		final int t_m = (int) (s_total /    600 % 60);
		final int t_s = (int) (s_total          % 60);
		
		try {
			return new Time(t_h, t_m, t_s);
		} catch (NumberOutOfRangeException e) {
			// impossible
			return new Time();
		}
	}
	
	public static Time now() {
		long t = System.currentTimeMillis();
		long off = TimeZone.getDefault().getOffset(t);
		return fromMillis(t + off);
	}

	private int h, m, s;
	
	public Time() {
		h = 0;
		m = 0;
		s = 0;
	}
	
	public Time(int h, int m, int s) throws NumberOutOfRangeException {
		setH(h);
		setM(m);
		setS(s);
	}
	
	public boolean equals(Time other) {
		return equalsIgnoreSeconds(other) && s == other.getS();
	}

	public boolean equalsIgnoreSeconds(Time other) {
		return other == null ? false : h == other.h && m == other.m;
	}
	
	public String format(boolean seconds, boolean h12) {
		StringBuilder sb = new StringBuilder();
		if (h12) {
			sb.append(h % 12 == 0 ? 12 : h % 12);
		} else {
			sb.append(h);
		}
		sb.append(":");
		sb.append(Utils.pad(m, 2));
		if (seconds) {
			sb.append(":");
			sb.append(Utils.pad(s, 2));
		}
		if (h12) {
			if (h < 12) {
				sb.append(" AM");
			} else {
				sb.append(" PM");
			}
		}
		
		return sb.toString();
	}
	
	public int getH() {
		return h;
	}
	
	public int getM() {
		return m;
	}
	
	public int getS() {
		return s;
	}
	
	public void setH(int h) throws NumberOutOfRangeException {
		if (h < 0 || h > 23) {
			throw new NumberOutOfRangeException();
		}
		this.h = h;
	}
	
	public void setM(int m) throws NumberOutOfRangeException {
		if (m < 0 || m > 59) {
			throw new NumberOutOfRangeException();
		}
		this.m = m;
	}

	public void setS(int s) throws NumberOutOfRangeException {
		if (s < 0 || s > 59) {
			throw new NumberOutOfRangeException();
		}
		this.s = s;
	}
}
