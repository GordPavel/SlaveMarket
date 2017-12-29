package view;

import javafx.scene.control.TextField;

import java.util.Map;

@FunctionalInterface
public interface Submitter {
  void submit(Map<String, TextField> object);
}
