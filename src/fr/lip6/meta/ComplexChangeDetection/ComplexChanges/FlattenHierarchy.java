package fr.lip6.meta.ComplexChangeDetection.ComplexChanges;

import java.util.ArrayList;

import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.AtomicChange;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.DeleteClass;

public class FlattenHierarchy extends ComplexChange{

	private ArrayList<String> propertiesNames = null;
	private ArrayList<ComplexChange> pushes = null;	
	private String superClassName = "";
	private ArrayList<String> subClassesNames = null;
	private int deletePropertyPosition = -1;
	private DeleteClass deleteClass = null;

	public FlattenHierarchy(String name, String superClassName, ArrayList<String> subClassesNames, ArrayList<String> propertiesNames, ArrayList<ComplexChange> pushes) {
		super(name);
		// TODO Auto-generated constructor stub
		this.superClassName = superClassName;
		this.subClassesNames = subClassesNames;
		this.propertiesNames = propertiesNames;
		this.pushes = pushes;
	}

	public ArrayList<String> getPropertiesNames() {
		return propertiesNames;
	}

	public void setPropertiesNames(ArrayList<String> propertiesNames) {
		this.propertiesNames = propertiesNames;
	}

	public ArrayList<ComplexChange> getPushes() {
		return pushes;
	}

	public void setPushes(ArrayList<ComplexChange> pushes) {
		this.pushes = pushes;
	}

	public String getSuperClassName() {
		return superClassName;
	}

	public void setSuperClassName(String superClassName) {
		this.superClassName = superClassName;
	}

	public ArrayList<String> getSubClassesNames() {
		return subClassesNames;
	}

	public void setSubClassesNames(ArrayList<String> subClassesNames) {
		this.subClassesNames = subClassesNames;
	}

	public int getDeletePropertyPosition() {
		return deletePropertyPosition;
	}

	public void setDeletePropertyPosition(int deletePropertyPosition) {
		this.deletePropertyPosition = deletePropertyPosition;
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
		ComplexChange min = this.pushes.get(0);
		for(ComplexChange push : this.pushes){
			if(push instanceof PushProperty){
				if(push.startPosition() < min.startPosition()){
					min = push;
				}
			}
		}
		if(min.startPosition() < Integer.parseInt(this.deleteClass.getId()))
			return min.startPosition();
		else return Integer.parseInt(this.deleteClass.getId());
	}
	
	@Override
	public boolean doesContainAtomicChange(AtomicChange atomic){
		
		for(ComplexChange c : pushes){
				
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
		
		for(ComplexChange push : pushes){
			if(push instanceof MoveProperty){
				for(AtomicChange a : push.getAtomicChanges()){
					ac.add(a);
				}
			}
		}
		
		return ac;
	}
}
