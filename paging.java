/* Student Name: Matthew Tessler */
import java.util.*;

public class paging {
	public static void main(String[] args) {
		/* initialize input vars */
		int machineSize = Integer.parseInt(args[0]);
		int pageSize = Integer.parseInt(args[1]);
		int processSize = Integer.parseInt(args[2]);
		int jobMix = Integer.parseInt(args[3]);
		int numOfReferences = Integer.parseInt(args[4]);
		String replacementAlgo = args[5];
		int debugging = Integer.parseInt(args[6]);

		/* print input vars */
		System.out.println("\nMachine size is " + machineSize + ".");
		System.out.println("The page size is " + pageSize + ".");
		System.out.println("The process size is " + processSize + ".");
		System.out.println("The job mix number is " + jobMix + ".");
		System.out.println("The number of references per process is " + numOfReferences + ".");
		System.out.println("The replacement algorithm is " + replacementAlgo + ".");
		System.out.println("The level of debugging output is " + debugging + ".\n");

		FrameTable machine = new FrameTable(machineSize, pageSize);
	}
}


// a machine organized as a frame table divided up into frames
class FrameTable {
	Frame[] frames;

	// instantiates an array of frame objects with length machine size divided by page size
	public FrameTable(int machineSize, int pageSize) {
		this.frames = new Frame[machineSize/pageSize];
		for (int i = 0; i < frames.length; i++) {
			this.frames[0] = new Frame(pageSize);
		}
	}
}

// frame within frame table of machine
class Frame {
	Page onePage;

	public Frame(int pageSize) {
		this.onePage = new Page(pageSize);
	}
}

// holds information about specific page from process
class Page {
	int[] references;

	public Page(int pageSize) {
		this.references = new int[pageSize]; 
	}
}