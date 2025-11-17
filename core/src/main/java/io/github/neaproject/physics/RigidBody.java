package io.github.neaproject.physics;

import com.badlogic.gdx.math.Vector2;
import io.github.neaproject.physics.shape.Shape;

public class RigidBody extends Particle {
    private Shape shape;
    public float orientation;
    public float angular_velocity;
    public final float mass;
    public final float inv_mass;
    public final float inertia;
    public final float inv_inertia;
    public final float restitution;
    public final float static_friction;
    public final float dynamic_friction;

    public RigidBody(Vector2 position, Vector2 velocity, Shape shape, float orientation, float angular_velocity, float mass, boolean has_gravity) {
        super(position, velocity, has_gravity);

        this.shape = shape;
        this.orientation = orientation;
        this.angular_velocity = angular_velocity;
        this.mass = mass;
        this.inv_mass = (mass==0 ? 0 : 1f/mass);
        this.restitution = 0.5f;
        this.static_friction  = 0.5f;
        this.dynamic_friction = 0.3f;




        BoundingBox b = this.shape.get_bounding_box(position);
        float width_sq =  b.get_width() * b.get_width();
        float height_sq =  b.get_height() * b.get_height();
        this.inertia = 0.0833f * mass * (width_sq + height_sq);
        this.inv_inertia = 1f/ inertia;

    }

    public void tick(float deltaT) {
        particle_tick(deltaT);
        orientation += angular_velocity * deltaT;
    }

    private Vector2 calc_midpoint(Vector2[] points) {
        int count = points.length;
        Vector2 midpoint = points[0];
        for (int i=1; i<count; i++) {
            midpoint.add(points[i]);
        }

        midpoint.scl(1f/count);
        return midpoint;
    }

    public Vector2 get_centre_of_mass() {

        Vector2[] vertices = shape.get_polygon().vertices();
        int triangle_count = vertices.length - 2;

        Vector2[] centroids = new Vector2[triangle_count];
        float[] masses = new float[triangle_count];

        for (int i=0; i<triangle_count; i++) {

            Vector2[] v = {vertices[0].cpy(), vertices[i+1].cpy(), vertices[i + 2].cpy()};
            Vector2 midpoint = calc_midpoint(v);

            float area = 0.5f * (
                v[0].x * (v[1].y-v[2].y) +
                v[1].x * (v[2].y-v[0].y) +
                v[2].x * (v[0].y-v[1].y)
            );

            // ignore three co-linear points
            if (area==0) {
                continue;
            }

            centroids[i] = midpoint;
            masses[i] = area;
        }

        // weighted average of each triangles' centroids
        Vector2 sum_centroids = new Vector2(0,0);
        float sum_masses = 0;
        for (int i=0; i<triangle_count; i++) {
            sum_centroids.mulAdd(centroids[i], masses[i]);
            sum_masses += masses[i];
        }

        return sum_centroids.scl(1f/sum_masses);
    }

    public Polygon get_polygon() {

        Vector2 centre_of_mass = get_centre_of_mass();
        Vector2[] vertices = shape.get_polygon().vertices();
        int vert_count = vertices.length;

        float sina = (float) Math.sin(-orientation);
        float cosa = (float) Math.cos(-orientation);

        Vector2[] rotated_verts = new Vector2[vert_count];
        for (int i=0; i<vert_count; i++) {
            rotated_verts[i] = new Vector2(
                (vertices[i].x-centre_of_mass.x)*cosa - (vertices[i].y-centre_of_mass.y)*sina,
                (vertices[i].y-centre_of_mass.y)*cosa + (vertices[i].x-centre_of_mass.x)*sina);

            rotated_verts[i].add(this.position);
            rotated_verts[i].add(centre_of_mass);
        }

        return new Polygon(rotated_verts);
    }

    public void physics_tick(float deltaT) {
        super.particle_tick(deltaT);
        orientation += angular_velocity * deltaT;
    }
}
