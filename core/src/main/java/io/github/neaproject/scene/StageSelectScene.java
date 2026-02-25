package io.github.neaproject.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.neaproject.UI.UIManager;
import io.github.neaproject.UI.elements.*;
import io.github.neaproject.input.UIInputProcessor;

public class StageSelectScene extends Scene {

    public StageSelectScene(SceneManager manager) {
        super(manager);
    }

    OrthographicCamera camera;
    OrthographicCamera ui_camera;

    UIInputProcessor ui_input;
    UIManager ui_manager;

    ShapeRenderer sr;
    SpriteBatch batch;

    private void init_ui() {
        ui_manager = new UIManager();

        Panel root_panel = new Panel("root_panel", new Vector2(0,0), 600, 1080, new Color(0.1f, 0.1f, 0.1f, 0.6f));

        FileHandle dir = Gdx.files.local("scenes");
        if (!dir.exists()) dir.mkdirs();

        FileHandle[] files = dir.list("json");

        float y = 120f;

        for (FileHandle file : files) {

            String name = file.nameWithoutExtension();

            Button file_button = new Button("file_button_" + name, new Vector2(30, y), 540, 150, Color.DARK_GRAY.cpy(), root_panel);
            new TextBox("file_text_" + name, new Vector2(0,0), 540, 150, name, 0.2f, Align.center, Color.WHITE, file_button);

            String path = file.path();
            file_button.set_release_action(() -> manager.set_scene(new StagePlayScene(manager, path)));

            y += 180f;
        }

        Button back_button = new Button("back_button", new Vector2(30, 900), 540, 150, Color.DARK_GRAY.cpy(), root_panel);
        new TextBox("back_button_text", new Vector2(0,0), 540, 150, "Back", 0.2f, Align.center, Color.WHITE, back_button);
        back_button.set_release_action(() -> manager.set_scene(new MainMenuScene(manager)));

        ui_manager.add_node(root_panel);
    }

    @Override
    public void on_open() {
        // cameras
        float height = 20;
        float ppu = Gdx.graphics.getHeight() / height;
        float width = Gdx.graphics.getWidth() / ppu;
        camera = new OrthographicCamera(width, height);

        float ui_height = 1080;
        float ui_ppu = Gdx.graphics.getHeight() / ui_height;
        float ui_width = Gdx.graphics.getWidth() / ui_ppu;
        ui_camera = new OrthographicCamera(ui_width, ui_height);
        ui_camera.translate(new Vector2(ui_width, -ui_height).scl(0.5f));
        ui_camera.update();

        init_ui();

        ui_input = new UIInputProcessor();
        Gdx.input.setInputProcessor(ui_input);

        sr = new ShapeRenderer();
        batch = new SpriteBatch();
    }

    @Override
    public void update(float dt) {
        ui_input.begin_frame();
        ui_manager.take_input(ui_input, ui_camera);
    }

    @Override
    public void render() {

        // enable alpha channel
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        ScreenUtils.clear(0f, 0f, 0f, 1f);

        sr.setProjectionMatrix(ui_camera.combined);
        batch.setProjectionMatrix(ui_camera.combined);

        ui_manager.render_all(sr, batch);
    }

    @Override
    public void on_close() {
        sr.dispose();
        batch.dispose();
    }
}
