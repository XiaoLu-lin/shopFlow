package org.ysling.litemall.admin.web;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [litemall-plus] is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *              http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *  MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */
import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.ysling.litemall.admin.model.region.result.RegionListResult;
import org.ysling.litemall.core.utils.response.ResponseUtil;
import org.ysling.litemall.db.domain.LitemallRegion;
import org.ysling.litemall.admin.service.AdminRegionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ysling.litemall.core.annotation.JsonBody;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 地区管理
 */
@Slf4j
@RestController
@RequestMapping("/admin/region")
@Validated
public class AdminRegionController {

    @Autowired
    private AdminRegionService regionService;

    /**
     * 获取地区子列表
     */
    @GetMapping("/list/sub")
    public Object subList(@JsonBody String id) {
        return ResponseUtil.okList(regionService.queryByPid(id));
    }

    /**
     * 获取地区列表
     */
    @GetMapping("/list")
    public Object list() {
        List<LitemallRegion> regionAll = regionService.getAll();
        Map<Byte, List<LitemallRegion>> collect = regionAll.stream().collect(Collectors.groupingBy(LitemallRegion::getType));
        byte provinceType = 1;
        List<LitemallRegion> provinceList = collect.get(provinceType);
        byte cityType = 2;
        List<LitemallRegion> citys = collect.get(cityType);
        Map<String, List<LitemallRegion>> cityListMap = citys.stream().collect(Collectors.groupingBy(LitemallRegion::getPid));
        byte areaType = 3;
        List<LitemallRegion> areas = collect.get(areaType);
        Map<String, List<LitemallRegion>> areaListMap = areas.stream().collect(Collectors.groupingBy(LitemallRegion::getPid));

        //省列表
        List<RegionListResult> regionVOList = new ArrayList<>();
        //遍历省
        for (LitemallRegion province : provinceList) {
            //复制省
            RegionListResult provinceResult = new RegionListResult();
            BeanUtil.copyProperties(province , provinceResult);
            List<RegionListResult> cityVOList = new ArrayList<>();
            //遍历市
            List<LitemallRegion> cityList = cityListMap.get(province.getId());
            for (LitemallRegion city : cityList) {
                //复制市
                RegionListResult cityResult = new RegionListResult();
                BeanUtil.copyProperties(city , cityResult);
                List<RegionListResult> areaVOList = new ArrayList<>();
                //遍历区
                List<LitemallRegion> areaList = areaListMap.get(city.getId());
                for (LitemallRegion area : areaList) {
                    //复制地区
                    RegionListResult areaResult = new RegionListResult();
                    BeanUtil.copyProperties(area , areaResult);
                    areaVOList.add(areaResult);
                }
                cityResult.setChildren(areaVOList);
                cityVOList.add(cityResult);
            }
            provinceResult.setChildren(cityVOList);
            regionVOList.add(provinceResult);
        }
        return ResponseUtil.okList(regionVOList);
    }


}
