package dump;

import java.io.File;
import java.io.PrintWriter;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class dump {
	static MongoClient mongoClient;
	static DBCollection coll;

	public static void main(String[] args) {
		try {

			// To connect to mongodb server
			mongoClient = new MongoClient("localhost", 27017);
			@SuppressWarnings("deprecation")
			DB db = mongoClient.getDB("engineKiller");
			coll = db.getCollection("crawl");
			DBCursor cursor = coll.find();

			File file = new File(
					"C:\\Users\\andy\\workspaceForLuna\\cs-454-starter-fall-2015-master\\"
							+ "dumpFile.txt");
			file.getParentFile().mkdirs();
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			while (cursor.hasNext()) {
				writer.print(cursor.next());

			}
			writer.close();
			System.out.println("Dump file is created!");

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());

		}
	}
}
