package su.msk.jet.tikaserver;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.test.framework.JerseyTest;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tika.io.IOUtils;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class UnpackerResourceTest extends JerseyTest {
  private static final String UNPACKER_PATH = "/unpacker";

  private static final String TEST_DOC_WAV = "Doc1_ole.doc";
  private static final String WAV1_MD5 = "bdd0a78a54968e362445364f95d8dc96";
  private static final String WAV1_NAME = "_1310388059/MSj00974840000[1].wav";
  private static final String WAV2_MD5 = "3bbd42fb1ac0e46a95350285f16d9596";
  private static final String WAV2_NAME = "_1310388058/MSj00748450000[1].wav";
  private static final String APPLICATION_MSWORD = "application/msword";
  private static final String TEST_DOC_EMPTY = "Instruction.doc";
  private static final int NO_CONTENT = 204;
  private static final String JPG_NAME = "image1.jpg";
  private static final String XSL_IMAGE1_MD5 = "68ead8f4995a3555f48a2f738b2b0c3d";
  private static final String JPG_MD5 = XSL_IMAGE1_MD5;
  private static final String JPG2_NAME = "image2.jpg";
  private static final String JPG2_MD5 = "b27a41d12c646d7fc4f3826cf8183c68";
  private static final String TEST_DOCX_IMAGE = "2pic.docx";
  private static final String DOCX_IMAGE1_MD5 = "5516590467b069fa59397432677bad4d";
  private static final String DOCX_IMAGE2_MD5 = "a5dd81567427070ce0a2ff3e3ef13a4c";
  private static final String DOCX_IMAGE1_NAME = "image1.jpeg";
  private static final String DOCX_IMAGE2_NAME = "image2.jpeg";
  private static final String DOCX_EXE1_MD5 = "d71ffa0623014df725f8fd2710de4411";
  private static final String DOCX_EXE1_NAME = "GMapTool.exe";
  private static final String DOCX_EXE2_MD5 = "2485435c7c22d35f2de9b4c98c0c2e1a";
  private static final String DOCX_EXE2_NAME = "Setup.exe";
  private static final String XSLX_IMAGE1_NAME = "image1.jpeg";
  private static final String XSLX_IMAGE2_NAME = "image2.jpeg";
  private static final String XSL_IMAGE2_MD5 = "8969288f4245120e7c3870287cce0ff3";
  private static final String COVER_JPG_MD5SUM = "4d236dab6e711735ed11686641b1fba9";
  private static final String COVER_JPG = "cover.jpg";
  private static final String APPLICATION_XML = "application/xml";
  private static final String CONTENT_TYPE = "Content-type";

  public UnpackerResourceTest() throws Exception {
    super("su.msk.jet.tikaserver");
  }

  @Test
  public void testDocWAV() throws Exception {
    InputStream is =
            webResource
                    .path(UNPACKER_PATH)
                    .type(APPLICATION_MSWORD)
                    .put(InputStream.class, ClassLoader.getSystemResourceAsStream(TEST_DOC_WAV));

    ZipInputStream zip = new ZipInputStream(is);

    Map<String, String> data = readZip(zip);

    assertEquals(WAV1_MD5, data.get(WAV1_NAME));
    assertEquals(WAV2_MD5, data.get(WAV2_NAME));
  }

  @Test
  public void testDocPicture() throws Exception {
    InputStream is =
            webResource
                    .path(UNPACKER_PATH)
                    .type(APPLICATION_MSWORD)
                    .put(InputStream.class, ClassLoader.getSystemResourceAsStream(TEST_DOC_WAV));

    ZipInputStream zip = new ZipInputStream(is);

    Map<String, String> data = readZip(zip);

    assertEquals(JPG_MD5, data.get(JPG_NAME));
  }

  @Test
  public void testDocPictureNoOle() throws Exception {
    InputStream is =
            webResource
                    .path(UNPACKER_PATH)
                    .type(APPLICATION_MSWORD)
                    .put(InputStream.class, ClassLoader.getSystemResourceAsStream("2pic.doc"));

    ZipInputStream zip = new ZipInputStream(is);

    Map<String, String> data = readZip(zip);

    assertEquals(JPG2_MD5, data.get(JPG2_NAME));
  }

  @Test
  public void testEmptyDoc() throws Exception {
    ClientResponse cr =
            webResource
                    .path(UNPACKER_PATH)
                    .type(APPLICATION_MSWORD)
                    .put(ClientResponse.class, ClassLoader.getSystemResourceAsStream(TEST_DOC_EMPTY));

    assertEquals(NO_CONTENT, cr.getStatus());
  }

  @Test
  public void testEmptyDoc2() throws Exception {
    ClientResponse cr =
            webResource
                    .path(UNPACKER_PATH)
                    .type(APPLICATION_MSWORD)
                    .put(ClientResponse.class, ClassLoader.getSystemResourceAsStream("Приложение 8.1.doc"));

    assertEquals(NO_CONTENT, cr.getStatus());
  }

  @Test
  public void testImageDOCX() throws Exception {
    InputStream is =
            webResource
                    .path(UNPACKER_PATH)
                    .put(InputStream.class, ClassLoader.getSystemResourceAsStream(TEST_DOCX_IMAGE));

    ZipInputStream zip = new ZipInputStream(is);

    Map<String, String> data = readZip(zip);

    assertEquals(DOCX_IMAGE1_MD5, data.get(DOCX_IMAGE1_NAME));
    assertEquals(DOCX_IMAGE2_MD5, data.get(DOCX_IMAGE2_NAME));
  }

  @Test
  public void testExeDOCX() throws Exception {
    String TEST_DOCX_EXE = "2exe.docx";
    InputStream is =
            webResource
                    .path(UNPACKER_PATH)
                    .put(InputStream.class, ClassLoader.getSystemResourceAsStream(TEST_DOCX_EXE));

    ZipInputStream zip = new ZipInputStream(is);

    Map<String, String> data = readZip(zip);

    assertEquals(DOCX_EXE1_MD5, data.get(DOCX_EXE1_NAME));
    assertEquals(DOCX_EXE2_MD5, data.get(DOCX_EXE2_NAME));
  }

  @Test
  public void testImageXSLX() throws Exception {
    InputStream is =
            webResource
                    .path(UNPACKER_PATH)
                    .put(InputStream.class, ClassLoader.getSystemResourceAsStream("pic.xlsx"));

    ZipInputStream zip = new ZipInputStream(is);

    Map<String, String> data = readZip(zip);

    assertEquals(XSL_IMAGE1_MD5, data.get(XSLX_IMAGE1_NAME));
    assertEquals(XSL_IMAGE2_MD5, data.get(XSLX_IMAGE2_NAME));
  }

  @Test
  public void testImageXSL() throws Exception {
    InputStream is =
            webResource
                    .path(UNPACKER_PATH)
                    .put(InputStream.class, ClassLoader.getSystemResourceAsStream("pic.xls"));

    ZipInputStream zip = new ZipInputStream(is);

    Map<String, String> data = readZip(zip);

    assertEquals(XSL_IMAGE1_MD5, data.get("0.jpg"));
    assertEquals(XSL_IMAGE2_MD5, data.get("1.jpg"));
  }

  @Test
  public void testDocxWithDoc() throws Exception {
    InputStream is =
            webResource
                    .path(UNPACKER_PATH)
                    .put(InputStream.class, ClassLoader.getSystemResourceAsStream("docx_with_doc.docx"));

    ZipInputStream zip = new ZipInputStream(is);

    Map<String, String> data = readZip(zip);
    assertEquals("5476ca38de506efc4fb46bf52f8e93f9", data.get("oleObject2.bin.doc"));
  }

  @Test
  public void testStrangeOLE() throws Exception {
    InputStream is =
            webResource
                    .path(UNPACKER_PATH)
                    .put(InputStream.class, ClassLoader.getSystemResourceAsStream("macro4206_r.docx"));

    ZipInputStream zip = new ZipInputStream(is);

    Map<String, String> data = readZip(zip);
    assertEquals("b5aa80690d8e08b22346430d1d6951d2", data.get("34-ole-[42, 4D, 26, 6D, 00, 00, 00, 00]"));
  }

  @Test
  public void testPptx() throws Exception {
    InputStream is =
            webResource
                    .path(UNPACKER_PATH)
                    .put(InputStream.class, ClassLoader.getSystemResourceAsStream("2ole-vision.pptx"));

    ZipInputStream zip = new ZipInputStream(is);

    Map<String, String> data = readZip(zip);
    assertEquals("0e8d7eb10863c8c21c8d5e994eedac35", data.get("_________Microsoft_Office_Word1.docx"));
    assertEquals("7163d4e24ffb70035ec5252c993ddc81", data.get("image1.emf"));
  }

  @Test
  public void testPpt() throws Exception {
    InputStream is =
            webResource
                    .path(UNPACKER_PATH)
                    .put(InputStream.class, ClassLoader.getSystemResourceAsStream("rosfirm.ppt"));

    ZipInputStream zip = new ZipInputStream(is);

    Map<String, String> data = readZip(zip);
    assertEquals("eadf1989f324247fb3169fa14a87ce6a", data.get("349.xls"));
    assertEquals("f32d5f26c1e0943187992a5414ac657d", data.get("303.xls"));
  }

  @Test
  public void testEmptyPpt() throws Exception {
    ClientResponse cr =
            webResource
                    .path(UNPACKER_PATH)
                    .put(ClientResponse.class, ClassLoader.getSystemResourceAsStream("billig_deny_update.ppt"));

    assertEquals(NO_CONTENT, cr.getStatus());
  }

  @Test
  public void testDocInXLS() throws Exception {
    InputStream is =
            webResource
                    .path(UNPACKER_PATH)
                    .put(InputStream.class, ClassLoader.getSystemResourceAsStream("vtb24_all_tariffs_15122008.xls"));

    ZipInputStream zip = new ZipInputStream(is);

    Map<String, String> data = readZip(zip);
    assertTrue(data.containsKey("MBD001B2651.doc"));
    assertTrue(data.containsKey("MBD001D0B89.doc"));
  }

  @Test
  public void testOutlook() throws Exception {
    InputStream is =
            webResource
                    .path(UNPACKER_PATH)
                    .put(InputStream.class, ClassLoader.getSystemResourceAsStream("test.msg"));

    ZipInputStream zip = new ZipInputStream(is);

    Map<String, String> data = readZip(zip);
    assertTrue(!data.isEmpty());
  }
  
  @Test
  public void testFB2() throws Exception {
    InputStream in = new FileInputStream( (new File(".")).getCanonicalPath() + "/src/test/resources/aksakov_sergei_alenkii_cvetochek.fb2" ),
            is = webResource.path(UNPACKER_PATH).header(CONTENT_TYPE, APPLICATION_XML).put(InputStream.class, in);
    Map < String, String > result = readZip( new ZipInputStream(is) );
    String hashSum = result.get(COVER_JPG);
    assertNotNull(hashSum);
    assertEquals(COVER_JPG_MD5SUM, hashSum );
  }

  private static Map<String, String> readZip(ZipInputStream zip) throws IOException {
    Map<String, String> data = new HashMap<String, String>();

    while (true) {
      ZipEntry entry = zip.getNextEntry();

      if (entry==null) {
        break;
      }

      ByteArrayOutputStream bos = new ByteArrayOutputStream();

      IOUtils.copy(zip, bos);

      data.put(entry.getName(), DigestUtils.md5Hex(bos.toByteArray()));
    }

    return data;
  }
}
