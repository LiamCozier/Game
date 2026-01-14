package io.github.neaproject.editor.tools;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.neaproject.input.EditorInputProcessor;
import io.github.neaproject.physics.Stage;

public class EditorToolbox {

    Stage stage;
    OrthographicCamera camera;
    EditorInputProcessor input;
    EditorTool current_tool;
    CreateBodyTool create_body_tool;

    public EditorToolbox(Stage stage, EditorInputProcessor editor_input_processor, OrthographicCamera camera) {
        this.stage = stage;
        this.input = editor_input_processor;
        this.camera = camera;

        current_tool = null;
        create_body_tool = new CreateBodyTool(stage);
    }

    public void update() {
        if (current_tool == null) return;

        Vector3 tmp = camera.unproject(new Vector3(input.mouse_position.x, input.mouse_position.y, 0));
        Vector2 world_position = new Vector2(tmp.x, tmp.y);

        if (input.left_just_pressed) current_tool.on_click(Input.Buttons.LEFT, world_position);
        if (!input.mouse_delta.equals(Vector2.Zero)) {
            if (input.left_pressed) current_tool.on_drag(Input.Buttons.LEFT, input.mouse_delta);
            current_tool.on_move(world_position);
        }

    }

    public void set_tool(int tool) {
        switch (tool) {
            case 1:
                this.current_tool = create_body_tool;
        }
    }



}
