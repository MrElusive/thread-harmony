package edu.texas.threadharmony;

import java.util.Iterator;
import java.util.Map;

import org.paukov.combinatorics.ICombinatoricsVector;

public class ContextSwitchIterator implements Iterator<ContextSwitch> {

	private Iterator<Long> threadOrderIterator;
	private Map<Long, InterleafCountComposition> interleafCountCompositionMap;
	
	public ContextSwitchIterator(ICombinatoricsVector<Long> threadOrder, Map<Long, InterleafCountComposition> interleafCountCompositionMap) {
		this.threadOrderIterator = threadOrder.iterator();
		this.interleafCountCompositionMap = interleafCountCompositionMap;
	}

	@Override
	public boolean hasNext() {		
		return threadOrderIterator.hasNext();
	}

	@Override
	public ContextSwitch next() {
		Long threadId = threadOrderIterator.next();
		return new ContextSwitch(threadId, interleafCountCompositionMap.get(threadId).next());
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
