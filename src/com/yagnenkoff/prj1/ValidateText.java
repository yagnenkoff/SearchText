package com.yagnenkoff.prj1;

import javafx.scene.control.TextField;

import java.io.File;

class ValidateText {
    static boolean textFieldNotEmpty(TextField textField) {
        boolean result = false;
        if (textField.getText() != null && !textField.getText().isEmpty()) {
            result = true;
        }
        return result;
    }

    static boolean fileExist(TextField textField){
        boolean result = false;
        File f = new File(textField.getText());
        if (f.exists()){
            result = true;
        }
        return result;
    }
}