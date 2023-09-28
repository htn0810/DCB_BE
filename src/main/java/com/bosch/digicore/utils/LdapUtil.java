package com.bosch.digicore.utils;

import com.bosch.digicore.constants.LdapAttribute;
import com.bosch.digicore.dtos.LdapUserDTO;
import org.springframework.stereotype.Service;

import javax.naming.directory.Attributes;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
public class LdapUtil {

	private String getAttributeValue(String attributeDescription, Attributes attributes) {
		int start = attributeDescription.length() + 2;
		String attribute = String.valueOf(attributes.get(attributeDescription));
		return attribute.equals("null") ? null : attribute.substring(start);
	}

	public LdapUserDTO convertToLDAPUserDto(Attributes attributes) {
		// department
		String department = getAttributeValue(LdapAttribute.DEPARTMENT.getName(), attributes);
		List<String> departments = department != null ? List.of(department.split(" ")) : List.of();
		// dates
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SX");
		Instant createdDate =  OffsetDateTime.parse(Objects.requireNonNull(getAttributeValue(LdapAttribute.WHEN_CREATED.getName(), attributes)),formatter).toInstant();
		Instant lastModifiedDate = OffsetDateTime.parse(Objects.requireNonNull(getAttributeValue(LdapAttribute.WHEN_CHANGED.getName(), attributes)),formatter).toInstant();
		return LdapUserDTO.builder()
				.ntid(getAttributeValue(LdapAttribute.CN.getName(), attributes))
				.lastName(getAttributeValue(LdapAttribute.SN.getName(), attributes))
				.firstName(getAttributeValue(LdapAttribute.GIVEN_NAME.getName(), attributes))
				.displayName(getAttributeValue(LdapAttribute.DISPLAY_NAME.getName(), attributes))
				.email(getAttributeValue(LdapAttribute.MAIL.getName(), attributes))
				.department(departments)
				.createdDate(createdDate)
				.lastModifiedDate(lastModifiedDate)
				.corporate(getAttributeValue(LdapAttribute.CO.getName(), attributes))
				.build();
	}

	public String getEmployeeNtid(Attributes attributes) {
		return getAttributeValue(LdapAttribute.CN.getName(), attributes);
	}
}
