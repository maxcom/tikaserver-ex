package su.msk.jet.tikaserver;

import org.apache.tika.exception.TikaException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class TikaExceptionMapper implements ExceptionMapper<TikaException> {
  @Override
  public Response toResponse(TikaException e) {
    if (e.getCause() !=null && e.getCause() instanceof WebApplicationException) {
      return ((WebApplicationException) e.getCause()).getResponse();
    } else {
      return Response.serverError().build();
    }
  }
}
