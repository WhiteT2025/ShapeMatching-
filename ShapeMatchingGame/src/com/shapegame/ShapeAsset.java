 package com.shapegame;

import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.Objects;

/**
 * ShapeAsset
 * ------------
 * Encapsulates the media assets for a single shape used by the Shape Matching Game:
 *   • name        – logical identifier (e.g., "triangle")
 *   • fullImage   – filled/colored image, used as the drop target
 *   • emptyImage  – outline image, used as the draggable source
 *   • sound       – MediaPlayer that speaks/plays the shape name on a correct match
 *
 * Expected classpath resource layout (place these under your resources folder):
 *   /com/shapegame/resources/{name}.png          ← full colored image
 *   /com/shapegame/resources/empty_{name}.png    ← outline image
 *   /com/shapegame/resources/{name}.mp3          ← audio file
 *
 * Example (triangle):
 *   /com/shapegame/resources/triangle.png
 *   /com/shapegame/resources/empty_triangle.png
 *   /com/shapegame/resources/triangle.mp3
 *
 * Authors: Elena Reiss & Tennie White
 * Version: 16 (Java 24 / JavaFX 24)
 */
public final class ShapeAsset {

    /* ─────────────────────────────── Constants ─────────────────────────────── */

    /** Base directory on the classpath where assets live. */
    private static final String BASE = "/com/shapegame/resources/";

    /* ─────────────────────────────── Fields ───────────────────────────────── */

    /** Logical identifier for this shape (used for drag/drop equality checks). */
    private final String name;

    /** Filled/colored image (drop target). */
    private final Image fullImage;

    /** Outline image (drag source). */
    private final Image emptyImage;

    /** Audio player that announces the shape name when matched. */
    private final MediaPlayer sound;

    /* ─────────────────────────────── Construction ─────────────────────────── */

    /**
     * Private constructor; instances are created through {@link #load(String)} only
     * after all resources are validated and loaded.
     */
    private ShapeAsset(String name, Image fullImage, Image emptyImage, MediaPlayer sound) {
        this.name = name;
        this.fullImage = fullImage;
        this.emptyImage = emptyImage;
        this.sound = sound;
    }

    /**
     * Factory method that loads a complete {@code ShapeAsset} from classpath resources.
     * <p>
     * Returns {@code null} if any of the three required resources are missing or fail to load.
     *
     * @param name shape identifier that matches filenames (e.g., "triangle")
     * @return initialized {@code ShapeAsset}, or {@code null} if something is missing
     */
    public static ShapeAsset load(String name) {
        Objects.requireNonNull(name, "shape name must not be null");
        // Resolve the three resource paths based on the agreed naming convention
        final String fullPath  = BASE + name + ".png";
        final String emptyPath = BASE + "empty_" + name + ".png";
        final String soundPath = BASE + name + ".mp3";

        // Locate resources on the classpath (inside target/classes or JAR)
        final URL fullUrl  = ShapeAsset.class.getResource(fullPath);
        final URL emptyUrl = ShapeAsset.class.getResource(emptyPath);
        final URL soundUrl = ShapeAsset.class.getResource(soundPath);

        // Fail softly but clearly if anything is missing
        if (fullUrl == null) {
            System.err.println("Missing image resource: " + fullPath);
            return null;
        }
        if (emptyUrl == null) {
            System.err.println("Missing image resource: " + emptyPath);
            return null;
        }
        if (soundUrl == null) {
            System.err.println("Missing audio resource: " + soundPath);
            return null;
        }

        // Load images from URLs. Using URL strings avoids manual stream management.
        final Image fullImg;
        final Image emptyImg;
        try {
            fullImg = new Image(fullUrl.toExternalForm());
            emptyImg = new Image(emptyUrl.toExternalForm());
        } catch (Exception ex) {
            System.err.println("Failed to load images for '" + name + "': " + ex.getMessage());
            return null;
        }

        // Create a MediaPlayer for the audio. We construct the player here so it can be reused.
        final MediaPlayer player;
        try {
            Media media = new Media(soundUrl.toExternalForm());
            player = new MediaPlayer(media);
            // Optional: preload by touching status or volume if you like
            // player.setVolume(1.0);
        } catch (Exception ex) {
            System.err.println("Failed to load audio for '" + name + "': " + ex.getMessage());
            return null;
        }

        return new ShapeAsset(name, fullImg, emptyImg, player);
    }

    /* ─────────────────────────────── Accessors ───────────────────────────── */

    /** @return logical identifier of the shape (used for drag/drop matching) */
    public String getName() {
        return name;
    }

    /** @return filled/colored image used as the drop target */
    public Image getFullImage() {
        return fullImage;
    }

    /** @return outline image used as the draggable source */
    public Image getEmptyImage() {
        return emptyImage;
    }

    /** @return MediaPlayer that plays the shape’s name on a correct match */
    public MediaPlayer getSound() {
        return sound;
    }

    /* ─────────────────────────────── Notes ──────────────────────────────────
     * • Lifetime: MediaPlayer instances can be reused. If you create many assets
     *   or switch levels, consider calling player.dispose() when an asset is no
     *   longer needed to release native resources.
     * • Background loading: If you want images to load asynchronously, you can use
     *   the Image(String url, double w, double h, boolean preserve, boolean smooth, boolean backgroundLoading)
     *   constructor with backgroundLoading=true, then react when image.getProgress()==1.0.
     * • Internationalization: You can extend the naming convention to include a locale
     *   suffix (e.g., triangle_en.mp3) and select at load time.
     * ─────────────────────────────────────────────────────────────────────── */
}
