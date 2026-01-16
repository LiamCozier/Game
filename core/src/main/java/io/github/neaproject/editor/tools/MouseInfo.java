package io.github.neaproject.editor.tools;

import com.badlogic.gdx.math.Vector2;

public class MouseInfo {

    public final Vector2 world_position;
    public final Vector2 screen_position;
    public final Vector2 mouse_delta;

    public MouseInfo(Vector2 world_position, Vector2 screen_position, Vector2 mouse_delta) {
        this.world_position = world_position;
        this.screen_position = screen_position;
        this.mouse_delta = mouse_delta;
    }
}
