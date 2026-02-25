package io.github.neaproject.scene;

import com.badlogic.gdx.Gdx;
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
import io.github.neaproject.physics.RigidBody;
import io.github.neaproject.physics.Stage;
import io.github.neaproject.physics.shape.BoxShape;

import java.util.Random;

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

    Stage stage;
    float spawn_timer;



    private void init_ui() {
        ui_manager = new UIManager();

        Panel root_panel = new Panel("root_panel", new Vector2(0,0), 600, 1080, new Color(0.1f, 0.1f, 0.1f, 0.6f));

        Button load_button = new Button("load_button", new Vector2(30, 360), 540, 150, Color.DARK_GRAY.cpy(), root_panel);
        new TextBox("load_button_text", new Vector2(0,0), 540, 150, "Load Stage", 0.2f, Align.center, Color.WHITE, load_button);
        load_button.set_release_action(() -> manager.set_scene(new StageSelectScene(manager)));

        Button edit_button = new Button("edit_button", new Vector2(30, 540), 540, 150, Color.DARK_GRAY.cpy(), root_panel);
        new TextBox("edit_button_text", new Vector2(0,0), 540, 150, "Edit Stage", 0.2f, Align.center, Color.WHITE, edit_button);
        edit_button.set_release_action(() -> manager.set_scene(new StageEditorScene(manager)));

        Button settings_button = new Button("settings_button", new Vector2(30, 720), 540, 150, Color.DARK_GRAY.cpy(), root_panel);
        new TextBox("settings_button_text", new Vector2(0,0), 540, 150, "Settings", 0.2f, Align.center, Color.WHITE, settings_button);

        Button exit_button = new Button("exit_button", new Vector2(30, 900), 540, 150, Color.DARK_GRAY.cpy(), root_panel);
        new TextBox("exit_button_text", new Vector2(0,0), 540, 150, "Exit to desktop", 0.2f, Align.center, Color.WHITE, exit_button);
        exit_button.set_release_action(() -> Gdx.app.exit());

        ui_manager.add_node(root_panel);
    }

    private void spawn_box() {
        Random r = new Random();

        float x_vel = r.nextFloat(7.5f);
        float x = -20;

        if (r.nextBoolean()) {
            x *= -1;
            x_vel *= -1;
        }

        stage.add_runtime_body(new RigidBody(
            new Vector2(x, 20),
            new Vector2(x_vel, r.nextFloat(10f)+5),
            new BoxShape(r.nextFloat(2.5f)+1, r.nextFloat(2.5f)+1),
            0f, r.nextFloat(50f),
            1f, true
        ));
    }

    private void cleanup_bodies() {
        RigidBody[] bodies = stage.get_world().get_bodies();

        for (RigidBody body : bodies) {
            if (body.position.y < -25f || body.position.x > 40f) {
                stage.get_world().remove_body(body);
            }
        }
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

        stage = new Stage();
        spawn_timer = 0f;

        // big slope
        stage.add_body(new RigidBody(
            new Vector2(5f, -17f),
            new Vector2(0, 0),
            new BoxShape(80f, 20f),
            (float) Math.toRadians(-15f),
            0f, 0f,
             false
        ));

        stage.play();
    }

    @Override
    public void update(float dt) {
        ui_input.begin_frame();
        ui_manager.take_input(ui_input, ui_camera);

        spawn_timer += dt;
        if (spawn_timer > 0.175f) {
            spawn_timer = 0f;
            spawn_box();
        }

        stage.tick(dt);
        cleanup_bodies();
    }

    @Override
    public void render() {

        // enable alpha channel
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1f);

        sr.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);
        stage.render(sr, batch);

        sr.setProjectionMatrix(ui_camera.combined);
        batch.setProjectionMatrix(ui_camera.combined);

        ui_manager.render_all(sr, batch);
    }

    @Override
    public void on_close() {

    }
}
