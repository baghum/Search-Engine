package project;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import java.util.Arrays;
import java.util.List;

public class ConnectToMongo {
	MongoClient mongoClient;
	DBCollection coll;

	

	public void ToMongo() {
		try {

			// To connect to mongodb server
			mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("engineKiller");
			// System.out.println("Connect to database successfully");

			coll = db.getCollection("crawl");
			//System.out.println("Collection mycol selected successfully");

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());

		}
	}
   
	public void inserting(String title, String timestamp, String viewport,
			String author) {
		ToMongo();
		BasicDBObject doc = new BasicDBObject("title", title)
				.append("timestamp", timestamp).append("viewport", viewport)
				.append("author", author);
		
		coll.insert(doc);
		//System.out.println("Document inserted successfully");

		mongoClient.close();
	}
}