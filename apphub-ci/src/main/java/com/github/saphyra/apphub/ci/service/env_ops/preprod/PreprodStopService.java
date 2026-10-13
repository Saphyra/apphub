package com.github.saphyra.apphub.ci.service.env_ops.preprod;

import com.github.saphyra.apphub.ci.tool.kubernetes.KubernetesPodScaler;
import com.github.saphyra.apphub.ci.value.Constants;
import com.github.saphyra.apphub.ci.value.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PreprodStopService {
    private final KubernetesPodScaler kubernetesPodScaler;

    public void stopServices(boolean stopUserDefinedServices, List<Service> services) {
        if (stopUserDefinedServices) {
            kubernetesPodScaler.scale(services, Constants.NAMESPACE_NAME_PREPROD, 0);
        }else{
            kubernetesPodScaler.scaleAll(Constants.NAMESPACE_NAME_PREPROD, 0);
        }
    }
}
