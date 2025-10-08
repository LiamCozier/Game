package io.github.neaproject.physics;

import com.badlogic.gdx.math.Vector2;

public class MassPoint {
    public float mass;
    public Vector2 point;

    public MassPoint(float mass, Vector2 point) {
        this.mass = mass;
        this.point = point;
    }
}
