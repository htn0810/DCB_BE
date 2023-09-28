package com.bosch.digicore.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;


@Entity
@Table(name = "images")
@Getter
@Setter
@NoArgsConstructor
public class Image {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
	private String name;
	private String type;
	@Lob
	private byte[] data;

	public Image(String name, String type, byte[] data) {
		this.name = name;
		this.type = type;
		this.data = data;
	}
}
