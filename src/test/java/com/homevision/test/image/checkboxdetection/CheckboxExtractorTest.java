package com.homevision.test.image.checkboxdetection;

import com.homevision.test.image.checkboxdetection.model.CheckboxResult;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CheckboxExtractorTest {

    static {
        org.bytedeco.javacpp.Loader.load(org.bytedeco.opencv.opencv_java.class);
    }

    @Test
    void testFindCheckBoxes() {
        String testImagePath = "src/test/resources/sample/sample-section-mod.jpg";

        List<CheckboxResult> checkboxes = CheckboxExtractor.findCheckBoxes(testImagePath);

        assertNotNull(checkboxes, "The result should not be null.");
        assertFalse(checkboxes.isEmpty(), "Checkboxes should be detected.");

        assertEquals(checkboxes.size(),87,"The number of checkboxes detected");
        CheckboxResult firstCheckbox = checkboxes.get(0);
        assertNotNull(firstCheckbox.getLabel(), "Label should not be null.");
        assertTrue(firstCheckbox.isChecked() || !firstCheckbox.isChecked(), "Checkbox should be either checked or unchecked.");
    }

    @Test
    void testFindCheckBoxesEmptyImage() {
        String testImagePath = "src/test/resources/sample/empty-image.png";
        List<CheckboxResult> checkboxes = CheckboxExtractor.findCheckBoxes(testImagePath);

        assertTrue(checkboxes.isEmpty(), "Checkboxes should not be detected.");

    }

    @Test
    void testConvertToGrayscale() {
        String testImagePath = "src/test/resources/sample/sample-section-mod.jpg";
        Mat image = Imgcodecs.imread(testImagePath);

        Mat grayImage = CheckboxExtractor.convertToGrayscale(image);

        assertNotNull(grayImage, "Grayscale image should not be null.");
        assertEquals(image.size(), grayImage.size(), "Grayscale image size should match original image size.");
    }

    @Test
    void testIsCheckboxTicked() {
        String testImagePath = "src/test/resources/sample/sample-section-mod.jpg";
        Mat image = Imgcodecs.imread(testImagePath);
        Mat grayImage = CheckboxExtractor.convertToGrayscale(image);

        Mat checkboxRegion = grayImage.submat(0, 200, 0, 200);
        boolean isChecked = CheckboxExtractor.isCheckboxTicked(checkboxRegion);

        assertTrue(isChecked, "The checkbox should be ticked.");
    }

    @Test
    void testProcessImageFolder() {
        File testDirectory = new File("src/test/resources/sample");
        assertTrue(testDirectory.exists() && testDirectory.isDirectory(), "Test image folder should exist.");
    }
}