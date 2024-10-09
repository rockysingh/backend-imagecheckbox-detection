package com.homevision.test.image.checkboxdetection.service;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ImageCheckboxService {

    public static Mat convertToGrayscale(Mat image) {
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        return grayImage;
    }

    public static Mat convertToGaussianAndThreshold(Mat grayImage) {
        Mat blur = new Mat();
        Imgproc.GaussianBlur(grayImage, blur, new Size(3, 3), 0);
        Mat thresh = new Mat();
        Imgproc.threshold(blur, thresh, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
        return thresh;
    }

    public static Mat findContours(Mat thresh, List<MatOfPoint> cnts) {
        Mat hierarchy = new Mat();
        Imgproc.findContours(thresh, cnts, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        final int AREA_THRESHOLD = 3000;
        for (MatOfPoint c : cnts) {
            double area = Imgproc.contourArea(c);
            if (area < AREA_THRESHOLD) {
                Imgproc.drawContours(thresh, List.of(c), -1, new Scalar(0), -1);
            }
        }
        return hierarchy;
    }

    public static void repairVerticalAndHorizontalWalls(Mat thresh, Mat repair) {
        Mat repairKernel1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 1));
        Imgproc.morphologyEx(thresh, repair, Imgproc.MORPH_CLOSE, repairKernel1, new Point(-1, -1), 1);
        Mat repairKernel2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 5));
        Imgproc.morphologyEx(repair, repair, Imgproc.MORPH_CLOSE, repairKernel2, new Point(-1, -1), 1);
        Mat repairKernel3 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 1));
        Imgproc.morphologyEx(repair, repair, Imgproc.MORPH_CLOSE, repairKernel3, new Point(-1, -1), 1);
        Mat repairKernel4 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2));
        Imgproc.morphologyEx(repair, repair, Imgproc.MORPH_CLOSE, repairKernel4, new Point(-1, -1), 1);
        Mat repairKernel5 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.morphologyEx(repair, repair, Imgproc.MORPH_CLOSE, repairKernel5, new Point(-1, -1), 1);
    }

    public static List<MatOfPoint> detectCheckBox(List<MatOfPoint> cnts, Mat repair, Mat hierarchy, Mat original){
        List<MatOfPoint> checkboxContours = new ArrayList<>();
        cnts.clear();
        Imgproc.findContours(repair, cnts, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        for (MatOfPoint c : cnts) {
            double peri = Imgproc.arcLength(new MatOfPoint2f(c.toArray()), true);
            MatOfPoint2f approx = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(c.toArray()), approx, 0.035 * peri, true);
            Rect boundingRect = Imgproc.boundingRect(approx);
            double aspectRatio = (double) boundingRect.width / boundingRect.height;
            if (approx.total() == 4 && (aspectRatio >= 0.8 && aspectRatio <= 2.0)) {
                Imgproc.rectangle(original, boundingRect.tl(), boundingRect.br(), new Scalar(36, 255, 12), 3);
                checkboxContours.add(c);
            }
        }
        return checkboxContours;
    }
}
