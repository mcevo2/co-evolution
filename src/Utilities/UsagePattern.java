package Utilities;

public enum UsagePattern {
	TypeUseRename,createObjectRename,getObjectRename,setObjectRename,QualifiedTypeRename,castObjectRename,
	getAttribute,setAttribute,getClass_Attribute,accessAttribute,
	ImportRename,
	PropertyRename,
	getPropertyRename,
	setPropertyRename,
	isPropertyRename,
	callMethod,getClass_call,
	VariableDeclarationDelete,VariableUseDelete,ClassInstanceDelete,MethodInvocTypeDelete,
	parameterDelete,
	parameterInMiDelete,
	ImportDelete, 
	ReturnTypeDelete,
	SuperClassDelete, 
	LiteralDelete,
	ComplexStatementDelete,
	VisitClassMethodDelete,
	GetObjectDelete,
	PropertyDelete,
	getorSetPropertyDelete,
	GetorSetMoveProperty,
	MoveProperty,
	LiteralRename,
	LiteralPush,
	propertyPush,
	PropertyPushMD,
	
	getPropertyPush,
	setPropertyPush,
	GeneralizeProperty,
	GeneralizePropertyBefore,
	ChangeType
	

}