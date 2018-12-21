package fr.ods.intranet.invoice;

import fr.ods.intranet.invoice.impl.InvoiceServiceImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.serviceproxy.ProxyHelper;

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

    /*
    @Override
    public void stop(Future<Void> stopFuture) {
        if (publishedRecord != null) {
            discovery.unpublish(publishedRecord.getRegistration(), ar -> {
                if (ar.succeeded()) {
                    // Ok
                } else {
                    // cannot un-publish the service, may have already been removed, or the record is not published
                }
            });
        }
    }
    */
}
