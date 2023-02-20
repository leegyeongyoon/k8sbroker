package okestro.servicebroker.repo;

import okestro.servicebroker.model.entity.PaasServiceInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaasServiceInstanceRepo extends JpaRepository<PaasServiceInstance, String> {
    PaasServiceInstance findByInstanceId(String instance_id);
}
