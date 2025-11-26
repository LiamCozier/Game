package io.github.neaproject.physics.tools;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class InputToolbox {

    private Tool current;

    public void set_tool(Tool tool) {
        this.current = tool;
    }

    public void update(float dt) {
        if (current != null)
            current.update(dt);
    }

    public void render(ShapeRenderer sr) {
        if (current != null)
            current.render(sr);
    }
}
