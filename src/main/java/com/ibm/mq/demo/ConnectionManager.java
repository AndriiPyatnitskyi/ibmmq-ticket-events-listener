package com.ibm.mq.demo;

import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;


public class ConnectionManager {
    private static final Logger logger = Logger.getLogger("com.ibm.mq.demo");

    private static final boolean TRANSACTED = false;

    public static Session createAndGetConnection() throws JMSException {
        JmsFactoryFactory jmsFactoryFactory = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
        JmsConnectionFactory connectionFactory = jmsFactoryFactory.createConnectionFactory();

        connectionFactory.setStringProperty(WMQConstants.WMQ_HOST_NAME, MQProperties.HOST);
        connectionFactory.setIntProperty(WMQConstants.WMQ_PORT, MQProperties.PORT);
        connectionFactory.setStringProperty(WMQConstants.WMQ_CHANNEL, MQProperties.CHANNEL);
        connectionFactory.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
        connectionFactory.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
        connectionFactory.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, MQProperties.QUEUE_MANAGER);

        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(TRANSACTED, Session.AUTO_ACKNOWLEDGE);

        return session;
    }

    public static void close(Session session) {
        try {
            session.close();
            logger.finest("JMS Session closed successfully");
        } catch (JMSException jmsex) {
            logger.severe("Unable to close JMS Session");
            jmsex.printStackTrace();
        }
    }
}
