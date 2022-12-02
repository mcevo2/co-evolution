package fr.lip6.meta.ComplexChangeDetection.AtomicChanges;

public class AddSuperType extends AtomicChange{

	private String nameTarget = "";

	public String getNameTarget() {
		return nameTarget;
	}

	public void setNameTarget(String nameTarget) {
		this.nameTarget = nameTarget;
	}

	public AddSuperType() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AddSuperType(String name, String nameTarget) {
		super(name);
		// TODO Auto-generated constructor stub
		this.nameTarget =  nameTarget;
	}
	
}
