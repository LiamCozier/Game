package io.github.neaproject.editor.tools;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import io.github.neaproject.UI.UIManager;
import io.github.neaproject.physics.RigidBody;
import io.github.neaproject.physics.Stage;

public class SelectBodyTool extends EditorTool {

    RigidBody dragging_body;
    Vector2 body_relative_position;

    public SelectBodyTool(Stage stage, UIManager manager) {
        super(stage, manager);
        dragging_body = null;
    }

    @Override
    public void on_click(int button, MouseInfo info) {

    }

    @Override
    public void on_move(MouseInfo info) {

    }

    @Override
    public void on_drag(int button, MouseInfo info) {
        if (button != Input.Buttons.LEFT) return;

        if (dragging_body == null) {
            RigidBody body = stage.get_overlapping_body(info.world_position);
            if (body == null) return;

            dragging_body = body;
            dragging_body.sleeping = true;
            body_relative_position = dragging_body.position.cpy().sub(info.world_position);
        }

        dragging_body.position.set(info.world_position.cpy().add(body_relative_position));
        dragging_body.velocity.set(info.mouse_delta);

        if (stage.is_running()) stage.pause();
    }

    @Override
    public void on_release(int button, MouseInfo info) {
        if (dragging_body != null) {

            // Update the initial body so reset_world() keeps the new position
            int index = stage.world_index_of(dragging_body);
            if (index != -1) {
                RigidBody init = stage.get_init_body(index);
                init.position.set(dragging_body.position);
                init.velocity.set(dragging_body.velocity);
                init.orientation = dragging_body.orientation;
                init.angular_velocity = dragging_body.angular_velocity;
            }

            dragging_body.sleeping = false;
            dragging_body = null;
        }
    }



}
