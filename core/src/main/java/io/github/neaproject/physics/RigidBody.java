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

        List<MassPoint> mass_points = new ArrayList<MassPoint>(0);
        Vector2[] vertices = {
            new Vector2(1,0),
            new Vector2(1,0.25f),
            new Vector2(1,0.5f),
            new Vector2(1,0.75f),
            new Vector2(1,1),
            new Vector2(3,1),
            new Vector2(30,0)
        };

        for (int i=1; i<vertices.length-1; i++) {

            Vector2[] v = {vertices[0].cpy(), vertices[i].cpy(), vertices[i + 1].cpy()};

            Vector2 midpoint = calc_midpoint(v);

            float area = 0.5f * Math.abs(
                v[0].x * (v[1].y-v[2].y) +
                v[1].x * (v[2].y-v[0].y) +
                v[2].x * (v[0].y-v[1].y)
            );

            // ignore three co-linear points
            if (area==0) {
                continue;
            }
            mass_points.add(new MassPoint(area, midpoint));
        }


        while(mass_points.size()>1) {

            MassPoint mp1 = mass_points.get(0);
            MassPoint mp2 = mass_points.get(1);

            Vector2 p1 = mp1.point.cpy();
            Vector2 p2 = mp2.point.cpy();

            // lerp between mp1 & mp2 with t being the ratio between their masses
            float t = mp1.mass / (mp1.mass  + mp2.mass);

            Vector2 mass_centre = p1.scl(t).mulAdd(p2, (1-t));

            mass_points.remove(1);
            mass_points.remove(0);
            mass_points.add(new MassPoint(
                mp1.mass + mp2.mass,
                mass_centre
            ));
        }

        return mass_points.get(0).point.cpy();
    }


    public Polygon get_polygon() {

        Vector2 centre_of_mass = get_centre_of_mass();
        Vector2[] vertices = shape.get_polygon().vertices();
        int vert_count = vertices.length;

        float sina = (float) Math.sin(orientation);
        float cosa = (float) Math.cos(orientation);

        Vector2[] rotated_verts = new Vector2[vert_count];
        for (int i=0; i<vert_count; i++) {
            rotated_verts[i] = new Vector2(
                (vertices[i].x-centre_of_mass.x)*cosa - (vertices[i].y-centre_of_mass.y)*sina,
                (vertices[i].y-centre_of_mass.y)*cosa + (vertices[i].x-centre_of_mass.x)*sina);
        }

        return new Polygon(rotated_verts);
    }




}
