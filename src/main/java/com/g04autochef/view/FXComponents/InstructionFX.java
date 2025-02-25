package com.g04autochef.view.FXComponents;

import javafx.scene.control.TextField;

/**
 * Class that will construct an Interactive View of instructions
 */
public class InstructionFX {

    private final TextField instructionTextField;

    public InstructionFX(String instruction) {
        this.instructionTextField = new TextField(instruction);
    }

    public InstructionFX() {this("");}

    @SuppressWarnings("unused")
    public TextField getInstructionTextField() {
        return instructionTextField;
    }

    public String getInstructionString() {
        return instructionTextField.getText();
    }
}
