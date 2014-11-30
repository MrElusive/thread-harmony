package edu.texas.threadharmony;

public class ContextSwitch {
	long threadId;
	int numberOfInstructions;
	
	public ContextSwitch(long threadId, int numberOfInstructions) {
		this.threadId = threadId;
		this.numberOfInstructions = numberOfInstructions;
	}
	
	public long getThreadId() {
		return threadId;
	}
	
	public int getNumberOfInstructions() {
		return numberOfInstructions;
	}

	@Override
	public String toString() {
		return "Interleaving [threadId=" + threadId + ", numberOfInstructions="
				+ numberOfInstructions + "]";
	}	
	
	
}
