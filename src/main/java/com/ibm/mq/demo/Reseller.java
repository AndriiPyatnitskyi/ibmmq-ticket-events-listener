package com.ibm.mq.demo;

import java.util.Scanner;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import jakarta.xml.bind.JAXBException;
import com.fasterxml.jackson.core.JsonProcessingException;

public class Reseller {
    private static final Logger logger = Logger.getLogger("com.ibm.mq.demo");

    public static void main(String[] args) throws JsonProcessingException, JMSException, JAXBException {
        LoggerInitializer.initialiseLogging();
        logger.info("Reseller Application is starting");

        Session session = ConnectionManager.createAndGetConnection();

        // Challenge : Subscribes to topic
        TicketSubscriber ticketSubscriber = new TicketSubscriber(session);
        TicketRequester ticketRequester = new TicketRequester(session);

        logger.fine("Entering wait loop for event tickets");
        // Challenge : Receives a publication
        try {
            while (true) {

                Message message = ticketSubscriber.waitForPublish();
                if (message != null) {
                    logger.fine("Tickets have been made available");

                    // Avoids an illegal reflective access operation caused by jaxb dependencies
                    final String key = "org.glassfish.jaxb.runtime.v2.bytecode.ClassTailor.noOptimize";
                    System.setProperty(key, "true");

                    // Challenge : Processes a publication
                    int numToReserve = getNumbersToReserve(message);

                    logger.fine("Sending request to purchase tickets over peer to peer");

                    // Challenge : Receiving a publication triggers a put
                    // then requests to purchase a batch of tickets
                    String correlationID = ticketRequester.put(message, numToReserve);

                    logger.fine("Request has been sent, waiting response from Event Booking System");
                    if (ticketRequester.get(correlationID)) {
                        logger.info("Tickets secured!");
                    } else {
                        logger.info("No tickets reserved!");
                    }
                }
            }
        } finally {
            ConnectionManager.close(session);
            logger.info("Reseller Application is closing");
        }
    }


    private static int getNumbersToReserve(Message message) throws JAXBException, JMSException {
        int ticketsOrderNumber = -1;
        Advert advert = AdvertFactory.advertFromMessage(message);
        System.out.printf("There are %d tickets available for %s \n", advert.getCapacity(), advert.getTitle());

        Scanner in = new Scanner(System.in);
        while (-1 == ticketsOrderNumber) {
            System.out.println("How many do you want to reserve? :");
            if (in.hasNextInt())
                ticketsOrderNumber = in.nextInt();
            else {
                System.out.println("I am expecting a quantity expressed in digits from you?");
                in.next();
            }
        }

        return ticketsOrderNumber;
    }
}
