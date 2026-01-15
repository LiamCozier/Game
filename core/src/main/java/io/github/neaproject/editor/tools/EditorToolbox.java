package io.github.neaproject.editor.tools;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.github.neaproject.input.EditorInputProcessor;
import io.github.neaproject.physics.Stage;

public class EditorToolbox {

    public static final int CREATE_BODY = 1;
    public static final int SELECT_BODY = 2;

    private final CreateBodyTool CREATE_BODY_TOOL;
    private final SelectBodyTool SELECT_BODY_TOOL;

    private Stage stage;
    private OrthographicCamera camera;
    private EditorInputProcessor input;
    private EditorTool current_tool;

    private boolean dragging;

    public EditorToolbox(Stage stage, EditorInputProcessor editor_input_processor, OrthographicCamera camera) {
        this.stage = stage;
        this.input = editor_input_processor;
        this.camera = camera;

        CREATE_BODY_TOOL = new CreateBodyTool(stage);
        SELECT_BODY_TOOL = new SelectBodyTool(stage);

        current_tool = null;
        dragging = false;
    }

    public void update() {
        if (current_tool == null) return;

        Vector3 screen_mouse = new Vector3(
            input.mouse_position.x,
            input.mouse_position.y,
            0
        );

        Vector2 world_mouse_position = get_world_mouse_postition();

        if (input.left_just_released) {
            dragging = false;
            current_tool.on_release(
                Input.Buttons.LEFT,
                world_mouse_position
            );
        }

        if (input.mouse_delta.len2() != 0f) {

            Vector2 world_mouse_delta = get_world_mouse_delta();

            if (input.left_pressed) {
                dragging = true;
                current_tool.on_drag(
                    Input.Buttons.LEFT,
                    world_mouse_position,
                    world_mouse_delta
                );
            }

            current_tool.on_move(world_mouse_position);
        }
    }

    private Vector2 get_world_mouse_delta() {
        float current_screen_x = input.mouse_position.x;
        float current_screen_y = input.mouse_position.y;

        float previous_screen_x = current_screen_x - input.mouse_delta.x;
        float previous_screen_y = current_screen_y - input.mouse_delta.y;

        Vector3 previous_world_mouse_3 = camera.unproject(
            new Vector3(previous_screen_x, previous_screen_y, 0)
        );

        Vector3 current_world_mouse_3 = camera.unproject(
            new Vector3(current_screen_x, current_screen_y, 0)
        );

        return new Vector2(
            current_world_mouse_3.x - previous_world_mouse_3.x,
            current_world_mouse_3.y - previous_world_mouse_3.y
        );
    }

    private Vector2 get_world_mouse_postition() {
        Vector3 screen_mouse = new Vector3(
            input.mouse_position.x,
            input.mouse_position.y,
            0
        );

        Vector3 world_mouse_3 = camera.unproject(screen_mouse);
        return new Vector2(
            world_mouse_3.x,
            world_mouse_3.y
        );
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
