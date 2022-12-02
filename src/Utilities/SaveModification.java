package Utilities;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

public class SaveModification {
	
	public static void SaveModif(CompilationUnit compilUnit, TextEdit edits)
	{
		
		  ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager(); 
		  IPath pathcu = compilUnit.getJavaElement().getPath();
	         try { 			    			 		
	        	 // connect the path 			    
	        	 bufferManager.connect(pathcu, null);   
	        	 ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(pathcu); 	
	        //	 TextEdit edit3 = rewriter1.rewriteAST(document, null); 
	        	 System.out.println("text edit content  " +edits.hasChildren()); 	
	        	 // apply the changes to the document 		
	        	 IDocument documenti = textFileBuffer.getDocument(); 
	        	 edits.apply(documenti);  			    
	        	 // write the changes from the buffer to the file 	
	        	 textFileBuffer.commit(null /* ProgressMonitor */, false /* Overwrite */);
	        
	        	 } 
	         catch (MalformedTreeException e) { 	
	        	 e.printStackTrace(); 			  
	        	 } catch (BadLocationException e) { 
	        		 e.printStackTrace(); 			  
	        		 } catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally
	         { 			    	
	        			 // disconnect the path 	
	        			 try {
							bufferManager.disconnect(pathcu, null);
						} catch (CoreException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 	
	        			 } 	
	}

}
