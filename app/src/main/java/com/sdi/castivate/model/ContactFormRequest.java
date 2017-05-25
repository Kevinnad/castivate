package com.sdi.castivate.model;

/**
 * Created by Balachandar on 20-Apr-15.
 */

public class ContactFormRequest {

	public String userId;
	public String talent_image_id;
	public String agent_email;
	public String agent_name;
	public String agent_phone;
	public String agent_message;

	public ContactFormRequest(String userId, String talent_image_id, String agent_email, String agent_name, String agent_phone, String agent_message) {
		this.userId = userId;
		this.talent_image_id = talent_image_id;
		this.agent_email = agent_email;
		this.agent_name = agent_name;
		this.agent_phone = agent_phone;
		this.agent_message = agent_message;
	}
}
