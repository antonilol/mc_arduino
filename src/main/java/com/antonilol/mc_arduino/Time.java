package com.antonilol.mc_arduino;

public class Time {
	private int h, m, s;
	public Time(int h, int m, int s) throws Utils.NumberOutOfRangeException {
		setH(h);
		setM(m);
		setS(s);
	}
	
	public Time() {
		h = 0;
		m = 0;
		s = 0;
	}

	public static Time fromMinecraftTime(long minecraftTime) {
		final long s_total = ((minecraftTime + 6000L) % 24000L) * 36;
		final int t_h = (int) (s_total / 36000);
		final int t_m = (int) ((s_total % 36000) / 600);
		final int t_s = (int) ((s_total %   600) / 10);
		
		try {
			return new Time(t_h, t_m, t_s);
		} catch (Utils.NumberOutOfRangeException e) {
			// impossible
			return new Time();
		}
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
	
	public boolean equals(Time other) {
		return equalsIgnoreSeconds(other) && s == other.getS();
	}
	
	public boolean equalsIgnoreSeconds(Time other) {
		return other == null ? false : h == other.h && m == other.m;
	}

	public int getH() {
		return h;
	}
	
	public void setH(int h) throws Utils.NumberOutOfRangeException {
		if (h < 0 || h > 23) {
			throw new Utils.NumberOutOfRangeException();
		}
		this.h = h;
	}
	
	public int getM() {
		return m;
	}
	
	public void setM(int m) throws Utils.NumberOutOfRangeException {
		if (m < 0 || m > 59) {
			throw new Utils.NumberOutOfRangeException();
		}
		this.m = m;
	}
	
	public int getS() {
		return s;
	}
	
	public void setS(int s) throws Utils.NumberOutOfRangeException {
		if (s < 0 || s > 59) {
			throw new Utils.NumberOutOfRangeException();
		}
		this.s = s;
	}
	
}

// vim: set ts=8 sw=8 tw=0 noet :
