package fr.ods.intranet.invoice.impl;

import fr.ods.intranet.invoice.InvoiceService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.serviceproxy.ProxyHelper;

import java.util.ArrayList;
import java.util.List;

public class InvoiceVerticle extends AbstractVerticle {

    private ServiceDiscovery discovery;
    private Record publishedRecord = null;

    @Override
    public void start(Future<Void> startFuture) {
        discovery = ServiceDiscovery.create(vertx);

        InvoiceService service = new InvoiceServiceImpl(vertx);

        ProxyHelper.registerService(InvoiceService.class, vertx, service, "invoice-service-address");

        Record record = EventBusService.createRecord(
                "invoice-eventbus-service",
                "invoice-service-address",
                InvoiceService.class
        );

        discovery.publish(record, ar -> {
            if (ar.succeeded()) {
                // publication succeeded
                publishedRecord = ar.result();
                startFuture.complete();
            } else {
                // publication failed
                startFuture.fail("Fail to publish invoice service" );
            }
        });
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
            discovery.close();
            stopFuture.complete();
        } else {
            CompositeFuture.all(futures)
                    .setHandler(ar -> {
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
