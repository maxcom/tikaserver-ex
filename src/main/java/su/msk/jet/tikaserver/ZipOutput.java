package su.msk.jet.tikaserver;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipOutputStream;

public class ZipOutput implements StreamingOutput {
  private final Map<PartExtractor, Collection> parts = new HashMap<PartExtractor, Collection>();

  public <T> void put(PartExtractor<T> extractor, Collection<T> parts) {
    if (parts.isEmpty()) {
      return;
    }

    this.parts.put(extractor, parts);
  }

  @Override
  public void write(OutputStream outputStream) throws IOException, WebApplicationException {
    ZipOutputStream zip = new ZipOutputStream(outputStream);

    zip.setMethod(ZipOutputStream.STORED);

    addParts(zip);

    zip.close();
  }

  private void addParts(ZipOutputStream zip) throws IOException {
    for (Map.Entry<PartExtractor, Collection> entry : parts.entrySet()) {
      for (Object part : entry.getValue()) {
        entry.getKey().extract(part, zip);
      }
    }
  }

  public boolean isEmpty() {
    return parts.isEmpty();
  }
}
