package com.homevision.test.image.checkboxdetection;

import com.homevision.test.image.checkboxdetection.model.Checkbox;
import com.homevision.test.image.checkboxdetection.service.ImageService;
import org.jline.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.standard.ShellMethod;

import java.util.List;

@SpringBootApplication
public class ImageCheckboxDetectionApplication {

	private static Logger LOG = LoggerFactory
			.getLogger(ImageCheckboxDetectionApplication.class);

	@ShellMethod(value = "Please enter an image to process for checkboxes")
	public static void main(String[] args) {
		SpringApplication.run(ImageCheckboxDetectionApplication.class, args);

		String imagePath = System.getenv("image");
		if (imagePath == null) {
			throw new RuntimeException("Environment variable image is required for checkbox processing");
		}

		Log.info("image resource is:" + imagePath);

		List<Checkbox> checkboxList = ImageService.image(imagePath);


		for (Checkbox checkbox : checkboxList) {
			Log.info("check box found:" + checkbox.toString());
		}
 	}

}
