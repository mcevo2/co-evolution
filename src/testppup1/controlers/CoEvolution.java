package testppup1.controlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.ui.text.correction.AssistContext;
import org.eclipse.jdt.internal.ui.text.correction.JavaCorrectionProcessor;
import org.eclipse.jdt.internal.ui.text.correction.ProblemLocation;

import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.correction.*;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import Utilities.ASTManager;
import Utilities.ASTModificationManager;
import Utilities.ChangeDetection;
import Utilities.ErrorsRetriever;
import Utilities.JavaVisitor;
import Utilities.MarkerComparator;
import Utilities.MoveResolution;
import Utilities.QuickAssistsProcessorGetterSetter;
import Utilities.Resolutions;
import Utilities.SaveModification;
import Utilities.Usage;
import Utilities.UsagePattern;
import Utilities.UsesManager;
import Utilities.UtilProjectParser;
import fr.lip6.meta.ComplexChangeDetection.Change;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.DeleteClass;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.RenameClass;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.RenameProperty;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.SetProperty;
import fr.lip6.meta.ComplexChangeDetection.ComplexChanges.ExtractClass;
import fr.lip6.meta.ComplexChangeDetection.ComplexChanges.MoveProperty;
import fr.lip6.meta.ComplexChangeDetection.ComplexChanges.PushProperty;

public class CoEvolution {
	public static ArrayList<Usage> sortMarkerList(CompilationUnit cu, ArrayList<IMarker> ml,
			ArrayList<Change> myChanges) {
		ArrayList<IMarker> sorted = new ArrayList<IMarker>();
		ArrayList<Usage> usages = new ArrayList<Usage>();
		ArrayList<Usage> nullusages = new ArrayList<Usage>();
		ArrayList<IMarker> nullPattern = new ArrayList<IMarker>();

		boolean out = false;

		for (IMarker m : ml) {


			Usage usage = UsesManager.classify(myChanges, m, cu);

			//out = true;
			if(!usage.getPatterns().isEmpty()) {
				System.out.println(" full usage ");
				usages.add(usage);
			}
			else
				if ( (usage.getPatterns().isEmpty()) ) {

					Usage nullusage = new Usage();
					nullusage.setError(m);
					nullusage.setNode(ASTManager.getErrorNode(cu, m));

					nullusages.add(nullusage);

				}


		}

		for (Usage usage : nullusages) {
			System.out.println(" adding one null");
			usages.add(usage);

		}

		return usages;

	}

	public static void run() {
		ArrayList<Change> myChanges = ChangeDetection.initializeChangements();

		IProject project = UtilProjectParser.getSelectedProject();
		
		ArrayList<ICompilationUnit> ListICompilUnit = UtilProjectParser.getCompilationUnits(project);

		IJavaProject myp = UtilProjectParser.getJavaProject(project);
		ASTNode adeclaration = null;
		ASTNode anImport = null;
		Usage usage = null;
		IProblem iProblem = null;
		ArrayList<Usage> usages = new ArrayList<Usage>();
		for (ICompilationUnit iCompilUnit : ListICompilUnit) {

			System.out.println("Compilation unit : " + iCompilUnit.getElementName());
			CompilationUnit compilUnit = ASTManager.getCompilationUnit(iCompilUnit);
			// compilUnit.recordModifications();
			IProblem[] problems = compilUnit.getProblems();
			ArrayList<IMarker> ml = new ArrayList<IMarker>();

			try {
				ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);

				// Collections.sort(ml, new MarkerComparator());
				if (ml.size() > 0) {
					JavaVisitor jVisitor = new JavaVisitor();
					jVisitor.process(compilUnit);

					while (!ml.isEmpty()) {

						usages = sortMarkerList(compilUnit, ml, myChanges);

						// System.out.println(" change and node " + node+" "+change);

						usage = usages.get(0);
						IMarker amarker = usage.getError();
						ASTNode node = usage.getNode();

						System.out.println("  THE GOT ERROR   " + usage.getError());

						//System.out.println("  THE FOUND PATTERN " + usage.getPattern());
						if (!usage.getPatterns().isEmpty()) {
							for( UsagePattern pattern :usage.getPatterns())
							{
								System.out.println(" +++ THE GOT ERROR   " + usage.getError());
								System.out.println("+++  THE FOUND PATTERN "+ pattern);
							}
							for( UsagePattern pattern :usage.getPatterns())
							{

								System.out.println("  THE FOUND PATTERN "+ pattern);
								switch (pattern) {

								case TypeUseRename:

									Resolutions.renamingClassResolution(compilUnit, usage,pattern,
											(((RenameClass) usage.getChange()).getNewname()));
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = new ArrayList<IMarker>(ErrorsRetriever.findJavaProblemMarkers(iCompilUnit));

									break;
								case createObjectRename:
									Resolutions.renamingClassResolution(compilUnit, usage,pattern,
											(((RenameClass) usage.getChange()).getNewname()));
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = new ArrayList<IMarker>(ErrorsRetriever.findJavaProblemMarkers(iCompilUnit));

									break;
								case getObjectRename:

									Resolutions.renamingClassResolution(compilUnit, usage,pattern,
											(((RenameClass) usage.getChange()).getNewname()));
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = new ArrayList<IMarker>(ErrorsRetriever.findJavaProblemMarkers(iCompilUnit));

									break;
								case setObjectRename:

									Resolutions.renamingClassResolution(compilUnit, usage,pattern,
											(((RenameClass) usage.getChange()).getNewname()));
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = new ArrayList<IMarker>(ErrorsRetriever.findJavaProblemMarkers(iCompilUnit));

									break;
								case ImportRename:

									Resolutions.renamingImport(compilUnit, usage.getChange(), usage, amarker,
											(((RenameClass) usage.getChange()).getNewname()));
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = new ArrayList<IMarker>(ErrorsRetriever.findJavaProblemMarkers(iCompilUnit));

									break;
								case QualifiedTypeRename:

									Resolutions.renamingClassResolution(compilUnit, usage,pattern,
											(((RenameClass) usage.getChange()).getNewname()));
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = new ArrayList<IMarker>(ErrorsRetriever.findJavaProblemMarkers(iCompilUnit));

									break;

								case VariableDeclarationDelete:

									problems = compilUnit.getProblems();
									iProblem = ErrorsRetriever.getEquivalentProblem(problems, amarker);
									adeclaration = ASTManager.findFieldOrVariableDeclarations(node);
									if (((DeleteClass) usage.getChange()).getName()
											.equals(((SimpleName) node).getIdentifier()))

									{
										if (adeclaration != null && (adeclaration instanceof FieldDeclaration
												|| adeclaration instanceof VariableDeclarationStatement)) {
											Resolutions.deleteVariableDeclaration(compilUnit, adeclaration,usage);
											compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the
											// compilation unit
											jVisitor.process(compilUnit);
											Thread.sleep(3000);
											ml = new ArrayList<IMarker>(
													ErrorsRetriever.findJavaProblemMarkers(iCompilUnit));

										}
									}
									break;
								case VariableUseDelete:

									break;
								case parameterDelete:

									ASTNode foundParameter = ASTManager.findParameterInMethodDeclaration(node);
									Resolutions.deleteParameter(compilUnit, foundParameter,usage);
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = new ArrayList<IMarker>(ErrorsRetriever.findJavaProblemMarkers(iCompilUnit));

									break;
								case parameterInMiDelete:

									//	ASTNode foundParameterMI = ASTManager.findParameterInMethodDeclaration(node);
									Resolutions.deleteParameterInMI(compilUnit,usage.getNode());
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = new ArrayList<IMarker>(ErrorsRetriever.findJavaProblemMarkers(iCompilUnit));

									break;
								case ImportDelete:

									anImport = ASTManager.findImportDeclaration(node);
									if (anImport != null && anImport instanceof ImportDeclaration) {

										Resolutions.deleteImport(compilUnit, usage.getChange(), node);
										compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation
										// unit
										jVisitor.process(compilUnit);
										Thread.sleep(3000);
										ml = new ArrayList<IMarker>(ErrorsRetriever.findJavaProblemMarkers(iCompilUnit));

									}

									break;

								case MethodInvocTypeDelete:

									Resolutions.resolveMethodInvocDeletedClass(compilUnit, node);
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = new ArrayList<IMarker>(ErrorsRetriever.findJavaProblemMarkers(iCompilUnit));

									break;
								case ClassInstanceDelete:
									ASTNode classInstance = ASTManager.findClassInstanceCreations(node);
									if (((DeleteClass) usage.getChange()).getName()
											.equals(((SimpleName) node).getIdentifier())) {
										if (classInstance != null && classInstance instanceof ClassInstanceCreation) {
											Resolutions.deleteInstanceClass(compilUnit, classInstance);
											compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the
											// compilation unit
											jVisitor.process(compilUnit);
											Thread.sleep(3000);
											ml = new ArrayList<IMarker>(
													ErrorsRetriever.findJavaProblemMarkers(iCompilUnit));

										}
									}

									break;
								case ReturnTypeDelete:

									Resolutions.deleteReturnType(compilUnit, usage.getChange(), node);
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = new ArrayList<IMarker>(ErrorsRetriever.findJavaProblemMarkers(iCompilUnit));

									break;
								case SuperClassDelete:
									Resolutions.deleteSuperClass(compilUnit, usage.getChange(), node);
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);
									break;
								case LiteralDelete:
									Resolutions.deleteLiteral(compilUnit, usage.getChange(), node);
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);
									break;
								case ComplexStatementDelete:
									Resolutions.deleteCompextStatemnt(compilUnit, usage.getChange(), node);
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);
									break;
								case VisitClassMethodDelete:
									Resolutions.DeleteVisitorGetClassMethod(compilUnit, usage.getChange(), node);
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);
									break;
								case GetObjectDelete:
									Resolutions.DeleteVisitorGetClassMethod(compilUnit, usage.getChange(), node);
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);
									break;

								case PropertyDelete:

									Resolutions.DeletePropertyResolution(compilUnit, usage.getChange(), node);
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);
									break;

								case getorSetPropertyDelete:

									Resolutions.DeletePropertyResolution(compilUnit, usage.getChange(), node);
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);
									break;
								case GetorSetMoveProperty:
									if( usage.getChange() instanceof MoveProperty) {
										if (((MoveProperty) usage.getChange()).getUpperBound() == -1)

										{

											MoveResolution.applyResolution(compilUnit, (MoveProperty) usage.getChange(), node,
													2);
											compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation
											// unit
											jVisitor.process(compilUnit);
											Thread.sleep(3000);
											ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);

										} else {

											MoveResolution.applyResolution(compilUnit, (MoveProperty) usage.getChange(), node,
													0);
										}
									}
									if( usage.getChange() instanceof ExtractClass) {
										//MoveResolution.applyResolution(compilUnit, (MoveProperty) usage.getChange(), node,
											//	0);
										
									}
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);
									break;
								case MoveProperty:
									if (((MoveProperty) usage.getChange()).getUpperBound() == -1)

									{

										MoveResolution.applyResolution(compilUnit, (MoveProperty) usage.getChange(), node,
												2);
										compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation
										// unit
										jVisitor.process(compilUnit);
										Thread.sleep(3000);
										ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);
										ASTModificationManager.AddImportDeclaration(compilUnit,
												((MoveProperty) usage.getChange()).getImportPath());

									} else {

										MoveResolution.applyResolution(compilUnit, (MoveProperty) usage.getChange(), node,
												0);
									}
									// Resolutions.MovePropertyResolution(compilUnit, change, node);
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);
									break;
								case PropertyRename:
									Resolutions.renamingPropertyResolution(compilUnit, usage,pattern);
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);
									break;
								case getPropertyRename:
									Resolutions.renamingPropertyResolution(compilUnit, usage,pattern);
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);
									break;
								case setPropertyRename:
									Resolutions.renamingPropertyResolution(compilUnit, usage,pattern);
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);
									break;
								case isPropertyRename:
									Resolutions.renamingPropertyResolution(compilUnit, usage,pattern);
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);
									break;
								case LiteralRename:
									if (usage.getChange() instanceof RenameClass) {
										String before = ASTManager.makeLiteral(((RenameClass) usage.getChange()).getName());
										String after = ASTManager
												.makeLiteral(((RenameClass) usage.getChange()).getNewname());
										System.out.println(" THE BEFORE LITERAL " + before);
										System.out.println(" THE AFTER  LITERAL " + after);
										String res = ((SimpleName) (usage.getNode())).getIdentifier().replace(before,
												after);
										System.out.println(" THE RES LITERAL " + res);
										Resolutions.RenameLiteralResolution(compilUnit, usage.getChange(), node, res);
										compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation
										// unit
										jVisitor.process(compilUnit);
										Thread.sleep(3000);
										ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);

									} else if (usage.getChange() instanceof RenameProperty) {
										String before = ASTManager
												.makeLiteral(((RenameProperty) usage.getChange()).getName());
										String after = ASTManager
												.makeLiteral(((RenameProperty) usage.getChange()).getNewname());
										System.out.println(" THE BEFORE LITERAL " + before);
										System.out.println(" THE AFTER  LITERAL " + after);
										String res = ((SimpleName) (usage.getNode())).getIdentifier().replace(before,
												after);
										System.out.println(" THE RES LITERAL " + res);
										Resolutions.RenameLiteralResolution(compilUnit, usage.getChange(), node, res);
										compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation
										// unit
										jVisitor.process(compilUnit);
										Thread.sleep(3000);
										ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);

									}

									else if (usage.getChange() instanceof MoveProperty) {

										Resolutions.RenameLiteralResolution(compilUnit, usage.getChange(), node,
												((MoveProperty) usage.getChange()).getTargetClassName() + "__"
														+ ((MoveProperty) usage.getChange()).getName());
										compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation
										// unit
										jVisitor.process(compilUnit);
										Thread.sleep(3000);
										ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);
									}
									break;
								case propertyPush:
									Resolutions.PushResolution(compilUnit, usage.getChange(), node);
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);

									break;
								case getPropertyPush:
									// Resolutions.PushGetorSetResolution(compilUnit, usage.getChange(), node);

									Resolutions.applyResolutionIntroduceIfCastNew(compilUnit, usage.getChange(), node);
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);
									Resolutions.addImportD(ListICompilUnit,
											((PushProperty) usage.getChange()).getSubClassesNames(), compilUnit);
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);

									break;
								case setPropertyPush:
									Resolutions.PushGetorSetResolution(compilUnit, usage.getChange(), node);
									// Resolutions.applyResolutionIntroduceIfCastNew(compilUnit,usage.getChange(),
									// node);
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);

									break;
								case LiteralPush:
									Resolutions.PushLiteralResolution(compilUnit, usage.getChange(), node);
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);

									break;
								case PropertyPushMD:
									// Resolutions.PushGetorSetResolution(compilUnit, usage.getChange(), node);
									Resolutions.removePushedPropertyMD(compilUnit, usage.getChange(), node);
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);

									break;

								case GeneralizeProperty:
									// Resolutions.PushGetorSetResolution(compilUnit, usage.getChange(), node);
									Resolutions.GeneralizationResolution(compilUnit, usage.getChange(), usage.getNode());
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);

									break;

								case GeneralizePropertyBefore:
									// Resolutions.PushGetorSetResolution(compilUnit, usage.getChange(), node);
									Resolutions.GeneralizationBeforeResolution(compilUnit, usage.getChange(),
											usage.getNode());
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);
								case ChangeType:
									// Resolutions.PushGetorSetResolution(compilUnit, usage.getChange(), node);
									Resolutions.ChangeTypeResolution(compilUnit, usage.getChange(), usage.getNode());
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);
									ArrayList<String> classess = new ArrayList<String>();
									classess.add(((SetProperty) usage.getChange()).getNewType());
									Resolutions.addImportD(ListICompilUnit,classess, compilUnit);
									compilUnit = ASTManager.getCompilationUnit(iCompilUnit); // Refresh the compilation unit
									jVisitor.process(compilUnit);
									Thread.sleep(3000);
									ml = ErrorsRetriever.findJavaProblemMarkers(iCompilUnit);

									break;
								default:
									// int offset = amarker.getAttribute(IMarker.CHAR_START, 0);
									//
									// int end = amarker.getAttribute(IMarker.CHAR_END, 0);
									//
									//
									// int length = end + 1 - offset;
									// IInvocationContext context = new AssistContext(iCompilUnit , offset, length);
									// ArrayList<IJavaCompletionProposal> proposals = new
									// ArrayList<IJavaCompletionProposal>();
									// problems = compilUnit.getProblems();
									// iProblem= ErrorsRetriever.getEquivalentProblem(problems, amarker);
									// System.out.println("problem: " + iProblem.getSourceStart()+ "
									// "+iProblem.getSourceEnd()+" "+ iProblem.getSourceLineNumber());
									//
									// ProblemLocation problem = new ProblemLocation(iProblem);
									// JavaCorrectionProcessor.collectCorrections(context, new IProblemLocation[] {
									// problem }, proposals);
									// for ( IJavaCompletionProposal ijp : proposals)
									// {
									// System.out.println(" PROPOSAL "+ijp.getDisplayString());
									//
									//
									// }

									break;

								}
							}
						} else {

								System.out.println(
										" in else NOT TTREATED ++++++++++++++++++++++++++++++++++++++++++++++++ ");
								System.out.println(usage.getNode());
								System.out.println(" ITS TYPE   " + usage.getNode().getClass());

								break;
							}

						//}

					}

				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println(" END OF EXECUTION");
	}

	public static IJavaCompletionProposal hasQuickFix(ICompilationUnit iCompilUnit, IMarker amarker,
			IProblem iProblem) {
		int offset = amarker.getAttribute(IMarker.CHAR_START, 0);

		int end = amarker.getAttribute(IMarker.CHAR_END, 0);

		int length = end + 1 - offset;
		QuickAssistsProcessorGetterSetter qa = new QuickAssistsProcessorGetterSetter();
		IInvocationContext context = new AssistContext(iCompilUnit, offset, length);
		ArrayList<IJavaCompletionProposal> proposals = new ArrayList<IJavaCompletionProposal>();

		if (iProblem != null) {

			ProblemLocation problem = new ProblemLocation(iProblem);
			System.out.println(" for the problem " + problem);
			long start1 = System.currentTimeMillis();

			// System.out.println("the context is " + context.
			JavaCorrectionProcessor.collectCorrections(context, new IProblemLocation[] { problem }, proposals);

			long end1 = System.currentTimeMillis();
			System.out.println(" The time is IS " + (end1 - start1));
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (IJavaCompletionProposal ijp : proposals) {

				if (ijp.getDisplayString().equals("Add unimplemented methods")
						|| ijp.getDisplayString().contains("Import '")) {
					return ijp;
				}

			}
		}
		return null;
	}

}
