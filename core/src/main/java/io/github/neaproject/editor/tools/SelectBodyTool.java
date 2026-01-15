package io.github.neaproject.editor.tools;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import io.github.neaproject.physics.RigidBody;
import io.github.neaproject.physics.Stage;

public class SelectBodyTool extends EditorTool {
    public SelectBodyTool(Stage stage) {super(stage);}

    @Override
    public void on_click(int button, Vector2 world_position) {

    }

    @Override
    public void on_move(Vector2 world_position) {

    }

    @Override
    public void on_drag(int button, Vector2 world_position, Vector2 mouse_delta) {
        if (button != Input.Buttons.LEFT) return;

        RigidBody body = stage.get_overlapping_body(world_position);
        if (body == null) return;

        body.position.add(mouse_delta);
    }

}
