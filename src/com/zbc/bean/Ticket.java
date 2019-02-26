package com.zbc.bean;

import org.apache.solr.client.solrj.beans.Field;

public class Ticket {
	
	@Field
	private String id;
	@Field
	private String ticket_id;
	@Field
	private String project_name;
	@Field
	private String cust_name;
	@Field
	private String jobs;
	@Field("destatus")
	private String destatus;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTicket_id() {
		return ticket_id;
	}
	public void setTicket_id(String ticket_id) {
		this.ticket_id = ticket_id;
	}
	public String getProject_name() {
		return project_name;
	}
	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}
	public String getCust_name() {
		return cust_name;
	}
	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}
	public String getJobs() {
		return jobs;
	}
	public void setJobs(String jobs) {
		this.jobs = jobs;
	}
	public String getDestatus() {
		return destatus;
	}
	public void setDestatus(String destatus) {
		this.destatus = destatus;
	}
	
	@Override
	public String toString() {
		return "Ticket [id=" + id + ", ticket_id=" + ticket_id + ", project_name=" + project_name + ", cust_name="
				+ cust_name + ", jobs=" + jobs + ", destatus=" + destatus + "]";
	}
	
	
}
