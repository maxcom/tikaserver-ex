package su.msk.jet.tikaserver;

import au.com.bytecode.opencsv.CSVReader;
import com.sun.jersey.test.framework.JerseyTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class MetadataResourceTest extends JerseyTest {
  private static final String META_PATH = "/meta";

  public MetadataResourceTest() throws Exception {
    super("su.msk.jet.tikaserver");
  }

  @Test
  public void testSimpleWord() throws Exception {
    Reader reader =
            webResource.path(META_PATH)
            .type("application/msword")
                    .put(Reader.class, ClassLoader.getSystemResourceAsStream(TikaResourceTest.TEST_DOC));

    CSVReader csvReader = new CSVReader(reader);

    Map<String,String> metadata = new HashMap<String, String>();

    String[] nextLine;
    while ((nextLine = csvReader.readNext()) != null) {
      metadata.put(nextLine[0], nextLine[1]);
    }

    assertNotNull(metadata.get("Author"));
    assertEquals("Maxim Valyanskiy", metadata.get("Author"));
  }

  @Test
  public void testXLSX() throws Exception {
    Reader reader =
            webResource.path(META_PATH)
            .type("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("File-Name", TikaResourceTest.TEST_XLSX)
                    .put(Reader.class, ClassLoader.getSystemResourceAsStream(TikaResourceTest.TEST_XLSX));

    CSVReader csvReader = new CSVReader(reader);

    final Map < String, String > metadataActual = new HashMap < String, String > (),
            metadataExpected = new HashMap < String, String > ();

    String[] nextLine;
    while ((nextLine = csvReader.readNext()) != null) {
      metadataActual.put(nextLine[0], nextLine[1]);
    }
    metadataExpected.put("Author", "jet");
    metadataExpected.put("Application-Name", "Microsoft Excel");
    metadataExpected.put("description", "Тестовый комментарий");
    metadataExpected.put("resourceName", TikaResourceTest.TEST_XLSX);
    metadataExpected.put("protected", "false");
    metadataExpected.put("Creation-Date", "2010-05-11T12:37:42Z");
    metadataExpected.put("Last-Modified", "2010-05-11T14:46:20Z");
    assertEquals( true, metadataActual.size() >= metadataExpected.size() );
    for ( final Map.Entry < String, String > field : metadataExpected.entrySet() ) {
      final String key = field.getKey(), valueActual = metadataActual.get(key), valueExpected = field.getValue();
      assertNotNull( valueActual );
      assertEquals( valueExpected, valueActual );
    }
  }
}
