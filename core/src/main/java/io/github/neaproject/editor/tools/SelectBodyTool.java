package io.github.neaproject.editor.tools;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import io.github.neaproject.physics.RigidBody;
import io.github.neaproject.physics.Stage;

public class SelectBodyTool extends EditorTool {

    RigidBody dragging_body;
    Vector2 body_relative_position;

    public SelectBodyTool(Stage stage) {
        super(stage);
        dragging_body = null;
    }

    @Override
    public void on_click(int button, Vector2 world_position) {

    }

    @Override
    public void on_move(Vector2 world_position) {

    }

    @Override
    public void on_drag(int button, Vector2 world_position, Vector2 mouse_delta) {
        if (button != Input.Buttons.LEFT) return;

        if (dragging_body == null) {
            RigidBody body = stage.get_overlapping_body(world_position);
            if (body == null) return;

            dragging_body = body;
            dragging_body.sleeping = true;
            body_relative_position = dragging_body.position.cpy().sub(world_position);
        }

        dragging_body.position.set(world_position.cpy().add(body_relative_position));
    }

    @Override
    public void on_release(int button, Vector2 world_position) {
        if (dragging_body != null) {
            dragging_body.sleeping = false;
            dragging_body = null;
        }
    }


}
