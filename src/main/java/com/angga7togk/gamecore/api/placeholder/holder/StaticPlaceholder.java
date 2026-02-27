package com.angga7togk.gamecore.api.placeholder.holder;

import cn.nukkit.Player;
import java.util.function.Supplier;

public class StaticPlaceholder implements Placeholder {

  private final Supplier<String> supplier;

  public StaticPlaceholder(Supplier<String> supplier) {
    this.supplier = supplier;
  }

  @Override
  public String process(Player player, String[] params) {
    return supplier.get();
  }
}
