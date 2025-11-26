package io.github.neaproject.physics.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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
        hovered = findHoveredBody();

        if (hovered != null && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            world.remove_body(hovered);
            hovered = null;
        }
    }

    @Override
    public void render(ShapeRenderer sr) {
        if (hovered == null) return;

        sr.setColor(Color.RED);
        sr.polygon(hovered.get_polygon().get_float_array());
    }

    private RigidBody findHoveredBody() {
        Vector3 mouse = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        Vector2 mousePos = new Vector2(mouse.x, mouse.y);

        RigidBody[] bodies = world.get_bodies();

        for (RigidBody body : bodies) {

            // AABB hit test
            BoundingBox bb = body.get_bounding_box();
            if (mousePos.x >= bb.min.x && mousePos.x <= bb.max.x &&
                mousePos.y >= bb.min.y && mousePos.y <= bb.max.y) {

                // precise test: point inside polygon
                if (pointInsidePolygon(mousePos, body.get_polygon().vertices()))
                    return body;
            }
        }
        return null;
    }

    private boolean pointInsidePolygon(Vector2 p, Vector2[] verts) {
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
