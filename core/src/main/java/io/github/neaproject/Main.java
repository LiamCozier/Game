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
import io.github.neaproject.physics.shape.PolygonShape;
import io.github.neaproject.physics.tools.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/** {@link ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    OrthographicCamera camera;
    boolean drag = false;
    InputToolbox toolbox;
    BoxCreationTool boxTool;

    ShapeRenderer sr;
    PhysicsWorld world;
    RigidBody box1;
    Vector2 start_position;

    // init tools
    BoxCreationTool CREATE_BOX;
    BoxDeletionTool DELETE_BOX;
    int current_tool = 0;


    @Override
    public void create() {
        world = new PhysicsWorld();

        // camera
        float height = 40;
        float ppu = Gdx.graphics.getHeight() / height;
        float width = Gdx.graphics.getWidth() / ppu;
        camera = new OrthographicCamera(width, height);
        sr = new ShapeRenderer();

        toolbox = new InputToolbox();
        boxTool = new BoxCreationTool(camera, world);

        toolbox.set_tool(boxTool);

        CREATE_BOX = new BoxCreationTool(camera, world);
        DELETE_BOX = new BoxDeletionTool(camera, world);
    }



    @Override
    public void render() {
        ScreenUtils.clear(0.18f, 0.24f, 0.29f, 1);

        sr.setProjectionMatrix(camera.combined);

        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.WHITE);
        RigidBody[] bodies = world.get_bodies();
        for (RigidBody body: bodies) {
            sr.polygon(body.get_polygon().get_float_array());
        }
        toolbox.render(sr);
        sr.end();


        float delta_time = Gdx.graphics.getDeltaTime();
        world.physics_tick(delta_time);
        toolbox.update(delta_time);
        input();


    }

    public void input() {

        // camera
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
                0, (float) Math.PI * -1,
                (float)Math.PI * radius * radius, true
            ));
        }
    }

    @Override
    public void dispose() {
    }
}
