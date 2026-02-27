package com.angga7togk.gamecore.api.placeholder.holder;

import cn.nukkit.Player;

import java.util.function.BiFunction;

public class VisitorPlaceholder implements Placeholder {

    private final BiFunction<Player, String[], String> handler;

    public VisitorPlaceholder(BiFunction<Player, String[], String> handler) {
        this.handler = handler;
    }

    @Override
    public String process(Player player, String[] params) {
        return handler.apply(player, params);
    }
}
