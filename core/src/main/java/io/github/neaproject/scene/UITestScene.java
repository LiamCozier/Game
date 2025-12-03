package io.github.neaproject.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.ShortArray;
import io.github.neaproject.UI.Panel;
import io.github.neaproject.input.CameraInputProcessor;

public class UITestScene extends Scene {

    OrthographicCamera camera;
    CameraInputProcessor cam_input;

    ShapeRenderer sr;
    Panel panel;
    Panel sub_panel;

    @Override
    public void on_open() {
        // camera
        float height = 20;
        float ppu = Gdx.graphics.getHeight() / height;
        float width = Gdx.graphics.getWidth() / ppu;
        camera = new OrthographicCamera(width, height);
        cam_input = new CameraInputProcessor(camera);
        Gdx.input.setInputProcessor(cam_input);

        sr = new ShapeRenderer();
        panel = new Panel(new Vector2(0,15), 5, 5, Color.WHITE, Color.GRAY);
        sub_panel = new Panel(new Vector2(0.5f,3f), 4, 1, Color.GRAY, Color.GRAY, panel);
    }

    @Override
    public void update(float dt) {
        input();
        panel.translate(new Vector2(dt, 0));
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.15f, 0);

        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Color.WHITE);
        sr.rect(0, 0, 1, 1);
        sr.end();
        sr.begin(ShapeRenderer.ShapeType.Filled);
        panel.render(sr, camera);
        sub_panel.render(sr, camera);
        sr.end();

    }

    public void draw_filled_polygon(ShapeRenderer sr, float[] verts, Color color) {
        EarClippingTriangulator triangulator = new EarClippingTriangulator();
        ShortArray tris = triangulator.computeTriangles(new FloatArray(verts));

        sr.setColor(color);

        for (int i = 0; i < tris.size; i += 3) {
            int i1 = tris.get(i) * 2;
            int i2 = tris.get(i + 1) * 2;
            int i3 = tris.get(i + 2) * 2;

            sr.triangle(
                verts[i1],     verts[i1 + 1],
                verts[i2],     verts[i2 + 1],
                verts[i3],     verts[i3 + 1]
            );
        }
    }

    public void input() {

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
