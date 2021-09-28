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
