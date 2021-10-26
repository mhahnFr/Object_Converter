package hahn.mainIDE;

import hahn.mainIDE.gui.ClassPropertyValue;

public abstract class ClassProperty implements Convertable {
	protected Type type;
	protected String name;
	
	public String getName() {
		return name;
	}
	
	public Type getType() {
		return type;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public abstract ClassPropertyValue getValue();
	
	public enum Type {
		/**
		 * 1
		 */
		BOOLEAN,
		/**
		 * 2
		 */
		STRING,
		/**
		 * 3
		 */
		INTEGER,
		/**
		 * 4
		 */
		LONG,
		/**
		 * 5
		 */
		SHORT,
		/**
		 * 6
		 */
		DOUBLE,
		/**
		 * 7
		 */
		FLOAT,
		/**
		 * 8
		 */
		BYTE,
		UNKNOWN;
	}
}