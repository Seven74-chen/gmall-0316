package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.mapper.SkuMapper;
import com.atguigu.gmall.pms.mapper.SpuDescMapper;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import com.atguigu.gmall.pms.service.SkuImagesService;
import com.atguigu.gmall.pms.service.SpuAttrValueService;
import com.atguigu.gmall.pms.vo.SkuVo;
import com.atguigu.gmall.pms.vo.SpuAttrValueVo;
import com.atguigu.gmall.pms.vo.SpuVo;
import com.atguigu.gmall.sms.vo.SkuSaleVo;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SpuMapper;
import com.atguigu.gmall.pms.service.SpuService;
import org.springframework.util.CollectionUtils;


@Service("spuService")
public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {

    @Autowired
    private SpuDescMapper spuDescMapper;
    @Autowired
    private SpuAttrValueService spuAttrValueService;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuAttrValueService skuAttrValueService;
    @Autowired
    private GmallSmsClient smsClient;


    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public PageResultVo querySpu(PageParamVo pageParamVo, Long cid) {
        QueryWrapper<SpuEntity> queryWrapper = new QueryWrapper<>();
        if (cid != 0) {
            queryWrapper.eq("category_id", cid);
        }
        //?????????????????????????????????
        String key = pageParamVo.getKey();
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.and(t -> t.like("name", key).or().like("id", key));
        }
        //??????????????????
        IPage<SpuEntity> page = this.page(pageParamVo.getPage(), queryWrapper);
        //??????????????????Ipage????????????page??????????????????
        PageResultVo pageResultVo = new PageResultVo(page);
        return pageResultVo;
    }

    @GlobalTransactional
    @Override
    public void bigSave(SpuVo spuVo) {
        //1?????????spu????????????
        //1.1???spu?????????
        //????????????
        Long spuId = saveSpu(spuVo);

        //1.2???spu????????????
        saveSpuDesc(spuVo, spuId);

        //1.3???spu??????????????????
        saveBaseAttr(spuVo, spuId);

        //2?????????sku????????????
        saveSku(spuVo, spuId);
        //???????????????
        //int i = 1/0;
    }

    private void saveSku(SpuVo spuVo, Long spuId) {
        //??????skus????????????
        List<SkuVo> skuVos = spuVo.getSkus();
        //??????????????????null
        if (CollectionUtils.isEmpty(skuVos)) {
            return;
        }
        //??????skuVos???????????????skuVo
        skuVos.forEach(skuVo -> {
            //2.1???sku????????????
            //????????????skuEntity???
            SkuEntity skuEntity = new SkuEntity();
            //???skuvo???????????????skuEntity
            BeanUtils.copyProperties(skuVo, skuEntity);
            //??????skuEntity???????????????
            //??????id
            skuEntity.setBrandId(spuVo.getBrandId());
            //??????id
            skuEntity.setCatagoryId(spuVo.getCategoryId());
            //??????????????????
            List<String> images = skuVo.getImages();
            //??????????????????
            if (!CollectionUtils.isEmpty(images)) {
                skuEntity.setDefaultImage(skuEntity.getDefaultImage() == null ? images.get(0) : skuEntity.getDefaultImage());
            }
            skuEntity.setSpuId(spuId);
            this.skuMapper.insert(skuEntity);
            //??????skuId
            Long skuId = skuEntity.getId();

            //2.2???sku????????????
            if (!CollectionUtils.isEmpty(images)){
                String defaultImage = images.get(0);
                List<SkuImagesEntity> collect = images.stream().map(image -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setDefaultStatus(StringUtils.equals(defaultImage, image) ? 1 : 0);
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setSort(0);
                    skuImagesEntity.setUrl(image);
                    return skuImagesEntity;
                }).collect(Collectors.toList());
                this.skuImagesService.saveBatch(collect);
            }
            //2.3???sku??????????????????
            List<SkuAttrValueEntity> saleAttrs = skuVo.getSaleAttrs();
            saleAttrs.forEach(saleAttr -> {
                // ??????????????????????????????id??????AttrEntity
                saleAttr.setSort(0);
                saleAttr.setSkuId(skuId);
            });
            this.skuAttrValueService.saveBatch(saleAttrs);
            //3??????????????????????????????
            SkuSaleVo skuSaleVo = new SkuSaleVo();
            BeanUtils.copyProperties(skuVo,skuSaleVo);
            skuSaleVo.setSkuId(skuId);
            this.smsClient.saveSkuSaleInfo(skuSaleVo);
        });
    }

    private void saveBaseAttr(SpuVo spuVo, Long spuId) {
        //???????????????????????????
        List<SpuAttrValueVo> baseAttrs = spuVo.getBaseAttrs();
        //?????????????????????????????????
        if (!CollectionUtils.isEmpty(baseAttrs)) {
            //??????????????????baseAtters???????????????????????????collect??????spuAttrValueVo???baseAtters????????????
            List<SpuAttrValueEntity> collect = baseAttrs.stream().map(spuAttrValueVo -> {
                spuAttrValueVo.setSpuId(spuId);
                spuAttrValueVo.setSort(0);
                return spuAttrValueVo;
            }).collect(Collectors.toList());
            //????????????
            this.spuAttrValueService.saveBatch(collect);
        }
    }

    private void saveSpuDesc(SpuVo spuVo, Long spuId) {
        SpuDescEntity spuDescEntity = new SpuDescEntity();
        spuDescEntity.setSpuId(spuId);
        //???list???????????????string???????????? "," ?????????
        spuDescEntity.setDecript(StringUtils.join(spuVo.getSpuImages(), ","));
        this.spuDescMapper.insert(spuDescEntity);
    }

    private Long saveSpu(SpuVo spuVo) {
//        SpuEntity spuEntity = new SpuEntity();
//        BeanUtils.copyProperties(spuVo,spuEntity);
//        spuEntity.setCreateTime(new Date());
//        spuEntity.setPublishStatus(1);
//        spuEntity.setUpdateTime(spuEntity.getCreateTime());
//        this.save(spuEntity);
//        return spuEntity.getId();

        spuVo.setPublishStatus(1);
        spuVo.setCreateTime(new Date());
        spuVo.setUpdateTime(spuVo.getCreateTime());
        this.save(spuVo);
        return spuVo.getId();
    }


}