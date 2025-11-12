package io.github.neaproject;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.neaproject.physics.CollisionManifold;
import io.github.neaproject.physics.Polygon;
import io.github.neaproject.physics.shape.BoxShape;
import io.github.neaproject.physics.PhysicsManager;
import io.github.neaproject.physics.RigidBody;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    OrthographicCamera camera;
    ShapeRenderer sr;
    RigidBody box1;
    RigidBody box2;

    @Override
    public void create() {

        float height = 5;
        float ppu = Gdx.graphics.getHeight() / height;
        float width = Gdx.graphics.getWidth() / ppu;
        camera = new OrthographicCamera(width, height);

        sr = new ShapeRenderer();

        box1 = new RigidBody(
            new Vector2(-0f, 5),
            new Vector2(0f, -1f),
            new BoxShape(1, 1),
            (float)Math.PI*0f,
            (float)Math.PI*0.25f,
            1
        );

        box2 = new RigidBody(
            new Vector2(0, -0.5f),
            Vector2.Zero.cpy(),
            new BoxShape(10, 1),
            (float)Math.PI*0f,
            (float)Math.PI*-0.01f,
            1
        );
    }

    @Override
    public void render() {
        float deltaT = Gdx.graphics.getDeltaTime();
        box1.physics_tick(deltaT);
        box2.physics_tick(deltaT);

        camera_input();

        ScreenUtils.clear(0.18f, 0.24f, 0.29f, 1);
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setProjectionMatrix(camera.combined);

        Polygon p1 = box1.get_polygon();
        Polygon p2 = box2.get_polygon();

        sr.setColor(Color.WHITE);
        CollisionManifold cm = PhysicsManager.SAT_overlap(p1, p2);
        if (cm.minimum_penetration_depth > 0) {
            System.out.println(cm.contact_points.length);
            sr.setColor(Color.RED);
            for (Vector2 cv: cm.contact_points) sr.polygon(new float[]{
                cv.x + 0.025f, cv.y + 0.025f,
                cv.x + 0.025f, cv.y - 0.025f,
                cv.x - 0.025f, cv.y - 0.025f,
                cv.x - 0.025f, cv.y + 0.025f
            });
            sr.setColor(Color.GREEN);
            box1.position.mulAdd(cm.collision_normal, -cm.minimum_penetration_depth);
        }

        sr.polygon(box1.get_polygon().get_float_array());
        sr.polygon(box2.get_polygon().get_float_array());

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
