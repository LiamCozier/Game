package io.github.neaproject;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.neaproject.scene.*;

/** {@link ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    SceneManager manager;

    @Override
    public void create() {
        manager = new SceneManager();
        manager.set_scene(new MainMenuScene(manager));
    }

    @Override
    public void render() {
        // temp
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) Gdx.app.exit();

        manager.scene.update(Gdx.graphics.getDeltaTime());
        manager.scene.render();
    }

    @Override
    public void dispose() {
        manager.close_scene();
    }
}
