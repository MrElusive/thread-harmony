package edu.texas.threadharmony;

import java.util.Iterator;

import org.paukov.combinatorics.ICombinatoricsVector;

public class InterleafCountComposition implements Iterator<Integer> {
	
	private ICombinatoricsVector<Integer> instructionCountVector;
	private Iterator<Integer> instructionCountIterator;

	public InterleafCountComposition(ICombinatoricsVector<Integer> instructionCountVector) {
		this.instructionCountVector = instructionCountVector;
		this.instructionCountIterator = instructionCountVector.iterator();
	}

	public boolean hasNext() {
		return instructionCountIterator.hasNext();
	}

	@Override
	public Integer next() {
		return instructionCountIterator.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public int getSize() {
		return this.instructionCountVector.getSize();
	}

	public void reset() {
		this.instructionCountIterator = instructionCountVector.iterator();
	}

}
