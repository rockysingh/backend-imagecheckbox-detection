package com.homevision.test.image.checkboxdetection.service;

import com.homevision.test.image.checkboxdetection.model.Checkbox;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageService {

    public static List<Checkbox> image(String imagePath){
        BufferedImage bufferedImage = loadBufferedImage(imagePath);
        return new ArrayList<>();
    }

    public static BufferedImage loadBufferedImage(String imagePath){
        BufferedImage img = null;

        try
        {
            img = ImageIO.read(new File(imagePath));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return img;
    }

}
