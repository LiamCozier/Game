package io.github.neaproject;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.neaproject.physics.*;
import io.github.neaproject.physics.shape.BoxShape;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    OrthographicCamera camera;
    PhysicsWorld pw;
    ShapeRenderer sr;
    RigidBody box1;
    RigidBody box2;

    @Override
    public void create() {

        float height = 20;
        float ppu = Gdx.graphics.getHeight() / height;
        float width = Gdx.graphics.getWidth() / ppu;
        camera = new OrthographicCamera(width, height);

        sr = new ShapeRenderer();

        pw = new PhysicsWorld();

        box1 = new RigidBody(
            new Vector2(-5f, 5f),
            new Vector2(5f, 5f),
            new BoxShape(1, 1),
            (float)Math.PI*0f,
            (float)Math.PI*0f,
            1,
            true
        );

        box2 = new RigidBody(
            new Vector2(0f, -5f),
            new Vector2(0f, 0f),
            new BoxShape(50, 6),
            (float)Math.PI*0f,
            (float)Math.PI*0f,
            0,
            false
        );

        pw.add_body(box1);
        pw.add_body(box2);
    }



    @Override
    public void render() {


        float delta_time = Gdx.graphics.getDeltaTime();
         pw.physics_tick(delta_time);


        camera_input();

        ScreenUtils.clear(0.18f, 0.24f, 0.29f, 1);
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.polygon(box1.get_polygon().get_float_array());
        sr.polygon(box2.get_polygon().get_float_array());
        sr.setProjectionMatrix(camera.combined);


        for (RigidBody r: pw.get_bodies()) {
            sr.polygon(r.get_polygon().get_float_array());
        }
        sr.end();
    }

    private void debugBody(String label, RigidBody body) {
        System.out.printf(
            "%s | Pos(%.3f, %.3f)  Vel(%.3f, %.3f)  Angle=%.3f  AngVel=%.3f%n",
            label,
            body.position.x, body.position.y,
            body.velocity.x, body.velocity.y,
            body.orientation, body.angular_velocity
        );
    }

    private void debugContact(CollisionManifold cm) {
        System.out.printf(
            "    Contact: depth=%.5f  normal(%.3f, %.3f)  points=%d%n",
            cm.minimum_penetration_depth,
            cm.collision_normal.x, cm.collision_normal.y,
            cm.contact_points.length
        );

        for (int i = 0; i < cm.contact_points.length; i++) {
            Vector2 p = cm.contact_points[i];
            System.out.printf("        P%d (%.3f, %.3f)%n", i, p.x, p.y);
        }
    }



    public void camera_input() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0, 0.25f);
            camera.update();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0, -0.25f);
            camera.update();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-0.25f, 0);
            camera.update();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(0.25f, 0);
            camera.update();
        }
    }

    @Override
    public void dispose() {
    }
}
