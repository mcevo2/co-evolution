package Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NameQualifiedType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.ui.text.TypingRun.ChangeType;
import org.eclipse.jface.text.Document;

import fr.lip6.meta.ComplexChangeDetection.Change;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.ChangeUpperBound;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.DeleteClass;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.DeleteProperty;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.RenameClass;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.RenameProperty;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.SetProperty;
import fr.lip6.meta.ComplexChangeDetection.ComplexChanges.ComplexChange;
import fr.lip6.meta.ComplexChangeDetection.ComplexChanges.ExtractClass;
import fr.lip6.meta.ComplexChangeDetection.ComplexChanges.MoveProperty;
import fr.lip6.meta.ComplexChangeDetection.ComplexChanges.PushProperty;



public class UsesManager {
	public static Usage classify(ArrayList<Change> myChanges, IMarker error,CompilationUnit cu)
	{
		Usage usage=new Usage();
		ASTNode errornode = ASTManager.getErrorNode(cu, error);
		usage.setNode(errornode);
		usage.setError(error); 

		System.out.println(" THe ERROR NODE "+ errornode +" type of class  "+errornode.getClass());
		for(Change change : myChanges) {
			System.out.println(" change en cours is "+ change);
			if(errornode instanceof SimpleName) {
				System.out.println(" in simplename ");
				if (change instanceof RenameClass)
				{
					if(((SimpleName)errornode).getIdentifier().equals(((RenameClass)change).getName()))
					{

						usage.getPatterns().add((UsagePattern.TypeUseRename));
						usage.setChange(change);

					}
					else 
						if(((SimpleName)errornode).getIdentifier().equals("get"+((RenameClass)change).getName()))
						{
							usage.getPatterns().add((UsagePattern.getObjectRename));
							usage.setChange(change);

						}
					if(((SimpleName)errornode).getIdentifier().equals("set"+((RenameClass)change).getName()))
					{
						usage.getPatterns().add((UsagePattern.setObjectRename));
						usage.setChange(change);
					}
					if( ASTManager.findMethodInvocation(errornode)!=null) {

						MethodInvocation mi= (MethodInvocation) ASTManager.findMethodInvocation(errornode);

						if(mi.arguments().size()==0 && ((SimpleName)errornode).getIdentifier().equals("create"+((RenameClass)change).getName()))
						{

							System.out.println(" in CREATE OBJ "+errornode);
							usage.getPatterns().add((UsagePattern.createObjectRename));
							usage.setChange(change);
						}
					}

					if(ASTManager.isLiteral(errornode))
					{
						String nodename=((SimpleName)errornode).getIdentifier();
						String changename=ASTManager.makeLiteral(((RenameClass) change).getName());
						if(nodename.contains(changename))
						{
							usage.getPatterns().add((UsagePattern.LiteralRename));
							usage.setChange(change);
						}

					}

				}
				if (change instanceof RenameProperty)
				{
					if(((SimpleName)errornode).getIdentifier().equals(((RenameProperty)change).getName()))
					{

						System.out.println(" in DIRECT ACCESS OF ATTRIBUTE "+errornode);
						usage.getPatterns().add((UsagePattern.PropertyRename));
						usage.setChange(change);
					}
					if(((SimpleName)errornode).getIdentifier().equals("get"+capitalizeFirstLetter(((RenameProperty)change).getName())))
					{
						//System.out.println(" in GET ATTRIBUTE "+errornode);
						usage.getPatterns().add((UsagePattern.getPropertyRename));
						usage.setChange(change);
					}
					if(((SimpleName)errornode).getIdentifier().equals("set"+capitalizeFirstLetter(((RenameProperty)change).getName())))
					{
						System.out.println(" in SET ATTRIBUTE "+errornode);

						usage.getPatterns().add((UsagePattern.setPropertyRename));
						usage.setChange(change);
					}
					if(((SimpleName)errornode).getIdentifier().contains("get"+((RenameProperty)change).getClassName()+"_"+((RenameProperty)change).getName()))
					{
						//System.out.println(" in GET CLASS ATTRIBUTE "+errornode);
						usage.getPatterns().add((UsagePattern.getClass_Attribute));
						usage.setChange(change);
					}

					if(ASTManager.isLiteral(errornode))
					{
						String changeLiteral =ASTManager.makeLiteral(((RenameProperty)change).getName());

						if(((SimpleName)errornode).getIdentifier().contains(changeLiteral))
						{
							usage.getPatterns().add((UsagePattern.LiteralRename));
							usage.setChange(change);
						}
					}
					System.out.println("here is your maj string ot contaijns000 "+((SimpleName)errornode).getIdentifier()  +"  fhgg "+ASTManager.containOnlyCapitals(((SimpleName)errornode).getIdentifier()));

					if(ASTManager.containOnlyCapitals(((SimpleName)errornode).getIdentifier())) {
						String errorid=((SimpleName)errornode).getIdentifier();
						errorid=errorid.toLowerCase();
						errorid=errorid.replace("_", "");
						RenameProperty rp=((RenameProperty)change);
						System.out.println("here is your maj string ot contaijns "+rp.getClassName()+rp.getName());

						if(errorid.equals(rp.getClassName().toLowerCase()+rp.getName().toLowerCase()))
						{
							if(!(usage.getPatterns().contains(UsagePattern.LiteralRename) )) {
								
							usage.getPatterns().add((UsagePattern.LiteralRename));
							usage.setChange(change);
							}
						}

					}


				}
				if(change instanceof DeleteClass)
				{
					System.out.println(" In Delete Class ");
					if(((SimpleName)errornode).getIdentifier().equals(((DeleteClass)change).getName()))
					{
						System.out.println(" In 1 ");
						if(ASTManager.isReturnType(errornode) )
						{
							System.out.println(" In 2 ");
							if(((MethodDeclaration) ASTManager.findMethodDeclaration(errornode)).resolveBinding().getName().matches("getOcl"+((SimpleName)errornode).getIdentifier()) ||((MethodDeclaration) ASTManager.findMethodDeclaration(errornode)).resolveBinding().getName().matches("get"+((SimpleName)errornode).getIdentifier()) ) {
								System.out.println(" In GET TYPE");
								usage.getPatterns().add((UsagePattern.GetObjectDelete));
								usage.setChange(change);

							}
							else {
								System.out.println(" In return type usage ");
								System.out.println(" In 3 ");
								usage.getPatterns().add((UsagePattern.ReturnTypeDelete));
								usage.setChange(change);
							}
						}
						else 
							if(ASTManager.findParameterInMethodDeclaration(usage.getNode())!=null)
							{
								if(((MethodDeclaration) ASTManager.findMethodDeclaration(errornode)).resolveBinding().getName().matches("visit"+((SimpleName)errornode).getIdentifier())) {

									usage.getPatterns().add((UsagePattern.VisitClassMethodDelete));
									usage.setChange(change);

								}
								else {
									System.out.println( " the parent of the node in the for is "+ASTManager.findParameterInMethodDeclaration(usage.getNode()).getParent());
									if(! (ASTManager.findParameterInMethodDeclaration(usage.getNode()).getParent() instanceof EnhancedForStatement)) {

										usage.getPatterns().add((UsagePattern.parameterDelete));
										usage.setChange(change);
									}
								}
							}

					}



					if(ASTManager.isSuperClass(errornode,((DeleteClass) change).getName()))//Check if the super class error matches the change
					{
						usage.getPatterns().add((UsagePattern.SuperClassDelete)); 
						usage.setChange(change);
					}
					if(((SimpleName)errornode).getIdentifier().equals(((DeleteClass)change).getName()))
					{


						if(ASTManager.findFieldOrVariableDeclarations(errornode)!=null && ASTManager.findSingleVariableDeclaration(errornode)==null)
						{
							if ( ASTManager.findSingleVariableDeclaration(errornode)!=null)
								System.out.println("===in vd pattern and its a param  ");
							usage.getPatterns().add((UsagePattern.VariableDeclarationDelete));
							usage.setChange(change);
						}

						if(ASTManager.findClassInstanceCreations(errornode)!=null)
						{

							usage.getPatterns().add((UsagePattern.ClassInstanceDelete));
							usage.setChange(change);
						}

					}
					if(ASTManager.isLiteral(errornode))
					{
						String changeLiteral =ASTManager.makeLiteral(((DeleteClass)change).getName());

						if(((SimpleName)errornode).getIdentifier().contains(changeLiteral))
						{
							if(!(usage.getPatterns().contains(UsagePattern.LiteralDelete) )) {
								
							usage.getPatterns().add((UsagePattern.LiteralDelete));
							usage.setChange(change);
							}
							}
					}
					if(((SimpleName)errornode).getIdentifier().equals(((DeleteClass)change).getName())) {
						System.out.println("before ifforwhile ");
						if(ASTManager.findIfWhileForStatement(errornode)!=null)
						{
							System.out.println("before ifforwhile 2");
							if(!(usage.getPatterns().contains(UsagePattern.MethodInvocTypeDelete) || usage.getPatterns().contains(UsagePattern.VariableDeclarationDelete))) {
								System.out.println("before ifforwhile 3");
								usage.getPatterns().add((UsagePattern.ComplexStatementDelete));
								usage.setChange(change);
							}
						}
					}

					if(((SimpleName)errornode).getIdentifier().contains(((DeleteClass)change).getName())) {
						if(ASTManager.findInfixExpression(errornode) !=null)
						{
							//usage.setPattern(UsagePattern.ComplexStatementDelete);
							//usage.setChange(change);
						}
					}
					if(((SimpleName)errornode).getIdentifier().contains(((DeleteClass)change).getName())) {
						if(ASTManager.findMethodInvocation(errornode) !=null && ASTManager.findAssignment(errornode) !=null && !ASTManager.isLiteral(errornode))
						{
							// MethodInvocation in an assignment ==> right side ==> replace it by null;
							usage.getPatterns().add((UsagePattern.MethodInvocTypeDelete));
							usage.setChange(change);
						}
					}
					//	if(((SimpleName)errornode).getIdentifier().equals("create"+ capitalizeFirstLetter(((DeleteClass)change).getName())){

					//}
					// case : MethodInvocation with deleted parameter -- method name has not a relationship with the change
					if( ASTManager.findMethodInvocation(errornode)!=null)
					{
						System.out.println(" ok");
						MethodInvocation mi=(MethodInvocation) ASTManager.findMethodInvocation(errornode);
						if(mi.arguments() !=null)
							System.out.println(" ok 1");
						if (mi.typeArguments() !=null)
						{
							if(mi.typeArguments().isEmpty())
								System.out.println(" ok 2");
						}

						for ( Object o : mi.arguments()) {
							System.out.println(" the arg is "+o.toString() + " its class is  "+o.getClass());
							// To do : case of parameters: methodInvocation,SimpleName....
							if(o instanceof CastExpression)
							{
								if(((SimpleName)((SimpleType) ((CastExpression)o).getType()).getName()).getIdentifier().equals(((DeleteClass)change).getName())){
									usage.getPatterns().add((UsagePattern.parameterInMiDelete));
									usage.setNode((ASTNode)o);
									usage.setChange(change);

								}



							}

						}
					}


				}
				if(change instanceof DeleteProperty)
				{
					String propertyName = capitalizeFirstLetter(((DeleteProperty)change).getName());


					if(((SimpleName)errornode).getIdentifier().equals("get"+propertyName) || ((SimpleName)errornode).getIdentifier().equals("set"+propertyName)|| ((SimpleName)errornode).getIdentifier().equals("is"+propertyName)) {
						System.out.println(" in get/Set pattern "+propertyName);
						usage.getPatterns().add((UsagePattern.getorSetPropertyDelete));
						usage.setChange(change);
					}
					else if(((SimpleName)errornode).getIdentifier().equals(((DeleteProperty)change).getName()))
					{
						usage.getPatterns().add((UsagePattern.PropertyDelete));
						usage.setChange(change);

					}
					if(ASTManager.isLiteral(errornode))
					{

						String changeLiteral =ASTManager.makeLiteral(((DeleteProperty)change).getName());

						if(((SimpleName)errornode).getIdentifier().contains(changeLiteral))

						{
							//System.out.println(" in literal pattern ");
							if(!(usage.getPatterns().contains(UsagePattern.LiteralDelete) )) {
									
							usage.getPatterns().add((UsagePattern.LiteralDelete));
							usage.setChange(change);
						}}
					}
					if(ASTManager.containOnlyCapitals(((SimpleName)errornode).getIdentifier())){
					if( ASTManager.findReturnStatment(errornode)!=null) {
						
							String deletedProp =((DeleteProperty)change).getClassName() +((DeleteProperty)change).getName();
							String errorInRS=((SimpleName)errornode).getIdentifier();
							errorInRS=errorInRS.toLowerCase();
							errorInRS=errorInRS.replace("_", "");
							System.out.println(" you compare "+errorInRS +" with  "+deletedProp);
							if(errorInRS.equals(deletedProp.toLowerCase()))
							{
								if(!(usage.getPatterns().contains(UsagePattern.LiteralDelete) )) {
									
								usage.getPatterns().add((UsagePattern.LiteralDelete));
								usage.setChange(change);
							}}

							
						}
					else if( ASTManager.findSwitchCase(errornode)!=null) {
						String deletedProp =((DeleteProperty)change).getClassName() +((DeleteProperty)change).getName();
						String errorInRS=((SimpleName)errornode).getIdentifier();
						errorInRS=errorInRS.toLowerCase();
						errorInRS=errorInRS.replace("_", "");
						System.out.println(" you compare "+errorInRS +" with  "+deletedProp);
						if(errorInRS.equals(deletedProp.toLowerCase()))
						{
							if(!(usage.getPatterns().contains(UsagePattern.LiteralDelete) )) {
								
							usage.getPatterns().add((UsagePattern.LiteralDelete));
						
						usage.setChange(change);
							}}
					}
						
					}
				}
				else if(change instanceof MoveProperty)
				{
					String property =capitalizeFirstLetter(((MoveProperty)change).getName());

					//System.out.println(" You check   "+"get"+property);
					if(((SimpleName)errornode).getIdentifier().equals("get"+property) || ((SimpleName)errornode).getIdentifier().equals("set"+property)) {
						//System.out.println(" You check  one of 2 cases");
						if(((MoveProperty)change).getUpperBound()==-1)
						{
							//System.out.println(" You check  in move prov case -1");
							usage.getPatterns().add((UsagePattern.MoveProperty));
							usage.setChange(change);
						}
						else
						{
							//System.out.println(" You check  move prop not -1 ");
							usage.getPatterns().add((UsagePattern.GetorSetMoveProperty));	
							usage.setChange(change);
						}

					}
					else  if(ASTManager.containOnlyCapitals(((SimpleName)errornode).getIdentifier()))
					{
						//System.out.println(" in9999  "+((SimpleName)errornode).getIdentifier()+ "  22222  "+(((MoveProperty)change).getSourceClassName().toUpperCase()+"__"+ASTManager.makeLiteral(((MoveProperty)change).getName())));
						if((((SimpleName)errornode).getIdentifier()).equals((((MoveProperty)change).getSourceClassName().toUpperCase()+"__"+ASTManager.makeLiteral(((MoveProperty)change).getName())))) {
							System.out.println(" in same move prop");
							if(ASTManager.isLiteral(errornode)&& ((SimpleName)errornode).getIdentifier().equals(((MoveProperty)change).getSourceClassName().toUpperCase()+"__"+ASTManager.makeLiteral(((MoveProperty)change).getName())))
							{
								usage.getPatterns().add((UsagePattern.LiteralRename));
								usage.setChange(change);

							}
							else

							{

								System.out.println(" Move special case");
								usage.getPatterns().add((UsagePattern.MoveProperty));
								usage.setChange(change);
							}
						}
					}
				}
				else if (change instanceof ExtractClass) {
					if(ASTManager.containOnlyCapitals(((SimpleName)errornode).getIdentifier()))
					{
						ArrayList<ComplexChange> moves = ((ExtractClass)change).getMoves();
						for( ComplexChange cc :moves) {
							if( cc instanceof MoveProperty) {
								MoveProperty mp= (MoveProperty)cc;

								System.out.println(" inEXTRACT9999  "+((SimpleName)errornode).getIdentifier()+ "  22222  "+(mp.getSourceClassName().toUpperCase()+"__"+ASTManager.makeLiteral(mp.getName())));
								if((((SimpleName)errornode).getIdentifier()).equals((mp.getSourceClassName().toUpperCase()+"__"+ASTManager.makeLiteral(mp.getName())))) {
									System.out.println(" in same move prop");
									
										usage.getPatterns().add((UsagePattern.LiteralRename));
										usage.setChange(mp);

									
									/*else

								{

									System.out.println(" Move special case");
									usage.getPatterns().add((UsagePattern.MoveProperty));
									usage.setChange(change);
								}*/
								}
							}
						}
					}
				}
					else if( change instanceof PushProperty)
					{

						if(((SimpleName)errornode).getIdentifier().equals((((PushProperty)change).getName())))
						{
							usage.getPatterns().add((UsagePattern.propertyPush));
							usage.setChange(change);
						}
						if(((SimpleName)errornode).getIdentifier().equals("get"+capitalizeFirstLetter(((PushProperty)change).getName())) ||((SimpleName)errornode).getIdentifier().equals("is"+capitalizeFirstLetter(((PushProperty)change).getName())) )
						{

							if(ASTManager.findMethodDeclaration(errornode)!=null)

								usage.getPatterns().add((UsagePattern.getPropertyPush));
							usage.setChange(change);
						}
						else if(((SimpleName)errornode).getIdentifier().equals("set"+capitalizeFirstLetter(((PushProperty)change).getName())))
						{
							usage.getPatterns().add((UsagePattern.setPropertyPush));
							usage.setChange(change);
						}
						if(ASTManager.isLiteral(errornode)&& ((SimpleName)errornode).getIdentifier().equals(ASTManager.makeLiteral(((PushProperty)change).getSuperClassName())+"__"+(ASTManager.makeLiteral(((PushProperty)change).getName()))))
						{
							System.out.println(" IN PUSH goal literal");
							usage.getPatterns().add((UsagePattern.LiteralPush));	
							usage.setChange(change);
						}

					}
					else if(change instanceof ChangeUpperBound)
					{
						MethodInvocation mi= (MethodInvocation) ASTManager.findMethodInvocation(errornode);
						ChangeUpperBound c= (ChangeUpperBound) change;

						if(mi!=null )
						{
							for(Object o : mi.arguments())
							{
								if(o instanceof MethodInvocation)
								{

									if(((MethodInvocation)o).resolveTypeBinding().getName().equals("List<"+c.getClassName()+">"))
									{

										usage.getPatterns().add((UsagePattern.GeneralizeProperty));	
										usage.setChange(change);
										usage.setNode((ASTNode)o);
									}
								}
							}
						}

						if(mi.getExpression()!=null ) {
							if(mi.getExpression().resolveTypeBinding().getName().contains(c.getClassName())) {
								usage.getPatterns().add((UsagePattern.GeneralizePropertyBefore));	
								usage.setChange(change);

							}
						}

					}
					if( change instanceof ExtractClass) {
						ArrayList<ComplexChange> moves = ((ExtractClass)change).getMoves();
						for( ComplexChange cc :moves) {
							if( cc instanceof MoveProperty) {
								MoveProperty mp= (MoveProperty)cc;
								String propefter=mp.getName();
								String Prop =capitalizeFirstLetter(mp.getName());

								if(((SimpleName)errornode).getIdentifier().equals("get"+Prop) || ((SimpleName)errornode).getIdentifier().equals("set"+Prop)) {
									usage.getPatterns().add((UsagePattern.GetorSetMoveProperty));	
									usage.setChange(cc);
								}



							}

						}

					}


				}
				else if(errornode instanceof QualifiedName)
				{ 

					if(ASTManager.checkImportDeclaration(errornode)  )
					{

						List<ASTNode > childrennodes=ASTManager.getChildren(errornode);


						for (int i = 0; i < childrennodes.size(); i++) {
							ASTNode n= childrennodes.get(i);
							if( n instanceof SimpleName)
							{


								if( change instanceof RenameClass && ((SimpleName)n).getIdentifier().equals(((RenameClass)change).getName()) )
								{
									usage.getPatterns().add((UsagePattern.ImportRename));
									usage.setChange(change);

								}
								else if( change instanceof DeleteClass && ((SimpleName)n).getIdentifier().equals(((DeleteClass)change).getName()) )
								{



									usage.getPatterns().add((UsagePattern.ImportDelete));
									usage.setChange(change);




								} 
							}
						}

					}


				}
				else  if (errornode instanceof NameQualifiedType) {
					if ( ASTManager.findParameterInMethodDeclaration(errornode)!=null)
					{

						List<ASTNode > childrennodes=ASTManager.getChildren(errornode);


						for (int i = 0; i < childrennodes.size(); i++) {

							ASTNode n= childrennodes.get(i);
							if( n instanceof SimpleName  )

							{
								String id =((SimpleName)n).getIdentifier();

								if(change instanceof DeleteClass && ((SimpleName)n).getIdentifier().equals(((DeleteClass)change).getName()) )
								{
									ASTNode parentnode =(ASTManager.findParameterInMethodDeclaration(errornode)).getParent() ;
									if( parentnode instanceof MethodDeclaration &&  ((MethodDeclaration)parentnode).resolveBinding().getName().equals("visit"+id) )
									{
										usage.getPatterns().add((UsagePattern.VisitClassMethodDelete));
										usage.setChange(change);
										System.out.println(" QNT PARAM 1111 ");
									}
									else if( parentnode instanceof MethodDeclaration &&  ((MethodDeclaration)parentnode).resolveBinding().getName().equals("get"+id) )
									{
										usage.getPatterns().add((UsagePattern.GetObjectDelete));
										usage.setChange(change);
										System.out.println(" QNT PARAM 22222 ");
									} 

								}
							}

						}
						if( change instanceof RenameClass)
						{
							for (int i = 0; i < childrennodes.size(); i++) {

								ASTNode n= childrennodes.get(i);
								if( n instanceof SimpleName  &&  ((SimpleName)n).getIdentifier().equals(((RenameClass)change).getName()) )

								{
									usage.getPatterns().add((UsagePattern.QualifiedTypeRename));
									usage.setChange(change);



								}
							}



						}

					}
				}else if( errornode instanceof MethodDeclaration )
				{
					MethodDeclaration md =((MethodDeclaration)errornode);
					String mn=	md.getName().getIdentifier();
					if( change instanceof DeleteProperty ) {
						String methodName =capitalizeFirstLetter(((DeleteProperty)change).getName());
						if (((MethodDeclaration)errornode).resolveBinding().getName().equals(((DeleteProperty)change).getName()))
						{
						
							usage.getPatterns().add((UsagePattern.PropertyDelete));	
							usage.setChange(change);
						}
						else if(((MethodDeclaration)errornode).resolveBinding().getName().equals("get"+methodName) || ((MethodDeclaration)errornode).resolveBinding().getName().equals("set"+methodName)) {
						
							usage.getPatterns().add((UsagePattern.getorSetPropertyDelete));
							usage.setChange(change);
						}
					}
					else if (  change instanceof MoveProperty )
					{
						String methodName=((MoveProperty)change).getName();
						if(((MethodDeclaration)errornode).resolveBinding().getName().equals("get"+methodName) || ((MethodDeclaration)errornode).resolveBinding().getName().equals("set"+methodName)) {
						
							usage.getPatterns().add((UsagePattern.GetorSetMoveProperty));
							usage.setChange(change);
						}
						else {
							usage.getPatterns().add((UsagePattern.MoveProperty));
							usage.setChange(change);
						}
					}
					else if( change instanceof RenameProperty)


					{

						if(mn.equals("get"+capitalizeFirstLetter(((RenameProperty)change).getName())))
						{
							
							usage.setNode(md.getName());
							usage.getPatterns().add((UsagePattern.getPropertyRename));
							usage.setChange(change);
						}
						if(mn.contains("set"+capitalizeFirstLetter(((RenameProperty)change).getName())))
						{
							//System.out.println(" in SET ATTRIBUTE "+errornode);
							usage.setNode(md.getName());
							usage.getPatterns().add((UsagePattern.setPropertyRename));
							usage.setChange(change);
						}
						if(mn.contains("is"+capitalizeFirstLetter(((RenameProperty)change).getName())))
						{
							//System.out.println(" in SET ATTRIBUTE "+errornode);
							usage.setNode(md.getName());
							usage.getPatterns().add((UsagePattern.isPropertyRename));
							usage.setChange(change);
						}
					}
					else if ( change instanceof PushProperty)
					{
						if(mn.equals("get"+capitalizeFirstLetter(((PushProperty)change).getName()))||mn.equals("is"+capitalizeFirstLetter(((PushProperty)change).getName())) ||mn.equals("set"+capitalizeFirstLetter(((PushProperty)change).getName())) ) {
							usage.setNode(md.getName());
							usage.getPatterns().add((UsagePattern.PropertyPushMD));
							usage.setChange(change);
						}
					}
					else if( change instanceof SetProperty)
					{

					}

				}else if( errornode instanceof MethodInvocation){
					if (change instanceof ChangeUpperBound )
					{

						ChangeUpperBound c= (ChangeUpperBound) change;
						MethodInvocation tempmi=((MethodInvocation)errornode);
						boolean stop=false;

						while(tempmi.arguments().size()!=0 && !stop) {
							if(tempmi.resolveTypeBinding()!=null)
							{
								if(tempmi.resolveTypeBinding().getName().equals("List<"+c.getClassName()+">")) {
									usage.getPatterns().add((UsagePattern.GeneralizeProperty));	
									usage.setChange(change);
									usage.setNode(errornode);
									stop=true;
								}
							}
							else {
								boolean foundone=false;
								for(Object o : tempmi.arguments())
								{
									if(o instanceof MethodInvocation) {
										tempmi= (MethodInvocation) o;
										foundone=true;
									}

								}
								if (!foundone) stop =true;

							}

						}

						if(tempmi.resolveTypeBinding().getName().equals("List<"+c.getClassName()+">")) {
							usage.getPatterns().add((UsagePattern.GeneralizeProperty));	
							usage.setChange(change);
							usage.setNode(errornode);
						}


					}

					else if ( change instanceof SetProperty) {
						
						if(errornode instanceof MethodInvocation)
						{
							
							MethodInvocation mi= (MethodInvocation) errornode;
							SetProperty c = (SetProperty) change;

							if(mi.resolveTypeBinding() !=null) {
								
								if(mi.resolveTypeBinding().getName().equals(c.getNewType())) {
								
									usage.getPatterns().add((UsagePattern.ChangeType));	
									usage.setChange(change);
									usage.setNode(errornode);
								}
							}
						}


					}
				}
			}

			return usage;

		}

		public static  ArrayList<Usage> getUsages(Change change, ArrayList<IMarker> errors,CompilationUnit cu)
		{
			ArrayList<Usage> usages= new ArrayList<Usage>();
			Usage usage=null;
			for( IMarker error: errors)
			{

				//usage= classify(change,error,cu );
				//System.out.println(" here is an error in classify "+usage.node);
				usages.add(usage);
			}



			return usages;

		}
		public static  Map<Change,  ArrayList<Usage> >  changesUsages(ArrayList<Change> changes, ArrayList<IMarker> errors, CompilationUnit cu)
		{
			HashMap<Change,  ArrayList<Usage> >  myUsages = new HashMap<Change,  ArrayList<Usage> >();
			for(Change c : changes){
				//myUsages.put(c,getUsages(c, errors,cu));
			}

			return myUsages;

		}
		public static String capitalizeFirstLetter(String s)
		{

			String firstLetStr = s.substring(0, 1);
			// Get remaining letter using substring
			String remLetStr = s.substring(1);

			// convert the first letter of String to uppercase
			firstLetStr = firstLetStr.toUpperCase();

			// concantenate the first letter and remaining string
			return firstLetStr + remLetStr;
		}


	}
