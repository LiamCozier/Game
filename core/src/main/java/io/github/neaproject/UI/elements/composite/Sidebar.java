package io.github.neaproject.UI.elements.composite;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github.neaproject.UI.UIAnimator;
import io.github.neaproject.UI.elements.Button;
import io.github.neaproject.UI.elements.Control;
import io.github.neaproject.UI.elements.Panel;
import io.github.neaproject.UI.elements.Switch;
import io.github.neaproject.editor.tools.EditorToolbox;

public class Sidebar {

    public static Panel editor_tool_sidebar(EditorToolbox toolbox) {
        Panel sidebar = new Panel("sidebar", new Vector2(-80, 140), 80, 800, new Color(0.2f, 0.2f, 0.2f, 1f));
        Switch sidebar_switch = new Switch("sidebar_switch", new Vector2(80, 336), 23, 128, new Color(0.2f, 0.2f, 0.2f, 1f), 2, sidebar);
        sidebar_switch.set_state_action(0, () -> sidebar.animator.translate(UIAnimator.EasingType.ExponentialOut, new Vector2(80, 0), 0.5f));
        sidebar_switch.set_state_action(1, () -> sidebar.animator.translate(UIAnimator.EasingType.ExponentialOut, new Vector2(-80, 0), 0.5f));

        // buttons
        Button create_tool_button = new Button("create_tool_button", new Vector2(8, 8f), 64, 64, new Color(0.4f, 0.4f, 0.4f, 1), sidebar);
        create_tool_button.set_release_action(() -> toolbox.set_tool(EditorToolbox.CREATE_BODY));
        Button select_tool_button = new Button("select_tool_button", new Vector2(8, 80f), 64, 64, new Color(0.4f, 0.4f, 0.4f, 1), sidebar);
        select_tool_button.set_release_action(() -> toolbox.set_tool(EditorToolbox.SELECT_BODY));
        Switch play_pause_switch = new Switch("play_pause_switch", new Vector2(8, 152f), 64, 64, new Color(0.4f, 0.4f, 0.4f, 1), 2, sidebar);
        new Button("reset_button", new Vector2(8, 224f), 64, 64, new Color(0.4f, 0.4f, 0.4f, 1), sidebar);
        new Button("save_button", new Vector2(8, 296f), 64, 64, new Color(0.4f, 0.4f, 0.4f, 1), sidebar);

        create_tool_button.set_icon_renderer(
            (ShapeRenderer sr, Vector2 position) -> {

                // box outline
                sr.rect(position.x + 16f, position.y + 16f, 32f, 4f);
                sr.rect(position.x + 16f, position.y + 44f, 32f, 4f);
                sr.rect(position.x + 16f, position.y + 16f, 4f, 32f);
                sr.rect(position.x + 44f, position.y + 16f, 4f, 32f);

                // plus
                sr.rect(position.x + 30f, position.y + 24f, 4f, 16f);
                sr.rect(position.x + 24f, position.y + 30f, 16f, 4f);
            }
        );
        select_tool_button.set_icon_renderer(
            (ShapeRenderer sr, Vector2 position) -> {
                sr.rect(position.x + 16f, position.y + 16f, 12f, 4f);
                sr.rect(position.x + 36f, position.y + 16f, 12f, 4f);
                sr.rect(position.x + 16f, position.y + 44f, 12f, 4f);
                sr.rect(position.x + 36f, position.y + 44f, 12f, 4f);

                sr.rect(position.x + 16f, position.y + 20f, 4f, 8f);
                sr.rect(position.x + 16f, position.y + 36f, 4f, 12f);
                sr.rect(position.x + 44f, position.y + 20f, 4f, 8f);
                sr.rect(position.x + 44f, position.y + 36f, 4f, 12f);
            }
        );

        play_pause_switch.set_state_renderer(0,
            (ShapeRenderer sr, Vector2 position) -> sr.triangle(
                position.x + 48f, position.y + 32f,
                position.x + 16f, position.y + 48f,
                position.x + 16f, position.y + 16f
            ));
        play_pause_switch.set_state_renderer(1,
            (ShapeRenderer sr, Vector2 position) -> {
                sr.rect(position.x + 16f, position.y + 16f, 8f, 32f);
                sr.rect(position.x + 36f, position.y + 16f, 8f, 32f);
            });

        return sidebar;
    }

}
