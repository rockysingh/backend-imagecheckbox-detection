package com.homevision.test.image.checkboxdetection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.homevision.test.image.checkboxdetection.model.PipelinesConfig;
import com.homevision.test.image.checkboxdetection.model.Tesseract;
import com.homevision.test.image.checkboxdetection.service.ImageCheckboxService;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

public class CheckboxExtractor {
    static {
        Loader.load(opencv_java.class);
    }

    public static List<String> findCheckBoxes(String path) {
        Mat image = Imgcodecs.imread(path);
        if (image.empty()) {
            System.out.println(path.split("\\\\")[path.split("\\\\").length - 1] + " is not an image file.");
            return new ArrayList<>();
        }

        Mat grayImage = ImageCheckboxService.convertToGrayscale(image);
        int width = grayImage.cols();

        // TODO The above was a way of making it configurable per image to give it different
        // config for different aspects of the image such as weight/height etc for the checkboxes as they could differ per image
        // This was an idea but never got around to implementing it.

        PipelinesConfig config = new PipelinesConfig();
//        config.setWidthRange(new int[] { 15, 35 });
//        config.setHeightRange(new int[] { 15, 40 });
//        config.setScalingFactors(new double[] { 0.7 });
//        config.setWhRatioRange(new double[] { 0.5, 1.7 });


        List<MatOfPoint> checkboxes = getCheckboxes(image, grayImage, config);
        System.out.println(checkboxes.size() + " checkboxes found.");

        List<String> data = new ArrayList<>();
        for (MatOfPoint checkbox : checkboxes) {
                Rect rect = new Rect(0,0,checkbox.width(),checkbox.height());
                Mat croppedImage = grayImage.submat(rect.y, rect.y + 80, rect.x, rect.x + width);
                // TODO: We need to find the text related to checkbox (the label)
                // I did no get time to do this.
                String text = Tesseract.imageToString(croppedImage);
                data.add(text);
        }

        if (checkboxes.isEmpty()) {
            System.out.println("No checkbox is checked.");
        }
        return data;
    }

    private static List<MatOfPoint> getCheckboxes(Mat original, Mat grayImage, PipelinesConfig config) {
        List<MatOfPoint> checkboxes = new ArrayList<>();
        Mat thresh = ImageCheckboxService.convertToGaussianAndThreshold(grayImage);

        // Find contours and filter using contour area filtering to remove noise
        List<MatOfPoint> cnts = new ArrayList<>();
        Mat hierarchy = ImageCheckboxService.findContours(thresh, cnts);

        // Repair checkbox horizontal and vertical walls if there is issue with quality.
        // This needs more I think a few edge cases to consider where squares or rect are not fully closed on one side etc.
        // There could be a strong shape with 3 shapes but the 4th side is not so visible.
        Mat repair = new Mat();
        ImageCheckboxService.repairVerticalAndHorizontalWalls(thresh, repair);

        // Detect checkboxes using shape approximation and aspect ratio filtering
        List<MatOfPoint> checkboxContours = ImageCheckboxService.detectCheckBox(cnts,repair,hierarchy,original);
        checkboxes.addAll(checkboxContours);
        System.out.println("Checkboxes: " + checkboxContours.size());


        // This is show the transformation of the steps above with the image visually using HighGUI.
        HighGui.imshow("thresh", thresh);
        HighGui.imshow("repair", repair);
        HighGui.imshow("original", original);
        // Note: Uncomment this and re-run the programme to do see the original/repair and thresh pictures of how they compare.
        HighGui.waitKey();

        return checkboxes;
    }

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
                List<String> fileData = findCheckBoxes(file.getAbsolutePath());
                System.out.println(file.getName());
                if (!fileData.isEmpty()) {
                    System.out.println(fileData);
                }
            }
        }
        if (count > 0) {
            System.out.println("TOTAL OF " + count + " files PROCESSED");
        } else {
            System.out.println("No Image file(s) found.");
        }
    }
}