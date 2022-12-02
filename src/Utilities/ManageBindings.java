package Utilities;

import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;

public class ManageBindings {

//	IVariableBinding binding;
//	ASTNode node;
	
	private Map<IBinding, ArrayList<ASTNode>> bindingsNodes;
	
	public ManageBindings() {
		super();
		// TODO Auto-generated constructor stub
		
		bindingsNodes = new HashMap<IBinding, ArrayList<ASTNode>>();
	}

	public Map<IBinding, ArrayList<ASTNode>> getBindingsNodes() {
		return bindingsNodes;
	}

	public void setBindingsNodes(Map<IBinding, ArrayList<ASTNode>> bindingsNodes) {
		this.bindingsNodes = bindingsNodes;
	}
	 
	
	
}
