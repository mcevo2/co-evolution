package Utilities;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import fr.lip6.meta.ComplexChangeDetection.ComplexChanges.PushProperty;

public class PushResolution {//can treat Flatten hieararchy too

	
	/* Here we treat the cases of Push property
	 * 0 introduce an if else, we test if the type of the call before the prop, is of type target class, then we add a cast
	 * 1 replace element with another one
	 * */
	public static void applyResolution(CompilationUnit cu,PushProperty push, ASTNode node, int choice){
		
		if(choice == 0) {
			
			for(String subClass : push.getSubClassesNames()){
				ResolutionsUtils.addMissingImports(cu,subClass, node);
				
				PushResolution.applyResolutionIntroduceIfCast(cu,subClass, node);
			}
			
		} 
//		else if(choice == 1) {
//
//			//PushResolution.applyResolutionReduceNavigationPath("", node);
//			
//		} else if(choice == 2) {
//
//			//PushResolution.applyResolutionExtendNavigationPathWIthLoop("", "", node);
//			
//		} else if(choice == 3) {
//			
//		}
	}

	private static void applyResolutionIntroduceIfCast(CompilationUnit cu,String subClass, ASTNode node) {

		/* Steps to do 
		 * copy call path until p, 
		 * create an if, and test in its expression if call path until p is instance of subclass,
		 * crete a then expressio wich contains the statment of prop, where a cast is introduced 
		 * the cast type if the subclass
		 * add an import of subclass in case it is not already imported
		 * then put the var under the prop expression and thats it
		 * */
		
		//here we create the structure of the loop and all most of its needed elements
		IfStatement eif = (IfStatement) node.getRoot().getAST().createInstance(IfStatement.class);
//		SingleVariableDeclaration param = (SingleVariableDeclaration) node.getAST().createInstance(SingleVariableDeclaration.class);
		SimpleType type = (SimpleType) eif.getAST().createInstance(SimpleType.class);
		SimpleName name = (SimpleName) eif.getAST().createInstance(SimpleName.class);

		
		InstanceofExpression instance = (InstanceofExpression) eif.getAST().createInstance(InstanceofExpression.class);
		
		name.setIdentifier(subClass);
		type.setName(name);
		instance.setRightOperand(type);
	
		ParenthesizedExpression paranthaze = (ParenthesizedExpression) eif.getAST().createInstance(ParenthesizedExpression.class);
		CastExpression cast = (CastExpression) eif.getAST().createInstance(CastExpression.class);
		
		SimpleType ctype = (SimpleType) eif.getAST().createInstance(SimpleType.class);
		SimpleName cname = (SimpleName) eif.getAST().createInstance(SimpleName.class);
		cname.setIdentifier(subClass);
		ctype.setName(cname);
		cast.setType(ctype);
		
//		SimpleName varnameParam = (SimpleName) efor.getAST().createInstance(SimpleName.class);
//		String generatedName = targetClassName.toLowerCase() + "_" + ((int)(Math.random()*10000));
//		varnameParam.setIdentifier(generatedName);
//		param.setName(varnameParam);
//		efor.setParameter(param);
		
		ASTNode nodeTemp = node;
		//boolean gotcha = false;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			//check if it is in a variable declaration
			
			if(nodeTemp instanceof MethodInvocation){
//				System.out.println("			yay khelladi method invocation "+nodeTemp + " >> class "+nodeTemp.getClass());
//				System.out.println("			yay khelladi parent method invocation "+nodeTemp.getParent() + " >> class "+nodeTemp.getParent().getClass());
				
				//here we put the expression cast in the then statment of if
				ASTNode astexpInstance = ASTNode.copySubtree(eif.getAST(), ((MethodInvocation) nodeTemp).getExpression());
				instance.setLeftOperand((Expression) astexpInstance);
				eif.setExpression(instance);
				
				ASTNode astexpCast = ASTNode.copySubtree(eif.getAST(), ((MethodInvocation) nodeTemp).getExpression());
				cast.setExpression((Expression) astexpCast);
				paranthaze.setExpression(cast);
				((MethodInvocation) nodeTemp).setExpression(paranthaze);
				
				ASTNode findStat = nodeTemp;
				//boolean gotcha = false;
				
//				System.out.println("			1 >> yay deidara MethodInvocation "+nodeTemp + " >> class "+nodeTemp.getClass());
				
				//now we treat the body of the if
				while (findStat != null && !(findStat instanceof CompilationUnit)) {
					//check if it is in a variable declaration
					System.out.println("			2 >> yay deidara MethodInvocation "+findStat + " >> class "+findStat.getClass());
//					System.out.println("			2 >> yay deidara MethodInvocation "+findStat.getParent() + " >> class "+findStat.getParent().getClass());
					
					if(findStat instanceof VariableDeclarationFragment){//treat teh case of var delcaration, we keep it declaration out of the if, and we creat an assiment for it
						System.out.println("			2.1 >> yay deidara MethodInvocation "+findStat + " >> class "+findStat.getClass());
						
						ASTNode aststat = ASTNode.copySubtree(eif.getAST(), ((VariableDeclarationFragment) findStat).getInitializer());
						Assignment assign = (Assignment) eif.getAST().createInstance(Assignment.class);
						SimpleName aname = (SimpleName) eif.getAST().createInstance(SimpleName.class);
						aname.setIdentifier(((VariableDeclarationFragment) findStat).getName().getIdentifier());
						assign.setLeftHandSide(aname);
						assign.setRightHandSide((Expression) aststat);
						ExpressionStatement expstat = (ExpressionStatement) eif.getAST().createInstance(ExpressionStatement.class);
						expstat.setExpression(assign);
						
						
						Block block = (Block) eif.getAST().createInstance(Block.class);
						block.statements().add(expstat);
						eif.setThenStatement(block);

						//we delete the treated statment
						((VariableDeclarationFragment) findStat).setInitializer(null);//getInitializer().delete();//or delete it ??
						
						//findStat.getParent()
						
						((Block)((VariableDeclarationStatement)findStat.getParent()).getParent()).statements().add(
								((Block)((VariableDeclarationStatement)findStat.getParent()).getParent()).statements().indexOf(findStat.getParent()) + 1, 
								eif);
						//((Block)findStat.getParent()).statements().remove(findStat);
						return;
						
					} else if(findStat instanceof Statement && findStat.getParent() instanceof Block){//treat the rest of cases 
						//gotcha = true;
						//nodeTemp.delete();
						System.out.println("			2.2 >> yay deidara MethodInvocation "+findStat + " >> class "+findStat.getClass());

						((Block)findStat.getParent()).statements().add(((Block)findStat.getParent()).statements().indexOf(findStat), eif);
						((Block)findStat.getParent()).statements().remove(findStat);
						//ASTNode aststat = ASTNode.copySubtree(eif.getAST(), findStat);
						Block block = (Block) eif.getAST().createInstance(Block.class);
						block.statements().add(findStat);//(Statement) 
						eif.setThenStatement(block);
						
						//we delete the treated statment
						//findStat.delete();
						return;
					}
					
					findStat = findStat.getParent();
				}
				//ResolutionsUtils.replaceDirectExpressionInParentNode(nodeTemp, method);
				//nodeTemp.delete();
				return;
				
			} else if(nodeTemp instanceof FieldAccess){//here treat the case the prop is not method call but the path is with method call and is x.y().z.w().v.prop. ...

				//here we put the expression cast in the then statment of if//now we treat the body of the if
				ASTNode astexpInstance = ASTNode.copySubtree(eif.getAST(), ((FieldAccess) nodeTemp).getExpression());
				instance.setLeftOperand((Expression) astexpInstance);
				eif.setExpression(instance);
				
				ASTNode astexpCast = ASTNode.copySubtree(eif.getAST(), ((FieldAccess) nodeTemp).getExpression());
				cast.setExpression((Expression) astexpCast);
				paranthaze.setExpression(cast);
				((FieldAccess) nodeTemp).setExpression(paranthaze);
				
				ASTNode findStat = nodeTemp;
				//boolean gotcha = false;
//				System.out.println("			1 >> yay deidara FieldAccess "+nodeTemp + " >> class "+nodeTemp.getClass());
				
				//now we treat the body of the if
				while (findStat != null && !(findStat instanceof CompilationUnit)) {
					//check if it is in a variable declaration
					System.out.println("			2 >> yay deidara FieldAccess "+findStat + " >> class "+findStat.getClass());
//					System.out.println("			2 >> yay deidara FieldAccess "+findStat.getParent() + " >> class "+findStat.getParent().getClass());
					
					if(findStat instanceof VariableDeclarationFragment){//treat teh case of var delcaration, we keep it declaration out of the if, and we creat an assiment for it
						System.out.println("			2.1 >> yay deidara FieldAccess "+findStat + " >> class "+findStat.getClass());
						
						ASTNode aststat = ASTNode.copySubtree(eif.getAST(), ((VariableDeclarationFragment) findStat).getInitializer());
						Assignment assign = (Assignment) eif.getAST().createInstance(Assignment.class);
						SimpleName aname = (SimpleName) eif.getAST().createInstance(SimpleName.class);
						aname.setIdentifier(((VariableDeclarationFragment) findStat).getName().getIdentifier());
						assign.setLeftHandSide(aname);
						assign.setRightHandSide((Expression) aststat);
						ExpressionStatement expstat = (ExpressionStatement) eif.getAST().createInstance(ExpressionStatement.class);
						expstat.setExpression(assign);
						
						Block block = (Block) eif.getAST().createInstance(Block.class);
						block.statements().add(expstat);
						eif.setThenStatement(block);
						
						//we delete the treated statment
						((VariableDeclarationFragment) findStat).setInitializer(null);//getInitializer().delete();//or delete it ??
						
						//findStat.getParent()
						
						((Block)((VariableDeclarationStatement)findStat.getParent()).getParent()).statements().add(
								((Block)((VariableDeclarationStatement)findStat.getParent()).getParent()).statements().indexOf(findStat.getParent()) + 1, 
								eif);
						//((Block)findStat.getParent()).statements().remove(findStat);
						return;
						
					} else if(findStat instanceof Statement && findStat.getParent() instanceof Block){//treat the rest of cases 
						//gotcha = true;
						//nodeTemp.delete();
						System.out.println("			2.2 >> yay deidara FieldAccess "+findStat + " >> class "+findStat.getClass());
						
						((Block)findStat.getParent()).statements().add(((Block)findStat.getParent()).statements().indexOf(findStat), eif);
						((Block)findStat.getParent()).statements().remove(findStat);
						//ASTNode aststat = ASTNode.copySubtree(eif.getAST(), findStat);
						Block block = (Block) eif.getAST().createInstance(Block.class);
						block.statements().add(findStat);//(Statement) 
						eif.setThenStatement(block);
						//we delete the treated statment
						//findStat.delete();
						return;
					}
					
					findStat = findStat.getParent();
				}
				//ResolutionsUtils.replaceDirectExpressionInParentNode(nodeTemp, method);
				//nodeTemp.delete();
				return;
				
			} else if(nodeTemp instanceof QualifiedName){//here treat the case the prop is not method call and the path is not method calls like x.y.z.prop.
				
				//here we put the expression cast in the then statment of if
				ASTNode astexpInstance = ASTNode.copySubtree(eif.getAST(), ((QualifiedName) nodeTemp).getQualifier());
				instance.setLeftOperand((Expression) astexpInstance);
				eif.setExpression(instance);
				
				ASTNode astexpCast = ASTNode.copySubtree(eif.getAST(), ((QualifiedName) nodeTemp).getQualifier());
				cast.setExpression((Expression) astexpCast);
				paranthaze.setExpression(cast);
				
				//now we create a FieldAccess,to replace later the QN
				FieldAccess fieldOf_nodetemp = (FieldAccess) eif.getAST().createInstance(FieldAccess.class);
				ASTNode fname = ASTNode.copySubtree(eif.getAST(), ((QualifiedName) nodeTemp).getName());
				
				fieldOf_nodetemp.setName((SimpleName) fname);
				fieldOf_nodetemp.setExpression(paranthaze);
//				System.out.println("			1 >> yay deidara QualifiedName get parent "+fieldOf_nodetemp.getParent() );
				
				ResolutionsUtils.replaceDirectExpressionInParentNode(cu,nodeTemp, fieldOf_nodetemp);
				//((QualifiedName) nodeTemp).setQualifier(paranthaze);
//				System.out.println("			1 >> yay deidara QualifiedName get parent "+fieldOf_nodetemp.getParent() );
				ASTNode findStat = fieldOf_nodetemp;//nodeTemp;
				//boolean gotcha = false;
				
//				System.out.println("			1 >> yay deidara QualifiedName "+fieldOf_nodetemp + " >> class "+fieldOf_nodetemp.getClass());
				
				//now we treat the body of the if
				while (findStat != null && !(findStat instanceof CompilationUnit)) {
					//check if it is in a variable declaration
					System.out.println("			2 >> yay deidara QualifiedName "+findStat + " >> class "+findStat.getClass());
//					System.out.println("			2 >> yay deidara QualifiedName "+findStat.getParent() + " >> class ");//+findStat.getParent().getClass());
					if(findStat instanceof VariableDeclarationFragment){//treat teh case of var delcaration, we keep it declaration out of the if, and we creat an assiment for it
						System.out.println("			2.1 >> yay deidara QualifiedName "+findStat + " >> class "+findStat.getClass());
						ASTNode aststat = ASTNode.copySubtree(eif.getAST(), ((VariableDeclarationFragment) findStat).getInitializer());
						Assignment assign = (Assignment) eif.getAST().createInstance(Assignment.class);
						SimpleName aname = (SimpleName) eif.getAST().createInstance(SimpleName.class);
						aname.setIdentifier(((VariableDeclarationFragment) findStat).getName().getIdentifier());
						assign.setLeftHandSide(aname);
						assign.setRightHandSide((Expression) aststat);
						ExpressionStatement expstat = (ExpressionStatement) eif.getAST().createInstance(ExpressionStatement.class);
						expstat.setExpression(assign);
						
						Block block = (Block) eif.getAST().createInstance(Block.class);
						block.statements().add(expstat);
						eif.setThenStatement(block);
						
						//we delete the treated statment
						((VariableDeclarationFragment) findStat).setInitializer(null);//getInitializer().delete();//or delete it ??
						
						//findStat.getParent()
						
						((Block)((VariableDeclarationStatement)findStat.getParent()).getParent()).statements().add(
								((Block)((VariableDeclarationStatement)findStat.getParent()).getParent()).statements().indexOf(findStat.getParent()) + 1, 
								eif);
						//now we replace the QualifiedName with a FieldAccess, cuz we cannot put a cast exp in a QN
						//what if the path is impacted by another change (eg. rename) => make sure the copy still reference it ;), otherwise, rerun visitor
						//ResolutionsUtils.replaceDirectExpressionInParentNode(nodeTemp, fieldOf_nodetemp);
						//((Block)findStat.getParent()).statements().remove(findStat);
						return;
						
					} else if(findStat instanceof Statement && findStat.getParent() instanceof Block){//treat the rest of cases 
						//gotcha = true;
						//nodeTemp.delete();
						System.out.println("			2.2 >> yay deidara QualifiedName "+findStat + " >> class "+findStat.getClass());
						((Block)findStat.getParent()).statements().add(((Block)findStat.getParent()).statements().indexOf(findStat), eif);
						((Block)findStat.getParent()).statements().remove(findStat);
						//ASTNode aststat = ASTNode.copySubtree(eif.getAST(), findStat);
						Block block = (Block) eif.getAST().createInstance(Block.class);
						block.statements().add(findStat);//(Statement) 
						eif.setThenStatement(block);
						
						//now we replace the QualifiedName with a FieldAccess, cuz we cannot put a cast exp in a QN
						//ResolutionsUtils.replaceDirectExpressionInParentNode(nodeTemp, fieldOf_nodetemp);
						
						//we delete the treated statment
						//findStat.delete();
						return;
					}
					
					findStat = findStat.getParent();
				}
				//ResolutionsUtils.replaceDirectExpressionInParentNode(nodeTemp, method);
				//nodeTemp.delete();
				return;
			}
			
			nodeTemp = nodeTemp.getParent();
		}
		
	}

}
