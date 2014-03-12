package storm.immutant.java;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import org.hornetq.jms.client.HornetQConnectionFactory;

// this is here to work on HornetQ < 2.4.0Final
public class HornetQJmsConnectionFactory implements ConnectionFactory, Serializable {

    private HornetQConnectionFactory factory;

    public HornetQJmsConnectionFactory(HornetQConnectionFactory factory) {
        this.factory = factory;
    }

    @Override
    public Connection createConnection() throws JMSException {
        return this.factory.createConnection();
    }

    @Override
    public Connection createConnection(String userName, String password) throws JMSException {
        return this.factory.createConnection(userName, password);
    }
}
