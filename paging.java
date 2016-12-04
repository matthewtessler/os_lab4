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

		/* create a representation of a frame table --> machine size with rows length of pageSize */
		int[] machine = new int[machineSize];
		for (int i = 0; i < machine.length; i++) { 
			machine[i] = -1;
		}

		printFrameTable(machine, pageSize); // testing printing 

	}

	/* prints the frame table which is the total size of the machine, divided into rows of frames */
	public static void printFrameTable(int[] machine, int pageSize) {
		for (int i = 0; i < machine.length / pageSize; i++) {
			System.out.print("\nFrame " + i);
			for (int j = 0; j < pageSize; j++) {
				System.out.print(" " + machine[i*j]);
			}
		}
		System.out.println();
	}
}
