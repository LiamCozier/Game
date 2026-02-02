package io.github.neaproject.UI.elements.composite;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import io.github.neaproject.UI.UIManager;
import io.github.neaproject.UI.elements.*;
import io.github.neaproject.physics.RigidBody;
import io.github.neaproject.physics.shape.BoxShape;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

        Map<String, InputTextBox.InputFilter> input_fields = new HashMap<>();
        input_fields.put("width", InputTextBox.InputFilter.FLOAT);
        input_fields.put("height", InputTextBox.InputFilter.FLOAT);
        input_fields.put("mass", InputTextBox.InputFilter.FLOAT);

        int i = 0;
        for (Map.Entry<String, InputTextBox.InputFilter> entry: input_fields.entrySet()) {
            i++;

            new TextBox("body_editor_field_text_" + i, new Vector2(8, 40 * i), 100, 40, entry.getKey(), 0.12f, Align.center, new Color(1, 1, 1, 1), root);
            InputTextBox box = new InputTextBox("body_editor_field_" + i, entry.getKey(), new Vector2(106, 40 * i), 204, 40, 0.12f, 10, entry.getValue(), new Color(0.4f, 0.4f, 0.4f, 1), root);
            box.set_input_location(this::receive_input);
        }

        Button create_button = new Button("body_editor_create_button", new Vector2(200, 180), 120, 40, new Color(0.3f, 0.3f, 0.3f, 1), root);
        create_button.set_release_action(this::close);
        new TextBox("body_editor_create_text", new Vector2(200, 180), 120, 40, "Create", 0.12f, Align.center, new Color(1f, 1f, 1f, 1), root);

        manager.add_node(root);
    }

    public void receive_input(String field_id, String input_text) {
        BoxShape shape = (BoxShape) body.get_shape();
        switch (field_id) {
            case "width":
                try {
                    shape.set_width(Float.parseFloat(input_text));
                } catch (NumberFormatException e) {
                    shape.set_width(1);
                }
                break;
            case "height":
                try {
                    shape.set_height(Float.parseFloat(input_text));
                } catch (NumberFormatException e) {
                    shape.set_height(1);
                }
                break;
            case "mass":
                try {
                    body.set_mass(Float.parseFloat(input_text));
                } catch (NumberFormatException e) {
                    body.set_mass(1);
                }
                break;
        }

    }

    public void close() {
        manager.remove_node(root);
        body.sleeping = false;
    }
}
