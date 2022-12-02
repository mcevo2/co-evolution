package fr.lip6.meta.ComplexChangeDetection.AtomicChanges;

public class RenameProperty extends AtomicChange{

	private String newname = "";
	private String className = "";

	public RenameProperty() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getNewname() {
		return newname;
	}

	public void setNewname(String newname) {
		this.newname = newname;
	}

	public RenameProperty(String oldName, String newname, String className) {
		super(oldName);
		this.newname = newname;
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}
