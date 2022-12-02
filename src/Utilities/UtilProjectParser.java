package Utilities;

import java.util.ArrayList;

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class UtilProjectParser {
	
	
	public static IPath getSelectedProjectPath()
	{
		IPath path=null;
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	    if (window != null)
	    {
	        IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
	        Object firstElement = selection.getFirstElement();
	        if (firstElement instanceof IAdaptable)
	        {
	            IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
	            path = project.getLocation();
	            System.out.println(path);
	        }
	    }
	    return path;
	}
	public static IProject getSelectedProject()
	{
		 IProject project=null;
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	    if (window != null)
	    {
	        IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
	        Object firstElement = selection.getFirstElement();
	        if (firstElement instanceof IAdaptable)
	        {
	             project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
	            IPath path = project.getLocation();
	            System.out.println(path);
	        }
	    }
	    return project;
		
	}
	

	public static IJavaProject getJavaProject(IProject project) {
        
        IJavaProject javaProj = JavaCore.create(project);
        if (javaProj != null && javaProj.exists()) {
            return javaProj;
        }
        // current project is not a Java project
        
        return null; 
    }
	
public static ArrayList<ICompilationUnit> getCompilationUnits(IProject project) {
		
		ArrayList<ICompilationUnit> units = new ArrayList<ICompilationUnit>();
		
		try {
			
			if (project.hasNature(JavaCore.NATURE_ID)) {
			    IJavaProject javaProject =getJavaProject(project);
			    javaProject.open(null);
			    //javaProject.getResource().refreshLocal(IResource.DEPTH_INFINITE, null);
			    
			    for(IPackageFragment packageFrag : javaProject.getPackageFragments()){
			    	
			    	if(packageFrag.getPath().getFileExtension() == null){
			    		//System.out.println("package fragment >> "+packageFrag);
			    		
			    		for(IJavaElement javaEle : packageFrag.getChildren()){
			    			//System.out.println("comp unit"+ javaEle.getClass());
			    			if(javaEle instanceof ICompilationUnit){//alternativley check if the element kind is == 5, it is CompilationUnit
			    				ICompilationUnit compilUnit = (ICompilationUnit) javaEle;
			    				//Next instruction is another way to get ICompilationUnit
			    		//	ICompilationUnit compilUnit= JavaCore.createCompilationUnitFrom((IFile)javaEle.getCorrespondingResource());

			    				if (compilUnit != null) {
			    					
			    					units.add(compilUnit);
			    					
			    				}
			    			}
			    		}


			    	} // we ignore the jar files of a project under src folder else if(!packageFrag.getPath().getFileExtension().equals("jar")){}
			    
			    }
			}
			
		} catch(ResourceException e){
			System.out.println("\n ******** Project does not existsi or is not java ******** \n");
			e.printStackTrace();
		}
		catch (JavaModelException e) {
			// TODO Auto-generated catch block
			//here treat if a java class could not be oppened
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			//here is when the project is not java >> put a pop up frame saying it
			e.printStackTrace();
		}
		
		return units;
	}


}
