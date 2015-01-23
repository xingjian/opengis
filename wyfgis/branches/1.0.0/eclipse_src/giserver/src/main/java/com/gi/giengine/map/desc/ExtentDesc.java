package com.gi.giengine.map.desc;

public class ExtentDesc {
	private double xmin;
	private double xmax;
	private double ymin;
	private double ymax;

	public double getXmin() {
		return xmin;
	}

	public void setXmin(double xmin) {
		this.xmin = xmin;
	}

	public double getXmax() {
		return xmax;
	}

	public void setXmax(double xmax) {
		this.xmax = xmax;
	}

	public double getYmin() {
		return ymin;
	}

	public void setYmin(double ymin) {
		this.ymin = ymin;
	}

	public double getYmax() {
		return ymax;
	}

	public void setYmax(double ymax) {
		this.ymax = ymax;
	}
	
	//--------------------------------------------------
	

	public ExtentDesc(double xmin, double xmax, double ymin, double ymax) {
		super();
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
	}	
	
	public ExtentDesc clone(){
		return new ExtentDesc(xmin, xmax, ymin, ymax);
	}

}
