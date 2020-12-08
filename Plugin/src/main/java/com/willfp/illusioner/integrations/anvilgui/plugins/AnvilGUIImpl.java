package com.willfp.illusioner.integrations.anvilgui.plugins;

import com.willfp.illusioner.IllusionerPlugin;
import com.willfp.illusioner.integrations.anvilgui.AnvilGUIIntegration;

/**
 * Concrete implementation of {@link AnvilGUIIntegration}
 */
public class AnvilGUIImpl implements AnvilGUIIntegration {
    private static final String ANVIL_GUI_CLASS = "net.wesjd.anvilgui.version.Wrapper" + IllusionerPlugin.NMS_VERSION.substring(1) + "$AnvilContainer";

    @Override
    public boolean isInstance(Object object) {
        return object.getClass().toString().equals(ANVIL_GUI_CLASS);
    }

    @Override
    public String getPluginName() {
        return "AnvilGUI";
    }
}
