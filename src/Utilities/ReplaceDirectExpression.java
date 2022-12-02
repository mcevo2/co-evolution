package Utilities;


import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionMethodReference;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodReference;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

public class ReplaceDirectExpression {

	/* here we replace x.y.z.deletedPro.w... by x.y.z.w... in its parent node 
	 */
	public static void replaceExpressionInParentNode(CompilationUnit cu,ASTNode oldExp, ASTNode newExp){ 
		if( oldExp ==null) {	
			System.out.println("  old exp null");
		}
		else if(oldExp.getParent()== null)
		{
			
			System.out.println("  oldexp parent null ,exp is " +oldExp +" replace by "+newExp);
		}
		else
		System.out.println(" type of node " +(oldExp.getParent().getClass()));
		if(oldExp.getParent() instanceof Statement){
			System.out.println(" in replaceExpressionInParentNode stat");
			replaceExpressionInParentNodeStatement(cu,oldExp, newExp);
			
		} else if(oldExp.getParent() instanceof Expression){
			System.out.println(" in replaceExpressionInParentNode Expression");
			replaceExpressionInParentNodeExpression(cu,oldExp, newExp);
			
		} else if(oldExp.getParent() instanceof VariableDeclarationFragment){
			
			System.out.println(" in replaceExpressionInParentNode VariableDeclarationFragment");
			if(((VariableDeclarationFragment)oldExp.getParent()).getInitializer().equals(oldExp)){
				ASTNode ast = ASTNode.copySubtree(oldExp.getAST(), newExp);
				((VariableDeclarationFragment)oldExp.getParent()).setInitializer((Expression) ast);	
			}
			
		} else if(oldExp.getParent() instanceof SingleVariableDeclaration){
			
			System.out.println(" in replaceExpressionInParentNode SingleVariableDeclaration");
			if(((SingleVariableDeclaration)oldExp.getParent()).getInitializer().equals(oldExp)){
				ASTNode ast = ASTNode.copySubtree(oldExp.getAST(), newExp);
				((SingleVariableDeclaration)oldExp.getParent()).setInitializer((Expression) ast);	
				
			}
		}
		System.out.println(" khraj ml method");
	}
	
	private static void replaceExpressionInParentNodeStatement(CompilationUnit cu,ASTNode oldExp, ASTNode newExp){
		
		ASTNode ast = null;
		System.out.println(" new exp is " +newExp);
		if(newExp.getParent() == null) ast = newExp;
		else ASTNode.copySubtree(oldExp.getAST(), newExp);
		
		if(oldExp.getParent() instanceof AssertStatement){
			//if(((AssertStatement)oldExp.getParent()).get)
			
			if(((AssertStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((AssertStatement)oldExp.getParent()).setExpression((Expression) ast);
				
			} else if(((AssertStatement)oldExp.getParent()).getMessage().equals(oldExp)){
				
				((AssertStatement)oldExp.getParent()).setMessage((Expression) ast);
			}
			
		} else if(oldExp.getParent() instanceof Block){
			
		} else if(oldExp.getParent() instanceof BreakStatement){
			
		} else if(oldExp.getParent() instanceof ConstructorInvocation){
			
			if(((ConstructorInvocation)oldExp.getParent()).arguments().contains(oldExp)){
				
				((ConstructorInvocation)oldExp.getParent()).arguments().add(
						((ConstructorInvocation)oldExp.getParent()).arguments().indexOf(oldExp), ast);
				((ConstructorInvocation)oldExp.getParent()).arguments().remove(oldExp);
			} 

		} else if(oldExp.getParent() instanceof ContinueStatement){
			
		} else if(oldExp.getParent() instanceof DoStatement){
			
			if(((DoStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((DoStatement)oldExp.getParent()).setExpression((Expression) ast);
			}
			
		} else if(oldExp.getParent() instanceof EmptyStatement){
			
		} else if(oldExp.getParent() instanceof EnhancedForStatement){
			
			if(((EnhancedForStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((EnhancedForStatement)oldExp.getParent()).setExpression((Expression) ast);	
			} 
//			else if(((EnhancedForStatement)oldExp.getParent()).getBody().equals(oldExp)){
//				
//			}
			
		} else if(oldExp.getParent() instanceof ExpressionStatement){
			
			if(((ExpressionStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((ExpressionStatement)oldExp.getParent()).setExpression((Expression) ast);	
			} 

		} else if(oldExp.getParent() instanceof ForStatement){
			
			if(((ForStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((ForStatement)oldExp.getParent()).setExpression((Expression) ast);	
				
			} else if(((ForStatement)oldExp.getParent()).initializers().contains(oldExp)){
				
				((ForStatement)oldExp.getParent()).initializers().add(
						((ForStatement)oldExp.getParent()).initializers().indexOf(oldExp), ast);
				((ForStatement)oldExp.getParent()).initializers().remove(oldExp);
				
			} else if(((ForStatement)oldExp.getParent()).updaters().contains(oldExp)){
				
				((ForStatement)oldExp.getParent()).updaters().add(
						((ForStatement)oldExp.getParent()).updaters().indexOf(oldExp), ast);
			 	((ForStatement)oldExp.getParent()).updaters().remove(oldExp);
			} 
			
		} else if(oldExp.getParent() instanceof IfStatement){
			
			if(((IfStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				if(ast!=null)
				((IfStatement)oldExp.getParent()).setExpression((Expression) ast);	
				else {
					try{
						//((InstanceofExpression)oldExp.getParent()).delete();//delete in both cases where like x.y.deletdprop[0] or z[x.y.deletdprop]
						AST ast2 = cu.getAST();
						ASTRewrite rewriter1 = ASTRewrite.create(ast2);

						Document document=null;
						ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
						try {
							document = new Document(iCompilUnit.getSource());
							rewriter1.remove(oldExp.getParent(), null);  

							TextEdit edits = rewriter1.rewriteAST(document, null);
						
							SaveModification.SaveModif(cu, edits);
						}
						catch (JavaModelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (IllegalArgumentException e){
						//replaceExpressionInParentNode(cu,oldExp.getParent()); //it should be treated by ExpressionStatement
					}

					
				}
			
			} 
//			else if(((IfStatement)oldExp.getParent()).get){
//				
//			} else if(){
//				
//			}

		} else if(oldExp.getParent() instanceof LabeledStatement){
			
		} else if(oldExp.getParent() instanceof ReturnStatement){
			
			if(((ReturnStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((ReturnStatement)oldExp.getParent()).setExpression((Expression) ast);	
			}

		} else if(oldExp.getParent() instanceof SuperConstructorInvocation){
			
			if(((SuperConstructorInvocation)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((SuperConstructorInvocation)oldExp.getParent()).setExpression((Expression) ast);	
				
			} else if(((SuperConstructorInvocation)oldExp.getParent()).arguments().contains(oldExp)){
				
				((SuperConstructorInvocation)oldExp.getParent()).arguments().add(
						((SuperConstructorInvocation)oldExp.getParent()).arguments().indexOf(oldExp), ast);
				((SuperConstructorInvocation)oldExp.getParent()).arguments().remove(oldExp);	
			}

		} else if(oldExp.getParent() instanceof SwitchCase){
			
			if(((SwitchCase)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((SwitchCase)oldExp.getParent()).setExpression((Expression) ast);		
			}
			
		} else if(oldExp.getParent() instanceof SwitchStatement){
			
			if(((SwitchStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((SwitchStatement)oldExp.getParent()).setExpression((Expression) ast);		
			}//TODO check if the switch statments are treated along with the switch cases, but it should be treated as they are statements
			
		} else if(oldExp.getParent() instanceof SynchronizedStatement){
			
			if(((SynchronizedStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((SynchronizedStatement)oldExp.getParent()).setExpression((Expression) ast);		
			}
			
		} else if(oldExp.getParent() instanceof ThrowStatement){
			
			if(((ThrowStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((ThrowStatement)oldExp.getParent()).setExpression((Expression) ast);		
			}
			
		} else if(oldExp.getParent() instanceof TryStatement){
			//TODO check the resources and catch clauses, althought as they are based on paremeters and blocks, stetemenst, they should be treated too
			
		} else if(oldExp.getParent() instanceof TypeDeclarationStatement){
			//this type wraps TypeDeclaration (class declaration) and EnumDeclaration (ennum declaration)
			
		} else if(oldExp.getParent() instanceof VariableDeclarationStatement){
			//this si treated in VariableDeclarationFragment
			
		} else if(oldExp.getParent() instanceof WhileStatement){
			
			if(((WhileStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((WhileStatement)oldExp.getParent()).setExpression((Expression) ast);		
			}
		}
	}
	
	private static void replaceExpressionInParentNodeExpression(CompilationUnit cu,ASTNode oldExp, ASTNode newExp){
		
		//ASTNode ast = ASTNode.copySubtree(oldExp.getAST(), newExp);
		
		ASTNode ast = null;
		
		if(newExp.getParent() == null) ast = newExp;
		else ASTNode.copySubtree(oldExp.getAST(), newExp);
		
		if(oldExp.getParent() instanceof Annotation){
			//TODO the diffrent anotations are not trrated, especially the SingleMemberAnnotation wich has an expression 
			
		} else if(oldExp.getParent() instanceof ArrayAccess){
			
			if(((ArrayAccess)oldExp.getParent()).getArray().equals(oldExp)){
				
				((ArrayAccess)oldExp.getParent()).setArray((Expression) ast);
				
			} else if(((ArrayAccess)oldExp.getParent()).getIndex().equals(oldExp)){
				
				((ArrayAccess)oldExp.getParent()).setIndex((Expression) ast);		
			}

		} else if(oldExp.getParent() instanceof ArrayCreation){
			
			if(((ArrayCreation)oldExp.getParent()).dimensions().contains(oldExp)){
				
				((ArrayCreation)oldExp.getParent()).dimensions().add(
						((ArrayCreation)oldExp.getParent()).dimensions().indexOf(oldExp), ast);
				((ArrayCreation)oldExp.getParent()).dimensions().remove(oldExp);					
			}

		} else if(oldExp.getParent() instanceof ArrayInitializer){
			
			if(((ArrayInitializer)oldExp.getParent()).expressions().contains(oldExp)){
				
				((ArrayInitializer)oldExp.getParent()).expressions().add(
						((ArrayInitializer)oldExp.getParent()).expressions().indexOf(oldExp), ast);
				((ArrayInitializer)oldExp.getParent()).expressions().remove(oldExp);					
			}
			
		} else if(oldExp.getParent() instanceof Assignment){
			
			if(((Assignment)oldExp.getParent()).getLeftHandSide().equals(oldExp)){
				
				((Assignment)oldExp.getParent()).setLeftHandSide((Expression) ast);
				
			} else if(((Assignment)oldExp.getParent()).getRightHandSide().equals(oldExp)){
				
				((Assignment)oldExp.getParent()).setRightHandSide((Expression) ast);		
			}
			
		} else if(oldExp.getParent() instanceof BooleanLiteral){
			
		} else if(oldExp.getParent() instanceof CastExpression){
			
			if(((CastExpression)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((CastExpression)oldExp.getParent()).setExpression((Expression) ast);
			}
			
		} else if(oldExp.getParent() instanceof CharacterLiteral){
			
		} else if(oldExp.getParent() instanceof ClassInstanceCreation){
			
			if(((ClassInstanceCreation)oldExp.getParent()).arguments().contains(oldExp)){
				
				((ClassInstanceCreation)oldExp.getParent()).arguments().add(
						((ClassInstanceCreation)oldExp.getParent()).arguments().indexOf(oldExp), ast);
				((ClassInstanceCreation)oldExp.getParent()).arguments().remove(oldExp);					
			}
			
		} else if(oldExp.getParent() instanceof ConditionalExpression){
			
			if(((ConditionalExpression)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((ConditionalExpression)oldExp.getParent()).setExpression((Expression) ast);
				
			} else if(((ConditionalExpression)oldExp.getParent()).getThenExpression().equals(oldExp)){
				
				((ConditionalExpression)oldExp.getParent()).setThenExpression((Expression) ast);
				
			} else if(((ConditionalExpression)oldExp.getParent()).getElseExpression().equals(oldExp)){
				
				((ConditionalExpression)oldExp.getParent()).setElseExpression((Expression) ast);
			}
			
		} else if(oldExp.getParent() instanceof FieldAccess){
			
			if(((FieldAccess)oldExp.getParent()).getExpression().equals(oldExp)){
				
				((FieldAccess)oldExp.getParent()).setExpression((Expression) ast);
			}
			
		} else if(oldExp.getParent() instanceof InfixExpression){
			
			if(((InfixExpression)oldExp.getParent()).getLeftOperand().equals(oldExp)){
				
				((InfixExpression)oldExp.getParent()).setLeftOperand((Expression) ast);
				
			} else if(((InfixExpression)oldExp.getParent()).getRightOperand().equals(oldExp)){
				
				((InfixExpression)oldExp.getParent()).setRightOperand((Expression) ast);
				
			} else if(((InfixExpression)oldExp.getParent()).extendedOperands().contains(oldExp)){
				
				((InfixExpression)oldExp.getParent()).extendedOperands().add(
						((InfixExpression)oldExp.getParent()).extendedOperands().indexOf(oldExp), ast);
				((InfixExpression)oldExp.getParent()).extendedOperands().remove(oldExp);					
			}
			
		} else if(oldExp.getParent() instanceof InstanceofExpression){

			if(((InstanceofExpression)oldExp.getParent()).getLeftOperand().equals(oldExp)){
				
				((InstanceofExpression)oldExp.getParent()).setLeftOperand((Expression) ast);
			}
			
		} else if(oldExp.getParent() instanceof LambdaExpression){
			//TODO not treated here, but should already be treated as it has an expression and praamreters a s var declarations
			
		} else if(oldExp.getParent() instanceof MethodInvocation){
			
			if(((MethodInvocation)oldExp.getParent()).arguments().contains(oldExp)){
				
				((MethodInvocation)oldExp.getParent()).arguments().add(
						((MethodInvocation)oldExp.getParent()).arguments().indexOf(oldExp), ast);
				((MethodInvocation)oldExp.getParent()).arguments().remove(oldExp);
				
			} else if(((MethodInvocation)oldExp.getParent()).getExpression().equals(oldExp)){
				 
				((MethodInvocation)oldExp.getParent()).setExpression((Expression) ast);	
			}
			
		} else if(oldExp.getParent() instanceof MethodReference){
			
			if(oldExp.getParent() instanceof ExpressionMethodReference && ((ExpressionMethodReference)oldExp.getParent()).getExpression().equals(oldExp)){
				 
				((ExpressionMethodReference)oldExp.getParent()).setExpression((Expression) ast);	
			}
			
		} else if(oldExp.getParent() instanceof QualifiedName){//Name){
			System.out.println("			assignement error  "+oldExp + " >> class "+oldExp.getClass());
			System.out.println("			assignement error  "+oldExp.getParent() + " >> class "+oldExp.getParent().getClass());
			//((QualifiedName)oldExp.getParent()).set
			if(((QualifiedName)oldExp.getParent()).getQualifier().equals(oldExp)){
				 
				((QualifiedName)oldExp.getParent()).setQualifier((Name) ast);	
			} else if(((QualifiedName)oldExp.getParent()).getName().equals(oldExp)){
				 
				((QualifiedName)oldExp.getParent()).setName((SimpleName) ast);	
			}
			
		} else if(oldExp.getParent() instanceof NullLiteral){
			
		} else if(oldExp.getParent() instanceof NumberLiteral){
			
		} else if(oldExp.getParent() instanceof ParenthesizedExpression){

			if(((ParenthesizedExpression)oldExp.getParent()).getExpression().equals(oldExp)){
				 
				((ParenthesizedExpression)oldExp.getParent()).setExpression((Expression) ast);	
			}
			
		} else if(oldExp.getParent() instanceof PostfixExpression){

			if(((PostfixExpression)oldExp.getParent()).getOperand().equals(oldExp)){
				 
				((PostfixExpression)oldExp.getParent()).setOperand((Expression) ast);	
			}
			
		} else if(oldExp.getParent() instanceof PrefixExpression){

			if(((PrefixExpression)oldExp.getParent()).getOperand().equals(oldExp)){
				 
				((PrefixExpression)oldExp.getParent()).setOperand((Expression) ast);	
			}
			
		} else if(oldExp.getParent() instanceof StringLiteral){
			
		} else if(oldExp.getParent() instanceof SuperFieldAccess){
			//TODO here the class and prop as in names, not expression
		} else if(oldExp.getParent() instanceof SuperMethodInvocation){
			
			if(((SuperMethodInvocation)oldExp.getParent()).arguments().contains(oldExp)){
				
				((SuperMethodInvocation)oldExp.getParent()).arguments().add(
						((SuperMethodInvocation)oldExp.getParent()).arguments().indexOf(oldExp), ast);
				((SuperMethodInvocation)oldExp.getParent()).arguments().remove(oldExp);
			
			} else {
				//TODO here the case wehre Class.super.deletedProp(), => delete the whole expressio ??
			}
			
		} else if(oldExp.getParent() instanceof ThisExpression){
			
		} else if(oldExp.getParent() instanceof TypeLiteral){
			
		} else if(oldExp.getParent() instanceof VariableDeclarationExpression){
			
		}
	}
}
