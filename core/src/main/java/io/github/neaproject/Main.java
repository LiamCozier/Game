package io.github.neaproject;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import io.github.neaproject.scene.TestScene;

/** {@link ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    TestScene s;

    @Override
    public void create() {
        s = new TestScene();
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
