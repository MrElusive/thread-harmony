package tests;

import edu.texas.threadharmony.Interleavable;

public class SingleSharedVariableTest {

	static int myVariable = 0, test = 0;
	int variable = 1;

	@Interleavable
	public void assignSevenToVariableAndPrint() {
		System.out.println("hello");
		int blah = 0;
		myVariable = 7;
		variable = 0;
		assert(myVariable == 7);
	}
	
	@Interleavable
	public void assignFiveToVariableAndPrint() {
		int barney = 0;
		if (barney == 0) {
			barney = 1;
		}
		myVariable = 5;
		assert(myVariable == 5);
	}
	
	public static void main(String[] args) {
		
	}
}
