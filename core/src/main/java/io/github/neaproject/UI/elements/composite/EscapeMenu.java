package io.github.neaproject.UI.elements.composite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import io.github.neaproject.UI.UIManager;
import io.github.neaproject.UI.elements.*;
import io.github.neaproject.scene.MainMenuScene;
import io.github.neaproject.scene.SceneManager;
import io.github.neaproject.physics.Stage;

public class EscapeMenu {

    private final SceneManager manager;
    private final Stage stage;

    public Panel root;

    public EscapeMenu(UIManager ui_manager, Stage stage, SceneManager manager) {
        this.stage = stage;
        this.manager = manager;

        root = new Panel("escape_menu_root", new Vector2(560, 240), 400, 300, new Color(0.1f, 0.1f, 0.1f, 1f));
        root.set_z(10);

        new Panel("escape_menu_background", new Vector2(-600, -260), 5000, 5000, new Color(0, 0, 0, 0.5f), root);

        new TextBox("escape_menu_title", new Vector2(0, 0), 400, 70, "Paused", 0.25f, Align.center, Color.WHITE, root);

        Button resume_button = new Button("escape_resume_button", new Vector2(20, 90), 360, 60, Color.DARK_GRAY.cpy(), root);
        new TextBox("escape_resume_text", new Vector2(0, 0), 360, 60, "Resume", 0.18f, Align.center, Color.WHITE, resume_button);
        resume_button.set_release_action(this::resume);

        Button menu_button = new Button("escape_menu_button", new Vector2(20, 160), 360, 60, Color.DARK_GRAY.cpy(), root);
        new TextBox("escape_menu_text", new Vector2(0, 0), 360, 60, "Main Menu", 0.18f, Align.center, Color.WHITE, menu_button);
        menu_button.set_release_action(this::to_main_menu);

        Button exit_button = new Button("escape_exit_button", new Vector2(20, 230), 360, 60, Color.DARK_GRAY.cpy(), root);
        new TextBox("escape_exit_text", new Vector2(0, 0), 360, 60, "Exit to Desktop", 0.18f, Align.center, Color.WHITE, exit_button);
        exit_button.set_release_action(Gdx.app::exit);

        ui_manager.add_node(root);
        root.hide();
    }

    private void resume() {
        stage.play();
        root.hide();
    }

    private void to_main_menu() {
        manager.set_scene(new MainMenuScene(manager));
    }

    public void show() {
        stage.pause();
        root.show();
    }

    public void hide() {
        root.hide();
    }

    public boolean is_invisible() {
        return root.is_invisible();
    }
}
