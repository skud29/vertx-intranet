package fr.ods.intranet.invoice.impl;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.CouchbaseAsyncCluster;
import fr.ods.intranet.database.CouchbaseHelper;
import fr.ods.intranet.invoice.InvoiceService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

public class InvoiceServiceImpl implements InvoiceService {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceServiceImpl.class);

    private Vertx vertx;
    private CouchbaseAsyncCluster cluster = null;
    private AsyncBucket bucket = null;

    public InvoiceServiceImpl(Vertx vertx, JsonObject config) {
        this.vertx = vertx;
        // Connexion au cluster CouchBase
        cluster = CouchbaseHelper.connect(config);
        // Ouverture du bucket
        this.bucket = CouchbaseHelper.openBucket(cluster, config.getString("invoiceBucket", "intranet"))
                .toBlocking()
                .single();
    }

    public void finalize() {
        release();
    }

    @Override
    public void release() {
        if (this.bucket != null) {
            CouchbaseHelper.closeBucket(this.bucket).doOnNext(value -> this.bucket=null).subscribe();
        }
        if (this.cluster != null) {
            CouchbaseHelper.diconnect(this.cluster).doOnNext(value -> this.cluster=null).subscribe();
        }
    }

    @Override
    public void getAll(Handler<AsyncResult<JsonArray>> resultHandler) {
        Instant start = Instant.now();
        CouchbaseHelper.select(
                bucket,
                "SELECT * FROM `intranet`where type=\"invoices\";", handler-> {
                    if (handler.succeeded()) {
                        // do something with the result by calling handler.result()
                        Instant finish = Instant.now();
                        logger.info("Durée requete = "+ Duration.between(start, finish).toMillis()+" ms");
                        resultHandler.handle(Future.succeededFuture(handler.result()));
                    } else {
                        // do something with the error by calling for instance handler.cause()
                    }
                }
        );
    }

    @Override
    public void getAllByPage(int offset, int limit, String orderBy, Handler<AsyncResult<JsonArray>> resultHandler) {
        Instant start = Instant.now();
        // Construction de la requête
        StringBuilder request = new StringBuilder("SELECT * FROM `intranet` where type=\"invoices\"");
        if (orderBy!=null) {
            request.append(" ORDER BY ").append(orderBy);
        }
        request.append(" OFFSET ").append(offset).append(" LIMIT ").append(limit).append(";");
        CouchbaseHelper.select(
                bucket,
                request.toString(),
                handler-> {
                    if (handler.succeeded()) {
                        // do something with the result by calling handler.result()
                        Instant finish = Instant.now();
                        logger.info("Durée requete = "+Duration.between(start, finish).toMillis()+" ms");
                        resultHandler.handle(Future.succeededFuture(handler.result()));
                    } else {
                        // do something with the error by calling for instance handler.cause()
                    }
                }
        );
    }
}
