package fr.ods.intranet.invoice;

import fr.ods.intranet.common.BaseMicroserviceVerticle;
import fr.ods.intranet.invoice.api.RestInvoiceApiVerticle;
import fr.ods.intranet.invoice.impl.InvoiceServiceImpl;
import io.vertx.core.DeploymentOptions;
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
                .compose(servicePublished -> deployRestService(invoiceService))
                .setHandler(startFuture.completer());
    }

    private Future<Void> deployRestService(InvoiceService service) {
        Future<String> future = Future.future();
        vertx.deployVerticle(new RestInvoiceApiVerticle(service),
                new DeploymentOptions().setConfig(config()),
                future.completer());
        return future.map(r -> null);
    }

}
