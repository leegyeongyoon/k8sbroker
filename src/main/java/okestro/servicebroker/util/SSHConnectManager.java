package okestro.servicebroker.util;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

@Slf4j
@Component
public class SSHConnectManager {
    @Value("${k8s.url}")
    private String k8sUrl;
    @Value("${k8s.username}")
    private String k8sUsername;
    @Value("${k8s.password}")
    private String k8sPassword;


    public Session connect() throws JSchException {
        Session session = null;
        log.debug("### Connecting to " + k8sUrl);
        JSch jSch = new JSch();
        session = jSch.getSession(k8sUsername, k8sUrl, 22);

        session.setPassword(k8sPassword);

        Properties properties = new Properties();
        properties.put("StrictHostKeyChecking", "no");
        session.setConfig(properties);
        try {
            session.connect();
        } catch (JSchException e) {
            log.error("Session Connect Error : \n{}", e.getMessage());
        }
        return session;
    }

    public void runCmd(Session session, String command) throws JSchException, IOException {
        ChannelExec channel = null;
        String result = "";

        channel = (ChannelExec)session.openChannel("exec");
        channel.setCommand(command);
        channel.setPty(true);

        InputStream in = channel.getInputStream();

        channel.connect();

        result = getResult(channel, in);

        if (result.toLowerCase(Locale.ROOT).contains("error") || result.toLowerCase(Locale.ROOT).contains("fail")) {
            channel.disconnect();
            throw new JSchException(result);
        }

        channel.disconnect();
        log.info("### {} Results : \n{}",command , result);
    }


    public void disConnect(Session session) {
        if (session.isConnected()) {
            session.disconnect();
        }
    }

    public String getResult(Channel channel, InputStream in) throws IOException {

        byte[] buffer = new byte[1024];
        StringBuilder strBuilder = new StringBuilder();

        while (true){
            while (in.available() > 0) {
                int i = in.read(buffer, 0, 1024);
                if (i < 0) {
                    break;
                }
                strBuilder.append(new String(buffer, 0, i));
            }

            if (channel.isClosed()){
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e){}
        }

        return strBuilder.toString();
    }

}
