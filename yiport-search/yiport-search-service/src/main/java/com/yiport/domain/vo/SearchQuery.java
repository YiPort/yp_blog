package com.yiport.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class SearchQuery {

	/**
	 * 搜索的内容
	 */
	@NotNull
	String searchKey;

	/**
	 * 判断字段
	 */
	String sortBy;

	/**
	 * 当前页码
	 */
	@Range(min = 1, max = 50)
	int pageNum = 1;

	/**
	 * 页面大小
	 */
	@Range(min = 1, max = 20)
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
