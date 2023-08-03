package com.ibm.mq.demo;

import java.util.UUID;
import java.util.logging.Logger;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import jakarta.xml.bind.JAXBException;
import com.fasterxml.jackson.core.JsonProcessingException;


public class TicketRequester {
    private static final Logger logger = Logger.getLogger("com.ibm.mq.demo");

    // session needs to be a static else createTextMessage generates a
    // compilation error
    private static Session session = null;
    private static String PURCHASE_QUEUE = "purchase";
    private static String CONFIRMATION_QUEUE = "confirmation";
    private static String ACCEPTED = "Accepted";

    public TicketRequester(Session session) {
        TicketRequester.session = session;
    }


    public static String put(Message message, int numTickets) throws JsonProcessingException, JMSException, JAXBException {
        String correlationID = UUID.randomUUID().toString();

        logger.finest("Building message to request tickets");
        Advert advert = AdvertFactory.advertFromMessage(message);

        RequestTickets request = RequestTickets.builder()
            .eventID(advert.getEventID())
            .numberRequested(numTickets)
            .build();

        TextMessage requestMessage = session.createTextMessage(request.toJson());
        requestMessage.setJMSCorrelationID(correlationID);
        requestMessage.setJMSExpiration(900_000);

        logger.finest("Sending request to purchase tickets");
        Queue requestQueue = session.createQueue(PURCHASE_QUEUE);
        MessageProducer purchaseQueueProducer = session.createProducer(requestQueue);
        purchaseQueueProducer.send(requestMessage);

        logger.finest("Sent request for tickets");


        return correlationID;
    }


    public boolean get(String correlationID) {
        boolean success = false;
        Message responseMsg;

        try {
            Destination destination = session.createQueue(CONFIRMATION_QUEUE);
            MessageConsumer messageConsumer = session.createConsumer(destination, "JMSCorrelationID='" + correlationID + "'");
            logger.info("Waiting for 30 seconds for a response");
            responseMsg = messageConsumer.receive(30_000);

            if (responseMsg != null) {
                success = isAccepted(responseMsg);
            }
        } catch (JMSException e) {
            logger.warning("Error connecting to confirmation queue");
            e.printStackTrace();
        }
        return success;
    }

    private boolean isAccepted(Message responseMsg) {
        boolean accepted = false;
        try {
            String msgBody = responseMsg.getBody(String.class);
            accepted = msgBody.equals(ACCEPTED);

            logger.info("*************COMPLETED*********");
            logger.info("Received response of....");
            logger.info(msgBody);
        } catch (JMSException e) {
            logger.warning("Error parsing the response from Event Booking System");
            e.printStackTrace();
        }

        return accepted;
    }

}
