package org.mycompany;

import org.asteriskjava.fastagi.*;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import org.mycompany.Balance;

import ch.qos.logback.core.pattern.parser.Parser;

public class AstCall extends BaseAgiScript {

    @Override
    public void service(AgiRequest request, AgiChannel channel) throws AgiException {
        try {
            answer();
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target("http://localhost:8080/service/api/balance");

            // Play a prompt and wait for user input
            channel.streamFile("custom/enter-your-number");
            String number = channel.getData("beep", 7000, 11); // Timeout: 7s, Max 11 digits
            channel.streamFile("custom/you-entered");
            if(number == ""){
                channel.streamFile("custom/invalid-number");
                channel.hangup();
                return;
            }
            channel.sayDigits(number);//say your number digit by digit
            WebTarget finalTarget = target.queryParam("msisdn", number);
            Response response = finalTarget.request(MediaType.APPLICATION_JSON).get();
            Balance responseBody = response.readEntity(Balance.class);
            if (response.getStatus() == 200 && responseBody != null) {
                channel.streamFile("custom/your-balance-is");
                System.out.println(responseBody);
                channel.sayNumber(Double.toString(responseBody.getValue()));//say the complete number
            } else {
                System.out.println("Response body is null or status not 200");
                channel.streamFile("custom/you-entered");
                channel.streamFile("custom/invalid-number"); 
            }
            
            channel.hangup();
        } catch (AgiException e) {
            System.err.println("AGI Error: " + e.getMessage());
        } finally {
            hangup();
        }
    }
    
}
