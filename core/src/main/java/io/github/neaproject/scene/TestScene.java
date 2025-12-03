package io.github.neaproject.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.ShortArray;
import io.github.neaproject.input.CameraInputProcessor;
import io.github.neaproject.physics.PhysicsWorld;
import io.github.neaproject.physics.RigidBody;
import io.github.neaproject.physics.shape.PolygonShape;
import io.github.neaproject.physics.tools.BoxCreationTool;
import io.github.neaproject.physics.tools.BoxDeletionTool;
import io.github.neaproject.physics.tools.InputToolbox;

import java.util.ArrayList;
import java.util.List;

public class TestScene extends Scene{

    OrthographicCamera camera;
    CameraInputProcessor cam_input;
    InputToolbox toolbox;
    BoxCreationTool boxTool;

    ShapeRenderer sr;
    PhysicsWorld world;

    // init tools
    BoxCreationTool CREATE_BOX;
    BoxDeletionTool DELETE_BOX;
    int current_tool = 0;

    @Override
    public void on_open() {
        // camera
        float height = 20;
        float ppu = Gdx.graphics.getHeight() / height;
        float width = Gdx.graphics.getWidth() / ppu;
        camera = new OrthographicCamera(width, height);

        cam_input = new CameraInputProcessor(camera);
        Gdx.input.setInputProcessor(cam_input);

        world = new PhysicsWorld();


        sr = new ShapeRenderer();

        toolbox = new InputToolbox();
        boxTool = new BoxCreationTool(camera, world);

        toolbox.set_tool(boxTool);

        CREATE_BOX = new BoxCreationTool(camera, world);
        DELETE_BOX = new BoxDeletionTool(camera, world);
    }

    @Override
    public void update(float dt) {
        float delta_time = Gdx.graphics.getDeltaTime();
        world.physics_tick(delta_time);
        toolbox.update(delta_time);
        input();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.18f, 0.24f, 0.29f, 1);

        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Color.WHITE);
        RigidBody[] bodies = world.get_bodies();
        for (RigidBody body: bodies) {
            draw_filled_polygon(sr, body.get_polygon().get_float_array(), Color.WHITE);
        }
        toolbox.render(sr);
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


        // tools
        for (int i = 0; i < 9; i++) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
                current_tool = i + 1;
                break;
            }
        }
        switch (current_tool) {
            case 0:
                toolbox.set_tool(null);
                break;
            case 1:
                toolbox.set_tool(CREATE_BOX);
                break;
            case 2:
                toolbox.set_tool(DELETE_BOX);
                break;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            float radius = 3f;
            List<Vector2> verts = new ArrayList<>(0);
            for (float o = 0; o<2f*Math.PI; o+= (float) (Math.PI/64f)) {
                float sin_a = (float) Math.sin(-o);
                float cos_a = (float) Math.cos(-o);

                float rotated_x =  cos_a - sin_a;
                float rotated_y =  cos_a + sin_a;

                verts.add(new Vector2(rotated_x, rotated_y).scl(radius));
            }
            Vector3 temp = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            Vector2 position = new Vector2(temp.x, temp.y);
            world.add_body(new RigidBody(
                position,
                new Vector2(0, 5),
                new PolygonShape(verts.toArray(verts.toArray(new Vector2[0]))),
                0, (float) Math.PI * 0,
                (float)Math.PI * radius * radius, true
            ));
        }
    }

    @Override
    public void on_close() {}
}
