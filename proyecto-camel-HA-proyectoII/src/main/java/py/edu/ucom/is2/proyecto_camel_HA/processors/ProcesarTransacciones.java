package py.edu.ucom.is2.proyecto_camel_HA.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import py.edu.ucom.is2.proyecto_camel_HA.routes.TransferGenerator.Transferencia;

@Component
public class ProcesarTransacciones implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(ProcesarTransacciones.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        Transferencia transferencia = exchange.getIn().getBody(Transferencia.class);

        logger.info("Procesando transferencia con monto: {}", transferencia.getMonto());
        
        //La condicion para el valor del monto
        if (transferencia.getMonto() >= 20000000) {
            String respuesta = String.format("{\"id_transaccion\": \"%s\", \"mensaje\": \"El monto supera máximo permitido\"}", transferencia.getId_transaccion());
            exchange.getMessage().setBody(respuesta);
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
            exchange.setProperty("rechazado", true);
            exchange.setProperty("mensaje_rechazo", "El monto supera máximo permitido");
            logger.info("Transferencia rechazada: {}", respuesta);
        } else {
            exchange.setProperty("rechazado", false);
        }
    }
}
