package com.ginfon.core.holder;

import com.ginfon.core.enums.UserAndRloleType;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.TreeSet;

public class MenuHolder {
	private Set<Menu> sysAdminMenus;

	private Set<Menu> sysUserMenus;

	public Set<Menu> getSysAdminMenus() {
		return sysAdminMenus;
	}

	public void setSysAdminMenus(Set<Menu> sysAdminMenus) {
		this.sysAdminMenus = sysAdminMenus;
	}

	public Set<Menu> getSysUserMenus() {
		return sysUserMenus;
	}

	public void setSysUserMenus(Set<Menu> sysUserMenus) {
		this.sysUserMenus = sysUserMenus;
	}

	public Set<Menu> getMenus(String type) {
		if (UserAndRloleType.ADMIN.toString().equals(type)) {
			return checkValid(sysAdminMenus);
		} else if (UserAndRloleType.SYSUSER.toString().equals(type)) {
			return checkValid(sysUserMenus);
		} else {
			return null;
		}
	}

	/***
	 * 当前用户有效菜单
	 *
	 * @param menus
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	private Set<Menu> checkValid(Set<Menu> menus) {
		Set<Menu> set = new TreeSet<Menu>();
		for (Menu menu : menus) {
			String parentId = menu.getParentId();
			if (StringUtils.isNotBlank(parentId)) {
				for (Menu parent : menus) {
					if (parentId.equals(parent.getId())) {
						parent.addChild(menu);
						menu.setParent(parent);
						break;
					}
				}
			} else {
				set.add(menu);
			}
		}
		return set;
	}
}
