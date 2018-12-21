package fr.ods.intranet.invoice;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@ProxyGen
public interface InvoiceService {

    List<Invoice> getAll(Handler<AsyncResult<String>> resultHandler);

    void

}
