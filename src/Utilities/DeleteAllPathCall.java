package Utilities;


import java.util.Iterator;

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
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodReference;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.ReturnStatement;
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

public class DeleteAllPathCall {

	/* here we replace x.y.z.deletedPro.w... by nothing (we remove it) in its parent node 
	 */
	public static void replaceExpressionInParentNode(CompilationUnit cu,ASTNode oldExp){
		if(oldExp instanceof MethodDeclaration)
		{
			AST ast = cu.getAST(); 
			ASTRewrite rewriter1 = ASTRewrite.create(ast);

			//  IPath pathcu = cu.getJavaElement().getPath();

			Document document=null; 
			ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
			try {
				document = new Document(iCompilUnit.getSource());
				rewriter1.remove(oldExp, null);  

				TextEdit edits = rewriter1.rewriteAST(document, null);
			
				SaveModification.SaveModif(cu, edits);
			}
			catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		else 	
		if(oldExp.getParent() instanceof Statement){
			System.out.println(" in if statement "+oldExp.getNodeType());
			
			replaceExpressionInParentNodeStatement(cu,oldExp);
			
		} else if(oldExp.getParent() instanceof Expression){ 
			System.out.println(" in if Expression "+oldExp.getNodeType());
						
			replaceExpressionInParentNodeExpression(cu,oldExp);
			
		} else if(oldExp.getParent() instanceof VariableDeclarationFragment){
			
			if(((VariableDeclarationFragment)oldExp.getParent()).getInitializer().equals(oldExp)){
				System.out.println(" in if VariableDeclarationFragment ");
			//	((VariableDeclarationFragment)oldExp.getParent()).getInitializer().delete();
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);
				NullLiteral nullLiteral = (NullLiteral) ast.createInstance(NullLiteral.class);
				
				
				//  IPath pathcu = cu.getJavaElement().getPath();

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.replace(((VariableDeclarationFragment)oldExp.getParent()).getInitializer(),nullLiteral, null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);
				
					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
	
			}
			
		} else if(oldExp.getParent() instanceof SingleVariableDeclaration){
			System.out.println(" in SingleVariableDeclaration");
			
//			System.out.println("			yay deidara SingleVariableDeclaration "+oldExp + " >> class "+oldExp.getClass());
//			System.out.println("			yay deidara parent SingleVariableDeclaration "+oldExp.getParent() + " >> class "+oldExp.getParent().getClass());
			
			if(((SingleVariableDeclaration)oldExp.getParent()).getInitializer().equals(oldExp)){
				System.out.println(" in SingleVariableDeclaration getInitializer");
				
				//((SingleVariableDeclaration)oldExp.getParent()).getInitializer().delete();
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				//  IPath pathcu = cu.getJavaElement().getPath();

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.remove(((SingleVariableDeclaration)oldExp.getParent()).getInitializer(), null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);
				
					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		} 
	}
	
	private static void replaceExpressionInParentNodeStatement(CompilationUnit cu,ASTNode oldExp){
		//TODObelow if the expression is mandatory , then delete the statement, otherwise delete the expression (or set it to null)
		
		
		if(oldExp.getParent() instanceof AssertStatement){
	
			//if(((AssertStatement)oldExp.getParent()).get)
			
			if(((AssertStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
			
			//	((AssertStatement)oldExp.getParent()).delete();
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.remove(((AssertStatement)oldExp.getParent()), null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);
				
					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			} else if(((AssertStatement)oldExp.getParent()).getMessage().equals(oldExp)){
			
				//((AssertStatement)oldExp.getParent()).getMessage().delete();//or set it to nul?? setMessage(null);
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.remove(((AssertStatement)oldExp.getParent()).getMessage(), null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);
				
					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		} else if(oldExp.getParent() instanceof Block){
		
			
		} else if(oldExp.getParent() instanceof BreakStatement){
			
			
		} else if(oldExp.getParent() instanceof ConstructorInvocation){
			
			if(((ConstructorInvocation)oldExp.getParent()).arguments().contains(oldExp)){
				
				//((ConstructorInvocation)oldExp.getParent()).arguments().remove(oldExp);
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.remove(oldExp, null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);
				
					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 

		} else if(oldExp.getParent() instanceof ContinueStatement){
			
		} else if(oldExp.getParent() instanceof DoStatement){
			
			if(((DoStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				//((DoStatement)oldExp.getParent()).delete();
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.remove(((DoStatement)oldExp.getParent()), null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);
				
					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		} else if(oldExp.getParent() instanceof EmptyStatement){
			
		} else if(oldExp.getParent() instanceof EnhancedForStatement){
			
			if(((EnhancedForStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				//((EnhancedForStatement)oldExp.getParent()).delete();	
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				//  IPath pathcu = cu.getJavaElement().getPath();

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.remove(((EnhancedForStatement)oldExp.getParent()), null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);

					SaveModification.SaveModif(cu, edits);
	
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
//			else if(((EnhancedForStatement)oldExp.getParent()).getBody().equals(oldExp)){
//				
//			}
			
		} else if(oldExp.getParent() instanceof ExpressionStatement){
	
			
			if(((ExpressionStatement)oldExp.getParent()).getExpression().equals(oldExp)){
		
				
			//((ExpressionStatement)oldExp.getParent()).delete();	
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				//  IPath pathcu = cu.getJavaElement().getPath();

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.remove(((ExpressionStatement)oldExp.getParent()), null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);

					SaveModification.SaveModif(cu, edits);
	
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 

		} 
		else if(oldExp.getParent() instanceof ForStatement){
			
			if(((ForStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
			//	((ForStatement)oldExp.getParent()).getExpression().delete();//or set it to null	
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.remove(((ForStatement)oldExp.getParent()).getExpression(), null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);
				
					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else if(((ForStatement)oldExp.getParent()).initializers().contains(oldExp)){
				
				//((ForStatement)oldExp.getParent()).initializers().remove(oldExp);
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.remove(oldExp, null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);
				
					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else if(((ForStatement)oldExp.getParent()).updaters().contains(oldExp)){
				
				//((ForStatement)oldExp.getParent()).updaters().remove(oldExp);
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.remove(oldExp, null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);
				
					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
			
		} else if(oldExp.getParent() instanceof IfStatement){
			System.out.println(" in if state ");
			if(ASTManager.isLiteral(oldExp))
			{
				System.out.println(" in if state LITERAL ");
			}
			else
			if(((IfStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				//((IfStatement)oldExp.getParent()).delete();	
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.remove(((IfStatement)oldExp.getParent()), null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);
				
					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} else if(oldExp.getParent() instanceof LabeledStatement){
			
		} else if(oldExp.getParent() instanceof ReturnStatement){
			
			if(((ReturnStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
			//	((ReturnStatement)oldExp.getParent()).getExpression().delete();	
				//((IfStatement)oldExp.getParent()).delete();	
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.remove(((ReturnStatement)oldExp.getParent()).getExpression(), null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);
				
					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

		} else if(oldExp.getParent() instanceof SuperConstructorInvocation){
			
			if(((SuperConstructorInvocation)oldExp.getParent()).getExpression().equals(oldExp)){
				
				//((SuperConstructorInvocation)oldExp.getParent()).getExpression().delete();
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.remove(((SuperConstructorInvocation)oldExp.getParent()).getExpression(), null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);
				
					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
			} else if(((SuperConstructorInvocation)oldExp.getParent()).arguments().contains(oldExp)){
				
				//((SuperConstructorInvocation)oldExp.getParent()).arguments().remove(oldExp);	
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.remove(oldExp, null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);
				
					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} else if(oldExp.getParent() instanceof SwitchCase){
			
			if(((SwitchCase)oldExp.getParent()).getExpression().equals(oldExp)){
				
			//	((SwitchCase)oldExp.getParent()).getExpression().delete();;		
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.remove(((SwitchCase)oldExp.getParent()).getExpression(), null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);
				
					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		} else if(oldExp.getParent() instanceof SwitchStatement){
			
			if(((SwitchStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				//((SwitchStatement)oldExp.getParent()).delete();	
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.remove(((SwitchStatement)oldExp.getParent()), null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);
				
					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}//TODO check if the switch statments are treated along with the switch cases, but it should be treated as they are statements
			
		} else if(oldExp.getParent() instanceof SynchronizedStatement){
			
			if(((SynchronizedStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				//((SynchronizedStatement)oldExp.getParent()).delete();	
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.remove(((SynchronizedStatement)oldExp.getParent()), null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);
				
					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		} else if(oldExp.getParent() instanceof ThrowStatement){
			
			if(((ThrowStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
				//((ThrowStatement)oldExp.getParent()).delete();	
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.remove(((ThrowStatement)oldExp.getParent()), null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);
				
					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		} else if(oldExp.getParent() instanceof TryStatement){
			//TODO check the resources and catch clauses, althought as they are based on paremeters and blocks, stetemenst, they should be treated too
			
		} else if(oldExp.getParent() instanceof TypeDeclarationStatement){
			//this type wraps TypeDeclaration (class declaration) and EnumDeclaration (ennum declaration)
			
		} else if(oldExp.getParent() instanceof VariableDeclarationStatement){
			//this si treated in VariableDeclarationFragment
			
		} else if(oldExp.getParent() instanceof WhileStatement){
			
			if(((WhileStatement)oldExp.getParent()).getExpression().equals(oldExp)){
				
			//	((WhileStatement)oldExp.getParent()).delete();	
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.remove(((WhileStatement)oldExp.getParent()), null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);
				
					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private static void replaceExpressionInParentNodeExpression(CompilationUnit cu,ASTNode oldExp){//below if the used element is in a mandatoru=y expression then delete the whole parent node 
		
		//ASTNode ast = ASTNode.copySubtree(oldExp.getAST(), newExp);
		
		if(oldExp.getParent() instanceof Annotation){
			//TODO the diffrent anotations are not treated, especially the SingleMemberAnnotation wich has an expression 
			
		} else if(oldExp.getParent() instanceof ArrayAccess){
			
			if(((ArrayAccess)oldExp.getParent()).getIndex().equals(oldExp) || ((ArrayAccess)oldExp.getParent()).getArray().equals(oldExp)){
				
				
				try{
				
					//((ArrayAccess)oldExp.getParent()).delete();//delete in both cases where like x.y.deletdprop[0] or z[x.y.deletdprop]
					AST ast = cu.getAST();
					ASTRewrite rewriter1 = ASTRewrite.create(ast);

					Document document=null;
					ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
					try {
						document = new Document(iCompilUnit.getSource());
						rewriter1.remove(((ArrayAccess)oldExp.getParent()), null);  

						TextEdit edits = rewriter1.rewriteAST(document, null);
					
						SaveModification.SaveModif(cu, edits);
					}
					catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				} catch (IllegalArgumentException e){
					replaceExpressionInParentNode(cu,oldExp.getParent()); //it should be treated by ExpressionStatement
				}
			}
			//TODO check if we must not rather delete the statment containing it

		} else if(oldExp.getParent() instanceof ArrayCreation){
			
			if(((ArrayCreation)oldExp.getParent()).dimensions().contains(oldExp)){
				
				//((ArrayCreation)oldExp.getParent()).dimensions().remove(oldExp);	
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.remove(oldExp, null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);
				
					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} else if(oldExp.getParent() instanceof ArrayInitializer){
			
			if(((ArrayInitializer)oldExp.getParent()).expressions().contains(oldExp)){
				
				//((ArrayInitializer)oldExp.getParent()).expressions().remove(oldExp);	
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.remove(oldExp, null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);
				
					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		} else if(oldExp.getParent() instanceof Assignment){
			System.out.println(" in Assignment");
			if(((Assignment)oldExp.getParent()).getLeftHandSide().equals(oldExp) ||
					((Assignment)oldExp.getParent()).getRightHandSide().equals(oldExp)){
				System.out.println(" in Assignment 2");
				if(((Assignment)oldExp.getParent()).getRightHandSide().equals(oldExp))
				{
					try{
						//((Assignment)oldExp.getParent()).delete();
						AST ast = cu.getAST();
						ASTRewrite rewriter1 = ASTRewrite.create(ast);
						NullLiteral nullLiteral = (NullLiteral) ast.createInstance(NullLiteral.class);
						
						Document document=null;
						ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
						try {
							document = new Document(iCompilUnit.getSource());
							rewriter1.replace(((Assignment)oldExp.getParent()).getRightHandSide(),nullLiteral, null);  

							TextEdit edits = rewriter1.rewriteAST(document, null);
						
							SaveModification.SaveModif(cu, edits);
						}
						catch (JavaModelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (IllegalArgumentException e){
						replaceExpressionInParentNode(cu,oldExp.getParent()); //it should be treated by ExpressionStatement
					}
				}
				else {
				try{
					//((Assignment)oldExp.getParent()).delete();
					AST ast = cu.getAST();
					ASTRewrite rewriter1 = ASTRewrite.create(ast);

					Document document=null;
					ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
					try {
						document = new Document(iCompilUnit.getSource());
		 				rewriter1.remove(((Assignment)oldExp.getParent()), null);  

						TextEdit edits = rewriter1.rewriteAST(document, null);
					
						SaveModification.SaveModif(cu, edits);
					}
					catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (IllegalArgumentException e){
					replaceExpressionInParentNode(cu,oldExp.getParent()); //it should be treated by ExpressionStatement
				}
				}
			}
			else 
			{
				try{
					//((Assignment)oldExp.getParent()).delete();
					AST ast = cu.getAST();
					ASTRewrite rewriter1 = ASTRewrite.create(ast);
					NullLiteral nullLiteral = (NullLiteral) ast.createInstance(NullLiteral.class);
					
					Document document=null;
					ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
					try {
						document = new Document(iCompilUnit.getSource());
						rewriter1.replace(((Assignment)oldExp.getParent()),nullLiteral, null);  

						TextEdit edits = rewriter1.rewriteAST(document, null);
					
						SaveModification.SaveModif(cu, edits);
					}
					catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (IllegalArgumentException e){
					replaceExpressionInParentNode(cu,oldExp.getParent()); //it should be treated by ExpressionStatement
				}	
			}
			
		} else if(oldExp.getParent() instanceof BooleanLiteral){
			
		} else if(oldExp.getParent() instanceof CastExpression){
			System.out.println(" in CastExpression");
			if(((CastExpression)oldExp.getParent()).getExpression().equals(oldExp)){
				
				try{
				//	((CastExpression)oldExp.getParent()).delete();//delete in both cases where like x.y.deletdprop[0] or z[x.y.deletdprop]
				
					AST ast = cu.getAST();
					ASTRewrite rewriter1 = ASTRewrite.create(ast);

					Document document=null;
					ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
					try {
						document = new Document(iCompilUnit.getSource());
						rewriter1.remove(((CastExpression)oldExp.getParent()), null);  

						TextEdit edits = rewriter1.rewriteAST(document, null);
					
						SaveModification.SaveModif(cu, edits);
					}
					catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				} catch (IllegalArgumentException e){
					replaceExpressionInParentNode(cu,oldExp.getParent()); //it should be treated by ExpressionStatement
				}
			}
			
		} else if(oldExp.getParent() instanceof CharacterLiteral){
			
		} else if(oldExp.getParent() instanceof ClassInstanceCreation){
			System.out.println(" in ClassInstanceCreation");
			if(((ClassInstanceCreation)oldExp.getParent()).arguments().contains(oldExp)){
				
			//	((ClassInstanceCreation)oldExp.getParent()).arguments().remove(oldExp);	
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.remove(oldExp, null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);
				
					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		} else if(oldExp.getParent() instanceof ConditionalExpression){
			System.out.println(" in ConditionalExpression");
			if(((ConditionalExpression)oldExp.getParent()).getExpression().equals(oldExp) ||
					((ConditionalExpression)oldExp.getParent()).getThenExpression().equals(oldExp) ||
					((ConditionalExpression)oldExp.getParent()).getElseExpression().equals(oldExp)){
					
				try{
					//((ConditionalExpression)oldExp.getParent()).delete();//delete in both cases where like x.y.deletdprop[0] or z[x.y.deletdprop]
			
					AST ast = cu.getAST();
					ASTRewrite rewriter1 = ASTRewrite.create(ast);

					Document document=null;
					ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
					try {
						document = new Document(iCompilUnit.getSource());
						rewriter1.remove(((ConditionalExpression)oldExp.getParent()), null);  

						TextEdit edits = rewriter1.rewriteAST(document, null);
					
						SaveModification.SaveModif(cu, edits);
					}
					catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (IllegalArgumentException e){
					replaceExpressionInParentNode(cu,oldExp.getParent()); //it should be treated by ExpressionStatement
				}
			}
			
		} else if(oldExp.getParent() instanceof FieldAccess){
			System.out.println(" in FieldAccess");
			if(((FieldAccess)oldExp.getParent()).getExpression().equals(oldExp)){
				
				try{
					//((FieldAccess)oldExp.getParent()).delete();//delete in both cases where like x.y.deletdprop[0] or z[x.y.deletdprop]
				
					AST ast = cu.getAST();
					ASTRewrite rewriter1 = ASTRewrite.create(ast);

					Document document=null;
					ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
					try {
						document = new Document(iCompilUnit.getSource());
						rewriter1.remove(((FieldAccess)oldExp.getParent()), null);  

						TextEdit edits = rewriter1.rewriteAST(document, null);
					
						SaveModification.SaveModif(cu, edits);
					}
					catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (IllegalArgumentException e){
					replaceExpressionInParentNode(cu,oldExp.getParent()); //it should be treated by ExpressionStatement
				}
			}
			
		} else if(oldExp.getParent() instanceof InfixExpression){
			System.out.println(" in InfixExpression");
			if(((InfixExpression)oldExp.getParent()).getLeftOperand().equals(oldExp)){
				System.out.println(" in InfixExpression1");
				if(((InfixExpression)oldExp.getParent()).extendedOperands().size() == 0){
					System.out.println(" in InfixExpression2");
					//ResolutionsUtils.replaceDirectExpressionInParentNode(cu,oldExp.getParent(), ((InfixExpression)oldExp.getParent()).getRightOperand());
					
					try{
						
						AST ast = cu.getAST();
						ASTRewrite rewriter1 = ASTRewrite.create(ast);

						Document document=null;
						ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
						try {
							document = new Document(iCompilUnit.getSource());
							rewriter1.remove(ASTManager.findIfWhileForStatement(oldExp), null);  

							TextEdit edits = rewriter1.rewriteAST(document, null);
						
							SaveModification.SaveModif(cu, edits);
						}
						catch (JavaModelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (IllegalArgumentException e){
						replaceExpressionInParentNode(cu,oldExp.getParent()); //it should be treated by ExpressionStatement
					}
				} else {
					System.out.println(" in InfixExpression3");
					InfixExpression infixExpression = (InfixExpression) oldExp.getAST().createInstance(InfixExpression.class);
					infixExpression.setLeftOperand((Expression) ASTNode.copySubtree(oldExp.getAST(), ((InfixExpression)oldExp.getParent()).getRightOperand()));
					infixExpression.setRightOperand((Expression) ASTNode.copySubtree(oldExp.getAST(), (ASTNode) ((InfixExpression)oldExp.getParent()).extendedOperands().get(0)));
					((InfixExpression)oldExp.getParent()).extendedOperands().remove(0);
					Iterator it = ((InfixExpression)oldExp.getParent()).extendedOperands().iterator();
					while(it.hasNext()){
						System.out.println(" in InfixExpression4");
						infixExpression.extendedOperands().add(ASTNode.copySubtree(oldExp.getAST(), (ASTNode) it.next()));
					}
					infixExpression.setOperator(((InfixExpression)oldExp.getParent()).getOperator());
					ResolutionsUtils.replaceDirectExpressionInParentNode(cu,oldExp.getParent(), infixExpression);
				}
				
				
			} else if(((InfixExpression)oldExp.getParent()).getRightOperand().equals(oldExp)){
				System.out.println(" in InfixExpression5");
				if(((InfixExpression)oldExp.getParent()).extendedOperands().size() == 0){
					System.out.println(" in InfixExpression6");
					ResolutionsUtils.replaceDirectExpressionInParentNode(cu,oldExp.getParent(), ((InfixExpression)oldExp.getParent()).getLeftOperand());
					
				} else {
					System.out.println(" in InfixExpression7");
					
					InfixExpression infixExpression = (InfixExpression) oldExp.getAST().createInstance(InfixExpression.class);
					infixExpression.setLeftOperand((Expression) ASTNode.copySubtree(oldExp.getAST(), ((InfixExpression)oldExp.getParent()).getLeftOperand()));
					infixExpression.setRightOperand((Expression) ASTNode.copySubtree(oldExp.getAST(), (ASTNode) ((InfixExpression)oldExp.getParent()).extendedOperands().get(0)));
					((InfixExpression)oldExp.getParent()).extendedOperands().remove(0);
					Iterator it = ((InfixExpression)oldExp.getParent()).extendedOperands().iterator();
					while(it.hasNext()){
						infixExpression.extendedOperands().add(ASTNode.copySubtree(oldExp.getAST(), (ASTNode) it.next()));
					}
					infixExpression.setOperator(((InfixExpression)oldExp.getParent()).getOperator());
					ResolutionsUtils.replaceDirectExpressionInParentNode(cu,oldExp.getParent(), infixExpression);
				}	
				
			} else if(((InfixExpression)oldExp.getParent()).extendedOperands().contains(oldExp)){
				System.out.println(" in InfixExpression8   "+oldExp);
			//	((InfixExpression)oldExp.getParent()).extendedOperands().remove(oldExp);	
				try{
					//((InstanceofExpression)oldExp.getParent()).delete();//delete in both cases where like x.y.deletdprop[0] or z[x.y.deletdprop]
					AST ast = cu.getAST();
					ASTRewrite rewriter1 = ASTRewrite.create(ast);

					Document document=null;
					ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
					try {
						document = new Document(iCompilUnit.getSource());
						rewriter1.remove(oldExp, null);  

						TextEdit edits = rewriter1.rewriteAST(document, null);
					
						SaveModification.SaveModif(cu, edits);
					}
					catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (IllegalArgumentException e){
					replaceExpressionInParentNode(cu,oldExp.getParent()); //it should be treated by ExpressionStatement
				}
			}
			
		} 
		else if(oldExp.getParent() instanceof InstanceofExpression){
			System.out.println(" in InstanceofExpression");
			if(((InstanceofExpression)oldExp.getParent()).getLeftOperand().equals(oldExp)){
				
				try{
					//((InstanceofExpression)oldExp.getParent()).delete();//delete in both cases where like x.y.deletdprop[0] or z[x.y.deletdprop]
					AST ast = cu.getAST();
					ASTRewrite rewriter1 = ASTRewrite.create(ast);

					Document document=null;
					ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
					try {
						document = new Document(iCompilUnit.getSource());
						rewriter1.remove(((InstanceofExpression)oldExp.getParent()), null);  

						TextEdit edits = rewriter1.rewriteAST(document, null);
					
						SaveModification.SaveModif(cu, edits);
					}
					catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (IllegalArgumentException e){
					replaceExpressionInParentNode(cu,oldExp.getParent()); //it should be treated by ExpressionStatement
				}
			}
			
		} else if(oldExp.getParent() instanceof LambdaExpression){
			//TODO not treated here, but should already be treated as it has an expression and praamreters a s var declarations
			
		} else if(oldExp.getParent() instanceof MethodInvocation){
			System.out.println(" in MethodInvocation");
				if(((MethodInvocation)oldExp.getParent()).arguments().contains(oldExp)){
				//MethodDeclaration md =(MethodDeclaration) ASTManager.findMethodDeclaration(oldExp);
					//MethodInvocation mi = (MethodInvocation) ASTManager.findMethodInvocation(oldExp.getParent().getParent());
					System.out.println("the first level  oldExp is "+oldExp  + "its type is "+oldExp.getClass() + " its parent is "+oldExp.getParent()+ " the type of the parent is   "+(oldExp.getParent()).getClass());
				int pos=	((MethodInvocation)oldExp.getParent()).arguments().indexOf(oldExp);
			ITypeBinding tb=((MethodInvocation)oldExp.getParent()).resolveMethodBinding().getParameterTypes()[pos];
			System.out.println(" THE FOUND pos  "+pos + "   Arg Type is   "	+tb.getName());
			//((MethodInvocation)oldExp.getParent()).arguments().remove(oldExp);
					AST ast = cu.getAST();
					ASTRewrite rewriter1 = ASTRewrite.create(ast);
					NullLiteral nullLiteral = (NullLiteral) ast.createInstance(NullLiteral.class);
					
					
					Document document=null;
					ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
					try {
						document = new Document(iCompilUnit.getSource());
						if(tb.toString().equals("boolean"))
						{
							BooleanLiteral bb = (BooleanLiteral) ast.createInstance(BooleanLiteral.class);
							bb.setBooleanValue(false);
							rewriter1.replace(oldExp,bb, null);  
							
						}
						else if(tb.getName().equals("String") || tb.getName().equals("Number"))
						{
							StringLiteral st = (StringLiteral) ast.createInstance(StringLiteral.class);
							st.setLiteralValue("");
							rewriter1.replace(oldExp,st, null);
							
						}
						else{
						rewriter1.replace(oldExp,nullLiteral, null);  
						}
						TextEdit edits = rewriter1.rewriteAST(document, null);
					
						SaveModification.SaveModif(cu, edits);
					}
					catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} 
//				else if(((MethodInvocation)oldExp.getParent()).getExpression().equals(oldExp)){// here thsi case is treated by another call to these methods here
//					 
//					//((MethodInvocation)oldExp.getParent()).setExpression((Expression) ast);	
//				}
				
		} else if(oldExp.getParent() instanceof MethodReference){
			System.out.println(" in MethodReference");
			if(oldExp.getParent() instanceof ExpressionMethodReference && ((ExpressionMethodReference)oldExp.getParent()).getExpression().equals(oldExp)){
				 
				try{
					//((ExpressionMethodReference)oldExp.getParent()).delete();//delete in both cases where like x.y.deletdprop[0] or z[x.y.deletdprop]
					AST ast = cu.getAST();
					ASTRewrite rewriter1 = ASTRewrite.create(ast);

					Document document=null;
					ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
					try {
						document = new Document(iCompilUnit.getSource());
						rewriter1.remove(((ExpressionMethodReference)oldExp.getParent()), null);  

						TextEdit edits = rewriter1.rewriteAST(document, null);
					
						SaveModification.SaveModif(cu, edits);
					}
					catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (IllegalArgumentException e){
					replaceExpressionInParentNode(cu,oldExp.getParent()); //it should be treated by ExpressionStatement
				}
			}
			
		} else if(oldExp.getParent() instanceof Name){
			
		} else if(oldExp.getParent() instanceof NullLiteral){
			
		} else if(oldExp.getParent() instanceof NumberLiteral){
			
		} else if(oldExp.getParent() instanceof ParenthesizedExpression){

			if(((ParenthesizedExpression)oldExp.getParent()).getExpression().equals(oldExp)){
				System.out.println(" in ParenthesizedExpression");
				try{
					//((ParenthesizedExpression)oldExp.getParent()).delete();//delete in both cases where like x.y.deletdprop[0] or z[x.y.deletdprop]
			
					AST ast = cu.getAST();
					ASTRewrite rewriter1 = ASTRewrite.create(ast);

					Document document=null;
					ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
					try {
						document = new Document(iCompilUnit.getSource());
						rewriter1.remove(((ParenthesizedExpression)oldExp.getParent()), null);  

						TextEdit edits = rewriter1.rewriteAST(document, null);
					
						SaveModification.SaveModif(cu, edits);
					}
					catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (IllegalArgumentException e){
					replaceExpressionInParentNode(cu,oldExp.getParent()); //it should be treated by ExpressionStatement
				}
			}
			 
		} else if(oldExp.getParent() instanceof PostfixExpression){
			System.out.println(" in PostfixExpression");
			if(((PostfixExpression)oldExp.getParent()).getOperand().equals(oldExp)){
				
				try{
					//((PostfixExpression)oldExp.getParent()).delete();//delete in both cases where like x.y.deletdprop[0] or z[x.y.deletdprop]
			
					AST ast = cu.getAST();
					ASTRewrite rewriter1 = ASTRewrite.create(ast);

					Document document=null;
					ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
					try {
						document = new Document(iCompilUnit.getSource());
						rewriter1.remove(((PostfixExpression)oldExp.getParent()), null);  

						TextEdit edits = rewriter1.rewriteAST(document, null);
					
						SaveModification.SaveModif(cu, edits);
					}
					catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (IllegalArgumentException e){
					replaceExpressionInParentNode(cu,oldExp.getParent()); //it should be treated by ExpressionStatement
				}
			}
			
		} else if(oldExp.getParent() instanceof PrefixExpression){
			System.out.println(" in PrefixExpression");
			if(((PrefixExpression)oldExp.getParent()).getOperand().equals(oldExp)){
				
				try{
					//((PrefixExpression)oldExp.getParent()).delete();//delete in both cases where like x.y.deletdprop[0] or z[x.y.deletdprop]
				
					AST ast = cu.getAST();
					ASTRewrite rewriter1 = ASTRewrite.create(ast);

					Document document=null;
					ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
					try {
						document = new Document(iCompilUnit.getSource());
						rewriter1.remove(((PrefixExpression)oldExp.getParent()), null);  

						TextEdit edits = rewriter1.rewriteAST(document, null);
					
						SaveModification.SaveModif(cu, edits);
					}
					catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (IllegalArgumentException e){
					replaceExpressionInParentNode(cu,oldExp.getParent()); //it should be treated by ExpressionStatement
				}
			}
			
		} else if(oldExp.getParent() instanceof StringLiteral){
			
		} else if(oldExp.getParent() instanceof SuperFieldAccess){
			//TODO here the class and prop as in names, not expression
		} else if(oldExp.getParent() instanceof SuperMethodInvocation){
			System.out.println(" in SuperMethodInvocation");
			if(((SuperMethodInvocation)oldExp.getParent()).arguments().contains(oldExp)){
				
				//((SuperMethodInvocation)oldExp.getParent()).arguments().remove(oldExp);
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					rewriter1.remove(oldExp, null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);
				
					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			} else {
				//TODO here the case wehre Class.super.deletedProp(), => delete the whole expressio ??
			}
			
		} else if(oldExp.getParent() instanceof ThisExpression){
			
		} else if(oldExp.getParent() instanceof TypeLiteral){
			
		} else if(oldExp.getParent() instanceof VariableDeclarationExpression){
			
		}
	}
}
