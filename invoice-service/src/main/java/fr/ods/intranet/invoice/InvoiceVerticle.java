package fr.ods.intranet.invoice;

import fr.ods.intranet.common.BaseMicroserviceVerticle;
import fr.ods.intranet.invoice.impl.InvoiceServiceImpl;
import io.vertx.core.Future;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.serviceproxy.ServiceBinder;

public class InvoiceVerticle extends BaseMicroserviceVerticle {

    @Override
    public void start(Future<Void> startFuture) {
        super.start();

        discovery = ServiceDiscovery.create(vertx);

        InvoiceService invoiceService = new InvoiceServiceImpl(vertx, config());

        new ServiceBinder(vertx).setAddress(InvoiceService.SERVICE_ADDRESS).register(InvoiceService.class, invoiceService);

        publishEventBusService(InvoiceService.SERVICE_NAME, InvoiceService.SERVICE_ADDRESS, InvoiceService.class)
                .setHandler(startFuture.completer());
    }

}
