package py.edu.ucom.is2.proyecto_camel_HA.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class TransferGenerator extends RouteBuilder {

    private final Random random = new Random();

    @Override
    public void configure() throws Exception {
        fromF("timer:transferTimer?period=2000")
                .process(exchange -> {
                    // Generar datos aleatorios
                    String cuenta = String.valueOf(1000 + random.nextInt(4001)); // número de cuenta entre 1000 y 5000
                    int monto = 1000 + random.nextInt(4001); // monto entre 1000 y 5000
                    String bancoOrigen = getRandomBanco();
                    String bancoDestino = getRandomBanco();
                  

                    // Crear objeto JSON
                    Transferencia transferencia = new Transferencia(cuenta, monto, bancoOrigen, bancoDestino);
                    ObjectMapper objectMapper = new ObjectMapper();
                    String json = objectMapper.writeValueAsString(transferencia);

                    // Agregar el JSON al cuerpo del mensaje
                    exchange.getMessage().setBody(json);
                })
                .to("activemq:alfonzo-ITAU-IN"); // Enviar mensaje a la cola ActiveMQ
        		
    }

    private String getRandomBanco() {
        String[] bancos = {"ITAU", "ATLAS", "FAMILIAR"};
        return bancos[random.nextInt(bancos.length)];
    }

    // Clase modelo para la transferencia
    private static class Transferencia {
        private String cuenta;
        private int monto;
        private String banco_origen;
        private String banco_destino;

        public Transferencia(String cuenta, int monto, String banco_origen, String banco_destino) {
            this.cuenta = cuenta;
            this.monto = monto;
            this.banco_origen = banco_origen;
            this.banco_destino = banco_destino;
        }

        // Getters y setters
        // Para Jackson (ObjectMapper) es importante tener getters para serialización JSON
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
    }
}
