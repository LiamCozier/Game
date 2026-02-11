package io.github.neaproject.physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Stage {

    private final PhysicsWorld world;
    private final List<RigidBody> init_bodies;

    public Stage() {
        world = new PhysicsWorld();
        init_bodies = new ArrayList<>(0);

        initialise();
    }

    public void initialise() {
    }

    public void tick(float dt) {
        world.physics_tick(dt);

    }

    public void render(ShapeRenderer sr, SpriteBatch batch) {
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.WHITE);
        for (RigidBody body: world.get_bodies()) {
            sr.polygon(body.get_polygon().get_float_array());
        }
        sr.end();
    }

    public RigidBody get_overlapping_body(Vector2 world_position) {

        RigidBody[] bodies = world.get_bodies();

        for (RigidBody body : bodies) {

            // AABB hit test
            BoundingBox bb = body.get_bounding_box();
            if (world_position.x >= bb.min.x && world_position.x <= bb.max.x &&
                world_position.y >= bb.min.y && world_position.y <= bb.max.y) {

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

    public void add_body(RigidBody body) {
        init_bodies.add(body);
        reset_world();
    }

    public void reset_world() {
        world.set_all_bodies(init_bodies);
    }

}
