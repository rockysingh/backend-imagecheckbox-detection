package com.homevision.test.image.checkboxdetection.model;

public class PipelinesConfig {
    private int[] widthRange;
    private int[] heightRange;
    private double[] scalingFactors;
    private double[] whRatioRange;

    public int[] getWidthRange() {
        return widthRange;
    }

    public void setWidthRange(int[] widthRange) {
        this.widthRange = widthRange;
    }

    public int[] getHeightRange() {
        return heightRange;
    }

    public void setHeightRange(int[] heightRange) {
        this.heightRange = heightRange;
    }

    public double[] getScalingFactors() {
        return scalingFactors;
    }

    public void setScalingFactors(double[] scalingFactors) {
        this.scalingFactors = scalingFactors;
    }

    public double[] getWhRatioRange() {
        return whRatioRange;
    }

    public void setWhRatioRange(double[] whRatioRange) {
        this.whRatioRange = whRatioRange;
    }

}