package com.bosch.digicore.services;

import com.bosch.digicore.clients.CoreSearchEngineClient;
import com.bosch.digicore.clients.OrgManagerClient;
import com.bosch.digicore.clients.models.CoreSearchReq;
import com.bosch.digicore.clients.models.CoreSearchRes;
import com.bosch.digicore.clients.models.OrgReq;
import com.bosch.digicore.constants.LdapAttribute;
import com.bosch.digicore.dtos.LdapUserDTO;
import com.bosch.digicore.dtos.OrgManagerDTO;
import com.bosch.digicore.exceptions.RequestTimeoutException;
import com.bosch.digicore.utils.LdapUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.TimeLimitExceededException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalService {
    private final LdapTemplate ldapTemplate;
    private final LdapUtil ldapUtil;
    private final OrgManagerClient orgManagerClient;
    private final CoreSearchEngineClient coreSearchEngineClient;

    private String csrfToken = "";

    @Scheduled(fixedDelay = 3600000)
    private void getToken() {
        csrfToken = orgManagerClient.getToken().getCsrfToken();
    }

    public OrgManagerDTO getOrganizationByOrgName(String orgName) {
        final OrgReq request = new OrgReq(orgName);
        final int rawDataId = orgManagerClient.getOrganizationByOrgName(csrfToken, request).get(0)
                                              .getRows().get(0)
                                              .getElementGroups().get(0)
                                              .getObjectContainer()
                                              .getRawDataId();
        final String cookie = "CSRFToken=" + csrfToken;
        final var org = orgManagerClient.getOrganizationByRawDataId(cookie, csrfToken, rawDataId).get(0);
        return new OrgManagerDTO(org);
    }

    public List<LdapUserDTO> getAllEmployeesByOrgName(String orgName) {
        try {
            return ldapTemplate.search(query()
                            .where(LdapAttribute.OBJECT_CLASS.getName()).is(LdapAttribute.PERSON.getName())
                            .and(LdapAttribute.DEPARTMENT.getName()).whitespaceWildcardsLike(orgName),
                    ldapUtil::convertToLDAPUserDto);
        } catch (TimeLimitExceededException exception) {
            throw new RequestTimeoutException("The connection time to LDAP Server was exceeded");
        }
    }

    public List<LdapUserDTO> getEmployeeByNtid(String ntid) {
        return ldapTemplate.search(
                query().where(LdapAttribute.OBJECT_CLASS.getName()).is(LdapAttribute.PERSON.getName())
                        .and(LdapAttribute.CN.getName()).is(ntid),
                ldapUtil::convertToLDAPUserDto);
    }

    public List<LdapUserDTO> getEmployeeByEmail(String email) {
        return ldapTemplate.search(
                query().where(LdapAttribute.OBJECT_CLASS.getName()).is(LdapAttribute.PERSON.getName())
                        .and(LdapAttribute.MAIL.getName()).is(email),
                ldapUtil::convertToLDAPUserDto);
    }

    public List<String> getDeletedEmployeesFromLDAP(List<String> listNtid) {
        try {
            ContainerCriteria subConditionParam = query().where(LdapAttribute.CN.getName()).is(listNtid.get(0));

            for (int i = 1; i < listNtid.size(); i++) subConditionParam.or(LdapAttribute.CN.getName()).is(listNtid.get(i));

            ContainerCriteria mainConditionParam = query().attributes(LdapAttribute.CN.getName())
                                                          .where(LdapAttribute.OBJECT_CLASS.getName()).is(LdapAttribute.PERSON.getName())
                                                          .and(subConditionParam);

            List<String> employeesReturnFromLDAP = ldapTemplate.search(mainConditionParam, ldapUtil::getEmployeeNtid);

            return listNtid.stream().filter(emp -> !employeesReturnFromLDAP.contains(emp)).collect(Collectors.toList());
        } catch (TimeLimitExceededException exception) {
            throw new RequestTimeoutException("The connection time to LDAP Server was exceeded");
        }
    }

    public CoreSearchRes getDataSearch(String text){
        final CoreSearchReq request = new CoreSearchReq(text);
        return coreSearchEngineClient.getDataSearchEngine(request);
    }
}
