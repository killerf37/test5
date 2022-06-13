package com.ginfon.core.web.sys;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import com.ginfon.core.utils.ShiroUtils;
import com.ginfon.core.web.entity.Menu;
import com.ginfon.core.web.entity.User;
import com.ginfon.core.web.service.IMenuService;

/**
 * 	
 * @author Mark
 *
 */
@Controller
public class IndexController {
	
    @Autowired
    private IMenuService menuService;

    // 系统首页
    @GetMapping("/index")
    public String index(ModelMap mmap) {
        // 取身份信息
        User user = ShiroUtils.getUser();
        // 根据用户id取出菜单
        List<Menu> menus = menuService.selectMenusByUserId(user.getUserId());
        mmap.put("menus", menus);
        mmap.put("user", user);
        return "index";
    }

    @GetMapping("/system/main")
    public String main() {
        return "main";
    }
}
