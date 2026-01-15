package io.github.neaproject.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.neaproject.physics.shape.BoxShape;

public class Stage {

    private final PhysicsWorld physics_world;

    public Stage() {
        physics_world = new PhysicsWorld();

        initialise();
    }

    public void initialise() {

        physics_world.add_body(new RigidBody(
            new Vector2(0, 0),
            new Vector2(0, 0),
            new BoxShape(2, 2),
            0, 3.14f,
            10, false
        ));

        physics_world.add_body(new RigidBody(
            new Vector2(0, -5),
            new Vector2(0, 0),
            new BoxShape(50, 1),
            0, 0,
            0, false
        ));
    }

    public void tick(float dt) {
        physics_world.physics_tick(dt);

    }

    public void render(ShapeRenderer sr, SpriteBatch batch) {
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.WHITE);
        for (RigidBody body: physics_world.get_bodies()) {
            sr.polygon(body.get_polygon().get_float_array());
        }
        sr.end();
    }

    public void add_body(RigidBody body) {
        physics_world.add_body(body);
    }

    public RigidBody get_overlapping_body(Vector2 world_position) {

        RigidBody[] bodies = physics_world.get_bodies();

        for (RigidBody body : bodies) {

            // AABB hit test
            BoundingBox bb = body.get_bounding_box();
            if (world_position.x >= bb.min.x && world_position.x <= bb.max.x &&
                world_position.y >= bb.min.y && world_position.y <= bb.max.y) {

                // precise test: point inside polygon
                if (point_inside_polygon(world_position, body.get_polygon().vertices()))
                    return body;
            }
        }
        return null;
    }

    private boolean point_inside_polygon(Vector2 p, Vector2[] verts) {
        boolean inside = false;
        int n = verts.length;

        for (int i = 0, j = n - 1; i < n; j = i++) {
            Vector2 a = verts[i];
            Vector2 b = verts[j];

            boolean intersect =
                ((a.y > p.y) != (b.y > p.y)) &&
                    (p.x < (b.x - a.x) * (p.y - a.y) / (b.y - a.y) + a.x);

            if (intersect) inside = !inside;
        }
        return inside;
    }

}
