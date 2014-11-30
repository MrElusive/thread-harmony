package edu.texas.threadharmony;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.ICombinatoricsVector;

public class InterleavingIterator implements Iterator<Interleaving> {

    List<InterleafCountCompositionIterator> interleafCountCompositionIterators;
    
	Map<Long, InterleafCountComposition> currentInterleafCountCompositionMap;
	Iterator<ICombinatoricsVector<Long>> currentThreadOrderIterator;
	
	ICombinatoricsVector<Long> nextThreadOrder;
	
	public InterleavingIterator(List<THThread> threads) {
		
		assert(!threads.isEmpty());
		
		interleafCountCompositionIterators = new ArrayList<InterleafCountCompositionIterator>(threads.size());
		currentInterleafCountCompositionMap = new HashMap<Long, InterleafCountComposition>();
		
		List<Long> threadExecutionEvents = new ArrayList<Long>();
		
		for (THThread thread : threads) {
			assert(thread.getNumberOfInterleaves() > 0);
			
			InterleafCountCompositionIterator interleafCountCompositionIterator = new InterleafCountCompositionIterator(thread);
			InterleafCountComposition interleafCountComposition = interleafCountCompositionIterator.next(); 
			
			interleafCountCompositionIterators.add(interleafCountCompositionIterator);
			currentInterleafCountCompositionMap.put(thread.getId(), interleafCountComposition);
			
			threadExecutionEvents.addAll(Collections.nCopies(interleafCountComposition.getSize(), thread.getId()));
		}
		
		currentThreadOrderIterator = Factory.createPermutationGenerator(Factory.createVector(threadExecutionEvents)).iterator();
		nextThreadOrder = currentThreadOrderIterator.next();
	}

	@Override
	public boolean hasNext() {
		return this.currentThreadOrderIterator.hasNext() || !this.interleafCountCompositionIterators.isEmpty();
	}

	@Override
	public Interleaving next() {
		
		resetInterleafCountCompositionMap();
		Interleaving interleaving = new Interleaving(Factory.createVector(nextThreadOrder), new HashMap<Long, InterleafCountComposition>(currentInterleafCountCompositionMap));
		
		getNextInterleavingComponents();
		
		return interleaving;
	}

	private void getNextInterleavingComponents() {
		boolean foundNextThreadOrder = false;
		
		while (!foundNextThreadOrder && hasNext()) {			
			
			while (currentThreadOrderIterator.hasNext()) {
				ICombinatoricsVector<Long> threadOrder = currentThreadOrderIterator.next();
				boolean foundConsecutiveExecutionsOfSameThread = false;
				for (int i = 0; i < threadOrder.getSize() - 1 && !foundConsecutiveExecutionsOfSameThread; i++) {
					if (threadOrder.getValue(i).equals(threadOrder.getValue(i + 1))) {
						foundConsecutiveExecutionsOfSameThread = true;
					}
				}
				
				if (foundConsecutiveExecutionsOfSameThread) {
					continue;
				} else {			
					this.nextThreadOrder = threadOrder;
					foundNextThreadOrder = true;
					break;
				}
			}
			
			if (!foundNextThreadOrder) {
				createNextInterleafCountCompositionMap();
				createNextThreadOrderIterator();
			}
		
		}
	}

	private void resetInterleafCountCompositionMap() {
		for (Long threadId : currentInterleafCountCompositionMap.keySet()) {
			currentInterleafCountCompositionMap.get(threadId).reset();
		}
	}

	private void createNextThreadOrderIterator() {
		if (interleafCountCompositionIterators.isEmpty()) {
			return;
		}
		List<Long> threadExecutionEvents = new ArrayList<Long>();
		
		for (Long threadId : currentInterleafCountCompositionMap.keySet()) {
			threadExecutionEvents.addAll(Collections.nCopies(currentInterleafCountCompositionMap.get(threadId).getSize(), threadId));
		}
		
		currentThreadOrderIterator = Factory.createPermutationGenerator(Factory.createVector(threadExecutionEvents)).iterator();
		//nextThreadOrder = currentThreadOrderIterator.next();
	}

	private void createNextInterleafCountCompositionMap() {
		
		boolean allIteratorsAreDone = true;
		
		for (int i = 0; i < interleafCountCompositionIterators.size() && allIteratorsAreDone; i++) {
			InterleafCountCompositionIterator iterator = interleafCountCompositionIterators.get(i);
			
			if (iterator.hasNext()) {
				allIteratorsAreDone = false;
			} else {
				iterator = new InterleafCountCompositionIterator(iterator.getThread());
				interleafCountCompositionIterators.set(i, iterator);
			}
             
			currentInterleafCountCompositionMap.put(iterator.getThread().getId(), iterator.next());
		}
		
		if (allIteratorsAreDone) {
			interleafCountCompositionIterators.clear();
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
