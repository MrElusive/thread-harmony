package tests;

public class TestImportClass {

	public void followMethod() {
		System.out.println("Running the follow method");
		int accumulate = 0;
		
		for (int i = 0; i < 5; i++) {
			accumulate += i;
		}
		
		System.out.println("Total: " + accumulate);		
	}
}
