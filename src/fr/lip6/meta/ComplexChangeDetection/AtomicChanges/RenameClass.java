package fr.lip6.meta.ComplexChangeDetection.AtomicChanges;

public class RenameClass extends AtomicChange{

	private String newname = "";
	private String packageName = "";

	public RenameClass() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getNewname() {
		return newname;
	}

	public void setNewname(String newname) {
		this.newname = newname;
	}

	public RenameClass(String oldName, String newname, String packageName) {
		super(oldName);
		this.newname = newname;
		this.packageName = packageName;
	}
	
}
