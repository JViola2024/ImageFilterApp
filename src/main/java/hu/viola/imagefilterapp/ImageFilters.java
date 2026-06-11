package hu.viola.imagefilterapp;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ImageFilters {

    public static Image applyBlackWhiteFilter(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        WritableImage result = new WritableImage(width, height);
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = result.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                double gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3;

                writer.setColor(x, y, new Color(gray, gray, gray, color.getOpacity()));
            }
        }

        return result;
    }

    public static Image applyInvertFilter(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        WritableImage result = new WritableImage(width, height);
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = result.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);

                writer.setColor(x, y, new Color(
                        1.0 - color.getRed(),
                        1.0 - color.getGreen(),
                        1.0 - color.getBlue(),
                        color.getOpacity()
                ));
            }
        }

        return result;
    }

    public static Image applyBlurFilter(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        WritableImage result = new WritableImage(width, height);
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = result.getPixelWriter();

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                double red = 0;
                double green = 0;
                double blue = 0;

                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        Color color = reader.getColor(x + dx, y + dy);

                        red += color.getRed();
                        green += color.getGreen();
                        blue += color.getBlue();
                    }
                }

                writer.setColor(x, y, new Color(red / 9, green / 9, blue / 9, 1.0));
            }
        }

        return result;
    }

    public static Image applySepiaFilter(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        WritableImage result = new WritableImage(width, height);
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = result.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);

                double red = color.getRed();
                double green = color.getGreen();
                double blue = color.getBlue();

                double newRed = clamp(red * 0.393 + green * 0.769 + blue * 0.189);
                double newGreen = clamp(red * 0.349 + green * 0.686 + blue * 0.168);
                double newBlue = clamp(red * 0.272 + green * 0.534 + blue * 0.131);

                writer.setColor(x, y, new Color(newRed, newGreen, newBlue, color.getOpacity()));
            }
        }

        return result;
    }

    public static Image applyBrightnessFilter(Image image, double brightness) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        WritableImage result = new WritableImage(width, height);
        PixelReader reader = image.getPixelReader();
        PixelWriter writer = result.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);

                double red = clamp(color.getRed() + brightness);
                double green = clamp(color.getGreen() + brightness);
                double blue = clamp(color.getBlue() + brightness);

                writer.setColor(x, y, new Color(red, green, blue, color.getOpacity()));
            }
        }

        return result;
    }

    private static double clamp(double value) {
        return Math.max(0, Math.min(1.0, value));
    }
}