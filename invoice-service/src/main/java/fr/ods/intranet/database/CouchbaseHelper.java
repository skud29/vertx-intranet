package fr.ods.intranet.database;

import com.couchbase.client.core.CouchbaseException;
import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.CouchbaseAsyncCluster;
import com.couchbase.client.java.query.AsyncN1qlQueryRow;
import com.couchbase.client.java.query.N1qlQuery;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
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
        return cluster.disconnect()
                .doOnNext(isDisconnectedCluster -> logger.info("Cluser COucheBase déconnecté : "+isDisconnectedCluster.toString()));
    }

    public static Observable<AsyncBucket> openBucket(CouchbaseAsyncCluster cluster, String bucketName) {
        return cluster.openBucket(bucketName)
                .doOnNext(openedBucket -> logger.info("Bucket CouchBase ouvert : " + openedBucket.name()));
    }

    public static Observable<Boolean> closeBucket(AsyncBucket bucket) {
        return bucket.close()
                .doOnNext(isClosed -> logger.info("Bucket Couchebase fermé : " + isClosed.toString()));
    }

    public static void select(AsyncBucket bucket, String query, Handler<AsyncResult<JsonArray>> resultHandler) {
        JsonArray json = new JsonArray();
        N1qlQuery q = N1qlQuery.simple(query);
        bucket.query(q)
                .flatMap(result ->
                        result.errors()
                                .flatMap(e -> Observable.<AsyncN1qlQueryRow>error(new CouchbaseException("N1QL Error/Warning: " + e)))
                                .switchIfEmpty(result.rows())
                )
                .map(AsyncN1qlQueryRow::value)
                .subscribe(
                        rowContent -> json.add(new JsonObject(rowContent.toMap())),
                        runtimeError -> runtimeError.printStackTrace(),
                        () -> resultHandler.handle(Future.succeededFuture(json))
                );

    }

}
