package com.ginfon.core.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ginfon.core.mapper.GenericMapper;
import com.ginfon.core.model.Query;
import com.ginfon.core.service.IGenericService;

import java.io.Serializable;
import java.util.List;

/**
 * @author James
 */
public abstract class GenericServiceImpl<T, ID extends Serializable, Q extends Query>
		implements IGenericService<T, ID, Q> {
	
	protected abstract GenericMapper<T, ID, Q> getDao();

	/**
	 * 	根据主键值获取对象
	 *
	 * @param id
	 */
	public T get(ID id) {
		return getDao().get(id);
	}

	/**
	 * 	获取全部实体
	 */
	public List<T> loadAll() {
		return getDao().loadAll();
	}

	/**
	 * 	查找是否存在
	 *
	 * @param query 查询条件
	 */
	public boolean isExist(Q query) {
		return getDao().isExist(query) > 0;
	}

	/**
	 * 	保存
	 *
	 * @param entity 保存对象
	 * @return boolean
	 * @throws Exception
	 */
	public boolean save(T entity) throws Exception {
		return getDao().save(entity) > 0;
	}

	/**
	 * 	更新
	 *
	 * @param entity 修改对象
	 * @return boolean
	 * @throws Exception
	 */
	public boolean update(T entity) throws Exception {
		return getDao().update(entity) > 0;
	}

	/**
	 * 	根据主键删除记录
	 *
	 * @param id
	 * @return boolean
	 * @throws Exception
	 */
	public boolean delete(ID id) throws Exception {
		return getDao().delete(id) > 0;
	}

	/**
	 * 	分页查询
	 *
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public PageInfo<T> findByPage(int pageNo, int pageSize) {
		PageHelper.startPage(pageNo, pageSize);
		List<T> list = getDao().findByPage();
		return new PageInfo<T>(list);
	}

	/**
	 * 	统计
	 *
	 * @param query 查询条件
	 * @return int
	 */
	public int count(Q query) {
		return getDao().count(query);
	}

	/**
	 * 	查询
	 *
	 * @param query 查询条件
	 */
	public List<T> query(Q query) {
		return getDao().query(query);
	}

	/**
	 * 	根据id数组删除记录
	 *
	 * @param ids 数组
	 * @return boolean
	 */
	public boolean deleteByIds(String[] ids) throws Exception {
		return getDao().deleteByIds(ids) > 0;
	}
}