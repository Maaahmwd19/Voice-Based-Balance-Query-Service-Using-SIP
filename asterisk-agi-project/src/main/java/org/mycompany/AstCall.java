package org.mycompany;

import org.asteriskjava.fastagi.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

import java.sql.*;
import java.util.Date;

public class AstCall extends BaseAgiScript {

    @Override
    public void service(AgiRequest request, AgiChannel channel) throws AgiException {
        Date callStart = new Date();
        String number = "";
        String lang = "";
        double balanceValue = 0.0;

        try {
            answer();
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target("http://localhost:8080/service/api/balance");

            int tries = 1;
            channel.exec("WAIT", "2");
            channel.streamFile("custom/welcome-for-You-in-ITI");

            lang = channel.getData("beep", 3000, 1);

            if ("1".equals(lang)) {
                while (true || tries > 2) {
                    channel.streamFile("custom/Enter-your-phone-num");
                    number = channel.getData("beep", 5000, 11);
                    if ("".equals(number) || number.length() < 11) {
                        channel.streamFile("custom/invalid-phone-number");
                        channel.streamFile("please-try-again");
                        tries++;
                    } else {
                        channel.exec("WAIT", "2");
                        channel.streamFile("custom/you-entered");
                        channel.sayDigits(number);
                        break;
                    }
                }
            } else if ("2".equals(lang)) {
                channel.streamFile("ar-sounds/welcom-ar");
                while (true || tries > 2) {
                    channel.streamFile("ar-sounds/please-enter-your-num-ar");
                    number = channel.getData("beep", 5000, 11);
                    if ("".equals(number) || number.length() < 11) {
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
                balanceValue = responseBody.getValue();
                if ("1".equals(lang)) {
                    channel.streamFile("custom/your-balance-is");
                    channel.sayNumber(Double.toString(balanceValue));
                    channel.streamFile("custom/Egyptian-pound");
                    channel.exec("WAIT", "1");
                    channel.streamFile("custom/thanks-us-en");
                } else if ("2".equals(lang)) {
                    channel.streamFile("ar-sounds/Current-balance-ar");
                    channel.sayNumber(Double.toString(balanceValue));
                    channel.streamFile("ar-sounds/Egyptian-pound-ar");
                    channel.exec("WAIT", "1");
                    channel.streamFile("ar-sounds/thanxs-use-ar");
                }
            } else {
                if ("1".equals(lang)) {
                    channel.streamFile("custom/invalid-phone-number");
                } else if ("2".equals(lang)) {
                    channel.streamFile("ar-sounds/inavaled-num-ar");
                }
            }

        } catch (Exception e) {
            System.err.println("AGI Error: " + e.getMessage());
        } finally {
            Date callEnd = new Date();
            saveCallLogToPostgres(number, lang, balanceValue, callStart, callEnd);
            hangup();
        }
    }

    private void saveCallLogToPostgres(String msisdn, String lang, double balance, Date startTime, Date endTime) {
        String url = "jdbc:postgresql://localhost:5432/postgres"; 
        String user = "postgres";    
        String password = "19012001"; 

        String insertSQL = "INSERT INTO call_logs (msisdn, language, balance, start_time, end_time) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, msisdn);
            pstmt.setString(2, lang);
            pstmt.setDouble(3, balance);
            pstmt.setTimestamp(4, new Timestamp(startTime.getTime()));
            pstmt.setTimestamp(5, new Timestamp(endTime.getTime()));

            pstmt.executeUpdate();
            System.out.println("✅ Call log inserted into PostgreSQL.");
        } catch (SQLException e) {
            System.err.println("❌ PostgreSQL Error: " + e.getMessage());
        }
    }
}
