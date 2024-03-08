package com.ibg.receipt.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;
import com.ibg.receipt.base.vo.JsonResultVo;
import com.ibg.receipt.enums.business.OrganizationEnum;
import com.ibg.receipt.model.receipt.ReceiptUser;
import com.ibg.receipt.service.receipt.ReceiptUserService;
import com.ibg.receipt.shiro.ReceiptPasswordService;
import com.ibg.receipt.vo.api.user.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


/**
 *
 * @author wanghongbo01
 * @date 2022/8/22 17:24
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private ReceiptUserService receiptUserService;

    @Resource
    private ReceiptPasswordService passwordService;

    @PostMapping("/login")
    public JsonResultVo<?> login(@RequestBody UserLoginRequestVo vo) {
        log.info("用户登录:{}", vo.getUserName());


        try {
            vo.check();
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(vo.getUserName(), vo.getPassword());
            usernamePasswordToken.setRememberMe(true);

            subject.login(usernamePasswordToken);
//            return JsonResultVo.success(subject.getSession().getId());
            ReceiptUser user = receiptUserService.getReceiptUserByUserName(vo.getUserName());
            JSONObject result = new JSONObject();
            result.put("token",subject.getSession().getId());
            result.put("isExport",user.getIsExport() == null?(byte)0:user.getIsExport());
            result.put("isWhite",user.getIsWhite() == null?(byte)0:user.getIsWhite());
//            return JsonResultVo.success(subject.getSession().getId());
            return JsonResultVo.success(result);
        } catch (ServiceException e) {
            log.warn("登录异常", e);
            return JsonResultVo.error(e.getCode(), e.getMessage());
        } catch (AuthenticationException e){
            log.warn("用户名或者密码错误", e);
            return JsonResultVo.error(CodeConstants.C_99999999);
        } catch (Exception e){
            log.error("登录异常", e);
            return JsonResultVo.error(CodeConstants.C_10101002, "登录异常");
        }
    }

    @PostMapping("/check")
    public JsonResultVo<?> check(@RequestBody UserCheckRequestVo vo) {
        log.info("用户登录:{}", vo.getUserName());
        try {
            vo.check();
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(vo.getUserName(), (char[]) null);
            usernamePasswordToken.setRememberMe(true);

            subject.login(usernamePasswordToken);
            ReceiptUser user = receiptUserService.getReceiptUserByUserName(vo.getUserName());
            JSONObject result = new JSONObject();
            result.put("token",subject.getSession().getId());
            result.put("isExport",user.getIsExport() == null?(byte)0:user.getIsExport());
            result.put("isWhite",user.getIsWhite() == null?(byte)0:user.getIsWhite());
//            return JsonResultVo.success(subject.getSession().getId());
            return JsonResultVo.success(result);
        } catch (ServiceException e) {
            log.warn("登录异常", e);
            return JsonResultVo.error(e.getCode(), e.getMessage());
        } catch (AuthenticationException e){
            log.warn("用户名或者密码错误", e);
            return JsonResultVo.error(CodeConstants.C_99999999);
        } catch (Exception e){
            log.error("登录异常", e);
            return JsonResultVo.error(CodeConstants.C_10101002, "登录异常");
        }
    }

    @PostMapping("/register")
    public JsonResultVo<?> register(@RequestBody UserRegisterRequestVo vo) {
        log.info("用户注册:{}", vo.getUserName());
        try {
            vo.check();
            ReceiptUser existReceiptUser = receiptUserService.getReceiptUserByUserName(vo.getUserName());
            if(existReceiptUser != null) {
                log.warn("用户已存在:{}", JSON.toJSONString(existReceiptUser));
                return JsonResultVo.error(CodeConstants.C_10101002, "用户已存在");
            }
            ReceiptUser receiptUser = buildReceiptUser(vo);
            receiptUserService.save(receiptUser);
            return JsonResultVo.success();
        } catch (ServiceException e) {
            log.warn("用户注册异常", e);
            return JsonResultVo.error(e.getCode(), e.getMessage());
        } catch (Exception e){
            log.error("用户注册异常", e);
            return JsonResultVo.error(CodeConstants.C_10101002, "用户注册异常");
        }
    }

    @PostMapping("/logout")
    public JsonResultVo<?> logout() {
        try {
            Subject subject = SecurityUtils.getSubject();
            subject.logout();;
        } catch (Exception e) {
            log.warn("退出登录异常", e);
        } finally {
            return JsonResultVo.success();
        }

    }

    private ReceiptUser buildReceiptUser(UserRegisterRequestVo vo) {

        ReceiptUser receiptUser = new ReceiptUser();

        receiptUser.setPassword(passwordService.encryptPassword(vo.getPassword()));
        receiptUser.setUserName(vo.getUserName());
        receiptUser.setEmail(vo.getEmail());

        return receiptUser;
    }


    /**
     * 查询内部资金运营
     *
     * @return
     */
    @PostMapping(value = "/userList")
    public JsonResultVo<?> userList() {
        try {
            List<UserListVo> list = new ArrayList<>();
            List<ReceiptUser> userList = receiptUserService.findAllUserList();
            userList.stream().forEach(user -> {
                list.add(UserListVo.builder().userName(user.getUserName()).build());
            });
            log.info("查询内部资金运营返回参数:{}", JSON.toJSONString(JsonResultVo.success().addData("list", list)));
            return JsonResultVo.success().addData("list", list);
        } catch (ServiceException se) {
            log.warn("查询内部资金运营列表异常，code:{}， message:{}", se.getCode(), se.getMessage());
            return JsonResultVo.error(se.getCode(), se.getMessage());
        } catch (Exception e) {
            log.error("查询内部资金运营异常:{}", e.getMessage(), e);
            return JsonResultVo.error();
        }
    }

    /**
     * 所属机构
     *
     * @return
     */
    @PostMapping(value = "/organizationList")
    public JsonResultVo<?> organizationList() {
        try {
            List<OrganizationListVo> list = new ArrayList<>();
            Stream.of(OrganizationEnum.values()).forEach(e -> {
                list.add(OrganizationListVo.builder().name(e.name()).value(e.getDesc()).build());
            });
            log.info("查询所属机构返回参数:{}", JSON.toJSONString(JsonResultVo.success().addData("list", list)));
            return JsonResultVo.success().addData("list", list);
        } catch (ServiceException se) {
            log.warn("查询所属机构异常，code:{}， message:{}", se.getCode(), se.getMessage());
            return JsonResultVo.error(se.getCode(), se.getMessage());
        } catch (Exception e) {
            log.error("查询所属机构异常:{}", e.getMessage(), e);
            return JsonResultVo.error();
        }
    }

}
