package fr.lip6.meta.ComplexChangeDetection.AtomicChanges;

import fr.lip6.meta.ComplexChangeDetection.Change;

public class AtomicChange extends Change{
	
	private String kind = "";
	private String name = "";

	//here we add an attribut id so we can apply heuristics and distinguish the ones to keep (checked)
	private String id = "";
		
	public AtomicChange(){
		super();
		this.kind = this.getClass().toString();
	}

	public AtomicChange(String name) {
		super();
		//this.kind = kind;
		this.kind = this.getClass().toString();
		this.name = name;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
	
	
}
