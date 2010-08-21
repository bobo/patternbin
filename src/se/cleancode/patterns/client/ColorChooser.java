package se.cleancode.patterns.client;

import com.google.gwt.user.client.ui.Button;

public class ColorChooser extends Button {

	private final String cssName;
	
	public ColorChooser(String cssName) {
		this.cssName = cssName;
		setStyleName(cssName);
		setHeight("15px");
		setWidth("15px");
		
	}
	
	public String getCssName() {
		return cssName;
	}
}
