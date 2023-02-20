package okestro.servicebroker.model.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PAAS_SVC_INSTANCE", catalog="ECP_USER")
public class PaasServiceInstance {
    @Id
    @Column(name = "INSTN_ID")
    private String instanceId;

    @Column(name = "SVC_ID")
    private String serviceId;

    @Column(name = "PARAMTR")
    private String parameters;

    @Column(name = "STT")
    private String state;

    @Column(name = "DIR_PATH")
    private String directoryPath;

    @Column(name = "PROJECT_ID")
    private String projectId;

}
