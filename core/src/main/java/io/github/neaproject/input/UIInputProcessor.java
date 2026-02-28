package io.github.neaproject.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

public class UIInputProcessor implements InputProcessor {

    public Vector2 mouse_position = new Vector2(Gdx.input.getX(), Gdx.input.getY());

    public StringBuilder type_text = new StringBuilder();

    public boolean left_pressed = false;
    public boolean left_just_pressed = false;
    public boolean left_just_released = false;
    public boolean left_pressed_prev = false;

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        type_text.append(character);
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            left_pressed = true;
            left_just_pressed = true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT){
            left_pressed = false;
            left_just_released = true;
        }
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mouse_position.set(screenX, screenY);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public void begin_frame() {

        // resets at start of each frame so that the input only lasts for one
        left_just_pressed  = left_pressed && !left_pressed_prev;
        left_just_released = !left_pressed && left_pressed_prev;
        left_pressed_prev = left_pressed;
    }
}
