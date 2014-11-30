package edu.texas.threadharmony;

import java.util.Iterator;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.ICombinatoricsVector;

public class InterleafCountCompositionIterator implements Iterator<InterleafCountComposition> {

	private THThread thread;
	private Iterator<ICombinatoricsVector<Integer>> iterator;

	public InterleafCountCompositionIterator(THThread thread) {
		this.thread = thread;
		iterator = Factory.createCompositionGenerator(thread.getNumberOfInterleaves()).iterator();
	}

	public InterleafCountComposition next() {
		return new InterleafCountComposition(iterator.next());
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public THThread getThread() {
		return thread;
	}

}
