package se.cleancode.patterns.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Button;




public class ColorButton extends Button {

	public static String currentCSS="black";
	
	public ColorButton(String s) {
		super("");
		removeColor();
		setHeight("20px");
		setWidth("20px");
		addMouseOverHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				if(event.isControlKeyDown()) {
					addColor();
				} else if (event.isAltKeyDown()) {
					removeColor();
				}
				
			}
		});
	addClickHandler(new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			updateColor();
		}

		
	});
	}
	private void updateColor() {
		if(getStyleName().equals(currentCSS))
			removeColor();
		else
			addColor();
	}
	private void addColor() {
		setStyleName(currentCSS);
	}
	private void removeColor() {
		setStyleName("white");
	}
}
