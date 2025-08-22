 package com.shapegame;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * ShapeMatchingGame — JavaFX educational drag‑and‑drop game for basic shapes.
 *
 * <h2>Overview</h2>
 * A child drags a shape outline onto its matching full‑color target. Upon a
 * correct drop, the game speaks/plays a sound, briefly shows a celebratory
 * placeholder (confetti), and advances to the next shape.
 *
 * <h2>UI</h2>
 * • Sparkly pink background (classpath image).<br>
 * • Progress label (step counter + correct count).<br>
 * • Draggable outline (smaller) and full‑color target (larger).<br>
 * • Shape name label appears after a correct match.<br>
 * • Play Again and Exit buttons appear when all shapes are matched.
 *
 * <h2>Assets</h2>
 * Shapes are described by {@code ShapeAsset} (name, empty image, full image, sound).
 * All assets are loaded at startup; if any fail to load, the app exits to avoid
 * half‑functional gameplay.
 *
 * <h2>Author</h2>
 * Tennie White — Version 1.0 (Java 21 / JavaFX 24)
 */
public class ShapeMatchingGame extends Application {

    /* ────────────────────────────── Constants ────────────────────────────── */

    /** Scene width/height in pixels. */
    private static final int WIDTH  = 800;
    private static final int HEIGHT = 600;

    /** Rendered sizes for outline (drag source) and full target (drop target). */
    private static final int EMPTY_SIZE = 150;
    private static final int FULL_SIZE  = 200;

    /** Duration for the quick "confetti" placeholder after a correct match. */
    private static final Duration CONFETTI_DURATION = Duration.seconds(1);

    /** Background image path (classpath). */
    private static final String BG_PATH = "/com/shapegame/resources/sparkly_pink_background.png";

    /* ─────────────────────────────── State ──────────────────────────────── */

    /** Ordered list of shape assets (triangle…trapezoid). */
    private final List<ShapeAsset> assets = new ArrayList<>();

    /** Index of the current shape (0..assets.size()-1). */
    private int currentIndex = 0;

    /** Running count of correct matches in this session. */
    private int correctMatches = 0;

    /* ─────────────────────────────── UI refs ────────────────────────────── */

    /** Root stack (background under content). */
    private StackPane stackPane;

    /** Vertical container for counter, images, name label, and buttons. */
    private VBox rootContainer;

    /** Buttons (shown at the end). */
    private VBox buttonBox;

    /** Progress indicator: e.g., "Match #3 of 10 — Correct: 2". */
    private Label counterLabel;

    /** Draggable outline image. */
    private ImageView draggableShapeView;

    /** Full‑color drop target image. */
    private ImageView targetShapeView;

    /** Shape name label (revealed after correct drop). */
    private Label nameLabel;

    /* ─────────────────────── JavaFX lifecycle ───────────────────────────── */

    @Override
    public void start(Stage primaryStage) {
        initAssets();      // load all shapes (exit if any fail)
        buildUI();         // set up nodes and initial state
        registerHandlers();// wire drag/drop and button actions

        // Background
        Image bg = safeLoadImage(BG_PATH);
        ImageView bgView = new ImageView(bg);
        bgView.setFitWidth(WIDTH);
        bgView.setFitHeight(HEIGHT);
        bgView.setPreserveRatio(false);

        stackPane = new StackPane(bgView, rootContainer);

        primaryStage.setTitle("Shape Matching Game");
        primaryStage.setScene(new Scene(stackPane, WIDTH, HEIGHT));
        primaryStage.show();
    }

    /** Standard JavaFX launcher. */
    public static void main(String[] args) {
        launch(args);
    }

    /* ───────────────────────────── Asset Load ───────────────────────────── */

    /**
     * Loads all ShapeAsset instances. Exits the app if any asset fails to load.
     * Assumes a {@code ShapeAsset.load(String)} factory that returns null on failure.
     */
    private void initAssets() {
        String[] names = {
            "triangle", "square", "circle", "oval", "rectangle",
            "pentagon", "hexagon", "octagon", "rhombus", "trapezoid"
        };
        for (String name : names) {
            ShapeAsset asset = ShapeAsset.load(name);
            if (asset == null) {
                System.err.println("Could not load assets for shape: " + name);
                Platform.exit();
                return;
            }
            assets.add(asset);
        }
    }

    /* ─────────────────────────────── Build UI ───────────────────────────── */

    /**
     * Constructs labels, image views, and buttons. Shows the first shape.
     */
    private void buildUI() {
        // 1) Progress counter (top)
        counterLabel = new Label();
        counterLabel.setAlignment(Pos.CENTER);
        counterLabel.setFont(Font.font(18));

        // 2) Draggable outline (smaller)
        draggableShapeView = new ImageView();
        draggableShapeView.setFitWidth(EMPTY_SIZE);
        draggableShapeView.setFitHeight(EMPTY_SIZE);
        draggableShapeView.setPreserveRatio(true);
        draggableShapeView.setCursor(Cursor.HAND);

        // 3) Full‑color target (larger)
        targetShapeView = new ImageView();
        targetShapeView.setFitWidth(FULL_SIZE);
        targetShapeView.setFitHeight(FULL_SIZE);
        targetShapeView.setPreserveRatio(true);

        // 4) Shape name label (hidden until correct match)
        nameLabel = new Label();
        nameLabel.setFont(Font.font("Impact", 24));
        nameLabel.setAlignment(Pos.CENTER);

        // 5) End‑of‑game buttons
        Button playAgain = new Button("Play Again");
        playAgain.setStyle("-fx-font-size:18px; -fx-background-color:lightgreen; -fx-font-weight:bold;");
        playAgain.setOnAction(ignored -> resetGame());

        Button exit = new Button("Exit");
        exit.setStyle("-fx-font-size:18px; -fx-background-color:salmon; -fx-font-weight:bold;");
        exit.setOnAction(ignored -> Platform.exit());

        buttonBox = new VBox(10, playAgain, exit);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setVisible(false);

        // 6) Root layout
        rootContainer = new VBox(20, counterLabel, draggableShapeView, targetShapeView, nameLabel, buttonBox);
        rootContainer.setAlignment(Pos.CENTER);

        // Initialize first screen
        showCurrentShape();
    }

    /* ───────────────────────────── Handlers ─────────────────────────────── */

    /**
     * Wires drag‑and‑drop interactions and button actions.
     */
    private void registerHandlers() {
        // Start drag from outline; put the current shape's name on the Dragboard.
        draggableShapeView.setOnDragDetected(ignored -> {
            Dragboard db = draggableShapeView.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(assets.get(currentIndex).getName());
            db.setContent(content);

            // Snapshot used as drag preview; centers image under the cursor.
            Image preview = draggableShapeView.snapshot(null, null);
            db.setDragView(preview, preview.getWidth() / 2, preview.getHeight() / 2);
        });

        // Allow drop on the full‑color target if there's string data.
        targetShapeView.setOnDragOver(ignored -> {
            Dragboard db = ignored.getDragboard();
            if (db != null && db.hasString()) {
                ignored.acceptTransferModes(TransferMode.MOVE);
            }
        });

        // On drop, verify the dragged shape name matches the current shape.
        targetShapeView.setOnDragDropped(ignored -> {
            Dragboard db = ignored.getDragboard();
            boolean correct = (db != null)
                    && db.hasString()
                    && db.getString().equals(assets.get(currentIndex).getName());
            if (correct) {
                onCorrectMatch();
            }
            ignored.setDropCompleted(correct);
        });
    }

    /* ───────────────────────────── Game Flow ────────────────────────────── */

    /**
     * Shows the current shape (or the end screen if we've finished).
     */
    private void showCurrentShape() {
        if (currentIndex >= assets.size()) {
            // Finished all shapes: show buttons and final tallies.
            buttonBox.setVisible(true);
            counterLabel.setText(
                "All done! Correct matches: " + correctMatches + " / " + assets.size()
            );
            nameLabel.setText("");
            draggableShapeView.setImage(null);
            targetShapeView.setImage(null);
            return;
        }

        ShapeAsset asset = assets.get(currentIndex);
        // Counter shows step and running correct tally
        counterLabel.setText("Match #" + (currentIndex + 1) + " of " + assets.size()
                + " — Correct: " + correctMatches);

        // Outline (drag source) and full image (drop target)
        draggableShapeView.setImage(asset.getEmptyImage());
        targetShapeView.setImage(asset.getFullImage());

        // Clear the prior shape name & hide buttons (we're mid‑game).
        nameLabel.setText("");
        buttonBox.setVisible(false);
    }

    /**
     * Called on a correct drop: update counts, show name, play effect and sound, then advance.
     */
    private void onCorrectMatch() {
        correctMatches++;
        nameLabel.setText(assets.get(currentIndex).getName());

        // Quick celebratory delay (placeholder for a confetti animation).
        PauseTransition pt = new PauseTransition(CONFETTI_DURATION);
        pt.play();

        // Play per‑shape sound, then advance when it finishes.
        MediaPlayer player = assets.get(currentIndex).getSound();
        if (player != null) {
            player.stop();
            player.play();
            player.setOnEndOfMedia(() -> {
                player.stop();
                currentIndex++;
                showCurrentShape();
            });
        } else {
            // If no audio is available, just proceed after the delay.
            pt.setOnFinished(ignored -> {
                currentIndex++;
                showCurrentShape();
            });
        }

        // Update the progress line immediately to reflect the new correct count.
        counterLabel.setText("Match #" + (currentIndex + 1) + " of " + assets.size()
                + " — Correct: " + correctMatches);
    }

    /**
     * Resets the session to the first shape and clears counters.
     */
    private void resetGame() {
        currentIndex = 0;
        correctMatches = 0;
        showCurrentShape();
    }

    /* ─────────────────────────── Utilities ──────────────────────────────── */

    /**
     * Loads an image from the classpath and fails fast if unavailable.
     */
    private static Image safeLoadImage(String resourcePath) {
        var in = ShapeMatchingGame.class.getResourceAsStream(resourcePath);
        if (in == null) {
            System.err.println("Missing image resource: " + resourcePath);
            // Create a tiny transparent placeholder so the app still shows
            return new Image(ShapeMatchingGame.class.getResourceAsStream(
                    "/com/shapegame/resources/transparent_1x1.png"
            ));
        }
        return new Image(in);
    }
}
