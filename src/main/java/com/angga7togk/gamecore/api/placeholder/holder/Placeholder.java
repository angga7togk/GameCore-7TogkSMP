package com.angga7togk.gamecore.api.placeholder.holder;

import cn.nukkit.Player;

public interface Placeholder {

    /**
     * Process the placeholder.
     *
     * @param player The player context (can be null for static placeholders)
     * @return The processed value of the placeholder
     */
    String process(Player player, String[] params);
}
