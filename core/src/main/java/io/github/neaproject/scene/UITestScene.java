package io.github.neaproject.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.ShortArray;
import io.github.neaproject.UI.*;
import io.github.neaproject.input.CameraInputProcessor;
import io.github.neaproject.input.UIInputProcessor;

public class UITestScene extends Scene {

    OrthographicCamera camera;
    OrthographicCamera ui_camera;

    CameraInputProcessor cam_input;
    UIInputProcessor ui_input;
    InputMultiplexer multiplexer;

    ShapeRenderer sr;
    SpriteBatch batch;


    Panel panel;
    TextBox text;
    Button button;
    UIManager ui_manager;



    @Override
    public void on_open() {
        // camera
        float height = 20;
        float ppu = Gdx.graphics.getHeight() / height;
        float width = Gdx.graphics.getWidth() / ppu;
        camera = new OrthographicCamera(width, height);

        ui_camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ui_camera.translate(new Vector2(Gdx.graphics.getWidth(), -Gdx.graphics.getHeight()).scl(0.5f));
        ui_camera.zoom = 1f;
        ui_camera.update();

        sr = new ShapeRenderer();
        batch = new SpriteBatch();

        ui_manager = new UIManager();
        panel = new Panel(new Vector2(10, 10), 300, 300, Color.WHITE);
        text = new TextBox(new Vector2(20, 20), 260, 260, "The button.", Control.DARK_GREY, panel);
        button = new Button(new Vector2(20, 60), 260, 220, Color.DARK_GRAY, panel);

        ui_manager.add_node(panel);

        ui_input = new UIInputProcessor();
        cam_input = new CameraInputProcessor();
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(ui_input);
        multiplexer.addProcessor(cam_input);
        Gdx.input.setInputProcessor(multiplexer);

    }


    @Override
    public void update(float dt) {

        ui_manager.take_input(ui_input, ui_camera);
        input();

    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.15f, 0);

        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Color.WHITE);
        sr.rect(0, 0, 1, 1);
        sr.end();

        // render UI
        sr.setProjectionMatrix(ui_camera.combined);
        batch.setProjectionMatrix(ui_camera.combined);
        ui_manager.render_all(sr, batch);

    }

    public void
    input() {

        // pan camera
        if (cam_input.middle_pressed) {
            // move by (-x, y) delta because you 'drag' against the delta
            // (positive Y because screen space is defined Y-down but world is Y-up)
            Vector2 camera_delta = new Vector2(-cam_input.delta.x, cam_input.delta.y).scl(camera.zoom);
            // prevent drift from no mouse movement
            cam_input.delta.set(0,0);

            float inv_ppu = (float) camera.viewportHeight / Gdx.graphics.getHeight();
            camera_delta.scl(inv_ppu);

            camera.translate(camera_delta.x, camera_delta.y);
            camera.update();
        }

        // zoom camera
        if (cam_input.scroll_amount != 0) {

                Vector3 mouse_before = camera.unproject(new Vector3(cam_input.mouse_screen.x, cam_input.mouse_screen.y, 0));

                // zoom
                float zoomFactor = (float) Math.pow(1.1f, cam_input.scroll_amount);
                camera.zoom *= zoomFactor;
                camera.update();

                Vector3 mouse_after = camera.unproject(
                    new Vector3(cam_input.mouse_screen.x, cam_input.mouse_screen.y, 0)
                );

                // world-space
                float dx = mouse_before.x - mouse_after.x;
                float dy = mouse_before.y - mouse_after.y;

                // keep mouse on zoom centre
                camera.position.add(dx, dy, 0);
                camera.update();

                // prevent re-applying scroll
                cam_input.scroll_amount = 0;
            }
    }

    @Override
    public void on_close() {}
}
