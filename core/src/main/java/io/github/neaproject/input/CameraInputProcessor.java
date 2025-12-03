package io.github.neaproject.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class CameraInputProcessor implements InputProcessor {

    public OrthographicCamera camera;

    public boolean middle_pressed = false;
    public float scroll_amount = 0;
    public Vector2 delta = new Vector2();
    public Vector2 mouse_screen = new Vector2();
    private Vector2 last = new Vector2();


    public CameraInputProcessor(OrthographicCamera camera) {
        this.camera = camera;

        mouse_screen.x = Gdx.input.getX() - Gdx.graphics.getWidth()/2f;
        mouse_screen.y = Gdx.input.getY() - Gdx.graphics.getHeight()/2f;
        last.x = Gdx.input.getX();
        last.y = Gdx.input.getY();
    }

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
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.MIDDLE || button == Input.Buttons.RIGHT) {
            middle_pressed = true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.MIDDLE || button == Input.Buttons.RIGHT) {
            middle_pressed = false;
        }
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return mouseMoved(screenX, screenY);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mouse_screen.y = screenY;
        mouse_screen.x = screenX;
        delta.x = screenX - last.x;
        delta.y = screenY - last.y;
        last.x = screenX;
        last.y = screenY;

        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        scroll_amount += amountY;
        return false;
    }
}
