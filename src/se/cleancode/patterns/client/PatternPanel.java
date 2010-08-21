package se.cleancode.patterns.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PatternPanel extends VerticalPanel {

	private Grid grid = new Grid(20, 20);
	private final Grid menu = new Grid(3, 4);
	private final TextBox rows = new TextBox();
	private final TextBox columns = new TextBox();
	private final Button gridUpdateBtn = new Button("Update Size");
	private final TextBox loadName = new TextBox();
	private final TextBox patternName = new TextBox();
	private final Button save = new Button("Save");
	private final Button load = new Button("Load");
	private final Label savedAss = new Label();
	private final Label loaded = new Label();
	private final HorizontalPanel colorPanel = new HorizontalPanel();
	private final HorizontalPanel latestPanel = new HorizontalPanel();
	private final CheckBox publicCheckBox = new CheckBox("public");
	private final PatternServiceAsync patternService = GWT
			.create(PatternService.class);

	public PatternPanel() {
		add(menu);
		add(latestPanel);
		add(colorPanel);
		fillWithButtons(20, 20);
		buildMenu();
		addColors();
		addHandlers();
		preLoadImage();
		loadLatest();
		rows.setMaxLength(2);
		columns.setMaxLength(2);
		rows.setWidth("30px");
		columns.setWidth("30px");
			
	}
	
	private void loadLatest() {
		latestPanel.setSpacing(5);
		patternService.getLatestPublic(new AsyncCallback<List<String>>() {
			
			@Override
			public void onSuccess(List<String> result) {
				latestPanel.clear();
				latestPanel.add(new Label("Latest addtions: "));
				String url = com.google.gwt.user.client.Window.Location.getPath();
				for(String name : result) {
					HTML link = new HTML("<a href="+url+"?name="+name+">"+name+"</a>");
					latestPanel.insert(link,1);
				}
				
			}
			@Override
			public void onFailure(Throwable caught) {
				latestPanel.add(new Label(caught.getMessage()));
			}
		});
	}

	private void addHandlers() {
		load.addClickHandler(new ClickToLoadHanddler());
		loadName.addValueChangeHandler(new LoadHandler());
		save.addClickHandler(new SaveHandler());
		gridUpdateBtn.addClickHandler(new UpdateSizeHandler());
	}

	private void buildMenu() {
		menu.setWidget(0, 0, patternName);
		
		menu.setWidget(0, 1, save);
		menu.setWidget(0, 2, publicCheckBox);
		
		menu.setWidget(0, 3, savedAss);
		
		menu.setWidget(1, 0, loadName);
		menu.setWidget(1, 1, load);
		menu.setWidget(1, 2, new Button("clear"));
		menu.setWidget(1, 3, loaded);

		menu.setWidget(2, 0, columns);
		menu.setWidget(2, 1, rows);
		menu.setWidget(2, 2, gridUpdateBtn);
	}

	private void addColors() {
		for (Colors c : Colors.values()) {
			ColorChooser b = new ColorChooser(c.toString());
			b.addClickHandler(colorHandler);
			colorPanel.add(b);
		}
	}

	private void preLoadImage() {
		String value = com.google.gwt.user.client.Window.Location
				.getParameter("name");
		if (value != null) {
			loadName.setText(value);
			loadFromDB();
		}
	}

	private void fillWithButtons(int rows, int columns) {
		remove(grid);
		rows = rows > 50 ? 50 : rows;
		columns = columns > 50 ? 50 : columns;
		grid = new Grid(rows, columns);
		grid.setCellPadding(0);
		grid.setCellSpacing(0);
		add(grid);
		for (int row = 0; row < grid.getRowCount(); row++) {
			for (int column = 0; column < grid.getColumnCount(); column++) {
				grid.setWidget(row, column, new ColorButton(""));
			}
		}
	}

	private void displayResult(Pattern pattern) {
		String result = pattern.getPatternText();

		if (result == null || result.isEmpty()) {
			loaded.setText("none found");
		} else {
			fillWithButtons(pattern.getRows(), pattern.getColumns());
			String[] split = result.split(";");
			displayPattern(split);
		}
		clearTimer.schedule(1000 * 20);
	}

	private void displayPattern(String[] split) {
		for (int row = 0; row < split.length; row++) {
			String rowS = split[row];
			String[] split2 = rowS.split(",");
			for (int column = 0; column < split2.length; column++) {
				String s = split2[column];
				ColorButton b = new ColorButton("");
				b.setStyleName(getStyleNameOf(s));
				grid.setWidget(row, column, b);
			}
			loaded.setText("done loading");
		}
	}

	private String getStyleNameOf(String s) {
		try {
			int intval = Integer.parseInt(s);
			return Colors.values()[intval].toString();
		} catch (Exception e) {
		}
		return Colors.valueOf(s).toString();
	}

	private void loadFromDB() {
		patternService.get(loadName.getText(), new LoaedCallback());
	}

	private final class LoaedCallback implements AsyncCallback<Pattern> {
		@Override
		public void onSuccess(Pattern result) {
			displayResult(result);
		}

		@Override
		public void onFailure(Throwable caught) {

		}
	}

	private final class UpdateSizeHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			fillWithButtons(Integer.parseInt(rows.getText()), Integer
					.parseInt(columns.getText()));
		}
	}

	private final class ClickToLoadHanddler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			clearTimer.run();
			loadFromDB();
		}
	}

	private final class SaveHandler implements ClickHandler {

		private final class PatternStoredCallback implements
				AsyncCallback<String> {
			@Override
			public void onSuccess(String result) {
				savedAss.setText("Saved as: " + result);
				loadLatest();
				clearTimer.schedule(1000 * 20);
			}

			@Override
			public void onFailure(Throwable caught) {
				savedAss.setText("NOT SAVED, Name taken, choose another one.");
			}
		}

		@Override
		public void onClick(ClickEvent event) {
			clearTimer.run();
			StringBuilder sb = createPatternString();
			Pattern p = new Pattern(patternName.getText(),sb.toString(), grid.getRowCount(), grid
					.getColumnCount(),publicCheckBox.getValue());
			patternService.storePattern(p, patternName.getText(),
					new PatternStoredCallback());
		}

		private StringBuilder createPatternString() {
			StringBuilder sb = new StringBuilder();
			for (int row = 0; row < grid.getRowCount(); row++) {
				for (int column = 0; column < grid.getColumnCount(); column++) {
					Widget b = (Widget) grid.getWidget(row, column);
					sb.append(getStyleNumber(b)).append(",");
				}
				sb.append(";");
			}
			return sb;
		}

		private int getStyleNumber(Widget b) {
			return Colors.valueOf(b.getStyleName()).ordinal();
		}
	}

	private final Timer clearTimer = new Timer() {

		@Override
		public void run() {
			savedAss.setText("");
			loaded.setText("");
		}
	};

	private final class LoadHandler implements ValueChangeHandler<String> {

		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			clearTimer.run();
			loadFromDB();
		}
	}

	private ClickHandler colorHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			ColorChooser btn = (ColorChooser) event.getSource();
			ColorButton.currentCSS = btn.getCssName();

		}
	};

}
