package su.msk.jet.tikaserver;

import java.io.IOException;
import java.util.zip.ZipOutputStream;

public interface PartExtractor<T> {
  void extract(T part, ZipOutputStream output) throws IOException;
}