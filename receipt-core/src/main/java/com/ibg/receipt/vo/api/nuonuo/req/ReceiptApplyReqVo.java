package com.ibg.receipt.vo.api.nuonuo.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibg.receipt.vo.api.nuonuo.base.NuoNuoBaseReqVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptApplyReqVo extends NuoNuoBaseReqVO {

    private OrderBean order;

    @NoArgsConstructor
    @Data
    @Builder
    @AllArgsConstructor
    public static class OrderBean {
        /**
         * 企业名称/个人
         */
        @JsonProperty("buyerName")
        private String buyerName;

        /**
         * 销方税号（使用沙箱环境请求时消息体参数salerTaxNum和消息头参数userTax填写339902999999789113）
         */
        @JsonProperty("salerTaxNum")
        private String salerTaxNum;

        /**
         * 销方电话
         */
        @JsonProperty("salerTel")
        private String salerTel;

        /**
         * 销方地址
         */
        @JsonProperty("salerAddress")
        private String salerAddress;

        /**
         * 订单号（每个企业唯一）
         */
        @JsonProperty("orderNo")
        private String orderNo;

        /**
         * 订单时间 2022-01-13 12:30:00
         */
        @JsonProperty("invoiceDate")
        private String invoiceDate;

        /**
         * 开票员（全电发票时需要传入和开票登录账号对应的开票员姓名）
         */
        @JsonProperty("clerk")
        private String clerk;

        /**
         * 购方手机（pushMode为1或2时，此项为必填，同时受企业资质是否必填控制）
         */
        @JsonProperty("buyerPhone")
        private String buyerPhone;

        /**
         * 推送邮箱（pushMode为0或2时，此项为必填，同时受企业资质是否必填控制）
         */
        @JsonProperty("email")
        private String email;

        /**
         * 开票类型：1:蓝票;2:红票 （全电发票暂不支持红票）
         */
        @JsonProperty("invoiceType")
        private String invoiceType;

        /**
         * 发票种类
         */
        @JsonProperty("invoiceLine")
        private String invoiceLine;

        /**
         * 分机号
         */
        @JsonProperty("extensionNumber")
        private String extensionNumber;

        /**
         * 发票明细，支持填写商品明细最大2000行（包含折扣行、被折扣行）
         */
        @JsonProperty("invoiceDetail")
        private List<InvoiceDetailBean> invoiceDetail;

        /**
         * 机动车销售统一发票才需要传
         */
        @JsonProperty("vehicleInfo")
        private VehicleInfoBean vehicleInfo;

        /**
         * 开具二手车销售统一发票才需要传
         */
        @JsonProperty("secondHandCarInfo")
        private SecondHandCarInfoBean secondHandCarInfo;

        /**
         * 附加要素信息列表（全电发票特有字段，附加要素信息可以有多个，有值时需要附加模版名称也有值）
         */
        @JsonProperty("additionalElementList")
        private List<AdditionalElementListBean> additionalElementList;

        private String terminalNumber;
        private String buyerTel;
        private String listFlag;
        private String pushMode;
        private String managerCardNo;
        private String departmentId;
        private String nextInvoiceNum;
        private String clerkId;
        private String remark;
        private String checker;
        private String payee;
        private String buyerAddress;
        private String managerCardType;
        private String buyerTaxNum;
        private String buyerManagerName;
        private String redReason;
        private String specificFactor;
        private String salerAccount;
        private String callBackUrl;
        private String machineCode;
        private String billInfoNo;
        private String vehicleFlag;
        private String invoiceCode;
        private String invoiceNum;
        private String hiddenBmbbbh;
        private String buyerAccount;
        private String nextInvoiceCode;
        private String surveyAnswerType;
        private String additionalElementName;
        private String listName;
        private String proxyInvoiceFlag;

        @NoArgsConstructor
        @Data
        @Builder
        @AllArgsConstructor
        public static class SecondHandCarInfoBean {
            /**
             * 开票方类型 1：经营单位 2：拍卖单位 3：二手车市场
             */
            @JsonProperty("organizeType")
            private String organizeType;

            /**
             * 车辆类型,同明细中商品名称，开具机动车发票时明细有且仅有一行，商品名称为车辆类型且不能为空
             */
            @JsonProperty("vehicleType")
            private String vehicleType;

            /**
             * 厂牌型号
             */
            @JsonProperty("brandModel")
            private String brandModel;

            /**
             * 车辆识别号码/车架号
             */
            @JsonProperty("vehicleCode")
            private String vehicleCode;

            /**
             * 车牌照号
             */
            @JsonProperty("licenseNumber")
            private String licenseNumber;

            /**
             * 登记证号
             */
            @JsonProperty("registerCertNo")
            private String registerCertNo;

            /**
             * 转入地车管所名称
             */
            @JsonProperty("vehicleManagementName")
            private String vehicleManagementName;

            /**
             * 卖方单位/个人名称（开票方类型为1、2时，必须与销方名称一致）
             */
            @JsonProperty("sellerName")
            private String sellerName;

            /**
             * 卖方单位代码/身份证号码（开票方类型为1、2时，必须与销方税号一致）
             */
            @JsonProperty("sellerTaxnum")
            private String sellerTaxnum;

            /**
             * 卖方单位/个人地址（开票方类型为1、2时，必须与销方地址一致）
             */
            @JsonProperty("sellerAddress")
            private String sellerAddress;

            /**
             * 卖方单位/个人电话（开票方类型为1、2时，必须与销方电话一致）
             */
            @JsonProperty("sellerPhone")
            private String sellerPhone;

            private String intactCerNum;

        }

        @NoArgsConstructor
        @Data
        @Builder
        @AllArgsConstructor
        public static class VehicleInfoBean {
            /**
             * 车辆类型,同明细中商品名称，开具机动车发票时明细有且仅有一行，商品名称为车辆类型且不能为空
             */
            @JsonProperty("vehicleType")
            private String vehicleType;

            /**
             * 厂牌型号
             */
            @JsonProperty("brandModel")
            private String brandModel;

            /**
             * 原产地
             */
            @JsonProperty("productOrigin")
            private String productOrigin;

            /**
             * 车辆识别号码/车架号
             */
            @JsonProperty("vehicleCode")
            private String vehicleCode;

            private String taxOfficeCode;
            private String manufacturerName;
            private String importCerNum;
            private String certificate;
            private String engineNum;
            private String taxOfficeName;
            private String maxCapacity;
            private String intactCerNum;
            private String tonnage;
            private String insOddNum;
            private String idNumOrgCode;
        }

        @NoArgsConstructor
        @Data
        @Builder
        @AllArgsConstructor
        public static class InvoiceDetailBean {
            /**
             * 商品名称
             */
            @JsonProperty("goodsName")
            private String goodsName;

            /**
             * 单价含税标志：0:不含税,1:含税
             */
            @JsonProperty("withTaxFlag")
            private String withTaxFlag;

            /**
             * 税率，注：1、纸票清单红票存在为null的情况；2、二手车发票税率为null或者0
             */
            @JsonProperty("taxRate")
            private BigDecimal taxRate;

            private String specType;

            /**
             * 不含税金额。红票为负。不含税金额、税额、含税金额任何一个不传时，会根据传入的单价，数量进行计算，可能和实际数值存在误差，建议都传入
             */
            private BigDecimal taxExcludedAmount;
            /**
             * 含税金额，[不含税金额] + [税额] = [含税金额]，红票为负。不含税金额、税额
             * 、含税金额任何一个不传时，会根据传入的单价，数量进行计算，可能和实际数值存在误差，建议都传入
             */
            private BigDecimal taxIncludedAmount;
            /**
             * 税额，[不含税金额] * [税率] = [税额]；税额允许误差为 0.06。红票为负。不含税金额、税额
             * 、含税金额任何一个不传时，会根据传入的单价，数量进行计算，可能和实际数值存在误差，建议都传入
             */
            private BigDecimal tax;
            private String invoiceLineProperty;
            private String favouredPolicyName;
            private String num;
            private String favouredPolicyFlag;
            private String unit;
            private String deduction;
            private String price;
            private String zeroRateFlag;
            private String goodsCode;
            private String selfCode;
        }

        @NoArgsConstructor
        @Data
        @Builder
        @AllArgsConstructor
        public static class AdditionalElementListBean {
            private String elementValue;
            private String elementType;
            private String elementName;
        }
    }
}
