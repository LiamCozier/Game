package io.github.neaproject.physics;

import com.badlogic.gdx.math.Vector2;

public class CollisionManifold {
    public float minimum_penetration_depth;
    public Vector2 collision_normal;
    public Vector2[] contact_points;

    public CollisionManifold(float minimum_penetration_depth, Vector2 collision_normal, Vector2[] contact_points) {
        this.minimum_penetration_depth = minimum_penetration_depth;
        this.collision_normal = collision_normal;
        this.contact_points = contact_points;
    }
}
