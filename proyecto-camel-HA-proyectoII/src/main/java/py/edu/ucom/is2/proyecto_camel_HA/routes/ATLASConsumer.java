package py.edu.ucom.is2.proyecto_camel_HA.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;
import py.edu.ucom.is2.proyecto_camel_HA.processors.ProcesarRechazo;
import py.edu.ucom.is2.proyecto_camel_HA.processors.ValidarFecha;
import py.edu.ucom.is2.proyecto_camel_HA.processors.ProcesarTransacciones;
import py.edu.ucom.is2.proyecto_camel_HA.routes.TransferGenerator.Transferencia;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

@Component
public class ATLASConsumer extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Manejo global de errores
        onException(Exception.class)
            .log("Error en procesamiento: ${exception.message}")
            .handled(true); // Marca el error como manejado para evitar que se propague

        // Definición de la ruta
        from("activemq:alfonzo-ITAU-IN")
            .log("Mensaje recibido en Consumer Banco ATLAS: ${body}")
            .unmarshal().json(JsonLibrary.Jackson, Transferencia.class)
            .process(new ValidarFecha())
            .process(new ProcesarTransacciones())
            .choice()
                .when(exchangeProperty("rechazado").isEqualTo(true))
                    .setHeader("mensaje_rechazo", simple("${exchangeProperty.mensaje_rechazo}"))
                    .process(new ProcesarRechazo())
                .otherwise()
                    .process(exchange -> {
                        Transferencia transferencia = exchange.getIn().getBody(Transferencia.class);
                        Map<String, String> respuesta = new HashMap<>();
                        respuesta.put("id_transaccion", transferencia.getId_transaccion());
                        respuesta.put("mensaje", "Transferencia procesada exitosamente desde CONSUMERATLAS");
                        ObjectMapper objectMapper = new ObjectMapper();
                        String jsonRespuesta = objectMapper.writeValueAsString(respuesta);
                        exchange.getIn().setBody(jsonRespuesta);
                    })
                    .log("Transferencia aceptada desde CONSUMERATLAS: ${body}")
            .end();
    }
}

