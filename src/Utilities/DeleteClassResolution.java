package Utilities;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.DeleteClass;


public class DeleteClassResolution {

	/* here we redirect to the approrpiate resolution based on the user choice
	 * choice parameter can be : 
	 * 0 to delete all usage and statements, 
	 * 1 delete the type usages but do not delete statements where the variabes are used, rather replace them with default values 
	 * 2 replace the type with another type
	 * 3 offer option to delete minimu vs delete whole statment
	 *  //TODO special case when the type is used as retrun type of method, if 
	 *  we delete the method, then we propagate this delete and delete its calls,
	 *  alternatively, remove the type but also the return statement too (althought it will be deleted from a used var of the deleted type)
	 * delete the block containing it?
	 * delete the method containing it?
	 * */
	
	
	public static void applyResolution(DeleteClass change, ASTNode node, int choice, String replacement){
		
		if(choice == 0) {
			DeleteClassResolution.applyResolution(change, node);
		} else if(choice == 1) {
			//DeleteResolution.applyResolution(change, node);
		} else if(choice == 2) {
			//DeleteResolution.applyResolution(change, node);
		} 
	}
	
	/* here we delete the used types in imports and in a varable declaration (and statement where the var is used), 
	 * parameters, and where it is used ...
	 * */
	public static void applyResolution(DeleteClass change, ASTNode node){
				
		ASTNode foundImport = DeleteClassResolution.findImportDeclaration(node);
		//here we delete the import 
		if(foundImport != null && foundImport instanceof ImportDeclaration){
			
			//System.out.println("deleting of import "+foundImport.getName().getFullyQualifiedName());			
			foundImport.delete();
			return;
		}
		
		ASTNode foundDeclaration = DeleteClassResolution.findFieldOrVariableDeclarations(node);
		//here we delete variable declaration of the delete type, and then its variable usage so far by deleting the statements where it is used, 
		//later we should replace it with default values, null for objet types and 0 for int, "" for string, false for boolean ect etc
		if(foundDeclaration != null && (foundDeclaration instanceof FieldDeclaration || foundDeclaration instanceof VariableDeclarationStatement)){//TODO VariableDeclarationFragment
			
			//TODO here if you delete a field declaration, delete its variable usages locally
			
			DeleteClassResolution.deleteUsedVariables(foundDeclaration);
			foundDeclaration.delete();
			return;
		}
		
		ASTNode foundInstanceCreation = DeleteClassResolution.findClassInstanceCreations(node);
		//here treat when we have new DeletedType() alone not in a variable declaration
		if(foundInstanceCreation != null && foundInstanceCreation instanceof ClassInstanceCreation){
			
			ASTNode nodeTemp = foundInstanceCreation;
			boolean gotcha = false;
			while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
				//check if it is in a variable declaration
				if(nodeTemp instanceof VariableDeclarationStatement){
					gotcha = true;//it means that this is treated above as part of declaration
				}
				
				nodeTemp = nodeTemp.getParent();
			}
			
			if(!gotcha){
				//here we treat the new DeletedType() that is not part of declaration statement
				ASTNode foundStatement = DeleteClassResolution.findStatement(foundInstanceCreation);
				//here we delete the statements where the variable is used
				if(foundStatement != null && foundStatement instanceof ExpressionStatement){//TODO what about checking var decla ??
					//System.out.println("		*** found statement to delete >>> "+foundStatement);
					foundStatement.delete();
					return;
				}
			}
		}
		
		
		ASTNode foundParameter = DeleteClassResolution.findParameterInMethodDeclaration(node);
		//here we delete the parameters in method declarations
		if(foundParameter != null && foundParameter instanceof SingleVariableDeclaration){
			//((MethodDeclaration)foundParameter.getParent()).parameters().remove(foundParameter);
			//TODO here if you delete a parameter, the call of this method shoud be treated above
			//so far their statement are being deleted rather than just delete the parameter in the call of this method
 			//System.out.println("deleting of parameter in method declaration adn also its used varaible statements");
			
			DeleteClassResolution.deleteUsedVariables(foundParameter);
			int pos = -1;
			int parametersSize = -1;
			if(foundParameter.getParent() != null && foundParameter.getParent() instanceof MethodDeclaration){
				//here we take the position of the deleted parameter, and the size of the parameter list 
				pos = ((MethodDeclaration)foundParameter.getParent()).parameters().indexOf(foundParameter);
				parametersSize = ((MethodDeclaration)foundParameter.getParent()).parameters().size();
				//System.out.println("pos "+pos +" number of params"+ parametersSize);
				
				if(JavaVisitor.getManageBindings().getBindingsNodes().containsKey(((MethodDeclaration) foundParameter.getParent()).resolveBinding())){
					//System.out.println("I found the methods that I need to check");
					//here we check for all its method invocation if it the parameter was removed, 
					//if not (for example using an external method returning the deleted type, but the method still existing with a different returned type), we will remove it
					ArrayList<ASTNode> list_of_usage = JavaVisitor.getManageBindings().getBindingsNodes().get(((MethodDeclaration) foundParameter.getParent()).resolveBinding());
					for(ASTNode astNode : list_of_usage){

						ASTNode invocation = DeleteClassResolution.findMethodInvocation(astNode);
//						if(invocation != null && invocation instanceof MethodInvocation){
//							System.out.println("is it method decla ? "+astNode+" pos "+" number of params "+ ((MethodInvocation)invocation).arguments().size());
//						}
						//here we delete the parameter value if it is not deleted above in the medthod invocation
						if(invocation != null && invocation instanceof MethodInvocation && ((MethodInvocation)invocation).arguments().size() == parametersSize){
							((MethodInvocation)invocation).arguments().remove(pos);
						}
					}
				}
			}
			foundParameter.delete();
//			((SingleVariableDeclaration)foundParameter).getName().get 
			
			
			return;
		}
		
		//here treat the case when the deleted type is used as return type of method
		if(node instanceof SimpleName && node.getParent() instanceof SimpleType && node.getParent().getParent() instanceof MethodDeclaration){//&& node.getParent() != null
			//TODO what abut deleting whole method
			
			//System.out.println("				found deleted returned type ");
			
			PrimitiveType primitiveType = (PrimitiveType) node.getAST().createInstance(PrimitiveType.class);
			primitiveType.setPrimitiveTypeCode(PrimitiveType.toCode("void"));
			
			MethodDeclaration method = (MethodDeclaration)node.getParent().getParent();
			method.setReturnType2(primitiveType);
			
			for(Object st : method.getBody().statements().toArray()){
				
				if(st instanceof ReturnStatement){
					//System.out.println("				found return statement to delete "+st);
					((ReturnStatement) st).delete();
					return;
				}
			}
		}
		//((MethodDeclaration)node).set
		
	}

	/* here we delete the statements where a variable is used of type delected class
	 * */
	private static void deleteUsedVariables(ASTNode foundDeclaration) {
		
		Iterator it = null;
		//now we retrive the declared variables
		if(foundDeclaration instanceof FieldDeclaration){
			it = ((FieldDeclaration) foundDeclaration).fragments().iterator();
		} else if(foundDeclaration instanceof VariableDeclarationStatement){
			it = ((VariableDeclarationStatement) foundDeclaration).fragments().iterator();
		} else if(foundDeclaration instanceof SingleVariableDeclaration){
			ArrayList<SingleVariableDeclaration> list = new ArrayList<SingleVariableDeclaration>();
			list.add((SingleVariableDeclaration) foundDeclaration);
			it = list.iterator();
		} 
		
		while(it != null && it.hasNext()){
			
			Object obj = it.next(); 
			//System.out.println("frgament "+obj);
			//System.out.println("frgament class "+obj.getClass());
			/*here what I should do is 
				 * 1) check the build table of bindings,, 
				 * 2) find used varaiables
				 * 3) delete their usage statements or the element if it is a parameter
				 */
			ArrayList<ASTNode> list_of_usage = new ArrayList<ASTNode>();
			if(obj instanceof VariableDeclarationFragment){
				
				if(JavaVisitor.getManageBindings().getBindingsNodes().containsKey(((VariableDeclarationFragment) obj).resolveBinding())){
					//System.out.println("		found variable >>> "+((VariableDeclarationFragment) obj).resolveBinding().getName());
					
					list_of_usage = JavaVisitor.getManageBindings().getBindingsNodes().get(((VariableDeclarationFragment) obj).resolveBinding());
				}
					
			} else if (obj instanceof SingleVariableDeclaration){
				
				if(JavaVisitor.getManageBindings().getBindingsNodes().containsKey(((SingleVariableDeclaration) obj).resolveBinding())){
					//System.out.println("		found variable >>> "+((SingleVariableDeclaration) obj).resolveBinding().getName());
					
					list_of_usage = JavaVisitor.getManageBindings().getBindingsNodes().get(((SingleVariableDeclaration) obj).resolveBinding());
				}
					
			}
				for(ASTNode astNode : list_of_usage){
						
						ASTNode foundStatement = DeleteClassResolution.findStatement(astNode);
						//here we delete the statements where the variable is used
						if(foundStatement != null && foundStatement instanceof ExpressionStatement || foundStatement instanceof VariableDeclarationStatement){
							//System.out.println("		*** found statement to delete >>> "+foundStatement);
							foundStatement.delete();
						}
						
						//ASTNode foundParameter = DeleteResolution.findParameter(astNode);
						//now we should delete the parameters and then update the method declaration
						//here two method invocation should exist where the used varaible is contained in one
				}				
			
		}
	}

	
	/* find the import to delete
	 * */
	private static ASTNode findImportDeclaration(ASTNode node) {
		
		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			//System.out.println("khelladi import ? "+nodeTemp.getClass());
			
			if(nodeTemp instanceof ImportDeclaration){
				return nodeTemp;
			}
			
			nodeTemp = nodeTemp.getParent();
		}
		
		return null;
	}
	
	/* find the declaration to delete
	 * */
	private static ASTNode findFieldOrVariableDeclarations(ASTNode node) {
		
		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			//System.out.println("khelladi Field/Variable declaration ? "+nodeTemp.getClass());
			
			if(nodeTemp instanceof FieldDeclaration || nodeTemp instanceof VariableDeclarationStatement){
				return nodeTemp;
			}
			
			nodeTemp = nodeTemp.getParent();
		}
		
		return null;
	}
	
	/* find the statement to delete
	 * */
	private static ASTNode findStatement(ASTNode node) {
		
		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			//System.out.println("khelladi Expression/Variable declaration Statement ? "+nodeTemp.getClass());
			
			if(nodeTemp instanceof ExpressionStatement || nodeTemp instanceof VariableDeclarationStatement){
				return nodeTemp;
			}
			
			nodeTemp = nodeTemp.getParent();
		}
		
		return null;
	}
	
	/* find the parameter to delete
	 * */
	private static ASTNode findParameterInMethodDeclaration(ASTNode node) {
		
		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			//System.out.println("khelladi Parameter ? "+nodeTemp.getClass());
			
			if(nodeTemp instanceof SingleVariableDeclaration){//MethodDeclaration){no need to get to method declaration and then delete the parameter, just delete the parameter directly
				return nodeTemp;
			}
			
			nodeTemp = nodeTemp.getParent();
		}
		
		return null;
		
	}
	
	/* find the new creation of class to delete
	 * */
	private static ASTNode findClassInstanceCreations(ASTNode node) {
		
		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			//System.out.println("khelladi new class instance ? "+nodeTemp.getClass());
			
			if(nodeTemp instanceof ClassInstanceCreation){
				return nodeTemp;
			}
			
			nodeTemp = nodeTemp.getParent();
		}
		
		return null;
		
	}
	
	private static ASTNode findMethodInvocation(ASTNode node) {
		
		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			//System.out.println("khelladi method invocation ? "+nodeTemp.getClass());
			
			if(nodeTemp instanceof MethodInvocation){
				return nodeTemp;
			}
			
			nodeTemp = nodeTemp.getParent();
		}
		
		return null;
	}
	
}
