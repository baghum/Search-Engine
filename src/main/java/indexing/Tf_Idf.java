//http://computergodzilla.blogspot.com/2013/07/how-to-calculate-tf-idf-of-document.html

package indexing;

import java.util.Scanner;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.Link;
import org.apache.tika.sax.LinkContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.ToHTMLContentHandler;
import org.xml.sax.SAXException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class Tf_Idf {

	static MongoClient mongoClient;
	static DBCollection coll;
	static DBCollection collrank;
	static ConnectToMongoIndex mongo = new ConnectToMongoIndex();
	public static HashMap<String, String> all = new HashMap<String, String>();
	public static boolean DESC = false;
	private String searchTerm = "";

	public Map<String, Double> mongofrom(String searchTerm) {
		// ArrayList<String> mywords = generateStopWordList();
		Map<String, Double> sortedMapAsc = null;
		// System.out.println(mywords.size());

		try {

			// To connect to mongodb server
			System.out.println(searchTerm + "this is it im printing ");
			System.out.println("in mongo");
			mongoClient = new MongoClient("localhost", 27017);
			@SuppressWarnings("deprecation")
			DB db = mongoClient.getDB("hw3");
			coll = db.getCollection("user");
			double totalDocInDB = coll.count();
			// System.out.println("total " + totalDocInDB);
			// ...........

			DB dbrank = mongoClient.getDB("rank");
			collrank = dbrank.getCollection("rankTable");

			// while (true) {

			BasicDBObject q = new BasicDBObject();
			BasicDBObject qRank = new BasicDBObject();

			// System.out.println("SEARCH");
			// Scanner sc = new Scanner(System.in);
			// String m = sc.nextLine();
			q.put("content", java.util.regex.Pattern.compile(searchTerm));
			coll.find(q);

			DBCursor cursor = coll.find(q);
			DBCursor cursor2 = coll.find(q);
			// DBCursor cursor3 = coll.find();

			HashMap<String, Double> score = new HashMap<String, Double>();
			// Map<String, Double> sortedMapAsc = null;
			double rankScore = 0.0;
			double totalIdf = 0.0;

			// ..................................

			while (cursor.hasNext() && cursor2.hasNext()) {

				String path = cursor.next().get("path").toString();
				String content = cursor2.next().get("content").toString();
				String[] pathnew = path.split("C:W+");

				for (String link : pathnew) {

					qRank.put("path", link);
					DBCursor cursor3 = collrank.find(qRank);
					while (cursor3.hasNext()) {
						rankScore = (Double) cursor3.next().get("score");
					}

					double tf = tfCalculator(searchTerm, content);
					double idf = idfCalculator(pathnew.length, totalDocInDB);
					totalIdf = tf * idf;
					double idfAndLinkAnalysis = totalIdf + rankScore;

					score.put(link, idfAndLinkAnalysis);
					sortedMapAsc = sortByComparator(score, DESC);

				}

			}

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());

		}
		mongoClient.close();

		return sortedMapAsc;

	}

	private static Map<String, Double> sortByComparator(
			Map<String, Double> unsortMap, final boolean order) {

		List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(
				unsortMap.entrySet());

		// Sorting the list based on values
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1,
					Map.Entry<String, Double> o2) {
				if (order) {
					return o1.getValue().compareTo(o2.getValue());
				} else {
					return o2.getValue().compareTo(o1.getValue());

				}
			}
		});

		// Maintaining insertion order with the help of LinkedList
		Map<String, Double> sortedMap = new LinkedHashMap<>();
		for (Map.Entry<String, Double> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	public static void content(String s) throws IOException, SAXException,
			TikaException {
		// mongoClient.close();
		InputStream in = new FileInputStream(new File(s));
		LinkContentHandler linkHandler = new LinkContentHandler();
		BodyContentHandler textHandler = new BodyContentHandler(-1);
		ToHTMLContentHandler toHTMLHandler = new ToHTMLContentHandler();
		TeeContentHandler teeHandler = new TeeContentHandler(linkHandler,
				textHandler, toHTMLHandler);
		Metadata metadata = new Metadata();
		ParseContext parseContext = new ParseContext();
		HtmlParser parser = new HtmlParser();
		parser.parse(in, teeHandler, metadata, parseContext);
		String content = textHandler.toString();
		String fileName = metadata.get("title");

		String path = s.replace("'\\'", "'\'");
		String contentToMongo = stopWordFreeSentence(content);
		// all.put(path, content);
		System.out.println("inserting");
		mongo.inserting(fileName, contentToMongo, path);

	}

	private static ArrayList<String> generateStopWordList() {
		ArrayList<String> stopWords = new ArrayList<String>();
		stopWords.add("a");
		stopWords.add("an");
		stopWords.add("and");
		stopWords.add("are");
		stopWords.add("as");
		stopWords.add("at");
		stopWords.add("be");
		stopWords.add("by");
		stopWords.add("for");
		stopWords.add("from");
		stopWords.add("has");
		stopWords.add("he");
		stopWords.add("in");
		stopWords.add("is");
		stopWords.add("it");
		stopWords.add("its");
		stopWords.add("of");
		stopWords.add("on");
		stopWords.add("that");
		stopWords.add("the");
		stopWords.add("two");
		stopWords.add("was");
		stopWords.add("were");
		stopWords.add("will");
		stopWords.add("with");
		stopWords.add("Wikipedia");
		stopWords.add("users");
		stopWords.add("user");

		return stopWords;
	}

	public static String stopWordFreeSentence(String text) {
		String noStopWordSentence = "";
		ArrayList<String> finalStringList = new ArrayList<String>();
		ArrayList<String> stopWordList = generateStopWordList();
		StringBuilder sb = new StringBuilder();
		sb.append(text);
		String[] tokenizedTerms = sb.toString().replaceAll("[\\W&&[^\\s]]", "")
				.split("\\W+");

		for (String s : tokenizedTerms) {
			boolean isStopWord = false;
			for (String stopWord : stopWordList) {
				if (s.equalsIgnoreCase(stopWord)) {
					isStopWord = true;
					break;
				}
			}

			if (!isStopWord)
				finalStringList.add(s);

		}

		for (int i = 0; i < finalStringList.size(); i++) {
			if (i == finalStringList.size() - 1)
				noStopWordSentence += finalStringList.get(i);
			else
				noStopWordSentence += finalStringList.get(i) + " ";
		}

		return noStopWordSentence;
	}

	// TF calculator
	public double tfCalculator(String termToCheck, String content) {
		System.out.println(termToCheck);

		StringBuilder sb = new StringBuilder();
		sb.append(content);
		String[] tokenizedTerms = sb.toString().replaceAll("[\\W&&[^\\s]]", "")
				.split("\\W+");
		double sizeFile = tokenizedTerms.length;

		List<String> tokens = new ArrayList<String>();
		tokens.add(termToCheck);

		// String patternString = "\\b(" + StringUtils.join(tokens, "|") +
		// ")\\b";
		// System.out.println(patternString);

		String patternString = "\b(" + termToCheck + ")\b";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(content);
		int count = 0;
		while (matcher.find()) {
			count++;
		}

		double theTf = (count / (sizeFile - 1));
		return theTf;

	}

	// IDF Calculator
	public double idfCalculator(int count, double totalDoc) {
		return (Math.log10(totalDoc / count));
	}

	public static void main(String[] args) throws IOException, SAXException,
			TikaException {
		// // System.out.println("hello");
		// // FileIndexing file = new FileIndexing();
		// // file.readFile();
		// // ArrayList<String> Urls = file.url;
		// // System.out.println("starting mongo");
		// // a.ToMongo();
		// // System.out.println("going");
		// // for (int i = 0; i < Urls.size(); i++) {
		// // System.out.println("inside the loop");
		// // content(Urls.get(i));
		// // }
		Tf_Idf a = new Tf_Idf();
		String term = "it";
		Map<String, Double> s = a.mongofrom(term);
		for (Entry<String, Double> enty : s.entrySet()) {
			System.out.println(enty.getKey() + "\t" + enty.getValue());
		}
		//
		System.out.println("Done");

	}
}