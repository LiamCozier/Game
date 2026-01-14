package io.github.neaproject.editor.tools;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import io.github.neaproject.physics.Stage;

public abstract class EditorTool {

    protected Stage stage;

    public EditorTool(Stage stage) {this.stage = stage;}

    public abstract void on_click(int button, Vector2 world_position);
    public abstract void on_move(Vector2 world_position);
    public abstract void on_drag(int button, Vector2 world_position);


}
