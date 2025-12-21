package io.github.neaproject.scene;

public class SceneManager {

    public Scene scene;

    public SceneManager() {
        scene = null;
    }

    public SceneManager(Scene initial_scene) {
        set_scene(initial_scene);
    }

    public void set_scene(Scene scene) {

        if (this.scene != null) close_scene();

        this.scene = scene;
        this.scene.on_open();
    }

    public void close_scene() {
        this.scene.on_close();
        this.scene = null;
    }
}
