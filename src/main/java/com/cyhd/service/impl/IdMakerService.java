package com.cyhd.service.impl;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.common.util.IdGenerator;
import com.cyhd.service.dao.db.mapper.IdAutoIncreaseMapper;
import com.cyhd.service.dao.db.mapper.IdWithTimeMakerMapper;
import com.cyhd.service.dao.po.IdAutoIncrease;
import com.cyhd.service.dao.po.IdWithTimeMaker;
import com.cyhd.service.util.GlobalConfig;

/**
 * id maker
 * 
 */
@Service
public class IdMakerService {

	@Resource
	private IdWithTimeMakerMapper idWithTimeMakerMapper;

	@Resource
	private IdAutoIncreaseMapper idAutoIncreaseDao;

	private IdGenerator idGenerator;

	public IdMakerService() {
		idGenerator = new IdGenerator(GlobalConfig.server_id);
		if(GlobalConfig.isDeploy){
			range = 100;
			range2 = 100;
		}
	}

	/**
	 * 获取包含时间信息的唯一id 如：20140403 + 11223344 （共16位）
	 * 
	 * @return
	 * @throws StudyException
	 */
	public long getTimedId(){
		// example 20140403
		String idPart1 = DateUtil.format(GenerateDateUtil.getCurrentDate(), "yyMMdd");
		
		long idPart2 = getTimeMarkedSuffix();

		if (idPart2 == 0) {
			return this.getUniqRandomId();
		}
		
		String idPart2String = (new Long(idPart2)).toString();
		StringBuffer stringBuffer = new StringBuffer(idPart1);
		if(idPart2String.length() <= 8){
			for (int i = 0; i < 8 - idPart2String.length(); i++) {
				stringBuffer.append("0");
			}
		}
		stringBuffer.append(idPart2String);
		return Long.parseLong(stringBuffer.toString());
	}
	
	private AtomicLong idIncrease2 = new AtomicLong(0L);
	private int range2 = 30;
	private long lastId2 = 0L;
	
	private long getTimeMarkedSuffix(){
		synchronized (idIncrease2) {
			long id = idIncrease2.getAndIncrement();
			if (id >= lastId2) {
				IdWithTimeMaker maker = new IdWithTimeMaker();
				maker.setMark("0");
				idWithTimeMakerMapper.insert(maker);
				long rid = maker.getId();
				if (rid == 0)
					return 0;
				long beginId = rid * range2;
				long newLastId = beginId + range2;
				if (newLastId <= lastId2)
					return 0;
				lastId2 = newLastId;
				idIncrease2.set(beginId);
				id = idIncrease2.getAndIncrement();
			}
			return id;
		}
	}

	/**
	 * 生成唯一的随机索引
	 * 
	 * @return
	 */
	public long getUniqRandomId() {
		return idGenerator.getNextId();
	}

	private AtomicLong idIncrease = new AtomicLong(0L);
	private int range = 30;
	private long lastId = 0L;

	/**
	 * 取到全局唯一的自增id，不受进程重启限制，不受进程数限制。
	 * 
	 * @return
	 */
	public long getIncreaseId() {
		synchronized (idIncrease) {
			long id = idIncrease.getAndIncrement();
			if (id >= lastId) {
				IdAutoIncrease increase = new IdAutoIncrease(idGenerator.getNextId());
				idAutoIncreaseDao.insert(increase);
				long rid = increase.getId();
				if (rid == 0)
					return 0;
				long beginId = rid * range;
				long newLastId = beginId + range;
				if (newLastId <= lastId)
					return 0;
				lastId = newLastId;
				idIncrease.set(beginId);
				id = idIncrease.getAndIncrement();
			}
			return id;
		}
	}
}
