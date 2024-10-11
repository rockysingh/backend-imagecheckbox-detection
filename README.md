# backend-checkbox-detection


## Prerequisite

1. You will need Java 17
2. I ran the programme using the main class in `CheckboxExtractor` using `Intellij`
3. If you need to add more images please add them to `src/main/resources/sample/`


In order to run the unit tests please use `./gradlew test`

![Screenshot 2024-10-11 at 15.00.54.png](https://raw.githubusercontent.com/rockysingh/backend-imagecheckbox-detection/refs/heads/main/Screenshot%202024-10-11%20at%2014.56.48.png)

### Challenge

I determined eventually a few steps.

1. The first step, we needed to load the image, convert it to grayscale, 
2. Secondly, use adapter gaussian blur to help with different image quality.
2. Thirdly, I use Morphological operations to enhance checkbox shapes
3. Then use contours to iterate over and then in those find squares (assuming they are checkboxes)
4. In the checkboxes we then check for dark text and assumed it is ticked if we find it.
5. TODO: I did not find the label for checkbox (This was NOT a requirement)


## Future

I time boxed myself too 2hr 30 mins.

1. Added pipeline config to work with different config
2. There is an assumption that the checkbox would have dark tick or x. They could use a differnet background so we need to to inverse perhaps if its a dark background with light tick or x
3. I think use shapes like X or Tick to determine if its checked. I think it provides more accuracy too and its good to store the type of shape used to tick box
4. Label extraction (not a requirement)

## Learning

This was very interesting and challenging. I never considered the challenge of images and how they cause problems for
OCR. There are other factors too consider such as colour and the language of text. I have saved to project to play around with it in my spare time :)

