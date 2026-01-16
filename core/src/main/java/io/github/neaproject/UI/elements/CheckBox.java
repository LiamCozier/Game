package io.github.neaproject.UI.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class CheckBox extends Switch {

    private boolean checked;

    public CheckBox(String identifier, Vector2 position, float width, float height, Color color) {
        super(identifier, position, width, height, color, 2);
        super.set_state_action(0, this::check);
        super.set_state_action(1, this::uncheck);
        checked = false;
    }

    public CheckBox(String identifier, Vector2 position, float width, float height, Color color, Control parent) {
        super(identifier, position, width, height, color, 2, parent);
        super.set_state_action(0, this::check);
        super.set_state_action(1, this::uncheck);
        checked = false;
    }

    public void check() {
        checked = true;
    }

    public void uncheck() {
        checked = false;
    }

    public boolean get_checked() {
        return checked;
    }

    @Override
    public void shape_render(ShapeRenderer sr) {
        Vector2 position = super.position();
        if (checked) {
            sr.setColor(on_color);
        } else {
            sr.setColor(off_color);
        }
        sr.rect(position.x, -position.y-height, width, height);
    }
}
