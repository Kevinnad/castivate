package com.sdi.castivate.model;

/**
 * Created by Balachandar on 20-Apr-15.
 */

import java.io.Serializable;

public class Users{

	public String userId;
	public String casting_id;

	public Users(String userId, String casting_id) {
		this.userId = userId;
		this.casting_id = casting_id;
	}
}
