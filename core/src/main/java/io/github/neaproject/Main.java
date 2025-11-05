package io.github.neaproject;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.math.Vector2;
import io.github.neaproject.physics.shape.BoxShape;
import io.github.neaproject.physics.PhysicsManager;
import io.github.neaproject.physics.RigidBody;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    RigidBody box;

    @Override
    public void create() {
        box = new RigidBody(
            new Vector2(0, 0),
            Vector2.Zero.cpy(),
            new BoxShape(10, 10),
            (float)Math.PI*0,
            0,
            1
        );

        for (Vector2 v: box.get_unrotated_polygon().vertices()) {
            System.out.print(v.toString() + ",");
        }
        System.out.println();

        for (Vector2 v: box.get_polygon().vertices()) {
            System.out.print(v.toString() + ",");
        }
        System.out.println();

        PhysicsManager.get_polygon_normals(box.get_polygon());

    }

    @Override
    public void render() {

    }

    @Override
    public void dispose() {
    }
}
