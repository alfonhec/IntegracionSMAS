package py.edu.ucom.is2.proyecto_camel_HA.processors;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import py.edu.ucom.is2.proyecto_camel_HA.routes.TransferGenerator.Transferencia;

@Component
public class ValidarFecha implements Processor {

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Logger logger = LoggerFactory.getLogger(ValidarFecha.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        Transferencia transferencia = exchange.getIn().getBody(Transferencia.class);

        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaTransferencia = LocalDate.parse(transferencia.getFecha(), dateFormatter);

        logger.info("Validando fecha de la transferencia: {}", transferencia.getFecha());

        if (!fechaTransferencia.equals(fechaActual)) {
            String respuesta = String.format("{\"id_transaccion\": \"%s\", \"mensaje\": \"Mensaje caducado\"}", transferencia.getId_transaccion());
            exchange.getMessage().setBody(respuesta);
            exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
            exchange.setProperty("rechazado", true);
            exchange.setProperty("mensaje_rechazo", "Mensaje caducado");
            logger.info("Transferencia rechazada: {}", respuesta);
        } else {
            exchange.setProperty("rechazado", false);
        }
    }
}

