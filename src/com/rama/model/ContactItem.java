package com.rama.model;

public class ContactItem {

	private int id;
	private String name;
	private String phone;
	private String homePhone;

	private String email;
	private byte[] images;
	private String designation;
	private String city;
	private String group;

	private boolean select;

	public ContactItem(String name, String phone, byte[] images) {
		this.name = name;
		this.phone = phone;
		this.images = images;
	}

	public ContactItem(String name, String phone, String email, byte[] images,
			String designation, String city) {
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.images = images;
		this.designation = designation;
		this.city = city;
	}

	public ContactItem(String name, String phone, byte[] images, String email) {
		this.name = name;
		this.phone = phone;
		this.images = images;
		this.email = email;
	}

	public ContactItem(int id, String name, String phone, String homePhone,
			String email, byte[] images, String designation, String city,
			String group) {
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.homePhone = homePhone;
		this.email = email;
		this.images = images;
		this.designation = designation;
		this.city = city;
		this.group = group;
	}

	public ContactItem(String name, String phone, String homePhone,
			String email, byte[] images, String designation, String city,
			String group) {
		this.name = name;
		this.phone = phone;
		this.homePhone = homePhone;
		this.email = email;
		this.images = images;
		this.designation = designation;
		this.city = city;
		this.group = group;
	}

	public ContactItem(String name, String phone, String email, byte[] images) {
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.images = images;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public byte[] getImages() {
		return images;
	}

	public void setImages(byte[] images) {
		this.images = images;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

}
