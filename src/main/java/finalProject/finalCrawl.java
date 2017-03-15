package finalProject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ContentHandler;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.SysexMessage;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.LinkContentHandler;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.Link;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.ToHTMLContentHandler;

import project.ConnectToMongo;

public class finalCrawl {
	static ConnectToMongo mongo = new ConnectToMongo();
	static String crawlUrl = "";
	static int count = 0;
	static int depth;
	static String extract_flag;
	static boolean extructCount = true;
	static boolean my = true;
	static List<String> linkStrList = new ArrayList<String>();

	public static void checkURL(List<String> webUrls)
			throws SocketTimeoutException, HttpStatusException, SAXException,
			TikaException {

		for (String myUrl : webUrls) {

			System.out.println("size " + webUrls.size() + " the name " + myUrl);

			if (myUrl.startsWith("/")) {
				myUrl = crawlUrl + myUrl;
				crawl(myUrl);
				// System.out.println(myUrl);
			} else if ((myUrl.equals(null)) || (myUrl.startsWith("/*"))
					|| (myUrl.startsWith("*")) || myUrl.startsWith("https://")) {
				// System.out.println("do nothing");

			} else if (myUrl.startsWith("#")) {
				myUrl = crawlUrl + "/" + myUrl;
				// System.out.println(myUrl);
				crawl(myUrl);
			} else {
				crawl(myUrl);
				// System.out.println(myUrl);
			}
		}
	}

	public static void crawl(String s) throws HttpStatusException,
			SocketTimeoutException, SAXException, TikaException {
		// System.out.println(webUrls.size());

		try {
			// this gets all the url from the first given link
			Document doc = Jsoup.connect(s).get();
			Elements links = doc.select("path");
			System.out.println("link size " + links.size());

			// for loop for depth 1
			for (Element link : links) {
				System.out.println("the url " + link.attr("abs:href"));
				System.out.println("the text " + link.text());
				downloadHTML(link.attr("abs:href"));
				// System.out.println("after the first for loop ");

				// Document doc2 = Jsoup.connect(link.attr("abs:href")).get();
				// Elements links2 = doc2.select("a[href]");

				// // second foor loop for depth 2
				// for (Element link2 : links2) {
				// System.out.println("the url " + link2.attr("abs:href"));
				// System.out.println("the text " + link2.text());
				// // print(" * a: <%s>  (%s)", link2.attr("abs:href"),
				// // trim(link2.text(), 35));
				// downloadHTML(link2.attr("abs:href"));
				// }
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public static void downloadHTML(String urlFromTheList) throws IOException,
			SAXException, TikaException {
		System.out.println(urlFromTheList);
		// there are some lines in this method that I looked up online to
		// understand the logic.

		count++;
		URL url;
		InputStream is = null;
		DataInputStream dis;
		String s;

		// if (!(urlFromTheList.startsWith("https://"))) {
		try {

			url = new URL("http://ocean.pcwerk.com/data/" + urlFromTheList);
			is = url.openStream();

			dis = new DataInputStream(new BufferedInputStream(is));

			File file = new File("C:\\Users\\andy\\Desktop\\test\\"
					+ urlFromTheList);
			file.getParentFile().mkdirs();
			PrintWriter writer = new PrintWriter(file, "UTF-8");

			while ((s = dis.readLine()) != null) {
				writer.println(s);
			}
			System.out.println("file " + count + " finished creating");
			writer.close();

		} catch (MalformedURLException mue) {
			System.out.println("");

		} catch (IOException ioe) {

			System.out.println("");

		} finally {

			try {
				is.close();
			} catch (IOException ioe) {

			}

		}

		if (my == true) {
			my = false;
			extract();
		}

		// } else {
		// System.out.println("this url cannot be crawled");

	}

	public static void extract() throws IOException, SAXException,
			TikaException {

		System.out.println("the count in extract is " + count);
		InputStream in = new FileInputStream(new File(
				"C:\\Users\\andy\\Desktop\\test\\" + crawlUrl));
		LinkContentHandler linkHandler = new LinkContentHandler();
		BodyContentHandler textHandler = new BodyContentHandler(-1);
		ToHTMLContentHandler toHTMLHandler = new ToHTMLContentHandler();
		TeeContentHandler teeHandler = new TeeContentHandler(linkHandler,
				textHandler, toHTMLHandler);
		Metadata metadata = new Metadata();
		ParseContext parseContext = new ParseContext();
		HtmlParser parser = new HtmlParser();
		parser.parse(in, teeHandler, metadata, parseContext);
		// if (extract_flag.equals("-e")) {
		// mongo.inserting(metadata.get("title"), metadata.get("timestamp"),
		// metadata.get("viewport"), metadata.get("author"));
		// }
		// System.out.println(metadata.get("title") + "  titlehgf");
		// System.out.println(metadata.get("timestamp") + "  time");
		// System.out.println(metadata.get("viewport") + "  view");
		// System.out.println(metadata.get("author") + "  author");

		// gets all the links for depth 0
		if (extructCount == true) {
			extructCount = false;
			System.out.println("extructCount " + extructCount);
			List<Link> linkList = linkHandler.getLinks();
			linkStrList = new ArrayList<String>();
			for (Link l : linkList) {

				linkStrList.add(l.getUri());

			}
			// for (String s : linkStrList) {
			// System.out.println(s);
			// }

			// checkURL(linkStrList);

		}
	}

	public static void main(String[] args) throws IOException, SAXException,
			TikaException {
		int count = 0;
		crawlUrl = "index.html";
		downloadHTML(crawlUrl);
		for (String s : linkStrList) {
			downloadHTML(s);
			count++;
		}
	}
}