package py.edu.ucom.is2.proyecto_camel_HA.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public class ProcesarRechazo implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String idTransaccion = exchange.getIn().getHeader("id_transaccion", String.class);
        String mensaje = exchange.getIn().getHeader("mensaje_rechazo", String.class);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("id_transaccion", idTransaccion);
        respuesta.put("mensaje", mensaje);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRespuesta = objectMapper.writeValueAsString(respuesta);
        exchange.getIn().setBody(jsonRespuesta);
    }
}
