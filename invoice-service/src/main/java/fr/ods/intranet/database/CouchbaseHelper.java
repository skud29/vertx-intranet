package fr.ods.intranet.database;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseAsyncCluster;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

public class CouchbaseHelper {

    private static final Logger logger = LoggerFactory.getLogger(CouchbaseHelper.class);

    public static CouchbaseAsyncCluster connect(JsonObject config) {
        //getting the bootstrap node, as a JSON array (default to localhost)
        JsonArray seedNodeArray = config.getJsonArray("couchbase.seedNodes", new JsonArray().add("localhost"));
        //convert to a List
        List seedNodes = new ArrayList<>(seedNodeArray.size());
        for (Object seedNode : seedNodeArray) {
            seedNodes.add(seedNode);
        }
        //use that to bootstrap the Cluster
        CouchbaseAsyncCluster cluster = CouchbaseAsyncCluster.create(seedNodes);

        // Authentification
        cluster.authenticate(config.getString("user", "admin"), config.getString("password", "gnomes"));

        logger.info("Cluster CouchBase connecté");
        return cluster;
    }

    public static Observable<Boolean> diconnect(CouchbaseAsyncCluster cluster) {
        return cluster.disconnect();
    }

    public static Observable<AsyncBucket> openBucket(CouchbaseAsyncCluster cluster, String bucketName) {
        return cluster.openBucket(bucketName)
                .doOnNext(openedBucket -> logger.info("Bucket CouchBase ouvert : " + openedBucket.name()));
    }

    public static Observable<Boolean> closeBucket(AsyncBucket bucket) {
        return bucket.close();
    }
}
