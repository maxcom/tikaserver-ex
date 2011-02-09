package su.msk.jet.tikaserver;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypeException;
import org.xml.sax.helpers.DefaultHandler;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Path("/meta")
public class MetadataResource {
  private static final String CONTENT_LENGTH = "Content-Length";
  private static final String FILE_NNAME = "File-Name";
  private static final String RESOURCE_NAME = "resourceName";

  @PUT
  @Produces("text/csv")
  public StreamingOutput getMetadata( InputStream is, @Context HttpHeaders httpHeaders ) throws Exception {
    final Detector detector = new HeaderTrustingDetectorFactory ().createDetector( httpHeaders );
    final AutoDetectParser parser = new AutoDetectParser(detector);
    final ParseContext context = new ParseContext();
    context.set(Parser.class, parser);
    final Metadata metadata = new Metadata();
    parser.parse( is, new DefaultHandler(), metadata, context );
    fillMetadata ( httpHeaders, metadata );

    return new StreamingOutput() {
      @Override
      public void write(OutputStream outputStream) throws IOException, WebApplicationException {
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream));
        for (String name : metadata.names()) {
          String[] values = metadata.getValues(name);
          ArrayList<String> list = new ArrayList<String>(values.length+1);
          list.add(name);
          list.addAll(Arrays.asList(values));
          writer.writeNext(list.toArray(values));
        }
        writer.close();
      }
    };
  }

  private void fillMetadata ( HttpHeaders httpHeaders, Metadata metadata ) {
    final List < String > fileName = httpHeaders.getRequestHeader(FILE_NNAME), cl = httpHeaders.getRequestHeader(CONTENT_LENGTH);
    if ( cl != null && !cl.isEmpty() )
      metadata.set( CONTENT_LENGTH, cl.get(0) );

    if ( fileName != null && !fileName.isEmpty() )
      metadata.set( RESOURCE_NAME, fileName.get(0) );
  }

  private static class HeaderTrustingDetectorFactory {
    public Detector createDetector( HttpHeaders httpHeaders ) throws IOException, MimeTypeException {
      final javax.ws.rs.core.MediaType mediaType = httpHeaders.getMediaType();
      if (mediaType == null || mediaType.equals(javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM_TYPE ))
        return (new TikaConfig()).getMimeRepository();
      else return new Detector() {
        @Override
        public MediaType detect(InputStream inputStream, Metadata metadata) throws IOException {
          return MediaType.parse( mediaType.toString() );
        }
      };
    }
  }
}
