package com.homevision.test.image.checkboxdetection.model;

public class Checkbox {


    private String ticked;
    private String label;

    public Checkbox(String ticked, String label) {
        this.ticked = ticked;
        this.label = label;
    }

    public String getTicked() {
        return ticked;
    }

    public void setTicked(String ticked) {
        this.ticked = ticked;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Checkbox{" +
                "ticked='" + ticked + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
