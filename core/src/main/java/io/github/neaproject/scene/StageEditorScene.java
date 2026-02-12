package io.github.neaproject.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.neaproject.UI.UIManager;
import io.github.neaproject.UI.elements.*;
import io.github.neaproject.UI.elements.composite.*;
import io.github.neaproject.editor.tools.EditorToolbox;
import io.github.neaproject.input.EditorInputProcessor;
import io.github.neaproject.input.UIInputProcessor;
import io.github.neaproject.physics.RigidBody;
import io.github.neaproject.physics.Stage;
import io.github.neaproject.physics.shape.BoxShape;

public class StageEditorScene extends Scene {

    public StageEditorScene(SceneManager manager) {super(manager);}

    OrthographicCamera camera;
    OrthographicCamera ui_camera;

    UIInputProcessor ui_input;
    EditorInputProcessor editor_input;
    InputMultiplexer multiplexer;

    UIManager ui_manager;

    ShapeRenderer sr;
    SpriteBatch batch;

    EditorToolbox toolbox;
    BodyEditor body_editor;

    // physics
    Stage stage;
    Switch pp_switch;

    private void init_ui() {
        Panel sidebar = Sidebar.editor_tool_sidebar(toolbox);



        ui_manager.add_node(sidebar);
        Button reset_button = (Button) ui_manager.get_node("reset_button");
        reset_button.set_release_action(this::reset_simulation);

        pp_switch = (Switch) ui_manager.get_node("play_pause_switch");
        pp_switch.set_state_action(0, this::play_simulation);
        pp_switch.set_state_action(1, this::pause_simulation);
    }

    public void pause_simulation() {
        stage.pause();
    }

    public void play_simulation() {
        stage.play();
    }

    public void reset_simulation() {
        stage.reset_world();
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

        ui_input = new UIInputProcessor();
        editor_input = new EditorInputProcessor();

        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(ui_input);
        multiplexer.addProcessor(editor_input);
        Gdx.input.setInputProcessor(multiplexer);


        sr = new ShapeRenderer();
        batch = new SpriteBatch();

        stage = new Stage();

        stage.add_body(new RigidBody(
            new Vector2(0, -5),
            new Vector2(0, 0),
            new BoxShape(20, 1),
            0f, 0f,
            0f, false
        ));

        ui_manager = new UIManager();
        body_editor = new BodyEditor(ui_manager, stage);
        body_editor.hide();

        toolbox = new EditorToolbox(stage, editor_input, camera, ui_manager, body_editor);
        toolbox.set_tool(1);

        init_ui();

    }

    @Override
    public void update(float dt) {

        ui_input.begin_frame();
        editor_input.begin_frame();

        ui_manager.tick(dt);
        ui_manager.take_input(ui_input, ui_camera);

        if (!ui_manager.input_captured) toolbox.update();

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
