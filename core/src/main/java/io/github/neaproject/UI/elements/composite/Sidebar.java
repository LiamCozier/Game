package io.github.neaproject.UI.elements.composite;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import io.github.neaproject.UI.UIAnimator;
import io.github.neaproject.UI.elements.Button;
import io.github.neaproject.UI.elements.Panel;
import io.github.neaproject.UI.elements.Switch;
import io.github.neaproject.editor.tools.EditorToolbox;

public class Sidebar {

    public static Panel editor_tool_sidebar(EditorToolbox toolbox) {
        Panel sidebar = new Panel("sidebar", new Vector2(-80,140), 80, 800, new Color(0.2f, 0.2f, 0.2f, 1f));
        Switch sidebar_switch = new Switch("sidebar_switch", new Vector2(80,336), 23, 128, new Color(0.2f, 0.2f, 0.2f, 1f), 2, sidebar);
        sidebar_switch.set_state_action(0, () -> sidebar.animator.translate(UIAnimator.EasingType.ExponentialOut, new Vector2(80, 0), 0.5f));
        sidebar_switch.set_state_action(1, () -> sidebar.animator.translate(UIAnimator.EasingType.ExponentialOut, new Vector2(-80, 0), 0.5f));

        // buttons
        Button create_tool_button = new Button("create_tool_button", new Vector2(8, 8f), 64, 64, new Color(0.4f, 0.4f, 0.4f, 1), sidebar);
        create_tool_button.set_release_action(() -> toolbox.set_tool(EditorToolbox.CREATE_BODY));
        Button select_tool_button = new Button("select_tool_button", new Vector2(8, 80f), 64, 64, new Color(0.4f, 0.4f, 0.4f, 1), sidebar);
        select_tool_button.set_release_action(() -> toolbox.set_tool(EditorToolbox.SELECT_BODY));

        return sidebar;
    }

}
