
# Little Luminaries — **ShapeMatchingGame** (JavaFX 24)
## Eclipse 2025‑09 Run Guide — **No Modules** (no `module-info.java`)

**App:** ShapeMatchingGame (JavaFX drag‑and‑drop learning game)  
**Author:** Tennie White  
**Tested With:** JDK 24, JavaFX SDK 24, Eclipse 2025‑09  
**Build Tool:** None (plain Eclipse; **no Maven/Gradle**, **no modules**)

---

## 1) What this app is
**ShapeMatchingGame** is a kid‑friendly JavaFX game that teaches matching by dragging shapes (or animal/thing icons) onto their outlines. It uses images, simple animations, and sounds for feedback.

---

## 2) Requirements
- **JDK 24** installed and selected in Eclipse  
  *Eclipse →* **Window → Preferences → Java → Installed JREs** → add/select **JDK 24**
- **JavaFX SDK 24** downloaded and unzipped (you’ll need the path to its `lib` folder)
- **Eclipse 2025‑09** (or newer)

> We will run **non‑modular**: there is **no** `module-info.java`. JavaFX is provided at runtime using **VM arguments**.

---

## 3) Recommended Project Layout
Make sure your **package** line matches the folder path of the `.java` file(s). Update below paths if your package is different.

```
ShapeMatchingGame/                 ← Eclipse project root
├─ src/
│  └─ shapematching/               ← or com/littleluminaries/shapematching/
│     └─ ShapeMatchingGame.java    ← main class
├─ resources/                      ← images / audio used by the game
│  ├─ images/
│  │  ├─ circle.png
│  │  ├─ square.png
│  │  ├─ triangle.png
│  │  └─ ... (any other shapes/icons)
│  ├─ sounds/
│  │  ├─ correct.mp3
│  │  └─ try_again.mp3
│  └─ backgrounds/
│     └─ board.png (optional)
└─ lib/                            ← optional (extra jars if you use any)
```

**Package vs. folders:**  
- If `ShapeMatchingGame.java` starts with `package shapematching;` then it must be under `src/shapematching/ShapeMatchingGame.java`.  
- If it starts with `package com.littleluminaries.shapematching;` then it must be under `src/com/littleluminaries/shapematching/ShapeMatchingGame.java`.

**Asset paths (pick one style and stick to it):**
- *File URL style (simple, project‑root “resources/”):*  
  `new Image("file:resources/images/circle.png")`
- *Classpath style (mark `resources/` as Source Folder):*  
  `new Image(getClass().getResource("/images/circle.png").toExternalForm())`  
  To use this, right‑click **resources** → **Build Path → Use as Source Folder**.

---

## 4) Import the Project into Eclipse
**File → Import… → General → Existing Projects into Workspace → Next**  
- **Select root directory**: choose your project folder (or **Select archive file** if you have a .zip)  
- Ensure the project is found → **Finish**

Verify: `ShapeMatchingGame.java` exists at the expected path and its `package` matches its folder.

---

## 5) Add JavaFX 24 JARs (Non‑modular project)
JavaFX isn’t part of the JDK; add it to your **Build Path** and pass VM args at runtime.

1. Download **JavaFX SDK 24** and note its `lib` folder path. Examples:  
   - Windows: `C:\javafx-sdk-24\lib`  
   - macOS: `/Library/Java/javafx-sdk-24/lib` (or your chosen path)  
   - Homebrew macOS: `/opt/homebrew/opt/javafx/lib`

2. In Eclipse: **Project → Properties → Java Build Path → Libraries**  
   - You can add JavaFX jars to **Modulepath** (preferred even without modules) or to **Classpath** (either will work in this non‑modular run when we pass VM args).  
   - Click **Add External JARs…** and add **all JARs inside** your `javafx-sdk-24/lib` folder.  
   - **Apply and Close**.

> Tip: Create a **User Library** named `JavaFX24` pointing to `…/lib`, then add that library to your project for reuse.

---

## 6) Remove `module-info.java` (if present)
If your project contains `src/module-info.java`, **delete it** for this non‑modular setup.

---

## 7) Create a Run Configuration
**Run → Run Configurations… → Java Application → New**

**Main tab**  
- **Project:** select your project  
- **Main class:** use the fully‑qualified class name that matches your package, e.g.  
  - `shapematching.ShapeMatchingGame`  
  - or `com.littleluminaries.shapematching.ShapeMatchingGame`

**Arguments tab → VM arguments** (single line — **no carets `^`**):

- **Windows example:**
  ```
  --module-path "C:\javafx-sdk-24\lib" --add-modules=javafx.controls,javafx.graphics,javafx.media --enable-native-access=javafx.graphics
  ```

- **macOS/Linux example (adjust the path):**
  ```
  --module-path "/Library/Java/javafx-sdk-24/lib" --add-modules=javafx.controls,javafx.graphics,javafx.media --enable-native-access=javafx.graphics
  ```

Click **Apply** → **Run**.

**Why these flags?**  
- `--module-path` and `--add-modules` tell the JVM where to find JavaFX at runtime (even though our project is non‑modular).  
- `--enable-native-access=javafx.graphics` silences Java 24’s “restricted method” warning used internally by JavaFX.

---

## 8) First Run Checklist
- A window opens (e.g., “Shape Matching Game”).  
- Background (if any) renders; shapes and outlines appear.  
- You can **drag** a shape and **drop** it on its matching outline.  
- On correct drop: success animation / sound; on mismatch: gentle nudge / “try again” sound.

---

## 9) Troubleshooting (quick fixes)

**`Could not find or load main class shapematching.ShapeMatchingGame`**  
- **Run Config → Main class** must exactly match the package + class name.  
- Ensure the file path matches package: e.g., `src/shapematching/ShapeMatchingGame.java` with `package shapematching;`

**`ClassNotFoundException: ^`**  
- You pasted VM args with Windows line‑continuation `^`. In Eclipse, keep **one single line**; **remove `^`**.

**`NoClassDefFoundError: javafx/application/Application`**  
- JavaFX not on runtime path. Re‑check **VM arguments** and confirm the JavaFX jars were added to the project’s build path.

**Images don’t show**  
- Confirm files exist at the exact path your code uses.  
- For file URLs, files must be under `resources/` at project root.  
- For classpath loading (`getResource`), mark `resources/` as a **Source Folder** and use `/images/...` paths in code.  
- The Console will show URL/IO errors if a file is missing or malformed.

**Drag‑and‑drop not responding**  
- Ensure event handlers are attached to the **draggable** nodes and **drop targets** (`setOnDragDetected`, `setOnDragOver`, `setOnDragDropped`, etc.).  
- If using `Pane` overlays, set `node.setPickOnBounds(true)` when appropriate so mouse events hit transparent areas.  
- Make sure `acceptTransferModes(TransferMode.MOVE)` (or COPY) is called in the `setOnDragOver` handler of targets.  
- Keep references to nodes in scope (not recreated every frame) to avoid losing handlers.

**Audio doesn’t play**  
- Verify file format (MP3/WAV) and path.  
- Check the Console for `MediaPlayer` errors.  
- Ensure you added `javafx.media` in the `--add-modules` list.

**Build path confusion**  
- **Project → Clean…** then rebuild.  
- Make sure only one JDK (JDK 24) is active for the project.

---

## 10) Known‑Good VM Arguments (copy/paste)

**Windows:**
```
--module-path "C:\javafx-sdk-24\lib" --add-modules=javafx.controls,javafx.graphics,javafx.media --enable-native-access=javafx.graphics
```

**macOS (Homebrew JavaFX):**
```
--module-path "/opt/homebrew/opt/javafx/lib" --add-modules=javafx.controls,javafx.graphics,javafx.media --enable-native-access=javafx.graphics
```

**Linux (example):**
```
--module-path "/usr/lib/javafx/lib" --add-modules=javafx.controls,javafx.graphics,javafx.media --enable-native-access=javafx.graphics
```

---

## 11) Tips
- Keep **VM args** on a single line in Eclipse.  
- You may keep JavaFX jars on **Modulepath** or **Classpath** for non‑modular runs; both work when you pass the VM args.  
- Consider a **User Library** to avoid re‑adding JavaFX jars for every project.  
- If you later add a `module-info.java`, move JavaFX jars to **Modulepath** and keep the same VM args.

---

### You’re all set!
Run the **ShapeMatchingGame** configuration and enjoy matching! 🔷🔺⚪
