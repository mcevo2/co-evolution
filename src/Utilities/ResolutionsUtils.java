package Utilities;

import java.awt.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Dimension;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

/* TODO treat the case of super calls of methods
 * */
public class ResolutionsUtils {
	
	/* here we add a new import due to use of new class 
	 */
	public static void addMissingImports(CompilationUnit cu,String missingImport, ASTNode node) {
		//TODO here we should add imports to new elements if they are not used

		ASTNode nodeTemp = node;
		while (nodeTemp != null){// && !(nodeTemp instanceof CompilationUnit)) {
			
			if(nodeTemp instanceof CompilationUnit){
				((CompilationUnit) nodeTemp).imports().stream().forEach(i->System.out.println("imports " +i));
				
				ImportDeclaration imp = (ImportDeclaration) node.getAST().createInstance(ImportDeclaration.class);
				QualifiedName qname = (QualifiedName) node.getAST().createInstance(QualifiedName.class);
				
				SimpleName sname = (SimpleName) node.getAST().createInstance(SimpleName.class);
				SimpleName pname = (SimpleName) node.getAST().createInstance(SimpleName.class);
				sname.setIdentifier(missingImport);
				pname.setIdentifier("testAPI");//TODO, here recup�re the 
				
				qname.setName(sname);
				qname.setQualifier(pname);
				imp.setName(qname);
				
				//ResolutionsUtils.replaceDirectExpressionInParentNode(nodeTemp, method);
				//nodeTemp.delete();
				AST ast2 = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast2);

				//  IPath pathcu = cu.getJavaElement().getPath();
			 
				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					 ListRewrite lrw1 = rewriter1.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY);
			         lrw1.insertLast(imp, null);
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

	/* here we replace x.y.z.deletedPro.w... by x.y.z.w... in its parent node 
	 */
	public static void replaceDirectExpressionInParentNode(CompilationUnit cu,ASTNode oldExp, ASTNode newExp){
		
		ReplaceDirectExpression.replaceExpressionInParentNode(cu,oldExp, newExp);
	}
	
	/* here we replace x.y.z.deletedPro.w... by nothing (we remove it) in its parent node 
	 */
	public static void replaceDeleteAllCallPathExpressionInParentNode(CompilationUnit cu,ASTNode oldExp){
		System.out.println(" CASE OF PROPERTY replaceDeleteAllCallPathExpressionInParentNode");
		DeleteAllPathCall.replaceExpressionInParentNode( cu,oldExp);
	}
	
	/* here we find the type of an expression and return a default value for it
	 */ 
	public static ASTNode getDefaultValue4TypeOfExpression(CompilationUnit cu,ASTNode exp){
		
		AST ast = exp.getAST();
		
		if(exp instanceof QualifiedName){
			
			if(((QualifiedName)exp).resolveTypeBinding().isPrimitive()){
				return generatePrimitiveDefaultValue(((QualifiedName)exp).resolveTypeBinding().getName(), ast);
			} else {
				return generateDefaultValue(((QualifiedName)exp).resolveTypeBinding().getName(), ast, 
						((QualifiedName)exp).resolveTypeBinding().isParameterizedType(), 
						((QualifiedName)exp).resolveTypeBinding().isArray(), 
						((QualifiedName)exp).resolveTypeBinding().getDimensions());
			}
			

		} else if(exp instanceof MethodInvocation){
			if( ASTManager.findReturnStatment(exp)!=null)
			{
				AST ast2 = cu.getAST();
				ASTRewrite rewriter1 = ASTRewrite.create(ast2);

				//  IPath pathcu = cu.getJavaElement().getPath();

				Document document=null;
				ICompilationUnit iCompilUnit=(ICompilationUnit) cu.getJavaElement();
				try {
					document = new Document(iCompilUnit.getSource());
					
					rewriter1.remove(ASTManager.findReturnStatment(exp), null);  

					TextEdit edits = rewriter1.rewriteAST(document, null);
				
					SaveModification.SaveModif(cu, edits);
				}
				catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else
			if(((MethodInvocation)exp).resolveTypeBinding().isPrimitive()){
				return generatePrimitiveDefaultValue(((MethodInvocation)exp).resolveTypeBinding().getName(), ast);
			} else {
				return generateDefaultValue(((MethodInvocation)exp).resolveTypeBinding().getName(), ast, 
						((MethodInvocation)exp).resolveTypeBinding().isParameterizedType(), 
						((MethodInvocation)exp).resolveTypeBinding().isArray(), 
						((MethodInvocation)exp).resolveTypeBinding().getDimensions());
			}
			
		
		} else if(exp instanceof FieldAccess){

			if(((FieldAccess)exp).resolveTypeBinding().isPrimitive()){
				return generatePrimitiveDefaultValue(((FieldAccess)exp).resolveTypeBinding().getName(), ast);
			} else {
				return generateDefaultValue(((FieldAccess)exp).resolveTypeBinding().getName(), ast, 
						((FieldAccess)exp).resolveTypeBinding().isParameterizedType(), 
						((FieldAccess)exp).resolveTypeBinding().isArray(), 
						((FieldAccess)exp).resolveTypeBinding().getDimensions());
			}
			
		
		}
		
		return null;
	}
	
	private static ASTNode generatePrimitiveDefaultValue(String typeName, AST ast) {
		//node.getAST().createInstance(EmptyStatement.class)
		if(typeName.equals("byte") || typeName.equals("short") || typeName.equals("int") || typeName.equals("long")
				|| typeName.equals("float") || typeName.equals("double")){
			
			NumberLiteral number = (NumberLiteral) ast.createInstance(NumberLiteral.class);
			number.setToken("0");
			return number;
			
		} else if(typeName.equals("char")){

			CharacterLiteral car = (CharacterLiteral) ast.createInstance(CharacterLiteral.class);
			car.setCharValue('x');
			return car;
			
		} else if(typeName.equals("boolean")){
			
			BooleanLiteral bool = (BooleanLiteral) ast.createInstance(BooleanLiteral.class);
			bool.setBooleanValue(false);
			return bool;
			
		} else if(typeName.equals("void")){
			
			return null;
		}
	     
	    return null;
	}

	private static ASTNode generateDefaultValue(String typeName, AST ast, boolean isParameterized, boolean isArray, int dimensions){
		
		ClassInstanceCreation newInstance = (ClassInstanceCreation) ast.createInstance(ClassInstanceCreation.class);
		SimpleType stype = (SimpleType) ast.createInstance(SimpleType.class);
		SimpleName sname = (SimpleName) ast.createInstance(SimpleName.class);
		
		if(typeName.equals("String")){
			
			//String s = new String("");
			sname.setIdentifier("String");
			stype.setName(sname);
			newInstance.setType(stype);
			StringLiteral st = (StringLiteral) ast.createInstance(StringLiteral.class);
			st.setLiteralValue("");
			newInstance.arguments().add(st);
			return newInstance;
			
		} else if(typeName.equals("Boolean")){
			
			//Boolean b = new Boolean("false");
			sname.setIdentifier("Boolean");
			stype.setName(sname);
			newInstance.setType(stype);
			BooleanLiteral bb = (BooleanLiteral) ast.createInstance(BooleanLiteral.class);
			bb.setBooleanValue(false);
			newInstance.arguments().add(bb);
			return newInstance;
			
			
		} else if(typeName.equals("Byte")){
			
			//Byte bb = new Byte("0");
			sname.setIdentifier("Byte");
			stype.setName(sname);
			newInstance.setType(stype);
			StringLiteral st = (StringLiteral) ast.createInstance(StringLiteral.class);
			st.setLiteralValue("0");
			newInstance.arguments().add(st);
			return newInstance;
			
		} else if(typeName.equals("Short")){
			
			//Short sh = new Short("0");
			sname.setIdentifier("Short");
			stype.setName(sname);
			newInstance.setType(stype);
			StringLiteral st = (StringLiteral) ast.createInstance(StringLiteral.class);
			st.setLiteralValue("0");
			newInstance.arguments().add(st);
			return newInstance;
			
		} else if(typeName.equals("Character")){
			
			//Character c = new Character('x');
			sname.setIdentifier("Character");
			stype.setName(sname);
			newInstance.setType(stype);
			CharacterLiteral car = (CharacterLiteral) ast.createInstance(CharacterLiteral.class);
			car.setCharValue('x');
			newInstance.arguments().add(car);
			return newInstance;
			
		} else if(typeName.equals("Integer")){
			
			//Integer i =new Integer(0);
			sname.setIdentifier("Integer");
			stype.setName(sname);
			newInstance.setType(stype);
			NumberLiteral number = (NumberLiteral) ast.createInstance(NumberLiteral.class);
			number.setToken("0");
			newInstance.arguments().add(number);
			return newInstance;
			
		} else if(typeName.equals("Long")){
			
			//Long l = new Long(0);
			sname.setIdentifier("Long");
			stype.setName(sname);
			newInstance.setType(stype);
			NumberLiteral number = (NumberLiteral) ast.createInstance(NumberLiteral.class);
			number.setToken("0");
			newInstance.arguments().add(number);
			return newInstance;
			
		} else if(typeName.equals("Float")){
			
			//Float f = new Float(0);
			sname.setIdentifier("Float");
			stype.setName(sname);
			newInstance.setType(stype);
			NumberLiteral number = (NumberLiteral) ast.createInstance(NumberLiteral.class);
			number.setToken("0");
			newInstance.arguments().add(number);
			return newInstance;
			
		} else if(typeName.equals("Double")){
			
			//Double d = new Double(0);
			sname.setIdentifier("Double");
			stype.setName(sname);
			newInstance.setType(stype);
			NumberLiteral number = (NumberLiteral) ast.createInstance(NumberLiteral.class);
			number.setToken("0");
			newInstance.arguments().add(number);
			return newInstance;
			
		} else if(typeName != null && !typeName.equals("") && !isParameterized && !isArray){//this treat classes only 
			
			//TODO here maybe check if there is a basic constructor, otherwise find how many arguments and their type to initialize them
			//here it is a class type in the library/metamodel;
			System.out.println(" in  the problemmmmmm "+typeName);
			sname.setIdentifier(typeName);
			stype.setName(sname);
			newInstance.setType(stype);
			
//			NumberLiteral number = (NumberLiteral) ast.createInstance(NumberLiteral.class);
//			number.setToken("0");
//			newInstance.arguments().add(number);
			return newInstance;
			
		} else if(typeName != null && !typeName.equals("") && isParameterized){//TODO here treat  parameterized types only 
			//int[] itab = new int[0];
			//ArrayList[] atab = new ArrayList[0];
			
			//here we deal with parameterized type, likely collection<T> etc;
//			sname.setIdentifier(typeName.split("<")[0]);
//			stype.setName(sname);
//			ParameterizedType paramType = (ParameterizedType) ast.createInstance(ParameterizedType.class);
//			paramType.setType(stype);
			
			
			
//			for(ITypeBinding typeParam : typeParams){ 
//				//TODO it does not work, but it should be fine to deal with the main type , like from Type<x,y<z<w>>>() to Type()
//				//TODO but might not work with special parameterized classes that do not support a basic constructor without the type parameters
//				SimpleType tempstype = (SimpleType) ast.createInstance(SimpleType.class);
//				SimpleName tempsname = (SimpleName) ast.createInstance(SimpleName.class);
//				tempsname.setIdentifier(typeParam.getName());
//				tempstype.setName(tempsname);
//				paramType.typeArguments().add(tempstype);
//			}
			
			newInstance.setType(generateParamType(typeName, ast));
			return newInstance;
			
		}  else if(typeName != null && !typeName.equals("") && isArray){//TODO treat the array type, if dimensions are present Type[]
			//int[] itab = new int[0];
			//ArrayList[] atab = new ArrayList[0];
			ArrayCreation arrayCre = (ArrayCreation) ast.createInstance(ArrayCreation.class);
			ArrayType paramType = (ArrayType) ast.createInstance(ArrayType.class);
			
			//generatePrimitiveDefaultValue
			if(PrimitiveType.toCode(typeName.split("\\[")[0]) != null){
				
				PrimitiveType pt = (PrimitiveType) ast.createInstance(PrimitiveType.class);
				pt.setPrimitiveTypeCode(PrimitiveType.toCode(typeName.split("\\[")[0]));
				paramType.setElementType(pt);
				
//			} else if(typeName.split("\\[")[0].contains("<")){//TODO treat ayrray of parameterized type 
//				
//				paramType.setElementType(generateParamType(typeName.split("\\[")[0], ast));
//				
			} else {
				
				sname.setIdentifier(typeName.split("\\[")[0]);
				stype.setName(sname);
				paramType.setElementType(stype);
			}
			
			//here we remove them cuz there is already one dimension by default
			paramType.dimensions().removeAll(paramType.dimensions());
			arrayCre.dimensions().removeAll(arrayCre.dimensions());
			System.out.println("dimensions dimensions: " + dimensions );
			for(int i = 0; i < dimensions; i++){//by defalut paramType has one dimension 
				Dimension dim = (Dimension) ast.createInstance(Dimension.class);
				paramType.dimensions().add(dim);
				//dim.se
				System.out.println("<<<<dimensions: " + i );
				NumberLiteral number = (NumberLiteral) ast.createInstance(NumberLiteral.class);
				number.setToken("0");
				arrayCre.dimensions().add(number);
			}
			
			arrayCre.setType(paramType);
			return arrayCre;
		}
		
		return null;
	}
	
	private static Type generateParamType(String typeName, AST ast){
		
		SimpleType stype = (SimpleType) ast.createInstance(SimpleType.class);
		SimpleName sname = (SimpleName) ast.createInstance(SimpleName.class);
		ParameterizedType paramType = (ParameterizedType) ast.createInstance(ParameterizedType.class);
		
		
		String pattern = "([^<]*)(<)(.+)(>)";//([^>]*)
		
		Pattern r = Pattern.compile(pattern);
		
		Matcher m = r.matcher(typeName);
		
		if (m.find( )) {
			
			System.out.println();
			System.out.println("Found value: " + m.group(0) );
			System.out.println("Found value: " + m.group(1) );
			System.out.println("Found value: " + m.group(2) );
			System.out.println("Found value: " + m.group(3) );
			System.out.println("Found value: " + m.group(4) );
			
			sname.setIdentifier(m.group(1));
			stype.setName(sname);
			paramType.setType(stype);
			
			String foundParam = "";
			boolean bool = false;
			for(String st : m.group(3).split(",")){
				System.out.println(st);
				
				if(st.contains("<") && st.contains(">") && !bool){//here we are in the parameter types between <>
					
					paramType.typeArguments().add(generateParamType(st,ast));
					
				} else if(st.contains("<") && !st.contains(">")){//here we are in the parameter types between <>
					
					if(foundParam == "") foundParam += st;
					else foundParam += ","+ st;
					bool = true;
					
				} else if(!st.contains("<") && st.contains(">")){
					
					foundParam += ","+ st;
					System.out.println(foundParam);
					paramType.typeArguments().add(generateParamType(foundParam,ast));
					bool = false;
					
				} else if(!st.contains("<") && !st.contains(">") && !bool) {
					
					SimpleType tempstype = (SimpleType) ast.createInstance(SimpleType.class);
					SimpleName tempsname = (SimpleName) ast.createInstance(SimpleName.class);
					tempsname.setIdentifier(st);
					tempstype.setName(tempsname);
					paramType.typeArguments().add(tempstype);

				} else if(!st.contains("<") && !st.contains(">") &&bool) {
					foundParam += ","+ st;
				} 
				
			}
			
			//testParseParameterizedTypes(m.group(3).split(",")[m.group(3).split(",").length-1]);
			
	      }else {
	         System.out.println("NO MATCH");
	      }
		
		return paramType;
	}
	
}
