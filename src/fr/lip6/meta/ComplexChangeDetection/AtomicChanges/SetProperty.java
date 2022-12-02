package fr.lip6.meta.ComplexChangeDetection.AtomicChanges;

public class SetProperty extends AtomicChange{

	private String type = "";
	private String newType = "";
	
	private boolean basic = false;
	
	public String getNewType() {
		return newType;
	}
	public void setNewType(String newType) {
		this.newType = newType;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isBasic() {
		return basic;
	}
	public void setBasic(boolean basic) {
		this.basic = basic;
	}
	public SetProperty() {
		super();
		// TODO Auto-generated constructor stub
	}
	public SetProperty(String name, String type,String newType, boolean basic) {
		super(name);
		this.newType = newType;
		this.type = type;
		this.basic = basic;
	}
}
