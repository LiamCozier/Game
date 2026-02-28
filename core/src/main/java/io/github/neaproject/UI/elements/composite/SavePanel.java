package io.github.neaproject.UI.elements.composite;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import io.github.neaproject.UI.UIManager;
import io.github.neaproject.UI.elements.*;
import io.github.neaproject.editor.StageSaveLoad;
import io.github.neaproject.scene.MainMenuScene;
import io.github.neaproject.scene.SceneManager;
import io.github.neaproject.physics.Stage;

public class SavePanel {

    private final Stage stage;
    private final SceneManager manager;

    public Panel root;
    private InputTextBox name_box;

    public SavePanel(UIManager ui_manager, Stage stage, SceneManager manager) {
        this.stage = stage;
        this.manager = manager;

        root = new Panel("save_panel_root", new Vector2(600, 300), 400, 220, new Color(0.1f, 0.1f, 0.1f, 0.95f));

        new TextBox("save_panel_title", new Vector2(0, 0), 400, 60, "Save Stage", 0.2f, Align.center, Color.WHITE, root);

        new TextBox("save_panel_name_label", new Vector2(20, 70), 100, 40, "Name", 0.14f, Align.left, Color.WHITE, root);

        name_box = new InputTextBox(
            "save_panel_name_box",
            "name",
            new Vector2(120, 70),
            260,
            40,
            0.14f,
            32,
            InputTextBox.InputFilter.LETTERS,
            new Color(0.3f, 0.3f, 0.3f, 1),
            root
        );
        name_box.set_input_location(this::receive_input);

        Button save_button = new Button("save_panel_save_button", new Vector2(220, 140), 160, 50, Color.DARK_GRAY.cpy(), root);
        new TextBox("save_panel_save_text", new Vector2(0, 0), 160, 50, "Save", 0.16f, Align.center, Color.WHITE, save_button);
        save_button.set_release_action(this::save_and_exit);

        Button cancel_button = new Button("save_panel_cancel_button", new Vector2(20, 140), 160, 50, Color.DARK_GRAY.cpy(), root);
        new TextBox("save_panel_cancel_text", new Vector2(0, 0), 160, 50, "Cancel", 0.16f, Align.center, Color.WHITE, cancel_button);
        cancel_button.set_release_action(this::hide);

        ui_manager.add_node(root);
        root.hide();
    }

    public void receive_input(String id, String text) {}

    private void save_and_exit() {
        String name = name_box.get_text();
        if (name == null || name.isEmpty()) name = "scene";

        StageSaveLoad.save(stage, "scenes/" + name + ".json");
        manager.set_scene(new MainMenuScene(manager));
    }

    public void show() { root.show(); }
    public void hide() { root.hide(); }
    public boolean is_open() { return !root.is_invisible(); }
}
