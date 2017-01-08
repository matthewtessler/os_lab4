/* Student Name: Matthew Tessler */
import java.util.*;
import java.io.*;
import java.util.Scanner;

public class paging {

	static int[] randomOS = new int[100000];
	static int randomCount = 0;

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

		/* randomOS setup */
		try {
			File randomFile = new File("randoms.txt");
			Scanner scanRandom = new Scanner(randomFile);
			for (int i = 0; i < 100000; i++) {
				randomOS[i] = scanRandom.nextInt();
			}
		}
		catch (FileNotFoundException ex) {
			System.out.println("Random OS file not found.");
			System.exit(0);
		}

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
				if (debugging == 1 || debugging == 11) {
					System.out.print((current+1) + " references word " + processes[current].currentReference + " (page " + 
					(processes[current].currentReference/pageSize) + ") at time " + (time+1) + ": ");
				}
				/* Make references for process */
				// If current reference is in a page in the frame table belonging to current process
				if (referenceisInFrameTable(machine, processes, current, processes[current].currentReference, debugging)) {
					// update last usage time for page in current process
					processes[current].lastUsageTime[(processes[current].currentReference)/pageSize] = time;
				}
				else {
					processes[current].faults++;
					// if there is an empty frame to put the page containing the reference
					if (findHighestEmptyFrame(machine) != -1) {
						// put page in highest empty frame and set load time
						int highestEmptyFrame = findHighestEmptyFrame(machine);
						if (debugging == 1 || debugging == 11) {
							System.out.print(" using free frame " + highestEmptyFrame + ".\n");
						}
						machine.frames[highestEmptyFrame].empty = false;
						for (int j = 0; j < machine.frames[highestEmptyFrame].onePage.references.length; j++) {
							machine.frames[highestEmptyFrame].onePage.references[j] = processes[current].pages[processes[current].currentReference/pageSize].references[j];
						}
						machine.frames[highestEmptyFrame].onePage.process = current;
						processes[current].pageLoadTime[processes[current].currentReference/pageSize] = time;
						processes[current].lastUsageTime[processes[current].currentReference/pageSize] = time;
					}
					// page replacement algorithm: store eviction info, residency time, eviction count for process, load time for new page
					else {
						// evict the least recently used (lowest last used time)
						if (replacementAlgo.equals("lru")) {
							int leastRecentlyUsedFrameTime = Integer.MAX_VALUE;
							int leastRecentlyUsedFrame = -1;
							// for each frame get the last usage time of the page
							for (int k = 0; k < machine.frames.length; k++) {
								// find the last usage time of that page for that process by integer dividing that element
								// if the time is lower than the current time set the least recently used equal to that one
								if (processes[machine.frames[k].onePage.process].lastUsageTime[machine.frames[k].onePage.references[2]/pageSize] < leastRecentlyUsedFrameTime) {
									leastRecentlyUsedFrameTime = processes[machine.frames[k].onePage.process].lastUsageTime[machine.frames[k].onePage.references[2]/pageSize];
									leastRecentlyUsedFrame = k;
								}
							}
							if (debugging == 1 || debugging == 11) {
								System.out.println("evicting page " + (machine.frames[leastRecentlyUsedFrame].onePage.references[3]/pageSize) + " of process " + 
								(machine.frames[leastRecentlyUsedFrame].onePage.process+1) + " from frame " + leastRecentlyUsedFrame + ".");
							}
							processes[machine.frames[leastRecentlyUsedFrame].onePage.process].residency += time - processes[machine.frames[leastRecentlyUsedFrame].onePage.process].pageLoadTime[machine.frames[leastRecentlyUsedFrame].onePage.references[3]/pageSize];
							processes[machine.frames[leastRecentlyUsedFrame].onePage.process].evictions++;
							machine.frames[leastRecentlyUsedFrame].onePage.process = current;
							// replace it by copying in the new page and setting the process value of that frame to the new process, pageLoadTime
							for (int j = 0; j < machine.frames[leastRecentlyUsedFrame].onePage.references.length; j++) {
								machine.frames[leastRecentlyUsedFrame].onePage.references[j] = processes[current].pages[processes[current].currentReference/pageSize].references[j];
							}
							processes[current].pageLoadTime[processes[current].currentReference/pageSize] = time;
							processes[current].lastUsageTime[processes[current].currentReference/pageSize] = time;

						}
						else if (replacementAlgo.equals("lifo")) {
							int mostRecentLoad = -1;
							int mostRecentLoadTime = -1;
							// for each frame get the load time of the page
							for (int k = 0; k < machine.frames.length; k++) {
								// if the load time is higher than the current time set the variable equal to that
								if (processes[machine.frames[k].onePage.process].pageLoadTime[machine.frames[k].onePage.references[2]/pageSize] > mostRecentLoadTime) {
									mostRecentLoadTime = processes[machine.frames[k].onePage.process].pageLoadTime[machine.frames[k].onePage.references[2]/pageSize];
									mostRecentLoad = k;
								}
							}
							if (debugging == 1 || debugging == 11) {
								System.out.println("evicting page " + (machine.frames[mostRecentLoad].onePage.references[3]/pageSize) + " of process " + 
								(machine.frames[mostRecentLoad].onePage.process+1) + " from frame " + mostRecentLoad + ".");
							}
							processes[machine.frames[mostRecentLoad].onePage.process].residency += time - processes[machine.frames[mostRecentLoad].onePage.process].pageLoadTime[machine.frames[mostRecentLoad].onePage.references[3]/pageSize];
							processes[machine.frames[mostRecentLoad].onePage.process].evictions++;
							machine.frames[mostRecentLoad].onePage.process = current;
							// replace it by copying in the new page and setting the process value of that frame to the new process, pageLoadTime
							for (int j = 0; j < machine.frames[mostRecentLoad].onePage.references.length; j++) {
								machine.frames[mostRecentLoad].onePage.references[j] = processes[current].pages[processes[current].currentReference/pageSize].references[j];
							}
							processes[current].pageLoadTime[processes[current].currentReference/pageSize] = time;
							processes[current].lastUsageTime[processes[current].currentReference/pageSize] = time;
						}
						else if (replacementAlgo.equals("random")) {
							int randomEvict = rand() % machine.frames.length;
							if (debugging == 1 || debugging == 11) {
								System.out.println("evicting page " + (machine.frames[randomEvict].onePage.references[3]/pageSize) + " of process " + 
								(machine.frames[randomEvict].onePage.process+1) + " from frame " + randomEvict + ".");
							}
							processes[machine.frames[randomEvict].onePage.process].residency += time - processes[machine.frames[randomEvict].onePage.process].pageLoadTime[machine.frames[randomEvict].onePage.references[3]/pageSize];
							processes[machine.frames[randomEvict].onePage.process].evictions++;
							machine.frames[randomEvict].onePage.process = current;
							// replace it by copying in the new page and setting the process value of that frame to the new process, pageLoadTime
							for (int j = 0; j < machine.frames[randomEvict].onePage.references.length; j++) {
								machine.frames[randomEvict].onePage.references[j] = processes[current].pages[processes[current].currentReference/pageSize].references[j];
							}
							processes[current].pageLoadTime[processes[current].currentReference/pageSize] = time;
							processes[current].lastUsageTime[processes[current].currentReference/pageSize] = time;
						}
					}
				}
				// find next reference for the process dependent on jobMix
				if (jobMix == 1 || jobMix == 2) {
					// sequential
					processes[current].currentReference = (processes[current].currentReference + 1 + processSize) % processSize;
				}
				else if (jobMix == 3) {
					// random, next reference is (rand) mod S
					processes[current].currentReference = rand();
					if (debugging == 11) {
						System.out.println((current+1) + " uses random number: " + processes[current].currentReference);
					}
					processes[current].currentReference = processes[current].currentReference % processSize;
				}
				else if (jobMix == 4) {
					double A = 0.00;
					double B = 0.00;
					double C = 0.00;
					double y = rand();
					if (debugging == 11) {
						System.out.println((current+1) + " uses random number: " + (int)y);
					}
					y = y / (Integer.MAX_VALUE + 1d);
					if (current == 0) {
						A = 0.750;
						B = 0.250;
						C = 0.000;
					}
					else if (current == 1) {
						A = 0.750;
						B = 0.000;
						C = 0.250;
					}
					else if (current == 2) {
						A = 0.750;
						B = 0.125;
						C = 0.125;
					}
					else if (current == 3) {
						A = 0.500;
						B = 0.125;
						C = 0.125;
					}

					if (y < A) {
						// next reference = ( w + 1 + S) mod S
						processes[current].currentReference = (processes[current].currentReference + 1 + processSize) % processSize;
					}
					else if (y < (A + B)) {
						// next reference = (w - 5 + S) mod S
						processes[current].currentReference = (processes[current].currentReference - 5 + processSize) % processSize;
					}
					else if (y < (A + B + C)) {
						// next reference = (w + 4 + S) mod S
						processes[current].currentReference = (processes[current].currentReference + 4 + processSize) % processSize;
					}
					else {
						// random mod S
						processes[current].currentReference = rand();
						if (debugging == 11) {
							System.out.println((current+1) + " uses random number: " + processes[current].currentReference);
						}
						processes[current].currentReference = processes[current].currentReference % processSize;
					}
				}
				if (jobMix != 4) {
					if (debugging == 11) {
						System.out.println((current+1) + " uses random number " + rand());
					}
					else {
						rand();
					}
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

		// print results
		int totalFaults = 0;
		int totalResidency = 0;
		int totalEvictions = 0;
		System.out.println("");
		for (int i = 0; i < processes.length; i++) {
			if (processes[i].evictions != 0) {
				System.out.println("Process " + (i+1) + " had " + processes[i].faults + " faults and " + ((double)processes[i].residency / (double)processes[i].evictions) + " average residency.");
				totalFaults += processes[i].faults;
				totalResidency += processes[i].residency;
				totalEvictions += processes[i].evictions;
			}
			else {
				System.out.println("Process " + (i+1) + " had " + processes[i].faults + " faults.");
				System.out.println("	With no evictions, the average residence is undefined.");
				totalFaults += processes[i].faults;
			}
			
		}
		if (totalEvictions != 0) {
			System.out.println("\nThe total number of faults is " + totalFaults + " and the overall average residency is " + (double)totalResidency/(double)totalEvictions + ".");
		}
		else {
			System.out.println("\nThe total number of faults is " + totalFaults + " faults.");
			System.out.println("	With no evictions, the overall average residence is undefined.");
		}	
	}

	// prints the frame table and all process tables
	public static void print(FrameTable machine, Process[] processes) {
		FrameTable.print(machine.frames);
		Process.printAllProcesses(processes);
	}

	public static int rand() {
		return randomOS[randomCount++];
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
	public static boolean referenceisInFrameTable(FrameTable machine, Process[] processes, int processNum, int referenceNum, int debugging) {
		for (int i = 0; i < machine.frames.length; i++) {
			for (int j = 0; j < machine.frames[i].onePage.references.length; j++) {
				if (machine.frames[i].onePage.process == processNum && 
					machine.frames[i].onePage.references[j] == referenceNum) {
					if (debugging == 1 || debugging == 11) {
						System.out.print("Hit in frame " + i + ".\n");
					}
					return true;
				}
			}
		}
		if (debugging == 1 || debugging == 11) {
			System.out.print("Fault, ");
		}
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
			System.out.print("----+");
		}
		System.out.println("");
		for (int i = 0; i < frames.length; i++) {
			System.out.printf("|Frame %3d|",i);	
			System.out.printf("Process %2d|", (frames[i].onePage.process + 1));
			for (int j = 0; j < frames[i].onePage.references.length; j++) {
				System.out.print(" " + frames[i].onePage.references[j] + " |");
			}
			System.out.print("\n+---------+----------+");
			for (int k = 0; k < frames[0].onePage.references.length; k++) {
				System.out.print("----+");
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
	int faults;
	int residency;
	int evictions;

	// instantiates array of pages each having pageSize number of references
	public Process(int processSize, int pageSize, int processNum) {
		this.pages = new Page[processSize/pageSize];
		for (int i = 0; i < pages.length; i++) {
			this.pages[i] = new Page(pageSize, processNum);
			// gives references 0 through page size minus 1
			for (int j = 0; j < this.pages[i].references.length; j++) {
				this.pages[i].references[j] = i * (pageSize) + j;
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
		this.faults = 0;
		this.residency = 0;
		this.evictions = 0;

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