package fr.lip6.meta.ComplexChangeDetection.ComplexChanges;

import java.util.ArrayList;

import fr.lip6.meta.ComplexChangeDetection.Change;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.AtomicChange;

public class ComplexChange extends Change{
	
	private String kind = "";
	private String name = "";
	private double  priority = -1;
	
	//here we add an attribut id so we can apply heuristics and distinguish the ones to keep (checked)
	private String id = "";
	private String treePosition = ""; // here is the position in the tree display of all complex changes
	
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
	public ComplexChange(String name) {
		super();
		this.kind = this.getClass().toString();
		this.name = name;
	}
	
	public ComplexChange() {
		super();
		this.kind = this.getClass().toString();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public int startPosition(){
		
		return -1;
	}
	public String getTreePosition() {
		return treePosition;
	}
	public void setTreePosition(String position) {
		this.treePosition = position;
	}
	
	public boolean doesContainAtomicChange(AtomicChange atomic){
		return false;
	}
	
	public ArrayList<AtomicChange> getAtomicChanges(){
		return new ArrayList<AtomicChange>();
	}
	public double  getPriority() {
		return priority;
	}
	public void setPriority(double  priority) {
		this.priority = priority;
	}
	
	
	
}
