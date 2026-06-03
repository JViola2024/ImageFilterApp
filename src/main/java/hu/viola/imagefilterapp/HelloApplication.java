package hu.viola.imagefilterapp;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class HelloApplication extends Application {

    private Image originalImage;
    private Image currentImage;

    private boolean blackWhiteActive = false;
    private boolean blurActive = false;
    private boolean invertActive = false;

    private File[] imageFiles;
    private int currentImageIndex = 0;

    @Override
    public void start(Stage stage) {

        Button openButton = new Button("📂 Megnyitás");
        Button saveButton = new Button("💾  Mentés");

        Button previousButton = new Button("Előző");
        Button nextButton = new Button("Következő");

        Button blackWhiteButton = new Button("◐ Fekete-fehér");
        Button invertButton = new Button("◩  Invertálás");
        Button blurButton = new Button("💧  Elmosás");

        Button leftArrowButton = new Button("❮");
        Button rightArrowButton = new Button("❯");

        Button[] buttons = {
                openButton,
                saveButton,
                previousButton,
                nextButton,
                blackWhiteButton,
                invertButton,
                blurButton
        };

        for (Button button : buttons) {

            button.setStyle(
                    "-fx-font-size: 14px;" +
                            "-fx-padding: 8 14 8 14;" +
                            "-fx-background-radius: 8;" +
                            "-fx-background-color: #1e1e1e;" +
                            "-fx-text-fill: white;" +
                            "-fx-border-color: #444444;" +
                            "-fx-border-radius: 8;"
            );
        }

        leftArrowButton.setStyle(
                "-fx-font-size: 32px;" +
                        "-fx-background-color: rgba(0,0,0,0.45);" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 40px;" +
                        "-fx-min-width: 65px;" +
                        "-fx-min-height: 65px;"
        );

        rightArrowButton.setStyle(
                "-fx-font-size: 32px;" +
                        "-fx-background-color: rgba(0,0,0,0.45);" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 40px;" +
                        "-fx-min-width: 65px;" +
                        "-fx-min-height: 65px;"
        );

        HBox leftMenu = new HBox(10);
        leftMenu.getChildren().addAll(openButton, saveButton);
        leftMenu.setAlignment(Pos.CENTER_LEFT);

        HBox centerMenu = new HBox(10);
        centerMenu.getChildren().addAll(
                blackWhiteButton,
                invertButton,
                blurButton
        );
        centerMenu.setAlignment(Pos.CENTER);

        HBox rightMenu = new HBox(10);
        rightMenu.getChildren().addAll(
                previousButton,
                nextButton
        );
        rightMenu.setAlignment(Pos.CENTER_RIGHT);

        BorderPane toolbar = new BorderPane();

        toolbar.setLeft(leftMenu);
        toolbar.setCenter(centerMenu);
        toolbar.setRight(rightMenu);

        toolbar.setPadding(new Insets(15));

        toolbar.setStyle("-fx-background-color: #181818;");

        ImageView imageView = new ImageView();

        imageView.setPreserveRatio(true);

        imageView.setFitWidth(950);

        imageView.setFitHeight(650);

        HBox thumbnailBar = new HBox(10);

        thumbnailBar.setPadding(new Insets(10));

        thumbnailBar.setStyle(
                "-fx-background-color: #181818;"
        );

        ScrollPane thumbnailScroll = new ScrollPane(thumbnailBar);

        thumbnailScroll.setFitToHeight(true);

        thumbnailScroll.setPrefHeight(95);

        thumbnailScroll.setStyle(
                "-fx-background: #181818;" +
                        "-fx-background-color: #181818;"
        );

        Label infoLabel =
                new Label("Válassz ki egy képet a megnyitás gombbal.");

        infoLabel.setPadding(new Insets(8));

        infoLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;"
        );

        openButton.setOnAction(e -> {

            FileChooser fileChooser = new FileChooser();

            fileChooser.setTitle("Kép kiválasztása");

            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "Képek",
                            "*.png",
                            "*.jpg",
                            "*.jpeg"
                    )
            );

            File file = fileChooser.showOpenDialog(stage);

            if (file != null) {

                File folder = file.getParentFile();

                imageFiles = folder.listFiles((dir, name) ->
                        name.toLowerCase().endsWith(".png")
                                || name.toLowerCase().endsWith(".jpg")
                                || name.toLowerCase().endsWith(".jpeg")
                );

                thumbnailBar.getChildren().clear();

                for (int i = 0; i < imageFiles.length; i++) {

                    File imageFile = imageFiles[i];

                    Image thumbnailImage =
                            new Image(
                                    imageFile.toURI().toString(),
                                    100,
                                    70,
                                    true,
                                    true
                            );

                    ImageView thumbnailView =
                            new ImageView(thumbnailImage);

                    thumbnailView.setFitWidth(100);

                    thumbnailView.setFitHeight(70);

                    thumbnailView.setStyle(
                            "-fx-border-color: #555555;"
                    );

                    final int index = i;

                    thumbnailView.setOnMouseClicked(event -> {

                        currentImageIndex = index;

                        loadImage(
                                imageFiles[currentImageIndex],
                                imageView,
                                infoLabel
                        );
                    });

                    thumbnailBar.getChildren().add(thumbnailView);

                    if (imageFiles[i].equals(file)) {

                        currentImageIndex = i;
                    }
                }

                loadImage(file, imageView, infoLabel);
            }
        });

        previousButton.setOnAction(e -> {

            if (imageFiles == null || imageFiles.length == 0) {
                return;
            }

            currentImageIndex--;

            if (currentImageIndex < 0) {

                currentImageIndex = imageFiles.length - 1;
            }

            loadImage(
                    imageFiles[currentImageIndex],
                    imageView,
                    infoLabel
            );
        });

        leftArrowButton.setOnAction(e -> {
            previousButton.fire();
        });

        nextButton.setOnAction(e -> {

            if (imageFiles == null || imageFiles.length == 0) {
                return;
            }

            currentImageIndex++;

            if (currentImageIndex >= imageFiles.length) {

                currentImageIndex = 0;
            }

            loadImage(
                    imageFiles[currentImageIndex],
                    imageView,
                    infoLabel
            );
        });

        rightArrowButton.setOnAction(e -> {
            nextButton.fire();
        });

        blackWhiteButton.setOnAction(e -> {

            blackWhiteActive = !blackWhiteActive;

            updateImage(imageView, infoLabel);
        });

        invertButton.setOnAction(e -> {

            invertActive = !invertActive;

            updateImage(imageView, infoLabel);
        });

        blurButton.setOnAction(e -> {

            blurActive = !blurActive;

            updateImage(imageView, infoLabel);
        });

        saveButton.setOnAction(e -> {

            if (currentImage == null) {

                infoLabel.setText("Nincs menthető kép.");

                return;
            }

            FileChooser fileChooser = new FileChooser();

            fileChooser.setTitle("Kép mentése");

            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "PNG kép",
                            "*.png"
                    )
            );

            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {

                try {

                    RenderedImage renderedImage =
                            SwingFXUtils.fromFXImage(
                                    currentImage,
                                    null
                            );

                    ImageIO.write(
                            renderedImage,
                            "png",
                            file
                    );

                    infoLabel.setText(
                            "Kép elmentve: " + file.getName()
                    );

                } catch (IOException ex) {

                    infoLabel.setText("Mentési hiba.");
                }
            }
        });

        StackPane imageContainer = new StackPane();

        imageContainer.getChildren().addAll(
                imageView,
                leftArrowButton,
                rightArrowButton
        );

        StackPane.setAlignment(
                leftArrowButton,
                Pos.CENTER_LEFT
        );

        StackPane.setAlignment(
                rightArrowButton,
                Pos.CENTER_RIGHT
        );

        StackPane.setMargin(
                leftArrowButton,
                new Insets(0, 20, 0, 20)
        );

        StackPane.setMargin(
                rightArrowButton,
                new Insets(0, 20, 0, 20)
        );

        BorderPane root = new BorderPane();

        root.setTop(toolbar);

        root.setCenter(imageContainer);

        root.setBottom(thumbnailScroll);

        BorderPane.setAlignment(
                imageContainer,
                Pos.CENTER
        );

        BorderPane.setMargin(
                imageContainer,
                new Insets(20)
        );

        root.setStyle(
                "-fx-background-color: #333333;"
        );

        Scene scene = new Scene(root, 1300, 850);

        stage.setTitle("Image Filter App");

        stage.setScene(scene);

        stage.show();
    }

    private void loadImage(
            File file,
            ImageView imageView,
            Label infoLabel
    ) {

        Image image = new Image(file.toURI().toString());

        originalImage = image;

        currentImage = image;

        blackWhiteActive = false;
        blurActive = false;
        invertActive = false;

        imageView.setImage(image);

        infoLabel.setText(
                "Betöltött kép: " + file.getName()
        );
    }

    private void updateImage(
            ImageView imageView,
            Label infoLabel
    ) {

        if (originalImage == null) {
            return;
        }

        Image workingImage = originalImage;

        if (blackWhiteActive) {

            workingImage =
                    applyBlackWhiteFilter(workingImage);
        }

        if (invertActive) {

            workingImage =
                    applyInvertFilter(workingImage);
        }

        if (blurActive) {

            workingImage =
                    applyBlurFilter(workingImage);
        }

        currentImage = workingImage;

        imageView.setImage(currentImage);

        infoLabel.setText("Filterek frissítve.");
    }

    private Image applyBlackWhiteFilter(Image image) {

        int width = (int) image.getWidth();

        int height = (int) image.getHeight();

        WritableImage blackWhiteImage =
                new WritableImage(width, height);

        PixelReader pixelReader =
                image.getPixelReader();

        PixelWriter pixelWriter =
                blackWhiteImage.getPixelWriter();

        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {

                Color color =
                        pixelReader.getColor(x, y);

                double gray =
                        (color.getRed()
                                + color.getGreen()
                                + color.getBlue()) / 3;

                Color newColor =
                        new Color(
                                gray,
                                gray,
                                gray,
                                color.getOpacity()
                        );

                pixelWriter.setColor(x, y, newColor);
            }
        }

        return blackWhiteImage;
    }

    private Image applyInvertFilter(Image image) {

        int width = (int) image.getWidth();

        int height = (int) image.getHeight();

        WritableImage invertedImage =
                new WritableImage(width, height);

        PixelReader pixelReader =
                image.getPixelReader();

        PixelWriter pixelWriter =
                invertedImage.getPixelWriter();

        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {

                Color color =
                        pixelReader.getColor(x, y);

                Color invertedColor =
                        new Color(
                                1.0 - color.getRed(),
                                1.0 - color.getGreen(),
                                1.0 - color.getBlue(),
                                color.getOpacity()
                        );

                pixelWriter.setColor(
                        x,
                        y,
                        invertedColor
                );
            }
        }

        return invertedImage;
    }

    private Image applyBlurFilter(Image image) {

        int width = (int) image.getWidth();

        int height = (int) image.getHeight();

        WritableImage blurredImage =
                new WritableImage(width, height);

        PixelReader pixelReader =
                image.getPixelReader();

        PixelWriter pixelWriter =
                blurredImage.getPixelWriter();

        for (int y = 1; y < height - 1; y++) {

            for (int x = 1; x < width - 1; x++) {

                double red = 0;
                double green = 0;
                double blue = 0;

                for (int dy = -1; dy <= 1; dy++) {

                    for (int dx = -1; dx <= 1; dx++) {

                        Color color =
                                pixelReader.getColor(
                                        x + dx,
                                        y + dy
                                );

                        red += color.getRed();

                        green += color.getGreen();

                        blue += color.getBlue();
                    }
                }

                red /= 9;
                green /= 9;
                blue /= 9;

                Color blurredColor =
                        new Color(red, green, blue, 1.0);

                pixelWriter.setColor(
                        x,
                        y,
                        blurredColor
                );
            }
        }

        return blurredImage;
    }

    public static void main(String[] args) {

        launch();
    }
}
