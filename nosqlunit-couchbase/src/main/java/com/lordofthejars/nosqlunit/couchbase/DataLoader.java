package com.lordofthejars.nosqlunit.couchbase;

import com.couchbase.client.CouchbaseClient;
import com.lordofthejars.nosqlunit.couchbase.model.Document;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.MapType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DataLoader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static final String DATA_ROOT = "data";

    public static final String DESIGN_ROOT = "designDocs";// TODO

    private CouchbaseClient couchbaseClient;

    public DataLoader(CouchbaseClient couchbaseClient) {
        super();
        this.couchbaseClient = couchbaseClient;
    }

    public void load(final InputStream dataScript) {
        final Map<String, Document> documentsIterator = getDocuments(dataScript);
        insertDocuments(documentsIterator);
    }

    private void insertDocuments(final Map<String, Document> documentsIterator) {
        for (final Map.Entry<String, Document> documentEntry : documentsIterator.entrySet()) {
            final Document document = documentEntry.getValue();
            try {
                couchbaseClient.add(documentEntry.getKey(), document.calculateExpiration(),
                        MAPPER.writeValueAsString(document.getDocument())).get();
            } catch (JsonGenerationException e) {
               throw new IllegalArgumentException(e);
            } catch (JsonMappingException e) {
                throw new IllegalArgumentException(e);
            } catch (InterruptedException e) {
                throw new IllegalArgumentException(e);
            } catch (ExecutionException e) {
                throw new IllegalArgumentException(e);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public static Map<String, Document> getDocuments(final InputStream dataScript) {
        TypeFactory typeFactory = MAPPER.getTypeFactory();
        final MapType mapType = typeFactory.constructMapType(Map.class, String.class, Document.class);
        JavaType stringType = typeFactory.uncheckedSimpleType(String.class);
        MapType type = typeFactory.constructMapType(Map.class, stringType, mapType);

        Map<String, Map<String, Document>> rootNode;
        try {
            rootNode = MAPPER.readValue(dataScript, type);
        } catch (org.codehaus.jackson.JsonParseException e) {
            throw new IllegalArgumentException(e);
        } catch (JsonMappingException e) {
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
           throw new IllegalArgumentException(e);
        }
        return rootNode.get(DATA_ROOT);
    }

}
