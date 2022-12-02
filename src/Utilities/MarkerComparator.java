package Utilities;

import java.util.Comparator;

import org.eclipse.core.resources.IMarker;

public class MarkerComparator  implements Comparator<IMarker> {
  
    // override the compare() method
    public int compare(IMarker m1, IMarker m2)
    { 
        if (m1.getAttribute(IMarker.LINE_NUMBER, 0)== m2.getAttribute(IMarker.LINE_NUMBER, 0))
            return 0;
        else if (m1.getAttribute(IMarker.LINE_NUMBER, 0)> m2.getAttribute(IMarker.LINE_NUMBER, 0))
            return 1;
        else
            return -1;
    }
} 


 