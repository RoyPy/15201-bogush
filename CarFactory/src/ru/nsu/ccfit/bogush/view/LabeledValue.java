package ru.nsu.ccfit.bogush.view;

import javax.swing.*;

public class LabeledValue extends JPanel {
	private JLabel valueLabel;
	private JLabel label;
	private JPanel panel;

	public LabeledValue(String labelText) {
		label.setText(labelText);
		label.addPropertyChangeListener(evt -> {
			if ("label".equals(evt.getPropertyName())) {
				label.setText((String) evt.getNewValue());
			}
		});
	}

	public synchronized void setEnabled(boolean enabled) {
		valueLabel.setEnabled(enabled);
	}

	public synchronized void setValue(int value) {
		setText(String.valueOf(value));
	}

	public synchronized void setText(String text) {
		valueLabel.setText(text);
	}

	public String getText() {
		return valueLabel.getText();
	}
}
