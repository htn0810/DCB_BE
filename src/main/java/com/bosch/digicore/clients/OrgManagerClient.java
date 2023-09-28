package com.bosch.digicore.clients;

import com.bosch.digicore.clients.models.OrgDetailRes;
import com.bosch.digicore.clients.models.OrgReq;
import com.bosch.digicore.clients.models.OrgRes;
import com.bosch.digicore.clients.models.TokenRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "OrgManagerClient", url = "${org-manager.url}")
public interface OrgManagerClient {

    @GetMapping("/configuration/settings")
    TokenRes getToken();

    @PostMapping("/search/detail/2281/list")
    List<OrgRes> getOrganizationByOrgName(@RequestHeader("CSRF") String csrfToken, @RequestBody OrgReq orgRequest);

    @GetMapping("/data/views/2274/1/{rawDataId}/withDynamicLists/1")
    List<OrgDetailRes> getOrganizationByRawDataId(@RequestHeader("Cookie") String cookie, @RequestHeader("CSRF") String csrfToken,
                                                  @PathVariable("rawDataId") int rawDataId);
}
