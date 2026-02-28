package io.github.neaproject.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

public class EditorInputProcessor implements InputProcessor {

    public final Vector2 mouse_position = new Vector2();
    public final Vector2 prev_mouse_position = new Vector2();
    public final Vector2 mouse_delta = new Vector2();

    public boolean left_pressed = false;
    public boolean left_just_pressed = false;
    public boolean left_just_released = false;

    private boolean left_pressed_prev = false;
    private final Vector2 accum_delta = new Vector2();

    public EditorInputProcessor() {
        mouse_position.set(Gdx.input.getX(), Gdx.input.getY());
        prev_mouse_position.set(mouse_position);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) left_pressed = true;
        mouse_position.set(screenX, screenY);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) left_pressed = false;
        mouse_position.set(screenX, screenY);
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) left_pressed = false;
        mouse_position.set(screenX, screenY);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        accum_delta.add(screenX - mouse_position.x, screenY - mouse_position.y);
        mouse_position.set(screenX, screenY);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        accum_delta.add(screenX - mouse_position.x, screenY - mouse_position.y);
        mouse_position.set(screenX, screenY);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public void begin_frame() {
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();

        float dx = x - mouse_position.x;
        float dy = y - mouse_position.y;

        if (dx != 0 || dy != 0) {
            accum_delta.add(dx, dy);
            mouse_position.set(x, y);
        }

        mouse_delta.set(accum_delta);
        accum_delta.setZero();

        prev_mouse_position.set(mouse_position);

        left_just_pressed = left_pressed && !left_pressed_prev;
        left_just_released = !left_pressed && left_pressed_prev;
        left_pressed_prev = left_pressed;
    }

    public void sync_mouse() {
        mouse_position.set(Gdx.input.getX(), Gdx.input.getY());
        prev_mouse_position.set(mouse_position);
        mouse_delta.setZero();
        accum_delta.setZero();
    }
}
