package kanga.kcae.object;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

public class CollectionComparator<T extends Comparable<T>>
    implements Comparator<Collection<T>>
{
	@Override
    public int compare(
        final Collection<T> c1,
        final Collection<T> c2)
    {
        if (c1 == null) {
            if (c2 == null) { return 0; }
            else            { return -1; }
        }
        else if (c2 == null) {
            return 1;
        }

        final Iterator<T> i1 = c1.iterator();
        final Iterator<T> i2 = c2.iterator();
        while (i1.hasNext() && i2.hasNext()) {
            final T el1 = i1.next();
            final T el2 = i2.next();
            int elCompare = el1.compareTo(el2);
            
            if (elCompare != 0) {
                return elCompare;
            }
        }

        if (i1.hasNext())      { return 1; }
        else if (i2.hasNext()) { return -1; }
        else                   { return 0; }
    }
}
