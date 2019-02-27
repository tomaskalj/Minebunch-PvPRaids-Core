package com.minebunch.core.storage.database;

import com.minebunch.core.callback.DocumentCallback;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.util.Map;
import lombok.Getter;
import org.bson.Document;

public class MongoStorage {
	@Getter
	private final MongoDatabase database;

	public MongoStorage() {
		MongoClient mongoClient = MongoClients.create();
		database = mongoClient.getDatabase("minebunch");
	}

	public void getOrCreateDocument(String collectionName, Object documentObject, DocumentCallback callback) {
		MongoCollection<Document> collection = database.getCollection(collectionName);
		Document document = new Document("_id", documentObject);

		try (MongoCursor<Document> cursor = collection.find(document).iterator()) {
			if (cursor.hasNext()) {
				callback.call(cursor.next(), true);
			} else {
				collection.insertOne(document);
				callback.call(document, false);
			}
		}
	}

	public MongoCursor<Document> getAllDocuments(String collectionName) {
		MongoCollection<Document> collection = database.getCollection(collectionName);
		return collection.find().iterator();
	}

	public Document getDocumentByFilter(String collectionName, String filter, Object documentObject) {
		MongoCollection<Document> collection = database.getCollection(collectionName);
		return collection.find(Filters.eq(filter, documentObject)).first();
	}

	public Document getDocument(String collectionName, Object documentObject) {
		MongoCollection<Document> collection = database.getCollection(collectionName);
		return collection.find(Filters.eq("_id", documentObject)).first();
	}

	public void updateDocument(String collectionName, Object documentObject, String key, Object newValue) {
		MongoCollection<Document> collection = database.getCollection(collectionName);
		collection.updateOne(Filters.eq(documentObject), Updates.set(key, newValue));
	}

	public MongoCollection<Document> getCollection(String collectionName) {
		return database.getCollection(collectionName);
	}

	public void massUpdate(String collectionName, Object documentObject, Map<String, Object> updates) {
		MongoCollection<Document> collection = database.getCollection(collectionName);

		for (Map.Entry<String, Object> entry : updates.entrySet()) {
			collection.updateOne(Filters.eq(documentObject), Updates.set(entry.getKey(), entry.getValue()));
		}
	}
}
