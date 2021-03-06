package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.vo.SpuVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.pms.entity.SpuEntity;

import java.util.Map;

/**
 * spu信息
 *
 * @author lixianfeng
 * @email fengge@atguigu.com
 * @date 2020-08-19 19:17:00
 */
public interface SpuService extends IService<SpuEntity> {

    PageResultVo queryPage(PageParamVo paramVo);


    PageResultVo querySpu(PageParamVo pageParamVo, Long cid);

    void bigSave(SpuVo spuVo);
}

