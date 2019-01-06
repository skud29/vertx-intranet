package fr.ods.intranet.database;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

@ProxyGen
public interface DatabaseService {

    void open(Handler<AsyncResult<String>> resultHandler);

    void close(Handler<AsyncResult<String>> resultHandler);

    void read(String query, Handler<AsyncResult<JsonArray>> resultHandler);

}
