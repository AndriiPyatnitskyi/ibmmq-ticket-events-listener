package com.ibm.mq.demo;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

public class TicketSubscriber {
    private final MessageConsumer newTicketsTopicSubscriber;
    private static final String DESTINATION_NAME = "newTickets";


    public TicketSubscriber(Session session) throws JMSException {
        Destination topic = session.createTopic(DESTINATION_NAME);
        newTicketsTopicSubscriber = session.createConsumer(topic);
    }

    public Message waitForPublish() throws JMSException {
        Message message = newTicketsTopicSubscriber.receive();

        if (message != null) {
            System.out.println("************************************");
            System.out.println("Received Event Opportunity");
            System.out.println(message.getBody(String.class));
            System.out.println();
        }

        return message;
    }
}
