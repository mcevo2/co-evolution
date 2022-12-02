package fr.lip6.meta.ComplexChangeDetection.ComplexChanges;

import java.util.ArrayList;

import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.AddProperty;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.AtomicChange;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.DeleteProperty;

public class PullProperty extends ComplexChange{
	
	private String superClassName = "";
	private ArrayList<String> subClassesNames = null;
	private int addPropertyPosition = -1;
	//public int first = -1;
	//public int last = -1;
	
	private ArrayList<DeleteProperty> deleteProperties = null;
	private AddProperty addProperty = null;
	
	public PullProperty(String name, String superClassName,
			ArrayList<String> subClassesNames) {
		super(name);
		this.superClassName = superClassName;
		this.subClassesNames = subClassesNames;
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
	public ArrayList<DeleteProperty> getDeleteProperties() {
		return deleteProperties;
	}
	public void setDeleteProperties(ArrayList<DeleteProperty> deleteProperties) {
		this.deleteProperties = deleteProperties;
	}
	
	public AddProperty getAddProperty() {
		return addProperty;
	}
	public void setAddProperty(AddProperty addProperty) {
		this.addProperty = addProperty;
	}
	
	@Override
	public int startPosition(){
		DeleteProperty min = this.deleteProperties.get(0);
		for(DeleteProperty d : this.deleteProperties){
			if(Integer.parseInt(d.getId()) < Integer.parseInt(min.getId())){
				min = d;
			}
		}
		
		if (Integer.parseInt(min.getId()) < Integer.parseInt(this.addProperty.getId()))
			return Integer.parseInt(min.getId());
		else
			return Integer.parseInt(this.addProperty.getId());

	}
	
	@Override
	public boolean doesContainAtomicChange(AtomicChange atomic){
		
		for(DeleteProperty d : deleteProperties){
				
			if(d.equals(atomic)){
				return true;
			}  
		}
		
		if(this.addProperty.equals(atomic)){
			return true;
		}
		return false;
	}
	
	@Override
	public ArrayList<AtomicChange> getAtomicChanges(){
		ArrayList<AtomicChange> ac = new ArrayList<AtomicChange>();
		
		
		ac.add(this.addProperty);
		
		for(DeleteProperty d : deleteProperties){
			ac.add(d);
		}
		
		return ac;
	}
	

}
