package io.github.neaproject.UI.elements.composite;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import io.github.neaproject.UI.UIManager;
import io.github.neaproject.UI.elements.*;
import io.github.neaproject.physics.RigidBody;
import io.github.neaproject.physics.Stage;

public class BodyEditor {

    private final UIManager manager;
    private final RigidBody body;

    public Panel root;

    public BodyEditor(UIManager manager, RigidBody body) {

        this.manager = manager;
        this.body = body;

        root = new Panel("body_editor_root", new Vector2(0, 0), 320, 220, new Color(0.2f, 0.2f, 0.2f, 1));
        new Panel("body_editor_drag", new Vector2(0, 0), 320, 40, new Color(0.4f, 0.4f, 0.4f, 1), root);
        new TextBox("body_editor_title", new Vector2(8, 0), 320, 40, "Spawn body", 0.12f, Align.left, new Color(0.4f, 0.4f, 0.4f, 1), root);

        InputTextBox box = new InputTextBox("body_editor_field_1", new Vector2(8, 40), 304, 40, 0.12f, 10, InputTextBox.NUMBERS_ONLY, new Color(0.4f, 0.4f, 0.4f, 1), root);
        box.set_input_location(this::receive_input_1);

        Button create_button = new Button("body_editor_create_button", new Vector2(200, 180), 120, 40, new Color(0.3f, 0.3f, 0.3f, 1), root);
        create_button.set_release_action(this::close);
        new TextBox("body_editor_create_text", new Vector2(200, 180), 120, 40, "Create", 0.12f, Align.center, new Color(1f, 1f, 1f, 1), root);


        manager.add_node(root);

    }

    public void receive_input_1(String input_text) {
        body.set_mass(Float.parseFloat(input_text));
    }

    public void close() {
        manager.remove_node(root);
        body.sleeping = false;
    }
}
