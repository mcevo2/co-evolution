package fr.lip6.meta.ComplexChangeDetection.AtomicChanges;

public class DeleteSuperType extends AtomicChange{

	private String nameTarget = "";

	private String getNameTarget() {
		return nameTarget;
	}

	public void setNameTarget(String nameTarget) {
		this.nameTarget = nameTarget;
	}

	public DeleteSuperType() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DeleteSuperType(String name, String nameTarget) {
		super(name);
		// TODO Auto-generated constructor stub
		this.nameTarget =  nameTarget;
	}
}
