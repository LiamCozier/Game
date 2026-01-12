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
import io.github.neaproject.UI.elements.Button;
import io.github.neaproject.UI.elements.Panel;
import io.github.neaproject.UI.elements.Switch;
import io.github.neaproject.input.UIInputProcessor;
import io.github.neaproject.physics.PhysicsWorld;
import io.github.neaproject.physics.RigidBody;
import io.github.neaproject.physics.shape.BoxShape;

public class StageEditorScene extends Scene{

    public StageEditorScene(SceneManager manager) {super(manager);}

    OrthographicCamera camera;
    OrthographicCamera ui_camera;

    UIInputProcessor ui_input;
    UIManager ui_manager;

    ShapeRenderer sr;
    SpriteBatch batch;

    // physics
    PhysicsWorld physics_world;

    private void init_ui() {
        ui_manager = new UIManager();

        Panel sidebar = new Panel(new Vector2(-85,115), 85, 850, new Color(0.2f, 0.2f, 0.2f, 1f));
        Switch sidebar_switch = new Switch(new Vector2(85,375), 25, 100, new Color(0.2f, 0.2f, 0.2f, 1f), 2, sidebar);
        sidebar_switch.set_state_action(0, () -> sidebar.translate(new Vector2(85, 0)));
        sidebar_switch.set_state_action(1, () -> sidebar.translate(new Vector2(-85, 0)));

        // sidebar buttons
        Button tool_button = new Button(new Vector2(8, 8f), 64, 64, new Color(0.4f, 0.4f, 0.4f, 1), sidebar);

        ui_manager.add_node(sidebar);
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

        physics_world = new PhysicsWorld();

        physics_world.add_body(new RigidBody(
            new Vector2(0, 0),
            new Vector2(0, 25),
            new BoxShape(2, 2),
            0, 3.14f,
            1, true
        ));

        physics_world.add_body(new RigidBody(
            new Vector2(0, -5),
            new Vector2(0, 0),
            new BoxShape(5, 1),
            0, 0,
            0, true
        ));
    }

    @Override
    public void update(float dt) {
        ui_input.begin_frame();
        ui_manager.take_input(ui_input, ui_camera);
        physics_world.physics_tick(dt);
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

        sr.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.WHITE);
        for (RigidBody body: physics_world.get_bodies()) {
            sr.polygon(body.get_polygon().get_float_array());
        }
        sr.end();

    }

    @Override
    public void on_close() {

    }
}
