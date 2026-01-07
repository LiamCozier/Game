package io.github.neaproject.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.neaproject.UI.UIManager;
import io.github.neaproject.UI.elements.*;
import io.github.neaproject.input.UIInputProcessor;

public class MainMenuScene extends Scene {

    public MainMenuScene(SceneManager manager) {
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

        Panel root_panel = new Panel(new Vector2(0,0), 600, 1080, new Color(0.1f, 0.1f, 0.1f, 0.6f));
        Button button1 = new Button(new Vector2(30, 360), 540, 150, Color.DARK_GRAY.cpy(), root_panel);
        button1.set_release_action(() -> manager.set_scene(new TestScene(manager)));
        Button button2 = new Button(new Vector2(30, 540), 540, 150, Color.DARK_GRAY.cpy(), root_panel);
        Button button3 = new Button(new Vector2(30, 720), 540, 150, Color.DARK_GRAY.cpy(), root_panel);
        Button exit_button = new Button(new Vector2(30, 900), 540, 150, Color.DARK_GRAY.cpy(), root_panel);
        new TextBox(new Vector2(0,0), 540, 150, "Exit to desktop", Color.WHITE, 0.2f, exit_button);
        exit_button.set_release_action(() -> Gdx.app.exit());

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

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Color.BLUE);
        sr.rect(0, -1080, 1920, 120);
        sr.end();

        ui_manager.render_all(sr, batch);
    }

    @Override
    public void on_close() {

    }
}
