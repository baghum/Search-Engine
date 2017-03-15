package indexing;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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

import org.json.JSONObject;
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

import com.sun.media.jfxmedia.Media;

public class HtmlAndExtract {
	static ConnectToMongoIndex mongo = new ConnectToMongoIndex();
	static String crawlUrl = "http://www.";
	static int count = 0;
	static int depth;
	static String extract_flag;
	static boolean extructCount = true;

	@SuppressWarnings("resource")
	public static void downloadHTML(String filePath) throws IOException,
			SAXException, TikaException {
		BufferedReader br = null;
		String line = "";

		URL url;
		InputStream is = null;
		DataInputStream dis;
		String s;
		String[] links = null;

		br = new BufferedReader(new FileReader(filePath));
		while ((line = br.readLine()) != null) {
			System.out.println(links);
			links = line.split("space");
			//System.out.println(links);
		}

		for (int i = 0; i < links.length; i++) {
			count++;

			String path = links[i];

			try {

				url = new URL("file:///" + path);
				is = url.openStream();

				dis = new DataInputStream(new BufferedInputStream(is));

				File file = new File(
						"C:\\Users\\andy\\Desktop\\CS 454\\getHTML\\" + "file_"
								+ count + ".txt");
				file.getParentFile().mkdirs();
				PrintWriter writer = new PrintWriter(file, "UTF-8");

				while ((s = dis.readLine()) != null) {
					writer.println(s);
				}
				System.out.println("file " + count + " finished creating");
				writer.close();

			} catch (MalformedURLException mue) {
				System.out.println(mue);

			} catch (IOException ioe) {

				System.out.println(ioe);

			} finally {

				try {
					is.close();
				} catch (IOException ioe) {

				}
			}
			//extract();
		}
	}

	public static void extract() throws IOException, SAXException,
			TikaException {

		System.out.println("the count in extract is " + count);
		InputStream in = new FileInputStream(new File(
				"C:\\Users\\andy\\Desktop\\CS 454\\getHTML\\" + "file_" + count
						+ ".txt"));
		LinkContentHandler linkHandler = new LinkContentHandler();
		BodyContentHandler textHandler = new BodyContentHandler();
		ToHTMLContentHandler toHTMLHandler = new ToHTMLContentHandler();
		TeeContentHandler teeHandler = new TeeContentHandler(linkHandler,
				textHandler, toHTMLHandler);
		Metadata metadata = new Metadata();
		ParseContext parseContext = new ParseContext();
		HtmlParser parser = new HtmlParser();
		parser.parse(in, teeHandler, metadata, parseContext);

		File file = new File("C:\\Users\\andy\\Desktop\\CS 454\\Json\\"
				+ "file_" + count + ".txt");
		file.getParentFile().mkdirs();
		PrintWriter writer = new PrintWriter(file, "UTF-8");

		writer.println("{");
		writer.println("	Name: " + metadata.get("title"));
		writer.println("	Timestamp: " + metadata.get("timestamp"));
		writer.println("	Viewport: " + metadata.get("viewport"));
		writer.println("	Author: " + metadata.get("author"));
		writer.println("}");
		writer.close();

		System.out.println("json is created");

	}
//	public static void main(String[] args) throws IOException, SAXException, TikaException{
//		downloadHTML("C:\\Users\\andy\\Desktop\\CS 454\\TestIndex\\path.txt");
//	}

}