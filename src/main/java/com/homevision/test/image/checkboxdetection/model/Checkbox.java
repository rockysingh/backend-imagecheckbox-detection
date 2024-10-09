package com.homevision.test.image.checkboxdetection.model;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class Checkbox {
    Rect rect;
    Mat pixelMatrix;

    Checkbox(Rect rect, Mat pixelMatrix) {
        this.rect = rect;
        this.pixelMatrix = pixelMatrix;
    }
}
