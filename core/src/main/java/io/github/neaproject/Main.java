package io.github.neaproject;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.neaproject.physics.*;
import io.github.neaproject.physics.shape.BoxShape;

import java.util.List;

/** {@link ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    OrthographicCamera camera;
    boolean drag = false;

    ShapeRenderer sr;
    PhysicsWorld world;
    RigidBody box1;
    Vector2 start_position;


    @Override
    public void create() {
        world = new PhysicsWorld();

        float height = 40;
        float ppu = Gdx.graphics.getHeight() / height;
        float width = Gdx.graphics.getWidth() / ppu;
        camera = new OrthographicCamera(width, height);

        sr = new ShapeRenderer();

        box1 = new RigidBody(
            new Vector2(-20f, 10f),
            new Vector2(20f, 20f),
            new BoxShape(5, 5),
            (float)Math.PI*-0.25f,
            (float)Math.PI*-25f,
            25,
            true
        );

        RigidBody box2 = new RigidBody(
            new Vector2(-15f, -5f),
            new Vector2(0f, 0f),
            new BoxShape(50, 6),
            (float)Math.PI*-0.25f,
            (float)Math.PI*0f,
            0,
            false
        );

        RigidBody box3 = new RigidBody(
            new Vector2(25f, -10f),
            new Vector2(0f, 0f),
            new BoxShape(50, 6),
            (float)Math.PI*0f,
            (float)Math.PI*0f,
            0,
            false
        );

        world.add_body(box1);
        world.add_body(box2);
        world.add_body(box3);

    }



    @Override
    public void render() {
        float delta_time = Gdx.graphics.getDeltaTime();
        world.physics_tick(delta_time);

        camera_input();


        ScreenUtils.clear(0.18f, 0.24f, 0.29f, 1);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Line);

        sr.setColor(Color.WHITE);

        RigidBody[] bodies = world.get_bodies();
        for (RigidBody body: bodies) {
            sr.polygon(body.get_polygon().get_float_array());
        }
        sr.setColor(Color.CHARTREUSE);
        sr.polygon(box1.get_polygon().get_float_array());

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
