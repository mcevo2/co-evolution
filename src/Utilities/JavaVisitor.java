package Utilities;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.CreationReference;
import org.eclipse.jdt.core.dom.Dimension;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ExpressionMethodReference;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.IntersectionType;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NameQualifiedType;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeMethodReference;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;

//import  Utilities.utils.UtilsParser;

//import net.sourceforge.earticleast.app.LocalVariableDetector;

public class JavaVisitor extends ASTVisitor {//abstract?
	
	 List<ReturnStatement> returnStatments = new ArrayList<ReturnStatement>();
	 List<VariableDeclarationStatement> variableDeclarationStatements = new ArrayList<VariableDeclarationStatement>();
	public List<VariableDeclarationStatement> getVariableDeclarationStatements() {
		return variableDeclarationStatements;
	}

	public void setVariableDeclarationStatements(List<VariableDeclarationStatement> variableDeclarationStatements) {
		this.variableDeclarationStatements = variableDeclarationStatements;
	}

	//TODO, if you want to revisit during reolution, then put the initialisation here only and not in the constructors, and reintitialaisze it elewhere if you want to reexecute
	private static ManageBindings manageBindings = new ManageBindings();

	
	public static ManageBindings getManageBindings() {
		return manageBindings;
	}

	public static void setManageBindings(ManageBindings manageBindings) {
		JavaVisitor.manageBindings = manageBindings;
	}

	public JavaVisitor() {
		super();
		//manageBindings = new ManageBindings();
	}

	public JavaVisitor(boolean visitDocTags) {//this will visit the doc tags, maybe interesting in future
		super(visitDocTags);
		//manageBindings = new ManageBindings();
	}
	
	public void process(CompilationUnit unit) {

		System.out.println("*****************************************");
		System.out.println("I am in accept visit for the java class >>" + unit.getJavaElement().getElementName());
//		tempunit=unit;
//		tempunit.recordModifications(); this is rather for the co-evolution part when the ast is modified

		unit.accept(this);
		
	}
	 public List<ReturnStatement> getReturnStatments() {
	      return returnStatments;
	    }
	public void preVisit(ASTNode node) {
		//TODO System.out.println("class type >> "+node.getClass());
		
//		if(node instanceof Statement){
//			//System.out.println("			Omar statement  class id >>" + node + " | parent " + node.getParent());
//
//			System.out.println("			Oamar statement  class id >>" + node.getClass() + " | parent " + node.getParent().getClass());
//		}
	}

	
	public boolean visit(AnnotationTypeDeclaration node) {
		return true;
	}
	
	public boolean visit(AnnotationTypeMemberDeclaration node) {
		return true;
	}
	
	public boolean visit(AnonymousClassDeclaration node) {
		return true;
	}
	
	public boolean visit(ArrayAccess node) {
		return true;
	}
	
	public boolean visit(ArrayCreation node) {
		return true;
	}
	
	public boolean visit(ArrayInitializer node) {
		return true;
	}
	
	public boolean visit(ArrayType node) {
		return true;
	}
	
	public boolean visit(AssertStatement node) {
		return true;
	}
	
	public boolean visit(Assignment node) {
		return true;
	}
	
	public boolean visit(Block node) {
		return true;
	}
	
	public boolean visit(BlockComment node) {
		return true;
	}
	
	public boolean visit(BooleanLiteral node) {
		return true;
	}
	
	public boolean visit(BreakStatement node) {
		return true;
	}
	
	public boolean visit(CastExpression node) {
		return true;
	}
	
	public boolean visit(CatchClause node) {
		return true;
	}
	
	public boolean visit(CharacterLiteral node) {
		return true;
	}
	
	public boolean visit(ClassInstanceCreation node) {
		return true;
	}
	
	public boolean visit(CompilationUnit node) {
		return true;
	}
	
	public boolean visit(ConditionalExpression node) {
		return true;
	}
	
	public boolean visit(ConstructorInvocation node) {
		return true;
	}
	
	public boolean visit(ContinueStatement node) {
		return true;
	}
	
	public boolean visit(CreationReference node) {
		return true;
	}
	
	public boolean visit(Dimension node) {
		return true;
	}
	
	public boolean visit(DoStatement node) {
		return true;
	}
	
	public boolean visit(EmptyStatement node) {
		return true;
	}
	
	public boolean visit(EnhancedForStatement node) {
		return true;
	}
	
	public boolean visit(EnumConstantDeclaration node) {
		return true;
	}
	
	public boolean visit(EnumDeclaration node) {
		return true;
	}
	
	public boolean visit(ExpressionMethodReference node) {
		return true;
	}
	
	public boolean visit(ExpressionStatement node) {
		return true;
	}
	
	public boolean visit(FieldAccess node) {
		return true;
	}
	
	public boolean visit(FieldDeclaration node) {
		
		return true;
	}
	
	public boolean visit(ForStatement node) {
		return true;
	}
	
	public boolean visit(IfStatement node) {
		return true;
	}
	
	public boolean visit(ImportDeclaration node) {
//		System.out.println("khelladi import "+node.getName().getClass());
//		System.out.println("khelladi import "+node.getName().getFullyQualifiedName());
		
		return true;
	}
	
	public boolean visit(InfixExpression node) {
		return true;
	}
	
	public boolean visit(Initializer node) {
		return true;
	}
	
	public boolean visit(InstanceofExpression node) {
		return true;
	}
	
	public boolean visit(IntersectionType node) {
		return true;
	}
	
	public boolean visit(Javadoc node) {
		// visit tag elements inside doc comments only if requested
		return true;//this.visitDocTags;
	}
	
	public boolean visit(LabeledStatement node) {
		return true;
	}
	
	public boolean visit(LambdaExpression node) {
		return true;
	}
	
	public boolean visit(LineComment node) {
		return true;
	}
	
	public boolean visit(MarkerAnnotation node) {
		return true;
	}
	
	public boolean visit(MemberRef node) {
		return true;
	}
	
	public boolean visit(MemberValuePair node) {
		return true;
	}
	
	public boolean visit(MethodRef node) {
		return true;
	}
	
	public boolean visit(MethodRefParameter node) {
		return true;
	}
	
	public boolean visit(MethodDeclaration node) {
		return true;
	}
	
	public boolean visit(MethodInvocation node) {
		return true;
	}
	
	public boolean visit(Modifier node) {
		
		return true;
	}
	
	public boolean visit(NameQualifiedType node) {
		return true;
	}
	
	public boolean visit(NormalAnnotation node) {
		return true;
	}
	
	public boolean visit(NullLiteral node) {
		return true;
	}
	
	public boolean visit(NumberLiteral node) {
		return true;
	}
	
	public boolean visit(PackageDeclaration node) {
		return true;
	}
	
	public boolean visit(ParameterizedType node) {
		return true;
	}
	
	public boolean visit(ParenthesizedExpression node) {
		return true;
	}
	
	public boolean visit(PostfixExpression node) {
		return true;
	}
	
	public boolean visit(PrefixExpression node) {
		return true;
	}
	
	public boolean visit(PrimitiveType node) {
		return true;
	}
	
	public boolean visit(QualifiedName node) {
		return true;
	}
	
	public boolean visit(QualifiedType node) {
		return true;
	} 
	
	public boolean visit(ReturnStatement node) {
		
		this.returnStatments.add(node);
		return true;
	}
	
	/*TODO if you want to improve scalability on large projects where you have millions of bindings and nodes, 
	 * you can inverse the way you populate manageBindings
	 * before to populate it, check if this SimpleName is affected in the metamodel 
	 * i.e., if it is present in at least one impacting change (typically the number of changes is not great)
	 * thus you will reduce the size of manageBindings, but you will traverse the list of changes many times
	 * complexity wise it is the same, n_changes*m_bindings, but you can optimize it in the code  
	 */
	
	/* TO take into account the applyed resolutions which may influence the remaining resolutions we have to 
	 * revisit after each resolution the parent node containing the part where the resolution was applied
	 * 
	 * e.g., one expression impacted by push and rename, if push is applied first the rename my not be applied 
	 * */
	public boolean visit(SimpleName node) {//TODO if the binding treatment is needed elsewhere, then extract it in a new method
		
		//System.out.println("SimpleName  class id >>" + node.getIdentifier());
	//System.out.println("SimpleName  class name >>" + node.getFullyQualifiedName());
		
		IBinding binding = node.resolveBinding();
		
		if(!JavaParser.isPrimitiveType(binding)){
			
			if(manageBindings.getBindingsNodes().containsKey(binding)){
				
				/* here get the content object (which is a list of ASTNode) and add this node to it
				 * */
				manageBindings.getBindingsNodes().get(binding).add(node);
		//	System.out.println(" in  exist "+node.getIdentifier());
				
			} else {
				ArrayList<ASTNode> nodes = new ArrayList<ASTNode>();
				nodes.add(node);
				manageBindings.getBindingsNodes().put(binding, nodes);
				//System.out.println(" in  in new addes: "+node.getIdentifier());
			}
		}
		
		return true;
	}
	
	
	public boolean visit(SimpleType node) {
		return true;
	}
	
	public boolean visit(SingleMemberAnnotation node) {
		return true;
	}
	
	public boolean visit(SingleVariableDeclaration node) {
		return true;
	}
	
	public boolean visit(StringLiteral node) {
		return true;
	}
	
	public boolean visit(SuperConstructorInvocation node) {
		return true;
	}
	
	public boolean visit(SuperFieldAccess node) {
		return true;
	}
	
	public boolean visit(SuperMethodInvocation node) {
		return true;
	}
	
	public boolean visit(SuperMethodReference node) {
		return true;
	}
	
	public boolean visit(SwitchCase node) {
		return true;
	}
	
	public boolean visit(SwitchStatement node) {
		return true;
	}
	
	public boolean visit(SynchronizedStatement node) {
		return true;
	}
	
	public boolean visit(TagElement node) {
		return true;
	}
	
	public boolean visit(TextElement node) {
		return true;
	}
	
	public boolean visit(ThisExpression node) {
		return true;
	}
	
	public boolean visit(ThrowStatement node) {
		return true;
	}
	
	public boolean visit(TryStatement node) {
		return true;
	}
	
	public boolean visit(TypeDeclaration node) {
		return true;
	}
	
	public boolean visit(TypeDeclarationStatement node) {
		return true;
	}
	
	public boolean visit(TypeLiteral node) {
		return true;
	}
	
	public boolean visit(TypeMethodReference node) {
		return true;
	}
	
	public boolean visit(TypeParameter node) {
		return true;
	}
	
	public boolean visit(UnionType node) {
		return true;
	}
	
	public boolean visit(VariableDeclarationExpression node) {
		return true;
	}
	
	public boolean visit(VariableDeclarationStatement node) {
		variableDeclarationStatements.add(node);
		return true;
	}
	
	public boolean visit(VariableDeclarationFragment node) {
		return true;
	}
	
	public boolean visit(WhileStatement node) {
		return true;
	}
	
	public boolean visit(WildcardType node) {
		return true;
	}
		
}
