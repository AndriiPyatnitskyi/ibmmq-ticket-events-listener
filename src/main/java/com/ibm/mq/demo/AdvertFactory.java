package com.ibm.mq.demo;

import java.io.StringReader;
import javax.jms.JMSException;
import javax.jms.Message;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

public class AdvertFactory {
    public static Advert advertFromMessage(Message message) throws JAXBException, JMSException {
        return (Advert) JAXBContext
            .newInstance(Advert.class)
            .createUnmarshaller()
            .unmarshal(new StringReader(message.getBody(String.class)));
    }
}
