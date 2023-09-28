package com.bosch.digicore.clients;

import com.bosch.digicore.clients.models.CoreSearchReq;
import com.bosch.digicore.clients.models.CoreSearchRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "CoreSearchEngineClient", url = "${core-search.url}")
public interface CoreSearchEngineClient {

    @PostMapping("/public/search")
    CoreSearchRes getDataSearchEngine(@RequestBody CoreSearchReq coreSearchReq);
}
