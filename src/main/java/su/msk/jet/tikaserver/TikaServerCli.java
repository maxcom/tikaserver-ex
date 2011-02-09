package su.msk.jet.tikaserver;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class TikaServerCli {
  private static final Log logger = LogFactory.getLog(TikaServerCli.class);

  public static final int DEFAULT_PORT = 9998;

  private static Options getOptions() {
    Options options = new Options();
    options.addOption("p", "port", true, "listen port (default = "+DEFAULT_PORT+ ')');

    options.addOption("h", "help", false, "this help message");

    return options;
  }

  public static void main(String[] args) {
    try {
      TikaServerCli cli = new TikaServerCli();

      Map<String, String> params = new HashMap<String, String>();

      params.put("com.sun.jersey.config.property.packages", "su.msk.jet.tikaserver");

      Options options = cli.getOptions();

      CommandLineParser cliParser = new GnuParser();
      CommandLine line = cliParser.parse(options, args);

      int port = DEFAULT_PORT;

      if (line.hasOption("port")) {
        port = Integer.valueOf(line.getOptionValue("port"));
      }

      if (line.hasOption("help")) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("tikaserver", options);
        System.exit(-1);
      }

      String baseUri = "http://localhost/";
      URI buildUri = UriBuilder.fromUri(baseUri).port(port).build();
      SelectorThread threadSelector = GrizzlyWebContainerFactory.create(buildUri, params);

      logger.info("Started at " + buildUri);
    } catch (Exception ex) {
      logger.fatal("Can't start", ex);
      System.exit(-1);
    }
  }
}
