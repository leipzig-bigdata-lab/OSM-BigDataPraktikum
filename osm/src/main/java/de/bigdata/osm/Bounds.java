package de.bigdata.osm;

public class Bounds {
	
	private double top;
	private double right;
	private double bottom;
	private double left;
	private double centerLonLat;
	
	public Bounds() {
	}

	public double getTop() {
		return top;
	}
	public void setTop(double top) {
		this.top = top;
	}
	
	public double getRight() {
		return right;
	}
	public void setRight(double right) {
		this.right = right;
	}
	
	public double getBottom() {
		return bottom;
	}
	public void setBottom(double bottom) {
		this.bottom = bottom;
	}
	
	public double getLeft() {
		return left;
	}
	public void setLeft(double left) {
		this.left = left;
	}

	public double getCenterLonLat() {
		return centerLonLat;
	}
	public void setCenterLonLat(double centerLonLat) {
		this.centerLonLat = centerLonLat;
	}
}