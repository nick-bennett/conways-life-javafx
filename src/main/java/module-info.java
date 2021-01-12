module conways.life.javafx {

  requires javafx.controls;
  requires javafx.fxml;

  opens edu.cnm.deepdive.controller to javafx.fxml;
  opens edu.cnm.deepdive.view to javafx.fxml;

  exports edu.cnm.deepdive;

}