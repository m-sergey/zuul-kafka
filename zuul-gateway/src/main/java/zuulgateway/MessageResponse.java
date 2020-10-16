package zuulgateway;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MessageResponse implements ClientHttpResponse {

    private final int status = 200;

    private final HttpHeaders headers = new HttpHeaders();

    private final MultiValueMap<String, ResponseCookie> cookies = new LinkedMultiValueMap<>();

    private String body = "";

    private final DataBufferFactory bufferFactory = new DefaultDataBufferFactory();


    public MessageResponse(ConsumerRecord<String, String> message) {
        this.body = message.value();
//        this.headers =
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.valueOf(this.status);
    }

    @Override
    public int getRawStatusCode() {
        return status;
    }

    @Override
    public String getStatusText() throws IOException {
        return HttpStatus.valueOf(this.status).getReasonPhrase();
    }

    @Override
    public void close() {
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.headers;
    }

    @Override
    public String toString() {
        HttpStatus code = HttpStatus.resolve(this.status);
        return (code != null ? code.name() + "(" + this.status + ")" : "Status (" + this.status + ")") + this.headers;
    }

    @Override
    public InputStream getBody() {
        return new ByteArrayInputStream(body.getBytes());
    }
}
