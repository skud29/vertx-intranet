package fr.ods.intranet.invoice.impl;

import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.CouchbaseAsyncCluster;
import fr.ods.intranet.database.CouchbaseHelper;
import fr.ods.intranet.invoice.File;
import fr.ods.intranet.invoice.Invoice;
import fr.ods.intranet.invoice.InvoiceService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Override
    public void getAll(Handler<AsyncResult<JsonArray>> resultHandler) {
        Long t1 = System.currentTimeMillis();
        CouchbaseHelper.select(
                bucket,
                "SELECT * FROM `intranet`where type=\"invoices\";", handler-> {
                    if (handler.succeeded()) {
                        // do something with the result by calling handler.result()
                        Long t2 = System.currentTimeMillis();
                        System.out.println("Durée requete = "+(t2-t1)+" ms");
                        resultHandler.handle(Future.succeededFuture(handler.result()));
                    } else {
                        // do something with the error by calling for instance handler.cause()
                    }
                }
        );
    }
}
