package com.ibg.receipt.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ibg.commons.log.LogInfo;
import com.ibg.commons.log.LogInfos;
import com.ibg.commons.log.LogKey;
import com.ibg.receipt.base.enums.ReceiptStatus;
import com.ibg.receipt.base.exception.Assert;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;
import com.ibg.receipt.base.vo.JsonResultVo;
import com.ibg.receipt.base.vo.PageVo;
import com.ibg.receipt.config.fileSystem.FileSystemConfig;
import com.ibg.receipt.enums.business.ReceiptChannel;
import com.ibg.receipt.enums.business.ReceiptChildOrderAmountStatus;
import com.ibg.receipt.enums.business.UserSource;
import com.ibg.receipt.model.receipt.ReceiptUser;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderAmount;
import com.ibg.receipt.service.receipt.ReceiptUserService;
import com.ibg.receipt.service.receipt.complex.ShiroUserComplexService;
import com.ibg.receipt.service.receiptChild.ReceiptChildOrderService;
import com.ibg.receipt.util.*;
import com.ibg.receipt.vo.api.log.SecOperationLogVO;
import com.ibg.receipt.vo.api.receiptChild.ReceiptChildListRequestVo;
import com.ibg.receipt.vo.api.receiptChild.ReceiptChildListResponseVo;
import com.ibg.receipt.vo.api.receiptChild.ReceiptChildLoanDetailRequestVo;
import com.ibg.receipt.vo.api.receiptChild.ReceiptChildLoanDetailResponseVo;
import com.ibg.receipt.vo.api.receiptChild.ReceiptExportRequestVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/{source}/receiptChild")
public class ReceiptChildOrderController {

    @Autowired
    ReceiptChildOrderService receiptChildOrderService;

    @Autowired
    private ShiroUserComplexService shiroUserComplexService;
    @Autowired
    private FileSystemConfig fileSystemConfig;
    @Autowired
    ReceiptUserService receiptUserService;
    Logger secLog = LoggerFactory.getLogger("SecOperationLogger");

    @LogInfos({@LogInfo(key = LogKey.MODULE, value = "发票子单列表查询"),
            @LogInfo(key = "output", value = "#output", inReturn = true)})
    @PostMapping("/list")
    public JsonResultVo<?> list(HttpServletRequest request, @PathVariable("source") String source, @RequestBody ReceiptChildListRequestVo vo) throws IOException {
        try {
            log.info("来源:{},业务数据:{}", source, JsonUtils.toJson(vo));
            UserSource userSource = Assert.enumNotValid(UserSource.class, source, "用户来源");
            if (userSource == UserSource.CUSTOMER_SYSTEM) {
                shiroUserComplexService.getShiroUser(request);
            }else {
                //发票平台必传登陆人
                vo.checkParams();
            }
            PageVo<ReceiptChildListResponseVo> pageVo = (PageVo<ReceiptChildListResponseVo>)receiptChildOrderService.list(vo);

            // 安全日志打印vo
            SecOperationLogVO logVO = new SecOperationLogVO(
                    pageVo.getList().stream().map(order -> order.getPartnerUserId()).collect(Collectors.toList()), null
                    , SecOperationLogVO.SecLogType.CIPHERTEXT.getValue()
                    , SecOperationLogVO.SecLogType.NOT.getValue()
                    , SecOperationLogVO.SecLogType.NOT.getValue()
                    , SecOperationLogVO.SecLogType.PLAINTEXT.getValue()
                    , "发票管理-开票信息管理-开票信息管理-查看开票信息"
            );
            secLog.info(JSON.toJSONString(logVO));
            return JsonResultVo.success(pageVo);
        } catch (ServiceException e) {
            log.warn("发票测试, code：{}，message：{}", e.getCode(), e.getMessage());
            return JsonResultVo.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("发票测试异常！", e);
            return JsonResultVo.error(CodeConstants.C_10101002.getCode(), e.getMessage());
        }
    }


    @LogInfos({@LogInfo(key = LogKey.MODULE, value = "工单-贷款明细查询"),
            @LogInfo(key = "output", value = "#output", inReturn = true)})
    @PostMapping("/loanDetail")
    public JsonResultVo<?> loanDetail(HttpServletRequest request, @PathVariable("source") String source, @RequestBody ReceiptChildLoanDetailRequestVo vo) throws IOException {
        try {
            UserSource userSource = Assert.enumNotValid(UserSource.class, source, "用户来源");
            if (userSource == UserSource.CUSTOMER_SYSTEM) {
                shiroUserComplexService.getShiroUser(request);
            }
            ReceiptChildLoanDetailResponseVo responseVo = receiptChildOrderService.queryloanDetail(vo);

            // 安全日志打印vo
            SecOperationLogVO logVO = new SecOperationLogVO(
                    responseVo.getLoanSummarys().stream().map(detail -> detail.getPartnerUserId()).collect(Collectors.toList()), null
                    , SecOperationLogVO.SecLogType.NOT.getValue()
                    , SecOperationLogVO.SecLogType.NOT.getValue()
                    , SecOperationLogVO.SecLogType.NOT.getValue()
                    , SecOperationLogVO.SecLogType.PLAINTEXT.getValue()
                    , "发票管理-开票信息管理-开票信息管理-查看贷款单信息"
            );
            secLog.info(JSON.toJSONString(logVO));
            return JsonResultVo.success(responseVo);
        } catch (ServiceException e) {
            log.warn("发票测试, code：{}，message：{}", e.getCode(), e.getMessage());
            return JsonResultVo.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("发票测试异常！", e);
            return JsonResultVo.error(CodeConstants.C_10101002.getCode(), e.getMessage());
        }
    }


    @LogInfos({@LogInfo(key = LogKey.MODULE, value = "发票影像上传")})
    @RequestMapping(value = "/receiptUpload", method = RequestMethod.POST)
    public JsonResultVo<?> receiptUpload(@RequestParam(value = "file") MultipartFile[] multipartFile, HttpServletRequest request, @PathVariable("source") String source)  {
        try {
            String uploadBatchNo = SerialNoGenerator.generateUploadSerialNo();
            log.info("上传批次号{}",uploadBatchNo);
            UserSource userSource = Assert.enumNotValid(UserSource.class, source, "用户来源");
            if (userSource == UserSource.CUSTOMER_SYSTEM) {
                shiroUserComplexService.getShiroUser(request);
            }else {
                //发票平台必传登陆人
                if (StringUtils.isBlank(request.getParameter("landUserName"))) {
                    return JsonResultVo.error(CodeConstants.C_10101002.getCode(), "发票平台必传登陆人为空！");
                }
            }
            String landUserName = request.getParameter("landUserName");
            log.info("批次号{};当前操作人{}",uploadBatchNo,landUserName);
            List<String> errorFileList = receiptChildOrderService.uploadReceipt(multipartFile,request,uploadBatchNo,landUserName);
            if(CollectionUtils.isEmpty(errorFileList)){
                return JsonResultVo.success();
            }else{
                return JsonResultVo.errorWithData(JsonResultVo.ERROR, "影像文件上传失败",errorFileList);
            }
        }catch (Exception e){
            log.error("发票影像上传异常！", e);
            return JsonResultVo.error(CodeConstants.C_10101002.getCode(), e.getMessage());
        }
    }



    @GetMapping("/download/{fsFileId}")
    public void downloadFileFromFs( @PathVariable("fsFileId") String fsFileId,HttpServletResponse response)
            throws Exception {
        try {
            Map map = MetaFsUtil.downloadFromMetaFs(fileSystemConfig.getMetaFileDownloadSystemUrl(),fileSystemConfig.getSecret(),fsFileId);
            String fileName = (String) map.get("fileName");
            byte[] fileByte = (byte[]) map.get("fileBytes");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName);
            response.setStatus(HttpStatus.OK.value());
            response.addHeader(HttpHeaders.CONTENT_LENGTH, Integer.toString(fileByte.length));
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            IOUtils.write(fileByte, outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw  new Exception("文件下载失败");
        }
    }


    @LogInfos({@LogInfo(key = LogKey.MODULE, value = "子单导出")})
    @GetMapping("/export")
    public JsonResultVo export(@PathVariable("source") String source, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String landUserName = request.getParameter("landUserName");
            Asserts.notNull(landUserName,"登录人不能为空");
            UserSource userSource = Assert.enumNotValid(UserSource.class, source, "用户来源");
            if (userSource == UserSource.MANAGEMENT_PLATFORM) {
                receiptUserService.check(landUserName);
                ReceiptUser receiptUser = receiptUserService.getReceiptUserByUserName(landUserName);
                if(receiptUser != null){
                    if(receiptUser.getIsExport() == null || receiptUser.getIsExport() == (byte)0){
                        log.error("登录人{}无权限导出",landUserName);
                        throw new Exception("登录人无权限导出");
                    }
                }
            }
            List<ReceiptChildOrderAmount> list = receiptChildOrderService.findByCond(request);
            log.info("导出功能-此次导出子单列表为:{}",list);
            if(list == null){
                log.error("登录人无关联主体，无法导出");
                throw new Exception("登录人无关联主体，无法导出");
            }
            list = list.stream().filter(x -> ReceiptChildOrderAmountStatus.DEALING == ReceiptChildOrderAmountStatus.getEnum(x.getStatus())
                    || ReceiptChildOrderAmountStatus.INIT == ReceiptChildOrderAmountStatus.getEnum(x.getStatus())
                    || ReceiptChildOrderAmountStatus.FINISH == ReceiptChildOrderAmountStatus.getEnum(x.getStatus()) )
                    .filter(x-> ReceiptChannel.MANUAL == x.getReceiptChannel()).collect(Collectors.toList());
            String ids = request.getParameter("ids");
            if(StringUtils.isNotEmpty(ids)){
                String[] receiptOrderKeysArray = ids.split(",");
                if(receiptOrderKeysArray.length != list.size()){
                    log.error("子单导出数量差异");
                    throw new Exception("子单导出数量差异");
                }
            }
            log.info("发票系统当前导出操作人:{}",landUserName);
            byte[] bytes = receiptChildOrderService.export(list,landUserName);
            if(bytes == null){
                log.error("文件下载失败！");
                throw new Exception("文件下载失败！");
            }
            response.reset();
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=receipt.zip" );
            response.setStatus(HttpStatus.OK.value());
            response.addHeader(HttpHeaders.CONTENT_LENGTH, Integer.toString(bytes.length));
            response.setContentType("application/x-msdownload");

            BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            IOUtils.write(bytes, outputStream);
            outputStream.close();
            return JsonResultVo.success();
        }catch (Exception e){
            log.error("导出功能异常:",e);
            return JsonResultVo.error(CodeConstants.C_10101002.getCode(), e.getMessage());
        }

    }



    @LogInfos({@LogInfo(key = LogKey.MODULE, value = "批量发送邮件")})
    @PostMapping("/sendEmail")
    public JsonResultVo<?> sendEmail( HttpServletRequest request) throws Exception {
        JSONObject jsonObject = convertToJSONObject(request);
        log.info("批量发送邮件请求报文:{}",JSON.toJSONString(jsonObject));
        sendEmailCheck(jsonObject);
        List<ReceiptChildOrderAmount> list = receiptChildOrderService.findByCond(jsonObject);
        if(list == null){
            throw new Exception("查询子单列表失败！");
        }
        receiptChildOrderService.generateSendEmailJob(list);
        return JsonResultVo.success();
    }



    private JSONObject convertToJSONObject( HttpServletRequest request) throws IOException {
        InputStream in = request.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
        StringBuffer sb = new StringBuffer("");
        String temp;
        while ((temp = br.readLine()) != null) {
            sb.append(temp);
        }
        if (in != null) {
            in.close();
        }
        if (br != null) {
            br.close();
        }
        JSONObject jsonObject = JSONObject.parseObject(sb.toString());
        return jsonObject;
    }

    private void sendEmailCheck(JSONObject jsonObject) throws Exception {
        /*发送邮件：
        1、针对查询结果进行发送；
        2、查询条件中：开票状态，必须为已完成；如果选择发送状态为未发送，可以没有其他条件；如果发送状态为已发送，需要包含客户ID或主单ID条件；
        3、发送时候需要按照用户邮箱进行分组发送；发票文件需要去重；*/
        String landUserName = jsonObject.getString("landUserName");
        Assert.notNull(landUserName,"登陆用户名");
        //String uid = jsonObject.getString("uid");
        String receiptOrderKey = jsonObject.getString("receiptOrderKey");
        String status = jsonObject.getString("status");
        String creditor = jsonObject.getString("creditor");
        String priorityLevel = jsonObject.getString("priorityLevel");
        String sendStatus = jsonObject.getString("sendStatus");
        String partnerUserId = jsonObject.getString("partnerUserId");
        //开票状态，必须为已完成；
        if(StringUtils.isEmpty(status) || ReceiptChildOrderAmountStatus.getEnum(Byte.parseByte(status)) != ReceiptChildOrderAmountStatus.FINISH){
            throw new Exception("发送邮件所选子单状态必须为已完成");
        }
        if(StringUtils.isEmpty(sendStatus) || ReceiptStatus.INIT != ReceiptStatus.getEnum(Integer.parseInt(sendStatus))){
            boolean result = StringUtils.checkNotNull(receiptOrderKey,creditor,priorityLevel,partnerUserId);
            if(!result){
                throw new Exception("发送状态不为待发送时，必须有其他的查询条件");
            }
        }

        if(StringUtils.isNotEmpty(sendStatus) && ReceiptStatus.SUCCESS == ReceiptStatus.getEnum(Integer.parseInt(sendStatus))){
            boolean result = StringUtils.checkNotNull(partnerUserId,receiptOrderKey);
            if(!result){
                throw new Exception("发送状态为已发送时，必须填写用户id或主单id");
            }
        }
    }


}
