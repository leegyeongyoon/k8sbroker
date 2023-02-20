package okestro.servicebroker.service;

import com.jcraft.jsch.JSchException;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceRequest;

import java.io.IOException;

public interface PaasService {

    void createInstance(CreateServiceInstanceRequest request) throws JSchException, IOException;
    void deleteInstance(DeleteServiceInstanceRequest request) throws Exception;
//    Object connectTest() throws JSchException;

}
