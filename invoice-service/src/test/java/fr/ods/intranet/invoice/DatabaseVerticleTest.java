package fr.ods.intranet.invoice;

import fr.ods.intranet.database.DatabaseService;
import fr.ods.intranet.database.impl.DatabaseVerticle;
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

import java.util.concurrent.TimeUnit;

@RunWith(VertxUnitRunner.class)
public class DatabaseVerticleTest {
    private Vertx vertx;
    private ServiceDiscovery discovery;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        discovery = ServiceDiscovery.create(vertx);
        vertx.deployVerticle(DatabaseVerticle.class.getName(),
                context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testMyApplication(TestContext context) {
        Async async = context.async();

        EventBusService.getProxy(discovery, DatabaseService.class, ar -> {
            if (ar.succeeded()) {
                DatabaseService service = ar.result();
                Long t1 = System.currentTimeMillis();
                service.read("SELECT * FROM `intranet`where type=\"invoices\";", handler-> {
                    if (handler.succeeded()) {
                        // do something with the result by calling handler.result()
                        Long t2 = System.currentTimeMillis();
                        System.out.println(handler.result());
                        System.out.println("Durée requete = "+(t2-t1)+" ms");
                    } else {
                        // do something with the error by calling for instance handler.cause()
                    }
                    System.out.println("end test Database Verticle");
                    async.complete();
                });
            }
            else {
                async.complete();
            }
        });
    }
}
