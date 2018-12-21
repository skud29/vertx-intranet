package fr.ods.intranet.invoice;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

@ProxyGen
public interface InvoiceService {

    void getAll(Handler<AsyncResult<Invoices>> resultHandler);

}
