package ru.cracker.view.gui;

import javafx.scene.control.TextField;

import java.util.Map;

@FunctionalInterface
public interface Submitter {
  void submit(Map<String, TextField> object);
}
