package fr.lip6.meta.ComplexChangeDetection.ComplexChanges;

import java.util.ArrayList;

import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.AddClass;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.AtomicChange;

public class ExtractClass extends ComplexChange{

	private ArrayList<String> propertiesNames = null;
	private ArrayList<ComplexChange> moves = null;
	private String sourceClassName = "";
	private String targetClassName = "";
	private int addClassPosition = -1; //TODO to fill when instantiating ExtractClass
	private AddClass addClass = null;
	
	public ExtractClass(String name, String sourceClassName, String targetClassName, ArrayList<String> propertiesNames, ArrayList<ComplexChange> moves) {
		super(name);
		// TODO Auto-generated constructor stub
		this.sourceClassName = sourceClassName;
		this.targetClassName = targetClassName;
		this.propertiesNames = propertiesNames;
		this.moves = moves;
	}

	
	public String getSourceClassName() {
		return sourceClassName;
	}

	public void setSourceClassName(String sourceClassName) {
		this.sourceClassName = sourceClassName;
	}

	public String getTargetClassName() {
		return targetClassName;
	}

	public void setTargetClassName(String targetClassName) {
		this.targetClassName = targetClassName;
	}

	public ArrayList<String> getPropertiesNames() {
		return propertiesNames;
	}

	public void setPropertiesNames(ArrayList<String> propertiesNames) {
		this.propertiesNames = propertiesNames;
	}

	public ArrayList<ComplexChange> getMoves() {
		return moves;
	}

	public void setMoves(ArrayList<ComplexChange> moves) {
		this.moves = moves;
	}

	public int getAddClassPosition() {
		return addClassPosition;
	}

	public void setAddClassPosition(int addClassPosition) {
		this.addClassPosition = addClassPosition;
	}

	public AddClass getAddClass() {
		return addClass;
	}

	public void setAddClass(AddClass addClass) {
		this.addClass = addClass;
	}
	
	@Override
	public int startPosition(){
		
		/*if (Integer.parseInt(this.deleteProperty.getId()) < Integer.parseInt(this.addProperty.getId()))
			return Integer.parseInt(this.deleteProperty.getId());
		else
			return Integer.parseInt(this.addProperty.getId());*/
		ComplexChange min = this.moves.get(0);
		for(ComplexChange move : this.moves){
			if(move instanceof MoveProperty){
				if(move.startPosition() < min.startPosition()){
					min = move;
				}
			}
		}
		if(min.startPosition() < Integer.parseInt(this.addClass.getId()))
			return min.startPosition();
		else return Integer.parseInt(this.addClass.getId());
	}
	
	@Override
	public boolean doesContainAtomicChange(AtomicChange atomic){
		
		for(ComplexChange c : moves){
				
			if(c.doesContainAtomicChange(atomic)){
				return true;
			}  
		}
		
		return false;
	}
	
	@Override
	public ArrayList<AtomicChange> getAtomicChanges(){
		ArrayList<AtomicChange> ac = new ArrayList<AtomicChange>();
		
		ac.add(this.addClass);
		
		for(ComplexChange move : moves){
			if(move instanceof MoveProperty){
				for(AtomicChange a : move.getAtomicChanges()){
					ac.add(a);
				}
			}
		}
		
		return ac;
	}
}
