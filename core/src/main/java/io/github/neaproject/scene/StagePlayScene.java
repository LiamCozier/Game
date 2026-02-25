package io.github.neaproject.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.neaproject.editor.StageSaveLoad;
import io.github.neaproject.physics.Stage;

public class StagePlayScene extends Scene {

    public StagePlayScene(SceneManager manager, String path) {
        super(manager);
        this.path = path;
    }

    OrthographicCamera camera;

    ShapeRenderer sr;
    SpriteBatch batch;

    Stage stage;

    String path;

    @Override
    public void on_open() {
        // cameras
        float height = 20;
        float ppu = Gdx.graphics.getHeight() / height;
        float width = Gdx.graphics.getWidth() / ppu;
        camera = new OrthographicCamera(width, height);

        sr = new ShapeRenderer();
        batch = new SpriteBatch();

        stage = StageSaveLoad.load(path);
        stage.play();
    }

    @Override
    public void update(float dt) {
        stage.tick(dt);
    }

    @Override
    public void render() {
        // enable alpha channel
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        ScreenUtils.clear(0f, 0f, 0f, 1f);

        sr.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        stage.render(sr, batch);
    }

    @Override
    public void on_close() {
        sr.dispose();
        batch.dispose();
    }
}
