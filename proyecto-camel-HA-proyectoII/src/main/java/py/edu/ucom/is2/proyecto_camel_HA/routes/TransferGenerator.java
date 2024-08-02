
package py.edu.ucom.is2.proyecto_camel_HA.routes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import py.edu.ucom.is2.proyecto_camel_HA.processors.ValidarFecha;
import py.edu.ucom.is2.proyecto_camel_HA.processors.ProcesarTransacciones;

@Component
public class TransferGenerator extends RouteBuilder {

    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void configure() throws Exception {
        fromF("timer:transferTimer?period=2000")
            .process(exchange -> {
                // Aqui generamos datos aleatorios
                String cuenta = String.valueOf(1000 + random.nextInt(4001)); // Numero de cuenta entre 1000 y 5000
              //  int monto = 30000000; // Profe aqui se puede probar el caso de monto alto
                int monto = 1000 + random.nextInt(4001); // Rango de monto entre 1000 y 5000
                String bancoOrigen = getRandomBanco();
                String bancoDestino = getRandomBanco();

                // Obtener la fecha actual
                LocalDate fechaActual = LocalDate.now();
                String fecha = fechaActual.format(dateFormatter); // Formateo de Fecha
              

                String idTransaccion = UUID.randomUUID().toString(); // ID unico utilizando una libreria

                // Creamos el objeto Transferencia
                Transferencia transferencia = new Transferencia(cuenta, monto, bancoOrigen, bancoDestino, fecha, idTransaccion);

                // Convertimos el objeto Transferencia a JSON
                String json = objectMapper.writeValueAsString(transferencia);

                // Agregamos el JSON al cuerpo del mensaje
                exchange.getMessage().setBody(json);
            })
            .to("activemq:alfonzo-ITAU-IN"); // Aqui enviamos el mensaje a mi cola ActiveMQ

     
        from("activemq:alfonzo-ITAU-IN")
            .unmarshal().json(JsonLibrary.Jackson, Transferencia.class) // Convertirmos el JSON a Transferencia
            .process(new ValidarFecha()) // Validamos la fecha
            .process(new ProcesarTransacciones()) // Validamos el monto y otras reglas
            .to("activemq:resultado-transferencia"); // Enviamos el resultado a otra cola para procesar el resultado
    }

    private String getRandomBanco() {
        String[] bancos = {"ITAU", "ATLAS", "FAMILIAR"};
        return bancos[random.nextInt(bancos.length)];
    }

 
    public static class Transferencia {
        private String cuenta;
        private int monto;
        private String banco_origen;
        private String banco_destino;
        private String fecha;
        private String id_transaccion;

   
        public Transferencia() {
        }

        public Transferencia(String cuenta, int monto, String banco_origen, String banco_destino, String fecha, String id_transaccion) {
            this.cuenta = cuenta;
            this.monto = monto;
            this.banco_origen = banco_origen;
            this.banco_destino = banco_destino;
            this.fecha = fecha;
            this.id_transaccion = id_transaccion;
        }

        // Getters y setters
        public String getCuenta() {
            return cuenta;
        }

        public void setCuenta(String cuenta) {
            this.cuenta = cuenta;
        }

        public int getMonto() {
            return monto;
        }

        public void setMonto(int monto) {
            this.monto = monto;
        }

        public String getBanco_origen() {
            return banco_origen;
        }

        public void setBanco_origen(String banco_origen) {
            this.banco_origen = banco_origen;
        }

        public String getBanco_destino() {
            return banco_destino;
        }

        public void setBanco_destino(String banco_destino) {
            this.banco_destino = banco_destino;
        }

        public String getFecha() {
            return fecha;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }

        public String getId_transaccion() {
            return id_transaccion;
        }

        public void setId_transaccion(String id_transaccion) {
            this.id_transaccion = id_transaccion;
        }
    }
}
