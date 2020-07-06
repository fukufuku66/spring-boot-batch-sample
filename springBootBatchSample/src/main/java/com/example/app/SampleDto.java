package com.example.app;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "sample")
public class SampleDto {

	@Id
	@Column(name = "name", nullable = false)
	@Getter
	@Setter
	private String name;
	
	@Column(name = "age", nullable = false)
	@Getter
	@Setter
	private int age;
	
	@Column(name = "sex", nullable = false)
	@Getter
	@Setter
	private boolean sex;
	
	@Column(name = "profession", nullable = false)
	@Getter
	@Setter
	private String profession;
	
	@Column(name = "address", nullable = false)
	@Getter
	@Setter
	private String address;
	
	public SampleDto(
			String name,
			int age,
			boolean sex, 
			String profession,
			String address) {
		this.name = name;
		this.age = age;
		this.sex = sex;
		this.profession = profession;
		this.address = address;
	}
	
	public SampleDto() {
		
	}

}
