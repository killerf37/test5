package com.ginfon.scada.web;

import com.ginfon.core.annotation.Log;
import com.ginfon.core.enums.BusinessType;
import com.ginfon.core.model.MapDto;
import com.ginfon.core.utils.AjaxResult;
import com.ginfon.core.utils.ExcelUtil;
import com.ginfon.core.utils.ShiroUtils;
import com.ginfon.core.web.BaseController;
import com.ginfon.core.web.page.TableDataInfo;
import com.ginfon.core.web.service.IRoleService;
import com.ginfon.main.ScadaClientContext;
import com.ginfon.main.ScadaLauncher;
import com.ginfon.scada.entity.*;
import com.ginfon.scada.service.IFaultLogService;
import com.ginfon.scada.service.IScadaLogService;
import com.ginfon.scada.service.ISelectCurrentStatus;
import com.github.pagehelper.PageInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 设备监控功能的Controller。
 *
 * @author Mark
 */
@Controller
public class EquipmentMonitorController extends BaseController {

	public static final String[] STATUS_0 = {"急停", "过载", "堵包", "本地", "休眠", "停止", "运行", "恢复", "断开", "恢复", "预留11", "预留12", "预留13", "预留14", "预留15", "恢复"};

	public static final String[] STATUS_1 = {"运行", "待机", "休眠", "暂停", "堵包", "急停", "远程", "过载", "连接", "光电触发", "预留11", "预留12", "预留13", "预留14", "预留15", "故障"};


	/**
	 * 用户角色查询服务，用于判断访问用户所属的角色有没有权限调用功能。
	 */
	@Autowired
	private IRoleService roleService;

	/**
	 * 日志查询服务，用于查询SCADA日志给前端。
	 */
	@Autowired
	private IScadaLogService scadaLogService;

	/**
	 * SCADA应用上下文对象。
	 */
	@Autowired
	private ScadaClientContext scadaClientContext;

	@Autowired
	private ISelectCurrentStatus selectCurrentStatus;

	@Autowired
	private IFaultLogService faultLogService;

	/**
	 * 获取元素名称
	 */
	@Autowired
	private ScadaLauncher scadaLauncher;

	/**
	 * 布局图layout
	 * @param mmap
	 * @return
	 */
	@RequestMapping("/scada/layout")
	public String showl(ModelMap mmap) {
		List<DeviceFaultDTO> faultDTOList = faultLogService.selctTopFault();
		//	传递线体号与名称的关联定义
		mmap.addAttribute("lineNo", this.scadaClientContext.getDeviceMap());
		mmap.addAttribute("errinfo", faultDTOList);
		//	传递名称与线体号的关联定义
		mmap.addAttribute("lineNameNo", this.scadaClientContext.getMapDevice());
		//	传递线体号与名称的关联定义
		mmap.addAttribute("lineNoName", this.scadaClientContext.getdeviceToMap());
		//  传递元素名称
		mmap.addAttribute("elename", this.scadaLauncher.getGfElement());
		//传递设备类型名称<type:类型名称String>
		mmap.addAttribute("typedescrib", scadaClientContext.getDeviceType());
		//传递设备异常描述<type:<异常码Int:异常信息String>>
		mmap.addAttribute("errdescrib", scadaClientContext.getErrinfo());

		mmap.addAttribute("lineip", scadaClientContext.getDeviceNameIp());
		//地图名字的集合
		List<String> ops = new ArrayList<String>();
		try {
			File file = new File(EquipmentMonitorController.class.getResource("/svg/zl").toURI());
			//	没有对文件进行验证，如果该目录下有非SVG文件或者超大文件，会出问题的。
			//	因为要遍历文件传给前端。
			File[] svgs = file.listFiles();
			//	布局图的集合
			List<Elements> list = new ArrayList<>(svgs.length);
			//地图线体名称对应线号
			HashMap<Integer, String> mapNo = new HashMap<>();

			for (int i = 0; i < svgs.length; i++) {
				StringBuilder sb = new StringBuilder();
				//String tagname=svgs[i].getName();
				//String tagop =tagname.substring(0,tagname.indexOf("."));//文件名
				//int substart=tagop.indexOf("-");
				//String lineName=tagop.substring(substart+1);//取出文件名中线体名称
				//String url="http://"+scadaClientContext.getLineip().get(lineName)+":9011/";
				//mapNo.put((i+1),tagop);//将文件序号和文件名放在map中
				//ops.add(tagop);
				InputStreamReader isr = new InputStreamReader(new FileInputStream(svgs[i]), "UTF-8");
				BufferedReader br = new BufferedReader(isr);
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				Document doc = Jsoup.parse(sb.toString());
				Elements element = doc.getElementsByTag("svg");
				mmap.addAttribute("sca" + i, element);
				//mmap.addAttribute("linename"+i,lineName);
				//mmap.addAttribute("ip"+i,url);
				//list.add(element);
			}
			mmap.addAttribute("mapNo", mapNo);
			//	传递所有的SVG图
			mmap.addAttribute("count", svgs.length);
			//	传递下拉选单内可选选项
			//mmap.addAttribute("option", ops);
			//	传递下拉选单的数量
			//mmap.addAttribute("layerFlag", list.size());

		} catch (Exception err) {
			err.printStackTrace();
		}
		return "layout1";
	}

	/**
	 * 摆轮显示的小框框
	 * @param mmap
	 * @return
	 */
	@RequestMapping("/system/layout")
	public String showlayout(ModelMap mmap) {
		List<DeviceFaultDTO> faultDTOList = faultLogService.selctTopFault();
		//	传递线体号与名称的关联定义
		mmap.addAttribute("lineNo", this.scadaClientContext.getDeviceMap());
		mmap.addAttribute("errinfo", faultDTOList);
		//	传递名称与线体号的关联定义
		mmap.addAttribute("lineNameNo", this.scadaClientContext.getMapDevice());
		//	传递线体号与名称的关联定义
		mmap.addAttribute("lineNoName", this.scadaClientContext.getdeviceToMap());
		//  传递元素名称
		mmap.addAttribute("elename", this.scadaLauncher.getGfElement());
		//传递设备类型名称<type:类型名称String>
		mmap.addAttribute("typedescrib", scadaClientContext.getDeviceType());
		//传递设备异常描述<type:<异常码Int:异常信息String>>
		mmap.addAttribute("errdescrib", scadaClientContext.getErrinfo());

		mmap.addAttribute("lineip", scadaClientContext.getDeviceNameIp());
		//地图名字的集合
		List<String> ops = new ArrayList<>();
		try {
			File file = new File(EquipmentMonitorController.class.getResource("/svg/blx").toURI());
			//	没有对文件进行验证，如果该目录下有非SVG文件或者超大文件，会出问题的。
			//	因为要遍历文件传给前端。
			File[] svgs = file.listFiles();
			//地图线体名称对应线号
			HashMap<Integer, String> mapNo = new HashMap<>();

			List<MapDto> mapDtoList = new ArrayList<>();
			Map<Integer,List<MapDto>> res = new HashMap<>();
			for (int i = 0; i < svgs.length; i++) {
				StringBuilder sb = new StringBuilder();
				String tagname = svgs[i].getName();
				String tagop = tagname.substring(0, tagname.indexOf("."));//文件名
				int substart = tagop.indexOf("-");
				String lineName = tagop.substring(substart + 1);//取出文件名中线体名称
				String url = scadaClientContext.getDeviceNameIp().get(lineName);
				mapNo.put((i + 1), tagop);//将文件序号和文件名放在map中
				ops.add(tagop);
				InputStreamReader isr = new InputStreamReader(new FileInputStream(svgs[i]), "UTF-8");
				BufferedReader br = new BufferedReader(isr);
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				Document doc = Jsoup.parse(sb.toString());
				Elements element = doc.getElementsByTag("svg");
				mapDtoList.add(new MapDto(lineName, element, url));
				//组装数据，供前台遍历用，处理逻辑：每遍历6次，将List存入map
				int m = (i + 1) % 6,n = i / 6;//m为余数，n为map的key
				if(i != svgs.length - 1){
					if(m == 0){
						res.put(n,mapDtoList);
						mapDtoList = new ArrayList<>();
					}
				}else{
					res.put(n,mapDtoList);
				}
			}
			mmap.addAttribute("res", res);
			mmap.addAttribute("mapNo", mapNo);
			//	传递所有的SVG图
			mmap.addAttribute("count", svgs.length);
			//传递所有的线体定义
			mmap.addAttribute("bailunInfo", this.scadaClientContext.getBailunInfo());
		} catch (Exception err) {
			err.printStackTrace();
		}
		return "layout";
	}


	/**
	 * 输送线使用的吧。
	 *
	 * @param mmap
	 * @return
	 */
	@RequestMapping("/system/scada")
	public String showScada(ModelMap mmap) {
		Long userId = ShiroUtils.getUser().getUserId();
		//	传递用户的角色信息。
		mmap.addAttribute("roles", this.roleService.selectRoleKeys(userId));
		//	传递所有的线体定义
		mmap.addAttribute("allLine", this.scadaClientContext.getLineMap());
		//	传递线体号与名称的关联定义
		mmap.addAttribute("lineNo", this.scadaClientContext.getDeviceMap());
		//地图名字的集合
		List<String> ops = new ArrayList<String>();
//		ops.add("总图");
//		//遍历线体名称
//		for(Map.Entry<String, String> entry : this.scadaClientContext.getDeviceMap().entrySet())
//			ops.add(entry.getValue());
//		//
		//	读取目录
		try {
			File file = new File(EquipmentMonitorController.class.getResource("/svg/ssx").toURI());
			//没有对文件进行验证，如果该目录下有非SVG文件或者超大文件，会出问题的。
			//因为要遍历文件传给前端。
			File[] svgs = file.listFiles();
			//布局图的集合
			List<Elements> list = new ArrayList<>(svgs.length);
			List<MapDto> mapDtoList = new ArrayList<>();
			Map<Integer,List<MapDto>> res = new HashMap<>();
			int i = 0;
			for (File target : svgs) {
				StringBuilder sb = new StringBuilder();
				String tagname = target.getName();
				String tagop = tagname.substring(0, tagname.indexOf("."));
				String lineName = tagop.split("-")[1];
				ops.add(tagop);
				InputStreamReader isr = new InputStreamReader(new FileInputStream(target), "UTF-8");
				BufferedReader br = new BufferedReader(isr);
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				Document doc = Jsoup.parse(sb.toString());
				Elements element = doc.getElementsByTag("svg");
//				list.add(element);
				mapDtoList.add(new MapDto(lineName, element, ""));
				//组装数据，供前台遍历用，处理逻辑：每遍历6次，将List存入map
				int m = (i++ + 1) % 6,n = i / 6;//m为余数，n为map的key
				if(i != svgs.length){
					if(m == 0){
						res.put(n,mapDtoList);
						mapDtoList = new ArrayList<>();
					}
				}else{
					if (i%6 != 0){
						n++;
					}
					res.put(n,mapDtoList);
				}
			}
			//	传递所有的SVG图
//			mmap.addAttribute("scadas", list);
			//	传递下拉选单内可选选项
			mmap.addAttribute("option", ops);
			//	传递下拉选单的数量
//			mmap.addAttribute("layerFlag", list.size());
			mmap.addAttribute("res", res);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//先用main1，后续测试无问题，可将main页面删除，此处改成main
		return "main1";
	}

	/**
	 * 用于查询SCADA日志。查询的结果被限制为10条。
	 *
	 * @param log
	 * @return
	 */
	@RequestMapping("/scadaList")
	@ResponseBody
	public List<CurrentStatusResult> list(CurrentStatus log) {
		List<CurrentStatus> list = this.selectCurrentStatus.selectcurrentsta();
		return this.toResultList(list);
	}




//	public List<ScadaEventResult> list(ScadaLog log) {
//		List<ScadaEvent> list = this.scadaLogService.getScadaLogFifth(log);
//		return this.toResultList(list);
//	}

	@RequiresPermissions("device:event:query")
	@RequestMapping("/device/event/query")
	public String info(ScadaLog log) {
		return "mes/eventQuery";
	}

	@RequiresPermissions("device:event:query")
	@RequestMapping("/device/event/query/info")
	@ResponseBody
	public TableDataInfo eventList(CurrentStatus log) {
		super.startPage();
		List<CurrentStatus> list = this.selectCurrentStatus.selectallsta(log);

		TableDataInfo rspData = new TableDataInfo();
		rspData.setCode(0);
		rspData.setRows(this.toResultList(list));
		rspData.setTotal(new PageInfo<CurrentStatus>(list).getTotal());

		return rspData;
	}

//	public TableDataInfo eventList(ScadaLog log) {
//		super.startPage();
//		List<ScadaEvent> list = this.scadaLogService.getScadaLogInfo(log);
//
//        TableDataInfo rspData = new TableDataInfo();
//        rspData.setCode(0);
//        rspData.setRows(this.toResultList(list));
//        rspData.setTotal(new PageInfo<ScadaEvent>(list).getTotal());
//
//		return rspData;
//	}

	@Log(title = "日志数据导出", businessType = BusinessType.EXPORT)
	@RequiresPermissions("device:event:query")
	@PostMapping("/device/event/query/export")
	@ResponseBody
	public AjaxResult export(CurrentStatus log) throws Exception {
		List<CurrentStatus> list = this.selectCurrentStatus.selectallsta(log);
		List<CurrentStatusResult> resultList = new LinkedList<CurrentStatusResult>();
		for (CurrentStatus e : list) {
			resultList.add(this.toResult(e));
		}
		try {
			ExcelUtil<CurrentStatusResult> util = new ExcelUtil<>(CurrentStatusResult.class);
			return util.exportExcel(resultList, "数据表");
		} catch (Exception e) {
			e.printStackTrace();
			return super.error("导出Excel失败，请联系网站管理员！");
		}
	}


	private List<CurrentStatusResult> toResultList(List<CurrentStatus> list) {
		List<CurrentStatusResult> result = new LinkedList<>();
		for (CurrentStatus se : list)
			result.add(this.toResult(se));
		return result;
	}

	private CurrentStatusResult toResult(CurrentStatus se) {
		CurrentStatusResult result = new CurrentStatusResult();

		String lineNo = "";
		//	处理线体名称
		String lineName = "";
		if (se.getParentlineNo() != 0 && se.getParentlineNo() != 32) {
			lineNo = se.getParentlineNo() < 10 ? "0" + se.getParentlineNo() : String.valueOf(se.getParentlineNo());
			lineName = this.scadaClientContext.getDeviceMap().get(lineNo);
		} else {
			if (se.getParentlineNo() == 0) {
				lineName = "主盘";
			} else if (se.getParentlineNo() == 32) {
				lineName = "分盘";
			}
		}
		//	判断触发解除
		String tiggerType = se.getType() == 1 ? "活动" : "恢复";
		//	状态描述
		String status = STATUS_0[se.getStateId() - 1];

		result.setParentlineName(lineName);
		result.setLineName(se.getLineNo().toString());
		result.setStateId(status);
		result.setType(tiggerType);
		result.setStartime(se.getStartime());
		result.setEndtime(se.getEndtime());
		return result;
	}


	@RequiresPermissions("device:net:status")
	@RequestMapping("/device/net/status")
	public String netQuery(ModelMap map) {
		//传递所有的线体定义
		map.addAttribute("bailunInfo", this.scadaClientContext.getBailunInfo());
		return "netStatus";
	}


	@RequestMapping("/device/event/errQuery")
	public String errInfo(ModelMap mmap) {
		//	传递线体号与名称的关联定义
		mmap.addAttribute("lineNoName", this.scadaClientContext.getdeviceToMap());
		//传递设备类型名称<type:类型名称String>
		mmap.addAttribute("typedescrib", scadaClientContext.getDeviceType());
		//传递设备异常描述<type:<异常码Int:异常信息String>>
		mmap.addAttribute("errdescrib", scadaClientContext.getErrinfo());
		return "mes/errInfo";
	}

	@RequestMapping("/device/event/errQuery/info")
	@ResponseBody
	public TableDataInfo eventList(ScadaLog log) {
		startPage();
		List<DeviceFaultDTO> faultDTOList = faultLogService.selectMoreFault(log);
		return getDataTable(faultDTOList);
	}

	@RequestMapping("/device/setting")
	public String setInfo(ModelMap mmap) {

		return "mes/settingInfo";
	}

	@RequestMapping("/baiLun/detailShow/{lineName}")
	public String showBLDetail(@PathVariable("lineName") String lineName, ModelMap modelMap){
		modelMap.addAttribute("lineName", lineName);
		try {
			File file = new File(EquipmentMonitorController.class.getResource("/svg/blx").getFile());
			File[] blSvgs = file.listFiles();
			if(blSvgs.length > 0){
				for(File fileTemp:blSvgs){
					StringBuilder sb = new StringBuilder();
					String name = fileTemp.getName();
					String dealName = name.substring(0, name.indexOf(".")).split("-")[1];
					if(dealName.equals(lineName)){
						InputStreamReader isr = new InputStreamReader(new FileInputStream(fileTemp), "UTF-8");
						BufferedReader br = new BufferedReader(isr);
						String line;
						while ((line = br.readLine()) != null) {
							sb.append(line);
						}
						br.close();
						Document doc = Jsoup.parse(sb.toString());
						Elements element = doc.getElementsByTag("svg");
						modelMap.addAttribute("blSvg",element);
					}
				}
			}

		}catch (Exception e){
			e.printStackTrace();
		}
		List<DeviceFaultDTO> faultDTOList = faultLogService.selctTopFault();
		//	传递线体号与名称的关联定义
		modelMap.addAttribute("lineNo", this.scadaClientContext.getDeviceMap());
		modelMap.addAttribute("errinfo", faultDTOList);
		//	传递名称与线体号的关联定义
		modelMap.addAttribute("lineNameNo", this.scadaClientContext.getMapDevice());
		//	传递线体号与名称的关联定义
		modelMap.addAttribute("lineNoName", this.scadaClientContext.getdeviceToMap());
		//  传递元素名称
		modelMap.addAttribute("elename", this.scadaLauncher.getGfElement());
		//传递设备类型名称<type:类型名称String>
		modelMap.addAttribute("typedescrib", scadaClientContext.getDeviceType());
		//传递设备异常描述<type:<异常码Int:异常信息String>>
		modelMap.addAttribute("errdescrib", scadaClientContext.getErrinfo());

		modelMap.addAttribute("lineip", scadaClientContext.getLineip());

		modelMap.addAttribute("currentlineNo",this.scadaClientContext.getMapDevice().get(lineName));

		//传递所有的线体定义
		modelMap.addAttribute("bailunInfo", this.scadaClientContext.getBailunInfo());
		return "mes/blDetail";
	}

	@RequestMapping("/shuSong/detailShow/{lineName}")
	public String showSSDetail(@PathVariable("lineName") String lineName, ModelMap modelMap){
		modelMap.addAttribute("lineName", lineName);

		//	传递所有的线体定义
		modelMap.addAttribute("allLine", this.scadaClientContext.getLineMap());
		//	传递线体号与名称的关联定义
		modelMap.addAttribute("lineNo", this.scadaClientContext.getDeviceMap());

		try {
			File file = new File(EquipmentMonitorController.class.getResource("/svg/ssx").getFile());
			File[] blSvgs = file.listFiles();
			if(blSvgs.length > 0){
				for(File fileTemp:blSvgs){
					StringBuilder sb = new StringBuilder();
					String name = fileTemp.getName();
					String dealName = name.substring(0, name.indexOf(".")).split("-")[1];
					if(dealName.equals(lineName)){
						InputStreamReader isr = new InputStreamReader(new FileInputStream(fileTemp), "UTF-8");
						BufferedReader br = new BufferedReader(isr);
						String line;
						while ((line = br.readLine()) != null) {
							sb.append(line);
						}
						br.close();
						Document doc = Jsoup.parse(sb.toString());
						Elements element = doc.getElementsByTag("svg");
						modelMap.addAttribute("blSvg",element);
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return "mes/ssDetail";
	}

	/**
	 * 用于查询bl日志。查询的结果被限制为10条。
	 *
	 * @param lineNo
	 * @return
	 */
	@RequestMapping("/blList")
	@ResponseBody
	public List<DeviceFaultDTO> bllist(int lineNo) {
        List<DeviceFaultDTO> faultDTOList = faultLogService.selectLineFault(lineNo);
		return faultDTOList;
	}
}
