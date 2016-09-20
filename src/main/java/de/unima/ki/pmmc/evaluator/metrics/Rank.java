package de.unima.ki.pmmc.evaluator.metrics;

import de.unima.ki.pmmc.evaluator.alignment.Correspondence;

public class Rank implements Comparable<Rank>{

	private Correspondence c;
	private double rank;
	private double normalizedRank;
	
	public Rank(Correspondence c) {
		this.c = c;
	}
	
	public Rank(Correspondence c, double rank) {
		this.c = c;
		this.rank = rank;
	}
	
	public Correspondence getC() {
		return c;
	}
	public void setC(Correspondence c) {
		this.c = c;
	}
	public double getRank() {
		return rank;
	}
	public void setRank(double rank) {
		this.rank = rank;
	}

	@Override
	public int compareTo(Rank o) {
		if(this.c.getConfidence() < o.getC().getConfidence()) {
			return -1;
		} else if(this.c.getConfidence() > o.getC().getConfidence()){
			return 1;
		}
		return 0;
	}

	@Override
	public boolean equals(Object rank) {
		if (rank instanceof Rank) {
			Rank that = (Rank)rank;
			return this.getC().getUri1().equals(that.getC().getUri1()) && this.getC().getUri2().equals(that.getC().getUri2());
		}
		else {
			return false;
		}
		
	}

	public double getNormalizedRank() {
		return normalizedRank;
	}

	public void setNormalizedRank(double normalizedRank) {
		this.normalizedRank = normalizedRank;
	}

	@Override
	public String toString() {
		return "Rank [c=" + c + ", rank=" + rank + ", normalizedRank="
				+ normalizedRank + "]";
	}

	
	
	
	
}
