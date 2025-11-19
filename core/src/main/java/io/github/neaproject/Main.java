package io.github.neaproject;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.neaproject.physics.*;
import io.github.neaproject.physics.shape.BoxShape;

import java.util.ArrayList;
import java.util.List;

/** {@link ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    OrthographicCamera camera;

    ShapeRenderer sr;
    RigidBody box1;
    RigidBody box2;
    RigidBody box3;
    List<Vector2> contact_points;

    @Override
    public void create() {

        contact_points = new ArrayList<Vector2>(0);

        float height = 40;
        float ppu = Gdx.graphics.getHeight() / height;
        float width = Gdx.graphics.getWidth() / ppu;
        camera = new OrthographicCamera(width, height);

        sr = new ShapeRenderer();



        box1 = new RigidBody(
            new Vector2(-20f, 10f),
            new Vector2(10f, 20f),
            new BoxShape(5, 5),
            (float)Math.PI*0f,
            (float)Math.PI*0f,
            1,
            true
        );

        box2 = new RigidBody(
            new Vector2(0f, -5f),
            new Vector2(0f, 0f),
            new BoxShape(50, 6),
            (float)Math.PI*-0.01f,
            (float)Math.PI*-1e-2f,
            0,
            false
        );

        box3 = new RigidBody(
            new Vector2(28f, 17f),
            new Vector2(0f, 0f),
            new BoxShape(6, 50),
            (float)Math.PI*0f,
            (float)Math.PI*0f,
            0,
            false
        );

    }



    @Override
    public void render() {

        CollisionManifold cm = null;

        int divisions = 10;
        int iterations = 25;
        float delta_time = Gdx.graphics.getDeltaTime() / divisions;
        for (int i=0; i<divisions; i++) {
            box1.physics_tick(delta_time);
            box2.physics_tick(delta_time);

            for (int j=0; j<iterations; j++) {
                cm = PhysicsManager.sat_overlap(box1.get_polygon(), box2.get_polygon());
                contact_points.addAll(List.of(cm.contact_points));
                PhysicsManager.resolve_collision(box1, box2, cm);

                cm = PhysicsManager.sat_overlap(box1.get_polygon(), box3.get_polygon());
                contact_points.addAll(List.of(cm.contact_points));
                PhysicsManager.resolve_collision(box1, box3, cm);
            }
        }







        camera_input();

        ScreenUtils.clear(0.18f, 0.24f, 0.29f, 1);
        sr.begin(ShapeRenderer.ShapeType.Line);

        sr.setColor(Color.WHITE);
        sr.polygon(box1.get_polygon().get_float_array());
        sr.polygon(box2.get_polygon().get_float_array());
        sr.polygon(box3.get_polygon().get_float_array());
        sr.setProjectionMatrix(camera.combined);
        sr.end();


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
