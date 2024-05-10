package org.example.sanitycheck;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class SanityCheckConfigurable implements Configurable {
    private JTextField modelNameField;
    private JTextField apiUrlField;
    private JPanel mainPanel;

    @Override
    public String getDisplayName() {
        return "Sanity Check Plugin Settings";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        modelNameField = new JTextField();
        apiUrlField = new JTextField();

        // Set the preferred size of the text fields to make them appear as one line
        modelNameField.setPreferredSize(new Dimension(200, 20)); // Width of 200 pixels and height of 20 pixels
        apiUrlField.setPreferredSize(new Dimension(200, 20));

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(new JLabel("Model Name:"));
        mainPanel.add(modelNameField);
        mainPanel.add(new JLabel("API URL:"));
        mainPanel.add(apiUrlField);
        reset();
        return mainPanel;
    }

    @Override
    public boolean isModified() {
        SanityCheckSettings settings = SanityCheckSettings.getInstance();
        return !modelNameField.getText().equals(settings.modelName) ||
                !apiUrlField.getText().equals(settings.apiUrl);
    }

    @Override
    public void apply() {
        SanityCheckSettings settings = SanityCheckSettings.getInstance();
        settings.modelName = modelNameField.getText();
        settings.apiUrl = apiUrlField.getText();
    }

    @Override
    public void reset() {
        SanityCheckSettings settings = SanityCheckSettings.getInstance();
        modelNameField.setText(settings.modelName);
        apiUrlField.setText(settings.apiUrl);
    }

    @Override
    public void disposeUIResources() {
        mainPanel = null;
    }
}
