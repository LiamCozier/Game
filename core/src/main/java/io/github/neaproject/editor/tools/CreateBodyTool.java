package io.github.neaproject.editor.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import io.github.neaproject.UI.UIManager;
import io.github.neaproject.UI.elements.Panel;
import io.github.neaproject.UI.elements.composite.BodyEditor;
import io.github.neaproject.physics.RigidBody;
import io.github.neaproject.physics.Stage;
import io.github.neaproject.physics.shape.BoxShape;


public class CreateBodyTool extends EditorTool {
    public CreateBodyTool(Stage stage, UIManager manager, BodyEditor body_editor) {
        super(stage, manager);
        this.body_editor = body_editor;
    }

    BodyEditor body_editor;

    @Override
    public void on_click(int button, MouseInfo info) {
        if (!body_editor.root.is_invisible()) return;

        RigidBody b = new RigidBody(
            info.world_position,
            new Vector2(0, 0),
            new BoxShape(1, 1),
            0, 0,
            1, true
        );
        b.sleeping = true;

        stage.world.add_body(b);
        body_editor.set_body(b);

        body_editor.root.set_position(info.screen_position.cpy().add(new Vector2(64, -48)));
        body_editor.show();
    }

    @Override
    public void on_move(MouseInfo info) {}

    @Override
    public void on_drag(int button, MouseInfo info) {}

    @Override
    public void on_release(int button, MouseInfo info) {}

}
