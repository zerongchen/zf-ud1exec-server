package com.aotain.ud1exec.service.traffic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aotain.common.utils.tools.MonitorStatisticsUtils;
import com.aotain.ud1exec.service.IUd1LogService;
import com.aotain.ud1exec.utils.DpiAttributeUtil;
import com.aotain.ud1exec.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cym
 * 输出文件名格式定义：
FlowDirectionFile+软件厂家编号+综分服务器IP +UTC时间.txt
 */
public class TrafficServiceImpl implements IUd1LogService {

	private Logger logger = LoggerFactory.getLogger(TrafficServiceImpl.class);

	private TrafficCache flowDirectionCache;
	
	public TrafficServiceImpl() {

		flowDirectionCache = new TrafficCache();
	}

	/**
	 * 文件命名： FlowDirectionFile+软件厂家编号+综分服务器IP +UTC时间.txt
	 *
	 * 文件命名： FlowDirectionFile+ AOT+192.168.1.100+1513768013.txt
	 * 
	 * sendIp:CU服务器IP
	 * 
	 */
	@SuppressWarnings("static-access")
	public void execute(long receivedtime, String receivedIp, String sendIp, String data, String probetype) {
		logger.debug("receive data ={ " + data + " }");

		if ("SYNC_RUN".equals(data)) {
			flowDirectionCache.schedulerCheckElement();
		} else {

			if (data == null || data.length() < 1)
				return;

			try {
				JSONObject trafficFlowLog = JSON.parseObject(data);

				String logcontent = trafficFlowLog.getString("logcontent");
				String[] logArray = logcontent.split("\\|");
				if (logArray.length != 9) {
					logger.error(" traffic flow error line is [" + data + "],current line length is {" + logArray.length + "},required length is {9}");
				}
				String dthour = StringUtil.getDtHour(logArray[0]);
				String euVender = DpiAttributeUtil.getEuSoftwareProviderByIp(sendIp);
				String areaId = DpiAttributeUtil.getEuAreaIdByIp(sendIp);
				String file_key = sendIp + "+" + Thread.currentThread().getId();

				logcontent = String.format("%s|%s|%s|%d|%s|%s|%s", logcontent, probetype, areaId, receivedtime, receivedIp, sendIp, euVender);
				flowDirectionCache.addElement(dthour, file_key, logcontent);

				flowDirectionCache.checkElement(dthour, file_key);

			} catch (Exception e) {
				logger.error(" failure of traffic flow policy ", e);
				MonitorStatisticsUtils.addEvent(e);
			}
		}
	}
}
