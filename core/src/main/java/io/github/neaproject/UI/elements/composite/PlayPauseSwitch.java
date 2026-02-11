package io.github.neaproject.UI.elements.composite;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.neaproject.UI.elements.Control;
import io.github.neaproject.UI.elements.Switch;

public class PlayPauseSwitch extends Switch {


    public PlayPauseSwitch(String identifier, Vector2 position, float width, float height, Color color, int states) {
        super(identifier, position, width, height, color, states);
    }

    public PlayPauseSwitch(String identifier, Vector2 position, float width, float height, Color color, int states, Control parent) {
        super(identifier, position, width, height, color, states, parent);
    }

    @Override
    public void shape_render(ShapeRenderer sr) {
        super.shape_render(sr);

        sr.setColor(new Color(1, 1, 1, 1));
        Vector2 position = this.position();
        if (current_state == 0) {

        } else {
            float x = position.x + 18;
            float y = -position.y - height + 16;


        }
    }
}
