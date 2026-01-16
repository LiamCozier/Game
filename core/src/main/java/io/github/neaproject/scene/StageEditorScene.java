package io.github.neaproject.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.neaproject.UI.UIAnimator;
import io.github.neaproject.UI.UIManager;
import io.github.neaproject.UI.elements.Button;
import io.github.neaproject.UI.elements.Panel;
import io.github.neaproject.UI.elements.Switch;
import io.github.neaproject.UI.elements.TextBox;
import io.github.neaproject.editor.tools.EditorToolbox;
import io.github.neaproject.input.EditorInputProcessor;
import io.github.neaproject.input.UIInputProcessor;
import io.github.neaproject.physics.Stage;

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

    // physics
    Stage stage;

    private void init_ui() {
        ui_manager = new UIManager();

        Panel sidebar = new Panel("sidebar", new Vector2(-80,140), 80, 800, new Color(0.2f, 0.2f, 0.2f, 1f));
        Switch sidebar_switch = new Switch("sidebar_switch", new Vector2(80,336), 23, 128, new Color(0.2f, 0.2f, 0.2f, 1f), 2, sidebar);
        sidebar_switch.set_state_action(0, () -> sidebar.animator.translate(UIAnimator.EasingType.ExponentialOut, new Vector2(80, 0), 0.5f));
        sidebar_switch.set_state_action(1, () -> sidebar.animator.translate(UIAnimator.EasingType.ExponentialOut, new Vector2(-80, 0), 0.5f));

        // sidebar buttons
        Button create_tool_button = new Button("create_tool_button", new Vector2(8, 8f), 64, 64, new Color(0.4f, 0.4f, 0.4f, 1), sidebar);
        create_tool_button.set_release_action(() -> toolbox.set_tool(EditorToolbox.CREATE_BODY));
        Button select_tool_button = new Button("select_tool_button", new Vector2(8, 80f), 64, 64, new Color(0.4f, 0.4f, 0.4f, 1), sidebar);
        select_tool_button.set_release_action(() -> toolbox.set_tool(EditorToolbox.SELECT_BODY));

        TextBox info_text = new TextBox("info_text", new Vector2(8, 152), 64, 64, "fart", new Color(1, 1, 1, 1), 0.125f, Align.center, sidebar);


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
        editor_input = new EditorInputProcessor();

        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(ui_input);
        multiplexer.addProcessor(editor_input);
        Gdx.input.setInputProcessor(multiplexer);


        sr = new ShapeRenderer();
        batch = new SpriteBatch();

        stage = new Stage();

        toolbox = new EditorToolbox(stage, editor_input, camera);
        toolbox.set_tool(1);
    }

    @Override
    public void update(float dt) {

        ui_input.begin_frame();
        editor_input.begin_frame();

        ui_manager.tick(dt);
        ui_manager.take_input(ui_input, ui_camera);

        if (!ui_manager.input_captured) toolbox.update();

        stage.tick(dt);
        TextBox info_text = (TextBox) ui_manager.get_node("info_text");

        info_text.set_text(String.valueOf(stage.world.get_body_count()));

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
