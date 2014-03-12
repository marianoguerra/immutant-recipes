package storm.immutant.java;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import backtype.storm.contrib.jms.JmsTupleProducer;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("serial")
public class EventTupleProducer implements JmsTupleProducer {
    public static ObjectMapper mapper = new ObjectMapper();

    public Values jsonToValue(String json) throws IOException {
        EventDTO proc = mapper.readValue(json, EventDTO.class);

        return new Values(proc.username, proc.channel, proc.msg, proc.timestamp);
    }

    public Values toTuple(Message jmsmsg) throws JMSException {
        if(jmsmsg instanceof TextMessage){
            String json = ((TextMessage) jmsmsg).getText();
            try {
                return jsonToValue(json);
            } catch (IOException ioe) {
                throw new javax.jms.MessageFormatException("Error parsing message body as json: " + ioe);
            }
        } else {
            return null;
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("username", "channel", "msg", "timestamp"));
    }

}

