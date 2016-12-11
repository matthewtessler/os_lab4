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
		Process processes[] = new Process[1];
		
		/* if jobMix is one there is only one process and it makes N references sequentially */
		if (jobMix == 1) {
			processes = new Process[1];
			processes[0] = new Process(processSize, pageSize, 0);
		}
		else if (jobMix == 2 || jobMix == 3 || jobMix == 4) {
			processes = new Process[4];
			for (int i = 0; i < processes.length; i++) {
				processes[i] = new Process(processSize, pageSize, i);
			}
		}
		
		// loop through processes until all have made required number of references
		int current = 0; 
		int time = 0;
		while (!allReferencesFulfilled(processes,numOfReferences)) {
			for (int i = 0; i < 3; i++) {
				if (processes[current].referenceCount + (3 - i) > numOfReferences) {
					continue;
				}

				/* Make references for process */
				System.out.print(current + " references word " + processes[current].currentReference + " at time " + time + ": ");
				// If current reference is in a page in the frame table belonging to current process
				if (referenceisInFrameTable(machine, processes, current, processes[current].currentReference)) {
					// update last usage time for page in current process
					processes[current].lastUsageTime[(processes[current].currentReference)/10] = time;
				}
				else {
					// if there is an empty frame to put the page containing the reference
					if (findHighestEmptyFrame(machine) != -1) {
						// put page in highest empty frame and set load time
						int highestEmptyFrame = findHighestEmptyFrame(machine);
						System.out.print(" using free frame " + highestEmptyFrame + ".\n");
						machine.frames[highestEmptyFrame].empty = false;
						for (int j = 0; j < machine.frames[highestEmptyFrame].onePage.references.length; j++) {
							machine.frames[highestEmptyFrame].onePage.references[j] = processes[current].pages[processes[current].currentReference/10].references[j];
						}
						machine.frames[highestEmptyFrame].onePage.process = current;
						processes[current].pageLoadTime[processes[current].currentReference/10] = time;
					}
					else {
						System.out.print("evicting page (not implemented yet).\n");
						// page replacement algorithm
							// store eviction info, residency time, eviction count for process, load time for new page
					}
				}
				// find next reference for the process dependent on jobMix
				if (jobMix == 1 || jobMix == 2) {
					// sequential
					processes[current].currentReference = (processes[current].currentReference + 1 + processSize) % processSize;
				}
				else if (jobMix == 3) {
					// random, next reference is (rand) mod S
				}
				else if (jobMix == 4) {
					// something else
				}
				processes[current].referenceCount++;
				time++;
			}
			if (current == processes.length - 1) {
				current = 0;
			}
			else {
				current++;
			}
		}
	}

	// prints the frame table and all process tables
	public static void print(FrameTable machine, Process[] processes) {
		FrameTable.print(machine.frames);
		Process.printAllProcesses(processes);
	}

	// if all processes have made number of references required, return true, else false 
	public static boolean allReferencesFulfilled(Process[] processes, int numOfReferences) {
		for (int i = 0; i < processes.length; i++) {
			if (processes[i].referenceCount < numOfReferences) {
				return false;
			}
		}
		return true;
	}

	// returns true if a reference is in the frame table, else returns false
	public static boolean referenceisInFrameTable(FrameTable machine, Process[] processes, int processNum, int referenceNum) {
		for (int i = 0; i < machine.frames.length; i++) {
			for (int j = 0; j < machine.frames[i].onePage.references.length; j++) {
				if (machine.frames[i].onePage.process == processNum && 
					machine.frames[i].onePage.references[j] == referenceNum) {
					System.out.print("Hit in frame " + i + ".\n");
					return true;
				}
			}
		}
		System.out.print("Fault, ");
		return false;
	}

	// finds highest empty frame, returns -1 if there is no empty frame
	public static int findHighestEmptyFrame(FrameTable machine) {
		int emptyFrame = -1;
		for (int i = 0; i < machine.frames.length; i++) {
			if (machine.frames[i].empty) {
				emptyFrame = i;
			}
		}
		return emptyFrame;
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
		System.out.print("+---------+----------+");;
		for (int i = 0; i < frames[0].onePage.references.length; i++) {
			System.out.print("---+");
		}
		System.out.println("");
		for (int i = 0; i < frames.length; i++) {
			System.out.printf("|Frame %3d|",i);	
			System.out.printf("Process %2d|", frames[i].onePage.process);
			for (int j = 0; j < frames[i].onePage.references.length; j++) {
				System.out.print(" " + frames[i].onePage.references[j] + " |");
			}
			System.out.print("\n+---------+----------+");
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
	boolean empty;

	public Frame(int pageSize) {
		this.onePage = new Page(pageSize, -1);
		this.empty = true;
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
	int[] pageLoadTime;
	int[] lastUsageTime;
	int initialReference;
	int referenceCount;
	int currentReference;

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
		this.lastUsageTime = new int[pages.length];
		for (int i = 0; i < this.lastUsageTime.length; i++) {
			this.lastUsageTime[i] = -1;
		}
		this.pageLoadTime = new int[pages.length];
		for (int i = 0; i < this.pageLoadTime.length; i++) {
			this.pageLoadTime[i] = -1;
		}
		this.initialReference = (111 * (processNum + 1) + processSize) % processSize;
		this.currentReference = this.initialReference;
		this.referenceCount = 0;

	}

	public static void print(Page[] pages) {
		System.out.print("+---------+");
		for (int i = 0; i < pages[0].references.length; i++) {
			System.out.print("----+");
		}
		System.out.println("");
		for (int i = 0; i < pages.length; i++) {
			System.out.printf("|Page %3d |",i);	
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

	public static void printAllProcesses(Process[] processes) {
		System.out.println("+---------------------+");
		System.out.println("All Process Tables!!!!");
		System.out.println("+---------------------+\n");
		for (int i = 0; i < processes.length; i++) {
			System.out.println("Process Table " + i);
			print(processes[i].pages);
		}
	}

}