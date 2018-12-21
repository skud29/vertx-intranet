package fr.ods.intranet.invoice;

import fr.ods.intranet.invoice.impl.InvoiceVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.EventBusService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class InvoiceVerticleTest {
    private Vertx vertx;
    private ServiceDiscovery discovery;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        discovery = ServiceDiscovery.create(vertx);
        vertx.deployVerticle(InvoiceVerticle.class.getName(),
                context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testMyApplication(TestContext context) {
        Async async = context.async();

        EventBusService.getProxy(discovery, InvoiceService.class, ar -> {
            if (ar.succeeded()) {
                InvoiceService service = ar.result();
                service.getAll(handler-> {
                    if (handler.succeeded()) {
                        // do something with the result by calling handler.result()
                        System.out.println(handler.result());
                    } else {
                        // do something with the error by calling for instance handler.cause()
                    }
                    System.out.println("end testMyApplication");
                    async.complete();
                });
            }
        });
    }
}
