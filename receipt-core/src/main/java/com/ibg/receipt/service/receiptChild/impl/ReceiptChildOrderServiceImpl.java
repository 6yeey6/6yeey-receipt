package com.ibg.receipt.service.receiptChild.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ibg.receipt.base.constant.ConfigConstants;
import com.ibg.receipt.base.enums.ReceiptUploadInfoStatus;
import com.ibg.receipt.base.exception.Assert;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.vo.PageVo;
import com.ibg.receipt.config.fileSystem.FileSystemConfig;
import com.ibg.receipt.enums.business.*;
import com.ibg.receipt.enums.job.JobMachineStatus;
import com.ibg.receipt.model.receipt.ReceiptBaseInfo;
import com.ibg.receipt.model.receipt.ReceiptOrder;
import com.ibg.receipt.model.receipt.ReceiptRepayDetail;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderAmount;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderBase;
import com.ibg.receipt.model.receiptUploadInfo.ReceiptUploadInfo;
import com.ibg.receipt.service.common.ConfigService;
import com.ibg.receipt.service.common.MetaFileService;
import com.ibg.receipt.service.job.JobService;
import com.ibg.receipt.service.receipt.*;
import com.ibg.receipt.service.receipt.complex.LoanOrderService;
import com.ibg.receipt.service.receiptChild.ReceiptChildOrderAmountService;
import com.ibg.receipt.service.receiptChild.ReceiptChildOrderBaseService;
import com.ibg.receipt.service.receiptChild.ReceiptChildOrderService;
import com.ibg.receipt.service.receiptUploadInfo.ReceiptUploadInfoService;
import com.ibg.receipt.util.*;
import com.ibg.receipt.vo.api.receiptChild.*;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReceiptChildOrderServiceImpl implements ReceiptChildOrderService {
    @Autowired
    private ReceiptChildOrderAmountService receiptChildOrderAmountService;

    @Autowired
    private ReceiptChildOrderBaseService receiptChildOrderBaseService;
    @Autowired
    private ReceiptRepayDetailService receiptRepayDetailService;

    @Autowired
    private JobService jobService;

    @Autowired
    private MetaFileService metaFileService;

    @Autowired
    private FileSystemConfig fileSystemConfig;

    @Autowired
    private ReceiptOrderService receiptOrderService;

    @Autowired
    private ReceiptUploadInfoService receiptUploadInfoService;

    @Autowired
    private ReceiptBaseInfoService receiptBaseInfoService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private CreditorService creditorService;

    @Autowired
    private CreditorBaseConfigService creditorBaseConfigService;

    @Autowired
    private CreditorAmountConfigService creditorAmountConfigService;

    @Autowired
    private LoanOrderService loanOrderService;

    public final static List<String> itemExtInfoList = Arrays.asList("repayInterest","repayFunderOverdueInterest","repayMgmtFee","repayOverdueInterest",
         "repayOverdueMgmtFee","repayInRepayFee","repayGuaranteeFee","repayCommutation");

    @Override
    public PageVo<ReceiptChildListResponseVo> list(ReceiptChildListRequestVo vo) {
        PageVo<ReceiptChildOrderAmount> amountList =  receiptChildOrderAmountService.page(vo,vo.getPageNum(),vo.getPageSize());
        if(amountList == null){
            log.info("无对应主体，子单列表默认返回空");
            return new PageVo(null, null, vo.getPageNum(), vo.getPageSize());
        }
        //PageVo pageVo = new PageVo(amountList, ReceiptChildOrderAmount.class, vo.getPageNum(), vo.getPageSize());
        List<ReceiptChildOrderAmount> list = amountList.getList();
        if (CollectionUtils.isEmpty(list)) {
            return new PageVo(null, null, vo.getPageNum(), vo.getPageSize());
        }
        List<ReceiptChildListResponseVo> responseVos = buildResponse(list,false);
//        List<ReceiptChildListResponseVo> responseVos = buildResponse(list,false);
        //按照标识筛选
        /*if(vo.getPriorityLevel()!= null){
            responseVos.stream().filter(x -> vo.getPriorityLevel().compareTo(x.getPriorityLevel()) == 0).collect(Collectors.toList());
        }
        //发票平台才有校验;登录人不是管理员角色,只能查看自己创建的主体类
        if(vo.getLandUserName()!= null && !configService.hitKey(ConfigConstants.RECEIPT_ADMIN,vo.getLandUserName())){
            //当前人查询主题列表
            List<String> creditorList = creditorService.findByUserNameAndDeleted(vo.getLandUserName(),false).stream().map(x-> x.getCreditor().name()).collect(Collectors.toList());
            responseVos = creditorList.size() > 0 ? responseVos.stream().filter(x ->  creditorList.contains(x.getCreditor())).collect(Collectors.toList()) : null;
        }*/
        PageVo newPageVo = new PageVo();
        newPageVo.setPageNum(vo.getPageNum());
        newPageVo.setPageSize(vo.getPageSize());
        newPageVo.setList(responseVos);
        newPageVo.setTotal(amountList.getTotal());
        return newPageVo;
    }

    /**
     * 贷款明细查询
     * @param vo
     * @return
     */
    @Override
    public ReceiptChildLoanDetailResponseVo queryloanDetail(ReceiptChildLoanDetailRequestVo vo) {
        Assert.notNull(vo.getReceiptOrderKey(),"工单key");
        List<ReceiptChildOrderAmount> list = receiptChildOrderAmountService.findByReceiptOrderKey(vo.getReceiptOrderKey());
        Assert.notEmpty(list,"子单信息");
        Map<String,List<ReceiptChildOrderAmount>> loanIds = list.stream().collect(Collectors.groupingBy(ReceiptChildOrderAmount::getLoanId));
        ReceiptOrder receiptOrder = receiptOrderService.findByReceiptOrderKey(vo.getReceiptOrderKey());
        //主体+loanId 基础信息集合
        return buildReceiptChildLoanDetailResponseVo(list,loanIds,receiptOrder.getUserName());
    }

    @Override
    public List<ReceiptChildOrderAmount> findByReceiptChildOrderKeyIn(String[] receiptChildOrderKeys) {

        return receiptChildOrderAmountService.findByReceiptChildOrderKeyInAndStatus(receiptChildOrderKeys, ReceiptChildOrderAmountStatus.DEALING.getStatus());
    }

    @Override
    public List<String> uploadReceipt(MultipartFile[] multipartFile, HttpServletRequest request, String uploadBatchNo,String landUserName) throws Exception {

        List<String> errorFileList = new ArrayList<>();
        try {
            //支持复选
            /*String[] idArr = null;
            String ids = request.getParameter("ids");
            if(StringUtils.isNotEmpty(ids)){
                idArr = ids.split(";");
            }
            if(idArr != null && idArr.length != 0){
                //以勾选的子单id集合更新
                if(multipartFile == null || multipartFile.length != 1){
                    throw  new Exception("选取多个子单上传发票影像件，必须且只能上传一个文件");
                }
                List<ReceiptChildOrderAmount> list = receiptChildOrderAmountService
                        .findByIdInAndStatus(idArr,ReceiptChildOrderAmountStatus.DEALING.getStatus());
                if(list.size() != idArr.length){
                    throw  new Exception("可上传的子单和勾选子单数目不一致");
                }
                MultipartFile file = multipartFile[0];
                String fileName = file.getOriginalFilename();
                byte [] byteArr=file.getBytes();
                InputStream inputStream = new ByteArrayInputStream(byteArr);
                //InputStream fileStream = stream.openStream();
                String metaFileId = metaFileService.uploadToMetaFs( inputStream, fileName,
                        null, null, "RECEIPT");
                String fileUrl = fileSystemConfig.getReceiptFileDownloadUrl() + metaFileId;

            }else{
                //以文件名解析
            }*/
            Map<String,byte[]> fileMap = new HashMap();
            for (MultipartFile file:multipartFile) {
                try {
                    fileMap.put(file.getOriginalFilename(), file.getBytes());
                } catch (IOException ioException) {
                    log.error("批次号:{},上传文件异常！",uploadBatchNo,ioException);
                    throw new Exception("批次号"+uploadBatchNo+"上传异常");
                }
            }
            //List<ReceiptUploadInfo> uploadInfos = new ArrayList<>();
            for (String fileName:fileMap.keySet()){
                byte[] fileBytes = fileMap.get(fileName);
                InputStream inputStream = new ByteArrayInputStream(fileBytes);
                String[] suffixs = fileName.split("\\.");

                String metaFileId = metaFileService.uploadToMetaFs( inputStream,
                        DateUtils.getFormatDate(new Date(),DateUtils.DATE_TIME_NO_BLANK_FORMAT) + "." + suffixs[suffixs.length-1],
                        null, null, "RECEIPT");
                String fileUrl = fileSystemConfig.getReceiptFileDownloadUrl() + metaFileId;

                //借款1+借款2+借款3;服务费+担保费（资金项code）.jpg
                String[] receiptItems = fileName.substring(0, fileName.indexOf(".")).split(";");
                if(receiptItems != null && receiptItems.length == 2){
                    String[] loanIds = receiptItems[0].split("\\+");
                    String[] receiptItemCodes = receiptItems[1].split("\\+");
                    String[] receiptItemCodesEnum = new String[receiptItemCodes.length];
                    for(int i =0;i< receiptItemCodes.length;i++){
                        receiptItemCodesEnum[i] = ReceiptItemCodeAmount.getEnumByDesc(receiptItemCodes[i]).name();
                    }
                    List<ReceiptChildOrderAmount> list = receiptChildOrderAmountService.findByLoanIdInAndReceiptItemCodeIn(loanIds,receiptItemCodesEnum);
                    if(list.size() != loanIds.length*receiptItemCodes.length){
                        log.warn("上传批次{}应关联子单数量:{}，实际子单数量{}不一致",uploadBatchNo,loanIds.length*receiptItemCodes.length,list.size());
                    }
                    for (String loanId:loanIds){
                        for (String receiptItemCode:receiptItemCodesEnum){
                            List<ReceiptChildOrderAmount> list1 = new ArrayList<>();
                            list1 = list.stream()
                                    .filter(x->x.getLoanId().equals(loanId) && x.getReceiptItemCode().name().equals(receiptItemCode))
                                    .collect(Collectors.toList());
                            if(list1.size() != 0){
                                for(ReceiptChildOrderAmount receiptChildOrderAmount:list1){
                                    if(receiptChildOrderAmount != null){
                                        receiptChildOrderAmount.setStatus((byte)2);
                                        receiptChildOrderAmountService.update(receiptChildOrderAmount);
                                        //应上传数据匹配成功
                                        ReceiptUploadInfo temp =ReceiptUploadInfo.builder().uploadBatchNo(uploadBatchNo).loanId(receiptChildOrderAmount.getLoanId())
                                                .receiptItemCode(receiptChildOrderAmount.getReceiptItemCode())
                                                .receiptChildOrderKey(receiptChildOrderAmount.getReceiptChildOrderKey())
                                                .status(ReceiptUploadInfoStatus.SUCC.getStatus()).fileName(fileName)
                                                .receiptUrl(fileUrl).receiptFileId(metaFileId)
                                                .receiptUserId(landUserName)
                                                .uploadTime(new Date())
                                                .build();
                                        receiptUploadInfoService.save(temp);

                                        ChildOrderSuccessVo childOrderSuccessVo = new ChildOrderSuccessVo();
                                        childOrderSuccessVo.setReceiptUrl(fileUrl);
                                        childOrderSuccessVo.setReceiptFileId(metaFileId);
                                        jobService.generateJob(JobMachineStatus.CHILD_ORDER_SUCCESS, receiptChildOrderAmount.getReceiptChildOrderKey(), JsonUtils.toJson(childOrderSuccessVo),new Date(),null);
                                    }
                                }
                            }else{
                                //应上传数据匹配失败
                                ReceiptUploadInfo temp = ReceiptUploadInfo.builder().uploadBatchNo(uploadBatchNo).loanId(loanId)
                                        .receiptItemCode(ReceiptItemCodeAmount.getEnum(receiptItemCode))
                                        .status(ReceiptUploadInfoStatus.FAIL.getStatus()).fileName(fileName)
                                        .receiptUrl(fileUrl).receiptFileId(metaFileId)
                                        .receiptUserId(landUserName)
                                        .uploadTime(new Date())
                                        .build();
                                receiptUploadInfoService.save(temp);
                                //uploadInfos.add(temp);
                            }
                        }
                    }
                    jobService.generateJob(JobMachineStatus.UPLOAD_FINISH, uploadBatchNo, "",new Date(),null);
                }else{
                    throw  new Exception("上传文件命名格式错误！参考格式：loanId1+loanId2;服务费+担保费.jpg/pdf");
                }
            }

            /*InputStream inputStream = new ByteArrayInputStream(byteArr);
            //借款1+借款2+借款3;服务费+担保费（资金项code）.jpg
            String[] receiptItems = fileName.substring(0, fileName.indexOf(".")).split(";");
            if(receiptItems != null && receiptItems.length == 2){
                String[] loanIds = receiptItems[0].split("\\+");
                String[] receiptItemCodes = receiptItems[1].split("\\+");
                List<ReceiptChildOrderAmount> list = receiptChildOrderAmountService.findByLoanIdInAndReceiptItemCodeIn(loanIds,receiptItemCodes);
                if(list.size() != loanIds.length*receiptItemCodes.length){
                    log.warn("上传批次{}应关联子单数量:{}，实际子单数量{}不一致",uploadBatchNo,loanIds.length*receiptItemCodes.length,list.size());

                }
            }else{
                throw  new Exception("上传文件命名格式错误！参考格式：loanId1+loanId2;服务费+担保费.jpg/pdf");
            }
                String[] receiptChildOrderKeys = fileName.substring(0, fileName.indexOf(".")).split(";");
                List<ReceiptChildOrderAmount> list = receiptChildOrderAmountService.findByReceiptChildOrderKeyInAndStatus(receiptChildOrderKeys, ReceiptChildOrderAmountStatus.DEALING.getStatus());
                if (list.size() != receiptChildOrderKeys.length) {
                    errorFileList.add(fileName);
                    continue;
                }
                //InputStream fileStream = stream.openStream();
                String metaFileId = metaFileService.uploadToMetaFs( inputStream, fileName,
                        null, null, "RECEIPT");
                String fileUrl = fileSystemConfig.getReceiptFileDownloadUrl() + metaFileId;
                list.forEach(receiptChildOrderAmount -> {
                    ChildOrderSuccessVo childOrderSuccessVo = new ChildOrderSuccessVo();
                    childOrderSuccessVo.setReceiptUrl(fileUrl);
                    childOrderSuccessVo.setReceiptFileId(metaFileId);
                    jobService.generateJob(JobMachineStatus.CHILD_ORDER_SUCCESS, receiptChildOrderAmount.getReceiptChildOrderKey(), JsonUtils.toJson(childOrderSuccessVo),new Date(),null);
                });
            }*/
        } catch (Exception e) {
            log.error("发票系统上传文件异常!",e);
            NoticeUtils.businessError("发票系统上传文件异常!");
            throw new Exception("发票系统上传文件异常!");
        }
        return  errorFileList;
    }

    @Override
    public Map<String, byte[]> downloadFileFromFs(String metaFileId) {
        /*try {
            Map<String, byte[]> map = MetaFsUtil.downloadFromMetaFs(metaFileId);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }*/
        return null;
    }

    @Override
    public byte[] export(List<ReceiptChildOrderAmount> list,String landUserName) throws Exception {
        String date = DateUtils.getFormatDate(new Date(),"yyyyMMdd");
        //按照压缩路径拆分
        Map<CreditorEnum,Map<ReceiptItemCodeAmount,Map<String,List<ReceiptChildOrderAmount>>>> map= list.stream()
                .collect(Collectors.groupingBy(ReceiptChildOrderAmount::getCreditor,
                        Collectors.groupingBy(ReceiptChildOrderAmount::getReceiptItemCode,Collectors.groupingBy(ReceiptChildOrderAmount::getPartnerUserId))));
        //存储压缩包的所有文件的路径
        Map<String,byte[]> fileMap = new HashMap<>();

        for(CreditorEnum creditorEnum : map.keySet()){
            //主体名
            String creditorName = creditorEnum.getDesc();
            Map<ReceiptItemCodeAmount,Map<String,List<ReceiptChildOrderAmount>>> receiptItemCodeAmountMap = map.get(creditorEnum);
            for (ReceiptItemCodeAmount receiptItemCodeAmount: receiptItemCodeAmountMap.keySet()){
                List<ReceiptChildListResponseVo> allAist = new ArrayList<>();
                //资金项名
                String receiptItemCodeAmountDesc = receiptItemCodeAmount.getDesc();
                //生成总excel

                //按照用户维度生成文件
                Map<String,List<ReceiptChildOrderAmount>> uidMap = receiptItemCodeAmountMap.get(receiptItemCodeAmount);
                for(String uid:uidMap.keySet()){
                    List<ReceiptChildOrderAmount> amountList = uidMap.get(uid);
                    String receiptOrderKey = amountList.get(0).getReceiptOrderKey();
                    ReceiptOrder receiptOrder = receiptOrderService.findByReceiptOrderKey(receiptOrderKey);
                    String userName = EncryptUtil.getDecoded(receiptOrder.getUserName());
                    //拼装sheet1 基础信息、合同信息
                    List<ReceiptChildListResponseVo> responseVos = buildResponse(amountList,true);
                    log.info("发票导出功能所有构造信息为:{}", JSON.toJSONString(responseVos));
                    for (ReceiptChildListResponseVo x:responseVos){
                        //String url = "";
                        /**
                         * yyyyMMdd/creditor-itemcode/-file-/username-uid/*.pdf/*.jpg
                         */
                        String filePath = "%s/%s/file/%s/%s";
                        if(StringUtils.isNotEmpty(x.getGuaranteeServiceContractPath())){
                            getFileMap(fileMap,x, "GUARANTEE_SERVICE_CONTRACT.pdf", x.getGuaranteeServiceContractPath(), filePath, date, creditorName, receiptItemCodeAmountDesc, userName, receiptOrder);

                        }
                        if(StringUtils.isNotEmpty(x.getIdCardBackPath())){
                            getFileMap(fileMap,x, "ID_CARD_BACK.jpg", x.getIdCardBackPath(), filePath, date, creditorName, receiptItemCodeAmountDesc, userName, receiptOrder);
                        }
                        if(StringUtils.isNotEmpty(x.getIdCardFrontPath())){
                            getFileMap(fileMap,x, "ID_CARD_FRONT.jpg", x.getIdCardFrontPath(), filePath, date, creditorName, receiptItemCodeAmountDesc, userName, receiptOrder);
                        }
                        if(StringUtils.isNotEmpty(x.getLoanContractPath())){
                            getFileMap(fileMap,x, "LOAN_CONTRACT.pdf", x.getLoanContractPath(), filePath, date, creditorName, receiptItemCodeAmountDesc, userName, receiptOrder);
                        }
                        if(StringUtils.isNotEmpty(x.getInsureLetterPath())){
                            getFileMap(fileMap,x, "INSURE_LETTER.pdf", x.getInsureLetterPath(), filePath, date, creditorName, receiptItemCodeAmountDesc, userName, receiptOrder);
                        }
                        if(StringUtils.isNotEmpty(x.getGatherAuthLetter())){
                            getFileMap(fileMap,x, "GATHER_AUTH_LETTER.pdf", x.getGatherAuthLetter(), filePath, date, creditorName, receiptItemCodeAmountDesc, userName, receiptOrder);
                        }
                    }
                    allAist.addAll(responseVos);
                }
                //构建sheet1
                String sheetName = "子单明细";
                List<String> titles = Lists.newArrayList("姓名","电话号码", "身份证号", "地址",
                        "资金方借款编号","进件编号","资金方借款编号（长银）","资金方名称（放款主体）","期限","放款时间","结清时间",
                        "放款金额","开票主体","开票资金项","开票金额","信托计划名称","资金平台借款状态","还款金额","老长银资方借据号","还款状态");
                List<String> cellList = Lists.newArrayList("userName","account",  "userPid", "address","funderLoanKey",
                        "loanId","funderLoanKeyCyOld","fundName","period","loanTime","payoffTime",
                        "loanAmount","creditor","receiptItemName","receiptAmount","trustName","fundStatus","repayAmount","funderLoanKeyCyOld","repayStatus");
                List<String> needRepayDetailsList = allAist.stream()
                        .filter(x -> StringUtils.equals("1",x.getNeedRepayDetail())).map(ReceiptChildListResponseVo::getLoanId).distinct()
                        .collect(Collectors.toList());
                //查询配置表，哪些主体需要传输repayDetail
                List<ReceiptRepayDetail> receiptRepayDetailList = new ArrayList<>();
                List<ReceiptRepayDetail> receiptRepayDetails = receiptRepayDetailService.findByLoanIdIn(needRepayDetailsList);
                //排序重新展示,按loan_id分组,并升序排列
                Map<String, List<ReceiptRepayDetail>> receiptRepayDetailMap = receiptRepayDetails.stream().collect(Collectors.groupingBy(ReceiptRepayDetail::getLoanId));
                for (String key:receiptRepayDetailMap.keySet()){
                    receiptRepayDetailList.addAll(receiptRepayDetailMap.get(key).stream().sorted(Comparator.comparing(ReceiptRepayDetail::getPeriod)).collect(Collectors.toList()));
                }
                //构建sheet2
                String sheetName2 = "还款明细";
                List<String> titles2 = Lists.newArrayList("订单号","期次", "结清时间", "本金", "利息",
                        "服务费", "资金方罚息", "逾期罚息",
                        "逾期管理费","担保费","提前结清违约金","代偿金","宽限期利息","逾期担保费");
                List<String> cellList2 = Lists.newArrayList("loanId","period", "payoffTime", "principal", "interest",
                        "mgmtFee", "fundOverdueInterest", "overdueInterest",
                        "overdueMgmtFee","guaranteeFee","inRepayFee","commutation","gracePeriodInterest","repayOverdueGuaranteeFee");

                List<String> cellLists = this.getCellList(creditorEnum,allAist);
                log.info("导出表单主体:{};列表:{};操作人:{}",creditorEnum.name(),cellLists,landUserName);
                List<String> titleList = this.getTitleList(cellLists);
                log.info("导出表单中文名称:{}",titleList);
                //遍历responseVos,将所有合同下载后保存file
                XSSFWorkbook workbook = this.generateExcel(sheetName, titleList, cellLists, allAist,
                        sheetName2, titles2, cellList2, receiptRepayDetailList);
                String fileName = "子单明细.xlsx";
                /**
                 * yyyyMMdd/creditor-itemcode/*.xls
                 */
                String excelPath = "%s/%s/%s";
                excelPath = String.format(excelPath,date,creditorName.concat("-").concat(receiptItemCodeAmountDesc),
                        fileName);
                fileMap.put(excelPath,ExcelUtils.excelToByte(workbook));
            }
        }
        String randomStr = DateUtils.getFormatDate(new Date(),DateUtils.TIME_FORMAT_PATTERN_COLON);
        List<File> filePath = new ArrayList<>();
        List<String> childPath = new ArrayList<>();
        String password = PasswordUtil.getPassWord(8);
        for (Map.Entry<String, byte[]> entry : fileMap.entrySet()) {
            String fileName = FileUtils.getTempDirectoryPath() + File.separator + randomStr + File.separator+ entry.getKey();
            File file = new File(fileName);
            log.info("子文件路径:{}",fileName);
            FileUtils.writeByteArrayToFile(file, entry.getValue());
            filePath.add(file);
            childPath.add(entry.getKey());
        }
        //ZipFile zipFile = ZipOutputStreamUtil.zipFilesAndEncrypt(filePath, filePath.get(0).getAbsolutePath(),password);
        String path = filePath.get(0).getAbsolutePath().substring(0, filePath.get(0).getAbsolutePath().lastIndexOf("/", filePath.get(0).getAbsolutePath().lastIndexOf("/") - 1));
        String finalPath = FileUtils.getTempDirectoryPath() + File.separator + randomStr + File.separator + childPath.get(0).substring(0,8);
        ZipFile zipFile = new ZipFile(finalPath + File.separator+ "receipt.zip");
        zipFile.setFileNameCharset("gbk");
        log.info("zip文件路径:{}", finalPath + File.separator + "receipt.zip");
        //若存在此目录,则删除
        if (zipFile.getFile().exists()){
            zipFile.getFile().delete();
        }
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);//压缩方式
        //设置压缩级别
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);//压缩级别
        if (password != null && password != "") {
            parameters.setEncryptFiles(true);//设置压缩文件加密
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);//加密方式
            parameters.setPassword(password);//设置加密密码
        }
        log.info("要打包的文件夹:{}", finalPath);
        zipFile.addFolder(finalPath, parameters);
        log.info("导出文件大小:{}", zipFile.toString());

        //将excel/文件统一打包压缩
        //byte[] zipByte = ZipUtils.compressZipFiles(fileMap);
        //log.info("路径:{}",FileUtils.getTempDirectoryPath());
        //log.info("分隔符:{}",File.separator);
        //加密
        //File file = new File(StringUtils.join(
        //        new String[] { FileUtils.getTempDirectoryPath(), "receipt.zip" },
        //        File.separator));
        //FileUtils.writeByteArrayToFile(file, zipByte);
        //String password = PasswordUtil.getPassWord(8);
        //String zip = CompressUtil.zip(file.getAbsolutePath(), password);
        //File fileEn=new File(zip);
        //更新子单状态为处理中
        //未完成的状态修改为处理中
        List<ReceiptChildOrderAmount> result = list.stream().filter(x-> x.getStatus().compareTo(ReceiptChildOrderAmountStatus.FINISH.getStatus()) != 0).collect(Collectors.toList());
        result.stream().filter(x->x.getStatus() == ReceiptChildOrderAmountStatus.INIT.getStatus()).forEach(x -> {
            x.setStatus(ReceiptChildOrderAmountStatus.DEALING.getStatus());
            receiptChildOrderAmountService.update(x);
        });
        JSONObject param = new JSONObject();
        param.put("landUserName",landUserName);
        //发送文件密码邮件
        jobService.generateJob(JobMachineStatus.SEND_PASSWORD_EMAIL, EncryptUtil.getEncoded(password), param.toJSONString(),new Date(),null);

        return FileUtils.readFileToByteArray(zipFile.getFile());
    }

    /**
     * 拼装title名称
     *
     * @param creditorEnum
     * @return
     */
    private List<String> getCellList(CreditorEnum creditorEnum,List<ReceiptChildListResponseVo> allAist) {
        //该主体配置资金项
        //List<String> creditorAmountConfigList = creditorAmountConfigService.findByCreditorAndDeleted(creditorEnum, false).stream().map(x -> x.getReceiptItemCode().name()).collect(Collectors.toList());
        ////基本信息字段配置
        List<String> creditorBaseConfigList = creditorBaseConfigService.findByCreditorAndDeleted(creditorEnum, false)
                .stream().filter(e -> ItemType.STRING.equals(e.getItemType())).map(x -> x.getReceiptItemCode().getCode()).collect(Collectors.toList());
        List<String> customList =  allAist.stream().map(x -> x.getReceiptItemCode()).collect(Collectors.toList());
        log.info("该主体配置的资金项列表:{}",customList);
        log.info("该主体数据源资金项:{}",creditorBaseConfigList);
        if (customList.contains(ReceiptItemCodeAmount.INTEREST_FEE.getCode())) {
            log.info("导出主体:{},需增加息费字段!",creditorEnum.name());
            creditorBaseConfigList.add("repayInterest");
            creditorBaseConfigList.add("repayFunderOverdueInterest");
        }
        if (customList.contains(ReceiptItemCodeAmount.TOTAL_SERVICE_FEE.getCode())) {
            log.info("导出主体:{},需增加总服务费字段!",creditorEnum.name());
            creditorBaseConfigList.add("repayMgmtFee");
            creditorBaseConfigList.add("repayOverdueInterest");
            creditorBaseConfigList.add("repayOverdueMgmtFee");
            creditorBaseConfigList.add("repayInRepayFee");
        }
        if (customList.contains(ReceiptItemCodeAmount.TOTAL_GUARANTOR_FEE.getCode())) {
            log.info("导出主体:{},需增加总担保费字段!",creditorEnum.name());
            creditorBaseConfigList.add("repayGuaranteeFee");
            creditorBaseConfigList.add("repayCommutation");
        }
        //去掉导出excel中的实还明细字段
        if(creditorBaseConfigList.contains(ReceiptItemCodeBase.REPAY_DETAIL.getCode())){
            log.info("该主体包含实还明细字段");
            creditorBaseConfigList.remove(ReceiptItemCodeBase.REPAY_DETAIL.getCode());
        }
        return creditorBaseConfigList;
    }

    /**
     * 拼装title名称
     *
     * @param cellList
     * @return
     */
    private List<String> getTitleList(List<String> cellList) {

        List<String> result = new ArrayList<>();
        for (int i = 0;i<cellList.size();i++){
            result.add(ReceiptItemCodeBase.getEnumByCode(cellList.get(i)));
        }
        if (cellList.contains(ReceiptItemCodeAmount.INTEREST.getCode())) {
            result.add(ReceiptItemCodeAmount.INTEREST.getDesc());
        }
        if (cellList.contains(ReceiptItemCodeAmount.FUNDER_OVERDUE_INTEREST.getCode())) {
            result.add(ReceiptItemCodeAmount.FUNDER_OVERDUE_INTEREST.getDesc());
        }

        if (cellList.contains(ReceiptItemCodeAmount.MGMT_FEE.getCode())) {
            result.add(ReceiptItemCodeAmount.MGMT_FEE.getDesc());
        }

        if (cellList.contains(ReceiptItemCodeAmount.OVERDUE_INTEREST.getCode())) {
            result.add(ReceiptItemCodeAmount.OVERDUE_INTEREST.getDesc());
        }

        if (cellList.contains(ReceiptItemCodeAmount.OVERDUE_MGMT_FEE.getCode())) {
            result.add(ReceiptItemCodeAmount.OVERDUE_MGMT_FEE.getDesc());
        }

        if (cellList.contains(ReceiptItemCodeAmount.IN_REPAY_FEE.getCode())) {
            result.add(ReceiptItemCodeAmount.IN_REPAY_FEE.getDesc());
        }
        if (cellList.contains(ReceiptItemCodeAmount.GUARANTEE_FEE.getCode())) {
            result.add(ReceiptItemCodeAmount.GUARANTEE_FEE.getDesc());
        }
        if (cellList.contains(ReceiptItemCodeAmount.COMMUTATION.getCode())) {
            result.add(ReceiptItemCodeAmount.COMMUTATION.getDesc());
        }
        return result.stream().filter(x->!Objects.isNull(x)).collect(Collectors.toList());
    }

    /**
     * 存储合同
     * @param fileMap
     * @param x
     * @param pdfName
     * @param url
     * @param filePath
     * @param date
     * @param creditorName
     * @param receiptItemCodeAmountDesc
     * @param userName
     * @param receiptOrder
     */
    public void getFileMap(Map<String,byte[]> fileMap,ReceiptChildListResponseVo x,String pdfName,String url,String filePath,String date,String creditorName,String receiptItemCodeAmountDesc,String userName,ReceiptOrder receiptOrder){

        String fileName = x.getLoanId().concat("-").concat(x.getCreditor()).concat("-").concat(pdfName);
        byte[] fileByte = FileUtils.getByteArrayContent(url);
        filePath = String.format(filePath,date,creditorName.concat("-").concat(receiptItemCodeAmountDesc),
                userName.concat(receiptOrder.getUid()),fileName);
        fileMap.put(filePath,fileByte);
    }

    @Override
    public List<ReceiptChildOrderAmount> findByCond(HttpServletRequest request) throws Exception {
        try {
            String ids = request.getParameter("ids");
            if (StringUtils.isNotEmpty(ids)) {
                log.info("发票导出功能主单号:{}", ids);
                String[] receiptOrderKeysArray = ids.split(",");
            /*List<ReceiptChildOrderAmount> list = receiptChildOrderAmountService.findByIdInAndStatusIn(receiptOrderKeysArray,
                    Arrays.asList(ReceiptChildOrderAmountStatus.INIT.getStatus(),ReceiptChildOrderAmountStatus.DEALING.getStatus()));*/
                List<ReceiptChildOrderAmount> list = receiptChildOrderAmountService.findByIdIn(receiptOrderKeysArray);
                return list;
            } else {
                log.info("creditors为:{}", request.getParameter("creditors"));
                log.info("statusList为:{}", request.getParameter("statusList"));
                String creditors = request.getParameter("creditors");
                String status = request.getParameter("statusList");
                List<String> creditorList = new ArrayList<>();
                List<String> statusList = new ArrayList<>();
                if(StringUtils.isNotBlank(creditors)){
                    creditorList = Arrays.asList(creditors.split(","));
                    log.info("导出此次主体为:{}",creditorList);
                    //creditorEnums = creditorList.stream().map(x -> Assert.enumNotValid(CreditorEnum.class, x,
                    //        "主体")).collect(Collectors.toList());
                }
                if(StringUtils.isNotBlank(status)){
                    statusList = Arrays.asList(status.split(","));
                    log.info("导出此次状态为:{}",statusList);
                    //creditorEnums = statusList.stream().map(x -> ).collect(Collectors.toList());
                }
                //request.getParameterValues("creditors");
                //List<String> creditors = Arrays.asList(request.getParameterValues("creditorsww"));
                log.info("导出功能未传主单号!");
                log.info("landUserName为:{}", request.getParameter("landUserName"));
                log.info("uid为:{}", request.getParameter("uid"));
                log.info("receiptOrderKey为:{}", request.getParameter("receiptOrderKey"));
                log.info("status为:{}", request.getParameter("status"));
                log.info("creditor为:{}", request.getParameterValues("creditors"));
                log.info("statusList为:{}", request.getParameterValues("statusList"));
                log.info("priorityLevel为:{}", request.getParameter("priorityLevel"));
                log.info("sendStatus为:{}", request.getParameter("sendStatus"));
                //登陆名
                String landUserName = request.getParameter("landUserName");
                String uid = request.getParameter("uid");
                String receiptOrderKey = request.getParameter("receiptOrderKey");
                //String status = request.getParameter("status");
                String creditor = request.getParameter("creditor");
                String priorityLevel = request.getParameter("priorityLevel");
                String sendStatus = request.getParameter("sendStatus");
                String partnerUserId = request.getParameter("partnerUserId");
                String receiptChildOrderKey = request.getParameter("receiptChildOrderKey");
                ReceiptOrderAmountCondVo receiptOrderAmountCondVo = ReceiptOrderAmountCondVo.builder()
                        .landUserName(landUserName).uid(uid).receiptOrderKey(receiptOrderKey).statusList(statusList)
                        .creditorList(creditorList).priorityLevel(StringUtils.isNotEmpty(priorityLevel) ? Integer.parseInt(priorityLevel) : null)
                        .sendStatus(sendStatus).partnerUserId(partnerUserId).receiptChildOrderKey(receiptChildOrderKey).build();
                Specification cond = buildReceiptOrderAmountSpecification(receiptOrderAmountCondVo);
                if (cond == null) {
                    return null;
                }
                return receiptChildOrderAmountService.findAll(cond);
            }
        }catch (Exception e){
            log.error("导出功能通过查询条件查询异常!");
            throw new Exception("导出功能通过查询条件查询异常:"+e);
        }
    }

    @Override
    public List<ReceiptChildOrderAmount> findByCond(JSONObject jsonObject) {
        ReceiptOrderAmountCondVo receiptOrderAmountCondVo = jsonObject.toJavaObject(ReceiptOrderAmountCondVo.class);
        log.info("发送邮件请求参数:{}", JSON.toJSONString(receiptOrderAmountCondVo));
        Specification cond = buildReceiptOrderAmountSpecification(receiptOrderAmountCondVo);
        if(cond==null){
            return null;
        }
        return receiptChildOrderAmountService.findAll(cond);
    }

    @Override
    public void generateSendEmailJob(List<ReceiptChildOrderAmount> list) {
        list = list.stream()
                .filter(x-> x.getReceiptChannel() == ReceiptChannel.MANUAL ||  x.getReceiptChannel() == ReceiptChannel.NUONUO).collect(Collectors.toList());
        //按照用户分组
        Map<String,List<ReceiptChildOrderAmount>> map = list.stream().collect(Collectors.groupingBy(ReceiptChildOrderAmount::getPartnerUserId));
        //遍历用户，进行影像文件的去重
        map.forEach((k,v)->{
            List<String> receiptChildOrderKeys = v.stream().map(ReceiptChildOrderAmount::getReceiptChildOrderKey).collect(Collectors.toList());
            JSONObject param = new JSONObject();
            param.put("partnerUserId",k);
            param.put("receiptChildOrderKeys", JSONArray.toJSONString(receiptChildOrderKeys));
            String sendEmailBatchNo = SerialNoGenerator.generateSendSerialNo();
            log.info("uid:{},发送发票任务key:{}",k,sendEmailBatchNo);
            jobService.generateJob(JobMachineStatus.SEND_RECEIPT_EMAIL, sendEmailBatchNo, param.toJSONString(),new Date(),null);
        });
    }

    private Specification buildReceiptOrderAmountSpecification(ReceiptOrderAmountCondVo vo) {
        if(StringUtils.isNotEmpty(vo.getIds())){
            String[] ids = vo.getIds().split(",");
            Specification specification = (Specification<ReceiptOrderAmountCondVo>) (root, criteriaQuery, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                Path<Object> path = root.get("id");
                CriteriaBuilder.In<Object> in = criteriaBuilder.in(path);
                for (int i = 0; i < ids.length; i++) {
                    in.value(ids[i]);
                }
                predicates.add(criteriaBuilder.and(criteriaBuilder.and(in)));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])); };
            return specification;
        }else{
            log.info("导出查询特殊主体的子单列表");
            Specification specification = (Specification<ReceiptOrderAmountCondVo>) (root, criteriaQuery, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if(StringUtils.isNotBlank(vo.getLandUserName())){
                    //发票平台才有校验;登录人不是管理员角色,只能查看自己创建的主体类
                    if(!configService.hitKeyNew(ConfigConstants.RECEIPT_ADMIN,vo.getLandUserName())) {
                        //当前人查询主题列表
                        //List<String> creditorList = creditorService.findByUserNameAndDeleted(vo.getLandUserName(), false).stream().map(x -> x.getCreditor().name()).collect(Collectors.toList());
                        List<CreditorEnum> creditorList = creditorService.findByUserNameAndDeleted(vo.getLandUserName(), false).stream().map(x -> x.getCreditor()).collect(Collectors.toList());
                        /*if (creditorList != null && creditorList.size() > 0) {
                            Path<Object> path = root.get("creditor");
                            CriteriaBuilder.In<Object> in = criteriaBuilder.in(path);
                            for (int i = 0; i < creditorList.size(); i++) {
                                in.value(creditorList.get(i).name());
                            }
                            predicates.add(in);*/
                        if (creditorList != null && creditorList.size() > 0) {
                            log.info("导出功能非管理员只能导出自己负责主体项目!creditorList:{}",creditorList);
                            //导出功能所传递的主体枚举
                            List<CreditorEnum> result = new ArrayList<>();
                            List<CreditorEnum> creditorEnums = new ArrayList<>();
                            if (vo.getCreditorList() != null && vo.getCreditorList().size() > 0) {
                                log.info("导出功能非管理员想导出的主体creditorEnums:{}", vo.getCreditorList());
                                creditorEnums = vo.getCreditorList().stream().map(x -> Assert.enumNotValid(CreditorEnum.class, x,
                                        "主体")).collect(Collectors.toList());

                                for (int i = 0; i < creditorList.size(); i++) {
                                    for (int j = 0; j < creditorEnums.size(); j++) {
                                        if (creditorList.get(i).equals(creditorEnums.get(j))){
                                            result.add(creditorList.get(i));
                                        }
                                    }
                                }
                                log.info("非管理员导出功能交集主体为:{}",result);
                                Predicate ins = root.get("creditor").in(result);
                                predicates.add(ins);
                            }else{
                                log.info("非管理员查询子单主体为:{}",creditorList);
                                Predicate ins = root.get("creditor").in(creditorList);
                                predicates.add(ins);
                            }
                        }else{
                            return null;
                        }
                    }else {
                        //导出
                        if(vo.getCreditorList() != null && vo.getCreditorList().size() > 0){
                            log.info("导出文件主体不为空:{}",vo.getCreditorList());
                            List<CreditorEnum> creditorEnums = vo.getCreditorList().stream().map(x -> Assert.enumNotValid(CreditorEnum.class, x,
                                    "主体")).collect(Collectors.toList());
                            Predicate ins = root.get("creditor").in(creditorEnums);
                            predicates.add(ins);
                            //predicates.add(criteriaBuilder.equal(root.get("creditor"), CreditorEnum.valueOf(vo.getCreditor())));
                        }
                    }
                }
                if(StringUtils.isNotBlank(vo.getReceiptChildOrderKey())){
                    predicates.add(criteriaBuilder.equal(root.get("receiptChildOrderKey"), vo.getReceiptChildOrderKey()));
                }
                if(StringUtils.isNotBlank(vo.getReceiptOrderKey())){
                    predicates.add(criteriaBuilder.equal(root.get("receiptOrderKey"), vo.getReceiptOrderKey()));
                }
                //发送邮件主体枚举
                if(vo.getCreditors() != null && vo.getCreditors().size() > 0){
                    log.info("发送邮件主体不为空!{}",vo.getCreditors());
                    List<CreditorEnum> creditorEnums = vo.getCreditors().stream().map(x -> Assert.enumNotValid(CreditorEnum.class, x,
                            "主体")).collect(Collectors.toList());
                    Predicate ins = root.get("creditor").in(creditorEnums);
                    predicates.add(ins);
                    //predicates.add(criteriaBuilder.equal(root.get("creditor"), CreditorEnum.valueOf(vo.getCreditor())));
                }
                //发送邮件状态
                if(StringUtils.isNotBlank(vo.getStatus())){
                    log.info("发送邮件状态不为空:{}",vo.getStatus());
                    predicates.add(criteriaBuilder.equal(root.get("status"), Byte.valueOf(vo.getStatus())));
                }
                //导出转发数组
                if(vo.getStatusList() != null && vo.getStatusList().size() > 0){
                    log.info("导出状态数组不为空:{}",vo.getStatusList());
                    List<Byte> statusLists = vo.getStatusList().stream().map(x -> Byte.valueOf(x)).collect(Collectors.toList());
                    Predicate ins = root.get("status").in(statusLists);
                    predicates.add(ins);
                    //predicates.add(criteriaBuilder.equal(root.get("status"), Byte.valueOf(vo.getStatus())));
                }
                if(StringUtils.isNotBlank(vo.getUid())){
                    predicates.add(criteriaBuilder.equal(root.get("uid"), vo.getUid()));
                }
                if(StringUtils.isNotBlank(vo.getSendStatus())){
                    predicates.add(criteriaBuilder.equal(root.get("sendStatus"), Byte.valueOf(vo.getSendStatus())));
                }
                if(StringUtils.isNotBlank(vo.getPartnerUserId())){
                    predicates.add(criteriaBuilder.equal(root.get("partnerUserId"), vo.getPartnerUserId()));
                }
                if(vo.getPriorityLevel() != null){
                    predicates.add(criteriaBuilder.equal(root.get("priorityLevel"), vo.getPriorityLevel()));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            };
            return specification;
        }

    }


    /**
     *
     * @param sheetName sheet1名字
     * @param titles    sheet1列名
     * @param cellList  sheet1字段属性
     * @param responseVos   sheet1基础数据集合
     * @param sheetName2    sheet1名字
     * @param titles2   sheet2列名
     * @param cellList2 sheet2字段属性
     * @param receiptRepayDetails   sheet2还款明细集合
     * @return
     */
    private XSSFWorkbook generateExcel(String sheetName, List<String> titles, List<String> cellList,
                                       List<ReceiptChildListResponseVo> responseVos,
                                       String sheetName2, List<String> titles2, List<String> cellList2, List<ReceiptRepayDetail> receiptRepayDetails) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        try {
            XSSFSheet sheet = workbook.createSheet(sheetName);
            int rowNum = 0;
            XSSFRow row = sheet.createRow(rowNum++);
            for (int i = 0; i < cellList.size(); i++) {
                row.createCell(i).setCellValue(titles.get(i));
            }
            for (ReceiptChildListResponseVo t : responseVos) {
                row = sheet.createRow(rowNum++);
                for (int i = 0; i < cellList.size(); i++) {
                    if (cellList.get(i).equals("creditor")) {
                        row.createCell(i).setCellValue(CreditorEnum.getEnum(getFieldValueByFieldName(cellList.get(i), t)).getDesc());
                        //若配置此项，则取json值
                    } else if (itemExtInfoList.contains(cellList.get(i))) {
                        if (Objects.isNull(t.getItemExtInfo())) {
                            row.createCell(i).setCellValue("历史数据为空");
                        } else {
                            JSONObject jsonObject = JSONObject.parseObject(t.getItemExtInfo());
                            row.createCell(i).setCellValue(jsonObject.get(cellList.get(i)).toString());
                        }
                    } else {
                        row.createCell(i).setCellValue(EncryptUtil.getDecoded(StringUtils.getWithDefault(getFieldValueByFieldName(cellList.get(i), t), "")));
                    }
                }
            }
            if (cellList2 != null || cellList2.size() != 0) {
                XSSFSheet sheet2 = workbook.createSheet(sheetName2);
                rowNum = 0;
                row = sheet2.createRow(rowNum++);
                for (int i = 0; i < cellList2.size(); i++) {
                    row.createCell(i).setCellValue(titles2.get(i));
                }
                for (Object t : receiptRepayDetails) {
                    row = sheet2.createRow(rowNum++);
                    for (int i = 0; i < cellList2.size(); i++) {
                        row.createCell(i).setCellValue(EncryptUtil.getDecoded(StringUtils.getWithDefault(getFieldValueByFieldName(cellList2.get(i), t), "")));
                    }
                }
            }
        }catch (Exception e){
            log.error("导出表单数据异常!",e);
            throw new ServiceException("导出表单数据异常!",e);
        }
        return workbook;
    }

    /**
     * 根据属性名获取属性值
     *
     * @param fieldName 属性名
     * @param object    类
     */
    private String getFieldValueByFieldName(String fieldName, Object object) {
        Object val = null;
        try {
            if (object instanceof Map) {
                val = ((Map) object).get(fieldName);
            } else {
                Field field = object.getClass().getDeclaredField(fieldName);
                //设置对象的访问权限，保证对private的属性的访问
                field.setAccessible(true);
                val = field.get(object);
                if(val instanceof Date && val != null){
                    val = DateUtils.formatSimpleData((Date)val);
                }
            }

        } catch (Exception e) {
            return null;
        }
        if (val instanceof Enum) {
            val = ((Enum) val).name();
        } else {
            val = String.valueOf(val);
        }
        return (String) val;
    }


    private ReceiptChildLoanDetailResponseVo buildReceiptChildLoanDetailResponseVo(List<ReceiptChildOrderAmount> list,
                                                                                   Map<String,List<ReceiptChildOrderAmount>> loanIds,
                                                                                   String userName) {
        try {
            String userNameStr = loanOrderService.desensitizedName(EncryptUtil.getDecoded(userName));
            BigDecimal totalReceiptAmount = list.stream().map(ReceiptChildOrderAmount::getReceiptAmount).reduce(BigDecimal.ZERO,
                    BigDecimal::add);
            //汇总信息
            ReceiptChildLoanDetailResponseVo.Summary summary = ReceiptChildLoanDetailResponseVo.Summary.builder()
                    .userName(userNameStr).totalReceiptAmount(totalReceiptAmount).build();
            //订单纬度的主体配置消息
            List<ReceiptChildLoanDetailResponseVo.LoanDetails> loanDetails = buildLoanDetails(list);
            //按借款纬度
            List<ReceiptChildLoanDetailResponseVo.LoanSummary> loanSummarys = new ArrayList<>();
            //订单状态
            for (String loanId : loanIds.keySet()) {
                List<ReceiptBaseInfo> receiptBaseInfo = receiptBaseInfoService.findByLoanId(loanId);
                ReceiptChildLoanDetailResponseVo.LoanSummary loanSummary = this.initLoanSummary(receiptBaseInfo);
                //取该订单所有的子单
                List<ReceiptChildOrderAmount> amountList = loanIds.get(loanId);
                if (CollectionUtils.isNotEmpty(amountList)) {
                    List<ReceiptChildOrderAmount> sysImport = amountList.stream()
                            .filter(c -> c.getReceiptOrderKey().startsWith("IMPORT")).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(sysImport)) {
                        for (ReceiptChildOrderAmount amount : amountList) {
                            BeanUtils.setProperty(loanSummary, amount.getReceiptItemCode().getCode(), amount.getReceiptAmount());
                        }
                    } else if (sysImport.size() < amountList.size()){
                        List<ReceiptChildOrderAmount> notSysImport = amountList.stream()
                                .filter(c -> !c.getReceiptOrderKey().startsWith("IMPORT")).collect(Collectors.toList());
                        for (ReceiptChildOrderAmount amount : notSysImport) {
                            BeanUtils.setProperty(loanSummary, amount.getReceiptItemCode().getCode(), amount.getReceiptAmount());
                        }
                    }
                }
                loanSummarys.add(loanSummary);
            }
            return ReceiptChildLoanDetailResponseVo.builder().loanDetails(loanDetails).loanSummarys(loanSummarys).summary(summary).build();
        }catch (Exception e){
            log.warn("子单查询明细receiptOrderKey:{}",list.get(0).getReceiptOrderKey(),e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 初始化
     * @param receiptBaseInfoList
     * @return
     */
    private ReceiptChildLoanDetailResponseVo.LoanSummary initLoanSummary(List<ReceiptBaseInfo> receiptBaseInfoList){
        BigDecimal repayInterest = BigDecimal.ZERO;
        BigDecimal repayMgmtFee = BigDecimal.ZERO;
        BigDecimal repayFunderOverdueInterest = BigDecimal.ZERO;
        BigDecimal repayOverdueInterest = BigDecimal.ZERO;
        BigDecimal repayInRepayFee = BigDecimal.ZERO;
        BigDecimal repayOverdueMgmtFee = BigDecimal.ZERO;
        BigDecimal repayCommutation = BigDecimal.ZERO;
        BigDecimal repayGuaranteeFee = BigDecimal.ZERO;
        for (ReceiptBaseInfo info : receiptBaseInfoList) {
            repayInterest = receiptBaseInfoList.stream().map(ReceiptBaseInfo -> BigDecimalUtils.ifNullDefaultZero(ReceiptBaseInfo.getRepayInterest())).reduce(BigDecimal.ZERO, BigDecimal::add);
            repayMgmtFee = receiptBaseInfoList.stream().map(ReceiptBaseInfo -> BigDecimalUtils.ifNullDefaultZero(ReceiptBaseInfo.getRepayMgmtFee())).reduce(BigDecimal.ZERO, BigDecimal::add);
            repayOverdueInterest = receiptBaseInfoList.stream().map(ReceiptBaseInfo -> BigDecimalUtils.ifNullDefaultZero(ReceiptBaseInfo.getRepayOverdueInterest())).reduce(BigDecimal.ZERO, BigDecimal::add);
            repayOverdueMgmtFee = receiptBaseInfoList.stream().map(ReceiptBaseInfo -> BigDecimalUtils.ifNullDefaultZero(ReceiptBaseInfo.getRepayOverdueMgmtFee())).reduce(BigDecimal.ZERO, BigDecimal::add);
            repayFunderOverdueInterest = receiptBaseInfoList.stream().map(ReceiptBaseInfo -> BigDecimalUtils.ifNullDefaultZero(ReceiptBaseInfo.getRepayFunderOverdueInterest())).reduce(BigDecimal.ZERO, BigDecimal::add);
            repayCommutation = receiptBaseInfoList.stream().map(ReceiptBaseInfo -> BigDecimalUtils.ifNullDefaultZero(ReceiptBaseInfo.getRepayCommutation())).reduce(BigDecimal.ZERO, BigDecimal::add);
            repayInRepayFee = receiptBaseInfoList.stream().map(ReceiptBaseInfo -> BigDecimalUtils.ifNullDefaultZero(ReceiptBaseInfo.getRepayInRepayFee())).reduce(BigDecimal.ZERO, BigDecimal::add);
            repayGuaranteeFee = receiptBaseInfoList.stream().map(ReceiptBaseInfo -> BigDecimalUtils.ifNullDefaultZero(ReceiptBaseInfo.getRepayGuaranteeFee())).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return ReceiptChildLoanDetailResponseVo.LoanSummary
                .builder().loanId(receiptBaseInfoList.get(0).getLoanId()).userName(EncryptUtil.getDecoded(receiptBaseInfoList.get(0).getUserName()))
                .fundName("人人贷理财".equals(EncryptUtil.getDecoded(receiptBaseInfoList.get(0).getFundName()))? "WE": EncryptUtil.getDecoded(receiptBaseInfoList.get(0).getFundName()))
                .loanAmount(receiptBaseInfoList.get(0).getLoanAmount())
                .repayStatus(EncryptUtil.getDecoded(receiptBaseInfoList.get(0).getRepayStatus())).loanTime(receiptBaseInfoList.get(0).getLoanTime())
                .repayInterest(repayInterest)
                .repayMgmtFee(repayMgmtFee)
                .repayFunderOverdueInterest(repayFunderOverdueInterest)
                .repayInRepayFee(repayInRepayFee)
                .repayOverdueInterest(repayOverdueInterest)
                .repayOverdueMgmtFee(repayOverdueMgmtFee)
                .repayCommutation(repayCommutation)
                .payOffTime(receiptBaseInfoList.get(0).getPayoffTime())
                .repayGuaranteeFee(repayGuaranteeFee)
                .partnerUserId(receiptBaseInfoList.get(0).getPartnerUserId())
                .build();

    }

    /**
     * loanId维度统计
     * @param list
     * @param cache
     * @return
     */
    private List<ReceiptChildLoanDetailResponseVo.LoanSummary> buildLoanSummarys(List<ReceiptChildOrderAmount> list, Map<String,Map<String, ReceiptChildOrderBase>> cache,String userName) {
        List<ReceiptChildLoanDetailResponseVo.LoanSummary> loanSummarys = new ArrayList<>();

        AtomicReference<BigDecimal> repayInterest = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal>  repayMgmtFee = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal>  repayOverdueInterest = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal>  repayOverdueMgmtFee = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal>  repayFunderOverdueInterest = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal>  repayGuaranteeDeposit = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal>  repayGuaranteeFee = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal>  repayOverdueGuaranteeFee = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal>  repayCommutation = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal>  repayInRepayFee = new AtomicReference<>(BigDecimal.ZERO);


        cache.forEach((loanIdTmp,map) ->{
            String loanId = loanIdTmp;
            String fundName = map.values().stream().filter(x -> StringUtils.isNotEmpty(x.getFundName())).map(ReceiptChildOrderBase::getFundName).distinct().collect(Collectors.toList()).get(0);
            BigDecimal loanAmount = map.values().stream().filter(x -> x.getLoanAmount() != null).map(ReceiptChildOrderBase::getLoanAmount).distinct().collect(Collectors.toList()).get(0);
            String repayStatus = map.values().stream().filter(x -> StringUtils.isNotEmpty(x.getRepayStatus())).map(ReceiptChildOrderBase::getRepayStatus).distinct().collect(Collectors.toList()).get(0);
            Date loanTime = map.values().stream().filter(x -> x.getLoanTime() != null).map(ReceiptChildOrderBase::getLoanTime).distinct().collect(Collectors.toList()).get(0);
            list.stream().filter(x -> x.getLoanId().equals(loanId)).forEach(x -> {
                ReceiptItemCodeAmount receiptItemCodeAmount = ReceiptItemCodeAmount.getEnum(x.getReceiptItemCode().getCode());
                switch (receiptItemCodeAmount){
                    case INTEREST:
                        repayInterest.set(x.getReceiptAmount());
                        break;
                    case MGMT_FEE:
                        repayMgmtFee.set(x.getReceiptAmount());
                        break;
                    case IN_REPAY_FEE:
                        repayInRepayFee.set(x.getReceiptAmount());
                        break;
                    case GUARANTEE_FEE:
                        repayGuaranteeFee.set(x.getReceiptAmount());
                        break;
                    case OVERDUE_INTEREST:
                        repayOverdueInterest.set(x.getReceiptAmount());
                        break;
                    case OVERDUE_MGMT_FEE:
                        repayOverdueMgmtFee.set(x.getReceiptAmount());
                        break;
                    case FUNDER_OVERDUE_INTEREST:
                        repayFunderOverdueInterest.set(x.getReceiptAmount());
                        break;
                    case COMMUTATION:
                        repayCommutation.set(x.getReceiptAmount());
                        break;
                    case GUARANTEE_DEPOSIT:
                        repayGuaranteeDeposit.set(x.getReceiptAmount());
                        break;
                    case OVERDUE_GUARANTEE_FEE:
                        repayOverdueGuaranteeFee.set(x.getReceiptAmount());
                        break;

                }
            });
            loanSummarys.add(ReceiptChildLoanDetailResponseVo.LoanSummary.builder()
                    .repayInterest(repayInterest.get()).repayOverdueInterest(repayOverdueInterest.get())
                    .repayGuaranteeDeposit(repayGuaranteeDeposit.get()).repayGuaranteeFee(repayGuaranteeFee.get())
                    .repayFunderOverdueInterest(repayFunderOverdueInterest.get()).repayOverdueMgmtFee(repayOverdueMgmtFee.get())
                    .repayOverdueGuaranteeFee(repayOverdueGuaranteeFee.get()).repayMgmtFee(repayMgmtFee.get())
                    .repayInRepayFee(repayInRepayFee.get()).repayCommutation(repayCommutation.get())
                    .userName(userName)
                    .loanTime(loanTime).loanAmount(loanAmount).loanId(loanId).fundName(fundName).repayStatus(repayStatus)
                    .build());
        });
        return loanSummarys;
    }

    private List<ReceiptChildLoanDetailResponseVo.LoanDetails> buildLoanDetails(List<ReceiptChildOrderAmount> list) {
        Map<String,List<ReceiptChildOrderAmount>> loanIds = list.stream().collect(Collectors.groupingBy(ReceiptChildOrderAmount::getLoanId));
        List<ReceiptChildLoanDetailResponseVo.LoanDetails> loanDetails = new ArrayList<>();
        loanIds.forEach((loanId,receiptChildOrderAmounts) ->{
            ReceiptChildLoanDetailResponseVo.LoanDetails loanDetail = new ReceiptChildLoanDetailResponseVo.LoanDetails();
            loanDetail.setLoanId(loanId);
            List<ReceiptChildLoanDetailResponseVo.LoanDetails.LoanDetail> loanDetails1 = new ArrayList<>();
            receiptChildOrderAmounts.forEach(receiptChildOrderAmount -> {
                loanDetails1.add(ReceiptChildLoanDetailResponseVo.LoanDetails.LoanDetail.builder()
                        .creditorName(receiptChildOrderAmount.getCreditor().getDesc())
                        .organization(ReceiptChannel.NUONUO.equals(receiptChildOrderAmount.getReceiptChannel()) ? OrganizationEnum.WEICAI.getDesc() : OrganizationEnum.FUNDER.getDesc())
                        .creditor(receiptChildOrderAmount.getCreditor().name())
                        .receiptItemName(ReceiptItemCodeAmount.getReceiptItemCodeAmountByCode(receiptChildOrderAmount.getReceiptItemCode().getCode()).getDesc())
                        .receiptChannel(receiptChildOrderAmount.getReceiptChannel().getDesc())
                        .receiptAmount(receiptChildOrderAmount.getReceiptAmount())
                        .status(receiptChildOrderAmount.getStatus())
                        .finishTime(receiptChildOrderAmount.getFinishTime())
                        .sendStatus(receiptChildOrderAmount.getSendStatus())
                        .receiptUrl(receiptChildOrderAmount.getReceiptUrl())
                        .sendTime(receiptChildOrderAmount.getSendTime())
                        .build());
            });
            loanDetail.setLoanDetails(loanDetails1);
            loanDetails.add(loanDetail);
        });
        return loanDetails;
    }

    /**
     * 构造响应列表
     * @param list
     * @return
     */
    private List<ReceiptChildListResponseVo> buildResponse(List<ReceiptChildOrderAmount> list,Boolean isExport) {
        Map<String,List<ReceiptChildOrderAmount>> loanIds = list.stream().collect(Collectors.groupingBy(ReceiptChildOrderAmount::getLoanId));
        List<ReceiptChildListResponseVo> responseVos = new ArrayList<>();
        Map<String,Map<String, ReceiptChildOrderBase>> cache = new HashMap<>();
        loanIds.forEach((loanId,receiptChildOrderAmounts) -> {
            receiptChildOrderAmounts.forEach(receiptChildOrderAmount ->{
                ReceiptChildOrderBase base = null;
                String creditor = receiptChildOrderAmount.getCreditor().name();
                if(cache.containsKey(loanId) && cache.get(loanId).containsKey(creditor)){
                    base = cache.get(loanId).get(creditor);
                }else{
                    List<ReceiptChildOrderBase> list1 = receiptChildOrderBaseService.findByLoanIdAndCreditorOrderByIdDesc(loanId,receiptChildOrderAmount.getCreditor());
                    log.error("发票子单基础信息不存在receiptChildOrderBase！loanId:{}-Creditor:{}",loanId,receiptChildOrderAmount.getCreditor());
                    Assert.notEmpty(list1,"发票子单基础信息loanId:"+loanId+"-Creditor:"+receiptChildOrderAmount.getCreditor());
                    base = list1.get(0);
                    init(cache,loanId,creditor,base);
                }
                responseVos.add(buildResponseVo(isExport,base,receiptChildOrderAmount));
            });
        });
        return responseVos.stream().sorted(Comparator.comparing(ReceiptChildListResponseVo::getPriorityLevel)).collect(Collectors.toList());
    }

    /**
     * 构造响应VO
     * @param base
     * @param receiptChildOrderAmount
     * @return
     */
    private ReceiptChildListResponseVo buildResponseVo(Boolean isExport,ReceiptChildOrderBase base, ReceiptChildOrderAmount receiptChildOrderAmount) {
        ReceiptOrder receiptOrder = receiptOrderService.findByReceiptOrderKey(base.getReceiptOrderKey());
        log.info("主单receiptOrderKey:{}",base.getReceiptOrderKey());
        //借款号
        ReceiptChildListResponseVo vo = ReceiptChildListResponseVo.builder().build();
        String userName;
        try {
            //导出数据源
            //if (isExport){
            BeanUtils.copyProperties(vo, base);
            userName = EncryptUtil.getDecoded(base.getUserName());
            //}else {
            //    //页面数据源
            //    BeanUtils.copyProperties(vo, baseInfoList.get(0));
            //    userName = EncryptUtil.getDecoded(baseInfoList.get(0).getUserName());
            //}
        } catch (IllegalAccessException e) {
            log.error("BeanUtils.copyProperties异常", e);
            throw new ServiceException(e);
        } catch (InvocationTargetException e) {
            log.error("BeanUtils.copyProperties异常", e);
            throw new ServiceException(e);
        }
        vo.setReceiptOrderKey(receiptChildOrderAmount.getReceiptOrderKey());
        vo.setReceiptChildOrderKey(receiptChildOrderAmount.getReceiptChildOrderKey());
        vo.setReceiptItemCode(receiptChildOrderAmount.getReceiptItemCode().getCode());
        vo.setReceiptItemName(ReceiptItemCodeAmount.getEnum(receiptChildOrderAmount.getReceiptItemCode().name()).getDesc());
        vo.setCreditorName(receiptChildOrderAmount.getCreditor().getDesc());
        vo.setReceiptAmount(receiptChildOrderAmount.getReceiptAmount());
        vo.setReceiptUrl(receiptChildOrderAmount.getReceiptUrl());
        vo.setUpdateTime(receiptChildOrderAmount.getUpdateTime());
        vo.setFinishTime(receiptChildOrderAmount.getFinishTime());
        vo.setStatus(receiptChildOrderAmount.getStatus());
        vo.setId(receiptChildOrderAmount.getId());
        vo.setEmail(Desensitization.emailDesensitization(EncryptUtil.getDecoded(receiptOrder.getEmail())));
        vo.setReceiptAmount(receiptChildOrderAmount.getReceiptAmount());
        vo.setAccount(isExport ? EncryptUtil.getDecoded(base.getAccount()) : desensitizedPhoneNumber(EncryptUtil.getDecoded(base.getAccount())));
        vo.setAddress(EncryptUtil.getDecoded(isExport ? base.getAddress() : base.getAddress()));
        vo.setPriorityLevel(receiptOrder.getPriorityLevel());
        vo.setSendStatus(receiptChildOrderAmount.getSendStatus());
        vo.setSendTime(receiptChildOrderAmount.getSendTime());
        vo.setUid(receiptChildOrderAmount.getUid());
        vo.setPartnerUserId(receiptChildOrderAmount.getPartnerUserId());
        vo.setItemExtInfo(receiptChildOrderAmount.getItemExtInfo());
        vo.setUserName(userName);
        vo.setTrustName(EncryptUtil.getDecoded(base.getTrustName()));
        vo.setUserName(isExport?userName:this.desensitizedName(userName));
        if((receiptChildOrderAmount.getReceiptChannel() == ReceiptChannel.MANUAL)
                && (receiptChildOrderAmount.getStatus() == ReceiptChildOrderAmountStatus.INIT.getStatus()
                || receiptChildOrderAmount.getStatus() == ReceiptChildOrderAmountStatus.DEALING.getStatus())){
            vo.setCanDownload(true);
        }else{
            vo.setCanDownload(false);
        }
        String organization;
        if (ReceiptChannel.NUONUO.equals(receiptChildOrderAmount.getReceiptChannel())){
            organization = OrganizationEnum.WEICAI.getDesc();
        }else{
            organization = OrganizationEnum.FUNDER.getDesc();
        }
        vo.setOrganization(organization);
        //额外信息为新增费用项
        if (!Objects.isNull(receiptChildOrderAmount.getItemExtInfo())){
            JSONObject extraInfo = JSON.parseObject(receiptChildOrderAmount.getItemExtInfo());
            vo.setRepayInterest(extraInfo.getBigDecimal("repayInterest"));
            vo.setRepayFunderOverdueInterest(extraInfo.getBigDecimal("repayFunderOverdueInterest"));
            vo.setRepayMgmtFee(extraInfo.getBigDecimal("repayMgmtFee"));
            vo.setRepayOverdueInterest(extraInfo.getBigDecimal("repayOverdueInterest"));
            vo.setRepayOverdueMgmtFee(extraInfo.getBigDecimal("repayOverdueMgmtFee"));
            vo.setRepayInRepayFee(extraInfo.getBigDecimal("repayInRepayFee"));
            vo.setRepayGuaranteeFee(extraInfo.getBigDecimal("repayGuaranteeFee"));
            vo.setRepayCommutation(extraInfo.getBigDecimal("repayCommutation"));
        }
        //不是导出的，几个合同的路径处理
        if(!isExport){
            vo.setLoanContractPath("");
            vo.setInsureLetterPath("");
            vo.setGuaranteeServiceContractPath("");
            vo.setGatherAuthLetter("");
        }
        return vo;
    }

    /**
     * 初始化查询缓存
     * @param cache
     * @param loanId
     * @param creditor
     * @param base
     */
    private void init(Map<String, Map<String, ReceiptChildOrderBase>> cache, String loanId, String creditor, ReceiptChildOrderBase base) {
        if(cache.containsKey(loanId)){
            if(!cache.get(loanId).containsKey(creditor)){
                cache.get(loanId).put(creditor,base);
            }
        }else{
            Map map = new HashMap();
            map.put(creditor,base);
            cache.put(loanId,map);
        }
    }

    /**
     * 手机号脱敏
     * @param phoneNumber
     * @return
     */
    private String desensitizedPhoneNumber(String phoneNumber){
        if(StringUtils.isNotEmpty(phoneNumber)){
            phoneNumber = phoneNumber.replaceAll("(\\w{3})\\w*(\\w{4})", "$1****$2");
        }
        return phoneNumber;
    }

    /**
     *
     */
    private String desensitizedName(String userName){
        return userName.replace(StringUtils.left(userName,1),"*");
    }

    public static void main(String[] args) {

        System.out.println(EncryptUtil.getEncoded("9012010093737061"));
        System.out.println(EncryptUtil.getEncoded("8907501007250726"));
        System.out.println(EncryptUtil.getDecoded("78qFj12a7aj3vtCfi23xeoz0Zo3FThUxtjjI1BYqohE="));
        System.out.println(EncryptUtil.getDecoded("YGinA3vgLrgnTEvR2k+uuHYT4rm10omfXjmTp68SPDM="));
        System.out.println(EncryptUtil.getDecoded("YGinA3vgLrgnTEvR2k+uuFiiVXVOLNRnL8pH0GjqbaA="));
        System.out.println(EncryptUtil.getDecoded("78qFj12a7aj3vtCfi23xes2s7EC0ycUNKARkPQ2XRHs="));
        System.out.println(EncryptUtil.getDecoded("p2DcolX/JTLGGB+j1lKJn65oHG0MGR5i8uX0l/2d2Iw=="));
    }
}