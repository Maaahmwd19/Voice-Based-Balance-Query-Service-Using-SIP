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
            String number = "";
            String lang = "";
            int triers = 1;

            channel.exec("WAIT", "2");
            channel.streamFile("custom/welcome-for-You-in-ITI");
//            channel.exec("WAIT", "1");
            lang = channel.getData("beep", 3000, 1); // Timeout: 3s, Max 1 digits

            if ("1".equals(lang)) {
                // Play a prompt and wait for user input
                while (true || triers > 2) {
                    channel.streamFile("custom/Enter-your-phone-num");
                    number = channel.getData("beep", 5000, 11); // Timeout: 7s, Max 11 digits
                    if ("".equals(number) || 11 > number.length()) {
                        channel.streamFile("custom/invalid-phone-number");
                        channel.streamFile("please-try-again");
                        if (triers == 2) {
                                
                        }
                        triers++;
                    } else {
                        channel.exec("WAIT", "2");
                        channel.streamFile("custom/you-entered");
                        channel.sayDigits(number);
                        break;
                    }
                }
            } else if ("2".equals(lang)) {
                channel.streamFile("ar-sounds/welcom-ar");
                while (true || triers > 2) {
                    channel.streamFile("ar-sounds/please-enter-your-num-ar");
                    number = channel.getData("beep", 5000, 11); // Timeout: 5s, Max 11 digits
                    if ("".equals(number) || 11 > number.length()) {

                        channel.streamFile("ar-sounds/inavaled-num-ar");
                        channel.streamFile("ar-sounds/please-try-again-ar");
                    } else {
                        channel.exec("WAIT", "2");
                        channel.streamFile("ar-sounds/you-enterd-ar");
                        for (char digit : number.toCharArray()) {
                            if (Character.isDigit(digit)) {
                                String path = "custom_num_ar/" + digit + "-ar";
                                channel.streamFile(path);
                            }
                        }
                        break;
                    }
                }
            } else {
                channel.streamFile("custom/you-entered");
                channel.streamFile("custom/invalid-phone-number");
                hangup();
            }

            WebTarget finalTarget = target.queryParam("msisdn", number);
            Response response = finalTarget.request(MediaType.APPLICATION_JSON).get();
            Balance responseBody = response.readEntity(Balance.class);
            if (response.getStatus() == 200 && responseBody != null) {
                if ("1".equals(lang)) {
                    channel.streamFile("custom/your-balance-is");
                    System.out.println(responseBody);
                    channel.sayNumber(Double.toString(responseBody.getValue()));//say the complete number
                    channel.streamFile("custom/Egyptian-pound");
                    channel.exec("WAIT", "1");
                    channel.streamFile("custom/thanks-us-en");

                } else if ("2".equals(lang)) {
                    channel.streamFile("ar-sounds/Current-balance-ar");
                    System.out.println(responseBody);
                    channel.sayNumber(Double.toString(responseBody.getValue()));//say the complete number
                    channel.streamFile("ar-sounds/Egyptian-pound-ar");
                    channel.exec("WAIT", "1");
                    channel.streamFile("ar-sounds/thanxs-use-ar");

                }
            } else {
                System.out.println("Response body is null or status not 200");
                if ("1".equals(lang)) {
                    channel.streamFile("custom/you-entered");
                    channel.streamFile("custom/invalid-phone-number");
                } else if ("2".equals(lang)) {

                    channel.streamFile("ar-sounds/inavaled-num-ar");
                }
            }

            channel.hangup();
        } catch (AgiException e) {
            System.err.println("AGI Error: " + e.getMessage());
        } finally {
            hangup();
        }
    }

}
