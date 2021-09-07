package com.antonilol.mc_arduino;

import net.minecraft.entity.player.PlayerEntity;

public class XP {
	
	private final int level;
	private final float progress;
	private final long points;
	
	public XP(int level) {
		this.level = level;
		if (level >= 32) {
			points = (long) (4.5 * level * level - 162.5 * level + 2220);
		} else if (level >= 17) {
			points = (long) (2.5 * level * level -	40.5 * level + 360);
		} else {
			points = (level * level + 6 * level);
		}
		progress = 0;
	}
	
	public XP(int level, float progress) {
		long t = new XP(level).getPoints();
		this.level = 0;
		points = (long) (((new XP(level + 1).getPoints()) - t) * (double) progress + t);
		this.progress = progress;
	}
	
	//public XP(long points) {
	//	TODO this constructor
	//	this.points = points;
	//}
	
	public static XP fromPlayer(PlayerEntity player) {
		return new XP(player.experienceLevel, player.experienceProgress);
	}
	
	public boolean equals(XP other) {
		return other == null ? false : points == other.getPoints();
	}

	public int getLevel() {
		return level;
	}

	public float getProgress() {
		return progress;
	}

	public long getPoints() {
		return points;
	}
}
