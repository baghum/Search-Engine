package finalProject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import java.util.Arrays;
import java.util.List;

public class MongoRank {

	MongoClient mongoClient;
	DBCollection coll;

	public void ToMongo() {
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

	public void inserting(String path, double score) {
		ToMongo();
		BasicDBObject doc = new BasicDBObject("path", path).append(
				"score", score);

		coll.insert(doc);
		// System.out.println("Document inserted successfully");

		mongoClient.close();
	}
}
