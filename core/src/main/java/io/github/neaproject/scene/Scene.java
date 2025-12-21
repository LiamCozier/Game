package io.github.neaproject.scene;

public abstract class Scene {

    SceneManager manager;

    public Scene(SceneManager manager) {
        this.manager = manager;
    }

    public abstract void on_open();
    public abstract void update(float dt);
    public abstract void render();
    public abstract void on_close();

}
