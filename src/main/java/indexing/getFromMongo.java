package indexing;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class getFromMongo {
	static MongoClient mongoClient;
	static DBCollection coll;

	// public static Tf_Idf idf = new Tf_Idf();

	public static Double connect(String s) {
		double score = 0;

		try {

			System.out.println("in mongo");
			//mongoClient = new MongoClient("localhost", 27017);
			//@SuppressWarnings("deprecation")
			DB db = mongoClient.getDB("rank");
			coll = db.getCollection("rankTable");
			while (true) {

				// System.out.println("SEARCH");
				// Scanner sc = new Scanner(System.in);
				// String m = sc.nextLine();
				BasicDBObject q = new BasicDBObject();
				q.put("path", s);
				DBCursor cursor = coll.find(q);

				while (cursor.hasNext()) {

					score = (double) cursor.next().get("score");
				}
			}

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());

		}
		//mongoClient.close();
		return score;
	}
}
