package com.yinrj.emos.wx.controller;

import cn.hutool.core.io.FileUtil;
import com.yinrj.emos.wx.common.util.R;
import com.yinrj.emos.wx.config.shiro.JwtUtil;
import com.yinrj.emos.wx.dto.CheckinDto;
import com.yinrj.emos.wx.exception.EmosException;
import com.yinrj.emos.wx.service.CheckinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author yinrongjie
 * @version 1.0
 * @description
 * @date 2021/3/12
 */
@RestController
@RequestMapping("/checkin")
@Api("签到模块Web接口")
@Slf4j
public class CheckInController {
    private final CheckinService checkinService;

    private final JwtUtil jwtUtil;

    @Value("${emos.image-folder}")
    private String imageFolder;

    public CheckInController(CheckinService checkinService, JwtUtil jwtUtil) {
        this.checkinService = checkinService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/validate")
    @ApiOperation("查看今天是否可以签到")
    public R validateCanCheckIn(@RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        String result = checkinService.validCanCheckIn(userId, LocalDate.now().toString());
        return R.ok().put("result", result);
    }

    @PostMapping("/checkin")
    @ApiOperation("签到")
    public R checkin(@Valid CheckinDto checkinDto, @RequestParam("photo") MultipartFile file,
                     @RequestHeader("token") String token) {
        if (file == null) {
            return R.error("没有上传图片");
        }
        int userId = jwtUtil.getUserId(token);
        String fileName = file.getOriginalFilename().toLowerCase(Locale.ROOT);
        if (!fileName.endsWith(".jpg")) {
            return R.error("文件格式错误");
        } else {
            String path = imageFolder + "/" + fileName;

            try {
                file.transferTo(Paths.get(path));
                HashMap<String, Object> param = new HashMap<>();
                param.put("userId", userId);
                param.put("path", path);
                param.put("city", checkinDto.getCity());
                param.put("district", checkinDto.getDistrict());
                param.put("address", checkinDto.getAddress());
                param.put("country", checkinDto.getCountry());
                param.put("province", checkinDto.getProvince());
                checkinService.checkin(param);
                return R.ok("签到成功");
            } catch (IOException e) {
                e.printStackTrace();
                log.error(e.getMessage() + this.getClass().getName());
                throw new EmosException("图片保存错误");
            } finally {
                FileUtil.del(path);
            }
        }
    }

    @PostMapping("/create_face_model")
    @ApiOperation("创建人脸模型")
    public R createFaceModel(@RequestParam("photo") MultipartFile file, @RequestHeader("token") String token) {
        if (file == null) {
            return R.error("没有上传图片");
        }
        int userId = jwtUtil.getUserId(token);
        String fileName = file.getOriginalFilename().toLowerCase(Locale.ROOT);
        if (!fileName.endsWith(".jpg")) {
            return R.error("文件格式错误");
        } else {
            String path = imageFolder + "/" + fileName;
            try {
                file.transferTo(Paths.get(path));
                checkinService.createFaceModel(userId, path);
                return R.ok("人脸模型成功");
            } catch (IOException e) {
                e.printStackTrace();
                log.error(e.getMessage());
                throw new EmosException("图片保存错误");
            }finally {
                FileUtil.del(path);
            }
        }
    }
}
