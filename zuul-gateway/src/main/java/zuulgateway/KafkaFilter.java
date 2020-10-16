package zuulgateway;

import com.netflix.client.ClientException;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.*;

public class KafkaFilter extends ZuulFilter {

    /** Filter Order for */
    public static final int KAFKA_ROUTING_FILTER_ORDER = 1;

    private static final Log log = LogFactory.getLog(KafkaFilter.class);

    protected ProxyRequestHelper helper;

    @Autowired
    ReplyingKafkaTemplate<String, String, String> kafkaTemplate;

//    @Value("${kafka.topic.request-topic}")
    String requestTopic = "in-topic";

//    @Value("${kafka.topic.requestreply-topic}")
    String requestReplyTopic = "out-topic";

    public KafkaFilter(ProxyRequestHelper helper) {
        this.helper = helper;
    }

    @Override
    public String filterType() {
        return ROUTE_TYPE;
    }

    @Override
    public int filterOrder() {
        return KAFKA_ROUTING_FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        String url = (String) ctx.get(SERVICE_ID_KEY);
        return (url != null && url.startsWith("kafka:") && ctx.sendZuulResponse());
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        this.helper.addIgnoredHeaders();
        try {
            HttpServletRequest request = context.getRequest();
            // create producer record
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(requestTopic, getRequestBody(request));
            // set reply topic in header
            record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, requestReplyTopic.getBytes()));
            // post in kafka topic
            RequestReplyFuture<String, String, String> sendAndReceive = kafkaTemplate.sendAndReceive(record, Duration.ofSeconds(120));

            // confirm if producer produced successfully
            SendResult<String, String> sendResult = sendAndReceive.getSendFuture().get();

            // get consumer record
            ConsumerRecord<String, String> consumerRecord = sendAndReceive.get();

            ClientHttpResponse response = new MessageResponse(consumerRecord);

            setResponse(response);

            return response;
        } catch (Exception ex) {
            throw new ZuulRuntimeException(ex);
        }
    }

    protected void setResponse(ClientHttpResponse resp)
            throws ClientException, IOException {
        // Stop chain of filters
        RequestContext.getCurrentContext().set("sendZuulResponse", false);
        // Add response to request context
        RequestContext.getCurrentContext().set("zuulResponse", resp);
        this.helper.setResponse(resp.getRawStatusCode(), resp.getBody(),
                resp.getHeaders());
    }

    protected String getRequestBody(HttpServletRequest request) {

        StringBuilder builder = new StringBuilder();

        try {
            InputStream requestEntity = (InputStream) RequestContext.getCurrentContext()
                    .get(REQUEST_ENTITY_KEY);
            if (requestEntity == null) {
                requestEntity = request.getInputStream();
            }

            try (Reader reader = new BufferedReader(new InputStreamReader
                    (requestEntity, Charset.forName(StandardCharsets.UTF_8.name())))) {
                int c = 0;
                while ((c = reader.read()) != -1) {
                    builder.append((char) c);
                }
            }

        }
        catch (IOException ex) {
            log.error("error during getRequestBody", ex);
        }

        return builder.toString();
    }
}
