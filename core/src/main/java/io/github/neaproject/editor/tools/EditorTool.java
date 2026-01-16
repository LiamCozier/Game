package io.github.neaproject.editor.tools;

import com.badlogic.gdx.math.Vector2;
import io.github.neaproject.UI.UIManager;
import io.github.neaproject.physics.Stage;

public abstract class EditorTool {

    protected Stage stage;
    protected UIManager ui_manager;

    public EditorTool(Stage stage, UIManager ui_manager) {
        this.stage = stage;
        this.ui_manager = ui_manager;
    }

    public abstract void on_click(int button, MouseInfo info);
    public abstract void on_move(MouseInfo info);
    public abstract void on_drag(int button, MouseInfo info);
    public abstract void on_release(int button, MouseInfo info);


}
