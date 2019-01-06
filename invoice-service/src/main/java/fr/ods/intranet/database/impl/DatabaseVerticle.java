package fr.ods.intranet.database.impl;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.CouchbaseAsyncCluster;
import fr.ods.intranet.database.DatabaseService;
import io.vertx.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.serviceproxy.ProxyHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseVerticle extends AbstractVerticle {

    private ServiceDiscovery discovery;
    private Record publishedRecord = null;
    private CouchbaseAsyncCluster  cluster = null;
    private AsyncBucket bucket = null;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);

    }

    @Override
    public void start(Future<Void> startFuture) {

        List<Future> futures = new ArrayList<>();

        discovery = ServiceDiscovery.create(vertx);

        //getting the bootstrap node, as a JSON array (default to localhost)
        JsonArray seedNodeArray = config().getJsonArray("couchbase.seedNodes", new JsonArray().add("localhost"));
        //convert to a List
        List seedNodes = new ArrayList<>(seedNodeArray.size());
        for (Object seedNode : seedNodeArray) {
            seedNodes.add((String) seedNode);
        }
        //use that to bootstrap the Cluster
        this.cluster = CouchbaseAsyncCluster.create(seedNodes);

        // Authentification
        this.cluster.authenticate("admin", "gnomes");

        Future<Void> openBucketFuture = Future.future();
        cluster.openBucket(config().getString("couchbase.bucketName", "intranet"))
                .doOnNext(openedBucket -> System.out.println("Bucket opened " + openedBucket.name()))
                .subscribe(
                    openedBucket -> bucket = openedBucket,
                    openBucketFuture::fail,
                    openBucketFuture::complete
                );

        openBucketFuture.compose(v -> {
            DatabaseService service = new DatabaseServiceImpl(vertx, bucket);
            ProxyHelper.registerService(DatabaseService.class, vertx, service, "database-service-address");

            Record record = EventBusService.createRecord(
                    "database-eventbus-service",
                    "database-service-address",
                    DatabaseService.class
            );

            Future<Void> publishFuture = Future.future();
            discovery.publish(record, ar -> {
                if (ar.succeeded()) {
                    // publication succeeded
                    publishedRecord = ar.result();
                    publishFuture.complete();
                } else {
                    // publication failed
                    publishFuture.fail("Fail to publish database service" );
                }
            });
            return publishFuture;
        }).compose(v -> {
            System.out.println("Start Database bucket finished");
            startFuture.complete();
        }, startFuture);
    }

    @Override
    public void stop(Future<Void> stopFuture) {
        List<Future> futures = new ArrayList<>();
        if (publishedRecord != null) {
            Future<Void> unregistrationFuture = Future.future();
            futures.add(unregistrationFuture);
            discovery.unpublish(publishedRecord.getRegistration(), unregistrationFuture.completer());
        }

        if (futures.isEmpty()) {
            cluster.disconnect();
            discovery.close();
            stopFuture.complete();
        } else {
            CompositeFuture.all(futures)
                    .setHandler(ar -> {
                        cluster.disconnect();
                        discovery.close();
                        if (ar.failed()) {
                            stopFuture.fail(ar.cause());
                        } else {
                            stopFuture.complete();
                        }
                    });
        }
    }
}
