package su.msk.jet.tikaserver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.hwpf.OldWordFileFormatException;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

@Path("/tika")
public class TikaResource {
  public static final String GREETING = "This is Tika Server. Please PUT\n";
  private final Log logger = LogFactory.getLog(TikaResource.class);

  static {
    ExtractorFactory.setAllThreadsPreferEventExtractors(true);
  }

  @SuppressWarnings({"SameReturnValue"})
  @GET
  @Produces("text/plain")
  public String getMessage() {
    return GREETING;
  }

  public static AutoDetectParser createParser() {
    final AutoDetectParser parser = new AutoDetectParser();

    parser.setFallback(new Parser() {
      public Set<MediaType> getSupportedTypes(ParseContext parseContext) {
        return parser.getSupportedTypes(parseContext);
      }

      @Override
      public void parse(InputStream inputStream, ContentHandler contentHandler, Metadata metadata, ParseContext parseContext) {
        throw new WebApplicationException(Response.Status.UNSUPPORTED_MEDIA_TYPE);
      }

      @Override
      public void parse(InputStream inputStream, ContentHandler contentHandler, Metadata metadata) {
        throw new WebApplicationException(Response.Status.UNSUPPORTED_MEDIA_TYPE);
      }
    });

    return parser;
  }

  public static void fillMetadata(AutoDetectParser parser, Metadata metadata, HttpHeaders httpHeaders) {
    javax.ws.rs.core.MediaType mediaType = httpHeaders.getMediaType();

    if (mediaType !=null && !mediaType.equals(javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM_TYPE)) {
      metadata.add(org.apache.tika.metadata.HttpHeaders.CONTENT_TYPE, mediaType.toString());

      final Detector detector = parser.getDetector();

      parser.setDetector(new Detector() {
        @Override
        public MediaType detect(InputStream inputStream, Metadata metadata) throws IOException {
          String ct = metadata.get(org.apache.tika.metadata.HttpHeaders.CONTENT_TYPE);

          if (ct!=null) {
            return MediaType.parse(ct);
          } else {
            return detector.detect(inputStream, metadata);
          }
        }
      });
    }
  }

  @PUT
  @Consumes("*/*")
  @Produces("text/plain")
  public StreamingOutput getText(final InputStream is, @Context HttpHeaders httpHeaders) {
    final AutoDetectParser parser = createParser();
    final Metadata metadata = new Metadata();

    fillMetadata(parser, metadata, httpHeaders);

    return new StreamingOutput() {
      @Override
      public void write(OutputStream outputStream) throws IOException, WebApplicationException {
        BodyContentHandler body = new BodyContentHandler(outputStream);

        try {
          parser.parse(is, body, metadata);
        } catch (SAXException e) {
          throw new WebApplicationException(e);
        } catch (TikaException e) {
          if (e.getCause()!=null && e.getCause() instanceof WebApplicationException) {
            throw (WebApplicationException) e.getCause();
          }

          if (e.getCause()!=null && e.getCause() instanceof IllegalStateException) {
            throw new WebApplicationException(Response.status(422).build());
          }

          if (e.getCause()!=null && e.getCause() instanceof EncryptedDocumentException) {
            throw new WebApplicationException(Response.status(422).build());
          }

          if (e.getCause()!=null && e.getCause() instanceof OldWordFileFormatException) {
            throw new WebApplicationException(Response.status(422).build());
          }

          logger.warn("Text extraction failed", e);

          throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
      }
    };
  }
}
