package io.github.neaproject.physics;

import com.badlogic.gdx.math.Vector2;

public class Particle {
    public Vector2 position;
    public Vector2 velocity;
    protected boolean has_gravity;

    public Particle(Vector2 position, Vector2 velocity, boolean has_gravity) {
        this.position = position;
        this.velocity = velocity;
        this.has_gravity = has_gravity;
    }

    public void particle_tick(float deltaT) {

        if (this.has_gravity) velocity.mulAdd(new Vector2(0.0f, -60.0f), deltaT);
        position.mulAdd(velocity.cpy(), deltaT);
        if (has_gravity) velocity.mulAdd(new Vector2(0, -0.5f), deltaT);

    }
}
