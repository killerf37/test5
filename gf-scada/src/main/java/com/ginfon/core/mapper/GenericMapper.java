package com.ginfon.core.mapper;

import com.ginfon.core.model.Query;

import java.io.Serializable;
import java.util.List;

/**
 * @author James
 */
public interface GenericMapper<T, ID extends Serializable, Q extends Query> {
	/**
	 * 根据主键值获取对象
	 *
	 * @param id
	 */
	T get(ID id);

	/**
	 * 获取全部实体
	 */
	List<T> loadAll();

	/**
	 * 查找是否存在
	 *
	 * @param query 查询条件
	 * @return int
	 */
	int isExist(Q query);

	/**
	 * 保存
	 *
	 * @param entity 保存对象
	 * @return int
	 * @throws Exception
	 */
	int save(T entity) throws Exception;

	/**
	 * 更新
	 *
	 * @param entity 修改对象
	 * @return int
	 * @throws Exception
	 */
	int update(T entity) throws Exception;

	/**
	 * 根据主键删除记录
	 *
	 * @param id
	 * @return int
	 * @throws Exception
	 */
	int delete(ID id) throws Exception;

	/**
	 * 分页查询
	 */
	List<T> findByPage();

	/**
	 * 统计
	 *
	 * @param query 查询条件
	 * @return int
	 */
	int count(Q query);

	/**
	 * 查询
	 *
	 * @param query 查询条件
	 */
	List<T> query(Q query);

	/**
	 * 根据id数组删除记录
	 *
	 * @param ids 数组
	 * @return int
	 */
	int deleteByIds(String[] ids) throws Exception;
}