package io.github.neaproject;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.neaproject.physics.shape.BoxShape;
import io.github.neaproject.physics.PhysicsManager;
import io.github.neaproject.physics.RigidBody;
import io.github.neaproject.physics.shape.PolygonShape;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    OrthographicCamera camera;
    ShapeRenderer sr;
    RigidBody box;

    @Override
    public void create() {

        float height = 20;
        float ppu = Gdx.graphics.getHeight() / height;
        float width = Gdx.graphics.getWidth() / ppu;
        camera = new OrthographicCamera(width, height);

        sr = new ShapeRenderer();

        box = new RigidBody(
            new Vector2(6, 9),
            Vector2.Zero.cpy(),
            new BoxShape(5, 5),
            (float)Math.PI*0.25f,
            (float)Math.PI*0.25f,
            1
        );

        Vector2[] verts = box.get_polygon().vertices();
        System.out.print("P=");
        for (int i=0; i<verts.length; i++) {
            if (Math.abs(verts[i].x)<0.0000001) verts[i].x = 0;
            if (Math.abs(verts[i].y)<0.0000001) verts[i].y = 0;
            System.out.print(verts[i].toString());
            if (i+1!=verts.length) System.out.print(",");
        }
        System.out.println();

        PhysicsManager.get_polygon_normals(box.get_polygon());

    }

    @Override
    public void render() {
        float deltaT = Gdx.graphics.getDeltaTime();
        box.physics_tick(deltaT);

        camera_input();

        ScreenUtils.clear(0.18f, 0.24f, 0.29f, 1);
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setProjectionMatrix(camera.combined);
        sr.polygon(box.get_polygon().get_float_array());
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
