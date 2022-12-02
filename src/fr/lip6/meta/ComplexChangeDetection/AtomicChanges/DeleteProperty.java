package fr.lip6.meta.ComplexChangeDetection.AtomicChanges;

public class DeleteProperty extends AtomicChange{

	private String className = "";

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public DeleteProperty() { 
		super();
		// TODO Auto-generated constructor stub
	}

	public DeleteProperty(String name, String className) {
		super(name);
		// TODO Auto-generated constructor stub
		this.className = className;
	}
	
	
}
 