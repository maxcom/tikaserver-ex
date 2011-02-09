package su.msk.jet.tikaserver;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.test.framework.JerseyTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class TikaResourceTest extends JerseyTest {
  private static final String TIKA_PATH = "tika";
  public static final String TEST_DOC = "test.doc";
  public static final String TEST_XLSX = "16637.xlsx";
  private static final int UNPROCESSEABLE = 422;

  public TikaResourceTest() throws Exception {
    super("su.msk.jet.tikaserver");
  }

  /**
   * Test to see that the message "Hello World" is sent in the response.
   */
  @Test
  public void testHelloWorld() {
    String responseMsg = webResource.path(TIKA_PATH).get(String.class);
    assertEquals(TikaResource.GREETING, responseMsg);
  }

  @Test
  public void testSimpleWord() {
    String responseMsg =
            webResource.path(TIKA_PATH)
            .type("application/msword")
                    .put(String.class, ClassLoader.getSystemResourceAsStream(TEST_DOC));

    assertTrue(responseMsg.contains("test"));
  }

  @Test
  public void testApplicationWadl() {
    String serviceWadl = webResource.path("application.wadl").
            accept(MediaTypes.WADL).get(String.class);

    assertTrue(serviceWadl.length() > 0);
  }

  @Test
  public void testPasswordXLS() throws Exception {
    ClientResponse cr =
            webResource
                    .path(TIKA_PATH)
                    .type("application/vnd.ms-excel")                    
                    .put(ClientResponse.class, ClassLoader.getSystemResourceAsStream("password.xls"));

    assertEquals(UNPROCESSEABLE, cr.getStatus());
  }

/*  @Test
  public void testWord95() throws Exception {
    ClientResponse cr =
            webResource
                    .path(TIKA_PATH)
                    .put(ClientResponse.class, ClassLoader.getSystemResourceAsStream("word95.doc"));

    assertEquals(200, cr.getStatus());

  }
*/


}
