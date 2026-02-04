package io.github.neaproject.editor.tools;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.neaproject.UI.UIManager;
import io.github.neaproject.UI.elements.composite.BodyEditor;
import io.github.neaproject.input.EditorInputProcessor;
import io.github.neaproject.physics.Stage;

public class EditorToolbox {

    public static final int CREATE_BODY = 1;
    public static final int SELECT_BODY = 2;

    private final CreateBodyTool CREATE_BODY_TOOL;
    private final SelectBodyTool SELECT_BODY_TOOL;

    private final OrthographicCamera camera;
    private final EditorInputProcessor input;
    private EditorTool current_tool;

    private boolean dragging;

    public EditorToolbox(Stage stage, EditorInputProcessor editor_input_processor, OrthographicCamera camera, UIManager manager, BodyEditor body_editor) {
        this.input = editor_input_processor;
        this.camera = camera;

        CREATE_BODY_TOOL = new CreateBodyTool(stage, manager, body_editor);
        SELECT_BODY_TOOL = new SelectBodyTool(stage, manager);

        current_tool = null;
        dragging = false;
    }

    public void update() {
        if (current_tool == null) return;

        Vector2 world_position = get_world_mouse_postition();
        Vector2 screen_position = new Vector2(input.mouse_position.x, input.mouse_position.y);
        Vector2 mouse_delta = get_world_mouse_delta();
        MouseInfo mouse_info = new MouseInfo(world_position, screen_position, mouse_delta);

        if (input.left_just_pressed) current_tool.on_click(Input.Buttons.LEFT, mouse_info);

        if (input.left_just_released) {
            dragging = false;
            current_tool.on_release(Input.Buttons.LEFT, mouse_info);
        }

        if (input.mouse_delta.len2() != 0f) {

            if (input.left_pressed) {
                dragging = true;
                current_tool.on_drag(Input.Buttons.LEFT, mouse_info);
            }

            current_tool.on_move(mouse_info);
        }
    }

    private Vector2 get_world_mouse_delta() {
        float current_screen_x = input.mouse_position.x;
        float current_screen_y = input.mouse_position.y;

        float previous_screen_x = current_screen_x - input.mouse_delta.x;
        float previous_screen_y = current_screen_y - input.mouse_delta.y;

        Vector3 previous_world_mouse_3 = camera.unproject(new Vector3(previous_screen_x, previous_screen_y, 0));

        Vector3 current_world_mouse_3 = camera.unproject(new Vector3(current_screen_x, current_screen_y, 0));

        return new Vector2(
            current_world_mouse_3.x - previous_world_mouse_3.x,
            current_world_mouse_3.y - previous_world_mouse_3.y
        );
    }

    private Vector2 get_world_mouse_postition() {
        Vector3 screen_mouse = new Vector3(input.mouse_position.x, input.mouse_position.y, 0);

        Vector3 world_mouse_3 = camera.unproject(screen_mouse);
        return new Vector2(world_mouse_3.x, world_mouse_3.y);
    }

    public void set_tool(int tool) {
        switch (tool) {
            case 1:
                this.current_tool = CREATE_BODY_TOOL;
                break;
            case 2:
                this.current_tool = SELECT_BODY_TOOL;
                break;
        }
    }



}
