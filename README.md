# backend-checkbox-detection


### Challenge

I determined eventually a few steps.

1. The first step, we needed to load the image, convert it to grayscale, gaussian blur and add a threshold (Otsu's).
2. Secondly, We needed to find contours and then filter using the contour area to remove noise on the image.

The step in two was more tricky and in long term needed configuration per image. I did try that but didn't get time to finish it off.

3. Thirdly, we need to repair the vertical and horizontal walls of the checkboxes in the image. However, I found this tricky due to the thickness of walls.
4. Lastly, we needed to detect the checkboxes and use approxmation and aspect ratio filtering.


## Future

I time boxed myself too 1hr 30 mins.

1. I did not find all checkboxes on the image except the larger square. I needed to play around with the repair of the image
and consider thickness of the wall etc.
2. I did not find if the checkbox was ticked or not.
3. I did not find the text of the checkbox.
4. I refactored most of the code in to a more managable class `ImageCheckboxService` to see how the differnet methods 
were being executed in the steps to determine a checkbox. I did not get time to clean up the `CheckboxExtractor` class.
5. I would have added config per image which could be used to determine the variation of checkboxes i.e size, thickness or lines etc.
6. There is a lot of challenges with image size, quality and type of file etc.
7. Language considerations etc could play a part with text rendering and type of character set.

## Learning

This was very interesting and challenging. I never considered the challenge of images and how they cause problems for
OCR. There are other factors too consider such as colour and the language of text. I have saved to project to play around with it in my spare time :)

