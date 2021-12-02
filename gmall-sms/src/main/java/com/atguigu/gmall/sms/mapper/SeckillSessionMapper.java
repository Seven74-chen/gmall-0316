package com.atguigu.gmall.sms.mapper;

import com.atguigu.gmall.sms.entity.SeckillSessionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 秒杀活动场次
 * 
 * @author cbz
 * @email fengge@atguigu.com
 * @date 2021-11-30 21:18:41
 */
@Mapper
public interface SeckillSessionMapper extends BaseMapper<SeckillSessionEntity> {
	
}
