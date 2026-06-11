package hu.viola.imagefilterapp;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class HelloApplication extends Application {

    private Image originalImage;

    private boolean blackWhiteActive = false;
    private boolean blurActive = false;
    private boolean invertActive = false;
    private boolean sepiaActive = false;
    private double brightnessValue = 0;

    private File[] imageFiles;
    private int currentImageIndex = 0;

    @Override
    public void start(Stage stage) {
        Button openButton = createButton("📂 Megnyitás");
        Button saveButton = createButton("💾 Mentés");

        Button previousButton = createButton("Előző");
        Button nextButton = createButton("Következő");

        Button blackWhiteButton = createButton("◐ Fekete-fehér");
        Button invertButton = createButton("◩ Invertálás");
        Button blurButton = createButton("💧 Elmosás");
        Button sepiaButton = createButton("🟤 Szépia");

        Slider brightnessSlider = new Slider(-0.5, 0.5, 0);
        brightnessSlider.setPrefWidth(150);

        Button leftArrowButton = new Button("❮");
        Button rightArrowButton = new Button("❯");

        leftArrowButton.getStyleClass().add("arrow-button");
        rightArrowButton.getStyleClass().add("arrow-button");

        HBox leftMenu = new HBox(10, openButton, saveButton);
        leftMenu.setAlignment(Pos.CENTER_LEFT);

        HBox centerMenu = new HBox(10, blackWhiteButton, invertButton, blurButton, sepiaButton, brightnessSlider);
        centerMenu.setAlignment(Pos.CENTER);

        HBox rightMenu = new HBox(10, previousButton, nextButton);
        rightMenu.setAlignment(Pos.CENTER_RIGHT);


        BorderPane toolbar = new BorderPane();
        toolbar.setLeft(leftMenu);
        toolbar.setCenter(centerMenu);
        toolbar.setRight(rightMenu);
        toolbar.getStyleClass().add("toolbar");

        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(950);
        imageView.setFitHeight(650);

        HBox thumbnailBar = new HBox(10);
        thumbnailBar.getStyleClass().add("thumbnail-bar");

        ScrollPane thumbnailScroll = new ScrollPane(thumbnailBar);
        thumbnailScroll.setFitToHeight(true);
        thumbnailScroll.setPrefHeight(95);
        thumbnailScroll.getStyleClass().add("thumbnail-scroll");

        openButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Kép kiválasztása");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Képek", "*.png", "*.jpg", "*.jpeg")
            );

            File file = fileChooser.showOpenDialog(stage);

            if (file != null) {
                File folder = file.getParentFile();

                imageFiles = folder.listFiles((dir, name) ->
                        name.endsWith(".png")
                                || name.endsWith(".jpg")
                                || name.endsWith(".jpeg")
                );

                for (int i = 0; i < imageFiles.length; i++) {
                    if (imageFiles[i].equals(file)) {
                        currentImageIndex = i;
                    }
                }

                buildThumbnails(thumbnailBar, imageView);
                loadImage(file, imageView);
            }
        });

        previousButton.setOnAction(e -> showImageByStep(-1, imageView));
        nextButton.setOnAction(e -> showImageByStep(1, imageView));

        leftArrowButton.setOnAction(e -> previousButton.fire());
        rightArrowButton.setOnAction(e -> nextButton.fire());

        blackWhiteButton.setOnAction(e -> {
            blackWhiteActive = !blackWhiteActive;
            updateImage(imageView);
        });

        invertButton.setOnAction(e -> {
            invertActive = !invertActive;
            updateImage(imageView);
        });

        blurButton.setOnAction(e -> {
            blurActive = !blurActive;
            updateImage(imageView);
        });

        sepiaButton.setOnAction(e -> {
            sepiaActive = !sepiaActive;
            updateImage(imageView);
        });

        brightnessSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            brightnessValue = newVal.doubleValue();
            updateImage(imageView);
        });

        saveButton.setOnAction(e -> {
            if (imageView.getImage() == null) {
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Kép mentése");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PNG kép", "*.png")
            );

            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                try {
                    RenderedImage renderedImage = SwingFXUtils.fromFXImage(imageView.getImage(), null);
                    ImageIO.write(renderedImage, "png", file);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        StackPane imageContainer = new StackPane(imageView, leftArrowButton, rightArrowButton);

        StackPane.setAlignment(leftArrowButton, Pos.CENTER_LEFT);
        StackPane.setAlignment(rightArrowButton, Pos.CENTER_RIGHT);

        StackPane.setMargin(leftArrowButton, new Insets(0, 20, 0, 20));
        StackPane.setMargin(rightArrowButton, new Insets(0, 20, 0, 20));

        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(imageContainer);
        root.setBottom(thumbnailScroll);

        BorderPane.setAlignment(imageContainer, Pos.CENTER);
        BorderPane.setMargin(imageContainer, new Insets(20));

        Scene scene = new Scene(root, 1300, 850);
        scene.getStylesheets().add(
                getClass().getResource("style.css").toExternalForm()
        );

        stage.setTitle("Image Filter App");
        stage.setScene(scene);
        stage.show();
    }

    private void buildThumbnails(HBox thumbnailBar, ImageView imageView) {
        thumbnailBar.getChildren().clear();

        for (int i = 0; i < imageFiles.length; i++) {
            File imageFile = imageFiles[i];

            Image thumbnailImage = new Image(imageFile.toURI().toString(), 100, 70, true, true);

            ImageView thumbnailView = new ImageView(thumbnailImage);
            thumbnailView.setPreserveRatio(true);
            thumbnailView.setStyle("-fx-border-color: #555555;");

            final int index = i;

            thumbnailView.setOnMouseClicked(event -> {
                currentImageIndex = index;
                loadImage(imageFiles[currentImageIndex], imageView);
            });

            thumbnailBar.getChildren().add(thumbnailView);
        }
    }

    private void showImageByStep(int step, ImageView imageView) {
        if (imageFiles == null || imageFiles.length == 0) {
            return;
        }

        currentImageIndex += step;

        if (currentImageIndex < 0) {
            currentImageIndex = imageFiles.length - 1;
        }

        if (currentImageIndex >= imageFiles.length) {
            currentImageIndex = 0;
        }

        loadImage(imageFiles[currentImageIndex], imageView);
    }

    private void loadImage(File file, ImageView imageView) {
        Image image = new Image(file.toURI().toString());

        originalImage = image;

        blackWhiteActive = false;
        blurActive = false;
        invertActive = false;
        sepiaActive = false;

        imageView.setImage(image);
    }

    private void updateImage(ImageView imageView) {
        if (originalImage == null) {
            return;
        }

        Image workingImage = originalImage;

        if (blackWhiteActive) {
            workingImage = ImageFilters.applyBlackWhiteFilter(workingImage);
        }

        if (invertActive) {
            workingImage = ImageFilters.applyInvertFilter(workingImage);
        }

        if (blurActive) {
            workingImage = ImageFilters.applyBlurFilter(workingImage);
        }

        if (sepiaActive) {
            workingImage = ImageFilters.applySepiaFilter(workingImage);
        }

        if (brightnessValue != 0) {
            workingImage = ImageFilters.applyBrightnessFilter(workingImage, brightnessValue);
        }

        imageView.setImage(workingImage);
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("main-button");
        return button;
    }

    public static void main(String[] args) {
        launch();
    }
}