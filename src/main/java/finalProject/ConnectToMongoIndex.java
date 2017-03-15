package finalProject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import java.util.Arrays;
import java.util.List;

public class ConnectToMongoIndex {
	MongoClient mongoClient;
	DBCollection coll;

	public void ToMongo() {
		try {
			System.out.println("in mongo");
			// To connect to mongodb server
			mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("search");
			// System.out.println("Connect to database successfully");

			coll = db.getCollection("pages");
			// System.out.println("Collection mycol selected successfully");

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());

		}
		//mongoClient.close();
	}

	public void inserting(String fileName, String content, String path) {
		ToMongo();
		BasicDBObject doc = new BasicDBObject("fileName", fileName).append(
				"content", content).append("path", path);

		coll.insert(doc);
		// System.out.println("Document inserted successfully");

		mongoClient.close();
	}
	public void rankMongo(){
		try {
			System.out.println("in mongo");
			// To connect to mongodb server
			mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("rankProject");
			// System.out.println("Connect to database successfully");

			coll = db.getCollection("rankTableProject");
			// System.out.println("Collection mycol selected successfully");

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());

		}
	}
	public void rankInserting(String path, double score) {
		rankMongo();
		BasicDBObject doc = new BasicDBObject("path", path).append(
				"score", score);

		coll.insert(doc);
		// System.out.println("Document inserted successfully");

		mongoClient.close();
	}
}