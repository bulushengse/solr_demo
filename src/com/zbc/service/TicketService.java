package com.zbc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.stereotype.Service;

import com.zbc.bean.Ticket;
import com.zbc.util.StringUtil;

@Service("ticketService")
@PropertySource("classpath:my.properties")
public class TicketService {

	@Autowired
	private SolrTemplate solrTemplate;
	
	@Resource
    private Environment environment;
	
	
	/**
	 * 查询  (筛选分页)
	 * @param start
	 * @param size
	 */
	public List<Ticket> queryByPage(Map<String,Object> searchMap){
		Query query = new SimpleQuery("*:*");
		
		//设置条件,Criteria用于对条件的封装
		String ticket_id = (String) searchMap.get("ticket_id");
		if(StringUtil.isNotEmpty(ticket_id)) {
			//Criteria c = new Criteria("ticket_id").contains(ticket_id);//前后*
			Criteria c = new Criteria("ticket_id").startsWith(ticket_id);//后*
			//Criteria c = new Criteria("ticket_id").endsWith(ticket_id);//前*
			//Criteria c = new Criteria("ticket_id").lessThanEqual(ticket_id);//小于等于
			//Criteria c = new Criteria("ticket_id").greaterThanEqual(ticket_id);//大于等于
			query.addCriteria(c);
		}
		
		String destatus = (String) searchMap.get("destatus");
		if(StringUtil.isNotEmpty(destatus)) {
			Criteria c = new Criteria("destatus").is(destatus);
			query.addCriteria(c);
		}
		
		String project_name = (String) searchMap.get("project_name");
		if(StringUtil.isNotEmpty(project_name)) {
			Criteria c = new Criteria("project_name").is(project_name);
			query.addCriteria(c);
		}
		
		//排序
		//String sortValue= (String) searchMap.get("sort");//ASC  DESC  
		String sortField1 = (String) searchMap.get("sortField1");//排序字段
		if(StringUtil.isNotEmpty(sortField1)) {
			Sort sort = new Sort(Sort.Direction.DESC, sortField1);
			query.addSort(sort);//排序1
		}
		
		//分页参数    开始索引(默认0),每页记录数(默认10)
		String pageNo = (String) searchMap.get("pageNo");
		Integer pageSize = Integer.valueOf(environment.getRequiredProperty("page.size"));
		if(StringUtil.isNotEmpty(pageNo)) {
			Long page = Long.valueOf(pageNo);
			query.setOffset(((page-1)*pageSize));
			query.setRows(pageSize);
		}
		
		//分页查询结果    "new_core"是core实例   
		ScoredPage<Ticket> page = solrTemplate.queryForPage("new_core",query, Ticket.class);
		
		List<Ticket> list = page.getContent();
		
		return list;
	}
	
	/**
	 * 查询  (分组统计)
	 * @param start
	 * @param size
	 */
	public List<List<Ticket>> queryByGroup(Map<String,Object> searchMap){
		Query query = new SimpleQuery("*:*");
		
		//设置条件,Criteria用于对条件的封装
		String ticket_id = (String) searchMap.get("ticket_id");
		if(StringUtil.isNotEmpty(ticket_id)) {
			Criteria c = new Criteria("ticket_id").startsWith(ticket_id);//后*
			query.addCriteria(c);
		}
		String destatus = (String) searchMap.get("destatus");
		if(StringUtil.isNotEmpty(destatus)) {
			Criteria c = new Criteria("destatus").is(destatus);
			query.addCriteria(c);
		}
		
		//分组统计
		String groupField = (String) searchMap.get("groupField");//分组字段
		if(StringUtil.isNotEmpty(groupField)) {
			GroupOptions groupOptions = new GroupOptions().addGroupByField(groupField);
			groupOptions.setLimit(10);//分组中每个组的上限数量
			groupOptions.setOffset(0);//分组中每个组的开始条数
			
			query.setGroupOptions(groupOptions);
		}
		
		//query.setOffset(0l);
		query.setRows(10);
		
		//分组查询结果    "new_core"是core实例   
		GroupPage<Ticket> page = solrTemplate.queryForGroupPage("new_core", query, Ticket.class);
		
		//这个域名，一定要是在上面进行分组过的域名 groupField
		GroupResult<Ticket> groupResult = page.getGroupResult(groupField);
		//获取分组入口页
		Page<GroupEntry<Ticket>> groupEntries = groupResult.getGroupEntries();
		//从分组入口页groupEntries中获取分组入口集合
		List<GroupEntry<Ticket>> entryList = groupEntries.getContent();
	
		System.out.println("匹配结果总数:"+groupResult.getMatches());
		System.out.println("总分组数:"+entryList.size());
		System.out.println("分组字段:"+groupResult.getName());
		
		List<List<Ticket>> list = new ArrayList<List<Ticket>>();
		
		//从entryList中取出每一个分组结果
		for (GroupEntry<Ticket> entry : entryList) {
			String result = entry.getGroupValue();
			Page<Ticket> p = entry.getResult();
			
			System.out.println("该组("+result+")数量:"+p.getNumberOfElements());
			
			List<Ticket> l = p.getContent();
			for (Ticket t : l) {
				System.out.println(t.toString());
			}
			
			list.add(l);
		}
		
		return list;
	}
	
	
	/**
	 * 查询  (高亮)
	 * @param start
	 * @param size
	 */
	public List<Ticket> queryByHl(Map<String,Object> searchMap){
		HighlightQuery query = new SimpleHighlightQuery();
		
		//设置条件,Criteria用于对条件的封装
		String ticket_id = (String) searchMap.get("ticket_id");
		if(StringUtil.isNotEmpty(ticket_id)) {
			Criteria c = new Criteria("ticket_id").startsWith(ticket_id);//后*
			query.addCriteria(c);
		}
		String destatus = (String) searchMap.get("destatus");
		if(StringUtil.isNotEmpty(destatus)) {
			Criteria c = new Criteria("destatus").is(destatus);
			query.addCriteria(c);
		}
		
		//封装高亮条件
		String hlField = (String) searchMap.get("hlField");//高亮字段
		System.out.println(hlField);
		HighlightOptions highlightOptions=new HighlightOptions() ;
		highlightOptions.addField(hlField);//查询条件，与高亮的字段要相同!
		highlightOptions.setSimplePrefix("<em style='color: red'>");
		highlightOptions.setSimplePostfix("</em>");
		query.setHighlightOptions(highlightOptions);
		
		//高亮查询结果    "new_core"是core实例   
		HighlightPage<Ticket> page = solrTemplate.queryForHighlightPage("new_core",query, Ticket.class);
		
		List<HighlightEntry<Ticket>> highlighted = page.getHighlighted();
		
		for(HighlightEntry<Ticket> h:highlighted) {
			Ticket item=h.getEntity();//获取记录中的实体类
			if(h.getHighlights().size()>0&&h.getHighlights().get(0).getSnipplets().size()>0) {
				
				//获取第一个高亮域的内容  h.getHighlights().get(0).getSnipplets()
				//设置高亮域的结果 
				item.setDestatus(h.getHighlights().get(0).getSnipplets().get(0));
			}
		}
		
		List<Ticket> list = page.getContent();
		
		return list;
	}
	
}
