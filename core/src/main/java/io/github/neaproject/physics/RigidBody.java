package io.github.neaproject.physics;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class RigidBody  extends Particle {

    private Shape shape;

    private float orientation;
    private float angular_velocity;

    private final float mass;
    private final float moment_of_inertia;

    public RigidBody(Vector2 position, Vector2 velocity, Shape shape, float orientation, float angular_velocity, float mass) {
        super(position, velocity);

        this.shape = shape;
        this.orientation = orientation;
        this.angular_velocity = angular_velocity;
        this.mass = mass;


        BoundingBox b = this.shape.get_bounding_box(position);
        float width_sq =  b.get_width() * b.get_width();
        float height_sq =  b.get_height() * b.get_height();
        this.moment_of_inertia = 0.0833f * mass * (width_sq + height_sq);

    }

    public void tick(float deltaT) {
        particle_tick(deltaT);
        orientation += angular_velocity * deltaT;
    }

    public Vector2 get_centre_of_mass() {

        List<MassPoint> mass_points = new ArrayList<MassPoint>(0);
        Vector2[] vertices = shape.get_polygon().vertices();

        for (int i=1; i<vertices.length-1; i++) {

            Vector2 midpoint = vertices[0].cpy();
            midpoint.add(vertices[i].cpy());
            midpoint.add(vertices[i + 1].cpy());
            midpoint.scl(0.333f);

            float area = 1;

            mass_points.add(new MassPoint(area, midpoint));

        }

        return null;
    }


    public Polygon get_polygon() {
        return null;
    }




}
