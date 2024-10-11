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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Rect getLocation() {
        return location;
    }

    public void setLocation(Rect location) {
        this.location = location;
    }


    @Override
    public String toString() {
        return "Checkbox: " + label + ", Checked: " + isChecked + ", Location: " + location;
    }
}