package Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;






public class ASTModificationManager {
	public static void AddImportDeclaration(CompilationUnit cu, String importPath)
	{

		String[] importString=importPath.split("\\.");
		AST ast = cu.getAST(); 
		ASTRewrite rewriter1 = ASTRewrite.create(ast);

		Document document=null;
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		try {
			document = new Document(iCompilUnit.getSource());

			ImportDeclaration id1 = ast.newImportDeclaration();

			id1.setName(ast.newName(importString));


			ListRewrite lrw1 = rewriter1.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY);
			lrw1.insertLast(id1, null);

			TextEdit edits = rewriter1.rewriteAST(document, null);

			SaveModification.SaveModif(cu, edits);

		}
		catch (JavaModelException | MalformedTreeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




	}

	public static void RenameSimpleName(CompilationUnit cu,ASTNode an, String newname)
	{

		AST ast = cu.getAST();// Get AST of the compilation unit
		// Create ASTWerite object to collect AST modifications
		ASTRewrite rewriter1 = ASTRewrite.create(ast);
		// Get code source of compilation unit
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		Document document;
		try {
			// Create document object to set code source file content
			document = new Document(iCompilUnit.getSource());
			// Edit the identifier of the node
		
			rewriter1.set((SimpleName)an, SimpleName.IDENTIFIER_PROPERTY, newname, null);
			// Gather all the modifications on AST  from ASTRewrite
			TextEdit edits = rewriter1.rewriteAST(document, null);
			// Send modifications to be saved in disk
			SaveModification.SaveModif(cu, edits);
		} catch (JavaModelException e) {

			e.printStackTrace();
		}

	}

	public static void AddHelloStatement(CompilationUnit cu)
	{
		AST ast = cu.getAST();
		ASTRewrite rewriter1 = ASTRewrite.create(ast);
		ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
		Document document=null;
		List<MethodDeclaration> methodDeclarations = MethodDeclarationFinder.perform(cu);
		for (MethodDeclaration methodDeclaration : methodDeclarations) {
			MethodInvocation methodInvocation = ast.newMethodInvocation();


			QualifiedName qName = ast.newQualifiedName(ast.newSimpleName("System"), ast.newSimpleName("out"));
			methodInvocation.setExpression(qName);
			methodInvocation.setName(ast.newSimpleName("println"));

			StringLiteral literal = ast.newStringLiteral();
			literal.setLiteralValue("Hello, World");
			methodInvocation.arguments().add(literal);

			if (methodDeclaration.getBody().statements()==null)
				System.out.println( "HERE IS THE METHOD INVOCATION TO ADD  "+methodInvocation);

			methodDeclaration.getBody().statements().add(ast.newExpressionStatement(methodInvocation));

			ListRewrite lrw2 = rewriter1.getListRewrite(cu, CompilationUnit.TYPES_PROPERTY);

			lrw2.insertLast(methodInvocation, null);



		}
		try {
			document = new Document(iCompilUnit.getSource());
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		TextEdit edits = rewriter1.rewriteAST(document, null);
		// edits.apply(document);
		SaveModification.SaveModif(cu, edits);
	}
	public static final class MethodDeclarationFinder extends ASTVisitor {
		private final List <MethodDeclaration> methods = new ArrayList <> ();

		public static List<MethodDeclaration> perform(ASTNode node) {
			MethodDeclarationFinder finder = new MethodDeclarationFinder();
			node.accept(finder);
			return finder.getMethods();
		}

		@Override
		public boolean visit (final MethodDeclaration method) {
			methods.add (method);
			return super.visit(method);
		}

		/**
		 * @return an immutable list view of the methods discovered by this visitor
		 */
		public List <MethodDeclaration> getMethods() {
			return Collections.unmodifiableList(methods);
		}
	}

}
