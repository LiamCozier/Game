package io.github.neaproject;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import io.github.neaproject.scene.*;

/** {@link ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    Scene s;

    @Override
    public void create() {
        s = new UITestScene();
        s.on_open();
    }

    @Override
    public void render() {
        s.update(Gdx.graphics.getDeltaTime());
        s.render();
    }

    @Override
    public void dispose() {
    }
}
