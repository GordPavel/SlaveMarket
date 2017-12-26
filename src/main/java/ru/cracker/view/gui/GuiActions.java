package ru.cracker.view.gui;

import java.util.Arrays;
import java.util.List;

public enum GuiActions {
  MAIN(Arrays.asList("Go to profile", "Add merchandise", "Exit")),
  PROFILE(Arrays.asList("Get all deals", "Change login", "Change password", "Go back")),
  MERCHANDISE(Arrays.asList(""));

  private final List<String> actions;

  GuiActions(List<String> arrayList) {
    actions = arrayList;
  }

  public List<String> getActions() {
    return actions;
  }
}
