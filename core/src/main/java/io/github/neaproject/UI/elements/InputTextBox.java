package io.github.neaproject.UI.elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import io.github.neaproject.UI.interfaces.Clickable;
import io.github.neaproject.UI.interfaces.Focusable;

public class InputTextBox extends TextBox implements Clickable, Focusable {

    public interface InputReceiver {
        void receive_input(String input_text);
    }

    public static final int NONE = 0;
    public static final int NO_SYMBOLS = 1;
    public static final int LETTERS_ONLY = 2;
    public static final int NUMBERS_ONLY = 3;

    private final int MAX_LENGTH;
    private final int INPUT_RESTRICTIONS;

    private String input_text;
    public boolean focused;

    InputReceiver receiver;


    public InputTextBox(String identifier, Vector2 position, float width, float height, float scale, int max_length, int input_restrictions, Color color) {
        super(identifier, position, width, height, "", scale, Align.left, color);
        this.MAX_LENGTH = max_length;
        this.INPUT_RESTRICTIONS = input_restrictions;
        input_text = "";
        receiver = null;
    }

    public InputTextBox(String identifier, Vector2 position, float width, float height, float scale, int max_length, int input_restrictions, Color color, Control parent) {
        super(identifier, position, width, height, "", scale, Align.left, color, parent);
        this.MAX_LENGTH = max_length;
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

            switch (this.INPUT_RESTRICTIONS) {
                case NO_SYMBOLS:
                    if ("0123456789abcdefghijklmnopqrstuvwxyz".indexOf(c) == -1) {
                        builder.deleteCharAt(i);
                        i--;
                    }
                    break;
                case LETTERS_ONLY:
                    if ("abcdefghijklmnopqrstuvwxyz ".indexOf(c) == -1) {
                        builder.deleteCharAt(i);
                        i--;
                    }
                    break;
                case NUMBERS_ONLY:
                    if ("0123456789".indexOf(c) == -1) {
                        builder.deleteCharAt(i);
                        i--;
                    }
                    break;
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
        send_input();
        on_unfocus();
    }

    @Override
    public void on_focus() {
        this.focused = true;
    }

    @Override
    public void on_unfocus() {
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
        this.receiver.receive_input(input_text);
    }



}
