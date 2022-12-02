package fr.lip6.meta.ComplexChangeDetection.ComplexChanges;

import java.util.ArrayList;

import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.AddProperty;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.AtomicChange;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.DeleteProperty;

public class PushProperty extends ComplexChange{

	private String superClassName = "";
	private ArrayList<String> subClassesNames = null;
	private int deletePropertyPosition = -1;
	//public int first = -1;
	//public int last = -1;
	
	private DeleteProperty deleteProperty = null;
	private ArrayList<AddProperty> addProperties = null;
	
	public PushProperty(String name, String superClassName,
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
	public int getDeletePropertyPosition() {
		return deletePropertyPosition;
	}
	public void setDeletePropertyPosition(int deletePropertyPosition) {
		this.deletePropertyPosition = deletePropertyPosition;
	}
	public DeleteProperty getDeleteProperty() {
		return deleteProperty;
	}
	public void setDeleteProperty(DeleteProperty deleteProperty) {
		this.deleteProperty = deleteProperty;
	}
	public ArrayList<AddProperty> getAddProperties() {
		return addProperties;
	}
	public void setAddProperties(ArrayList<AddProperty> addProperties) {
		this.addProperties = addProperties;
	}
	
	@Override
	public int startPosition(){
		AddProperty min = this.addProperties.get(0);
		for(AddProperty a : this.addProperties){
			if(Integer.parseInt(a.getId()) < Integer.parseInt(min.getId())){
				min = a;
			}
		}
		
		if (Integer.parseInt(min.getId()) < Integer.parseInt(this.deleteProperty.getId()))
			return Integer.parseInt(min.getId());
		else
			return Integer.parseInt(this.deleteProperty.getId());

	}
	
	@Override
	public boolean doesContainAtomicChange(AtomicChange atomic){
		
		for(AddProperty a : addProperties){
				
			if(a.equals(atomic)){
				return true;
			}  
		}
		
		if(this.deleteProperty.equals(atomic)){
			return true;
		}
		return false;
	}
	
	@Override
	public ArrayList<AtomicChange> getAtomicChanges(){
		ArrayList<AtomicChange> ac = new ArrayList<AtomicChange>();
		
		
		ac.add(this.deleteProperty);
		
		for(AddProperty a : addProperties){
			ac.add(a);
		}
		
		return ac;
	}
}
