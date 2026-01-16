package io.github.neaproject.UI.elements.composite;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import io.github.neaproject.UI.elements.*;

public class BodyEditor {

    public static Panel body_editor() {

        Panel root = new Panel("body_editor_root", new Vector2(0, 0), 256, 160, new Color(0.2f, 0.2f, 0.2f, 1));
        new Panel("body_editor_drag", new Vector2(0, 0), 256, 32, new Color(0.4f, 0.4f, 0.4f, 1), root);

        return root;
    }

}
