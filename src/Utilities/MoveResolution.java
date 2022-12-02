package Utilities;



import java.util.Iterator;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.DeleteProperty;
import fr.lip6.meta.ComplexChangeDetection.ComplexChanges.MoveProperty;

public class MoveResolution {//can treat extract class too

	/* here we treat the three cases of moves
	 * 0 extend navigation path
	 * 1 reduce navigation path
	 * 2 extend a navigation path and add a loop
	 * 3 extend a navigation path and get the first/last/i^th element
	 * 4 replace reference in path call x.y.z.prop to x.y.w.prop
	 * 5 think of other cases
	 * */
	public static void applyResolution(CompilationUnit cu,MoveProperty move, ASTNode node, int choice){
		
		if(choice == 0) {
			System.out.println(" in  0");
			MoveResolution.applyResolutionExtendNavigationPath(cu,move.getThroughReference(), node);
			
		} else if(choice == 1) {

			MoveResolution.applyResolutionReduceNavigationPath(cu,move.getThroughReference(), node);
			
		} else if(choice == 2) {
			
			//ResolutionsUtils.addMissingImports(cu,move.getTargetClassName(), node);
		
			MoveResolution.applyResolutionExtendNavigationPathWIthLoop(cu,move.getTargetClassName(), move.getThroughReference(), node);
			
			
			
		} else if(choice == 3) {
			
		} else if(choice == 4) {
			//TODO consider the case of access from the package factory, 
			//like PackageCLass.CLassName_Propety, then change only to PackageCLass.TargetCLassName_Propety
		}
	}

	
	
	/* Here we reduce the navigation path, 
	 * if it is x.y.prop, we reduce it to x.prop
	 * if it is x.prop, we remove the call path
	 * */
	private static void applyResolutionReduceNavigationPath(CompilationUnit cu,String throughReference, ASTNode node) {
		
		//System.out.println("			yay khelladi element to reduce path call "+node + " >> class "+node.getClass());
		
		ASTNode nodeTemp = node;
		//boolean gotcha = false;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			//check if it is in a variable declaration
			
			if(nodeTemp instanceof MethodInvocation){
				
				if(((MethodInvocation) nodeTemp).getExpression() instanceof SimpleName){//it means that it is x.prop()
					//TODO do something? delete call path?
					DeletePropertyResolution.applyResolution(cu,null, node, 2, "");
					
				} else if(((MethodInvocation) nodeTemp).getExpression() instanceof MethodInvocation){
					
					ASTNode ast = ASTNode.copySubtree(nodeTemp.getAST(), ((MethodInvocation)((MethodInvocation) nodeTemp).getExpression()).getExpression());
					if(ast != null) ((MethodInvocation) nodeTemp).setExpression((Expression) ast);
					else DeletePropertyResolution.applyResolution(cu,null, node, 2, "");
					
				} else if(((MethodInvocation) nodeTemp).getExpression() instanceof FieldAccess){
					
					ASTNode ast = ASTNode.copySubtree(nodeTemp.getAST(), ((FieldAccess)((MethodInvocation) nodeTemp).getExpression()).getExpression());
					if(ast != null) ((MethodInvocation) nodeTemp).setExpression((Expression) ast);
					else DeletePropertyResolution.applyResolution(cu,null, node, 2, "");
					
				} else if(((MethodInvocation) nodeTemp).getExpression() instanceof QualifiedName){
					
					ASTNode ast = ASTNode.copySubtree(nodeTemp.getAST(), ((QualifiedName)((MethodInvocation) nodeTemp).getExpression()).getQualifier());
					if(ast != null) ((MethodInvocation) nodeTemp).setExpression((Expression) ast);
					else DeletePropertyResolution.applyResolution(cu,null, node, 2, "");
					
				}
				
				//ResolutionsUtils.replaceDirectExpressionInParentNode(nodeTemp, method);
				//nodeTemp.delete();
				return;
				
			} else if(nodeTemp instanceof FieldAccess){//here treat the case the prop is not method call but the path is with method call and is x.y().z.w().v.prop. ...
				
				if(((FieldAccess) nodeTemp).getExpression() instanceof SimpleName){//it means that it is x.prop()
					//TODO do something? delete call path?
					DeletePropertyResolution.applyResolution(cu,null, node, 2, "");
					
				} else if(((FieldAccess) nodeTemp).getExpression() instanceof MethodInvocation){
					
					ASTNode ast = ASTNode.copySubtree(nodeTemp.getAST(), ((MethodInvocation)((FieldAccess) nodeTemp).getExpression()).getExpression());
					if(ast != null) ((FieldAccess) nodeTemp).setExpression((Expression) ast);
					else DeletePropertyResolution.applyResolution(cu,null, node, 2, "");
					
				} else if(((FieldAccess) nodeTemp).getExpression() instanceof FieldAccess){
					
					ASTNode ast = ASTNode.copySubtree(nodeTemp.getAST(), ((FieldAccess)((FieldAccess) nodeTemp).getExpression()).getExpression());
					if(ast != null) ((FieldAccess) nodeTemp).setExpression((Expression) ast);
					else DeletePropertyResolution.applyResolution(cu,null, node, 2, "");
					
				} else if(((FieldAccess) nodeTemp).getExpression() instanceof QualifiedName){
					
					ASTNode ast = ASTNode.copySubtree(nodeTemp.getAST(), ((QualifiedName)((FieldAccess) nodeTemp).getExpression()).getQualifier());
					if(ast != null) ((FieldAccess) nodeTemp).setExpression((Expression) ast);
					else DeletePropertyResolution.applyResolution(cu,null, node, 2, "");
					
				}
				
				//ResolutionsUtils.replaceDirectExpressionInParentNode(nodeTemp, method);
				//nodeTemp.delete();
				return;
				
			} else if(nodeTemp instanceof QualifiedName){//here treat the case the prop is not method call and the path is not method calls like x.y.z.prop.
				
				if(((QualifiedName) nodeTemp).getQualifier() instanceof SimpleName){//it means that it is x.prop()
					//TODO do something? delete call path?
					DeletePropertyResolution.applyResolution(cu,null, node, 2, "");
					
//				} else if(((QualifiedName) nodeTemp).getQualifier() instanceof MethodInvocation){
//					
//					ASTNode ast = ASTNode.copySubtree(nodeTemp.getAST(), ((MethodInvocation)((FieldAccess) nodeTemp).getExpression()).getExpression());
//					if(ast != null) ((FieldAccess) nodeTemp).setExpression((Expression) ast);
//					
//				} else if(((QualifiedName) nodeTemp).getQualifier() instanceof FieldAccess){
//					
//					ASTNode ast = ASTNode.copySubtree(nodeTemp.getAST(), ((FieldAccess)((FieldAccess) nodeTemp).getExpression()).getExpression());
//					if(ast != null) ((FieldAccess) nodeTemp).setExpression((Expression) ast);
//					
				} else if(((QualifiedName) nodeTemp).getQualifier() instanceof QualifiedName){
					
					ASTNode ast = ASTNode.copySubtree(nodeTemp.getAST(), ((QualifiedName)((QualifiedName) nodeTemp).getQualifier()).getQualifier());
					if(ast != null) ((QualifiedName) nodeTemp).setQualifier((Name) ast);
					
				}
				
//				ResolutionsUtils.replaceDirectExpressionInParentNode(nodeTemp, convertedQN);
				//nodeTemp.delete();
				return;
			}
			
			nodeTemp = nodeTemp.getParent();
		}
		
	}

	/* Here we extend path call with a loop addition
	 * from x.y.prop to
	 * for(type var: x.y.z){
	 * 		var.prop
	 * }
	 * */
	private static void applyResolutionExtendNavigationPathWIthLoop(CompilationUnit cu ,String targetClassName, String throughReference, ASTNode node) {
		
		/* Steps to do 
		 * copy call path untill p, and put it under a method call
		 * create a for loop, with the type of the method call, which happen to be the container of the prop, or also the type is in the move change target class ;)
		 * then put the var under the prop expression and thats it
		 * */
		
		//here we create the structure of the loop and all most of its needed elements
		EnhancedForStatement efor = (EnhancedForStatement) node.getRoot().getAST().createInstance(EnhancedForStatement.class);
		SingleVariableDeclaration param = (SingleVariableDeclaration) node.getAST().createInstance(SingleVariableDeclaration.class);
		SimpleType stype = (SimpleType) efor.getAST().createInstance(SimpleType.class);
		SimpleName sname = (SimpleName) efor.getAST().createInstance(SimpleName.class);
		SimpleName varnameParam = (SimpleName) efor.getAST().createInstance(SimpleName.class);
		
		MethodInvocation method = (MethodInvocation) efor.getAST().createInstance(MethodInvocation.class);
		SimpleName name = (SimpleName) efor.getAST().createInstance(SimpleName.class);
	   name.setIdentifier("get"+UsesManager.capitalizeFirstLetter(throughReference));
	//	name.setIdentifier(throughReference);
		method.setName(name);
		
		sname.setIdentifier(targetClassName);
		stype.setName(sname);
		param.setType(stype);
		
		String generatedName = targetClassName.toLowerCase() + "_" + ((int)(Math.random()*10000));
		varnameParam.setIdentifier(generatedName);
		param.setName(varnameParam);
		efor.setParameter(param);
		
		ASTNode nodeTemp = node;
		//boolean gotcha = false;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			//check if it is in a variable declaration
			
			if(nodeTemp instanceof MethodInvocation){
			System.out.println("			yay khelladi method invocation "+nodeTemp + " >> class "+nodeTemp.getClass());
//				System.out.println("			yay khelladi parent method invocation "+nodeTemp.getParent() + " >> class "+nodeTemp.getParent().getClass());
				
//				//here instead of exchanging
//				method.setName(((MethodInvocation) nodeTemp).getName());
//				name.setIdentifier(throughReference);
//				((MethodInvocation) nodeTemp).setName(name);
				
				//here we put the expression collection with the new call in the loop
				ASTNode astexp = ASTNode.copySubtree(method.getAST(), ((MethodInvocation) nodeTemp).getExpression());
				method.setExpression((Expression) astexp);
				efor.setExpression(method);
				
				//here we replace the path to prop by the new created var in the loop
				SimpleName varnameExp = (SimpleName) efor.getAST().createInstance(SimpleName.class);
				varnameExp.setIdentifier(generatedName);
				((MethodInvocation) nodeTemp).setExpression(varnameExp);
				
				ASTNode findStat = nodeTemp;
				//boolean gotcha = false;
				System.out.println(" findStat  "+findStat+ "NODE TEMP  "+nodeTemp);
				
				//now we treat the body of the loop
				while (findStat != null && !(findStat instanceof CompilationUnit)) {
					//check if it is in a variable declaration
					
					if(findStat instanceof VariableDeclarationFragment){//treat the case of var delcaration, we keep it declaration out of the loop, and we creat an assiment for it
						
						ASTNode aststat = ASTNode.copySubtree(method.getAST(), ((VariableDeclarationFragment) findStat).getInitializer());
						Assignment assign = (Assignment) efor.getAST().createInstance(Assignment.class);
						SimpleName aname = (SimpleName) efor.getAST().createInstance(SimpleName.class);
						aname.setIdentifier(((VariableDeclarationFragment) findStat).getName().getIdentifier());
						assign.setLeftHandSide(aname);
						ASTManager.findMyVarDeclarationStat(cu, aname);
						assign.setRightHandSide((Expression) aststat);
						ExpressionStatement expstat = (ExpressionStatement) efor.getAST().createInstance(ExpressionStatement.class);
						expstat.setExpression(assign);
						
						Block block = (Block) efor.getAST().createInstance(Block.class);
						block.statements().add(expstat);
						efor.setBody(block);
						System.out.println(" INDEXXXX "+((Block)((VariableDeclarationStatement)findStat.getParent()).getParent()).statements().indexOf((VariableDeclarationStatement)(findStat.getParent())) + 1);
						
						///((VariableDeclarationFragment) findStat).setInitializer(null);//or delete it ??
						
						//findStat.getParent()
						
					
						//((Block)findStat.getParent()).statements().remove(findStat);
						
						System.out.println(" 11111111  "+efor);
						//ResolutionsUtils.replaceDirectExpressionInParentNode(cu,nodeTemp, method);
						System.out.println(" Here it is");
						
						AST ast2 = cu.getAST();
						ASTRewrite rewriter1 = ASTRewrite.create(ast2);

						NullLiteral nullLiteral = (NullLiteral) ast2.createInstance(NullLiteral.class);
						

						Document document=null;
						ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
						try {
							document = new Document(iCompilUnit.getSource());
							rewriter1.replace(((VariableDeclarationFragment) findStat).getInitializer(),nullLiteral, null);  
							ListRewrite lrw2 = rewriter1.getListRewrite(((Block)((VariableDeclarationStatement)findStat.getParent()).getParent()),Block.STATEMENTS_PROPERTY);
							
							lrw2.insertAt(efor,((Block)((VariableDeclarationStatement)findStat.getParent()).getParent()).statements().indexOf((VariableDeclarationStatement)(findStat.getParent())) + 1, null);
							
						

						  //  lrw2.insertLast(methodInvocation, null);
							//rewriter1.
							

							TextEdit edits = rewriter1.rewriteAST();
						
							SaveModification.SaveModif(cu, edits);
						}
						catch (JavaModelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
												
						return;
						
					} else if(findStat instanceof Statement && findStat.getParent() instanceof Block){//treat the rest of cases 
						//gotcha = true;
						//nodeTemp.delete();
						System.out.println(" before if  "+node.getClass()+ "his parent  "+findStat.getParent().getParent().getClass() );
						if((findStat.getParent().getParent() instanceof IfStatement)) {
							ASTNode newStat = ASTNode.copySubtree(findStat.getAST(),findStat);
							 if(newStat instanceof ExpressionStatement)
									 {
								 System.out.println(" in expressionStatement");
							Iterator it=null;
						
								 
									 }
							Block block = (Block) efor.getAST().createInstance(Block.class);
							
							block.statements().add(newStat);
							efor.setBody(block);

							//MoveResolution.e(cu,throughReference, node);
							AST ast2 = cu.getAST();
							
							ASTRewrite rewriter1 = ASTRewrite.create(ast2);
							rewriter1.remove(findStat, null);
							//NullLiteral nullLiteral = (NullLiteral) ast2.createInstance(NullLiteral.class);
							

							Document document=null;
							ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
							try {
								document = new Document(iCompilUnit.getSource());
							//	rewriter1.replace(node,nullLiteral, null);  
								ListRewrite lrw2 = rewriter1.getListRewrite(((Block)(findStat.getParent())),Block.STATEMENTS_PROPERTY);
								
								System.out.println(" before addig rexrie  "+ efor);
								lrw2.insertAt(efor,((Block)(findStat.getParent())).statements().indexOf(node) + 1, null);
								
							
								Iterator it=null;
								 
								 if (	 ((ExpressionStatement)newStat).getExpression() instanceof Assignment) {
								      Assignment assignment= (Assignment) ((ExpressionStatement)newStat).getExpression();
								      SimpleName simpleName = (SimpleName)(assignment.getLeftHandSide());
								 	 
								    VariableDeclarationStatement vardecl=  ASTManager.findMyVarDeclarationStat(cu, simpleName);
								    if(vardecl !=null)
								    {
								    	System.out.println(" found declar is not nul ");
								    	it = ((VariableDeclarationStatement) vardecl).fragments().iterator();
								    	
								    	while(it != null && it.hasNext()){
								    		
								    		Object obj = it.next(); 
								    		if(obj instanceof VariableDeclarationFragment){
								    			System.out.println(" Variable is 2  :    "+((VariableDeclarationFragment)obj).getName().getIdentifier());
								    			if(((VariableDeclarationFragment)obj).getInitializer()==null) {
								    				System.out.println(" in itnit null 2 ");
								    				NullLiteral nullLiteral = (NullLiteral) vardecl.getAST().createInstance(NullLiteral.class);
													
								    			Expression expression= ((VariableDeclarationFragment)obj).getAST().newNullLiteral();
								    			//	Expression expression= vardecl.getAST().newNumberLiteral("hello");
								    				//NumberLiteral initializer = vardecl.getAST().newNumberLiteral();
								    				//  initializer.setToken("1L");
								    				//System.out.println("  initi null "+expression);
								    			 
								    				((VariableDeclarationFragment)obj).setInitializer(expression);
								    				ASTNode newDecl= ASTNode.copySubtree(cu.getAST(), vardecl);
								    				System.out.println(" replacing 2  "+vardecl +" by "+newDecl);
								    				rewriter1.replace(vardecl, newDecl, null);	
								    			}
								    		}
								    	}
								    }
								 }
							  //  lrw2.insertLast(methodInvocation, null);
								//rewriter1.
								

								TextEdit edits = rewriter1.rewriteAST();
							
								SaveModification.SaveModif(cu, edits);
							}
							catch (JavaModelException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							return;
						}else {
							ASTNode aststat = ASTNode.copySubtree(method.getAST(), findStat);
							Block block = (Block) efor.getAST().createInstance(Block.class);
							block.statements().add((Statement) aststat);
							//efor.setBody(block);
							((Block)findStat.getParent()).statements().add(((Block)findStat.getParent()).statements().indexOf(findStat), efor);
							//((Block)findStat.getParent()).statements().remove(findStat);
							System.out.println(" Block body22222  "+block);
							AST ast2 = cu.getAST();
							ASTRewrite rewriter1 = ASTRewrite.create(ast2);

							NullLiteral nullLiteral = (NullLiteral) ast2.createInstance(NullLiteral.class);
							

							Document document=null;
							ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
							try {
								document = new Document(iCompilUnit.getSource());
								rewriter1.replace(node.getParent(),nullLiteral, null);  
								//ListRewrite lrw2 = rewriter1.getListRewrite(((Block)((VariableDeclarationStatement)findStat.getParent()).getParent()),Block.STATEMENTS_PROPERTY);
								
								//lrw2.insertAt(efor,((Block)((VariableDeclarationStatement)findStat.getParent()).getParent()).statements().indexOf((VariableDeclarationStatement)(findStat.getParent())) + 1, null);
								
							

							  //  lrw2.insertLast(methodInvocation, null);
								//rewriter1.
								

								TextEdit edits = rewriter1.rewriteAST();
							
								SaveModification.SaveModif(cu, edits);
							}
							catch (JavaModelException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
						return;
					}
					}
					
					findStat = findStat.getParent();
				}
				//ResolutionsUtils.replaceDirectExpressionInParentNode(cu,nodeTemp, method);
			
				//nodeTemp.delete();
				return;
				
			} else if(nodeTemp instanceof FieldAccess){//here treat the case the prop is not method call but the path is with method call and is x.y().z.w().v.prop. ...

				ASTNode astexp = ASTNode.copySubtree(method.getAST(), ((FieldAccess) nodeTemp).getExpression());
				method.setExpression((Expression) astexp);
				efor.setExpression(method);
				
				SimpleName varnameExp = (SimpleName) efor.getAST().createInstance(SimpleName.class);
				varnameExp.setIdentifier(generatedName);
				
				((FieldAccess) nodeTemp).setExpression(varnameExp);
				
				ASTNode findStat = nodeTemp;
				//boolean gotcha = false;
				while (findStat != null && !(findStat instanceof CompilationUnit)) {
					//check if it is in a variable declaration
					
					if(findStat instanceof VariableDeclarationFragment){
						
						ASTNode aststat = ASTNode.copySubtree(method.getAST(), ((VariableDeclarationFragment) findStat).getInitializer());
						Assignment assign = (Assignment) efor.getAST().createInstance(Assignment.class);
						SimpleName aname = (SimpleName) efor.getAST().createInstance(SimpleName.class);
						aname.setIdentifier(((VariableDeclarationFragment) findStat).getName().getIdentifier());
						assign.setLeftHandSide(aname);
						assign.setRightHandSide((Expression) aststat);
						ExpressionStatement expstat = (ExpressionStatement) efor.getAST().createInstance(ExpressionStatement.class);
						expstat.setExpression(assign);
						
						Block block = (Block) efor.getAST().createInstance(Block.class);
						block.statements().add(expstat);
						efor.setBody(block);
						
						((VariableDeclarationFragment) findStat).setInitializer(null);//or delete it ??
						
						//findStat.getParent()
						
						((Block)((VariableDeclarationStatement)findStat.getParent()).getParent()).statements().add(
								((Block)((VariableDeclarationStatement)findStat.getParent()).getParent()).statements().indexOf(findStat.getParent()) + 1, 
								efor);
						//((Block)findStat.getParent()).statements().remove(findStat);
						return;
						
					} else if(findStat instanceof Statement && findStat.getParent() instanceof Block){ //ExpressionStatement){
						//gotcha = true;
						//nodeTemp.delete();
						ASTNode aststat = ASTNode.copySubtree(method.getAST(), findStat);
						Block block = (Block) efor.getAST().createInstance(Block.class);
						block.statements().add((Statement) aststat);
						efor.setBody(block);
						((Block)findStat.getParent()).statements().add(((Block)findStat.getParent()).statements().indexOf(findStat), efor);
						((Block)findStat.getParent()).statements().remove(findStat);
						return;
					}
					
					findStat = findStat.getParent();
				}
				//ResolutionsUtils.replaceDirectExpressionInParentNode(nodeTemp, method);
				//nodeTemp.delete();
				return;
				
			} else if(nodeTemp instanceof QualifiedName){//here treat the case the prop is not method call and the path is not method calls like x.y.z.prop.
				
				ASTNode astexp = ASTNode.copySubtree(method.getAST(), ((QualifiedName) nodeTemp).getQualifier());
				method.setExpression((Expression) astexp);
				efor.setExpression(method);
				
				SimpleName varnameExp = (SimpleName) efor.getAST().createInstance(SimpleName.class);
				varnameExp.setIdentifier(generatedName);
				
				((QualifiedName) nodeTemp).setQualifier(varnameExp);
				
				ASTNode findStat = nodeTemp;
				//boolean gotcha = false;
				while (findStat != null && !(findStat instanceof CompilationUnit)) {
					//check if it is in a variable declaration
					
					if(findStat instanceof VariableDeclarationFragment){
						
						ASTNode aststat = ASTNode.copySubtree(method.getAST(), ((VariableDeclarationFragment) findStat).getInitializer());
						Assignment assign = (Assignment) efor.getAST().createInstance(Assignment.class);
						SimpleName aname = (SimpleName) efor.getAST().createInstance(SimpleName.class);
						aname.setIdentifier(((VariableDeclarationFragment) findStat).getName().getIdentifier());
						assign.setLeftHandSide(aname);
						assign.setRightHandSide((Expression) aststat);
						ExpressionStatement expstat = (ExpressionStatement) efor.getAST().createInstance(ExpressionStatement.class);
						expstat.setExpression(assign);
						
						Block block = (Block) efor.getAST().createInstance(Block.class);
						block.statements().add(expstat);
						efor.setBody(block);
						
						((VariableDeclarationFragment) findStat).setInitializer(null);//or delete it ??
						
						//findStat.getParent()
						
						((Block)((VariableDeclarationStatement)findStat.getParent()).getParent()).statements().add(
								((Block)((VariableDeclarationStatement)findStat.getParent()).getParent()).statements().indexOf(findStat.getParent()) + 1, 
								efor);
						//((Block)findStat.getParent()).statements().remove(findStat);
						return;
						
					} else if(findStat instanceof Statement && findStat.getParent() instanceof Block){ //ExpressionStatement){
						//gotcha = true;
						//nodeTemp.delete();
						ASTNode aststat = ASTNode.copySubtree(method.getAST(), findStat);
						Block block = (Block) efor.getAST().createInstance(Block.class);
						block.statements().add((Statement) aststat);
						efor.setBody(block);
						((Block)findStat.getParent()).statements().add(((Block)findStat.getParent()).statements().indexOf(findStat), efor);
						((Block)findStat.getParent()).statements().remove(findStat);
						return;
					}
					
					findStat = findStat.getParent();
				}
//				ResolutionsUtils.replaceDirectExpressionInParentNode(nodeTemp, convertedQN);
//				//nodeTemp.delete();
				return;
			}else 
				if(nodeTemp instanceof MethodDeclaration)
				{
					ResolutionsUtils.replaceDeleteAllCallPathExpressionInParentNode(cu,nodeTemp);
	
				}
			
			nodeTemp = nodeTemp.getParent();
		}
	}

	/* Here we extend navigation path with just a method call
	 * from x.y.prop to x.y.ref.prop
	 * */
	private static void applyResolutionExtendNavigationPath(CompilationUnit cu,String throughReference, ASTNode node) {
		
		MethodInvocation method = (MethodInvocation) node.getAST().createInstance(MethodInvocation.class);
		SimpleName name = (SimpleName) node.getAST().createInstance(SimpleName.class);
		name.setIdentifier("get"+UsesManager.capitalizeFirstLetter(throughReference));
		method.setName(name);
		
		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			//check if it is in a variable declaration
			
			if(nodeTemp instanceof MethodInvocation){
				
//				System.out.println("			yay khelladi method invocation "+nodeTemp + " >> class "+nodeTemp.getClass());
//				System.out.println("			yay khelladi parent method invocation "+nodeTemp.getParent() + " >> class "+nodeTemp.getParent().getClass());
				
//				//here instead of exchanging
//				method.setName(((MethodInvocation) nodeTemp).getName());
//				name.setIdentifier(throughReference);
//				((MethodInvocation) nodeTemp).setName(name);
				
				//ASTNode ast = ASTNode.copySubtree(method.getAST(), ((MethodInvocation) nodeTemp).getExpression());
				ASTNode ast = ((MethodInvocation) nodeTemp).getExpression();
				((MethodInvocation) nodeTemp).setExpression(null);
				method.setExpression((Expression) ast);
				((MethodInvocation) nodeTemp).setExpression(method);
				System.out.println(" in MethodInvocation  "+nodeTemp);
				//ResolutionsUtils.replaceDirectExpressionInParentNode(nodeTemp, method);
				//nodeTemp.delete();
				AST ast2 = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast2);

				//  IPath pathcu = cu.getJavaElement().getPath();
			 
				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					System.out.println(" Replacing   "+nodeTemp+" byyyy "+method);
					rewriter1.replace(nodeTemp,method, null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);

					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
				
			} else if(nodeTemp instanceof FieldAccess){//here treat the case the prop is not method call but the path is with method call and is x.y().z.w().v.prop. ...
				System.out.println(" FA");
				//ASTNode ast = ASTNode.copySubtree(method.getAST(), ((FieldAccess) nodeTemp).getExpression());
				ASTNode ast = ((FieldAccess) nodeTemp).getExpression();
				((FieldAccess) nodeTemp).setExpression((FieldAccess) method.getAST().createInstance(FieldAccess.class));
				method.setExpression((Expression) ast);
				((FieldAccess) nodeTemp).setExpression(method);
				//ResolutionsUtils.replaceDirectExpressionInParentNode(nodeTemp, method);
				//nodeTemp.delete();
				return;
				
			} else if(nodeTemp instanceof QualifiedName){//here treat the case the prop is not method call and the path is not method calls like x.y.z.prop.
				System.out.println(" QN");
				//here i will change the QN to FA, put its child the method, and the chil of QN to the method child
				FieldAccess convertedQN = (FieldAccess) method.getAST().createInstance(FieldAccess.class);
				SimpleName qname = (SimpleName) node.getAST().createInstance(SimpleName.class);
				qname.setIdentifier(((QualifiedName) nodeTemp).getName().getIdentifier());
				convertedQN.setName(qname);
				
				//ASTNode ast = ASTNode.copySubtree(method.getAST(), ((QualifiedName) nodeTemp).getQualifier());
				ASTNode ast = ((QualifiedName) nodeTemp).getQualifier();
				((QualifiedName) nodeTemp).setQualifier((QualifiedName) method.getAST().createInstance(QualifiedName.class));//here becasue we cannot put null as it is mandatory
				method.setExpression((Expression) ast);
				convertedQN.setExpression(method);
				ResolutionsUtils.replaceDirectExpressionInParentNode(cu,nodeTemp, convertedQN);
				//nodeTemp.delete();
				return;
			}
			else if(nodeTemp instanceof MethodDeclaration)
			{

				AST ast = cu.getAST(); 
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				//  IPath pathcu = cu.getJavaElement().getPath();

				Document document=null; 
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.remove(nodeTemp, null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);
				
					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
			
			nodeTemp = nodeTemp.getParent();
		}
		
	}
}
