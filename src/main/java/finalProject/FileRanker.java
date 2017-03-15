package finalProject;

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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

//source code derived from http://chrisjordan.ca/post/15219674437/parsing-html-with-apache-tika
//this comment is for WebCrawler.java   method extract()

import indexing.FileIndexing;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class FileRanker {
	static ConnectToMongoIndex rankMongo = new ConnectToMongoIndex();
	private Map<File, Integer> numOutgoingLinks;
	private Map<File, Set<String>> outgoingMap;
	private Map<File, List<File>> incomingMap;
	private Map<File, Double> defaultScoreMap;
	private Map<File, List<Double>> scoreMap;
	private Map<File, Boolean> visitedMap;
	static final double lambda = 0.15;
	static final double epsilon = 1E-5;
	public static int counterAndo = 0;

	public FileRanker() {
		numOutgoingLinks = Maps.newHashMap();
		outgoingMap = Maps.newHashMap();
		incomingMap = Maps.newHashMap();
		scoreMap = Maps.newHashMap();
		visitedMap = Maps.newHashMap();
		defaultScoreMap = Maps.newHashMap();

	}

	public Map<File, Double> getDefaultScoreMap() {
		return defaultScoreMap;
	}

	public Map<File, Boolean> getVisitedMap() {
		return visitedMap;
	}

	public Map<File, List<Double>> getScoreMap() {
		return scoreMap;
	}

	public Map<File, List<File>> getIncomingMap() {
		return incomingMap;
	}

	public Map<File, Integer> getNumOutgoingLinks() {
		return numOutgoingLinks;
	}

	// int count = 0;
	// ArrayList<String> mypath = new ArrayList();
	Map<String, String> myPath = Maps.newHashMap();

	List<String> myLink = new ArrayList<String>();
	Set<String> outgoingLinks;

	public void generateIncomingMap(File f) {
		List<File> incomingFiles = Lists.newArrayList();

		// System.out.println("file name: " + f.getAbsolutePath());
		// System.out.println("im here");
		// System.out.println("Size of entry set: " +
		// outgoingMap.entrySet().size());

		for (Map.Entry<File, Set<String>> entry : outgoingMap.entrySet()) {

			if (entry.getKey().getAbsolutePath().equals(f.getAbsolutePath())) {
				// System.out.println("skipped: " + f.getAbsolutePath());
				continue;
			} else {

				// System.out.println("not skipped");

				File key = entry.getKey(); // google
				// System.out.println("file key: " + key);
				outgoingLinks = entry.getValue(); // links for google

				// int counter = 1;
				// System.out.println("size of record set: " +
				// outgoingLinks.size());
				for (String linkUrl : outgoingLinks) { // all the links for
														// GOOGLE
														// and only for google
					// System.out.println("file passed in: " +
					// f.getAbsolutePath());
					// System.out.println("file as key: " +
					// key.getAbsolutePath());
					// System.out.println(counter++ + " linkUrl: " + linkUrl +
					// '\n');

					if (linkUrl.equals(f.getName())) { // f is yahoo
						// System.out.println("match found");
						if (!incomingMap.containsKey(f)) { // incoming map has a
															// file and a list
															// of
															// incoming links
															// for
															// that file(yahoo)
							incomingFiles.add(key);
							incomingMap.put(f, incomingFiles);
						} else
							incomingMap.get(f).add(key);

					}

				}
			}
		}
	}

	public Map<File, Set<String>> getOutgoingMap() {
		return outgoingMap;
	}

	// This will fill a hash map which contains the provided file(file)
	// as a key. The value associated with that key is the number of
	// outgoing links this file contains
	public void calculateNumOutgoingLinks(File file) {
		InputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LinkContentHandler linkHandler = new LinkContentHandler();
		BodyContentHandler textHandler = new BodyContentHandler(-1);
		ToHTMLContentHandler toHTMLHandler = new ToHTMLContentHandler();
		TeeContentHandler teeHandler = new TeeContentHandler(linkHandler,
				textHandler, toHTMLHandler);
		Metadata metadata = new Metadata();
		ParseContext parseContext = new ParseContext();
		HtmlParser parser = new HtmlParser();
		try {
			parser.parse(in, teeHandler, metadata, parseContext);
		} catch (IOException | SAXException | TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<Link> linkList = linkHandler.getLinks();
		// System.out.println("list size: " + linkList.size());
		Set<String> linkSet = new HashSet<String>(); // this set will hold the
														// urls of the outgoing
														// links
														// for the given file

		for (Link link : linkList) {
			// System.out.println(file.getName() + " " +link.getUri());
			linkSet.add(link.getUri());
		}

		// System.out.println("set size: " + linkSet.size() + '\n');

		numOutgoingLinks.put(file, linkSet.size());
		outgoingMap.put(file, linkSet);
	}

	public void fillVisitedMap() {
		for (Map.Entry<File, Set<String>> entry : outgoingMap.entrySet())
			visitedMap.put(entry.getKey(), false);

	}

	public void fillDefaultScoreMap() {
		for (Map.Entry<File, Set<String>> entry : outgoingMap.entrySet())
			defaultScoreMap
					.put(entry.getKey(), 1 / (double) outgoingMap.size());

	}

	// helper function for generateScore()
	// calculates score for a given file on the first iteration
	private void generateScoreFirstIter(File altFile) {
		double incomingLinkSum = 0.0;
		double finalScore = 0.0;
		for (File incomingLink : incomingMap.get(altFile)) {
			if (numOutgoingLinks.get(incomingLink) == 0)
				incomingLinkSum += 0.0;
			else
				incomingLinkSum += defaultScoreMap.get(incomingLink)
						/ numOutgoingLinks.get(incomingLink);
		}

		finalScore = lambda / outgoingMap.size() + (1 - lambda)
				* incomingLinkSum;

		if (!scoreMap.containsKey(altFile)) {
			ArrayList<Double> scoreRecords = Lists.newArrayList();
			scoreRecords.add(finalScore);
			scoreMap.put(altFile, scoreRecords);
		} else
			scoreMap.get(altFile).add(finalScore);

	}

	// there may be some incoming links for a file that don't have an entry in
	// the score map
	// this takes care of that
	private void generateMissingScore(File f) {
		double finalScore = 0.0;
		double incomingLinkSum = 0.0;

		for (File incomingLink : incomingMap.get(f)) {
			if (numOutgoingLinks.get(incomingLink) == 0) // don't want to divide
															// by 0
				incomingLinkSum += 0.0;
			else {
				if (scoreMap.containsKey(incomingLink)) {
					System.out
							.println("This incoming link has a score history");

					ArrayList<Double> scoreRecords = (ArrayList<Double>) scoreMap
							.get(incomingLink);
					if (visitedMap.get(incomingLink)) { // if this document
														// recently updated the
														// score map
						incomingLinkSum += scoreRecords
								.get(scoreRecords.size() - 2)
								/ numOutgoingLinks.get(incomingLink);
					} else {
						// System.out.println("current file: " + f.getName());
						// System.out.println("incoming link for this file: " +
						// incomingLink.getName());
						// System.out.println("score for this incoming link: " +
						// scoreRecords.get(scoreRecords.size()-1) + '\n');

						incomingLinkSum += scoreRecords
								.get(scoreRecords.size() - 1)
								/ numOutgoingLinks.get(incomingLink);
					}

				} else {

					// this implies that this incoming link hasn't been visited
					// yet and given a score
					// give this link a score, utilizing the most up-to-date
					// scores from its incoming links
					System.out
							.println("This incoming link does not have a score history");
					generateScoreAlt(incomingLink); // create a score entry for
													// this incoming link

					ArrayList<Double> scoreRecords = (ArrayList<Double>) scoreMap
							.get(incomingLink);

					// System.out.println("current file: " + f.getName());
					// System.out.println("incoming link for this file: " +
					// incomingLink.getName());
					// System.out.println("score for this incoming link: " +
					// scoreRecords.get(scoreRecords.size()-1) + '\n');

					incomingLinkSum += scoreRecords
							.get(scoreRecords.size() - 1)
							/ numOutgoingLinks.get(incomingLink);

				}

			}

		}

		finalScore = lambda / outgoingMap.size() + (1 - lambda)
				* incomingLinkSum;
		if (!scoreMap.containsKey(f)) {
			ArrayList<Double> scoreRecords = Lists.newArrayList();
			scoreRecords.add(finalScore);
			visitedMap.put(f, true);
			scoreMap.put(f, scoreRecords);
		} else {
			scoreMap.get(f).add(finalScore);
			visitedMap.put(f, true);
		}
	}

	// helper function for generateScore()
	// useful for all iterations beyond the first iteration
	private void generateScoreAlt(File f) {
		double finalScore = 0.0;
		double incomingLinkSum = 0.0;
		for (File incomingLink : incomingMap.get(f)) {
			if (numOutgoingLinks.get(incomingLink) == 0) // don't want to divide
															// by 0
				incomingLinkSum += 0.0;
			else {
				if (scoreMap.containsKey(incomingLink)) {
					System.out
							.println("This incoming link has a score history");

					ArrayList<Double> scoreRecords = (ArrayList<Double>) scoreMap
							.get(incomingLink);
					if (visitedMap.get(incomingLink)) { // if this document
														// recently updated the
														// score map
						incomingLinkSum += scoreRecords
								.get(scoreRecords.size() - 2)
								/ numOutgoingLinks.get(incomingLink);
					} else {

						incomingLinkSum += scoreRecords
								.get(scoreRecords.size() - 1)
								/ numOutgoingLinks.get(incomingLink);
					}

				} else {

					// this implies that this incoming link hasn't been visited
					// yet and given a score
					// give this link a score, utilizing the most up-to-date
					// scores from its incoming links
					System.out
							.println("This incoming link does not have a score history");
					generateMissingScore(incomingLink); // create a score entry
														// for this incoming
														// link

					ArrayList<Double> scoreRecords = (ArrayList<Double>) scoreMap
							.get(incomingLink);

					// System.out.println("current file: " + f.getName());
					// System.out.println("incoming link for this file: " +
					// incomingLink.getName());
					// System.out.println("score for this incoming link: " +
					// scoreRecords.get(scoreRecords.size()-1) + '\n');

					incomingLinkSum += scoreRecords
							.get(scoreRecords.size() - 1)
							/ numOutgoingLinks.get(incomingLink);

				}

			}

		}

		finalScore = lambda / outgoingMap.size() + (1 - lambda)
				* incomingLinkSum;
		if (!scoreMap.containsKey(f)) {
			ArrayList<Double> scoreRecords = Lists.newArrayList();
			scoreRecords.add(finalScore);
			visitedMap.put(f, true);
			scoreMap.put(f, scoreRecords);
		} else {
			scoreMap.get(f).add(finalScore);
			visitedMap.put(f, true);
		}

	}

	// let's say f was LinkAnalysis.html
	public void generateScore() {
		double delta = defaultScoreMap.entrySet().iterator().next().getValue();
		System.out.println(delta + " == my DELTAA");

		double finalScore = 0.0;// 0.000165 / 0.00003

		int number = 0;

		while (number < 7) {
			System.out.println("starting");
			for (Map.Entry<File, Set<String>> entry : getOutgoingMap()
					.entrySet()) {
				File f = entry.getKey();
				
				if (incomingMap.get(f) != null) {
					double incomingLinkSum = 0.0;
					for (File incomingLink : incomingMap.get(f)) {
						

						if (numOutgoingLinks.get(incomingLink) == 0) {

							incomingLinkSum += 0.0;
						} else {
							incomingLinkSum += defaultScoreMap
									.get(incomingLink)
									/ numOutgoingLinks.get(incomingLink);
						}

					}

					finalScore = lambda / outgoingMap.size() + (1 - lambda)
							* incomingLinkSum;

					defaultScoreMap.put(f, finalScore);
					counterAndo++;

					number++;
					System.out
							.println(number
									+ " = number......................................................................................................");
				} else {
					System.out.println("do nothing");
				}
			}

		}
	}

	// }

	public static void main(String[] args) {
		System.out.println(epsilon);
		FileIndexing fileIndexer = new FileIndexing();
		fileIndexer.addFiles(new File("C:\\Users\\andy\\Desktop\\test"));

		ArrayList<File> files = fileIndexer.getQueue();
		FileRanker ranker = new FileRanker();

		System.out.println("filling outgoing map");
		for (File f : files) {
			ranker.calculateNumOutgoingLinks(f);
		}
		System.out.println("outgoing map size: "
				+ ranker.getOutgoingMap().entrySet().size());

		for (File fi : files) {
			ranker.generateIncomingMap(fi);
		}

		System.out.println("incoming map size: "
				+ ranker.getIncomingMap().entrySet().size());

		ranker.fillDefaultScoreMap(); // initialize default scores for all
		ranker.generateScore();
		System.out.println("the total count is " + counterAndo);

		for (Entry<File, Double> entry : ranker.defaultScoreMap.entrySet()) {
			String path = entry.getKey().getPath();
			double score = entry.getValue();
			rankMongo.rankInserting(path, score);
		}

		System.out.println("done");

	}
}