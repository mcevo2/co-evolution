package Utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import fr.lip6.meta.ComplexChangeDetection.Change;

public class ASTManager {
	public static CompilationUnit getCompilationUnit(ICompilationUnit iCompilUnit)
	{
		//JavaParser jp = new JavaParser();
		ASTParser parser = ASTParser.newParser(AST.JLS16);
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT); 

		parser.setSource(iCompilUnit);
		parser.setBindingsRecovery(true);

		parser.setBindingsRecovery(true);
		CompilationUnit cu = (CompilationUnit) parser.createAST( null);
		//JavaVisitor jVisitor = new JavaVisitor();
		//	CompilationUnit newUnit = (CompilationUnit) parser.createAST(null /* IProgressMonitor */); // parse
		//	jVisitor.process(newUnit);

		return cu;
	} 
	public static CompilationUnit getCompilationUnit2(ICompilationUnit iCompilUnit)
	{
		//JavaParser jp = new JavaParser();
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setBindingsRecovery(true);
		parser.setSource(iCompilUnit);

		//	parser.setBindingsRecovery(true);
		CompilationUnit cu = (CompilationUnit) parser.createAST( null);


		return cu;
	}
	public static AST getCompilUnitAST (CompilationUnit cu)
	{
		AST ast= cu.getAST();
		return ast;
	}

	public static ASTNode getErrorNode(CompilationUnit cu,IMarker marker)
	{
		int start = marker.getAttribute(IMarker.CHAR_START, 0);
		int end = marker.getAttribute(IMarker.CHAR_END, 0);
		NodeFinder nf = new NodeFinder(cu.getRoot(), start, end-start);
		ASTNode an=nf.getCoveringNode();

		return an;
	}
	public static ArrayList<ASTNode> getErrorNodes(CompilationUnit cu,ArrayList<IMarker> markers)
	{
		ArrayList<ASTNode> ans= new ArrayList<ASTNode>(); 

		ASTNode an=null;
		int start,end=0;
		for( IMarker marker: markers) {

			start = marker.getAttribute(IMarker.CHAR_START, 0);

			end = marker.getAttribute(IMarker.CHAR_END, 0);

			NodeFinder nf = new NodeFinder(cu.getRoot(), start, end-start);

			an=nf.getCoveringNode( );
			ans.add(an);
		}

		return ans;
	}
	public static ASTNode findImportDeclaration(ASTNode node) {

		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			if(nodeTemp instanceof ImportDeclaration){
				return nodeTemp;
			}

			nodeTemp = nodeTemp.getParent();
		}

		return null;
	}

	public static boolean isReturnType(ASTNode node )
	{
		boolean isReturnType=false;
		if(node instanceof SimpleName && node.getParent() instanceof SimpleType && node.getParent().getParent() instanceof MethodDeclaration){//&& node.getParent() != null

			isReturnType=true;

		}
		return isReturnType;
	}
	public static boolean checkImportDeclaration( ASTNode node)
	{
		boolean isImport=false;



		if(node.getParent() instanceof ImportDeclaration)
			isImport=true;



		return isImport;
	}
	public static List<ASTNode> getChildren(ASTNode node) {
		List<ASTNode> children = new ArrayList<ASTNode>();
		List list = node.structuralPropertiesForType();
		for (int i = 0; i < list.size(); i++) {
			Object child = node.getStructuralProperty((StructuralPropertyDescriptor)list.get(i));
			if (child instanceof ASTNode) {
				children.add((ASTNode) child);

			}
		}
		return children;
	}
	public static ASTNode findVariableDeclarationFragment(ASTNode node) {
		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {


			if(nodeTemp instanceof VariableDeclarationFragment ){

				return nodeTemp;
			}

			nodeTemp = nodeTemp.getParent();
		}

		return null;
	}
	
	public static ASTNode findBodyDeclaration(ASTNode node) {
		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {


			if(nodeTemp instanceof BodyDeclaration ){

				return nodeTemp;
			}

			nodeTemp = nodeTemp.getParent();
		}

		return null;
	}


	public static ASTNode findAssignment(ASTNode node) {
		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {


			if(nodeTemp instanceof Assignment ){

				return nodeTemp;
			}

			nodeTemp = nodeTemp.getParent();
		}

		return null;
	}
	public static ASTNode findFieldOrVariableDeclarations(ASTNode node) {

		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			if(nodeTemp instanceof FieldDeclaration || nodeTemp instanceof VariableDeclarationStatement){

				return nodeTemp;
			}

			nodeTemp = nodeTemp.getParent();
		}

		return null;
	}
	public static ASTNode findFieldDeclaration(ASTNode node) {

		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			if(nodeTemp instanceof FieldDeclaration ){

				return nodeTemp;
			}

			nodeTemp = nodeTemp.getParent();
		}

		return null;
	}
	
	public static ASTNode findRightOrLeftOperandExpression(ASTNode node) {

		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			if(nodeTemp.getParent() instanceof InfixExpression ){

				return nodeTemp;
			}

			nodeTemp = nodeTemp.getParent();
		}

		return null;
	}
	public static ASTNode findIfWhileForStatement(ASTNode node) {

		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			if(nodeTemp instanceof IfStatement  || nodeTemp instanceof WhileStatement || nodeTemp instanceof EnhancedForStatement ||nodeTemp instanceof EmptyStatement){

				return nodeTemp;
			}

			nodeTemp = nodeTemp.getParent();
		}

		return null;
	}
	public static ASTNode findExpressionStatement(ASTNode node) {

		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {


			if(nodeTemp instanceof ExpressionStatement){

				return nodeTemp;
			}

			nodeTemp = nodeTemp.getParent();
		}

		return null;
	}
	public static ASTNode findInfixExpression(ASTNode node) {

		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {


			if(nodeTemp instanceof InfixExpression){

				return nodeTemp;
			}

			nodeTemp = nodeTemp.getParent();
		}

		return null;
	}
	public static ASTNode findParameterInMethodDeclaration(ASTNode node) {

		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			if(nodeTemp instanceof SingleVariableDeclaration){//MethodDeclaration){no need to get to method declaration and then delete the parameter, just delete the parameter directly
				return nodeTemp;
			}

			nodeTemp = nodeTemp.getParent();
		}

		return null;

	}
	public static ASTNode findStatement(ASTNode node) {

		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			if(nodeTemp instanceof ExpressionStatement || nodeTemp instanceof VariableDeclarationStatement){
				return nodeTemp;
			}

			nodeTemp = nodeTemp.getParent();
		}

		return null;
	}
	public static ASTNode findMethodDeclaration(ASTNode node) {

		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {

			if(nodeTemp instanceof MethodDeclaration ){
				return nodeTemp;
			}

			nodeTemp = nodeTemp.getParent();
		}

		return null;
	}

	public static ASTNode findReturnStatment(ASTNode node) {

		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {

			if(nodeTemp instanceof ReturnStatement ){
				return nodeTemp;
			}

			nodeTemp = nodeTemp.getParent();
		}

		return null;
	}

	public static ASTNode findSwitchCase(ASTNode node) {

		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {

			if(nodeTemp instanceof SwitchCase ){
				return nodeTemp;
			}

			nodeTemp = nodeTemp.getParent();
		}

		return null;
	}

	
	public static ASTNode findClassInstanceCreations(ASTNode node) {

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

	public static ASTNode findMethodInvocation(ASTNode node) {

		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			if(nodeTemp instanceof MethodInvocation){
				return nodeTemp;
			}

			nodeTemp = nodeTemp.getParent();
		}

		return null;
	} 
	public static boolean isSuperClass(ASTNode node,String change)
	{

		boolean classname=false;
		while (node != null && !(node instanceof CompilationUnit)) {

			if(node instanceof TypeDeclaration  ){
				if(((TypeDeclaration)node).resolveBinding().getSuperclass()!=null) {
					classname=change.equals(((TypeDeclaration)node).resolveBinding().getSuperclass().getName());
					if(classname) {
						return true;
					}
				}
			}
			node = node.getParent();
		}
		return false;
	}


	public static boolean isLiteral(ASTNode node)
	{
		ASTNode parent =node.getParent();

		List<ASTNode> children ;
		ASTNode child;
		if(parent instanceof QualifiedName)
		{


			children=ASTManager.getChildren(parent) ; 

			child=children.get(0);
			if (child instanceof QualifiedName)
			{
				children=ASTManager.getChildren(child);
				child=children.get(1);
				if(child instanceof SimpleName) {
					if(((SimpleName)child).getIdentifier().equals("Literals"))
					{

						return true;
					}
				}

			}



		}
		return false;
	}
	public static ASTNode findParameter(ASTNode node)
	{

		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			if(nodeTemp.getParent() instanceof MethodDeclaration && nodeTemp instanceof SingleVariableDeclaration ) {
				return nodeTemp;
			}

			nodeTemp = nodeTemp.getParent();
		}

		return null;



	}
	
	public static ASTNode findSingleVariableDeclaration(ASTNode node)
	{

		ASTNode nodeTemp = node;
		while (nodeTemp != null && !(nodeTemp instanceof CompilationUnit)) {
			if( nodeTemp instanceof SingleVariableDeclaration ) {
				return nodeTemp;
			}

			nodeTemp = nodeTemp.getParent();
		}

		return null;



	}
	public static Boolean containOnlyCapitals(String literal)
	{

		char currentCharacter ;
		for (int i = 0; i < literal.length(); i++) {
			currentCharacter = literal.charAt(i);

			if (!Character.isUpperCase(currentCharacter) && currentCharacter !='_'  ) {

				return false;
			}


		}


		return true;
	}
	public static String makeLiteral(String literal)
	{
		System.out.println(" your literal before is "+literal);
		if(! ASTManager.containOnlyCapitals(literal))
		{
			String newLiteral="";
			char currentCharacter;
			char precedantCharacter = '\0';
			for (int i = 0; i < literal.length(); i++) {
				if( i>0)
				{
					precedantCharacter	=literal.charAt(i-1);
				}
				currentCharacter = literal.charAt(i);

				if (Character.isUpperCase(currentCharacter) && i!=0  && !Character.isUpperCase(precedantCharacter) ) {

					newLiteral=newLiteral+"_";
				}

				newLiteral=newLiteral+ Character.toUpperCase(currentCharacter);

			}
			System.out.println(" NEW LITERAL "+newLiteral);
			return newLiteral;
		}
		else 
			return literal;


	}

	public static VariableDeclarationStatement findMyVarDeclarationStat(CompilationUnit cu,  SimpleName node)
	{
		JavaVisitor jv =new JavaVisitor();
		Iterator it=null;
		cu.accept(jv);
		if( jv.getVariableDeclarationStatements().isEmpty())
		{
			System.out.println(" vide ") ;
		}

		for(VariableDeclarationStatement varDecl : jv.getVariableDeclarationStatements())
		{
			System.out.println("  in for vardeclar");
			it = ((VariableDeclarationStatement) varDecl).fragments().iterator();

			while(it != null && it.hasNext()){

				Object obj = it.next(); 
				if(obj instanceof VariableDeclarationFragment){
					System.out.println(" Variable is   :    "+((VariableDeclarationFragment)obj).getName().getIdentifier());
					if(((VariableDeclarationFragment)obj).getInitializer()==null) {
						System.out.println("  NULL initializer");
						if(node.getIdentifier().equals(((VariableDeclarationFragment)obj).getName().getIdentifier()))
						{
							System.out.println(" WANTED VAR DECLARRR"+varDecl);
							return varDecl;
						}
					}
				}


			}

		}

		return null;	

	}
}