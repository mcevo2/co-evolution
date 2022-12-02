package Utilities;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;

import javax.swing.SwingConstants;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.corext.dom.ASTNodeFactory;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import fr.lip6.meta.ComplexChangeDetection.Change;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.DeleteClass;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.DeleteProperty;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.RenameClass;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.RenameProperty;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.SetProperty;
import fr.lip6.meta.ComplexChangeDetection.ComplexChanges.MoveProperty;
import fr.lip6.meta.ComplexChangeDetection.ComplexChanges.PushProperty;

public class Resolutions {
	public static void renamingClassResolution( CompilationUnit cu,Usage usage,UsagePattern pattern, String newName)
	{

		ASTNode an = usage.getNode();
		switch(pattern)
		{
		case TypeUseRename:


			ASTModificationManager.RenameSimpleName(cu, an, newName);
			break;
		case createObjectRename:

			ASTModificationManager.RenameSimpleName(cu, an, "create"+newName);
			break;
		case getObjectRename:

			ASTModificationManager.RenameSimpleName(cu, an, "get"+newName);
			break;
		case setObjectRename:

			ASTModificationManager.RenameSimpleName(cu, an, "set"+newName);
			break;
		case QualifiedTypeRename:
			List<ASTNode > childrennodes=ASTManager.getChildren(an);


			for (int i = 0; i < childrennodes.size(); i++) {

				ASTNode n= childrennodes.get(i);
				if( n instanceof SimpleName  )
				{
					System.out.println(" in qualifiedtype rename   ");
					ASTModificationManager.RenameSimpleName(cu, n, newName);
				}

			}
			break;

		default:
			break;

		}

	}
	public static void renamingPropertyResolution( CompilationUnit cu,Usage usage,UsagePattern pattern)
	{

		ASTNode an = usage.getNode();
		String newName = ((RenameProperty)usage.getChange()).getNewname();
		switch(pattern)
		{
		case PropertyRename:

			ASTModificationManager.RenameSimpleName(cu, an, newName);
			break;
		case getPropertyRename:

			ASTModificationManager.RenameSimpleName(cu, an, "get"+UsesManager.capitalizeFirstLetter(newName));
			break;
		case setPropertyRename:

			ASTModificationManager.RenameSimpleName(cu, an, "set"+UsesManager.capitalizeFirstLetter(newName));
			break;
		case isPropertyRename:

			ASTModificationManager.RenameSimpleName(cu, an, "is"+UsesManager.capitalizeFirstLetter(newName));
			break;
		default:
			break;

		}

	}

	public static void renamingImport(CompilationUnit cu,Change change,Usage usage,IMarker amarker, String newName)
	{ 
		//Get the error node from the marker, its type is QualifiedName
		ASTNode errornode = ASTManager.getErrorNode(cu, amarker);
		// Get children of  QualifiedName error
		List<ASTNode > childrennodes=ASTManager.getChildren(errornode);
		for (int i = 0; i < childrennodes.size(); i++) {
			ASTNode n= childrennodes.get(i);
			if( n instanceof SimpleName)
			{
				// Look for SimpleNames in the QualifiedName and test if it is impacted by the rename change
				if(((SimpleName)n).getIdentifier().equals(((RenameClass)change).getName()))
				{
					// Rename SimpleName concerned by the error
					ASTModificationManager.RenameSimpleName(cu, n, newName);
				}
			}
		}

	}
	public static void deleteImport(CompilationUnit cu, Change change,ASTNode node)
	{


		AST ast = cu.getAST();
		ASTRewrite rewriter1 = ASTRewrite.create(ast);

		Document document=null;
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		List<ASTNode > childrennodes=ASTManager.getChildren(node);
		try {
			document = new Document(iCompilUnit.getSource());
			for (int i = 0; i < childrennodes.size(); i++) {
				ASTNode n= childrennodes.get(i);
				if( n instanceof SimpleName)
				{
					if(((SimpleName)n).getIdentifier().equals(((DeleteClass)change).getName())) {

						rewriter1.remove(node.getParent(), null);  
						System.out.println(" parent of deleted  "+node.getParent());
						// edits.apply(document);
						// ListRewrite lrw1 = rewriter1.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY);
						//lrw1.remove(node, null);

						TextEdit edits = rewriter1.rewriteAST(document, null);

						SaveModification.SaveModif(cu, edits);
					}
				}


			}
		}
		catch (JavaModelException | MalformedTreeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	public static void DeleteImport3(CompilationUnit cu,ASTNode node)
	{


		AST ast = cu.getAST();
		ASTRewrite rewriter1 = ASTRewrite.create(ast);

		Document document=null;
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		try {
			document = new Document(iCompilUnit.getSource());
		} catch (JavaModelException e) {

			e.printStackTrace();
		}
		rewriter1.remove(node.getParent(), null);  
		TextEdit edits = rewriter1.rewriteAST(document, null);

		SaveModification.SaveModif(cu, edits);






	}

	public static void deleteVariableDeclaration(CompilationUnit cu,ASTNode foundDeclaration,Usage usage) {

		//Prepare variables to edit AST
		AST ast = cu.getAST();
		ASTRewrite rewriter1 = ASTRewrite.create(ast);
		Document document=null;
		TextEdit edits =null; 
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		Iterator it = null;
		NullLiteral nullLiteral = (NullLiteral) ast.createInstance(NullLiteral.class);

		//now we retrieve the declared variable fragments
		// case of field declaration
		if(foundDeclaration instanceof FieldDeclaration){
			it = ((FieldDeclaration) foundDeclaration).fragments().iterator();
			// case of variable declaration statement
		} else if(foundDeclaration instanceof VariableDeclarationStatement){
			it = ((VariableDeclarationStatement) foundDeclaration).fragments().iterator();
			// case of parameter declaration
		} else if(foundDeclaration instanceof SingleVariableDeclaration){
			ArrayList<SingleVariableDeclaration> list = new ArrayList<SingleVariableDeclaration>();
			list.add((SingleVariableDeclaration) foundDeclaration);
			it = list.iterator();
		} 

		while(it != null && it.hasNext()){
			// Browse the list of fragments of the variable declaration
			Object obj = it.next();
			ArrayList<ASTNode> list_of_usage = new ArrayList<ASTNode>();
			// Get the usages of the variable declaration using its bindings
			if(obj instanceof VariableDeclarationFragment){
				if(JavaVisitor.getManageBindings().getBindingsNodes().containsKey(((VariableDeclarationFragment) obj).resolveBinding())){
					list_of_usage = JavaVisitor.getManageBindings().getBindingsNodes().get(((VariableDeclarationFragment) obj).resolveBinding());
				}

			} else if (obj instanceof SingleVariableDeclaration){

				if(JavaVisitor.getManageBindings().getBindingsNodes().containsKey(((SingleVariableDeclaration) obj).resolveBinding())){
					list_of_usage = JavaVisitor.getManageBindings().getBindingsNodes().get(((SingleVariableDeclaration) obj).resolveBinding());
				}

			}

			try {
				document = new Document(iCompilUnit.getSource());

			} catch (JavaModelException e) {

				e.printStackTrace();
			}
			//Browse the list of usages
			boolean leftoperand=false;
			for(ASTNode astNode : list_of_usage){
				ASTNode foundStatement = ASTManager.findStatement(astNode);
				/*	if( foundStatement instanceof VariableDeclarationStatement) {
					VariableDeclarationStatement vds = (VariableDeclarationStatement)foundStatement;
					String change = ((DeleteClass)usage.getChange()).getName();
			if(	((SimpleName)	((SimpleType)vds.getType()).getName()).equals(change))
			{
				leftoperand=true;
			}


				}*/

				if(foundStatement != null && foundStatement instanceof ExpressionStatement ){
					try {
						System.out.println(" here is a VD usage "+ foundStatement);
						document = new Document(iCompilUnit.getSource());
						// Delete the usage statement from the AST
						rewriter1.remove(foundStatement, null);  


					} catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(ASTManager.findFieldDeclaration(astNode)!=null)
				{
					rewriter1.replace(astNode, nullLiteral, null);

				}
				if( foundStatement instanceof VariableDeclarationStatement)
				{
					System.out.println(" Good boy");
					rewriter1.replace(astNode, nullLiteral, null);

				}
				if(ASTManager.findInfixExpression(astNode)!=null)
				{
					ASTNode operand =ASTManager.findRightOrLeftOperandExpression(astNode);
					System.out.println(" Find an OPERAND "+ operand);
					rewriter1.remove(operand, null);	
				}
				/*	if(! leftoperand) {
					ASTNode n= ASTManager.findVariableDeclarationFragment(astNode);
					rewriter1.replace(n, nullLiteral, null);


				}*/


			}	
			// Finally, delete the variable declaration
			rewriter1.remove(foundDeclaration, null);
			// Gather all the modifications of the AST
			edits = rewriter1.rewriteAST(document, null);
		}
		if( edits!=null)
		{ 
			// Send modifications to be saved
			SaveModification.SaveModif(cu, edits);
		}
	}

	public static void deleteParameter(CompilationUnit compilUnit,ASTNode foundParameter,Usage usage)
	{

		AST ast = compilUnit.getAST();
		ASTRewrite rewriter1 = ASTRewrite.create(ast);

		//  IPath pathcu = cu.getJavaElement().getPath();

		Document document=null;
		TextEdit edits =null;
		ICompilationUnit iCompilUnit=(ICompilationUnit) compilUnit.getJavaElement();

		Resolutions.deleteVariableDeclaration(compilUnit,foundParameter,usage);

		//		((SingleVariableDeclaration)foundParameter).getName().get 



	}

	public static void deleteVariableDeclaration(CompilationUnit cu, Change change,ASTNode node)
	{
		ASTNode variableDeclarationToDelete=null;
		AST ast = cu.getAST();
		ASTRewrite rewriter1 = ASTRewrite.create(ast);

		//  IPath pathcu = cu.getJavaElement().getPath();

		Document document=null;
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		System.out.println(" Node to be deleted  "+node);
		if(((SimpleName)node).getIdentifier().equals(((DeleteClass)change).getName())) {

			try {
				document = new Document(iCompilUnit.getSource());

				variableDeclarationToDelete=ASTManager.findFieldOrVariableDeclarations(node);
				System.out.println(" You will delete  "+variableDeclarationToDelete);
				rewriter1.remove(variableDeclarationToDelete, null);  

				// edits.apply(document);
				// ListRewrite lrw1 = rewriter1.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY);
				//lrw1.remove(node, null);

				TextEdit edits = rewriter1.rewriteAST(document, null);

				SaveModification.SaveModif(cu, edits);
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}
	public static void deleteSuperClass(CompilationUnit cu,Change change, ASTNode node)
	{
		AST ast = cu.getAST();
		ASTRewrite rewriter1 = ASTRewrite.create(ast);

		//  IPath pathcu = cu.getJavaElement().getPath();

		Document document=null; 
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		if(((SimpleName)node).getIdentifier().equals(((DeleteClass)change).getName())) {
			System.out.println("  here in change= error");
			try {
				document = new Document(iCompilUnit.getSource());


				rewriter1.remove(node.getParent(), null);  

				// edits.apply(document);
				// ListRewrite lrw1 = rewriter1.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY);
				//lrw1.remove(node, null);

				TextEdit edits = rewriter1.rewriteAST(document, null);

				SaveModification.SaveModif(cu, edits);
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static void deleteCompextStatemnt(CompilationUnit cu,Change change, ASTNode node)
	{
		AST ast = cu.getAST();
		ASTRewrite rewriter1 = ASTRewrite.create(ast);

		//  IPath pathcu = cu.getJavaElement().getPath();

		Document document=null; 
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		ASTNode complexStatement=ASTManager.findIfWhileForStatement(node);
		if(((SimpleName)node).getIdentifier().contains(((DeleteClass)change).getName())) {

			try {
				document = new Document(iCompilUnit.getSource());


				System.out.println(" The complex statement you'll remove is  " +complexStatement);
				rewriter1.remove(complexStatement, null);  

				// edits.apply(document);
				// ListRewrite lrw1 = rewriter1.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY);
				//lrw1.remove(node, null);

				TextEdit edits = rewriter1.rewriteAST(document, null);
				System.out.println(" Edits contains  "+edits.getChildrenSize());
				SaveModification.SaveModif(cu, edits);
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void deleteInstanceClass(CompilationUnit cu, ASTNode foundInstanceCreation)
	{
		ASTNode nodeTemp = foundInstanceCreation;
		AST ast = cu.getAST();
		ASTRewrite rewriter1 = ASTRewrite.create(ast);

		//  IPath pathcu = cu.getJavaElement().getPath();

		Document document=null;
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();

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
			ASTNode foundStatement = ASTManager.findStatement(foundInstanceCreation);
			//here we delete the statements where the variable is used
			if(foundStatement != null && foundStatement instanceof ExpressionStatement){//TODO what about checking var decla ??
				//System.out.println("		*** found statement to delete >>> "+foundStatement);
				try {
					document = new Document(iCompilUnit.getSource());



				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				rewriter1.remove(foundStatement, null);  


				TextEdit edits = rewriter1.rewriteAST(document, null);
				if( edits!=null)
				{
					//System.out.println(" FOUND Instance in saving part");
					SaveModification.SaveModif(cu, edits);
				}
			}
		}
	}
	public static void deleteVariablAssignment(CompilationUnit cu, Change change,ASTNode node)
	{
		ASTNode variableAssignmentToDelete=null;
		AST ast = cu.getAST();
		ASTRewrite rewriter1 = ASTRewrite.create(ast);

		//  IPath pathcu = cu.getJavaElement().getPath();

		Document document=null;
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		System.out.println(" Node to be deleted from assignment "+node);
		if(((SimpleName)node).getIdentifier().equals(((DeleteClass)change).getName())) {

			try {
				document = new Document(iCompilUnit.getSource());

				variableAssignmentToDelete=ASTManager.findAssignment(node);
				System.out.println(" You will delete from assignment "+variableAssignmentToDelete);
				rewriter1.remove(ASTManager.findExpressionStatement(variableAssignmentToDelete), null);  

				// edits.apply(document);
				// ListRewrite lrw1 = rewriter1.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY);
				//lrw1.remove(node, null);

				TextEdit edits = rewriter1.rewriteAST(document, null);

				SaveModification.SaveModif(cu, edits);
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}
	public static void deleteLiteral(CompilationUnit cu, Change change,ASTNode node)
	{
		ASTNode st;
		String deleted="";
		String literal="";
		if(node instanceof SimpleName && ASTManager.isLiteral(node) )
		{
			if( change instanceof DeleteClass) {
				deleted=((DeleteClass)change).getName();
				literal=((SimpleName)node).getIdentifier();
				literal= literal.replaceAll("_", "");

				//st=ASTManager.findFieldOrVariableDeclarations(node);



				//Resolutions.deleteUsedVariables(cu,st);

				AST ast = cu.getAST();
				Document document=null;
				TextEdit edits=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				StringLiteral sl = (StringLiteral) ast.createInstance(StringLiteral.class);
				sl.setLiteralValue("");
				NullLiteral nullLiteral = (NullLiteral) ast.createInstance(NullLiteral.class);

				ASTRewrite rewriter1 = ASTRewrite.create(ast);
				try {

					document = new Document(iCompilUnit.getSource());

					//newInstance.arguments().add(sl);
					//rewriter1.remove(node.getParent(), null);  
					if(ASTManager.findVariableDeclarationFragment(node)!=null)
					{
						System.out.println(" in string literal");
						rewriter1.replace(node.getParent(), sl, null);
					}
					else
					{
						System.out.println(" in null literal");
						rewriter1.replace(node.getParent(), nullLiteral, null);
					}

					edits = rewriter1.rewriteAST(document, null);	
					SaveModification.SaveModif(cu, edits);

				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}






			}
			else if (change instanceof DeleteProperty)
			{
				deleted=((DeleteProperty)change).getClassName()+((DeleteProperty)change).getName();
				literal=((SimpleName)node).getIdentifier();
				literal= literal.replaceAll("_", "");
				if(literal.toLowerCase().equals(deleted.toLowerCase() )||literal.toLowerCase().equals(deleted.toLowerCase()+"diagnosticchainmap" ))
				{
					//st=ASTManager.findFieldOrVariableDeclarations(node);



					//Resolutions.deleteUsedVariables(cu,st);

					AST ast = cu.getAST();
					Document document=null;
					TextEdit edits=null;
					ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
					NullLiteral nullLiteral = (NullLiteral) ast.createInstance(NullLiteral.class);

					ASTRewrite rewriter1 = ASTRewrite.create(ast);
					try {

						document = new Document(iCompilUnit.getSource());

						//newInstance.arguments().add(sl);
						//rewriter1.remove(node.getParent(), null);  
						if(ASTManager.findVariableDeclarationFragment(node)!=null) {
							System.out.println("literal var ");
							rewriter1.replace(((VariableDeclarationFragment)ASTManager.findVariableDeclarationFragment(node)).getInitializer(),nullLiteral, null);  

						}
						else 
						{
							System.out.println("literal NOT var ");
							rewriter1.replace(node.getParent(), nullLiteral, null);

						}
						edits = rewriter1.rewriteAST(document, null);	
						SaveModification.SaveModif(cu, edits);

					} catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}




				}
			}




		}
		if(ASTManager.findReturnStatment(node)!=null)
		{
			ReturnStatement rs=(ReturnStatement) ASTManager.findReturnStatment(node);
			if(rs.getParent() instanceof SwitchStatement)
			{
				SwitchStatement ss =(SwitchStatement)rs.getParent();
				System.out.println("swich case  "+ node.toString()+"  "+ss.getExpression().resolveTypeBinding().getName());
				if(ss.getExpression().resolveTypeBinding().getName().equals("int"))
				{
					AST ast = cu.getAST();
					Document document=null;
					TextEdit edits=null;
					ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
					NumberLiteral number = (NumberLiteral) ast.createInstance(NumberLiteral.class);
					number.setToken("0");
					ASTRewrite rewriter1 = ASTRewrite.create(ast);
					try {

						document = new Document(iCompilUnit.getSource());

							rewriter1.replace(node.getParent(), number, null);

						
						edits = rewriter1.rewriteAST(document, null);	
						SaveModification.SaveModif(cu, edits);

					} catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}




				}
				
			}
			
		}
		if(ASTManager.findSwitchCase(node)!=null)
		{
			SwitchCase rs=(SwitchCase) ASTManager.findSwitchCase(node);
			if(rs.getParent() instanceof SwitchStatement)
			{
				
					AST ast = cu.getAST();
					Document document=null;
					TextEdit edits=null;
					ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
					
					ASTRewrite rewriter1 = ASTRewrite.create(ast);
					try {

						document = new Document(iCompilUnit.getSource());

						rewriter1.remove(rs, null); 

						
						edits = rewriter1.rewriteAST(document, null);	
						SaveModification.SaveModif(cu, edits);

					} catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}




				
				
			}
			
		
	}
	}
	public static void deleteReturnType(CompilationUnit cu, Change change,ASTNode node)
	{
		AST ast = cu.getAST();
		Document document=null;
		TextEdit edits=null;
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		ASTRewrite rewriter1 = ASTRewrite.create(ast);


		MethodDeclaration md =(MethodDeclaration) ASTManager.findMethodDeclaration(node);
		String mdname=	md.getName().getIdentifier();
		System.out.println(" the MD IS "+mdname + "change type string is "+ ((DeleteClass)change).getName());
		if( mdname.contains(((DeleteClass)change).getName()))
		{
			try {
				System.out.println("In return type resolution    added code ");

				document = new Document(iCompilUnit.getSource());
				rewriter1.remove(md, null);  
				edits = rewriter1.rewriteAST(document, null);	
				SaveModification.SaveModif(cu, edits);
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		else if(node instanceof SimpleName )
		{


			if((( (DeleteClass)change).getName()).equals(((SimpleName)node).getIdentifier()))
			{



				//  IPath pathcu = cu.getJavaElement().getPath();
				JavaVisitor jv =new JavaVisitor();
				cu.accept(jv);
				List<ReturnStatement> returnStms =jv.getReturnStatments();

				try {
					System.out.println("In return type resolution   ");

					document = new Document(iCompilUnit.getSource());
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				for(ReturnStatement rs :returnStms )
				{
					if( ASTManager.findMethodDeclaration(rs).equals(ASTManager.findMethodDeclaration(node)))
					{
						System.out.println(" The return statement :  "+rs.toString());
						rewriter1.remove((ASTNode)rs, null);  
						edits = rewriter1.rewriteAST(document, null);	
					}






				}
				if( edits != null)
					SaveModification.SaveModif(cu, edits);

				ASTModificationManager.RenameSimpleName(cu, node, "void");	
			}
		}
		else if (node instanceof QualifiedName)
		{

		}
	}

	public static void deleteParameterInMI(CompilationUnit cu,ASTNode node)
	{

		AST ast = cu.getAST();
		ASTRewrite rewriter1 = ASTRewrite.create(ast);

		Document document=null;
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		try {
			document = new Document(iCompilUnit.getSource());


			rewriter1.remove(node, null);  


			TextEdit edits = rewriter1.rewriteAST(document, null);

			SaveModification.SaveModif(cu, edits);

		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void DeleteVisitorGetClassMethod(CompilationUnit cu, Change change,ASTNode node)
	{
		ASTNode method= ASTManager.findMethodDeclaration(node);
		AST ast = cu.getAST();
		ASTRewrite rewriter1 = ASTRewrite.create(ast);

		//  IPath pathcu = cu.getJavaElement().getPath();

		Document document=null;
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		try {
			document = new Document(iCompilUnit.getSource());
			System.out.println(" YOU WANT TO REMOVE :"+method +" ITS TYPE IS : "+method.getClass());
			rewriter1.remove(method, null);  

			TextEdit edits = rewriter1.rewriteAST(document, null);

			SaveModification.SaveModif(cu, edits);
		}
		catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	public static void DeletePropertyResolution(CompilationUnit cu, Change change,ASTNode node )
	{
		System.out.println(" CASE OF PROPERTY DeletePropertyResolution"); 
		DeletePropertyResolution.applyResolution(cu,(DeleteProperty)change, node, 2, null);

	}
	public static void MovePropertyResolution(CompilationUnit cu, Change change,ASTNode node )
	{
		System.out.println(" CASE OF PROPERTY MovePropertyResolution");
		if(((MoveProperty)change).getUpperBound()==-1)
		{
			MoveResolution.applyResolution(cu, (MoveProperty)change, node, 2);
		}
		else
		{
			MoveResolution.applyResolution(cu, (MoveProperty)change, node, 0);
		}
	}
	public static void RenameLiteralResolution(CompilationUnit cu, Change change,ASTNode node,String newLiteral )
	{
		// Transform the new name to a literal
		String newLiteral2=ASTManager.makeLiteral(newLiteral);

		// Get old literal
		String  literal=((SimpleName)node).getIdentifier();
		literal= literal.replaceAll("_", "").toLowerCase();
		if( change instanceof RenameClass)
		{
			ASTModificationManager.RenameSimpleName(cu, node, newLiteral);
		}
		if(change instanceof MoveProperty)
		{
			// Check if the old literal is concerned by the rename change
			String deleted=((MoveProperty)change).getSourceClassName().toLowerCase()+((MoveProperty)change).getName().toLowerCase();
			if(deleted.equals(literal))
			{

				// Rename the literal
				ASTModificationManager.RenameSimpleName(cu, node, newLiteral2);
			}
		}
		else if(change instanceof RenameProperty)
		{
			String edited=((RenameProperty)change).getClassName().toLowerCase()+((RenameProperty)change).getName().toLowerCase();
			if(edited.equals(literal))
			{

				// Rename the literal
				ASTModificationManager.RenameSimpleName(cu, node, newLiteral2);
			}
		}


	}


	public static void deleteGetorSetProperty(CompilationUnit cu, Change change,ASTNode node )
	{
		if( node instanceof MethodDeclaration) {
			ASTNode method= ASTManager.findMethodDeclaration(node);
			AST ast = cu.getAST();
			ASTRewrite rewriter1 = ASTRewrite.create(ast);

			//  IPath pathcu = cu.getJavaElement().getPath();

			Document document=null;
			ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
			try {
				document = new Document(iCompilUnit.getSource());
				rewriter1.remove(method, null);  

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
			DeletePropertyResolution.applyResolution(cu,(DeleteProperty)change, node, 2, null);

		}

	}
	public static void PushLiteralResolution (CompilationUnit cu, Change change, ASTNode node)
	{
		System.out.println(" in push literal resolution" );

		AST ast = cu.getAST();
		Document document=null;
		TextEdit edits=null;

		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		NullLiteral nullLiteral = (NullLiteral) ast.createInstance(NullLiteral.class);

		ASTRewrite rewriter1 = ASTRewrite.create(ast);
		if(ASTManager.findFieldOrVariableDeclarations(node)instanceof FieldDeclaration )
		{
			for ( int i =0; i< ((PushProperty)change).getSubClassesNames().size();i++)
			{
				//Save the node identifier
				String originname=((SimpleName)node).getIdentifier();
				// Get the FD node
				FieldDeclaration oldFd= (FieldDeclaration) ASTManager.findFieldOrVariableDeclarations(node);

				// Build the new literal name
				String before1=ASTManager.makeLiteral(((PushProperty)change).getSuperClassName());
				String after1=ASTManager.makeLiteral(((PushProperty)change).getSubClassesNames().get(i));

				String res =((SimpleName)node).getIdentifier().replace(before1, after1);
				//Renaming the error literal
				((SimpleName)node).setIdentifier(res);

				FieldDeclaration fd = (FieldDeclaration) cu.getAST().createInstance(FieldDeclaration.class);
				ASTNode copyfd =ASTNode.copySubtree(oldFd.getAST(),oldFd);
				fd=(FieldDeclaration) copyfd;
				// Rename the FD variable name
				String before=((VariableDeclarationFragment)fd.fragments().get(0)).getName().getIdentifier();
				String after=((PushProperty)change).getSubClassesNames().get(i);

				String newName =before.replace(((PushProperty)change).getSuperClassName(), after);
				System.out.println(" NEW NAAAAAAAAAAAAAAAME FROM PUSH IS   "+newName);
				//String newName =((PushProperty)change).getSuperClassName()+"_"+((PushProperty)change).getSubClassesNames().get(i);
				//rewriter1.set(, SimpleName.IDENTIFIER_PROPERTY, newName, null);

				((VariableDeclarationFragment)fd.fragments().get(0)).getName().setIdentifier(newName);

				System.out.println(" THE FDDDDDDD "+fd.fragments().get(0));
				int pos = ((TypeDeclaration)oldFd.getParent()).bodyDeclarations().indexOf(oldFd);
				//System.out.println(" FIRST ELEM DE DECLAR "+((TypeDeclaration)oldFd.getParent()).bodyDeclarations().add(fd));
				System.out.println(" pos of fd parent  "+pos);


				//((TypeDeclaration)oldFd.getParent()).bodyDeclarations().add(pos+1,fd);
				//ListRewrite lrw2 = rewriter1.getListRewrite(cu, CompilationUnit.TYPES_PROPERTY);
				ListRewrite	 lrw2 = rewriter1.getListRewrite(((TypeDeclaration)oldFd.getParent()),TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
				Iterator it = 	oldFd.fragments().iterator();
				while(it != null && it.hasNext()){
					// Browse the list of fragments of the variable declaration
					Object obj = it.next();
					ArrayList<ASTNode> list_of_usage = new ArrayList<ASTNode>();
					// Get the usages of the variable declaration using its bindings
					if(obj instanceof VariableDeclarationFragment){
						if(JavaVisitor.getManageBindings().getBindingsNodes().containsKey(((VariableDeclarationFragment) obj).resolveBinding())){
							list_of_usage = JavaVisitor.getManageBindings().getBindingsNodes().get(((VariableDeclarationFragment) obj).resolveBinding());
						}

					} else if (obj instanceof SingleVariableDeclaration){

						if(JavaVisitor.getManageBindings().getBindingsNodes().containsKey(((SingleVariableDeclaration) obj).resolveBinding())){
							list_of_usage = JavaVisitor.getManageBindings().getBindingsNodes().get(((SingleVariableDeclaration) obj).resolveBinding());
						}

					}

					try {
						document = new Document(iCompilUnit.getSource());

					} catch (JavaModelException e) {

						e.printStackTrace();
					}
					//Browse the list of usages

					for(ASTNode astNode : list_of_usage){
						System.out.println( " NOT TREATED "+ astNode.getClass());

						ASTNode foundStatement = ASTManager.findStatement(astNode);				
						if(foundStatement != null && foundStatement instanceof ExpressionStatement || foundStatement instanceof VariableDeclarationStatement){
							try {
								System.out.println(" here is a VD usage "+ foundStatement);
								document = new Document(iCompilUnit.getSource());
								// Delete the usage statement from the AST
								rewriter1.remove(foundStatement, null);  


							} catch (JavaModelException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if(ASTManager.findFieldDeclaration(astNode)!=null)
						{
							rewriter1.replace(astNode, nullLiteral, null);

						}
						if(ASTManager.findInfixExpression(astNode)!=null)
						{
							ASTNode operand =ASTManager.findRightOrLeftOperandExpression(astNode);
							System.out.println(" Find an OPERAND "+ operand);
							rewriter1.remove(operand, null);	
						}

					}	
					// Finally, delete the variable declaration

				}
				rewriter1.remove(oldFd, null);
				try {

					document = new Document(iCompilUnit.getSource());
					lrw2.insertAt(fd, pos, null);
					((SimpleName)node).setIdentifier(originname);

					System.out.println( " After init "+((SimpleName)node).getIdentifier());


					//lrw2.insertAfter(fd,oldFd , null);
					//	lrw2.insertLast(fd, null);
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			}
			edits = rewriter1.rewriteAST(document, null);	
			SaveModification.SaveModif(cu, edits);
		}
		else if(ASTManager.findFieldOrVariableDeclarations(node) instanceof VariableDeclaration )	
		{

		}

	}

	public static void PushResolution (CompilationUnit cu, Change change, ASTNode node)
	{
		System.out.println(" in push resolution");

		AST ast = cu.getAST();
		Document document=null;
		TextEdit edits=null;
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		NullLiteral nullLiteral = (NullLiteral) ast.createInstance(NullLiteral.class);

		ASTRewrite rewriter1 = ASTRewrite.create(ast);
		try {

			document = new Document(iCompilUnit.getSource());
			if(ASTManager.findVariableDeclarationFragment(node)!=null) {
				System.out.println(" in var declar null");
				rewriter1.replace(((VariableDeclarationFragment)ASTManager.findVariableDeclarationFragment(node)).getInitializer(),nullLiteral, null);  

			}

			else 
			{
				System.out.println(" in parent null");
				rewriter1.replace(node.getParent(), nullLiteral, null);

			}
			edits = rewriter1.rewriteAST(document, null);	
			SaveModification.SaveModif(cu, edits);

		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	public static void addImportD(ArrayList<ICompilationUnit> Listcu,ArrayList<String> subClasses, CompilationUnit myCU)
	{
		AST ast = myCU.getAST(); 
		ASTRewrite rewriter1 = ASTRewrite.create(ast);
		Document document=null;
		for(String className : subClasses){
			System.out.println(" in 1 ");
			for(ICompilationUnit iCompilUnit : Listcu){
				System.out.println(" in 2");
				String fullClassName =className+".java";
				//System.out.println("Compilation unit : "+iCompilUnit.getElementName());
				if(fullClassName.equals(iCompilUnit.getElementName()) )
				{
					//CompilationUnit cu =ASTManager.getCompilationUnit(iCompilUnit);
					//ImportDeclaration myImDeclar =null;

					String[] output = iCompilUnit.getPath().toString().split("src");
					System.out.println(" the path 1 "+ output[1]);
					output[1]=output[1].replace("/", ".");
					output[1]=output[1].substring(1);
					output[1]=output[1].replaceAll(".java", "");
					//	output[1]=output[1].substring();
					System.out.println(" the path 2 "+ output[1]);
					ImportDeclaration id1 = myCU.getAST().newImportDeclaration();

					id1.setName(myCU.getAST().newName(output[1]));



					ICompilationUnit myiCompilUnit=(ICompilationUnit) myCU.getJavaElement();
					try {
						document = new Document(myiCompilUnit.getSource());



						ListRewrite lrw1 = rewriter1.getListRewrite(myCU, CompilationUnit.IMPORTS_PROPERTY);
						lrw1.insertLast(id1, null);


					}
					catch (JavaModelException | MalformedTreeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}



				}

			}

		}

		TextEdit edits = rewriter1.rewriteAST(document, null);

		SaveModification.SaveModif(myCU, edits);




	}


	public  static void applyResolutionIntroduceIfCastNew(CompilationUnit cu, Change change, ASTNode node) {

		/* Steps to do
		 * copy call path until p,
		 * create an if, and test in its expression if call path until p is instance of subclass,
		 * crete a then expressio wich contains the statment of prop, where a cast is introduced
		 * the cast type if the subclass
		 * add an import of subclass in case it is not already imported
		 * then put the var under the prop expression and thats it
		 * */
		//here I prepare the elements that will allow me to insert multiple if then else with multiple casts
		cu.recordModifications();
		ArrayList<IfStatement> leif = new ArrayList<IfStatement>();
		ArrayList<InstanceofExpression> linstance = new ArrayList<InstanceofExpression>();
		ArrayList<CastExpression> lcast = new ArrayList<CastExpression>();
		ArrayList<ParenthesizedExpression> lparanthaze = new ArrayList<ParenthesizedExpression>();
		ArrayList<String> subClasses = ((PushProperty)change).getSubClassesNames();

		AST ast = cu.getAST();
		Document document=null;
		TextEdit edits=null;
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();

		NullLiteral nullLiteral = (NullLiteral) ast.createInstance(NullLiteral.class);


		ASTRewrite rewriter2= ASTRewrite.create(ast);
		Document document2=null;
		try {
			document2 = new Document(iCompilUnit.getSource());
		} catch (JavaModelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		for(String subClass : subClasses){

			//here we create the structure of the if and all most of its needed elements
			IfStatement eif = (IfStatement) node.getRoot().getAST().createInstance(IfStatement.class);
			//        SingleVariableDeclaration param = (SingleVariableDeclaration) node.getAST().createInstance(SingleVariableDeclaration.class);
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

			leif.add(eif);
			linstance.add(instance);
			lcast.add(cast);
			lparanthaze.add(paranthaze);   
		}
		//        SimpleName varnameParam = (SimpleName) efor.getAST().createInstance(SimpleName.class);
		//        String generatedName = targetClassName.toLowerCase() + "_" + ((int)(Math.random()*10000));
		//        varnameParam.setIdentifier(generatedName);
		//        param.setName(varnameParam);
		//        efor.setParameter(param);

		//ASTNode copyNode = ASTNode.copySubtree(node.getRoot().getAST(), node);//
		ASTNode nodeTemp = node;

		//boolean gotcha = false;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			//check if it is in a variable declaration
			System.out.println("\n \n test =>"+ nodeTemp);
			if(nodeTemp instanceof MethodInvocation){
				//                System.out.println("            yay khelladi method invocation "+nodeTemp + " >> class "+nodeTemp.getClass());
				//                System.out.println("            yay khelladi parent method invocation "+nodeTemp.getParent() + " >> class "+nodeTemp.getParent().getClass());



				ASTNode findStat = nodeTemp;
				//boolean gotcha = false;

				//                System.out.println("            1 >> here MethodInvocation "+nodeTemp + " >> class "+nodeTemp.getClass());

				//now we treat the body of the if
				while (findStat != null && !(findStat instanceof CompilationUnit)) {
					//check if it is in a variable declaration
					System.out.println("            2 >> here MethodInvocation "+findStat + " >> class "+findStat.getClass());
					//                    System.out.println("            2 >> here MethodInvocation "+findStat.getParent() + " >> class "+findStat.getParent().getClass());

					if(findStat instanceof VariableDeclarationFragment){//treat teh case of var delcaration, we keep it declaration out of the if, and we creat an assiment for it
						System.out.println("            2.1 >> here MethodInvocation "+findStat + " >> class "+findStat.getClass());

						ASTNode originalExp = ASTNode.copySubtree(node.getRoot().getAST(), ((MethodInvocation) nodeTemp).getExpression());
						ASTNode saveFoundStat = null;
						for(int i = 0; i < subClasses.size(); i++){

							String subClass = subClasses.get(i);

							//here we put the expression cast in the then statment of if
							ASTNode astexpInstance = ASTNode.copySubtree(leif.get(i).getAST(), originalExp);//((MethodInvocation) nodeTemp).getExpression());
							System.out.println(" the astexpInstance is "+astexpInstance);
							linstance.get(i).setLeftOperand((Expression) astexpInstance);
							leif.get(i).setExpression(linstance.get(i));


							ASTNode astexpCast = ASTNode.copySubtree(leif.get(i).getAST(), originalExp);//((MethodInvocation) nodeTemp).getExpression());

							lcast.get(i).setExpression((Expression) astexpCast);
							lparanthaze.get(i).setExpression(lcast.get(i));

							//((MethodInvocation) nodeTemp).setExpression(lparanthaze.get(i));

							System.out.println("djamel node temp " + nodeTemp +"parent " + nodeTemp.getParent());
							MethodInvocation newMthod = (MethodInvocation) leif.get(i).getAST().createInstance(MethodInvocation.class);
							newMthod.setExpression(lparanthaze.get(i));
							SimpleName sname = (SimpleName) leif.get(i).getAST().createInstance(SimpleName.class);
							sname.setIdentifier(((MethodInvocation) nodeTemp).getName().getIdentifier());
							newMthod.setName(sname);
							ReplaceDirectExpression.replaceExpressionInParentNode(cu,nodeTemp, newMthod);
							((MethodInvocation) nodeTemp).getExpression().delete();
							nodeTemp.delete();
							nodeTemp = newMthod;

							ASTNode aststat = ASTNode.copySubtree(leif.get(i).getAST(),nodeTemp);
							System.out.println(" The cast aststat to insert is "+ aststat);
							System.out.println(" The cast aststat to insert is 2 "+ leif.get(i).getAST());
							System.out.println(" The cast aststat to insert is 3"+ ((VariableDeclarationFragment) findStat).getInitializer());
							Assignment assign = (Assignment) leif.get(i).getAST().createInstance(Assignment.class);
							SimpleName aname = (SimpleName) leif.get(i).getAST().createInstance(SimpleName.class);
							aname.setIdentifier(((VariableDeclarationFragment) findStat).getName().getIdentifier());
							assign.setLeftHandSide(aname);
							assign.setRightHandSide((Expression) aststat);
							System.out.println(" The cast to insert is "+ leif.get(i));
							ExpressionStatement expstat = (ExpressionStatement) leif.get(i).getAST().createInstance(ExpressionStatement.class);
							expstat.setExpression(assign);


							Block block = (Block) leif.get(i).getAST().createInstance(Block.class);

							block.statements().add(expstat);
							leif.get(i).setThenStatement(block);

							if(i!=0){
								//((Block)findStat.getParent()).statements().remove(leif.get(i));
								leif.get(i-1).setElseStatement(leif.get(i));
							}


						}

						//we delete the treated statment


						//  ICompilationUnit icu = cu.; // content is "public class X {\n}"
						//String source = cu.getSource();



						// start record of the modifications
						//   cu.rtype filter textecordModifications();
						NullLiteral nullLiteral2 = (NullLiteral) findStat.getAST().createInstance(NullLiteral.class);

						((VariableDeclarationFragment) findStat).setInitializer(nullLiteral2);//getInitializer().delete();//or delete it ??

						//findStat.getParent()

						((Block)((VariableDeclarationStatement)findStat.getParent()).getParent()).statements().add(
								((Block)((VariableDeclarationStatement)findStat.getParent()).getParent()).statements().indexOf(findStat.getParent()) + 1,
								leif.get(0));
						//((Block)findStat.getParent()).statements().remove(findStat);
						//	cu.recordModifications();
						TextEdit edits2 = cu.rewrite(document2, iCompilUnit.getJavaProject().getOptions(true));


						SaveModification.SaveModif(cu, edits2);
						// computation of the text edits







						/*	try {

							document = new Document(iCompilUnit.getSource());

							ListRewrite lrw2 = rewriter2.getListRewrite(((Block)((VariableDeclarationStatement)findStat.getParent()).getParent()),Block.STATEMENTS_PROPERTY);

							lrw2.insertAt(leif.get(0),((Block)((VariableDeclarationStatement)findStat.getParent()).getParent()).statements().indexOf((VariableDeclarationStatement)(findStat.getParent())) + 1, null);
							edits = rewriter2.rewriteAST(document, null);	
							SaveModification.SaveModif(cu, edits);

						} catch (JavaModelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							}
						 */	


						break;//return;

					} else if(findStat instanceof Statement && findStat.getParent() instanceof Block){//treat the rest of cases
						//gotcha = true;
						//nodeTemp.delete();

						System.out.println("            2.2 >> here MethodInvocation "+findStat + " >> class "+findStat.getClass());

						ASTNode originalExp = ASTNode.copySubtree(node.getRoot().getAST(), ((MethodInvocation) nodeTemp).getExpression());
						//ASTNode saveFoundStat = findStat;

						for(int i = 0; i < subClasses.size(); i++){

							//String subClass = subClasses.get(i);

							//here we put the expression cast in the then statment of if
							ASTNode astexpInstance = ASTNode.copySubtree(leif.get(i).getAST(), originalExp);//((MethodInvocation) nodeTemp).getExpression());
							linstance.get(i).setLeftOperand((Expression) astexpInstance);
							leif.get(i).setExpression(linstance.get(i));


							ASTNode astexpCast = ASTNode.copySubtree(leif.get(i).getAST(), originalExp);//((MethodInvocation) nodeTemp).getExpression());
							lcast.get(i).setExpression((Expression) astexpCast);
							lparanthaze.get(i).setExpression(lcast.get(i));

							//((MethodInvocation) nodeTemp).setExpression(lparanthaze.get(i));

							System.out.println(" test node temp " + nodeTemp +"parent " + nodeTemp.getParent());
							MethodInvocation newMthod = (MethodInvocation) leif.get(i).getAST().createInstance(MethodInvocation.class);

							SimpleName sname = (SimpleName) leif.get(i).getAST().createInstance(SimpleName.class);
							sname.setIdentifier(((MethodInvocation) nodeTemp).getName().getIdentifier());
							newMthod.setName(sname);
							newMthod.setExpression(lparanthaze.get(i));
							System.out.println("========> test new method "+newMthod);
							System.out.println("========> test old state "+findStat);
							ReplaceDirectExpression.replaceExpressionInParentNode(cu,nodeTemp, newMthod);
							System.out.println("========> test new state "+findStat);
							//((MethodInvocation) nodeTemp).getExpression().delete();
							//nodeTemp.delete();
							nodeTemp = newMthod;

							Block block = (Block) leif.get(i).getAST().createInstance(Block.class);
							ASTNode copyFindStat = ASTNode.copySubtree(block.getAST(), findStat);
							//ASTNode copyoriginalExp = ASTNode.copySubtree(leif.get(i).getAST(), originalExp);
							//((MethodInvocation) nodeTemp).setExpression((Expression) copyoriginalExp); //he I put the node expression in the orignal statement back to its state for the next subclasses treatment
							//ASTNode copyFindStat2 = ASTNode.copySubtree(block.getAST(), findStat);

							block.statements().add(copyFindStat);//(findStat);//(Statement)
							//block.statements().add(copyFindStat2);
							leif.get(i).setThenStatement(block);
							if(i!=0){
								//((Block)findStat.getParent()).statements().remove(leif.get(i));
								leif.get(i-1).setElseStatement(leif.get(i));
							}


							//we delete the treated statment
							//findStat.delete();




						}

						/*System.out.println("test node temp " + nodeTemp +"parent " + nodeTemp.getParent());
                        MethodInvocation newMthod = (MethodInvocation) nodeTemp.getAST().createInstance(MethodInvocation.class);
                        //newMthod.setExpression(lparanthaze.get(0));
                        SimpleName sname = (SimpleName) nodeTemp.getAST().createInstance(SimpleName.class);
                        sname.setIdentifier(((MethodInvocation) nodeTemp).getName().getIdentifier());
                        newMthod.setName(sname);
                        ReplaceDirectExpression.replaceExpressionInParentNode(nodeTemp, newMthod);
						 */


						((Block)findStat.getParent()).statements().add(((Block)findStat.getParent()).statements().indexOf(findStat), leif.get(0));
						((Block)findStat.getParent()).statements().remove(findStat);
						findStat.delete();
						TextEdit edits2 = cu.rewrite(document2, iCompilUnit.getJavaProject().getOptions(true));


						SaveModification.SaveModif(cu, edits2);

						break;//return;
					}
					findStat = findStat.getParent();

				}
				//    rewriter2.replace(((VariableDeclarationFragment)(VariableDeclarationFragment) findStat).getInitializer() , nullLiteral, null);  
				// edits = rewriter2.rewriteAST(document, null);	


				/*if(saveFoundStat != null){
                    ((Block)saveFoundStat.getParent()).statements().remove(saveFoundStat);
                    System.out.println("\n=========>hello rm stat \n");
                }

                for(int i = 0; i < subClasses.size(); i++){
                    String subClass = subClasses.get(i);

                    SimpleType ctype = (SimpleType) lcast.get(i).getAST().createInstance(SimpleType.class);
                    SimpleName cname = (SimpleName) lcast.get(i).getAST().createInstance(SimpleName.class);
                    cname.setIdentifier(subClass+"zehahah");
                    ctype.setName(cname);
                    lcast.get(i).setType(ctype);
                    System.out.println("\n=========>hello"+lcast.get(i));
                }
				 */

				ASTNode temp = leif.get(0);

				while (temp != null && !(temp instanceof CompilationUnit)) {
					temp = temp.getParent();
				}
				//	System.out.println(" Temps is  "+temp);

				//ResolutionsUtils.replaceDirectExpressionInParentNode(nodeTemp, method);
				//nodeTemp.delete();
				return;

			}

			nodeTemp = nodeTemp.getParent();
		}


	}
	public static void removePushedPropertyMD(CompilationUnit cu, Change change, ASTNode node)
	{
		MethodDeclaration md= (MethodDeclaration) ASTManager.findMethodDeclaration(node);
		AST ast = cu.getAST();
		ASTRewrite rewriter1 = ASTRewrite.create(ast);

		Document document=null;
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		try {
			document = new Document(iCompilUnit.getSource());
		} catch (JavaModelException e) {

			e.printStackTrace();
		}

		rewriter1.remove(md, null);  
		TextEdit edits = rewriter1.rewriteAST(document, null);

		SaveModification.SaveModif(cu, edits);


	}

	public static void PushGetorSetResolution (CompilationUnit cu, Change change, ASTNode node)
	{
		//System.out.println("In resolution push " +ASTManager.findMyVarDeclarationStat(cu, (SimpleName)node) +"second one is "+ASTManager.findParameter(node));
		if (ASTManager.findVariableDeclarationFragment(node) !=null)
		{
			AST ast = cu.getAST();
			Document document=null;
			TextEdit edits=null;
			ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
			NullLiteral nullLiteral = (NullLiteral) ast.createInstance(NullLiteral.class);

			ASTRewrite rewriter1 = ASTRewrite.create(ast);
			try {

				document = new Document(iCompilUnit.getSource());

				rewriter1.replace(((VariableDeclarationFragment)ASTManager.findVariableDeclarationFragment(node)).getInitializer(),nullLiteral, null);  

				edits = rewriter1.rewriteAST(document, null);	
				SaveModification.SaveModif(cu, edits);

			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			if(ASTManager.findMyVarDeclarationStat(cu, (SimpleName)node)!=null || ASTManager.findParameter(node)!=null)
			{
				// Replace the initializer of the VD and the parameter by null
				System.out.println("IF1");
				AST ast = cu.getAST();
				Document document=null;
				TextEdit edits=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				NullLiteral nullLiteral = (NullLiteral) ast.createInstance(NullLiteral.class);

				ASTRewrite rewriter1 = ASTRewrite.create(ast);
				try {

					document = new Document(iCompilUnit.getSource());

					if(ASTManager.findVariableDeclarationFragment(node)!=null) {
						System.out.println(" var declar "+ASTManager.findVariableDeclarationFragment(node) +"for node "+node);
						rewriter1.replace(((VariableDeclarationFragment)ASTManager.findVariableDeclarationFragment(node)).getInitializer(),nullLiteral, null);  
						System.out.println(" initializer "+((VariableDeclarationFragment)ASTManager.findVariableDeclarationFragment(node)).getInitializer());
					}
					else 
					{
						System.out.println(" in parent null"+node.getParent());
						rewriter1.replace(node.getParent(), nullLiteral, null);

					}
					edits = rewriter1.rewriteAST(document, null);	
					SaveModification.SaveModif(cu, edits);

				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if ( ASTManager.findExpressionStatement(node)!=null)
			{

				//Delete the statement
				AST ast = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast);

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
				} catch (JavaModelException e) {

					e.printStackTrace();
				}
				System.out.println(" Statement will be deleted "+ASTManager.findExpressionStatement(node));
				rewriter1.remove(ASTManager.findExpressionStatement(node), null);  
				TextEdit edits = rewriter1.rewriteAST(document, null);

				SaveModification.SaveModif(cu, edits);
			}
		System.out.println(" out of the resolution ");
	}
	public static void resolveMethodInvocDeletedClass(CompilationUnit cu, ASTNode node)
	{
		AST ast = cu.getAST();
		Document document=null;
		TextEdit edits=null;
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		NullLiteral nullLiteral = (NullLiteral) ast.createInstance(NullLiteral.class);
		System.out.println(" the node in method invock  "+ node + "his parent "+node.getParent());

		ASTRewrite rewriter1 = ASTRewrite.create(ast);
		try {

			document = new Document(iCompilUnit.getSource());

			if(node.getParent().getParent() instanceof TypeLiteral) {
				rewriter1.replace(node.getParent().getParent(), nullLiteral, null);
				System.out.println(" in  typeliteral");
				edits = rewriter1.rewriteAST(document, null);

				SaveModification.SaveModif(cu, edits);
			}
			else
			{
				System.out.println(" out  typeliteral");
				rewriter1.replace( ASTManager.findMethodInvocation(node), nullLiteral, null);
				edits = rewriter1.rewriteAST(document, null);

				SaveModification.SaveModif(cu, edits);
			}
		}
		catch (JavaModelException e) {

			e.printStackTrace();
		}
	}
	public static void GeneralizationResolution (CompilationUnit cu, Change change, ASTNode node){
		NumberLiteral number = (NumberLiteral) cu.getAST().createInstance(NumberLiteral.class);
		number.setToken("0");
		MethodInvocation method = (MethodInvocation) node.getAST().createInstance(MethodInvocation.class);
		SimpleName name = (SimpleName) node.getAST().createInstance(SimpleName.class);
		name.setIdentifier("get");
		method.setName(name);
		method.arguments().add(number);
		System.out.println(" created meth  "+method);
		MethodInvocation mi =(MethodInvocation) ASTManager.findMethodInvocation(node);
		System.out.println(" le MI " +mi);
		MethodInvocation miTemp=mi;
		if(mi!=null )
		{



			//ASTNode ast = mi.getExpression();
			//	System.out.println(" AST found  "+ ast);
			//	mi.setExpression(null);
			//	//method.setExpression((Expression) ast);
			//mi.setExpression(miTemp);
			//	mi.setName(method);*
			ASTNode copymi =ASTNode.copySubtree(mi.getAST(),mi);

			method.setExpression((Expression) copymi);
			AST ast = cu.getAST();
			Document document=null;
			TextEdit edits=null;
			ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();

			ASTRewrite rewriter1 = ASTRewrite.create(ast);
			try {

				document = new Document(iCompilUnit.getSource());


				rewriter1.replace(mi,method, null);  
				edits = rewriter1.rewriteAST(document, null);	
				SaveModification.SaveModif(cu, edits);
			}
			catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println(" in MethodInvocation  "+method);





		}

	}

	public static void GeneralizationBeforeResolution (CompilationUnit cu, Change change, ASTNode node){
		NumberLiteral number = (NumberLiteral) cu.getAST().createInstance(NumberLiteral.class);
		number.setToken("0");
		MethodInvocation method = (MethodInvocation) node.getAST().createInstance(MethodInvocation.class);
		MethodInvocation newMethod = (MethodInvocation) node.getAST().createInstance(MethodInvocation.class);

		SimpleName name = (SimpleName) node.getAST().createInstance(SimpleName.class);
		name.setIdentifier("get");
		method.setName(name);
		method.arguments().add(number);
		System.out.println(" created meth  "+method);
		MethodInvocation mi =(MethodInvocation) ASTManager.findMethodInvocation(node);
		System.out.println(" le MI " +mi);
		MethodInvocation miTemp=mi;
		if(mi!=null )
		{



			//ASTNode ast = mi.getExpression();
			//	System.out.println(" AST found  "+ ast);
			//	mi.setExpression(null);
			//	//method.setExpression((Expression) ast);
			//mi.setExpression(miTemp);
			//	mi.setName(method);*
			ASTNode copymi =ASTNode.copySubtree(mi.getAST(),mi.getExpression());

			ASTNode copyName =ASTNode.copySubtree(mi.getAST(),mi.getName());

			System.out.println(" the copied name is "+ copyName);
			method.setExpression((Expression) copymi);
			System.out.println(" genralisze before "+ method);
			newMethod.setExpression(method);
			newMethod.setName((SimpleName) copyName);
			for( Object arg: mi.arguments())
			{
				ASTNode copyArg =ASTNode.copySubtree(mi.getAST(),(ASTNode) arg);

				newMethod.arguments().add(copyArg);
			}
			AST ast = cu.getAST();
			Document document=null;
			TextEdit edits=null;
			ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();

			ASTRewrite rewriter1 = ASTRewrite.create(ast);
			try {

				document = new Document(iCompilUnit.getSource());


				rewriter1.replace(mi,newMethod, null);  
				edits = rewriter1.rewriteAST(document, null);	
				SaveModification.SaveModif(cu, edits);
			}
			catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println(" in MethodInvocation before  "+newMethod);





		}

	}
	public static void ChangeTypeResolution (CompilationUnit cu, Change change, ASTNode node){
		SetProperty c= (SetProperty) change;
		ASTNode nodeTemp= ASTManager.findFieldOrVariableDeclarations(node);
		if(nodeTemp instanceof VariableDeclarationStatement)
		{
			VariableDeclarationStatement vd =(VariableDeclarationStatement) nodeTemp;
			SimpleName toRename=(SimpleName) (( (SimpleType) vd.getType()).getName());
			System.out.println(" you will rename to change type " +toRename+ " to "+c.getNewType());
			ASTModificationManager.RenameSimpleName(cu,toRename, c.getNewType());

		}
	}

}
