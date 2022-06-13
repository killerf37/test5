package com.ginfon.core.service;

import com.github.pagehelper.PageInfo;
import com.ginfon.core.model.Query;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: James
 * @Date: 2018/5/10 16:26
 * @Description:
 */
public interface IGenericService<T, ID extends Serializable, Q extends Query> {
	
	T get(ID id);

	List<T> loadAll();

	boolean isExist(Q query);

	boolean save(T entity) throws Exception;

	boolean update(T entity) throws Exception;

	boolean delete(ID id) throws Exception;

	PageInfo<?> findByPage(int pageNo, int pageSize);

	int count(Q query);

	List<T> query(Q query);

	boolean deleteByIds(String[] ids) throws Exception;
}
