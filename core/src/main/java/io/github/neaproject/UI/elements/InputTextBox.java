package io.github.neaproject.UI.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import io.github.neaproject.UI.interfaces.Clickable;

public class InputTextBox extends TextBox implements Clickable {

    String input_text;

    public InputTextBox(String identifier, Vector2 position, float width, float height, float scale, Color color) {
        super(identifier, position, width, height, "", scale, Align.left, color);
        input_text = "";
    }

    public InputTextBox(String identifier, Vector2 position, float width, float height, float scale, Color color, Control parent) {
        super(identifier, position, width, height, "", scale, Align.left, color, parent);
        input_text = "";
    }

    @Override
    public void shape_render(ShapeRenderer sr) {
        Vector2 position = this.position();
        sr.setColor(new Color(0.1f, 0.1f, 0.1f, 1));
        sr.rect(position.x, -position.y-height, width, height);
    }

    @Override
    public void on_click() {

    }

    @Override
    public void on_release() {

    }

    @Override
    public boolean is_holding() {
        return false;
    }

    public void type(String text) {
        input_text += text;
        super.set_text(input_text);
    }
}
