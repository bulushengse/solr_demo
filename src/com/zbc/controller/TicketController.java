package com.zbc.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zbc.bean.Ticket;
import com.zbc.service.TicketService;

@Controller
@RequestMapping(value="/ticket")
public class TicketController {

	@Autowired
	private TicketService ticketService;
	
	@RequestMapping(value = "/test1",produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public Object queryByPage() {
		
		Map<String,Object> searchMap = new HashMap<String,Object>();
		searchMap.put("ticket_id", "2018");
		//searchMap.put("destatus", "已完成");
		//searchMap.put("project_name", "xxx");
		searchMap.put("sortField1", "ticket_id");
		//searchMap.put("pageNo", "2");
		
		List<Ticket> list = ticketService.queryByPage(searchMap);
		
		System.out.println(list.toString());
		
		return list.toString();
		
		
	}
	
	
	@RequestMapping(value = "/test2",produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public Object queryByGroup() {
		
		Map<String,Object> searchMap = new HashMap<String,Object>();
		searchMap.put("ticket_id", "2018");
		//searchMap.put("destatus", "已完成");
		searchMap.put("groupField", "destatus");
		
		List<List<Ticket>> list = ticketService.queryByGroup(searchMap);
		
		System.out.println(list.toString());
		
		return list.toString();
		
	}
	
	
	@RequestMapping(value = "/test3",produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public Object queryByHl() {
		
		Map<String,Object> searchMap = new HashMap<String,Object>();
		searchMap.put("ticket_id", "201812");
		searchMap.put("destatus", "已完成");  
		searchMap.put("hlField", "destatus"); //查询条件，与高亮的字段要相同!
		
		List<Ticket> list = ticketService.queryByHl(searchMap);
		
		System.out.println(list.toString());
		
		return list.toString();
		
	}
}
