package com.hfjy.framework.view.factory;

import java.awt.Font;

import javax.swing.JLabel;

public class SwingContainerFactory {

	public static Font getFont() {
		Font font = new Font("", Font.PLAIN, 12);
		return font;
	}

	public static JLabel getJLabel(String text, int horizontalAlignment) {
		return getJLabel(text, horizontalAlignment, getFont());
	}

	public static JLabel getJLabel(String text, int horizontalAlignment, Font font) {
		JLabel label = new JLabel(text, horizontalAlignment);
		label.setFont(font);
		return label;
	}

	public static JLabel getJLabel() {
		JLabel label = new JLabel();
		label.setFont(getFont());
		return label;
	}
}
