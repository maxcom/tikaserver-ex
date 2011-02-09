package su.msk.jet.tikaserver;

import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipException;
import java.util.UUID;

public class ZipUtils {
  private ZipUtils() {
  }

  public static void zipStoreBuffer(ZipOutputStream zip, String name, byte[] dataBuffer) throws IOException {
    ZipEntry zipEntry = new ZipEntry(name!=null?name: UUID.randomUUID().toString());
    zipEntry.setMethod(ZipOutputStream.STORED);

    zipEntry.setSize(dataBuffer.length);
    CRC32 crc32 = new CRC32();
    crc32.update(dataBuffer);
    zipEntry.setCrc(crc32.getValue());

    try {
      zip.putNextEntry(zipEntry);
    } catch (ZipException ex) {
      if (name!=null) {
        zipStoreBuffer(zip, null, dataBuffer);
        return;
      }
    }

    zip.write(dataBuffer);

    zip.closeEntry();
  }

  public static String cleanupFilename(String name) {
    if (name.charAt(0)=='/') {
      name = name.substring(1);
    }

    return name;
  }
}
