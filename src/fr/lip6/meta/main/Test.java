package fr.lip6.meta.main;

import java.util.ArrayList;

import fr.lip6.meta.ComplexChangeDetection.Change;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.ChangeUpperBound;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.DeleteClass;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.DeleteProperty;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.RenameClass;
import fr.lip6.meta.ComplexChangeDetection.AtomicChanges.RenameProperty;
import fr.lip6.meta.ComplexChangeDetection.ComplexChanges.InverseMoveProperty;
import fr.lip6.meta.ComplexChangeDetection.ComplexChanges.MoveProperty;
import fr.lip6.meta.ComplexChangeDetection.ComplexChanges.PushProperty;

public class Test {

	public static void main(String[] args) {
		
		// old name, new name, package name
	    RenameClass renameClass = new RenameClass("Test","Testing","mainTest"); 
	    RenameClass renameClass1 = new RenameClass("Convert","ConvertV2","mainTest");
	    RenameClass renameClass2 = new RenameClass("ConvertInteger","ConvertIntegerV2","mainTest");
	    
	    //old name, new name, class
	    RenameProperty renameProperty = new RenameProperty("printOLD","display","Test");
	    
	    //name, source class, target class
	    MoveProperty move = new MoveProperty("print","Test","TestingV2");
	    //ref between source and target in that direction (owned by source, with type of target)
	    move.setThroughReference("getTestingV2");
	    
	    MoveProperty move1 = new MoveProperty("author","Book","Chapter");
	    move1.setThroughReference("getChapters");
	    
	    ArrayList<String> subClass = new ArrayList<String>();
	    subClass.add("Comic");
	    PushProperty push = new PushProperty("printOLD","Test", subClass);
	    PushProperty push1 = new PushProperty("volume","Book", subClass);
	    
	    DeleteProperty deleteProperty = new DeleteProperty("print", "Test");
	    DeleteProperty deleteProperty2 = new DeleteProperty("gogoList", "Tata");
	    DeleteProperty deleteProperty3 = new DeleteProperty("toto", "Tata");
	    DeleteProperty deleteProperty4 = new DeleteProperty("gogo", "Tata");
	    
	    DeleteProperty deleteProperty5 = new DeleteProperty("upper", "MultiplicityElement");
	    DeleteProperty deleteProperty6 = new DeleteProperty("pageNbr", "Chapter");
	    
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
	    changes.add(deleteClass1);
	    changes.add(deleteClass2);
	    
	    changes.add(upper);
	    changes.add(inverse);
		
		changes.add(deleteProperty6);
	    changes.add(move1);
	    changes.add(push1);
	    
	

	}

}
