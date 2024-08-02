
package py.edu.ucom.is2.proyecto_camel_HA.routes.mq;

import org.apache.camel.builder.RouteBuilder;

//@Component
public class ActiveMQConsumer extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		from("activemq:alfonzo-ITAU-IN")
		.log("Mensaje recibido ${body}")
		.to("log:LogSistema");
	}
}
