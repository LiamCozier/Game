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
        this.inv_mass = (mass == 0f ? 0f : 1f / mass);
        this.restitution = 0.6f;
        this.static_friction = 0.5f;
        this.dynamic_friction = 0.3f;

        BoundingBox bounding_box = this.shape.get_bounding_box(position);
        float width = bounding_box.get_width();
        float height = bounding_box.get_height();
        float width_sq = width * width;
        float height_sq = height * height;

        if (mass == 0f) {
            // Static body: no rotation response
            this.inertia = 0f;
            this.inv_inertia = 0f;
        } else {
            this.inertia = 0.0833f * mass * (width_sq + height_sq);
            this.inv_inertia = 1f / this.inertia;
        }
    }

    public void tick(float deltaT) {
        particle_tick(deltaT);
        orientation += angular_velocity * deltaT;
    }

    private Vector2 calc_midpoint(Vector2[] points) {
        int count = points.length;
        Vector2 midpoint = points[0].cpy();
        for (int i = 1; i < count; i++) {
            midpoint.add(points[i]);
        }

        midpoint.scl(1f / count);
        return midpoint;
    }

    public Vector2 get_centre_of_mass() {
        Vector2[] vertices = shape.get_polygon().vertices();
        int triangle_count = vertices.length - 2;

        Vector2[] centroids = new Vector2[triangle_count];
        float[] masses = new float[triangle_count];

        for (int i = 0; i < triangle_count; i++) {
            Vector2[] v = {
                vertices[0].cpy(),
                vertices[i + 1].cpy(),
                vertices[i + 2].cpy()
            };

            Vector2 midpoint = calc_midpoint(v);

            float area = 0.5f * (
                v[0].x * (v[1].y - v[2].y) +
                    v[1].x * (v[2].y - v[0].y) +
                    v[2].x * (v[0].y - v[1].y)
            );

            if (area == 0f) {
                continue;
            }

            centroids[i] = midpoint;
            masses[i] = area;
        }

        Vector2 sum_centroids = new Vector2(0f, 0f);
        float sum_masses = 0f;
        for (int i = 0; i < triangle_count; i++) {
            if (centroids[i] == null) continue;
            sum_centroids.mulAdd(centroids[i], masses[i]);
            sum_masses += masses[i];
        }

        if (sum_masses == 0f) {
            // Fallback: average of vertices
            Vector2 fallback = new Vector2();
            for (Vector2 v : vertices) {
                fallback.add(v);
            }
            return fallback.scl(1f / vertices.length);
        }

        return sum_centroids.scl(1f / sum_masses);
    }

    public Polygon get_polygon() {
        Vector2 centre_of_mass = get_centre_of_mass();
        Vector2[] vertices = shape.get_polygon().vertices();
        int vert_count = vertices.length;

        float sin_a = (float) Math.sin(orientation);
        float cos_a = (float) Math.cos(orientation);

        Vector2[] rotated_verts = new Vector2[vert_count];
        for (int i = 0; i < vert_count; i++) {
            float local_x = vertices[i].x - centre_of_mass.x;
            float local_y = vertices[i].y - centre_of_mass.y;

            float rotated_x = local_x * cos_a - local_y * sin_a;
            float rotated_y = local_y * cos_a + local_x * sin_a;

            rotated_verts[i] = new Vector2(rotated_x, rotated_y);
            rotated_verts[i].add(this.position);
            rotated_verts[i].add(centre_of_mass);
        }

        return new Polygon(rotated_verts);
    }

    public void physics_tick(float deltaT) {
        super.particle_tick(deltaT);
        orientation += angular_velocity * deltaT;
    }

    public BoundingBox get_bounding_box() {
        Polygon poly = get_polygon(); // already rotated + translated
        Vector2[] verts = poly.vertices();

        float min_x = Float.MAX_VALUE, min_y = Float.MAX_VALUE;
        float max_x = -Float.MAX_VALUE, max_y = -Float.MAX_VALUE;

        for (Vector2 v : verts) {
            if (v.x < min_x) min_x = v.x;
            if (v.y < min_y) min_y = v.y;
            if (v.x > max_x) max_x = v.x;
            if (v.y > max_y) max_y = v.y;
        }

        return new BoundingBox(new Vector2(min_x, min_y), new Vector2(max_x, max_y));
    }

}
