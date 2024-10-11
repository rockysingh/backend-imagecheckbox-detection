package com.homevision.test.image.checkboxdetection.model;

import org.opencv.core.Rect;

public class CheckboxResult {
    public String label;
    public boolean isChecked;
    public Rect location;

    public CheckboxResult(String label, boolean isChecked, Rect location) {
        this.label = label;
        this.isChecked = isChecked;
        this.location = location;
    }

    @Override
    public String toString() {
        return "Checkbox: " + label + ", Checked: " + isChecked + ", Location: " + location;
    }
}