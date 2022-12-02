package Utilities;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.dom.ASTNode;

import fr.lip6.meta.ComplexChangeDetection.Change;

public class Usage {
	ArrayList<UsagePattern>  patterns=new ArrayList<UsagePattern>() ;
	ASTNode node;
	IMarker error;
	Change change;
	
	public Change getChange() {
		return change;
	}
	public void setChange(Change change) {
		this.change = change;
	}
	int priority;
	boolean treated;
	public boolean isTreated() {
		return treated;
	}
	public void setTreated(boolean treated) {
		this.treated = treated;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public IMarker getError() {
		return error;
	}
	public void setError(IMarker error) {
		this.error = error;
	}
	
	public ASTNode getNode() {
		return node;
	}
	public ArrayList<UsagePattern> getPatterns() {
		return patterns;
	}
	public void setPatterns(ArrayList<UsagePattern> pattern) {
		this.patterns = pattern;
	}
	public void setNode(ASTNode node) {
		this.node = node;
	} 
	
	

}
