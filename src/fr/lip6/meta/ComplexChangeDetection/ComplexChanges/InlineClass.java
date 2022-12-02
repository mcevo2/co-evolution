package fr.lip6.meta.ComplexChangeDetection.ComplexChanges;

import java.util.ArrayList;

import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.AddProperty;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.AtomicChange;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.DeleteClass;

public class InlineClass extends ComplexChange{

	private ArrayList<String> propertiesNames = null;
	private ArrayList<ComplexChange> inverseMoves = null;
	private String sourceClassName = "";
	private String targetClassName = "";
	private int addClassPosition = -1; //TODO to fill when instantiating InlineClass
	private DeleteClass deleteClass = null;
	
	public InlineClass(String name, String sourceClassName, String targetClassName, ArrayList<String> propertiesNames, ArrayList<ComplexChange> inverseMoves) {
		super(name);
		// TODO Auto-generated constructor stub
		this.sourceClassName = sourceClassName;
		this.targetClassName = targetClassName;
		this.propertiesNames = propertiesNames;
		this.inverseMoves = inverseMoves;
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

	public ArrayList<ComplexChange> getInverseMoves() {
		return inverseMoves;
	}

	public void setInverseMoves(ArrayList<ComplexChange> inverseMoves) {
		this.inverseMoves = inverseMoves;
	}

	public int getAddClassPosition() {
		return addClassPosition;
	}

	public void setAddClassPosition(int addClassPosition) {
		this.addClassPosition = addClassPosition;
	}

	public DeleteClass getDeleteClass() {
		return deleteClass;
	}

	public void setDeleteClass(DeleteClass deleteClass) {
		this.deleteClass = deleteClass;
	}
	
	@Override
	public int startPosition(){
		
		/*if (Integer.parseInt(this.deleteProperty.getId()) < Integer.parseInt(this.addProperty.getId()))
			return Integer.parseInt(this.deleteProperty.getId());
		else
			return Integer.parseInt(this.addProperty.getId());*/
		ComplexChange min = this.inverseMoves.get(0);
		for(ComplexChange imove : this.inverseMoves){
			if(imove instanceof InverseMoveProperty){
				if(imove.startPosition() < min.startPosition()){
					min = imove;
				}
			}
		}
		if(min.startPosition() < Integer.parseInt(this.deleteClass.getId()))
			return min.startPosition();
		else return Integer.parseInt(this.deleteClass.getId());
	}
	
	@Override
	public boolean doesContainAtomicChange(AtomicChange atomic){
		
		for(ComplexChange c : inverseMoves){
				
			if(c.doesContainAtomicChange(atomic)){
				return true;
			}  
		}
		
		return false;
	}
	
	@Override
	public ArrayList<AtomicChange> getAtomicChanges(){
		ArrayList<AtomicChange> ac = new ArrayList<AtomicChange>();
		
		ac.add(this.deleteClass);
		
		for(ComplexChange imove : inverseMoves){
			if(imove instanceof MoveProperty){
				for(AtomicChange a : imove.getAtomicChanges()){
					ac.add(a);
				}
			}
		}
		
		return ac;
	}
	
}
