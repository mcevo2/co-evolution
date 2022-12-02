package fr.lip6.meta.ComplexChangeDetection.ComplexChanges;

import java.util.ArrayList;

import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.AddClass;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.AtomicChange;

public class ExtractSuperClass extends ComplexChange{

	private ArrayList<String> propertiesNames = null;
	private ArrayList<ComplexChange> pulles = null;	
	private String superClassName = "";
	private ArrayList<String> subClassesNames = null;
	private int addPropertyPosition = -1;
	private AddClass addClass = null;

	public ExtractSuperClass(String name, String superClassName, ArrayList<String> subClassesNames, ArrayList<String> propertiesNames, ArrayList<ComplexChange> pulles) {
		super(name);
		// TODO Auto-generated constructor stub
		this.superClassName = superClassName;
		this.subClassesNames = subClassesNames;
		this.propertiesNames = propertiesNames;
		this.pulles = pulles;
	}

	public ArrayList<String> getPropertiesNames() {
		return propertiesNames;
	}

	public void setPropertiesNames(ArrayList<String> propertiesNames) {
		this.propertiesNames = propertiesNames;
	}

	public ArrayList<ComplexChange> getPulles() {
		return pulles;
	}

	public void setPulles(ArrayList<ComplexChange> pulles) {
		this.pulles = pulles;
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

	public int getAddPropertyPosition() {
		return addPropertyPosition;
	}

	public void setAddPropertyPosition(int addPropertyPosition) {
		this.addPropertyPosition = addPropertyPosition;
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
		ComplexChange min = this.pulles.get(0);
		for(ComplexChange pull : this.pulles){
			if(pull instanceof PullProperty){
				if(pull.startPosition() < min.startPosition()){
					min = pull;
				}
			}
		}
		if(min.startPosition() < Integer.parseInt(this.addClass.getId()))
			return min.startPosition();
		else return Integer.parseInt(this.addClass.getId());
	}
	
	@Override
	public boolean doesContainAtomicChange(AtomicChange atomic){
		
		for(ComplexChange c : pulles){
				
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
		
		for(ComplexChange pull : pulles){
			if(pull instanceof MoveProperty){
				for(AtomicChange a : pull.getAtomicChanges()){
					ac.add(a);
				}
			}
		}
		
		return ac;
	}
}
