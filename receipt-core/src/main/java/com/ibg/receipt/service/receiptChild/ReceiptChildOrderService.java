package com.ibg.receipt.service.receiptChild;

import com.alibaba.fastjson.JSONObject;
import com.ibg.receipt.base.vo.PageVo;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderAmount;
import com.ibg.receipt.vo.api.receiptChild.ReceiptChildListRequestVo;
import com.ibg.receipt.vo.api.receiptChild.ReceiptChildLoanDetailRequestVo;
import com.ibg.receipt.vo.api.receiptChild.ReceiptChildLoanDetailResponseVo;
import com.ibg.receipt.vo.api.receiptChild.ReceiptExportRequestVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface ReceiptChildOrderService {

    PageVo<?> list(ReceiptChildListRequestVo vo);

    ReceiptChildLoanDetailResponseVo queryloanDetail(ReceiptChildLoanDetailRequestVo vo);

    List<ReceiptChildOrderAmount> findByReceiptChildOrderKeyIn(String[] receiptChildOrderKeys);

    List<String> uploadReceipt(MultipartFile[] multipartFile, HttpServletRequest request, String uploadBatchNo,String landUserName) throws Exception;

    Map<String, byte[]> downloadFileFromFs(String metaFileId);

    byte[] export(List<ReceiptChildOrderAmount> list,String landUserName) throws Exception;

    List<ReceiptChildOrderAmount> findByCond(HttpServletRequest request) throws Exception;

    List<ReceiptChildOrderAmount> findByCond(JSONObject jsonObject);

    void generateSendEmailJob(List<ReceiptChildOrderAmount> list);
}
