package storm.immutant.java;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.jms.client.HornetQDestination;
import org.hornetq.api.core.TransportConfiguration;

import backtype.storm.contrib.jms.JmsProvider;

@SuppressWarnings("serial")
public class HornetQJmsProvider implements JmsProvider {
    private ConnectionFactory connectionFactory;
    private Destination destination;

    public HornetQJmsProvider(String destination, HornetQJmsConnectionType type){
        this(5446, destination, type);
    }

    public HornetQJmsProvider(int port, String destination,
            HornetQJmsConnectionType type){
        java.util.Map<String, Object> connectionParams = new java.util.HashMap<String, Object>();

        connectionParams.put(org.hornetq.core.remoting.impl.netty.TransportConstants.PORT_PROP_NAME, port);

        TransportConfiguration transportConfiguration =
            new TransportConfiguration(
                    "org.hornetq.core.remoting.impl.netty.NettyConnectorFactory",
                    connectionParams);

        this.connectionFactory = new HornetQJmsConnectionFactory(
                HornetQJMSClient.createConnectionFactoryWithoutHA(
                    JMSFactoryType.CF, transportConfiguration));

        if (type == HornetQJmsConnectionType.Topic) {
            this.destination = HornetQDestination.fromAddress(
                    HornetQDestination.JMS_TOPIC_ADDRESS_PREFIX + destination);
        } else {
            this.destination = HornetQDestination.fromAddress(
                    HornetQDestination.JMS_QUEUE_ADDRESS_PREFIX + destination);
        }
    }

    public ConnectionFactory connectionFactory() throws Exception {
        return this.connectionFactory;
    }

    public Destination destination() throws Exception {
        return this.destination;
    }

}

