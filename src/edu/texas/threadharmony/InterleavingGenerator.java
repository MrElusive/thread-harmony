package edu.texas.threadharmony;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InterleavingGenerator implements Iterable<Interleaving> {
	
	private List<THThread> threads;
	
	public InterleavingGenerator(List<THThread> threads) {
		this.threads = threads;
	}	

	@Override
	public Iterator<Interleaving> iterator() {
		return new InterleavingIterator(this.threads);
	}
	
	public static void main(String[] args) {
		List<THThread> threads = new ArrayList<THThread>();
		//threads.add(new THThread());
		//threads.add(new THThread());
		//threads.add(new THThread(4));
		//threads.add(new THThread(4));
		
		InterleavingGenerator generator = new InterleavingGenerator(threads);
		
		int totalInterleavings = 0;
		int totalContextSwitches = 0;
		for (Interleaving interleaving : generator) {
            totalInterleavings++;
			//System.out.println("New Interleaving");
			//for (ContextSwitch contextSwitch : interleaving) {

				//totalContextSwitches++;
			//	System.out.println(contextSwitch);
			//}
			//System.out.println("");
		}
		System.out.println(totalInterleavings);
		System.out.println(totalContextSwitches);
	}
}
