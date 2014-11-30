package edu.texas.threadharmony;

import java.util.Iterator;
import java.util.Map;

import org.paukov.combinatorics.ICombinatoricsVector;

public class Interleaving implements Iterable<ContextSwitch> {
	private ICombinatoricsVector<Long> threadOrder;
	private Map<Long, InterleafCountComposition> interleafCountCompositionMap;
	
	public Interleaving(ICombinatoricsVector<Long> threadOrder, Map<Long, InterleafCountComposition> currentInterleafCountCompositionMap) {
		this.threadOrder = threadOrder;
		this.interleafCountCompositionMap = currentInterleafCountCompositionMap;
	}


	@Override
	public Iterator<ContextSwitch> iterator() {
		return new ContextSwitchIterator(this.threadOrder, this.interleafCountCompositionMap);
	}
}
