package io.github.neaproject.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import io.github.neaproject.physics.RigidBody;
import io.github.neaproject.physics.Stage;
import io.github.neaproject.physics.shape.BoxShape;
import io.github.neaproject.physics.shape.PolygonShape;
import io.github.neaproject.physics.shape.Shape;

import java.util.ArrayList;
import java.util.List;

public class StageSaveLoad {

    private static final Json json = new Json();

    static { // prevent value skipping
        json.setOutputType(JsonWriter.OutputType.json);
        json.setIgnoreUnknownFields(true);
        json.setUsePrototypes(false);
    }

    public static void save(Stage stage, String path) {
        SaveStage data = serialize(stage);
        FileHandle file = Gdx.files.local(path);
        file.writeString(json.prettyPrint(data), false);
    }

    public static Stage load(String path) {
        FileHandle file = Gdx.files.local(path);
        if (!file.exists()) return new Stage();

        SaveStage data = json.fromJson(SaveStage.class, file);
        return deserialize(data);
    }

    private static SaveStage serialize(Stage stage) {
        SaveStage data = new SaveStage();

        int count = stage.get_body_count();
        for (int i = 0; i < count; i++) {
            RigidBody body = stage.get_init_body(i);

            SaveBody sb = new SaveBody();
            sb.position = body.position.cpy();
            sb.velocity = body.velocity.cpy();
            sb.orientation = body.orientation;
            sb.angular_velocity = body.angular_velocity;
            sb.mass = body.get_mass();
            sb.has_gravity = body.has_gravity;
            sb.restitution = body.restitution;
            sb.static_friction = body.static_friction;
            sb.dynamic_friction = body.dynamic_friction;
            sb.shape = serialize_shape(body.get_shape());

            data.bodies.add(sb);
        }

        return data;
    }

    private static SaveShape serialize_shape(Shape shape) {
        if (shape instanceof BoxShape box) {
            SaveShape ss = new SaveShape();
            ss.type = "box";
            ss.width = box.get_width();
            ss.height = box.get_height();
            ss.vertices = null;
            return ss;
        }

        if (shape instanceof PolygonShape poly) {
            SaveShape ss = new SaveShape();
            ss.type = "polygon";
            ss.vertices = poly.get_polygon().vertices();
            return ss;
        }

        throw new RuntimeException("Unsupported shape type: " + shape.getClass().getSimpleName());
    }

    private static Stage deserialize(SaveStage data) {
        Stage stage = new Stage();

        for (SaveBody sb : data.bodies) {
            Shape shape = deserialize_shape(sb.shape);

            RigidBody body = new RigidBody(
                sb.position.cpy(),
                sb.velocity.cpy(),
                shape,
                sb.orientation,
                sb.angular_velocity,
                sb.mass,
                sb.has_gravity
            );

            body.restitution = sb.restitution;
            body.static_friction = sb.static_friction;
            body.dynamic_friction = sb.dynamic_friction;

            stage.add_body(body);
        }

        return stage;
    }

    private static Shape deserialize_shape(SaveShape ss) {
        if ("box".equals(ss.type)) {
            return new BoxShape(ss.width, ss.height);
        }

        if ("polygon".equals(ss.type)) {
            Vector2[] verts = new Vector2[ss.vertices.length];
            for (int i = 0; i < ss.vertices.length; i++) {
                verts[i] = ss.vertices[i].cpy();
            }
            return new PolygonShape(verts);
        }

        throw new RuntimeException("Unknown shape type: " + ss.type);
    }

    private static class SaveStage {
        public List<SaveBody> bodies = new ArrayList<>();
    }

    private static class SaveBody {
        public Vector2 position;
        public Vector2 velocity;
        public float orientation;
        public float angular_velocity;
        public float mass;
        public boolean has_gravity;
        public float restitution;
        public float static_friction;
        public float dynamic_friction;
        public SaveShape shape;
    }

    private static class SaveShape {
        public String type;
        public float width;
        public float height;
        public Vector2[] vertices;
    }


}
