package com.homevision.test.image.checkboxdetection;

import com.homevision.test.image.checkboxdetection.model.CheckboxResult;
import com.homevision.test.image.checkboxdetection.model.PipelinesConfig;
import com.homevision.test.image.checkboxdetection.model.Tesseract;
import com.homevision.test.image.checkboxdetection.service.ImageCheckboxService;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CheckboxExtractor {
    static {
        Loader.load(opencv_java.class);
    }

    public static List<CheckboxResult> findCheckBoxes(String path) {
        Mat image = Imgcodecs.imread(path);
        if (image.empty()) {
            System.out.println(path.split("\\\\")[path.split("\\\\").length - 1] + " is not an image file.");
            return new ArrayList<>();
        }

        Mat grayImage = ImageCheckboxService.convertToGrayscale(image);
        int width = grayImage.cols();

        // You can adjust config here for different image setups
        // This is not used.
        PipelinesConfig config = new PipelinesConfig();

        List<MatOfPoint> checkboxes = getCheckboxes(image, grayImage, config);
        System.out.println(checkboxes.size() + " checkboxes found.");

        List<CheckboxResult> checkboxResults = new ArrayList<>();
        for (MatOfPoint checkbox : checkboxes) {
            Rect rect = Imgproc.boundingRect(checkbox);

            // Crop checkbox area for checking if ticked
            Mat checkboxRegion = grayImage.submat(rect);

            // Check if the checkbox is ticked (we assume ticked if there's a dark region inside)
            boolean isChecked = isCheckboxTicked(checkboxRegion);

            // Extract the label near the checkbox (above or to the right)
            String label = extractCheckboxLabel(grayImage, rect, width);

            checkboxResults.add(new CheckboxResult(label, isChecked, rect));
        }

        return checkboxResults;
    }

    public static boolean isCheckboxTicked(Mat checkboxRegion) {
        // Apply a threshold to highlight dark regions (my assumption is tick is a dark inked)
        Mat threshold = new Mat();
        Imgproc.threshold(checkboxRegion, threshold, 100, 255, Imgproc.THRESH_BINARY_INV);

        // I tried look for shapes etc but it was too hard to calculate
        // I went for the simple approach of looking for dark pixels with in the checkbox area.
        // I thought that seemed more logical. However, there is an issue with it as it could be X or Tick
        // I still don't know which one is which. This is a future improvement and something to improve.
        // I Calculate the percentage of dark pixels in the checkbox
        double nonZeroCount = Core.countNonZero(threshold);
        double area = threshold.rows() * threshold.cols();
        double ratio = nonZeroCount / area;

        // Assumption is that the checkbox is ticked if more than 15% of the area is dark
        return ratio > 0.15;
    }

    public static String extractCheckboxLabel(Mat grayImage, Rect rect, int imageWidth) {
        // this could be configured per image (in the future) if we require
        int padding = 10; // Padding between checkbox and label
        int labelHeight = 50; // Increase the height of the region for the label

        // 1st: We look for the label to the right of the checkbox
        int labelXStart = rect.x + rect.width + padding;
        int labelXEnd = Math.min(imageWidth, labelXStart + 300); // Increase label width to 300px
        int labelYStart = rect.y;
        int labelYEnd = rect.y + rect.height + 10;

        // 2nd: If the label is not found or space is too small we try above the checkbox
        if (labelXEnd > imageWidth || labelXEnd - labelXStart < 10) {
            labelXStart = Math.max(0, rect.x - 300); // Increase area to the left by 300px
            labelXEnd = rect.x - padding;
            labelYStart = Math.max(0, rect.y - labelHeight - 10); // Search higher above the checkbox
            labelYEnd = rect.y;
        }

        // This is to define region of interst for label value
        Rect labelRect = new Rect(new Point(labelXStart, labelYStart), new Point(labelXEnd, labelYEnd));
        Mat labelRegion = grayImage.submat(labelRect);

        // TODO: Use Tesseract to extract text from the label region
        String label = Tesseract.imageToString(labelRegion);
        return label.trim();
    }

    public static List<MatOfPoint> getCheckboxes(Mat original, Mat grayImage, PipelinesConfig config) {
        List<MatOfPoint> checkboxes = new ArrayList<>();

        // Apply adaptive threshold to handle different image conditions
        Mat adaptiveThresh = new Mat();
        Imgproc.adaptiveThreshold(grayImage, adaptiveThresh, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);

        // Morphological operations to enhance checkbox shapes (dilate and erode)
        // We needed to this as it wasn't finding all the checkboxes.
        Mat morphed = new Mat();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.morphologyEx(adaptiveThresh, morphed, Imgproc.MORPH_CLOSE, kernel);

        // Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(morphed, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        for (MatOfPoint contour : contours) {
            Rect boundingRect = Imgproc.boundingRect(contour);

            // Filter by aspect ratio and size (checkboxes are square(s) that is my assumption! (sorry brute force))
            double aspectRatio = (double) boundingRect.width / boundingRect.height;
            if (aspectRatio > 0.8 && aspectRatio < 1.2 && boundingRect.area() > 100 && boundingRect.area() < 5000) {
                checkboxes.add(contour);
            }
        }

        return checkboxes;
    }

    // The below method only found one checkbox so had to rewrite it.

//    private static List<MatOfPoint> getCheckboxes(Mat original, Mat grayImage, PipelinesConfig config) {
//        List<MatOfPoint> checkboxes = new ArrayList<>();
//        Mat thresh = ImageCheckboxService.convertToGaussianAndThreshold(grayImage);
//
//        List<MatOfPoint> cnts = new ArrayList<>();
//        Mat hierarchy = ImageCheckboxService.findContours(thresh, cnts);
//
//        Mat repair = new Mat();
//        ImageCheckboxService.repairVerticalAndHorizontalWalls(thresh, repair);
//
//        List<MatOfPoint> checkboxContours = ImageCheckboxService.detectCheckBox(cnts, repair, hierarchy, original);
//        checkboxes.addAll(checkboxContours);
//
//        return checkboxes;
//    }

    public static void main(String[] args) {
        processImageFolder();
    }

    private static void processImageFolder() {
        String rootPath = "src/main/resources/sample";
        int count = 0;
        File directory = new File(rootPath);
        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                count++;
                System.out.println("Handling " + count + " - " + file.getAbsolutePath());
                List<CheckboxResult> checkboxes = findCheckBoxes(file.getPath());
                checkboxes.forEach(System.out::println);
            }
        }
        if (count > 0) {
            System.out.println("TOTAL OF " + count + " files PROCESSED");
        } else {
            System.out.println("No Image file(s) found.");
        }
    }
}