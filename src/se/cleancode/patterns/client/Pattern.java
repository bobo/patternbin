package se.cleancode.patterns.client;

import java.io.Serializable;

public class Pattern implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8482389183289139662L;
	private String patternText;
	private  int rows;
	private  int columns;
	private boolean isPublic;
	private  String name;
	public Pattern(String name,String patternText, int rows, int columns, boolean isPublic) {
		this.name = name;
		this.patternText = patternText;
		this.rows = rows;
		this.columns = columns;
		this.isPublic = isPublic;
		
	}
	public Pattern(String name, String patternText, int rows, int columns) {
		this(name, patternText,rows,columns,false);
	}
	
	
	public Pattern() {
	
	}	
	
	public boolean isPublic() {
		return isPublic;
	}
	public String getPatternText() {
		return patternText;
	}
	
	public int getColumns() {
		return columns;
	}
	public int getRows() {
		return rows;
	}
	public String getName() {
		return name;
	}
	
}
