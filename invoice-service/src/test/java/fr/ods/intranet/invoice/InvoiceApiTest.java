package fr.ods.intranet.invoice;

import fr.ods.intranet.invoice.api.RestInvoiceApiVerticle;
import io.restassured.response.Response;
import io.vertx.core.Vertx;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.jayway.awaitility.Awaitility.await;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.assertj.core.api.Assertions.*;


/**
 * Test case for {@link RestInvoiceApiVerticle}.
 *
 * @author Eric Zhao
 */
public class InvoiceApiTest {

  private Vertx vertx;

  @Before
  public void setUp() throws Exception {
    vertx = Vertx.vertx();
    AtomicBoolean completed = new AtomicBoolean();
    vertx.deployVerticle(new InvoiceVerticle(), ar -> completed.set(ar.succeeded()));
    await().untilAtomic(completed, is(true));
  }

  @After
  public void tearDown() throws Exception {
    AtomicBoolean completed = new AtomicBoolean();
    vertx.close((v) -> completed.set(true));
    await().untilAtomic(completed, is(true));
  }

  @Test
  public void testGetAll() throws Exception {
    Response response = given().port(8081).get("/invoices");
    assertThat(response.getStatusCode()).isEqualTo(200);
    assertThat(response.asString()).isNotBlank();
    System.out.println(response.asString());
  }

}