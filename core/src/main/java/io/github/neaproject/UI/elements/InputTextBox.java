package io.github.neaproject.UI.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import io.github.neaproject.UI.interfaces.Clickable;
import io.github.neaproject.UI.interfaces.Focusable;

import java.util.HashMap;
import java.util.Map;

public class InputTextBox extends TextBox implements Clickable, Focusable {

    public interface InputReceiver {
        void receive_input(String field_id, String input_text);
    }

    public enum InputFilter {
        NONE, NO_SYMBOLS, LETTERS, INTEGER, FLOAT
    }

    public static final Map<InputFilter, String> input_filter_map = new HashMap<>();
    static {
        input_filter_map.put(InputFilter.NONE, "");
        input_filter_map.put(InputFilter.NO_SYMBOLS, "0123456789abcdefghijklmnopqrstuvwxyz");
        input_filter_map.put(InputFilter.LETTERS, "abcdefghijklmnopqrstuvwxyz");
        input_filter_map.put(InputFilter.INTEGER, "0123456789");
        input_filter_map.put(InputFilter.FLOAT, "0123456789.");
    }

    private final int MAX_LENGTH;
    private final InputFilter INPUT_RESTRICTIONS;

    public String field_id;
    private String input_text;
    public boolean focused;

    InputReceiver receiver;


    public InputTextBox(String identifier, String field_id, Vector2 position, float width, float height, float scale, int max_length, InputFilter input_restrictions, Color color) {
        super(identifier, position, width, height, "", scale, Align.center, color);
        this.MAX_LENGTH = max_length;
        this.field_id = field_id;
        this.INPUT_RESTRICTIONS = input_restrictions;
        input_text = "";
        receiver = null;
    }

    public InputTextBox(String identifier, String field_id, Vector2 position, float width, float height, float scale, int max_length, InputFilter input_restrictions, Color color, Control parent) {
        super(identifier, position, width, height, "", scale, Align.center, color, parent);
        this.MAX_LENGTH = max_length;
        this.field_id = field_id;
        this.INPUT_RESTRICTIONS = input_restrictions;
        input_text = "";
        receiver = null;
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
        StringBuilder builder = new StringBuilder(text);

        for (int i = 0; i<builder.length(); i++) {
            char c = builder.charAt(i);

            // entry
            if (c == '\n') {
                this.enter();
                return;
            }

            // allow backspaces
            if (c == '\b') {
                this.backspace();
                builder.deleteCharAt(i);
                i--;
                continue;
            }

            String filter_string = input_filter_map.get(INPUT_RESTRICTIONS);
            if (filter_string.indexOf(c) == -1) {
                builder.deleteCharAt(i);
                i--;
            }
        }
        input_text += builder;
        if (input_text.length() > MAX_LENGTH) input_text = input_text.substring(0, MAX_LENGTH);
        super.set_text(input_text);
    }

    private void backspace() {
        if (!input_text.isEmpty()) input_text = input_text.substring(0, input_text.length() - 1);
    }

    private void enter() {
        on_unfocus();
    }

    @Override
    public void on_focus() {
        this.focused = true;
    }

    @Override
    public void on_unfocus() {
        send_input();
        this.focused = false;
    }

    @Override
    public boolean is_focused() {
        return focused;
    }

    public void set_input_location(InputReceiver receiver) {
        this.receiver = receiver;
    }

    public void send_input() {
        this.receiver.receive_input(field_id, input_text);
    }



}
