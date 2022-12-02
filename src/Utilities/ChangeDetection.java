package Utilities;

import java.util.ArrayList;

import fr.lip6.meta.ComplexChangeDetection.Change;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.AddClass;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.AddProperty;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.AddSuperType;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.ChangeLowerBound;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.ChangeUpperBound;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.DeleteClass;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.DeleteProperty;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.DeleteSuperType;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.RenameClass;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.RenameProperty;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.SetProperty;
import fr.lip6.meta.ComplexChangeDetection.ComplexChanges.ComplexChange;
import fr.lip6.meta.ComplexChangeDetection.ComplexChanges.ExtractClass;
import fr.lip6.meta.ComplexChangeDetection.ComplexChanges.ExtractSuperClass;
import fr.lip6.meta.ComplexChangeDetection.ComplexChanges.MoveProperty;
import fr.lip6.meta.ComplexChangeDetection.ComplexChanges.PullProperty;
import fr.lip6.meta.ComplexChangeDetection.ComplexChanges.PushProperty;

public class ChangeDetection {
	public static ArrayList<Change> initializeChangements()
	{
		ArrayList<Change> changes = new ArrayList<Change>();
		ArrayList<String> subclasses1 = new ArrayList<String>();
		subclasses1.add("NamedElement");
		
		PullProperty pp1 = new PullProperty("ownedAnnotation", "Element", subclasses1);
		changes.add(pp1);
		
		ArrayList<String> subclasses2 = new ArrayList<String>();
		subclasses2.add("Namespace");

		PushProperty pup1 = new PushProperty("ownedRule", "NamedElement", subclasses2); 
		
		changes.add(pup1);
		
		ArrayList<String> subclasses3 = new ArrayList<String>();
		subclasses3.add("Library");
		
		PushProperty pup2 = new PushProperty("ownedPrecedence", "Package", subclasses3);
		
		changes.add(pup2);
		
		RenameClass rc1 = new RenameClass("ClassifierType", "Metaclass", "");
		
		changes.add(rc1);
		

		RenameProperty rp1 = new RenameProperty("valueExpression", "expressionInOCL", "OpaqueExpression");		
		RenameProperty rp2 = new RenameProperty("resolveSelfType", "specializeIn", "Type"); //has an added param (selfType expr)
		RenameProperty rp3 = new RenameProperty("resolveSelfType", "specializeIn", "SelfType"); //has an added param (selfType expr)
		RenameProperty rp4 = new RenameProperty("association", "associationClass", "Property");
		
		RenameProperty rp5 = new RenameProperty("CompatibleInitialiser", "CompatibleDefaultExpression", "Porperty");//this is in the metamodel, btu weirdly, in the code they changed it manually, see change below in case you need it rather than this one
		//RenameProperty rp5 = new RenameProperty("validateCompatibleInitialiser", "validateCompatibleDefaultExpression", "Porperty"); //CompatibleInitialiser (validateCompatibleInitialiser in code) to CompatibleDefaultExpression (validateCompatibleDefaultExpression in code)
		
		changes.add(rp1);
		changes.add(rp2);
		changes.add(rp3);
		changes.add(rp4);
		changes.add(rp5);
		
		//here if the infos in the two changes are not sufficient, we can improve the change and add missing infos, i put them in comments below
		SetProperty sp1 = new SetProperty("stereotype","Type","Stereotype", false); //stereotype : from Type to Stereotype (passing by Class), in ElementExtension 
		SetProperty sp2 = new SetProperty("specification","ValueSpecification", "OpaqueExpression", false); //specification : from ValueSpecification to OpaqueExpression, in Constraint 
		
		SetProperty sp3 = new SetProperty("context", "NamedElement","Namespace", false); // context : from NamedElement to Namespace, in Constraint
		SetProperty sp4 = new SetProperty("ownedAnnotation", "Annotations","Element", false); // ownedAnnotation : from Annotations to Element, in Element pulled from NamedElement 
		
		changes.add(sp1);
		changes.add(sp2);
		changes.add(sp3);
		changes.add(sp4);
		
		
		ChangeLowerBound clb1 = new ChangeLowerBound("specification", "Constraint", "1", "0");
		ChangeLowerBound clb2 = new ChangeLowerBound("ownedParameter", "TemplateSignature", "0", "1");
		
		DeleteProperty dp1 = new DeleteProperty("upper", "MultiplicityElement"); 
		DeleteProperty dp2 = new DeleteProperty("lower", "MultiplicityElement");
		DeleteProperty dp3 = new DeleteProperty("isUnique", "MultiplicityElement");
		DeleteProperty dp4 = new DeleteProperty("isOrdered", "MultiplicityElement");
		DeleteProperty dp5 = new DeleteProperty("includesMultiplicity", "MultiplicityElement");
		DeleteProperty dp6 = new DeleteProperty("includesCardinality", "MultiplicityElement");
		DeleteProperty dp7 = new DeleteProperty("isMultivalued", "MultiplicityElement");
		DeleteProperty dp8 = new DeleteProperty("upperBound", "MultiplicityElement");
		DeleteProperty dp9 = new DeleteProperty("lowerBound", "MultiplicityElement");
		
		DeleteProperty dp10 = new DeleteProperty("not_own_self", "Element");
		DeleteProperty dp11 = new DeleteProperty("stereotype", "Constraint");
		DeleteProperty dp12 = new DeleteProperty("messageExpression", "ExpressionInOCL");
		DeleteProperty dp13 = new DeleteProperty("message", "OpaqueExpression");
		DeleteProperty dp14 = new DeleteProperty("parameter", "TemplateSignature");
		
		changes.add(dp1);
		changes.add(dp2);
		changes.add(dp3);
		changes.add(dp4);
		changes.add(dp5);
		changes.add(dp6);
		changes.add(dp7);
		changes.add(dp8);
		changes.add(dp9);
		changes.add(dp10);
		changes.add(dp11);
		changes.add(dp12);
		changes.add(dp13);
		changes.add(dp14);
		
		DeleteClass dc1 = new DeleteClass("MultiplicityElement");		
		DeleteClass dc2 = new DeleteClass("Model");
		DeleteClass dc3 = new DeleteClass("Int");
		
		changes.add(dc1);
		changes.add(dc2);
		changes.add(dc3);
			
		DeleteSuperType dst1 = new DeleteSuperType("MultiplicityElement", "Element");
		DeleteSuperType dst2 = new DeleteSuperType("Model", "Package");
		DeleteSuperType dst3 = new DeleteSuperType("TypedMultiplicityElement", "MultiplicityElement");
		DeleteSuperType dst4 = new DeleteSuperType("Library", "Model");
		DeleteSuperType dst5 = new DeleteSuperType("ConstructorPart", "Element");//this will be restricted from Element to TypedElement 
		DeleteSuperType dst6 = new DeleteSuperType("State", "NamedElement");//this will be changed from NamedElement to Namespace
		
		changes.add(dst1);
		changes.add(dst2);
		changes.add(dst3);
		changes.add(dst4);
		changes.add(dst5);
		changes.add(dst6);
		
		AddClass ac1 = new AddClass("DynamicBehavior");
		AddClass ac2 = new AddClass("OppositePropertyCallExp");
		AddClass ac3 = new AddClass("ProfileApplication");
		AddClass ac4 = new AddClass("TypeExtension");
		AddClass ac5 = new AddClass("PseudostateKind");
		AddClass ac6 = new AddClass("TransitionKind");
		AddClass ac7 = new AddClass("Behavior");
		AddClass ac8 = new AddClass("ConnectionPointReference");
		AddClass ac9 = new AddClass("DynamicElement");
		AddClass ac10 = new AddClass("DynamicProperty");
		AddClass ac11 = new AddClass("DynamicType");
		AddClass ac12 = new AddClass("ElementExtension");
		AddClass ac13 = new AddClass("FinalState");
		AddClass ac14 = new AddClass("Import");
		AddClass ac15 = new AddClass("Profile");
		AddClass ac16 = new AddClass("Pseudostate");
		AddClass ac17 = new AddClass("ReferringElement");
		AddClass ac18 = new AddClass("Region");
		AddClass ac19 = new AddClass("Root");
		AddClass ac20 = new AddClass("StateMachine");
		AddClass ac21 = new AddClass("Stereotype");
		AddClass ac22 = new AddClass("Transition");
		AddClass ac23 = new AddClass("Trigger");
		AddClass ac24 = new AddClass("Vertex");
		
		
		changes.add(ac1);
		changes.add(ac2);
		changes.add(ac3);
		changes.add(ac4);
		changes.add(ac5);
		changes.add(ac6);
		changes.add(ac7);
		changes.add(ac8);
		changes.add(ac9);
		changes.add(ac10);
		changes.add(ac11);
		changes.add(ac12);
		changes.add(ac13);
		changes.add(ac14);
		changes.add(ac15);
		changes.add(ac16);
		changes.add(ac17);
		changes.add(ac18);
		changes.add(ac19);
		changes.add(ac20);
		changes.add(ac21);
		changes.add(ac22);
		changes.add(ac23);
		changes.add(ac24);
		
		AddProperty ap1 = new AddProperty("CompatibleInitialiserType", "Variable");//add param (context diagnostics)
		AddProperty ap2 = new AddProperty("type", "TypeExtension");
		AddProperty ap3 = new AddProperty("stereotype", "TypeExtension");
		AddProperty ap4 = new AddProperty("isRequired", "TypeExtension");
		AddProperty ap5 = new AddProperty("extendedBys", "Type");
		AddProperty ap6 = new AddProperty("transition", "Trigger");
		AddProperty ap7 = new AddProperty("state", "Trigger");
		AddProperty ap8 = new AddProperty("extensionOfs", "Stereotype");
		AddProperty ap9 = new AddProperty("isStrict", "ProfileApplication");
		AddProperty ap10 = new AddProperty("applyingPackage", "ProfileApplication");
		AddProperty ap11 = new AddProperty("appliedProfile", "ProfileApplication");
		AddProperty ap12 = new AddProperty("application", "Profile");
		AddProperty ap13 = new AddProperty("profileApplication", "Package");
		AddProperty ap14 = new AddProperty("referredProperty", "OppositePropertyCallExp");
		AddProperty ap15 = new AddProperty("isRequired", "ElementExtension");
		AddProperty ap16 = new AddProperty("isApplied", "ElementExtension");
		AddProperty ap17 = new AddProperty("transition", "Constraint");
		AddProperty ap18 = new AddProperty("owningState", "Constraint");
		AddProperty ap19 = new AddProperty("redefinedConstraint", "Constraint");
		AddProperty ap20 = new AddProperty("nestedType", "Class");
		AddProperty ap21 = new AddProperty("isActive", "Class");
		AddProperty ap22 = new AddProperty("transition", "Behavior");	
		AddProperty ap23 = new AddProperty("incoming", "Vertex");
		AddProperty ap24 = new AddProperty("outgoing", "Vertex");
		AddProperty ap25 = new AddProperty("container", "Vertex");
		AddProperty ap26 = new AddProperty("isRequired", "TypedElement");
		AddProperty ap27 = new AddProperty("ownedInvariant", "Type");
		AddProperty ap28 = new AddProperty("UniqueInvariantName", "Type");//with param (context diagnostics)
		AddProperty ap29 = new AddProperty("container", "Transition");
		AddProperty ap30 = new AddProperty("trigger", "Transition");
		AddProperty ap31 = new AddProperty("effect", "Transition");
		AddProperty ap32 = new AddProperty("guard", "Transition");
		AddProperty ap33 = new AddProperty("target", "Transition");
		AddProperty ap34 = new AddProperty("source", "Transition");
		AddProperty ap35 = new AddProperty("kind", "Transition");
		AddProperty ap36 = new AddProperty("extendedStateMachine", "StateMachine");
		AddProperty ap37 = new AddProperty("submachineState", "StateMachine");
		AddProperty ap38 = new AddProperty("connectionPoint", "StateMachine");
		AddProperty ap39 = new AddProperty("region", "StateMachine");
		AddProperty ap40 = new AddProperty("deferrableTrigger", "State");
		AddProperty ap41 = new AddProperty("connectionPoint", "State");
		AddProperty ap42 = new AddProperty("doActivity", "State");
		AddProperty ap43 = new AddProperty("exit", "State");
		AddProperty ap44 = new AddProperty("entry", "State");
		AddProperty ap45 = new AddProperty("stateInvariant", "State");
		AddProperty ap46 = new AddProperty("region", "State");
		AddProperty ap47 = new AddProperty("redefinedState", "State");
		AddProperty ap48 = new AddProperty("connection", "State");
		AddProperty ap49 = new AddProperty("submachine", "State");
		AddProperty ap50 = new AddProperty("isSubmachineState", "State");
		AddProperty ap51 = new AddProperty("isSimple", "State");
		AddProperty ap52 = new AddProperty("isOrthogonal", "State");
		AddProperty ap53 = new AddProperty("isComposite", "State");
		AddProperty ap54 = new AddProperty("imports", "Root");
		AddProperty ap55 = new AddProperty("externalURI", "Root");
		AddProperty ap56 = new AddProperty("nestedPackage", "Root");
		AddProperty ap57 = new AddProperty("subvertex", "Region");
		AddProperty ap58 = new AddProperty("extendedRegion", "Region");
		AddProperty ap59 = new AddProperty("state", "Region");
		AddProperty ap60 = new AddProperty("stateMachine", "Region");
		AddProperty ap61 = new AddProperty("transition", "Region");
		AddProperty ap62 = new AddProperty("state", "Pseudostate");
		AddProperty ap63 = new AddProperty("stateMachine", "Pseudostate");
		AddProperty ap64 = new AddProperty("kind", "Pseudostate");
		AddProperty ap65 = new AddProperty("CompatibleResultType", "PropertyCallExp"); //with param (context diagnostics)
		AddProperty ap66 = new AddProperty("NonStaticSourceTypeIsConformant", "PropertyCallExp");//with param (context diagnostics)
		AddProperty ap67 = new AddProperty("getSpecializedReferredPropertyType", "PropertyCallExp");
		AddProperty ap68 = new AddProperty("getSpecializedReferredPropertyOwningType", "PropertyCallExp");
		AddProperty ap69 = new AddProperty("referredProperty", "Property");
		AddProperty ap70 = new AddProperty("subsettedProperty", "Property");
		AddProperty ap71 = new AddProperty("redefinedProperty", "Property");
		AddProperty ap72 = new AddProperty("defaultExpression", "Property");
		AddProperty ap73 = new AddProperty("importedPackage", "Package");
		AddProperty ap74 = new AddProperty("ArgumentTypeIsConformant", "OperationCallExp"); //with param (context diagnostics)
		AddProperty ap75 = new AddProperty("redefinedOperation", "Operation");
		AddProperty ap76 = new AddProperty("isValidating", "Operation");
		AddProperty ap77 = new AddProperty("isInvalidating", "Operation");
		AddProperty ap78 = new AddProperty("bodyExpression", "Operation");
		AddProperty ap79 = new AddProperty("postcondition", "Operation");
		AddProperty ap80 = new AddProperty("precondition", "Operation");
		AddProperty ap81 = new AddProperty("UniquePostconditionName", "Operation");//with param (context diagnostics)
		AddProperty ap82 = new AddProperty("UniquePreconditionName", "Operation");//with param (context diagnostics)
		AddProperty ap83 = new AddProperty("LoadableImplementation", "Operation");//with param (context diagnostics)
		AddProperty ap84 = new AddProperty("importedNamespace", "Import");
		AddProperty ap85 = new AddProperty("base", "ElementExtension");
		AddProperty ap86 = new AddProperty("stereotype", "ElementExtension");
		AddProperty ap87 = new AddProperty("extension", "Element");
		AddProperty ap88 = new AddProperty("getValue", "Element");//with param (propertyName stereotype)
		AddProperty ap89 = new AddProperty("ownedProperty", "DynamicType");
		AddProperty ap90 = new AddProperty("default", "DynamicProperty");
		AddProperty ap91 = new AddProperty("referredProperty", "DynamicProperty");
		AddProperty ap92 = new AddProperty("metaType", "DynamicElement");
		AddProperty ap93 = new AddProperty("exit", "ConnectionPointReference");
		AddProperty ap94 = new AddProperty("state", "ConnectionPointReference");
		AddProperty ap95 = new AddProperty("entry", "ConnectionPointReference");
		AddProperty ap96 = new AddProperty("upper", "CollectionType");
		AddProperty ap97 = new AddProperty("lower", "CollectionType");
		AddProperty ap98 = new AddProperty("ownedBehavior", "Class");		
		AddProperty ap99 = new AddProperty("getReferredElement", "ReferringElement");
		AddProperty ap100 = new AddProperty("ClosureBodyTypeIsConformanttoIteratorType", "IteratorExp");//with param (context diagnostics)
		AddProperty ap101 = new AddProperty("SortedByIteratorTypeIsComparable", "IteratorExp");//with param (context diagnostics)
		AddProperty ap102 = new AddProperty("external", "TransitionKind");
		AddProperty ap103 = new AddProperty("local", "TransitionKind");
		AddProperty ap104 = new AddProperty("internal", "TransitionKind");
		AddProperty ap105 = new AddProperty("terminate", "PseudostateKind");
		AddProperty ap106 = new AddProperty("exitPoint", "PseudostateKind");
		AddProperty ap107 = new AddProperty("entryPoint", "PseudostateKind");
		AddProperty ap108 = new AddProperty("choice", "PseudostateKind");
		AddProperty ap109 = new AddProperty("junction", "PseudostateKind");
		AddProperty ap110 = new AddProperty("fork", "PseudostateKind");
		AddProperty ap111 = new AddProperty("join", "PseudostateKind");
		AddProperty ap112 = new AddProperty("shallowHistory", "PseudostateKind");
		AddProperty ap113 = new AddProperty("deepHistory", "PseudostateKind");
		AddProperty ap114 = new AddProperty("initial", "PseudostateKind");
		
		changes.add(ap1);
		changes.add(ap2);
		changes.add(ap3);
		changes.add(ap4);
		changes.add(ap5);
		changes.add(ap6);
		changes.add(ap7);
		changes.add(ap8);
		changes.add(ap9);
		changes.add(ap10);
		changes.add(ap11);
		changes.add(ap12);
		changes.add(ap13);
		changes.add(ap14);
		changes.add(ap15);
		changes.add(ap16);
		changes.add(ap17);
		changes.add(ap18);
		changes.add(ap19);
		changes.add(ap20);
		changes.add(ap21);
		changes.add(ap22);
		changes.add(ap23);
		changes.add(ap24);
		changes.add(ap25);
		changes.add(ap26);
		changes.add(ap27);
		changes.add(ap28);
		changes.add(ap29);
		changes.add(ap30);
		changes.add(ap31);
		changes.add(ap32);
		changes.add(ap33);
		changes.add(ap34);
		changes.add(ap35);
		changes.add(ap36);
		changes.add(ap37);
		changes.add(ap38);
		changes.add(ap39);
		changes.add(ap40);
		changes.add(ap41);
		changes.add(ap42);
		changes.add(ap43);
		changes.add(ap44);
		changes.add(ap45);
		changes.add(ap46);
		changes.add(ap47);
		changes.add(ap48);
		changes.add(ap49);
		changes.add(ap50);
		changes.add(ap51);
		changes.add(ap52);
		changes.add(ap53);
		changes.add(ap54);
		changes.add(ap55);
		changes.add(ap56);
		changes.add(ap57);
		changes.add(ap58);
		changes.add(ap59);
		changes.add(ap60);
		changes.add(ap61);
		changes.add(ap62);
		changes.add(ap63);
		changes.add(ap64);
		changes.add(ap65);
		changes.add(ap66);
		changes.add(ap67);
		changes.add(ap68);
		changes.add(ap69);
		changes.add(ap70);
		changes.add(ap71);
		changes.add(ap72);
		changes.add(ap73);
		changes.add(ap74);
		changes.add(ap75);
		changes.add(ap76);
		changes.add(ap77);
		changes.add(ap78);
		changes.add(ap79);
		changes.add(ap80);
		changes.add(ap81);
		changes.add(ap82);
		changes.add(ap83);
		changes.add(ap84);
		changes.add(ap85);
		changes.add(ap86);
		changes.add(ap87);
		changes.add(ap88);
		changes.add(ap89);
		changes.add(ap90);
		changes.add(ap91);
		changes.add(ap92);
		changes.add(ap93);
		changes.add(ap94);
		changes.add(ap95);
		changes.add(ap96);
		changes.add(ap97);
		changes.add(ap98);
		changes.add(ap99);
		changes.add(ap100);
		changes.add(ap101);
		changes.add(ap102);
		changes.add(ap103);
		changes.add(ap104);
		changes.add(ap105);
		changes.add(ap106);
		changes.add(ap107);
		changes.add(ap108);
		changes.add(ap109);
		changes.add(ap110);
		changes.add(ap111);
		changes.add(ap112);
		changes.add(ap113);
		changes.add(ap114);
		
		AddSuperType ast1 = new AddSuperType("State", "Namespace");
		AddSuperType ast2 = new AddSuperType("DynamicBehavior", "DynamicType");
		AddSuperType ast3 = new AddSuperType("DynamicBehavior", "Behavior");
		AddSuperType ast4 = new AddSuperType("ConstructorPart", "TypedElement");
		AddSuperType ast5 = new AddSuperType("VariableExp", "ReferringElement");
		AddSuperType ast6 = new AddSuperType("TypeExp", "ReferringElement");
		AddSuperType ast7 = new AddSuperType("IterateExp", "ReferringElement");
		AddSuperType ast8 = new AddSuperType("FinalState", "State");
		AddSuperType ast9 = new AddSuperType("TypeExtension", "Element");
		AddSuperType ast10 = new AddSuperType("ProfileApplication", "Element");
		AddSuperType ast11 = new AddSuperType("OppositePropertyCallExp", "NavigationCallExp");
		AddSuperType ast12 = new AddSuperType("Vertex", "NamedElement	");
		AddSuperType ast13 = new AddSuperType("Transition", "Namespace");
		AddSuperType ast14 = new AddSuperType("StateMachine", "Behavior");
		AddSuperType ast15 = new AddSuperType("State", "Vertex");
		AddSuperType ast16 = new AddSuperType("Root", "Namespace");
		AddSuperType ast17 = new AddSuperType("Region", "Namespace");
		AddSuperType ast18 = new AddSuperType("Pseudostate", "Vertex");
		AddSuperType ast19 = new AddSuperType("PropertyCallExp", "ReferringElement");
		AddSuperType ast20 = new AddSuperType("OperationCallExp", "ReferringElement");
		AddSuperType ast21 = new AddSuperType("Import", "NamedElement");
		AddSuperType ast22 = new AddSuperType("ElementExtension", "Type");
		AddSuperType ast23 = new AddSuperType("DynamicType", "DynamicElement ");
		AddSuperType ast24 = new AddSuperType("DynamicType", "Type ");
		AddSuperType ast25 = new AddSuperType("DynamicProperty", "Element");
		AddSuperType ast26 = new AddSuperType("DynamicElement", "Element");
		AddSuperType ast27 = new AddSuperType("ConnectionPointReference", "Vertex");
		AddSuperType ast28 = new AddSuperType("IteratorExp ", "ReferringElement");
		AddSuperType ast29 = new AddSuperType("Behavior", "Class");
		AddSuperType ast30 = new AddSuperType("Profile", "Package");
		AddSuperType ast31 = new AddSuperType("Stereotype", "Class");
		AddSuperType ast32 = new AddSuperType("Trigger", "NamedElement");

		changes.add(ast1);
		changes.add(ast2);
		changes.add(ast3);
		changes.add(ast4);
		changes.add(ast5);
		changes.add(ast6);
		changes.add(ast7);
		changes.add(ast8);
		changes.add(ast9);
		changes.add(ast10);
		changes.add(ast11);
		changes.add(ast12);
		changes.add(ast13);
		changes.add(ast14);
		changes.add(ast15);
		changes.add(ast16);
		changes.add(ast17);
		changes.add(ast18);
		changes.add(ast19);
		changes.add(ast20);
		changes.add(ast21);
		changes.add(ast22);
		changes.add(ast23);
		changes.add(ast24);
		changes.add(ast25);
		changes.add(ast26);
		changes.add(ast27);
		changes.add(ast28);
		changes.add(ast29);
		changes.add(ast30);
		changes.add(ast31);
		changes.add(ast32);

		//RenameClass renameClass3 = new RenameClass("Person","Contact","???"); 
		//RenameClass renameClass1 = new RenameClass("Loc","Address","???"); 
		//RenameProperty renameProperty1 = new RenameProperty("lower","lowertoto","CollectionType"); 
		//RenameProperty renameProperty2 = new RenameProperty("value","valuetata","ShadowExp"); 
	
		// RenameProperty renameProperty4 = new RenameProperty("validateHasNoInitializer","validateHasNoInitializertoto","ParameterVariable"); 
		 //RenameProperty renameProperty5 = new RenameProperty("validateTypeIsNotInvalid","validateTypeIsNotInvalidtiti","CollectionLiteralPart"); 
		 //RenameProperty renameProperty6 = new RenameProperty("validateCompatibleInitializerType","validateCompatibleInitializerTypetata","Variable"); 

		//RenameProperty renameProperty = new RenameProperty("getList","getSortedList","???");
		// RenameProperty renameProperty1 = new RenameProperty("Num","Number","???");
		// RenameProperty renameProperty1 = new RenameProperty("isImplicit","isImplicittoto","Property"); 
		//	RenameClass renameClass = new RenameClass("BagType","BagTypetoto","org.eclipse.ocl.pivot"); 
		//RenameClass renameClass = new RenameClass("SelfType","SelfTypetoto","org.eclipse.ocl.pivot"); 
		//RenameClass renameClass2= new RenameClass("Annotation","Annotationtoto","org.eclipse.ocl.pivot"); 
		//DeleteProperty deleteProperty = new DeleteProperty("test","UseBookAddress");
		
	//	MoveProperty moveProperty = new MoveProperty("isInvalidating","Operation","LanguageExpression");
		//moveProperty.setThroughReference("bodyExpression");
		//DeleteClass deleteClass =  new DeleteClass("Address");
	//	DeleteClass deleteClass1 =  new DeleteClass("SelfType");
		//MoveProperty moveProperty = new MoveProperty("URI","Package","ProfileApplication");
		//moveProperty.setThroughReference("ownedProfileApplications");
		//moveProperty.setImportPath("org.eclipse.ocl.pivot.ProfileApplication");

		//moveProperty.setUpperBound(-1);
	//	DeleteClass deleteClass1 =  new DeleteClass("SelfType");
		//DeleteClass deleteClass2 =  new DeleteClass("VoidType");
		
		/*DeleteProperty deleteProperty = new DeleteProperty("validateSafeSourceCanBeNull","CallExp");
		DeleteProperty deleteProperty1 = new DeleteProperty("instanceClassName","Class");
		DeleteProperty deleteProperty2= new DeleteProperty("OwnedLiterals","Enumeration");

		DeleteProperty deleteProperty3 = new DeleteProperty("isSafe","CallExp");
		DeleteProperty deleteProperty4 = new DeleteProperty("externalURI","Model");
*/
		/*RenameClass renameClass1 = new RenameClass("SelfType","SelfTypetoto","org.eclipse.ocl.pivot"); 

		 RenameProperty renameProperty1 = new RenameProperty("validateHasNoInitializer","validateHasNoInitializertoto","ParameterVariable"); 
		 RenameProperty renameProperty2 = new RenameProperty("validateTypeIsNotInvalid","validateTypeIsNotInvalidtiti","CollectionLiteralPart"); 
		 RenameProperty renameProperty3 = new RenameProperty("validateCompatibleInitializerType","validateCompatibleInitializerTypetata","Variable"); 

		 MoveProperty moveProperty1 = new MoveProperty("isInvalidating","Operation","LanguageExpression");
		 moveProperty1.setThroughReference("bodyExpression");
		 */
	/*	DeleteClass deleteClass1 =  new DeleteClass("ReferenceCS");
		RenameClass renameClass1 = new RenameClass("FeatureCS","FeatureCSTOTO","org.eclipse.ocl.BaseCS"); 

		
		DeleteProperty deleteProperty1 = new DeleteProperty("getDescription","ElementCS");


		MoveProperty moveProperty1 = new MoveProperty("name","LambdaTypeCS","TypeRefCS");
		moveProperty1.setThroughReference("ownedParameterTypes");
		moveProperty1.setImportPath("org.eclipse.ocl.xtext.basecs.TypedRefCS");
		moveProperty1.setUpperBound(-1);
		*/
		
	//	ArrayList<String> subClassesNames = new ArrayList<String>();
		//subClassesNames.add("BagType");
		//subClassesNames.add("OrderedSetType");
		//subClassesNames.add("SequenceType");
		//subClassesNames.add("SetType");
	/*	ArrayList<String> subClass = new ArrayList<String>();
	    subClass.add("SetType");
	    subClass.add("OrderedSetType");
	    subClass.add("SequenceType");
	    subClass.add("BagType");
		PushProperty pushproperty = new PushProperty("upper","CollectionType",subClass);
		subClass = new ArrayList<String>();
	    subClass.add("FinalState");
		
		PushProperty pushproperty2 = new PushProperty("isComposite","State",subClass);
		*/	
		//ChangeUpperBound changeUpperBound = new ChangeUpperBound("generalization","AssociationClass","1","-1");
		//ChangeUpperBound changeUpperBound1 = new ChangeUpperBound("generalization1","EnumerationLiteral","1","-1");
		//ChangeUpperBound changeUpperBound2 = new ChangeUpperBound("generalization2","Stereotype","1","-1");
		//SetProperty setProperty1 = new SetProperty("setProperty1", "Type", "BagType", false);
		//SetProperty setProperty2 = new SetProperty("setProperty1", "Variable", "Enumeration", false);
		
		
	/*	changes.add(deleteProperty);
		changes.add(deleteProperty1);
		//changes.add(renameProperty3);
		changes.add(deleteProperty2);
		changes.add(deleteProperty3);
		changes.add(deleteProperty4);
		*/
		//  changes.add(renameClass); 
		//changes.add(renameClass2);

		//  changes.add(renameClass1);
		 //changes.add(renameClass);
		 //changes.add(renameClass3);
		// changes.add(renameProperty);
		//changes.add(renameProperty1);
		//changes.add(deleteClass);
		//changes.add(deleteProperty);
		//changes.add(moveProperty);

		/*	 	changes.add(deleteClass1);
	 	changes.add(deleteClass2);

	 	changes.add(deleteProperty1);
	 	changes.add(deleteProperty2);

	 	 changes.add(renameClass1);

	 	changes.add(renameProperty1);
	 	changes.add(renameProperty2);
	 	changes.add(renameProperty3);

	 	changes.add(moveProperty1);
		 */

	
		/*changes.add(renameClass1);
		changes.add(renameProperty1);
		changes.add(renameProperty2);
		changes.add(renameProperty3);

		changes.add(deleteProperty1);

		changes.add(moveProperty1);
*/
		//changes.add(pushproperty);
		//changes.add(pushproperty2);
	//	changes.add(changeUpperBound);
	//	changes.add(changeUpperBound1);

	//	changes.add(changeUpperBound2);
		//changes.add(setProperty1);
		
	//	changes.add(setProperty2);
		
	/*	RenameProperty rp1 = new RenameProperty("discoveryError", "discoveryErrors", "Discovery");		
		RenameProperty rp2 = new RenameProperty("dicoveryDate", "discoveryDate", "Discovery");
		RenameProperty rp3 = new RenameProperty("totalExecutionTimeInSeconds", "discoveryTimeInSeconds", "Discovery");
		RenameProperty rp4 = new RenameProperty("averageSaveTimeInSeconds", "saveTimeAverageInSeconds", "AveragedProjectDiscovery");
		RenameProperty rp5 = new RenameProperty("averageExecutionTimeInSeconds", "discoveryTimeAverageInSeconds", "AveragedProjectDiscovery");
		
		changes.add(rp1);
		changes.add(rp2);
		changes.add(rp3);
		changes.add(rp4);
		changes.add(rp5);
		
		ArrayList<ComplexChange> moves = new ArrayList<ComplexChange>();
		
		MoveProperty mp1 = new MoveProperty("discoveryDate", "Discovery", "DiscoveryIteration");//after its rename
		mp1.setThroughReference("iterations");
		MoveProperty mp2 = new MoveProperty("maxUsedMemoryInBytes", "Discovery", "DiscoveryIteration");
		mp2.setThroughReference("iterations");
		MoveProperty mp3 = new MoveProperty("saveTimeInSeconds", "Discovery", "DiscoveryIteration");
		mp3.setThroughReference("iterations");
		MoveProperty mp4 = new MoveProperty("discoveryErrors", "Discovery", "DiscoveryIteration");//after its rename
		mp4.setThroughReference("iterations");
		MoveProperty mp5 = new MoveProperty("discoveryTimeInSeconds", "Discovery", "DiscoveryIteration");//after its rename
		mp5.setThroughReference("iterations");
		moves.add(mp1);
		moves.add(mp2);
		moves.add(mp3);
		moves.add(mp4);
		moves.add(mp5);
		
		ExtractClass ec = new ExtractClass("EC", "Discovery", "DiscoveryIteration", null, moves);
		
		changes.add(ec);
		
		MoveProperty mp6 = new MoveProperty("saveTimeStandardDeviation", "AveragedProjectDiscovery", "ProjectDiscovery");
		MoveProperty mp7 = new MoveProperty("executionTimeStandardDeviation", "AveragedProjectDiscovery", "ProjectDiscovery");
		MoveProperty mp8 = new MoveProperty("saveTimeAverageInSeconds", "AveragedProjectDiscovery", "ProjectDiscovery");//after its rename
		MoveProperty mp9 = new MoveProperty("discoveryTimeAverageInSeconds", "AveragedProjectDiscovery", "ProjectDiscovery");//after its rename
		
		changes.add(mp6);
		changes.add(mp7);
		changes.add(mp8);
		changes.add(mp9);
		
		ArrayList<String> subclasses = new ArrayList<String>();
		subclasses.add("ProjectDiscovery");
		
		PullProperty pp1 = new PullProperty("saveTimeStandardDeviation", "Discovery", subclasses);
		PullProperty pp2 = new PullProperty("executionTimeStandardDeviation", "Discovery", subclasses);
		PullProperty pp3 = new PullProperty("saveTimeAverageInSeconds", "Discovery", subclasses);
		PullProperty pp4 = new PullProperty("discoveryTimeAverageInSeconds", "Discovery", subclasses);
		
		changes.add(pp1);
		changes.add(pp2);
		changes.add(pp3);
		changes.add(pp4);
		
		ArrayList<ComplexChange> pulls = new ArrayList<ComplexChange>();
		
		ArrayList<String> subclasses1 = new ArrayList<String>();
		subclasses1.add("Porject");
		PullProperty pp5 = new PullProperty("name", "Resource", subclasses1);
		PullProperty pp6 = new PullProperty("totalSizeInBytes", "Resource", subclasses1);
		pulls.add(pp5);
		pulls.add(pp6);
		
		ExtractSuperClass esc = new ExtractSuperClass("", "Resource", subclasses1, null, pulls);
		changes.add(esc);
		
		ArrayList<String> subclasses2 = new ArrayList<String>();
		subclasses2.add("MultiProjectBenchmark");
		subclasses2.add("ProjectDiscovery");
		
		ArrayList<String> subclasses3 = new ArrayList<String>();
		subclasses3.add("MultiDiscoveryBenchmark");
		subclasses3.add("AveragedMultiDiscoveryBenchmark");
		subclasses3.add("DiscoveredProject");
		
		PullProperty pp7 = new PullProperty("projects", "Benchmark", subclasses2);
		PullProperty pp8 = new PullProperty("discoveries", "Benchmark", subclasses3);
		changes.add(pp7);
		changes.add(pp8);
		
		DeleteProperty dp1 = new DeleteProperty("occurrences", "AveragedProjectDiscovery"); 
		DeleteProperty dp2 = new DeleteProperty("metaModelVariant", "Discovery");
		DeleteProperty dp3 = new DeleteProperty("algorithmVariant", "Discovery");
		
		changes.add(dp1);
		changes.add(dp2);
		changes.add(dp3);
		
		DeleteClass dc1 = new DeleteClass("AveragedProjectDiscovery");
		DeleteClass dc2 = new DeleteClass("AveragedMultiDiscoveryBenchmark");
		DeleteClass dc3 = new DeleteClass("DiscoveredProject");
		DeleteClass dc4 = new DeleteClass("ProjectDiscovery");
		DeleteClass dc5 = new DeleteClass("MultiDiscoveryBenchmark");
		DeleteClass dc6 = new DeleteClass("MultiProjectBenchmark");
		
		changes.add(dc1);
		changes.add(dc2);
		changes.add(dc3);
		changes.add(dc4);
		changes.add(dc5);
		changes.add(dc6);
		
		DeleteSuperType dst1 = new DeleteSuperType("MultiProjectBenchmark", "Benchmark");
		DeleteSuperType dst2 = new DeleteSuperType("MultiDiscoveryBenchmark", "Benchmark");
		DeleteSuperType dst3 = new DeleteSuperType("ProjectDiscovery", "Discovery");
		DeleteSuperType dst4 = new DeleteSuperType("DiscoveredProject", "Project");
		DeleteSuperType dst5 = new DeleteSuperType("AveragedMultiDiscoveryBenchmark", "Benchmark");
		
		changes.add(dst1);
		changes.add(dst2);
		changes.add(dst3);
		changes.add(dst4);
		changes.add(dst5);
		
		
		AddClass ac1 = new AddClass("DiscoveryIteration");
		AddClass ac2 = new AddClass("Resource");
		AddClass ac3 = new AddClass("EndEvent");
		AddClass ac4 = new AddClass("BeginEvent");
		AddClass ac5 = new AddClass("EventType");
		AddClass ac6 = new AddClass("Event");
		AddClass ac7 = new AddClass("MemoryMeasurement");
		
		changes.add(ac1);
		changes.add(ac2);
		changes.add(ac3);
		changes.add(ac4);
		changes.add(ac5);
		changes.add(ac6);
		changes.add(ac7);
		
		AddSuperType ast1 = new AddSuperType("BeginEvent", "Event");
		AddSuperType ast2 = new AddSuperType("MemoryMeasurement", "Event");
		AddSuperType ast3 = new AddSuperType("EndEvent", "Event");
		AddSuperType ast4 = new AddSuperType("Project", "Resource");
		
		changes.add(ast1);
		changes.add(ast2);
		changes.add(ast3);
		changes.add(ast4);
		
		AddProperty ap1 = new AddProperty("memoryUsed", "MemoryMeasurement");
		AddProperty ap2 = new AddProperty("name", "EventType");
		AddProperty ap3 = new AddProperty("eventType", "Event");
		AddProperty ap4 = new AddProperty("time", "Event");
		AddProperty ap5 = new AddProperty("beginning", "EndEvent");
		AddProperty ap6 = new AddProperty("copyOfDiscovererDescription", "Discovery");
		AddProperty ap7 = new AddProperty("discovererLaunchConfiguration", "Discovery");
		AddProperty ap8 = new AddProperty("iterations", "Discovery");
		AddProperty ap9 = new AddProperty("project", "Discovery");
		AddProperty ap10 = new AddProperty("memoryMeasurements", "DiscoveryIteration");
		AddProperty ap11 = new AddProperty("events", "DiscoveryIteration");
		
		changes.add(ap1);
		changes.add(ap2);
		changes.add(ap3);
		changes.add(ap4);
		changes.add(ap5);
		changes.add(ap6);
		changes.add(ap7);
		changes.add(ap8);
		changes.add(ap9);
		changes.add(ap10);
		changes.add(ap11);

		*/
		
		return changes;
	}

}
