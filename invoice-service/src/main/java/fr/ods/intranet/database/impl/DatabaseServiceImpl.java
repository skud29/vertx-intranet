package fr.ods.intranet.database.impl;

import com.couchbase.client.core.CouchbaseException;
import com.couchbase.client.java.AsyncBucket;
import com.couchbase.client.java.query.AsyncN1qlQueryRow;
import com.couchbase.client.java.query.N1qlQuery;
import fr.ods.intranet.database.DatabaseService;
import fr.ods.intranet.invoice.File;
import fr.ods.intranet.invoice.Invoice;
import fr.ods.intranet.invoice.InvoiceService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import rx.Observable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseServiceImpl implements DatabaseService {

    private Vertx vertx;
    private AsyncBucket bucket;

    public DatabaseServiceImpl(Vertx vertx, AsyncBucket bucket) {
        this.vertx = vertx;
        this.bucket = bucket;
    }

    @Override
    public void open(Handler<AsyncResult<String>> resultHandler) {
        resultHandler.handle(Future.succeededFuture("open"));

    }

    @Override
    public void close(Handler<AsyncResult<String>> resultHandler) {
        resultHandler.handle(Future.succeededFuture("close"));

    }

    @Override
    public void read(String query, Handler<AsyncResult<JsonArray>> resultHandler) {
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
