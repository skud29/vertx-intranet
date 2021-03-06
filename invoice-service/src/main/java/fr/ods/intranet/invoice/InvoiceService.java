package fr.ods.intranet.invoice;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

@ProxyGen
public interface InvoiceService {

    /**
     * The name of the event bus service.
     */
    String SERVICE_NAME = "invoice-eb-service";

    /**
     * The address on which the service is published.
     */
    String SERVICE_ADDRESS = "service.invoice";

    void release();
    void getAll(Handler<AsyncResult<JsonArray>> resultHandler);
    void getAllByPage(int offset, int limit, String orderBy, Handler<AsyncResult<JsonArray>> resultHandler);
    void getAllUnPaid(Handler<AsyncResult<JsonArray>> resultHandler);
    void getById(String invoiceId, Handler<AsyncResult<JsonArray>> resultHandler);
}
