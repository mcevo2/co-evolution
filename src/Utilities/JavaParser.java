package Utilities;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;


public class JavaParser {

	
	
	public JavaParser() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);//JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit); // set source
		parser.setResolveBindings(true); // we need bindings later on
		
			//LocalVariableDetector localVariableDetector = new LocalVariableDetector(); //TODO il faut g�rer les variables binding
			//localVariableDetector.process(unit);
		JavaVisitor jVisitor = new JavaVisitor();
		CompilationUnit newUnit = (CompilationUnit) parser.createAST(null /* IProgressMonitor */); // parse
		jVisitor.process(newUnit);
		
		
//		System.out.println("size of map bindings >> "+jVisitor.manageBindings.getBindingsNodes().size());
//		System.out.println("map bindings >> "+jVisitor.manageBindings.getBindingsNodes().entrySet());
//		System.out.println("key map bindings >> "+jVisitor.manageBindings.getBindingsNodes().keySet());
		
//		JavaParser.printManageBindings();
		
		return newUnit;//TODO maybe store it if needed for future useage, like for the resolutions ;)
	}
	
	 
	/*print the content of the ManageBindings map variable
	 * */
	public static void printManageBindings(){
		
		int i = 0;
		for(IBinding key : JavaVisitor.getManageBindings().getBindingsNodes().keySet()){
			System.out.println("key_"+i+" >> "+key);
//			System.out.println("key_kind "+i+" >> "+key.getKind());
//			System.out.println("key_modifiers "+i+" >> "+key.getModifiers());
//			System.out.println("key_parent name "+i+" >> "+key.getJavaElement().getParent().getElementName());
//			System.out.println("key_parent type "+i+" >> "+key.getJavaElement().getParent().getElementName());
//			System.out.println("key_parent_khelladi "+i+" >> "+key.getJavaElement().getParent().getClass());
			
			
			System.out.println("  key key>> "+key.getKey());
			System.out.println("    key class >> "+key.getClass());
			i++;
			
			int pos = 0;
			for(ASTNode node : JavaVisitor.getManageBindings().getBindingsNodes().get(key)){
				System.out.println("			node_"+pos+" >> "+node + " >> parent >> "+node.getParent());
				pos++;
			}
		}
	}
	
public static boolean isPrimitiveType(IBinding binding){
		
		if(binding == null){ 
			//TODO try to recover the binding, otherwise you may miss some impacts to co-evolve
			return false;
		}
		//System.out.println(">>>>>>>>> binding "+binding);
		//System.out.println(">>>>>>>>> binding key "+binding.getKey());
		
		String split = binding.
				getKey().
				split(";")
				[0];
		if(split.contains("java/lang/") || split.contains("java/io/")){// but you can also have types from other libraries like or org/eclipse/emf/ecore/ !!
			return true;
		}
		return false;
	}
	
}
