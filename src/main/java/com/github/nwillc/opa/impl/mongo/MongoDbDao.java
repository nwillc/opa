/*
 * Copyright 2017 nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without
 * fee is hereby granted, provided that the above copyright notice and this permission notice appear
 * in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
 * REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT,
 * OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE,
 * DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS
 * ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.github.nwillc.opa.impl.mongo;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.github.nwillc.opa.Dao;
import com.github.nwillc.opa.HasKey;
import com.github.nwillc.opa.query.Query;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.eq;

public class MongoDbDao<K, T extends HasKey<K>> implements Dao<K, T> {
    public static final String KEY_FIELD_DEFAULT = "key";
    private final ThreadLocal<ObjectMapper> mapper = ThreadLocal.withInitial(() ->
            new ObjectMapper().registerModule(new Jdk8Module())
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false));
    private final Class<T> tClass;
    private final MongoCollection<Document> collection;
    private final String keyFieldName;

    public MongoDbDao(final MongoClient client,
                      final String databaseName,
                      final Class<T> tClass) {
        this(client, databaseName, KEY_FIELD_DEFAULT, tClass);
    }

    public MongoDbDao(final MongoClient client,
                      final String databaseName, final String keyFieldName,
                      final Class<T> tClass) {
        this.tClass = tClass;
        this.keyFieldName = keyFieldName;
        collection = client.getDatabase(databaseName).getCollection(tClass.getSimpleName());
        collection.createIndex(new BasicDBObject(keyFieldName, 1));
    }

    @Override
    public Optional<T> findOne(K key) {
        Document document = collection.find(eq(keyFieldName, key)).first();
        return document == null ? Optional.empty() : Optional.of(fromJson(document.toJson(), tClass));
    }

    @Override
    public Stream<T> findAll() {
        return StreamSupport.stream(collection.find().spliterator(), false)
                .map(d -> fromJson(d.toJson(), tClass));
    }

    @Override
    public Stream<T> find(Query<T> query) {
        final MongoQueryMapper<T> mapper = new MongoQueryMapper<>();
        final Bson bson = (Bson) query.apply(mapper);
        return StreamSupport.stream(collection.find(bson).spliterator(), false)
                .map(d -> fromJson(d.toJson(), tClass));
    }

    @Override
    public void save(T entity) {
        Optional<T> one = findOne(entity.getKey());
        Document document = Document.parse(toJson(entity));
        if (one.isPresent()) {
            document = new Document("$set", document);
            collection.updateMany(eq(keyFieldName, entity.getKey()), document);
        } else {
            collection.insertOne(document);
        }
    }

    @Override
    public void delete(K key) {
        collection.deleteMany(eq(keyFieldName, key));
    }


    private <C> C fromJson(String json, Class<C> tClass) {
        try {
            return mapper.get().readValue(json, tClass);
        } catch (Exception e) {
            throw new RuntimeException("JSON parsing", e);
        }
    }

    private String toJson(Object obj) {
        try {
            return mapper.get().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON generation", e);
        }
    }
}
