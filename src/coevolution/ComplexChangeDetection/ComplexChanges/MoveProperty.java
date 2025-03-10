package coevolution.ComplexChangeDetection.ComplexChanges;

import java.util.ArrayList;

import coevolution.ComplexChangeDetection.AtomicChanges.AddProperty;
import coevolution.ComplexChangeDetection.AtomicChanges.AtomicChange;
import coevolution.ComplexChangeDetection.AtomicChanges.DeleteProperty;

public class MoveProperty extends ComplexChange{
	
	private String sourceClassName = "";
	private String targetClassName = "";
	private String importPath="";
	public String getImportPath() {
		return importPath;
	}

	public void setImportPath(String importPath) {
		this.importPath = importPath;
	}

	private int deletePropertyPosition = -1;
	private int addPropertyPosition = -1;
	
	private DeleteProperty deleteProperty = null;
	private AddProperty addProperty = null;
	
	private String throughReference = "";
	private int upperBound=0;
	private int lowerBound=1;
	

	public int getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(int upperBound) {
		this.upperBound = upperBound;
	}

	public int getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(int lowerBound) {
		this.lowerBound = lowerBound;
	}

	public MoveProperty(String name, String sourceClassName, String targetClassName) {
		super(name);
		// TODO Auto-generated constructor stub
		this.sourceClassName = sourceClassName;
		this.targetClassName = targetClassName;
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

	public int getDeletePropertyPosition() {
		return deletePropertyPosition;
	}

	public void setDeletePropertyPosition(int deletePropertyPosition) {
		this.deletePropertyPosition = deletePropertyPosition;
	}

	public int getAddPropertyPosition() {
		return addPropertyPosition;
	}

	public void setAddPropertyPosition(int addPropertyPosition) {
		this.addPropertyPosition = addPropertyPosition;
	}

	public int getFirstPosition(){
		if(this.addPropertyPosition > this.deletePropertyPosition) return this.deletePropertyPosition;
		else return this.addPropertyPosition;
	}
	
	public int getLastPosition(){
		if(this.addPropertyPosition > this.deletePropertyPosition) return this.addPropertyPosition;
		else return this.deletePropertyPosition;
	}

	public String getThroughReference() {
		return throughReference;
	}

	public void setThroughReference(String throughReference) {
		this.throughReference = throughReference;
	}

	public DeleteProperty getDeleteProperty() {
		return deleteProperty;
	}

	public void setDeleteProperty(DeleteProperty deleteProperty) {
		this.deleteProperty = deleteProperty;
	}

	public AddProperty getAddProperty() {
		return addProperty;
	}

	public void setAddProperty(AddProperty addProperty) {
		this.addProperty = addProperty;
	}
	
	@Override
	public int startPosition(){
		
		if (Integer.parseInt(this.deleteProperty.getId()) < Integer.parseInt(this.addProperty.getId()))
			return Integer.parseInt(this.deleteProperty.getId());
		else
			return Integer.parseInt(this.addProperty.getId());
	}
	
	@Override
	public boolean doesContainAtomicChange(AtomicChange atomic){
		if(this.deleteProperty.equals(atomic)){
			return true;
		} else if(this.addProperty.equals(atomic)){
			return true;
		}
		return false;
	}
	
	@Override
	public ArrayList<AtomicChange> getAtomicChanges(){
		ArrayList<AtomicChange> ac = new ArrayList<AtomicChange>();
		
		ac.add(this.deleteProperty);
		ac.add(this.addProperty);
		
		return ac;
	}
	
}
