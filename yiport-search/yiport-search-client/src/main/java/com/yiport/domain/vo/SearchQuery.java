package com.yiport.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchQuery {

	/**
	 * 搜索的内容
	 */
	String searchKey;

	/**
	 * 判断字段
	 */
	String sortBy;

	/**
	 * 当前页码
	 */
	int pageNum = 1;

	/**
	 * 页面大小
	 */
	int pageSize = 10;

	public SearchQuery(String searchKey) {
		this.searchKey = searchKey;
	}

	public SearchQuery(String searchKey, int pageNum, int pageSize) {
		this.searchKey = searchKey;
		this.pageNum = pageNum;
		this.pageSize = pageSize;
	}

	public SearchQuery(String searchKey, String sortBy, int pageNum, int pageSize) {
		this.searchKey = searchKey;
		this.sortBy = sortBy;
		this.pageNum = pageNum;
		this.pageSize = pageSize;
	}
}
