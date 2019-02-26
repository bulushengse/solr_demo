package test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;
import org.junit.Test;

public class SolrjTest {


	/**
	 * solrJ之新增修改
	 * @throws IOException
	 * @throws SolrServerException
	 */
    @Test
    public void add() throws IOException, SolrServerException {
       
        final String solrUrl = "http://localhost:8983/solr/new_core";
        //创建solrClient同时指定超时时间，不指定走默认配置
        HttpSolrClient solrServer = new HttpSolrClient.Builder(solrUrl)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();
        
        //创建SolrInputDocument对象，然后通过它来添加域。
        SolrInputDocument document = new SolrInputDocument();
        // 第一个参数：域的名称，域的名称必须是在schema.xml中定义的
        // 第二个参数：域的值，域的值就是新增或修改后的数据
        // 注意：id的域不能少
        document.addField("id", "s314108271");
        document.addField("ticket_id", "20190124001");
        document.addField("project_name", "xxx");
        document.addField("cust_name", "xxx");
        document.addField("jobs", "xxx");
        document.addField("destatus", "已完成");
        
        //通过HttpSolrServer对象将SolrInputDocument添加到索引库。
        solrServer.add(document);
        // 提交。
        solrServer.commit();
       
    }
    
	/**
	 * solrJ之删除
	 * @throws IOException
	 * @throws SolrServerException
	 */
    @Test
    public void deleteDocument() throws IOException, SolrServerException {
       
        final String solrUrl = "http://localhost:8983/solr/new_core";
        //创建solrClient同时指定超时时间，不指定走默认配置
        HttpSolrClient solrServer = new HttpSolrClient.Builder(solrUrl)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();
        
        //通过id删除
        solrServer.deleteById("s314108271");
   
        //提交。
        solrServer.commit();
       
    }
    
	/**
	 * solrJ之查询 
	 * @throws IOException
	 * @throws SolrServerException
	 */
	@Test
	public void query() throws IOException, SolrServerException {
	
	    final String solrUrl = "http://localhost:8983/solr/new_core";
	    //创建solrClient同时指定超时时间，不指定走默认配置
	    HttpSolrClient solrServer = new HttpSolrClient.Builder(solrUrl)
	            .withConnectionTimeout(10000)
	            .withSocketTimeout(60000)
	            .build();
	    
	    //封装查询参数
        Map<String, String> queryParamMap = new HashMap<String, String>();
        queryParamMap.put("q", "*:*");
        
        //添加到SolrParams对象
        MapSolrParams queryParams = new MapSolrParams(queryParamMap);
        //执行查询返回QueryResponse
        QueryResponse response = solrServer.query(queryParams);
        //获取doc文档
        SolrDocumentList documents = response.getResults();
        
        //内容遍历
        for(SolrDocument doc : documents) {
              System.out.println("id:"+doc.get("id")
                 +",ticket_id:"+doc.get("ticket_id")
                 +",destatus:"+doc.get("destatus")
                 +",project_name:"+doc.get("project_name")
                 +",cust_name:"+doc.get("cust_name")
                 +",jobs:"+doc.get("jobs"));
        }
        solrServer.close();
	}
	

	/**
	 * solrJ之查询  MapSolrParams 有一个 SolrQuery 子类，它提供了一些方法极大地简化了查询操作。
	 * @throws IOException
	 * @throws SolrServerException
	 */
	@Test
	public void query2() throws IOException, SolrServerException {
	
	    final String solrUrl = "http://localhost:8983/solr/new_core";
	    //创建solrClient同时指定超时时间，不指定走默认配置
	    HttpSolrClient solrServer = new HttpSolrClient.Builder(solrUrl)
	            .withConnectionTimeout(10000)
	            .withSocketTimeout(60000)
	            .build();
	    
	    //封装查询参数
	    SolrQuery solrQuery = new SolrQuery();
	    //设置查询条件
	    solrQuery.setQuery("ticket_id:2018*");
	    //设置过滤条件
	    solrQuery.setFilterQueries("destatus:已完成");
	    //设置显示的域的列表
	    solrQuery.addField("id");
	    solrQuery.addField("ticket_id");
	    solrQuery.addField("destatus");
	    solrQuery.addField("jobs");
	    //solrQuery.setFields("id", "ticket_id", "destatus","jobs") //设置显示的域的列表
	    
	    //设置排序
	    solrQuery.setSort("id",SolrQuery.ORDER.desc);//排序一
		solrQuery.addSort("ticket_id",SolrQuery.ORDER.desc);//排序二
		
	    //设置分页
	    solrQuery.setRows(10);
	    solrQuery.setStart(0);//从0开始
	    
        //执行查询返回QueryResponse
        QueryResponse response = solrServer.query(solrQuery);
        //获取doc文档
        SolrDocumentList documents = response.getResults();
        
        System.out.println("总数："+documents.getNumFound());
        //内容遍历
        for(SolrDocument doc : documents) {
              System.out.println("id:"+doc.get("id")
                 +",ticket_id:"+doc.get("ticket_id")
                 +",destatus:"+doc.get("destatus")
                 +",project_name:"+doc.get("project_name")
                 +",cust_name:"+doc.get("cust_name")
                 +",jobs:"+doc.get("jobs"));
        }
        solrServer.close();
	}
	
	
	/**
	 * solrJ之查询，分组统计
	 * @throws IOException
	 * @throws SolrServerException
	 */
	@Test
	public void query3() throws IOException, SolrServerException {
	
	    final String solrUrl = "http://localhost:8983/solr/new_core";
	    //创建solrClient同时指定超时时间，不指定走默认配置
	    HttpSolrClient solrServer = new HttpSolrClient.Builder(solrUrl)
	            .withConnectionTimeout(10000)
	            .withSocketTimeout(60000)
	            .build();
	    
	    //封装查询参数
	    SolrQuery solrQuery = new SolrQuery();
	    
	    //设置查询条件
	    solrQuery.setQuery("ticket_id:2018*");
	    //设置显示的域的列表
	    solrQuery.setFields("id", "ticket_id", "destatus", "jobs", "cust_name");
	    
	    //是否分组
        solrQuery.setParam("group", true);
        //分组的字段，不可以是多值字段
        solrQuery.setParam("group.field", "destatus");
        //分组中每个组的上限数量，默认为1
        solrQuery.setParam("group.limit","10");
        //分布式模式使用分组，并返回分组数量
        solrQuery.setParam("group.ngroups","true");
	    
        //设置start，开始的组
        solrQuery.setStart(0);//从0开始
        //设置rows，返回多少组
	    solrQuery.setRows(3);
	    
        //执行查询返回QueryResponse
        QueryResponse response = solrServer.query(solrQuery);
        //从response中获取想要的结果，因为结构与正常搜索的结构不一致，所以取数据时与普通搜索获取数据不一样
        GroupResponse groupResponse = response.getGroupResponse();
        
        List<GroupCommand> groupCommandList = groupResponse.getValues();
        
        // 判断是否为空
        if(groupCommandList!=null && groupCommandList.size()>0){
        
        	// 匹配的结果总数
        	long count = groupCommandList.get(0).getMatches();
        	System.out.println("匹配结果总数:"+count);
        	// 分组总数
        	long groupNum = groupCommandList.get(0).getNGroups();
        	System.out.println("分组总数:"+groupNum);
        	
        	//分组结果list
        	List<Group> groupList=groupCommandList.get(0).getValues();
        	
        	// 遍历返回的每个分组solrDocumentList
        	for(Group group:groupList){
        		
        		SolrDocumentList solrDocumentList = group.getResult();
        		System.out.println("该组数量:"+group.getResult().getNumFound());
        		
        		//solrDocument内容遍历
                for(SolrDocument doc : solrDocumentList) {
                	
                	System.out.println("ticket_id:"+doc.get("ticket_id")
                         +",destatus:"+doc.get("destatus")
                         +",project_name:"+doc.get("project_name")
                         +",cust_name:"+doc.get("cust_name")
                         +",jobs:"+doc.get("jobs"));
                }
        	}
        }	
        
        solrServer.close();
	}
	
	
	/**
	 * solrJ之查询  ,高亮显示。
	 * @throws IOException
	 * @throws SolrServerException
	 */
	@Test
	public void query4() throws IOException, SolrServerException {
	
	    final String solrUrl = "http://localhost:8983/solr/new_core";
	    //创建solrClient同时指定超时时间，不指定走默认配置
	    HttpSolrClient solrServer = new HttpSolrClient.Builder(solrUrl)
	            .withConnectionTimeout(10000)
	            .withSocketTimeout(60000)
	            .build();
	    
	    //封装查询参数
	    SolrQuery solrQuery = new SolrQuery();
	    
	    // 设置查询条件
	    solrQuery.setQuery("ticket_id:2018*");
	    
	    // 设置显示的域的列表
	 	solrQuery.setFields("id", "ticket_id", "destatus","jobs");
	  
	    //设置高亮
	    solrQuery.setHighlight(true);
	    solrQuery.addHighlightField("ticket_id");
	    solrQuery.setHighlightSimplePre("<SPAN>");
	    solrQuery.setHighlightSimplePost("</SPAN>");
	    
	    //设置每页显示多少条
	    solrQuery.setRows(3);
        
        //执行查询返回QueryResponse
        QueryResponse response = solrServer.query(solrQuery);
       
        Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
        System.out.println(highlighting);
        
        Set<String> set = highlighting.keySet();
        
        for (String key : set) {
            System.out.println("++" + key);        // id
            Map<String, List<String>> map = highlighting.get(key); // id对应的查询结果，可能有多个字段，所以是map结构
            Set<String> set2 = map.keySet();       // 字段名集合
            // 遍历字段
            for (String key2 : set2) {
                System.out.println("--" + key2);    // 字段名
                List<String> list = map.get(key2);  // 字段对应的值，因为分词了，所以是一个String列表
                for (String s : list) {
                    System.out.print(s + " ");      // 输出高亮的文本
                }
                System.out.println();
            }
        }
        
        solrServer.close();
	}
	
	
	
	
}
