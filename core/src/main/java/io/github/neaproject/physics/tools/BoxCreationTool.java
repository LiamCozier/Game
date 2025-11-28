package io.github.neaproject.physics.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.neaproject.physics.PhysicsWorld;
import io.github.neaproject.physics.RigidBody;
import io.github.neaproject.physics.shape.BoxShape;

public class BoxCreationTool implements Tool {

    private final Camera camera;
    private final PhysicsWorld world;

    private boolean dragging = false;
    private Vector2 start = new Vector2();

    public BoxCreationTool(Camera camera, PhysicsWorld world) {
        this.camera = camera;
        this.world = world;
    }

    @Override
    public void update(float dt) {

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            dragging = true;

            Vector3 tmp = camera.unproject(new Vector3(
                Gdx.input.getX(), Gdx.input.getY(), 0
            ));
            start.set(Math.round(tmp.x), Math.round(tmp.y));
        }

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            return;
        }

        // mouse released
        if (dragging) {
            dragging = false;

            Vector3 tmp = camera.unproject(new Vector3(
                Gdx.input.getX(), Gdx.input.getY(), 0
            ));
            Vector2 end = new Vector2(Math.round(tmp.x), Math.round(tmp.y));

            float x = Math.min(start.x, end.x);
            float y = Math.min(start.y, end.y);
            float w = Math.abs(end.x - start.x);
            float h = Math.abs(end.y - start.y);

            if (w == 0 || h == 0) return;

            Vector2 pos = new Vector2(x + w * 0.5f, y + h * 0.5f);

            float mass;
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                mass = 0f; // static
            } else {
                mass = (w * h);
                if (Gdx.input.isKeyPressed(Input.Keys.M)) mass *= 2;
            }

            RigidBody box = new RigidBody(
                pos, new Vector2(), new BoxShape(w, h),
                0f, 0f, mass, true
            );

            world.add_body(box);
        }
    }


    @Override
    public void render(ShapeRenderer sr) {
        if (!dragging) return;

        Vector3 tmp = camera.unproject(new Vector3(
            Gdx.input.getX(), Gdx.input.getY(), 0
        ));
        Vector2 end = new Vector2(Math.round(tmp.x), Math.round(tmp.y));

        float x = Math.min(start.x, end.x);
        float y = Math.min(start.y, end.y);
        float w = Math.abs(end.x - start.x);
        float h = Math.abs(end.y - start.y);

        sr.setColor(Color.LIME);
        sr.rect(x, y, w, h);
    }
}
