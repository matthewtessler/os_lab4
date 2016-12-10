/* Student Name: Matthew Tessler */
import java.util.*;

public class paging {
	public static void main(String[] args) {
		/* initialize input vars */
		int machineSize = Integer.parseInt(args[0]); // M
		int pageSize = Integer.parseInt(args[1]); // P
		int processSize = Integer.parseInt(args[2]); // S
		int jobMix = Integer.parseInt(args[3]); // J
		int numOfReferences = Integer.parseInt(args[4]); // N
		String replacementAlgo = args[5]; // R
		int debugging = Integer.parseInt(args[6]); // no letter

		/* print input vars */
		System.out.println("\nMachine size is " + machineSize + ".");
		System.out.println("The page size is " + pageSize + ".");
		System.out.println("The process size is " + processSize + ".");
		System.out.println("The job mix number is " + jobMix + ".");
		System.out.println("The number of references per process is " + numOfReferences + ".");
		System.out.println("The replacement algorithm is " + replacementAlgo + ".");
		System.out.println("The level of debugging output is " + debugging + ".\n");

		/* stores frames that hold individual pages from different processes */
		FrameTable machine = new FrameTable(machineSize, pageSize);
		Process processes[];
		
		/* if jobMix is one there is only one process and it makes N references sequentially */
		if (jobMix == 1) {
			processes = new Process[1];
			processes[0] = new Process(processSize, pageSize, 0);
		}
		/* if jobMix is two there are four processes -> each process makes references
		   within itself sequentially, making three references each in a round robin style between
		   the four processes -> the initial reference is determined by a formula dependent on the 
		   number of that process and the size of all individual processes (processSize - S) */
		else if (jobMix == 2) {

		}
		/* if jobMix is three it is like jobMix two except references are not sequential, they are 
		   determined by randomOS -> the initial reference is determined by same formula as jobMix two */
		else if (jobMix == 3) {

		}
		/* if jobMix is four it is still three references per process in round robin except the number
		   it is referencing is randomly selected */
		else if (jobMix == 4) {

		}
		else {
			System.out.println("Job mix input error.");
			System.exit(0);
		}
	}
}


// a machine organized as a frame table divided up into frames
class FrameTable {
	Frame[] frames;

	// instantiates an array of frame objects with length machine size divided by page size
	public FrameTable(int machineSize, int pageSize) {
		this.frames = new Frame[machineSize/pageSize];
		for (int i = 0; i < frames.length; i++) {
			this.frames[i] = new Frame(pageSize);
		}
	}

	public static void print(Frame[] frames) {
		System.out.println("Frame Table");
		System.out.print("+---------+");;
		for (int i = 0; i < frames[0].onePage.references.length; i++) {
			System.out.print("---+");
		}
		System.out.println("");
		for (int i = 0; i < frames.length; i++) {
			System.out.print("| Frame " + i + " |");	
			for (int j = 0; j < frames[i].onePage.references.length; j++) {
				System.out.print(" " + frames[i].onePage.references[j] + " |");
			}
			System.out.print("\n+---------+");
			for (int k = 0; k < frames[0].onePage.references.length; k++) {
				System.out.print("---+");
				if (k == frames[0].onePage.references.length - 1) {
					System.out.println("");
				}
			}
		}
		System.out.println("");
	}
}

// frame within frame table of machine
class Frame {
	Page onePage;

	public Frame(int pageSize) {
		this.onePage = new Page(pageSize, -1);
	}
}

// individual page of a process
class Page {
	int[] references; // holds individual memory references 
	int process; // holds process number (which process is this page from?)

	public Page(int pageSize, int processNum) {
		this.references = new int[pageSize];
		this.process = processNum; 
	}
}

// individual processes have an array of pages dependent on processSize and pageSize
class Process {
	Page[] pages; // an array with size of processSize divided by pageSize

	// instantiates array of pages each having pageSize number of references
	public Process(int processSize, int pageSize, int processNum) {
		this.pages = new Page[processSize/pageSize];
		for (int i = 0; i < pages.length; i++) {
			this.pages[i] = new Page(pageSize, processNum);
			// gives references 0 through page size minus 1
			for (int j = 0; j < this.pages[i].references.length; j++) {
				this.pages[i].references[j] = i * (10) + j;
			}
		}
	}

	public static void print(Page[] pages) {
		System.out.println("Process Reference Table");
		System.out.print("+---------+");;
		for (int i = 0; i < pages[0].references.length; i++) {
			System.out.print("----+");
		}
		System.out.println("");
		for (int i = 0; i < pages.length; i++) {
			System.out.printf("|Frame %3d|",i);	
			for (int j = 0; j < pages[i].references.length; j++) {
				System.out.printf("%4d|", pages[i].references[j]);
			}
			System.out.print("\n+---------+");
			for (int k = 0; k < pages[0].references.length; k++) {
				System.out.print("----+");
				if (k == pages[0].references.length - 1) {
					System.out.println("");
				}
			}
		}
		System.out.println("");
	}
}