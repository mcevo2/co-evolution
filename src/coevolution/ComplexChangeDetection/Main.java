package coevolution.ComplexChangeDetection;

import java.io.File;
import java.util.ArrayList;

import coevolution.ComplexChangeDetection.AtomicChanges.AtomicChange;
import coevolution.ComplexChangeDetection.AtomicChanges.ChangeUpperBound;
import coevolution.ComplexChangeDetection.AtomicChanges.DeleteClass;
import coevolution.ComplexChangeDetection.AtomicChanges.DeleteProperty;
import coevolution.ComplexChangeDetection.AtomicChanges.RenameClass;
import coevolution.ComplexChangeDetection.AtomicChanges.RenameProperty;
import coevolution.ComplexChangeDetection.ComplexChanges.ComplexChange;
import coevolution.ComplexChangeDetection.ComplexChanges.ExtractClass;
import coevolution.ComplexChangeDetection.ComplexChanges.ExtractSuperClass;
import coevolution.ComplexChangeDetection.ComplexChanges.FlattenHierarchy;
import coevolution.ComplexChangeDetection.ComplexChanges.InlineClass;
import coevolution.ComplexChangeDetection.ComplexChanges.InverseMoveProperty;
import coevolution.ComplexChangeDetection.ComplexChanges.MoveProperty;
import coevolution.ComplexChangeDetection.ComplexChanges.PullProperty;
import coevolution.ComplexChangeDetection.ComplexChanges.PushProperty;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		RenameClass renameClass = new RenameClass("Test","Testing","mainTest");
	    RenameClass renameClass1 = new RenameClass("Convert","ConvertV2","mainTest");
	    RenameClass renameClass2 = new RenameClass("ConvertInteger","ConvertIntegerV2","mainTest");
	    
	    RenameProperty renameProperty = new RenameProperty("printOLD","display","Test");
	    
	    MoveProperty move = new MoveProperty("print","Test","TestingV2");
	    move.setThroughReference("getTestingV2");
	    
	    ArrayList<String> subClass = new ArrayList<String>();
	    subClass.add("SubTest");
	    PushProperty push = new PushProperty("printOLD","Test", subClass);
	    
	    DeleteProperty deleteProperty = new DeleteProperty("print", "Test");
	    DeleteProperty deleteProperty2 = new DeleteProperty("gogoList", "Tata");
	    DeleteProperty deleteProperty3 = new DeleteProperty("toto", "Tata");
	    DeleteProperty deleteProperty4 = new DeleteProperty("gogo", "Tata");
	    
	    DeleteProperty deleteProperty5 = new DeleteProperty("upper", "MultiplicityElement");
	    
	    DeleteClass deleteClass = new DeleteClass("Test");
	    DeleteClass deleteClass1 = new DeleteClass("Convert");
	    DeleteClass deleteClass2 = new DeleteClass("ConvertInteger");
	    
	    ChangeUpperBound upper = new ChangeUpperBound("djamel3", "Game", "1", "-1");
	    
	    InverseMoveProperty inverse = new InverseMoveProperty("player", "Team", "Game");//InverseMoveProperty("size", "Player", "Team");
	    		
	    ArrayList<Change> changes = new ArrayList<Change>();
	    changes.add(renameClass);
	    changes.add(renameClass1);
	    changes.add(renameClass2);
	    
	    changes.add(renameProperty);
	    
	    changes.add(move);
	    changes.add(push);
	    
	    changes.add(deleteProperty);
	    changes.add(deleteProperty2);
	    changes.add(deleteProperty3);
	    changes.add(deleteProperty4);
	    changes.add(deleteProperty5);
	    changes.add(deleteClass);
	    
	    changes.add(upper);
	    changes.add(inverse);
		
	}

}
