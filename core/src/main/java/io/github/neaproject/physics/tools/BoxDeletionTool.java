package io.github.neaproject.physics.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ShortArray;
import io.github.neaproject.physics.*;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class BoxDeletionTool implements Tool {

    private final OrthographicCamera camera;
    private final PhysicsWorld world;

    private RigidBody hovered = null;

    public BoxDeletionTool(OrthographicCamera camera, PhysicsWorld world) {
        this.camera = camera;
        this.world = world;
    }

    @Override
    public void update(float delta) {
        hovered = find_hovered_body();

        if (hovered != null && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            world.remove_body(hovered);
            hovered = null;
        }
    }

    @Override
    public void render(ShapeRenderer sr) {
        if (hovered == null) return;
        draw_filled_polygon(sr, hovered.get_polygon().get_float_array(), Color.RED);
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

    private RigidBody find_hovered_body() {
        Vector3 mouse = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        Vector2 mousePos = new Vector2(mouse.x, mouse.y);

        RigidBody[] bodies = world.get_bodies();

        for (RigidBody body : bodies) {

            // AABB hit test
            BoundingBox bb = body.get_bounding_box();
            if (mousePos.x >= bb.min.x && mousePos.x <= bb.max.x &&
                mousePos.y >= bb.min.y && mousePos.y <= bb.max.y) {

                // precise test: point inside polygon
                if (point_inside_polygon(mousePos, body.get_polygon().vertices()))
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
