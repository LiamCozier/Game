package io.github.neaproject.UI.elements.composite;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import io.github.neaproject.UI.elements.*;

public class BodyEditor {

    public static Panel body_editor() {

        Panel root = new Panel("body_editor_root", new Vector2(0, 0), 320, 220, new Color(0.2f, 0.2f, 0.2f, 1));
        new Panel("body_editor_drag", new Vector2(0, 0), 320, 40, new Color(0.4f, 0.4f, 0.4f, 1), root);
        new TextBox("body_editor_title", new Vector2(8, 0), 320, 40, "Spawn body", 0.12f, Align.left, new Color(0.4f, 0.4f, 0.4f, 1), root);
        return root;
    }

}
