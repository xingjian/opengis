package com.gi.giserver.core.service.mapservice.desc;

public class TileLodDesc
{
  private int level;
  private double resolution;
  private double scale;

  public int getLevel()
  {
    return this.level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public double getResolution() {
    return this.resolution;
  }

  public void setResolution(double resolution) {
    this.resolution = resolution;
  }

  public double getScale() {
    return this.scale;
  }

  public void setScale(double scale) {
    this.scale = scale;
  }
}