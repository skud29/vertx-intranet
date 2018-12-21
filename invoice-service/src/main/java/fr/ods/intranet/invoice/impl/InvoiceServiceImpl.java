package fr.ods.intranet.invoice.impl;

import fr.ods.intranet.invoice.InvoiceService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class InvoiceServiceImpl implements InvoiceService {

    private Vertx vertx;

    public InvoiceServiceImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void save(String collection, JsonObject document, Handler<AsyncResult<String>> resultHandler) {
        System.out.printf("save in collection");
        resultHandler.handle(Future.succeededFuture("Hello Invoice"));
    }
}
