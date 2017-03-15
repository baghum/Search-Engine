// I used these websites to write the code  http://www.lucenetutorial.com/sample-apps/textfileindexer-java.html, http://oak.cs.ucla.edu/cs144/projects/lucene/

package indexing;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Search {
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s;
		

		//s = "";
		while (true) {
			String indexLocation = "C:\\Users\\andy\\Desktop\\CS 454\\TestIndex";
			StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
			IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(
					indexLocation)));
			IndexSearcher searcher = new IndexSearcher(reader);
			TopScoreDocCollector collector = TopScoreDocCollector.create(10, true);
			try {
				System.out.println("SEARCH: ");
				s = br.readLine();
				if (s.equalsIgnoreCase("q")) {
					System.out.println("exiting");
					break;
				}
				Query q = new QueryParser(Version.LUCENE_40, "contents",
						analyzer).parse(s);
				searcher.search(q, collector);
				ScoreDoc[] hits = collector.topDocs().scoreDocs;

				
				System.out.println("Found " + hits.length + " hits.");
				for (int i = 0; i < hits.length; ++i) {
					int docId = hits[i].doc;
					Document d = searcher.doc(docId);
					System.out.println((i + 1) + ". " + d.get("path") + "  FILENAME: " + d.get("filename")
							+ "      score=" + hits[i].score + "   " + d.get("contents"));
					
				}
				hits = null;
				

			} catch (Exception e) {
				System.out.println("Error searching " 
						+ e.getMessage());
			}
		}

	}
}
