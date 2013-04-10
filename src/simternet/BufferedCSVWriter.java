package simternet;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BufferedCSVWriter implements Runnable {
	public static int				defaultBufferSize	= 10000;
	public BlockingQueue<String>	outputQueue;
	public PrintWriter				outputWriter;
	public Thread					writerThread;
	public boolean					shutdown;
	public boolean					headersWritten		= false;
	public boolean					dataWritten			= false;

	public BufferedCSVWriter(String fileName, int bufferSize) {
		// Set up file output
		FileWriter fw = null;
		BufferedWriter bw = null;

		try {
			fw = new FileWriter(fileName);
			bw = new BufferedWriter(fw, bufferSize * 100); // in bytes
			outputWriter = new PrintWriter(bw);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Set up the per-line output queue
		outputQueue = new ArrayBlockingQueue<String>(bufferSize);

		// Set up the thread that writes to disk
		writerThread = new Thread(this);
		writerThread.start();
	}

	public BufferedCSVWriter(String fileName) {
		this(fileName, BufferedCSVWriter.defaultBufferSize);
	}

	@Override
	public void run() {
		while (!shutdown) {
			try {
				String line = outputQueue.take();
				outputWriter.println(line);
			} catch (InterruptedException e) {
				if (shutdown)
					return;
				else {
					e.printStackTrace();
				}
			}
		}
	}

	public void shutdown() {
		shutdown = true;
		outputWriter.flush();
		writerThread.interrupt();
	}

	public void writeLine(String line) {
		dataWritten = true;
		try {
			outputQueue.put(line);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void writeHeaders(String line) {
		if (dataWritten)
			throw new RuntimeException("Can't write headers after data!");
		if (headersWritten)
			throw new RuntimeException("Headers already written once");

		// output headers
		try {
			outputQueue.put(line);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		headersWritten = true;

	}

	/**
	 * For testing purposes
	 * 
	 * @param args
	 *            None
	 */
	public static void main(String[] args) {
		int numRunnables = 500;
		BufferedCSVWriter bcw = null;
		bcw = new BufferedCSVWriter("foo.csv", 100);

		Thread loggerThreads[] = new Thread[numRunnables];
		for (int i = 0; i < numRunnables; i++) {
			LogTestWriter lw = new LogTestWriter(bcw, i);
			loggerThreads[i] = new Thread(lw);
			loggerThreads[i].start();
		}

		for (int i = 0; i < numRunnables; i++) {
			try {
				loggerThreads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		bcw.shutdown();

	}

	public static class LogTestWriter implements Runnable {

		BufferedCSVWriter	writer;
		int					writerNum;

		public LogTestWriter(BufferedCSVWriter writer, int writerNum) {
			this.writer = writer;
			this.writerNum = writerNum;
		}

		@Override
		public void run() {
			for (int i = 0; i < 1000; i++) {
				writer.writeLine("Writer #" + writerNum + ", i=" + i);
			}
		}

	}

}
