package Utilities;

import java.util.Iterator;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.ChildPropertyDescriptor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimplePropertyDescriptor;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.DeleteClass;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.DeleteProperty;

public class DeletePropertyResolution {

	/* here we redirect to the approrpiate resolution based on the user choice
	 * choice parameter can be : 
	 * 0 to delete the instruction where the deleted method is used, 
	 * 1 delete the direct element only 
	 * 2 delete the direct expression (all call path) using this deleted property 
	 * 3 replace the direct expression (all call path) with a default value
	 * 4 replace it with another element (like rename)
	 * 5 think of other case of repalcement, like field access, method call, ect
	 * 
	 * delete the block containing it?
	 * delete the method containing it?
	 * */
	public static void applyResolution(CompilationUnit cu,DeleteProperty change, ASTNode node, int choice, String replacement){

		if(choice == 0) {//TODO here if you delete an var declaration, then delete all its used variable elsewhere as you did with teh delete class
			DeletePropertyResolution.applyResolutionDeleteStatements(node);

		} else if(choice == 1) { 
			DeletePropertyResolution.applyResolutionDeleteMinimum(cu,node);

		} else if(choice == 2) { 
			System.out.println(" CASE OF PROPERTY DeletePropertyResolution");
			DeletePropertyResolution.applyResolutionDeleteAllPathCall( cu,node);

		} else if(choice == 3) {
			DeletePropertyResolution.applyResolutionReplaceWithDefaultAllPathCall(cu,node);

		} else if(choice == 4) {
			//here replace/rename it 
			DeletePropertyResolution.applyResolutionReplaceWithAnotherElement(node, replacement);
		}
	}

	private static void applyResolutionReplaceWithAnotherElement(ASTNode node, String replacement) {
		//replacement has to be of the same type node, if method is method, field is field

		if(node instanceof SimpleName){
			//System.out.println("			renaming "+((SimpleName) node).getIdentifier()+ " to "+ newName);
			SimpleName sName = (SimpleName)node;
			sName.setIdentifier(replacement);
		}

	}

	/* Here we repalce the whole path call with a default value with a type from the last called element
	 * */
	private static void applyResolutionReplaceWithDefaultAllPathCall(CompilationUnit cu,ASTNode node) {

		ASTNode nodeTemp = node;
		//boolean gotcha = false;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			//check if it is in a variable declaration

			if(nodeTemp instanceof MethodInvocation || nodeTemp instanceof FieldAccess){//Here we treta both the case where a method call or field access to get the whole path

				//				EmptyStatement emptyStatement = (EmptyStatement) node.getAST().createInstance(EmptyStatement.class);

				//here the method call is in the middle or as argument of another method call
				ASTNode methodOrFieldCall = nodeTemp;
				while (methodOrFieldCall != null && 
						methodOrFieldCall.getParent() != null && 
						(methodOrFieldCall.getParent() instanceof MethodInvocation ||
								methodOrFieldCall.getParent() instanceof FieldAccess)){

					if(methodOrFieldCall.getParent() instanceof MethodInvocation && ((MethodInvocation)methodOrFieldCall.getParent()).arguments().contains(methodOrFieldCall)){

						ASTNode defaultValue = ResolutionsUtils.getDefaultValue4TypeOfExpression(cu,methodOrFieldCall);
						if(defaultValue != null){
							ResolutionsUtils.replaceDirectExpressionInParentNode(cu,methodOrFieldCall, defaultValue);
						} else {
							ResolutionsUtils.replaceDeleteAllCallPathExpressionInParentNode( cu,methodOrFieldCall);
						}

						//methodOrFieldCall.delete();
						return;
					}
					methodOrFieldCall = methodOrFieldCall.getParent();
				}

				if(methodOrFieldCall instanceof MethodInvocation || methodOrFieldCall instanceof FieldAccess){//here we reached the end of the method calls, it parent should be not method call or field access 
					System.out.println(" Defaut value is "+methodOrFieldCall);
					ASTNode defaultValue = ResolutionsUtils.getDefaultValue4TypeOfExpression(cu,methodOrFieldCall);
					System.out.println(" Defaut value is "+defaultValue);
					if(defaultValue == null || methodOrFieldCall.getParent() instanceof ExpressionStatement){
						ResolutionsUtils.replaceDeleteAllCallPathExpressionInParentNode(cu,methodOrFieldCall);
					} else {
						ResolutionsUtils.replaceDirectExpressionInParentNode(cu,methodOrFieldCall, defaultValue);
					}

					//methodOrFieldCall.delete();
					return;
				}

				//			} else if(nodeTemp instanceof FieldAccess){//here treat the case the prop is not method call but the path is with method call and is x.y().z.w().v.prop. ...

				//				EmptyStatement emptyStatement = (EmptyStatement) node.getAST().createInstance(EmptyStatement.class);
				//				ResolutionsUtils.replaceExpressionInParentNode(nodeTemp, emptyStatement);
				//				nodeTemp.delete();
				//				return;

			} else if(nodeTemp instanceof QualifiedName){//here treat the case the prop is not method call and not field access

				ASTNode methodOrFieldOrQualifiedcall = nodeTemp;
				while (methodOrFieldOrQualifiedcall != null && 
						methodOrFieldOrQualifiedcall.getParent() != null && 
						(methodOrFieldOrQualifiedcall.getParent() instanceof MethodInvocation ||
								methodOrFieldOrQualifiedcall.getParent() instanceof FieldAccess ||
								methodOrFieldOrQualifiedcall.getParent() instanceof QualifiedName)){

					if(methodOrFieldOrQualifiedcall.getParent() instanceof MethodInvocation && ((MethodInvocation)methodOrFieldOrQualifiedcall.getParent()).arguments().contains(methodOrFieldOrQualifiedcall)){

						ASTNode defaultValue = ResolutionsUtils.getDefaultValue4TypeOfExpression(cu,methodOrFieldOrQualifiedcall);
						if(defaultValue != null){
							ResolutionsUtils.replaceDirectExpressionInParentNode(cu,methodOrFieldOrQualifiedcall, defaultValue);
						} else {
							ResolutionsUtils.replaceDeleteAllCallPathExpressionInParentNode(cu,methodOrFieldOrQualifiedcall);
						}

						//methodOrFieldOrQualifiedcall.delete();
						return;
					}
					methodOrFieldOrQualifiedcall = methodOrFieldOrQualifiedcall.getParent();
				}

				if(methodOrFieldOrQualifiedcall instanceof MethodInvocation || methodOrFieldOrQualifiedcall instanceof FieldAccess || methodOrFieldOrQualifiedcall instanceof QualifiedName){//here we reached the end of the method calls, it parent should be not method call or field access 
					//					System.out.println("			yay khelladi method or field "+methodcall + " >> class "+methodcall.getClass());
					//					System.out.println("			yay khelladi parent method or field "+methodcall.getParent() + " >> class "+methodcall.getParent().getClass());

					ASTNode defaultValue = ResolutionsUtils.getDefaultValue4TypeOfExpression(cu,methodOrFieldOrQualifiedcall);
					if(defaultValue == null || methodOrFieldOrQualifiedcall.getParent() instanceof ExpressionStatement){
						ResolutionsUtils.replaceDeleteAllCallPathExpressionInParentNode(cu,methodOrFieldOrQualifiedcall);
					} else {
						ResolutionsUtils.replaceDirectExpressionInParentNode(cu,methodOrFieldOrQualifiedcall, defaultValue);
					}

					//methodOrFieldOrQualifiedcall.delete();
					return;
				}
				//				nodeTemp.delete();

			}
			else  if(nodeTemp instanceof MethodDeclaration)
			{
				ResolutionsUtils.replaceDeleteAllCallPathExpressionInParentNode(cu,nodeTemp);

			}

			nodeTemp = nodeTemp.getParent();
		}

	}

	/* there we delete the element and its call path, from x.y.z.deletedProp.w to nothing
	 * cases where it makes sense
	 * method arguments, 
	 * find cases where it does not male sense,  
	 * */
	private static void applyResolutionDeleteAllPathCall(CompilationUnit cu,ASTNode node) {
		System.out.println(" CASE OF PROPERTY applyResolutionDeleteAllPathCall");
		ASTNode nodeTemp = node;
		//boolean gotcha = false;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			//check if it is in a variable declaration

			if(nodeTemp instanceof MethodInvocation || nodeTemp instanceof FieldAccess){//Here we treat both the case where a method call or field access to get the whole path

				//				EmptyStatement emptyStatement = (EmptyStatement) node.getAST().createInstance(EmptyStatement.class);
				System.out.println(" CASE OF PROPERTY applyResolutionDeleteAllPathCall IF1");
				//here the method call is in the middle or as argument of another method call
				ASTNode methodOrFieldCall = nodeTemp;
				while (methodOrFieldCall != null && 
						methodOrFieldCall.getParent() != null && 
						(methodOrFieldCall.getParent() instanceof MethodInvocation ||
								methodOrFieldCall.getParent() instanceof FieldAccess)){

					if(methodOrFieldCall.getParent() instanceof MethodInvocation && ((MethodInvocation)methodOrFieldCall.getParent()).arguments().contains(methodOrFieldCall)){
						System.out.println(" CASE OF PROPERTY applyResolutionDeleteAllPathCall if2");
						ResolutionsUtils.replaceDeleteAllCallPathExpressionInParentNode( cu,methodOrFieldCall);
						//methodOrFieldCall.delete();
						return;
					}
					methodOrFieldCall = methodOrFieldCall.getParent();
				}

				if(methodOrFieldCall instanceof MethodInvocation || methodOrFieldCall instanceof FieldAccess){//here we reached the end of the method calls, it parent should be not method call or field access 
					System.out.println(" CASE OF PROPERTY applyResolutionDeleteAllPathCall if3");
					if( ASTManager.findReturnStatment(methodOrFieldCall)!=null && ASTManager.findReturnStatment(methodOrFieldCall).getParent() instanceof SwitchStatement)
					{
						AST ast2 = cu.getAST();
						ASTRewrite rewriter1 = ASTRewrite.create(ast2);

						//  IPath pathcu = cu.getJavaElement().getPath();

						Document document=null;
						ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
						try {
							document = new Document(iCompilUnit.getSource());
							rewriter1.remove(ASTManager.findReturnStatment(methodOrFieldCall), null);  

							TextEdit edits = rewriter1.rewriteAST(document, null);

							SaveModification.SaveModif(cu, edits);
						}
						catch (JavaModelException e) {
							// TODO Auto-generated catch block
							
							e.printStackTrace();
						}

					}
					else
						if(ASTManager.findReturnStatment(methodOrFieldCall)!=null && ASTManager.findReturnStatment(methodOrFieldCall).getParent().getParent() instanceof MethodDeclaration )
						{
							//((MethodDeclaration)(ASTManager.findReturnStatment(methodOrFieldCall).getParent())).resolveBinding().getReturnType().isPrimitive())
							
							//applyResolutionReplaceWithDefaultAllPathCall( cu,methodOrFieldCall);
							AST ast2 = cu.getAST();
							ASTRewrite rewriter1 = ASTRewrite.create(ast2);

							//  IPath pathcu = cu.getJavaElement().getPath();

							Document document=null;
							ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
							try {
								document = new Document(iCompilUnit.getSource()); 
								BooleanLiteral bool = (BooleanLiteral) ast2.createInstance(BooleanLiteral.class);
								bool.setBooleanValue(false);

								rewriter1.replace(methodOrFieldCall,bool, null);  


								TextEdit edits = rewriter1.rewriteAST(document, null);

								SaveModification.SaveModif(cu, edits);
							}
							catch (JavaModelException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}


						}
						else 
							{
							System.out.println(" get set as init");
								ResolutionsUtils.replaceDeleteAllCallPathExpressionInParentNode( cu,methodOrFieldCall);
								//methodOrFieldCall.delete();
							}
					return;
				}


				//			} else if(nodeTemp instanceof FieldAccess){//here treat the case the prop is not method call but the path is with method call and is x.y().z.w().v.prop. ...

				//				EmptyStatement emptyStatement = (EmptyStatement) node.getAST().createInstance(EmptyStatement.class);
				//				ResolutionsUtils.replaceExpressionInParentNode(nodeTemp, emptyStatement);
				//				nodeTemp.delete();
				//				return;

			} else if(nodeTemp instanceof QualifiedName){//here treat the case the prop is not method call and not field access
				System.out.println(" CASE OF PROPERTY applyResolutionDeleteAllPathCall if4");
				ASTNode methodOrFieldOrQualifiedcall = nodeTemp;
				while (methodOrFieldOrQualifiedcall != null && 
						methodOrFieldOrQualifiedcall.getParent() != null && 
						(methodOrFieldOrQualifiedcall.getParent() instanceof MethodInvocation ||
								methodOrFieldOrQualifiedcall.getParent() instanceof FieldAccess ||
								methodOrFieldOrQualifiedcall.getParent() instanceof QualifiedName)){

					if(methodOrFieldOrQualifiedcall.getParent() instanceof MethodInvocation && ((MethodInvocation)methodOrFieldOrQualifiedcall.getParent()).arguments().contains(methodOrFieldOrQualifiedcall)){
						System.out.println(" CASE OF PROPERTY applyResolutionDeleteAllPathCall if5"+ methodOrFieldOrQualifiedcall.getNodeType());
						ResolutionsUtils.replaceDeleteAllCallPathExpressionInParentNode(cu,methodOrFieldOrQualifiedcall);
						//methodOrFieldOrQualifiedcall.delete();
						return;
					}
					methodOrFieldOrQualifiedcall = methodOrFieldOrQualifiedcall.getParent();
				}

				if(methodOrFieldOrQualifiedcall instanceof MethodInvocation || methodOrFieldOrQualifiedcall instanceof FieldAccess || methodOrFieldOrQualifiedcall instanceof QualifiedName){//here we reached the end of the method calls, it parent should be not method call or field access 
					//					System.out.println("			yay khelladi method or field "+methodcall + " >> class "+methodcall.getClass());
					//					System.out.println("			yay khelladi parent method or field "+methodcall.getParent() + " >> class "+methodcall.getParent().getClass());
					System.out.println(" CASE OF PROPERTY applyResolutionDeleteAllPathCall if 6");
					ResolutionsUtils.replaceDeleteAllCallPathExpressionInParentNode(cu,methodOrFieldOrQualifiedcall);
					//methodOrFieldOrQualifiedcall.delete();
					return;
				}

			}else  if(nodeTemp instanceof MethodDeclaration)
			{
				ResolutionsUtils.replaceDeleteAllCallPathExpressionInParentNode(cu,nodeTemp);

			}

			nodeTemp = nodeTemp.getParent();
		}

	}

	/* there we delete the element only, from x.y.z.deletedProp.w to x.y.z.w
	 * */
	private static void applyResolutionDeleteMinimum(CompilationUnit cu,ASTNode node){

		System.out.println("			yay khelladi element to delete locally "+node + " >> class "+node.getClass());

		ASTNode nodeTemp = node;
		//boolean gotcha = false;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			//check if it is in a variable declaration

			if(nodeTemp instanceof MethodInvocation){
				//				System.out.println("			yay khelladi method invocation "+nodeTemp + " >> class "+nodeTemp.getClass());
				//				System.out.println("			yay khelladi parent method invocation "+nodeTemp.getParent() + " >> class "+nodeTemp.getParent().getClass());

				ResolutionsUtils.replaceDirectExpressionInParentNode(cu,nodeTemp, ((MethodInvocation)nodeTemp).getExpression());
				//nodeTemp.delete();
				return;

			} else if(nodeTemp instanceof FieldAccess){//here treat the case the prop is not method call but the path is with method call and is x.y().z.w().v.prop. ...

				ResolutionsUtils.replaceDirectExpressionInParentNode(cu,nodeTemp, ((FieldAccess)nodeTemp).getExpression());
				//nodeTemp.delete();
				return;

			} else if(nodeTemp instanceof QualifiedName){//here treat the case the prop is not method call and the path is not method calls like x.y.z.prop.

				//				if(((QualifiedName)nodeTemp).getQualifier().equals(oldExp)){
				//					 
				//					((QualifiedName)oldExp.getParent()).setQualifier((Name) ast);	
				//				} else if(((QualifiedName)oldExp.getParent()).getName().equals(oldExp)){
				//					 
				//					((QualifiedName)oldExp.getParent()).setName((SimpleName) ast);	
				//				}
				//above it was to treat the case where the expression begins with the QN, but this should never happen;
				ResolutionsUtils.replaceDirectExpressionInParentNode(cu,nodeTemp, ((QualifiedName)nodeTemp).getQualifier());
				//nodeTemp.delete();
				return;
			}
			else  if(nodeTemp instanceof MethodDeclaration)
			{
				ResolutionsUtils.replaceDeleteAllCallPathExpressionInParentNode(cu,nodeTemp);

			}
			nodeTemp = nodeTemp.getParent();
		}
	}


	/* here we treat the case of removing instruction, or remove the element in 
	 * a successive call path
	 * */
	private static void applyResolutionDeleteStatements(ASTNode node){

		ASTNode nodeTemp = node;
		//boolean gotcha = false;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			//check if it is in a variable declaration
			if(nodeTemp instanceof Statement){ //ExpressionStatement){
				//gotcha = true;
				//if(nodeTemp instanceof Variable declaration or method declaration){//we delete all its variable usages
				//TODO deal with this case
				//}
				nodeTemp.delete();
				return;
			}

			nodeTemp = nodeTemp.getParent();
		}
	}



}
