package py.edu.ucom.is2.proyecto_camel_HA.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ITAUConsumer extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("activemq:alfonzo-ITAU-IN")
                .log("Mensaje recibido en Banco ITAU: ${body}");
                // Aquí puedes agregar el procesamiento adicional
        }
}