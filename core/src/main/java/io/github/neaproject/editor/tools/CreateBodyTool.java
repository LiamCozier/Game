package io.github.neaproject.editor.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import io.github.neaproject.physics.RigidBody;
import io.github.neaproject.physics.Stage;
import io.github.neaproject.physics.shape.BoxShape;

import java.util.Random;


public class CreateBodyTool extends EditorTool {
    public CreateBodyTool(Stage stage) {
        super(stage);
    }

    @Override
    public void on_click(int button, Vector2 world_position) {
        RigidBody b = new RigidBody(
            world_position,
            new Vector2(0, 0),
            new BoxShape(1, 1),
            0, 0,
            1, true
        );

        stage.add_body(b);
    }

    @Override
    public void on_move(Vector2 world_position) {}

    @Override
    public void on_drag(int button, Vector2 world_position, Vector2 mouse_delta) {}

    @Override
    public void on_release(int button, Vector2 world_position) {}

}
