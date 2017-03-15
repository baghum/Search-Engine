// I used these websites to write the code  http://www.lucenetutorial.com/sample-apps/textfileindexer-java.html, http://oak.cs.ucla.edu/cs144/projects/lucene/
package finalProject;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.ArrayList;

public class FileIndexing {

	private ArrayList<File> queue = new ArrayList<File>();
	public static ArrayList<String> url = new ArrayList<String>();

	public FileIndexing() {
	}

	public ArrayList<File> getQueue() {
		return queue;
	}

	public static void readFile() throws IOException {
		FileIndexing indexer = new FileIndexing();
		try {
			System.out
					.println("reading from C:\\Users\\andy\\Desktop\\test");
			String s = "C:\\Users\\andy\\Desktop\\test";
			indexer.indexFileOrDirectory(s);
		} catch (Exception e) {
			System.out.println("Error indexing " + " : " + e.getMessage());
		}
	}

	public void indexFileOrDirectory(String fileName) throws IOException {
		addFiles(new File(fileName));
		int count = 0;

		for (File f : queue) {
			FileReader fr = null;
			try {
				fr = new FileReader(f);
				String path = f.getPath();
				url.add(path);
				// System.out.println(path);
				count++;
			} catch (Exception e) {
				System.out.println(e);
			} finally {
				fr.close();
			}
		}

		System.out.println("done " + count);
		queue.clear();
	}

	public void addFiles(File file) {
		if (!file.exists()) {
			System.out.println(file + " does not exist.");
		}
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				addFiles(f);
			}
		} else {
			String filename = file.getName().toLowerCase();

			// Only index text files
			if (filename.endsWith(".htm") || filename.endsWith(".html")
					|| filename.endsWith(".xml") || filename.endsWith(".txt")) {
				queue.add(file);
			} else {
				System.out.println("Skipped " + filename);
			}
		}
	}

}