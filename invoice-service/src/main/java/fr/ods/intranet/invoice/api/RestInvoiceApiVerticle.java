package fr.ods.intranet.invoice.api;

import fr.ods.intranet.common.RestApiVerticle;
import fr.ods.intranet.invoice.InvoiceService;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

public class RestInvoiceApiVerticle extends RestApiVerticle {

    public static final String SERVICE_NAME = "intranet-invoice-rest-api";

    private static final String API_ADD = "/add";
    private static final String API_RETRIEVE_BY_PAGE = "/invoices";
    private static final String API_RETRIEVE_ALL = "/invoices";
    private static final String API_RETRIEVE_PRICE = "/:invoiceId/price";
    private static final String API_RETRIEVE = "/:invoiceId";
    private static final String API_UPDATE = "/:invoiceId";
    private static final String API_DELETE = "/:invoiceId";
    private static final String API_DELETE_ALL = "/all";

    private final InvoiceService service;
    private RedisClient redisClient;

    public RestInvoiceApiVerticle(InvoiceService service) {
        this.service = service;
    }

    @Override
    public void start(Future<Void> future) throws Exception {
        super.start();
        final Router router = Router.router(vertx);
        // body handler
        router.route().handler(BodyHandler.create());

        // API route handler
        router.get(API_RETRIEVE_BY_PAGE).handler(this::apiRetrieveByPage);
        router.get(API_RETRIEVE_ALL).handler(this::apiRetrieveAll);
        router.get(API_RETRIEVE).handler(this::apiRetrieve);

        // get HTTP host and port from configuration, or use default value
        String host = config().getString("invoice.http.address", "0.0.0.0");
        int port = config().getInteger("invoice.http.port", 8081);

        String redisHost = config().getString("invoice.redis.host", "127.0.0.1");
        // Create the redis client
        redisClient = RedisClient.create(vertx, new RedisOptions().setHost(redisHost));

        // create HTTP server and publish REST service
        createHttpServer(router, host, port)
                .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port))
                .setHandler(future.completer());
    }

    private void apiRetrieve(RoutingContext context) {
        String invoiceId = context.request().getParam("invoiceId");
        String keyCache = "apiRetrieve_"+invoiceId;
        redisClient.get(keyCache, res -> {
            if (res.succeeded()) {
                System.out.println("key exists");
            }
            else {
                service.getById(invoiceId, resultHandlerNonEmpty(context, redisClient, keyCache, 1200));
            }
        });
    }

    private void apiRetrieveByPage(RoutingContext context) {
        try {
            String o = context.request().getParam("offset");
            int offset = o == null ? 1 : Integer.parseInt(o);
            String l = context.request().getParam("limit");
            int limit = l == null ? 20 : Integer.parseInt(l);
            String orderBy = context.request().getParam("orderBy");
            service.getAllByPage(offset, limit, orderBy, resultHandler(context, Json::encodePrettily));
        } catch (Exception ex) {
            badRequest(context, ex);
        }
    }

    private void apiRetrieveAll(RoutingContext context) {
        service.getAll(resultHandler(context, Json::encodePrettily));
    }

}
