package Utilities;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.compiler.IProblem;

public class ErrorsRetriever {
	
	// public static IMarker[] findJavaProblemMarkers(ICompilationUnit cu) 
	public static ArrayList<IMarker> findJavaProblemMarkers(ICompilationUnit cu) 

		      throws CoreException {

	

		 //System.out.println(" Compilation unit path : "+ cu.getPath());

		  

		      IResource javaSourceFile =  cu.getResource();//cu.getUnderlyingResource();
		     // javaSourceFile.
		  
		    //  javaSourceFile.f
		      IMarker[] markers = 

		         javaSourceFile.findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER,

		            true, IResource.DEPTH_ZERO);
		      
		      
		      ArrayList<IMarker> list2 = new ArrayList<IMarker>();
		      for(IMarker m:markers) {
		    	  if (m.getAttribute(IMarker.SEVERITY, -1)==2)
		    	  {
		    		//  System.out.println("IT IS AN ERROR");
		         list2.add(m);
		    	  }
		      }

		    // if(markers.length==0)

		        // System.out.println("No error detected ");

		      return list2;



		   }
	public static IProblem getEquivalentProblem(IProblem[] problems, IMarker marker)
	{
		
		int start =marker.getAttribute(IMarker.CHAR_START, 0);
		int end =marker.getAttribute(IMarker.CHAR_END, 0)-1;
		int line=marker.getAttribute(IMarker.LINE_NUMBER, 0);



		for(IProblem problem : problems) {
			   if(problem.getSourceStart()== start && problem.getSourceEnd()==end && problem.getSourceLineNumber()==line)
			   {
				  return problem;			   }
		}
		
		return null;
	}

}
