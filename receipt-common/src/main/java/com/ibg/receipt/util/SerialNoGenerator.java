package com.ibg.receipt.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 按业务生成流水号 yyMMdd + 两位业务号 + 32位UUID
 *
 * @author liuchunfeng
 */
public final class SerialNoGenerator {

    private final static char[] CODEC_TABLE = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
        'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
        'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
        '4', '5', '6', '7', '8', '9' };

    private final static char[] CODE_TABLE_UPP_CASE = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
        'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    private final static char[] CODE_NUMBERS_CASE = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    private static final String USER_KEY_PREFIX = "U_";

    private static final String APPLICATION_NO_PREFIX = "APPL_";

    private static final String UPLOAD_SERIAL_PREFIX = "UP_";

    private static final String SEND_SERIAL_PREFIX = "SEND_";

    private static final String REPAY_PLAN_NO_PREFIX = "PLAN_";

    private static final String LOAN_NO_PREFIX = "LOAN_";

    private static final String FILE_PREFIX = "FILE_";

    private static final String REPAY_PREFIX = "REPAY_";

    private static final String ADVANCE_PREFIX_GEN = "ADV_";

    private static final String REPURCHASE_PREFIX_GEN = "REP_";

    private static final String PAY_PREFIX = "PAY_";

    private static final String SMS_PREFIX = "SMS_";

    private static final String STL_PREFIX = "STL_";

    private static final String CARD_PREFIX = "CARD_";

    private static final String REPAY_DETAIL_PREFIX = "REPAYDETAIL_";

    private static final String REPURCHASE_PREFIX = "HG_";

    private static final String BILL_CHECK = "BILL_";

    private static final String FUNDER_PREFIX = "FUNDER_";

    private static final String BILL_DIFFERENC_CHECK = "BILL_DIFFERENC_";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssS");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static String generateSerialNo(String prefix, int totalLength) {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append(prefix);
        int abLength = sbuf.length();
        for (int i = totalLength - abLength; i > 0; i--) {
            sbuf.append(CODEC_TABLE[ThreadLocalRandom.current().nextInt(CODEC_TABLE.length)]);
        }
        return sbuf.toString();
    }

    //数字随机数
    public static String generateSerialNoByNumber(String prefix, int totalLength) {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append(prefix);
        int abLength = sbuf.length();
        for (int i = totalLength - abLength; i > 0; i--) {
            sbuf.append(CODE_NUMBERS_CASE[ThreadLocalRandom.current().nextInt(CODE_NUMBERS_CASE.length)]);
        }
        return sbuf.toString();
    }

    /**
     *
     *
     * @param prefix  前缀
     * @param isolateChar  分隔符
     * @param totalLength  总长度
     * @return
     */
    public static String generateSerialNo(String prefix,String isolateChar, int totalLength) {
        StringBuilder sbuf = new StringBuilder();
        if(!StringUtils.isBlank(isolateChar)){
            sbuf.append(currentFormatTime()).append(isolateChar).append(prefix).append(isolateChar);
        }else{
            sbuf.append(prefix).append(currentFormatTime());
        }
        int abLength = sbuf.length();
        for (int i = totalLength - abLength; i > 0; i--) {
            sbuf.append(CODEC_TABLE[ThreadLocalRandom.current().nextInt(CODEC_TABLE.length)]);
        }
        return sbuf.toString();
    }

    /**
     * 对于不区分大小写的渠道生成随机序列号
     *
     * @param prefix
     * @param totalLength
     * @return
     */
    public static String generateSerialNoWithUpperCase(String prefix, int totalLength) {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append(prefix);
        int abLength = sbuf.length();
        for (int i = totalLength - abLength; i > 0; i--) {
            sbuf.append(CODE_TABLE_UPP_CASE[ThreadLocalRandom.current().nextInt(CODE_TABLE_UPP_CASE.length)]);
        }
        return sbuf.toString();
    }

    public static String generateUserKey(String partnerCode) {
        return generateSerialNo(USER_KEY_PREFIX + getPartnerCodePrefix(partnerCode) + currentTime(), 32);
    }

    public static String generateApplicationNo(String partnerCode) {
        return generateSerialNo(APPLICATION_NO_PREFIX + getPartnerCodePrefix(partnerCode) + currentTime(), 32);
    }

    public static String generateUploadSerialNo() {
        return generateSerialNo(UPLOAD_SERIAL_PREFIX + currentTime(), 32);
    }
    public static String generateSendSerialNo() {
        return generateSerialNo(SEND_SERIAL_PREFIX + currentTime(), 32);
    }

    public static String generateRepayPlanNo(String partnerCode, Integer totalLength) {
        return generateSerialNo(REPAY_PLAN_NO_PREFIX + getPartnerCodePrefix(partnerCode) + currentTime(), totalLength == null ? 32 : 48);
    }

    public static String generateLoanNo(String partnerCode) {
        return generateSerialNo(LOAN_NO_PREFIX + getPartnerCodePrefix(partnerCode) + currentTime(), 32);
    }

    public static String generateFileNo(String partnerCode) {
        return generateSerialNo(FILE_PREFIX + getPartnerCodePrefix(partnerCode) + currentTime(), 32);
    }

    public static String generateRepaySerialNo(String partnerCode) {
        return generateSerialNo(REPAY_PREFIX + getPartnerCodePrefix(partnerCode) + currentTime(), 32);
    }

    public static String generateAdvanceRepaySerialNo(String channelCode) {
        return generateSerialNo(ADVANCE_PREFIX_GEN + channelCode + currentTime(), 32);
    }

    public static String generateRepurchaseRepaySerialNo(String channelCode) {
        return generateSerialNo(REPURCHASE_PREFIX_GEN + channelCode + currentTime(), 32);
    }

    public static String generatePaySerialNo(String businessType) {
        return generateSerialNo(PAY_PREFIX + businessType + currentTime(), 48);
    }

    public static String generateSmsSerialNo(String codeType) {
        return generateSerialNo(SMS_PREFIX + codeType + currentTime(), 32);
    }

    public static String generateStlSerialNo(String codeType) {
        return generateSerialNo(STL_PREFIX + codeType + currentTime(), 32);
    }

    public static String generateCardSerialNo(String partnerCode) {
        return generateSerialNo(CARD_PREFIX + getPartnerCodePrefix(partnerCode) + currentTime(), 32);
    }

    public static String generateRepurchaseSerialNo(String channelCode) {
        return generateSerialNo(REPURCHASE_PREFIX + channelCode + currentTime(), 32);
    }

    public static String generateRequestNo(String channelCode) {
        return generateSerialNo("" + channelCode + currentTime(), 32);
    }

    public static String generateBillingSerialNo(String partnerCode) {
        return generateSerialNo(BILL_CHECK + getPartnerCodePrefix(partnerCode) + currentTime(), 32);
    }

    public static String generateBillDifferencSerialNo(String billingCheckType) {
        return generateSerialNo(BILL_DIFFERENC_CHECK + billingCheckType + currentTime(), 32);
    }

    public static String generateFunderSerialNo(String partnerCode) {
        return generateSerialNo(FUNDER_PREFIX + getPartnerCodePrefix(partnerCode) + currentTime(), 32);
    }

    public static String generateFunderSerialNoWithUpperCase(String partnerCode) {
        return generateSerialNoWithUpperCase(getPartnerCodePrefix(partnerCode) + currentTime(), 32);
    }
    public static String generateDateSerialNo(String prefix) {
        return generateSerialNo(prefix + currentTime(), 32);
    }

    public static String currentTime() {
        return DATE_TIME_FORMATTER.format(LocalDateTime.now());
    }

    public static String currentFormatTime() {
        return DATE_FORMAT.format(LocalDateTime.now());
    }

    public static String currentformatterTime() {
        return DATE_FORMATTER.format(LocalDateTime.now());
    }

    private SerialNoGenerator() {
    }


    public static String getPartnerCodePrefix(String partnerCode){
        if(StringUtils.isNotEmpty(partnerCode)){
            if(partnerCode.equals("HAOHUAN")){
                return "H";
            }else if(partnerCode.equals("UCREDIT")){
                return "U";
            }else if(partnerCode.equals("RRD")){
                return "R";
            }else {
                return partnerCode;
            }
        }
        return partnerCode;
    }
}
