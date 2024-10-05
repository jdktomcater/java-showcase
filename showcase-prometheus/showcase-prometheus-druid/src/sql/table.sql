/*
 Navicat Premium Data Transfer

 Source Server         : 本地环境_MySQL8.0
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : 127.0.0.1:3306
 Source Schema         : typayv2

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 05/10/2024 15:43:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for _t_depositorder_del
-- ----------------------------
DROP TABLE IF EXISTS `_t_depositorder_del`;
CREATE TABLE `_t_depositorder_del`  (
  `OrderIndex` bigint NOT NULL COMMENT '订单索引',
  `OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单号',
  `SupplierBranchID` int NULL DEFAULT NULL COMMENT '分部ID',
  `MerchantID` int NULL DEFAULT NULL COMMENT '商户编号',
  `MerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户订单号最长64位',
  `UserLevel` int NULL DEFAULT NULL COMMENT '商户会员等级',
  `UserID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户会员唯一ID',
  `MerchantOrderTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `PayType` int NULL DEFAULT NULL COMMENT '充值方式,1:网银转账2:快捷支付3:银联支付4:微信5:支付宝6:QQ钱包7:京东支付8:云闪付',
  `ClientType` int NULL DEFAULT NULL COMMENT '客户端类型, 1：PC，2:WAP，4:APP',
  `PayAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '充值金额',
  `PaidAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '实际到账金额',
  `PaidChannelFee` decimal(10, 2) NULL DEFAULT NULL COMMENT '商户需付手续费',
  `SupplierPaidChannelFee` decimal(10, 2) NULL DEFAULT NULL COMMENT '渠道提供方手续费，比如需向三方通道付手续费，比如平均银行卡磨损费',
  `AgencyCommission` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理佣金',
  `NotifyUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户用于接收订单通知的地址',
  `ReturnUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单提交后立即返回的商户页面地址',
  `MerchantRemark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `ChannelOrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '平台提交给充值渠道的订单号, GUID32位',
  `BankCode` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行类型代码',
  `MD5OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'MD5签名',
  `PayChannelID` int NULL DEFAULT NULL COMMENT '处理该订单的充值渠道',
  `BankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理该订单的银行卡',
  `BankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理该订单的银行卡简称',
  `OrderStatus` int NULL DEFAULT 0 COMMENT '订单状态0:创建，默认值;1：处理中;2:清算中;3:正常支付关闭;4：手工确认订单已完成关闭; 5:超时,6:超时关闭；7:取消关闭',
  `OrderPaidStatusChangeTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单状态最后修改时间',
  `OrderNotifyStatus` int NULL DEFAULT NULL COMMENT '通知状态；0:未成功通知；1：已成功通知;2:通知超时异常',
  `OrderNotifyTime` datetime NULL DEFAULT NULL COMMENT '订单通知状态最后修改时间',
  `Remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '附言',
  `Name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `ReadMark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用于标注是否被自动付款取走',
  `CloseReason` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关闭原因',
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `UserIP` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'ip',
  `Operator` int NULL DEFAULT 0 COMMENT '操作员',
  `MemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '会员账号',
  `UserCreditLevel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户层级，根据用户层级等级分配不同支付渠道;多个层级，使用‘,’分割，比如支持5_1,5_2层级，则是5_1,5_2',
  `MemberBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户的存款银行卡',
  `EncryptBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理该订单的银行卡',
  `EncryptBankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理该订单的银行卡简称',
  `EncryptName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员姓名',
  `EncryptMemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员账号',
  `EncryptMemberBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户的存款银行卡',
  `SourceType` tinyint NOT NULL DEFAULT 0 COMMENT '提单方式',
  `ManualRemark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '人工备注',
  `AutoMataType` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '自动机客户端类型  0 PC 1 手机银行',
  `EncryptDepositName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '真实存款人名字',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `VirtualPayAmount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币金额',
  `VirtualPaidAmount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币实际金额',
  `VirtualRate` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币浮动汇率',
  `VirtualBasicRate` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币基础汇率',
  PRIMARY KEY (`OrderIndex`) USING BTREE,
  UNIQUE INDEX `MD5OrderID_UNIQUE`(`MD5OrderID` ASC) USING BTREE,
  INDEX `CreateTime_Index`(`CreateTime` ASC) USING BTREE,
  INDEX `Name_Index`(`Name` ASC) USING BTREE,
  INDEX `Name_Remark_Index`(`Remark` ASC, `Name` ASC) USING BTREE,
  INDEX `SupplierBranchID_Index`(`SupplierBranchID` ASC, `UserID` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '银行卡充值订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for fk_risk_order
-- ----------------------------
DROP TABLE IF EXISTS `fk_risk_order`;
CREATE TABLE `fk_risk_order`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id主键',
  `supplier_branchid` int NOT NULL DEFAULT 0 COMMENT '财务分部ID',
  `merchant_id` int NOT NULL DEFAULT 0 COMMENT '商户编号',
  `business_type` tinyint NOT NULL COMMENT '风控业务类型|1:充值|2:提现|3:充值视图|4:提现视图|10:其他',
  `merchant_order_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户订单号最长64位',
  `user_level` int NOT NULL DEFAULT 0,
  `user_credit_level` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户层级，根据用户层级等级分配不同支付渠道;多个层级，使用‘,’分割，比如支持5_1,5_2层级，则是5_1,5_2',
  `member_account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '会员账号',
  `user_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户会员IP',
  `pay_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '充值金额',
  `status` int NOT NULL DEFAULT -1 COMMENT '审核状态|-1:无需审核|0:未审核|1:审核通过|2:审核未通过|3:自动审核通过',
  `need_check` int NOT NULL DEFAULT 0 COMMENT '是否要校验 0：需要  1：不需要',
  `risk_type` int NOT NULL DEFAULT 0 COMMENT '风险等级 0：低，1：中  2：高',
  `bank_owner` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '名字',
  `sign` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '签名',
  `pay_type` int NOT NULL DEFAULT 0 COMMENT '支付方式',
  `return_url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '返回url',
  `notify_url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '通知url',
  `bank_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行代码',
  `bank_num` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '卡号',
  `bank_address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行地址',
  `protocol` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'usdt协议',
  `user_id` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户id',
  `phone_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '电话号码',
  `remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'remark',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for fk_rule_operate_log
-- ----------------------------
DROP TABLE IF EXISTS `fk_rule_operate_log`;
CREATE TABLE `fk_rule_operate_log`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id主键',
  `order_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '商户订单号',
  `rule_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '规则名称',
  `operate_type` int NOT NULL DEFAULT 0 COMMENT '操作类型 0：未匹配 1：匹配',
  `source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '来源',
  `is_sentMango` int NOT NULL DEFAULT 0 COMMENT '是否发送mango',
  `mango_message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '0' COMMENT 'mango消息',
  `create_Time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_Time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '创建者',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '更新者',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`id`, `node_id`) USING BTREE,
  INDEX `order_id_index`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for merchant_balance_modification_record
-- ----------------------------
DROP TABLE IF EXISTS `merchant_balance_modification_record`;
CREATE TABLE `merchant_balance_modification_record`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单ID',
  `merchant_order_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商户订单ID',
  `merchant_id` int NOT NULL COMMENT '商户ID',
  `modification_type` tinyint NOT NULL DEFAULT -1 COMMENT '余额变动类型，-1未知',
  `pre_balance` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '交易前总余额',
  `post_balance` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '交易后总余额',
  `pre_available_balance` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '交易前可用余额',
  `post_available_balance` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '交易后可用余额',
  `pre_frozen_balance` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '交易前冻结余额',
  `post_frozen_balance` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '交易后冻结余额',
  `remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '余额变动说明',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `node_id` int NULL DEFAULT 1 COMMENT '节点ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_orderId_modType`(`order_id` ASC, `modification_type` ASC) USING BTREE,
  INDEX `idx_date`(`created_at` ASC, `updated_at` ASC) USING BTREE,
  INDEX `idx_merchant_order_id`(`merchant_order_id` ASC) USING BTREE,
  INDEX `idx_merchantid`(`merchant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户余额变动记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for merchant_order_amount_config
-- ----------------------------
DROP TABLE IF EXISTS `merchant_order_amount_config`;
CREATE TABLE `merchant_order_amount_config`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `merchant_id` int NOT NULL DEFAULT -1 COMMENT '商户ID',
  `min_amount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '最小金额',
  `max_amount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '最大金额',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_merchant`(`merchant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户接单金额配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for old_t_merchantaccountrecord
-- ----------------------------
DROP TABLE IF EXISTS `old_t_merchantaccountrecord`;
CREATE TABLE `old_t_merchantaccountrecord`  (
  `MerchantID` int NULL DEFAULT NULL,
  `OrderID` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '天下订单号',
  `MerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `TransType` int NOT NULL COMMENT '交易方式,0：充值，1：提款，2 准备金，3 商户下发',
  `TransAmount` decimal(10, 2) NOT NULL COMMENT '交易金额',
  `PaidAmount` decimal(10, 2) NOT NULL COMMENT '实际交易金额',
  `PayChannelFee` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '渠道使用费',
  `SupplierPaidChannelFee` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商户需付使用费',
  `AgencyCommission` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理佣金',
  `PreBalance` decimal(14, 2) NULL DEFAULT 0.00 COMMENT '交易前余额',
  `Balance` decimal(14, 2) NULL DEFAULT 0.00 COMMENT '当前余额',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `SupplierBranchID` int NULL DEFAULT NULL,
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  INDEX `idx_MerchantOrderID`(`MerchantOrderID` ASC) USING BTREE,
  INDEX `idx_OrderID`(`OrderID` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户账变' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sn_information
-- ----------------------------
DROP TABLE IF EXISTS `sn_information`;
CREATE TABLE `sn_information`  (
  `sn_number` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'SN号',
  `sn_info` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'SN加密信息',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`sn_number`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'sn信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_agent_account_record
-- ----------------------------
DROP TABLE IF EXISTS `t_agent_account_record`;
CREATE TABLE `t_agent_account_record`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `agent_id` bigint NOT NULL COMMENT '代理ID',
  `merchant_id` bigint NOT NULL COMMENT '商户ID',
  `order_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '订单ID',
  `pay_amount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '下单金额',
  `paid_amount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '实际金额',
  `supplier_charge` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '商户服务费',
  `agent_charge` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '订单佣金',
  `pre_agent_charge` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '交易前佣金余额',
  `post_agent_charge` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '交易后佣金余额',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '节点ID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `trans_type` tinyint NOT NULL DEFAULT -1 COMMENT '交易类型',
  `merchant_order_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户订单号',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_indexOrderId`(`order_id` ASC) USING BTREE,
  INDEX `idx_agentid`(`agent_id` ASC) USING BTREE,
  INDEX `idx_create_at`(`created_at` ASC) USING BTREE COMMENT '创建时间索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '佣金收益记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_agent_bankcard_review
-- ----------------------------
DROP TABLE IF EXISTS `t_agent_bankcard_review`;
CREATE TABLE `t_agent_bankcard_review`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `agent_id` bigint NOT NULL DEFAULT 0 COMMENT '代理id',
  `supplierbranchid` int NOT NULL DEFAULT 0 COMMENT '财务分支',
  `review_status` int NOT NULL DEFAULT 0 COMMENT '审核状态 0:申请中，1:通过，2:拒绝',
  `applicant_id` bigint NOT NULL DEFAULT 0 COMMENT '申请人id',
  `applicant_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '申请人名称',
  `reviewer_id` bigint NOT NULL DEFAULT 0 COMMENT '审核人id',
  `reviewer` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '审核人名称',
  `befor_worktype` int NOT NULL DEFAULT 0 COMMENT '1:收款,2:出款,3:中转,4:下发,5:手动出款,6:风云出款,7:风云入款,8:结汇, 9:第三方,10:第四方,11:技术支持,12:U盾',
  `after_worktype` int NOT NULL DEFAULT 0 COMMENT '1:收款,2:出款,3:中转,4:下发,5:手动出款,6:风云出款,7:风云入款,8:结汇, 9:第三方,10:第四方,11:技术支持,12:U盾',
  `review_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '审核时间',
  `create_time` datetime NOT NULL COMMENT '申请时间',
  `bankcard_id` bigint NOT NULL DEFAULT 0 COMMENT '银行卡id',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '代理银行卡审核表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_agent_info
-- ----------------------------
DROP TABLE IF EXISTS `t_agent_info`;
CREATE TABLE `t_agent_info`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `agent_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理名称',
  `agent_type` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '代理类型，01：agent，02：recommend',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态，1启用 0停用',
  `supplier_branch_id` int NOT NULL DEFAULT 0 COMMENT '财务分支',
  `charge` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '总收益',
  `cur_charge` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '当前收益',
  `margin_charge` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '回款收益',
  `frozen_charge` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '冻结佣金',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '节点ID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_agent_type`(`agent_type` ASC) USING BTREE COMMENT '代理类型索引'
) ENGINE = InnoDB AUTO_INCREMENT = 147 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '代理信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_agent_member_config
-- ----------------------------
DROP TABLE IF EXISTS `t_agent_member_config`;
CREATE TABLE `t_agent_member_config`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `member_id` int NOT NULL COMMENT '成员ID',
  `member_type` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '成员类型, 00: merchant, 01:agent, 02:recommend',
  `deposit_parent_rebate` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '上级回U费率差',
  `deposit_plat_rebate` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '平台回U费率差',
  `parent_type` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '上级成员类型, 00: merchant, 01:agent, 02:recommend',
  `parent_id` int NOT NULL DEFAULT -1 COMMENT '上级成员ID, -1:平台',
  `visitable` tinyint NOT NULL DEFAULT 0 COMMENT '对间接上级可见',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '节点ID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deposit_fixed_rebate` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '商户固定汇率',
  `exchange_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '交易所：binance, okex',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_member_id_type`(`member_id` ASC, `member_type` ASC) USING BTREE,
  INDEX `idx_member_type_parent_id_visitable`(`member_type` ASC, `parent_id` ASC, `visitable` ASC) USING BTREE COMMENT '常见查询联合索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1110 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '代理上下级配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_agent_pay_type_rate
-- ----------------------------
DROP TABLE IF EXISTS `t_agent_pay_type_rate`;
CREATE TABLE `t_agent_pay_type_rate`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `agent_id` bigint NOT NULL COMMENT '代理ID',
  `merchant_id` bigint NOT NULL COMMENT '商户ID',
  `pay_type` int NOT NULL DEFAULT 0 COMMENT '支付方式',
  `agency_deposit_rate` decimal(5, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理返佣收款费率',
  `agency_withdraw_rate` decimal(5, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理返佣出款费率',
  `agency_withdraw_fixed_cost` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理出款固定费率',
  `member_recharge_rate` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '下级回U费率',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '1：启用，0:停用，-1:软删除',
  `remark` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '节点ID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_pay_type_merchant_agent`(`pay_type` ASC, `merchant_id` ASC, `agent_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '代理费率配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_agentbankcard
-- ----------------------------
DROP TABLE IF EXISTS `t_agentbankcard`;
CREATE TABLE `t_agentbankcard`  (
  `bankid` bigint NOT NULL COMMENT '银行卡id',
  `supplierbranchid` int NOT NULL DEFAULT -1 COMMENT '分部id',
  `banknum` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行卡号码',
  `bankcode` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行卡编码',
  `banktype` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行卡类型代码',
  `address` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '开户地址',
  `depositaddress` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '充值地址',
  `ownername` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '开户人',
  `ownerphone` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '开户人手机预留号码',
  `owneridentity` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '开户人身份证',
  `worktype` int NOT NULL DEFAULT -1 COMMENT '工作模式1:收款,2:付款',
  `balance` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '余额',
  `status` int NOT NULL DEFAULT 1 COMMENT '1：启用，0:停用，-1:软删除,-2:预启用, -3:交易限额停用 ,-4:余额超额停用,-5:交易笔数超额停用,-6:无转入转出停用,-7异常，-8临时停用，100：在线',
  `remark` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `createtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `lastupdatetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `cardindex` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行卡index',
  `createby` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `updatetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '提款密码',
  `agent_id` bigint NOT NULL DEFAULT 0 COMMENT '卡商代理ID',
  `agent_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理编码',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`bankid`) USING BTREE,
  UNIQUE INDEX `uniq_banknum`(`banknum` ASC, `bankcode` ASC) USING BTREE,
  UNIQUE INDEX `uniq_bankcode`(`bankcode` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '代理银行卡表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_agentcreditorder
-- ----------------------------
DROP TABLE IF EXISTS `t_agentcreditorder`;
CREATE TABLE `t_agentcreditorder`  (
  `orderindex` bigint NOT NULL AUTO_INCREMENT COMMENT '索引',
  `orderid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '订单号',
  `supplierbranchid` int NOT NULL DEFAULT 0 COMMENT '分部id',
  `paytype` int NOT NULL DEFAULT 0 COMMENT '充值方式,1:网银转账2:快捷支付3:银联支付4:微信5:支付宝6:qq钱包7:京东支付8:云闪付',
  `payamount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '提款金额',
  `paidamount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '实现到帐金额',
  `paidchannelfee` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '渠道手续费',
  `supplierpaidchannelfee` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '财务分支渠道费用',
  `notifyurl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户用于接收订单通知的地址',
  `returnurl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '订单提交后立即返回的商户页面地址',
  `bankcode` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行卡简码',
  `md5orderid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'md5签名',
  `paychannelid` int NOT NULL DEFAULT 0 COMMENT '处理该订单的充值渠道',
  `banknum` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '处理该订单的银行卡',
  `bankaccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '处理该订单的银行卡简称',
  `orderstatus` int NOT NULL DEFAULT 0 COMMENT '订单状态0:创建，默认值;1：处理中;2:清算中;3:正常支付关闭;4：手工确认订单已完成关闭; 5:超时,6:超时关闭；7:取消关闭',
  `orderpaidstatuschangetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单状态最后修改时间',
  `ordernotifystatus` int NOT NULL DEFAULT 0 COMMENT '订单通知状态',
  `ordernotifytime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单通知状态最后修改时间',
  `remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '附言',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '名称',
  `createtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `lastupdatetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `agent_id` bigint NOT NULL DEFAULT 0 COMMENT '卡商代理ID',
  `order_type` int NOT NULL DEFAULT 0 COMMENT '1为信用订单，2为转额度订单',
  `agent_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理编码',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`orderindex`) USING BTREE,
  INDEX `name_index`(`name` ASC) USING BTREE,
  INDEX `orderid_idx`(`orderid` ASC) USING BTREE,
  INDEX `name_remark_index`(`remark` ASC, `name` ASC) USING BTREE,
  INDEX `createtime_index`(`createtime` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '代理额度转换,信用订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_agentdepositorder
-- ----------------------------
DROP TABLE IF EXISTS `t_agentdepositorder`;
CREATE TABLE `t_agentdepositorder`  (
  `orderindex` bigint NOT NULL AUTO_INCREMENT COMMENT '索引',
  `orderid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '订单号',
  `supplierbranchid` int NOT NULL DEFAULT 0 COMMENT '分部id',
  `paytype` int NOT NULL DEFAULT 0 COMMENT '充值方式,1:网银转账2:快捷支付3:银联支付4:微信5:支付宝6:qq钱包7:京东支付8:云闪付',
  `payamount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '提款金额',
  `paidamount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '实现到帐金额',
  `paidchannelfee` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '渠道手续费',
  `supplierpaidchannelfee` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '财务分支渠道费用',
  `notifyurl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户用于接收订单通知的地址',
  `returnurl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '订单提交后立即返回的商户页面地址',
  `bankcode` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行卡简码',
  `md5orderid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'md5签名',
  `paychannelid` int NOT NULL DEFAULT 0 COMMENT '处理该订单的充值渠道',
  `banknum` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '处理该订单的银行卡',
  `bankaccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '处理该订单的银行卡简称',
  `orderstatus` int NOT NULL DEFAULT 0 COMMENT '订单状态0:创建，默认值;1：处理中;2:清算中;3:正常支付关闭;4：手工确认订单已完成关闭; 5:超时,6:超时关闭；7:取消关闭',
  `orderpaidstatuschangetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单状态最后修改时间',
  `ordernotifystatus` int NOT NULL DEFAULT 0 COMMENT '订单通知状态',
  `ordernotifytime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单通知状态最后修改时间',
  `remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '附言',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '名称',
  `createtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `lastupdatetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `agent_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理编码',
  `approvePersonnel` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '审核人员',
  `approveRemark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '审核备注',
  `approveTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
  `agent_id` int NOT NULL DEFAULT 0 COMMENT '代理Id',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`orderindex`) USING BTREE,
  INDEX `orderid_idx`(`orderid` ASC) USING BTREE,
  INDEX `name_remark_index`(`remark` ASC, `name` ASC) USING BTREE,
  INDEX `name_index`(`name` ASC) USING BTREE,
  INDEX `createtime_index`(`createtime` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '代理银行卡充值订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_agentwithdraworder
-- ----------------------------
DROP TABLE IF EXISTS `t_agentwithdraworder`;
CREATE TABLE `t_agentwithdraworder`  (
  `orderindex` bigint NOT NULL COMMENT '订单索引',
  `orderid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '订单id',
  `ordernotifytime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单通知时间',
  `supplierbranchid` int NOT NULL DEFAULT -1 COMMENT '财务分部id',
  `paytype` int NOT NULL DEFAULT -1 COMMENT '付款方式,1：银行卡，2：第三方，3：其他方式',
  `payamount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '提款金额',
  `paidamount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '实现到帐',
  `notifyurl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '通知地址',
  `returnurl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '回调地址',
  `paychannelid` int NOT NULL DEFAULT 0 COMMENT '处理该订单的充值渠道',
  `bankaccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行帐号',
  `orderstatus` int NOT NULL DEFAULT 0 COMMENT '订单状态',
  `ordernotifystatus` int NOT NULL DEFAULT 0 COMMENT '订单通知状态',
  `remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `bankcode` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行卡简码',
  `banknum` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行卡号',
  `bankaddress` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行卡地址',
  `bankowner` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行卡持有者',
  `md5orderid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'md5订单加密信息',
  `digest` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'digest',
  `ordertype` int NOT NULL DEFAULT 0 COMMENT '订单类型',
  `createtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `agent_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理编码',
  `lastupdatetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `agent_id` bigint NOT NULL DEFAULT 0 COMMENT '代理id',
  `agent_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理名称',
  `approvePersonnel` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '审核人员',
  `approveRemark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '审核备注',
  `approveTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`orderindex`) USING BTREE,
  UNIQUE INDEX `uniq_orderid`(`orderid` ASC) USING BTREE,
  INDEX `supplierbranchid_index`(`supplierbranchid` ASC) USING BTREE,
  INDEX `orderid_idx`(`orderid` ASC) USING BTREE,
  INDEX `digest_index`(`digest` ASC) USING BTREE,
  INDEX `createtime_index`(`createtime` ASC) USING BTREE,
  INDEX `create_ordernotify_ordertype`(`ordertype` ASC, `ordernotifystatus` ASC, `createtime` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '卡商提款订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_b2binternaltransorder
-- ----------------------------
DROP TABLE IF EXISTS `t_b2binternaltransorder`;
CREATE TABLE `t_b2binternaltransorder`  (
  `OrderIndex` bigint NOT NULL COMMENT '订单索引',
  `OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `SupplierBranchID` int NULL DEFAULT NULL COMMENT '分部ID',
  `OutputBankID` int NULL DEFAULT NULL COMMENT '转出银行卡ID',
  `OutputBankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `InputBankID` int NULL DEFAULT NULL COMMENT '转入银行卡ID',
  `InputBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `InputBankOnwer` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `InputBankAddress` varchar(270) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '转入银行卡开户地址',
  `TradeAmount` decimal(10, 2) NULL DEFAULT NULL,
  `Status` int NULL DEFAULT 0 COMMENT '订单状态0:创建，默认值;1：处理中;2:清算中;3:正常支付关闭;4：手工确认订单已完成关闭; 5:超时,6:超时关闭；7:取消关闭',
  `BankCode` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡类型代码',
  `OrderType` int NULL DEFAULT NULL COMMENT '订单类型,1:超额内转订单，2低额内转订单，3，充值测试转账订单,4提款测试转账订单,5卡异常转出金额,100父订单',
  `Remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `LastUpDateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `EncryptOutputBankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '转出银行卡简码',
  `EncryptInputBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '转入银行卡号码',
  `EncryptInputBankOnwer` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '转入姓名',
  `EncryptInputBankAddress` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '转入银行卡开户地址',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`OrderIndex`) USING BTREE,
  UNIQUE INDEX `uniq_OrderID`(`OrderID` ASC) USING BTREE,
  INDEX `idx_createtime`(`CreateTime` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_bank_sms_template
-- ----------------------------
DROP TABLE IF EXISTS `t_bank_sms_template`;
CREATE TABLE `t_bank_sms_template`  (
  `id` bigint NOT NULL COMMENT '表ID',
  `sms_phone_no` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行短信号码，分号分隔',
  `sms_template` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '短信模板',
  `bank_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行编码',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int NOT NULL DEFAULT 0 COMMENT '版本号',
  `operator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '操作人',
  `sms_content` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '短信内容，分号分隔',
  `sms_status` tinyint NOT NULL DEFAULT 0 COMMENT '状态 0禁用 1启用',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_bank_code`(`bank_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '银行短信模板表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_bank_type_strict
-- ----------------------------
DROP TABLE IF EXISTS `t_bank_type_strict`;
CREATE TABLE `t_bank_type_strict`  (
  `BankTypeID` int NOT NULL AUTO_INCREMENT COMMENT '银行卡类型ID',
  `BankTypeName` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '银行卡类型名称',
  `BankTypeCode` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '银行卡类型代码',
  `ReMark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `CreateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `LastUpDateTime` datetime NULL DEFAULT NULL,
  `CurrencyType` int NULL DEFAULT 0 COMMENT '币种类型  0 RMB  1虚拟币',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`BankTypeID`) USING BTREE,
  UNIQUE INDEX `BankTypeName_UNIQUE`(`BankTypeName` ASC) USING BTREE,
  UNIQUE INDEX `BankTypeCode_UNIQUE`(`BankTypeCode` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '银行卡类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_bankaccountrecord
-- ----------------------------
DROP TABLE IF EXISTS `t_bankaccountrecord`;
CREATE TABLE `t_bankaccountrecord`  (
  `BankID` int NOT NULL COMMENT '银行卡ID',
  `TransID` bigint NOT NULL COMMENT '交易流水ID',
  `OrderIndex` bigint NULL DEFAULT NULL COMMENT '平台订单号索引',
  `MerchantOrderIndex` bigint NULL DEFAULT NULL COMMENT '商户订单号索引',
  `OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `MerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `TransType` int NULL DEFAULT NULL COMMENT '交易方式,0：收款，1：付款 ,2:内转',
  `TransAmount` decimal(10, 2) NULL DEFAULT NULL,
  `PaidAmount` decimal(10, 2) NULL DEFAULT NULL,
  `PayChannelFee` decimal(10, 2) NULL DEFAULT NULL,
  `SupplierPaidChannelFee` decimal(20, 2) NULL DEFAULT NULL COMMENT '商户手续费',
  `PreBalance` decimal(10, 2) NULL DEFAULT NULL,
  `Balance` decimal(10, 2) NULL DEFAULT NULL,
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `SupplierBranchID` int NOT NULL,
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`BankID`, `TransID`) USING BTREE,
  INDEX `idx_transid_orderid`(`TransID` ASC, `OrderID` ASC) USING BTREE,
  INDEX `idx_MerchantOrderID`(`MerchantOrderID` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '银行卡账变表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_bankcard
-- ----------------------------
DROP TABLE IF EXISTS `t_bankcard`;
CREATE TABLE `t_bankcard`  (
  `BankID` int NOT NULL COMMENT '银行卡ID',
  `SupplierBranchID` int NULL DEFAULT NULL COMMENT '分部ID',
  `BankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡号码',
  `LoginAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡网银登录账号',
  `BankCode` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡编码',
  `PoolID` int NULL DEFAULT NULL COMMENT '所属银行卡池',
  `Auth` int NULL DEFAULT NULL COMMENT '查看员',
  `BankType` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡类型代码',
  `Address` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开户地址',
  `DepositAddress` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '充值地址',
  `OwnerName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开户人',
  `OwnerPhone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开户人手机预留号码',
  `OwnerIdentity` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开户人身份证',
  `WorkType` int NULL DEFAULT NULL COMMENT '工作模式1:收款,2:付款',
  `Balance` decimal(10, 2) NULL DEFAULT NULL COMMENT '余额',
  `MaxBalance` decimal(10, 2) NULL DEFAULT NULL COMMENT '最大余额，超过则要转到其他卡,恢复到InitBalance',
  `MinBalance` decimal(10, 2) NULL DEFAULT NULL COMMENT '最小额度，少于则要转入额度到InitBalance',
  `InitBalance` decimal(10, 2) NULL DEFAULT NULL COMMENT '初始余额',
  `SumAmountPerDayTrans` decimal(10, 2) NULL DEFAULT NULL COMMENT '每日交易总额限额,默认0不限制',
  `SumTimesPerDayTrans` decimal(10, 2) NULL DEFAULT NULL COMMENT '每日转出笔数限额,默认0不限制',
  `MinAmountPerTrans` decimal(10, 2) NULL DEFAULT NULL COMMENT '每笔交易最小额度,默认0不限制',
  `WMinAmountPerTrans` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '出款每笔交易最小额度,默认0不限制，作用于提款和内转转出',
  `MaxAmountPerTrans` decimal(10, 2) NULL DEFAULT NULL COMMENT '每笔交易最大额度,默认0不限制',
  `WMaxAmountPerTrans` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '每笔交易最大额度,默认0不限制, 作用于提款和内转转出',
  `AliSumTimesPerDayTrans` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付宝每日转出笔数限额,默认0不限制',
  `AliSumAmountPerDayTrans` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付宝每日交易总额限额,默认0不限制',
  `AliMinAmountPerTrans` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付宝每笔交易最小额度,默认0不限制',
  `AliMaxAmountPerTrans` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付宝每笔交易最大额度,默认0不限制',
  `Status` int NULL DEFAULT NULL COMMENT '1：启用，0:停用，-1:软删除,-2:预启用, -3:交易限额停用 ,-4:余额超额停用,-5:交易笔数超额停用,-6:无转入转出停用,-7异常，-8临时停用，100：在线',
  `Comments` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '人工备注',
  `ReMark` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '系统自动备注',
  `Version` int NULL DEFAULT NULL,
  `ULoginPWD` varchar(516) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'U盾登录密码',
  `UkeyPWD` varchar(516) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'U盾密码',
  `LoginPWD` varchar(516) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '登录密码，加密',
  `PayPWD` varchar(516) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付密码，加密',
  `UCode` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '子账号',
  `TagID` int NULL DEFAULT NULL COMMENT '银行卡标签',
  `PhoneID` int NULL DEFAULT NULL COMMENT '手机ID',
  `LastLoginClientName` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '最后一次登录的自动机',
  `CreateTime` datetime NULL DEFAULT NULL,
  `LastUpDateTime` datetime NULL DEFAULT NULL,
  `CardIndex` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡Index',
  `authes` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '保存多个权限名称',
  `CreateBy` int NOT NULL DEFAULT 0 COMMENT '创建人',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `updateBy` int NOT NULL DEFAULT 0 COMMENT '修改人',
  `surplier_type` int NOT NULL DEFAULT 0 COMMENT '卡类型（0为天下卡，1为卡商卡，默认0）',
  `agent_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '卡商代理ID',
  `qrcode` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '二维码url',
  `EncryptBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡号码',
  `EncryptLoginAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡网银登录账号',
  `EncryptBankCode` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡编码',
  `EncryptAddress` varchar(360) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开户地址',
  `EncryptDepositAddress` varchar(270) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '充值地址',
  `EncryptOwnerName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开户人',
  `EncryptOwnerPhone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开户人手机预留号码',
  `EncryptOwnerIdentity` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开户人身份证',
  `EncryptUcode` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '子账号',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `IsSmsMatch` tinyint NOT NULL DEFAULT 0 COMMENT '是否短信匹配 0否 1是',
  `FromCashier` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否来自卡商 0否 1是',
  `CashierId` bigint NOT NULL DEFAULT 0 COMMENT '卡商ID',
  `ReviewedTime` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `multiSwitch` int NULL DEFAULT NULL COMMENT '并发开关:0关闭;1开启',
  PRIMARY KEY (`BankID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '银行卡表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_bankcard_agent
-- ----------------------------
DROP TABLE IF EXISTS `t_bankcard_agent`;
CREATE TABLE `t_bankcard_agent`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '代理ID',
  `supplier_branch_id` int NOT NULL DEFAULT 0 COMMENT '财务分支',
  `agent_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理编码',
  `agent_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理名称',
  `parent_agent_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '父级代理编码',
  `balance` decimal(20, 2) NOT NULL DEFAULT 0.00 COMMENT '总余额',
  `credit_balance` decimal(20, 2) NOT NULL DEFAULT 0.00 COMMENT '信用余额',
  `available_balance` decimal(20, 2) NOT NULL DEFAULT 0.00 COMMENT '可用余额',
  `fronzen_balance` decimal(20, 2) NOT NULL DEFAULT 0.00 COMMENT '冻结余额',
  `ExtraBalance` decimal(20, 2) NOT NULL DEFAULT 0.00 COMMENT '额外支出余额',
  `ratio` decimal(20, 2) NOT NULL DEFAULT 0.00 COMMENT '费率',
  `profit_balance` decimal(20, 2) NOT NULL DEFAULT 0.00 COMMENT '收益',
  `status` int NOT NULL DEFAULT 1 COMMENT '1：启用，0:停用，-1:软删除',
  `remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` bigint NOT NULL DEFAULT 0 COMMENT '版本',
  `user_id` bigint NOT NULL DEFAULT -1 COMMENT '卡商代理登录账号',
  `agent_level` int NOT NULL DEFAULT -1 COMMENT '代理层级：0为卡商（总代），1为一级代理',
  `createby` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `parent_agent_id` bigint NOT NULL DEFAULT 0 COMMENT '父卡商代理ID',
  `pay_code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '支付密码（提款密码）',
  `parent_supplier_branch_id` int NOT NULL DEFAULT 0 COMMENT '父级财务分支',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '卡商代理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_bankcard_agent_accountrecord
-- ----------------------------
DROP TABLE IF EXISTS `t_bankcard_agent_accountrecord`;
CREATE TABLE `t_bankcard_agent_accountrecord`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `agent_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理编码',
  `supplier_branch_id` int NOT NULL DEFAULT 0 COMMENT '财务分支',
  `order_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '订单ID',
  `bank_id` int NOT NULL DEFAULT 0 COMMENT '银行卡ID',
  `trans_type` int NOT NULL DEFAULT -1 COMMENT '交易方式,0：充值，1：提款，2 交押金，3 退押金 4 利润转押金',
  `trans_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '交易金额',
  `paid_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '实际交易金额',
  `balance` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '余额',
  `available_balance` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '可用余额',
  `frozen_balance` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '冻结余额',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `agent_id` bigint NOT NULL DEFAULT 0 COMMENT '卡商代理ID',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time_agent`(`create_time` ASC, `agent_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '卡商卡账变' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_bankcard_agent_manual_record
-- ----------------------------
DROP TABLE IF EXISTS `t_bankcard_agent_manual_record`;
CREATE TABLE `t_bankcard_agent_manual_record`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '平台订单索引',
  `supplier_branch_id` int NOT NULL DEFAULT 0 COMMENT '财务分支',
  `agent_id` bigint NOT NULL DEFAULT 0 COMMENT '卡商代理ID',
  `agent_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理编码',
  `type` int NOT NULL DEFAULT -1 COMMENT '帐变类型,0：调整可用额度，1：调整额外支付余额',
  `reviseBalance` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '调整金额',
  `before_balance` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '调整前余额',
  `balance` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '余额',
  `before_available_balance` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '调整前可用余额',
  `available_balance` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '可用余额',
  `before_extra_balance` decimal(20, 2) NOT NULL DEFAULT 0.00 COMMENT '调整前额外支出余额',
  `extra_balance` decimal(20, 2) NOT NULL DEFAULT 0.00 COMMENT '额外支出余额',
  `frozen_balance` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '冻结余额',
  `remark` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_agent_code`(`agent_code` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '卡商卡手工账变表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_bankcard_valid
-- ----------------------------
DROP TABLE IF EXISTS `t_bankcard_valid`;
CREATE TABLE `t_bankcard_valid`  (
  `id` bigint NOT NULL COMMENT '银行卡ID',
  `bank_type` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行卡类型代码',
  `encrypt_bank_num` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行卡号码',
  `encrypt_owner_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '开户人',
  `encrypt_address` varchar(360) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '开户地址',
  `approve_code` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '认证编码',
  `approve_desc` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '认证描述',
  `approve_order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '认证订单号',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_encrypt_bank_num`(`encrypt_bank_num` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '有效银行卡信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_bankcardpool
-- ----------------------------
DROP TABLE IF EXISTS `t_bankcardpool`;
CREATE TABLE `t_bankcardpool`  (
  `BankPooID` int NOT NULL COMMENT '银行卡池ID',
  `SupplierBranchID` int NULL DEFAULT NULL COMMENT '财务分部ID',
  `BankPoolName` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡池名称',
  `WorkType` int NULL DEFAULT NULL,
  `Status` int NULL DEFAULT NULL COMMENT '1：启用，0:停用，-1:软删除',
  `RoleType` int NULL DEFAULT NULL COMMENT '0入款卡池 1出款卡池 2下发卡池 3其他卡池 4中转卡池',
  `InputPoolID` int NULL DEFAULT NULL COMMENT '向本池转入资金的卡池',
  `OutputPoolID` int NULL DEFAULT NULL COMMENT '从本池转出资金的卡池',
  `ReMark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `LastUpDateTime` datetime NULL DEFAULT NULL,
  `OutPutTestPoolID` int NULL DEFAULT NULL COMMENT '从本池转出资金的测试卡池',
  `OutputPoolIDs` varchar(168) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '从本池转出资金的卡池,多个逗号分隔',
  `surplier_type` int NOT NULL DEFAULT 0 COMMENT '卡类型（0为天下卡，1为卡商卡，默认0）',
  `MaxAmountPerTrans` decimal(8, 2) NOT NULL DEFAULT 0.00 COMMENT '单笔交易最大限额',
  `MinAmountPerTrans` decimal(8, 2) NOT NULL DEFAULT 0.00 COMMENT '单笔交易最小限额',
  `Node_ID` tinyint NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`BankPooID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '银行卡池' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_bankcardsummary
-- ----------------------------
DROP TABLE IF EXISTS `t_bankcardsummary`;
CREATE TABLE `t_bankcardsummary`  (
  `BankID` int NOT NULL,
  `DepositAmount` decimal(10, 2) NOT NULL,
  `HistoryDepositAmount` decimal(20, 2) NULL DEFAULT NULL,
  `WithdrawAmount` decimal(10, 2) NOT NULL,
  `Calendar` date NOT NULL,
  `LastUpDateTime` datetime NOT NULL,
  `DepositTimes` int NULL DEFAULT NULL,
  `WithdrawTimes` int NULL DEFAULT NULL,
  `Version` bigint NOT NULL,
  `AliDepositTimes` int NULL DEFAULT NULL COMMENT '支付宝日次数',
  `AliDepositAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付宝日存款',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`BankID`, `Calendar`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_bankclient_copy1
-- ----------------------------
DROP TABLE IF EXISTS `t_bankclient_copy1`;
CREATE TABLE `t_bankclient_copy1`  (
  `ClientID` int NOT NULL AUTO_INCREMENT COMMENT '客户端ID,自增',
  `SupplierBranchID` int NULL DEFAULT NULL,
  `BankType` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '客户端类型,1招行客户端CMB，2:建行客户端CCB ',
  `ClientName` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '客户端名称',
  `Status` int NULL DEFAULT NULL COMMENT '服务状态，1：启用，0:停用，-1:软删除,100:上线，-100异常',
  `Identity` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证码,登录时发送身份证码用于验证该客户端合法',
  `BankAccount` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡简码，当前绑定银行卡',
  `LogoutMsg` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `ReMark` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `CreateTime` datetime NULL DEFAULT NULL,
  `LastUpDateTime` datetime NULL DEFAULT NULL,
  `WorkType` int NULL DEFAULT NULL COMMENT '工作类型：1收款,2:付款, 3收付款',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`ClientID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '银行卡客户端' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_bankinchannel
-- ----------------------------
DROP TABLE IF EXISTS `t_bankinchannel`;
CREATE TABLE `t_bankinchannel`  (
  `BankID` int NOT NULL,
  `ChannelID` int NOT NULL,
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`BankID`, `ChannelID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_bankinpool
-- ----------------------------
DROP TABLE IF EXISTS `t_bankinpool`;
CREATE TABLE `t_bankinpool`  (
  `BankID` int NOT NULL,
  `PoolID` int NOT NULL,
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`BankID`, `PoolID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_banktag
-- ----------------------------
DROP TABLE IF EXISTS `t_banktag`;
CREATE TABLE `t_banktag`  (
  `TagID` int NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `SupplierBranchID` int NULL DEFAULT NULL,
  `TagName` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `Remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`TagID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '银行标签表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_banktrans
-- ----------------------------
DROP TABLE IF EXISTS `t_banktrans`;
CREATE TABLE `t_banktrans`  (
  `ID` bigint NOT NULL AUTO_INCREMENT COMMENT '流水ID',
  `SupplierBranchID` int NULL DEFAULT NULL COMMENT '财务分部ID',
  `ClientID` int NULL DEFAULT NULL COMMENT '客户端编号',
  `BankCode` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行类型代码',
  `BankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行简记码',
  `BankID` int NULL DEFAULT NULL COMMENT '银行卡编号',
  `PayChannelID` int NULL DEFAULT NULL COMMENT '渠道编号,内部卡转卡为0',
  `TransTime` datetime NULL DEFAULT NULL COMMENT '交易时间',
  `TransFee` decimal(10, 2) NULL DEFAULT NULL COMMENT '手续费',
  `TransType` int NULL DEFAULT NULL COMMENT '交易方式:0支出,1:收入',
  `TransAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '交易金额',
  `PreBalance` decimal(10, 2) NULL DEFAULT NULL COMMENT '交易前余额',
  `Balance` decimal(10, 2) NULL DEFAULT NULL COMMENT '当前余额',
  `Remark` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '附言',
  `Name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '交易姓名',
  `TransAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '交易银行卡',
  `ReadMark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '加锁标记',
  `Status` int NULL DEFAULT 0 COMMENT '处理状态，0：创建，1：处理中，2：已成功处理，3：超时移入异常收支',
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `PickTime` datetime NULL DEFAULT NULL COMMENT '被抓取时间',
  `Md5Sign` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'md5后的唯一编码',
  `MerchantID` int NULL DEFAULT NULL COMMENT '商户id',
  `MemberTransAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '会员交易账号',
  `EncryptBankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行简记码',
  `EncryptName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '交易姓名',
  `EncryptTransAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '交易银行卡',
  `MerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户订单号',
  `Operator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '操作员登录名',
  `ReceiptID` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '回执单号',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `DeviceType` tinyint(1) NOT NULL DEFAULT 0 COMMENT '设备类型 0PC自动机 1手机银行',
  `BankReplyCode` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行回传编码：唯一编码',
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `idx_CreateTime`(`CreateTime` ASC) USING BTREE,
  INDEX `idx_BankAccount`(`BankAccount` ASC) USING BTREE,
  INDEX `idx_Name`(`Name` ASC) USING BTREE,
  INDEX `idx_STATUS`(`Status` ASC) USING BTREE,
  INDEX `idx_TransAccount`(`TransAccount` ASC) USING BTREE,
  INDEX `idx_TransAmount`(`TransAmount` ASC) USING BTREE,
  INDEX `idx_TransType`(`TransType` ASC) USING BTREE,
  INDEX `idx_BankReplyCode`(`BankReplyCode` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '银行卡流水表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_banktype
-- ----------------------------
DROP TABLE IF EXISTS `t_banktype`;
CREATE TABLE `t_banktype`  (
  `BankTypeID` int NOT NULL AUTO_INCREMENT COMMENT '银行卡类型ID',
  `BankTypeName` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡类型名称',
  `BankTypeCode` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '银行卡类型代码',
  `ReMark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `LastUpDateTime` datetime NULL DEFAULT NULL,
  `currency_type` int NOT NULL DEFAULT 0 COMMENT '币种 0、人民币银行 1、虚拟币币种',
  `CurrencyType` int NULL DEFAULT 0 COMMENT '币种类型  0 RMB  1虚拟币',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`BankTypeID`) USING BTREE,
  UNIQUE INDEX `BankTypeCode_UNIQUE`(`BankTypeCode` ASC) USING BTREE,
  UNIQUE INDEX `BankTypeName_UNIQUE`(`BankTypeName` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '银行卡类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_buffer_order_record
-- ----------------------------
DROP TABLE IF EXISTS `t_buffer_order_record`;
CREATE TABLE `t_buffer_order_record`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `redis_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'redisKey',
  `member` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'sortset成员',
  `score` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'sortset成员分数',
  `order_status` tinyint NOT NULL DEFAULT 0 COMMENT '异常记录状态',
  `fail_num` int NOT NULL DEFAULT 1 COMMENT '写缓存失败次数',
  `crt_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `upt_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_crt_time`(`crt_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '缓存异常记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_business_withdraw_order
-- ----------------------------
DROP TABLE IF EXISTS `t_business_withdraw_order`;
CREATE TABLE `t_business_withdraw_order`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `supplier_branch_id` int NOT NULL DEFAULT 0 COMMENT '财务分支',
  `merchant_id` int NULL DEFAULT NULL COMMENT '商户编号',
  `merchant_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户名称',
  `order_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单号',
  `merchant_order_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户订单号',
  `user_credit_level` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户层级，根据用户层级等级分配不同支付渠道;多个层级，使用‘,’分割，比如支持5_1,5_2层级，则是5_1,5_2',
  `user_level` int NULL DEFAULT -1 COMMENT '会员等级',
  `pay_type` int NULL DEFAULT NULL COMMENT '付款方式,1：银行卡，2：第三方，3：其他方式',
  `pay_amount` decimal(18, 4) NULL DEFAULT 0.0000 COMMENT '订单金额',
  `paid_amount` decimal(18, 4) NULL DEFAULT 0.0000 COMMENT '实际金额',
  `supplier_paid_channel_fee` decimal(18, 4) NULL DEFAULT NULL COMMENT '渠道提供方手续费，比如需向三方通道付手续费，比如平均银行卡磨损费',
  `order_status` int NULL DEFAULT 0 COMMENT '订单状态 0创建,1处理中,3成功,7失败',
  `order_paid_status_change_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单状态最后修改时间',
  `order_notify_status` int NULL DEFAULT 0 COMMENT '订单通知状态 0未成功通知, 1已成功通知, 2通知超时异常',
  `order_type` int NULL DEFAULT 0 COMMENT '订单类型 0会员提款, 1商户提款',
  `encrypt_bank_account` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户银行卡简码',
  `encrypt_bank_owner` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '提款人',
  `encrypt_bank_num` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡号码',
  `encrypt_member_account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员账号',
  `encrypt_bank_address` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '开户地址',
  `amount_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'CNY' COMMENT '金额类型 默认CNY 具体数值参考币种',
  `virtual_pay_amount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币金额',
  `virtual_paid_amount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币金额',
  `remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '节点ID',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_merchant_order_id`(`merchant_order_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_merchant_order_id_pay_amount`(`merchant_order_id` ASC, `pay_amount` ASC) USING BTREE,
  INDEX `idx_order_paid_status_change_time`(`order_paid_status_change_time` ASC) USING BTREE,
  INDEX `idx_supplier_branch_id`(`supplier_branch_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户前台-会员提现订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_callback_domain
-- ----------------------------
DROP TABLE IF EXISTS `t_callback_domain`;
CREATE TABLE `t_callback_domain`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `cdn_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'CDN厂商名',
  `domain_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '域名地址',
  `status` int NOT NULL DEFAULT 0 COMMENT '服务状态(-1:删除,0:停用,1:启用)',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '创建者',
  `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '修改者',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `domain_type` tinyint UNSIGNED NOT NULL DEFAULT 1 COMMENT '域名类型（1：三方回调域名，2：平台反查域名，默认1）',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '回调域名' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_cashier_login_log
-- ----------------------------
DROP TABLE IF EXISTS `t_cashier_login_log`;
CREATE TABLE `t_cashier_login_log`  (
  `id` bigint NOT NULL COMMENT 'ID',
  `login_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '登录账号',
  `login_time` datetime NOT NULL COMMENT '登录时间',
  `ip_addr` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '登录地点',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '提示消息',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `supplier_branch_id` int NOT NULL DEFAULT 0 COMMENT '财务分部ID',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '卡商登录记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_cashier_user
-- ----------------------------
DROP TABLE IF EXISTS `t_cashier_user`;
CREATE TABLE `t_cashier_user`  (
  `id` bigint NOT NULL COMMENT '主键',
  `user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '账号',
  `pass_word` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '密码',
  `user_status` tinyint NOT NULL DEFAULT 1 COMMENT '用户状态  1启用  0禁用',
  `del_flag` tinyint NOT NULL DEFAULT 0 COMMENT '删除标识 0正常 1删除',
  `login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '最后登陆IP',
  `login_date` datetime NOT NULL DEFAULT '1970-01-01 08:00:00' COMMENT '最后登陆时间',
  `create_by` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `google_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '谷歌验证码',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `supplier_branch_id` int NOT NULL DEFAULT 0 COMMENT '财务分支',
  `last_login_date` datetime NOT NULL DEFAULT '1970-01-01 08:00:00' COMMENT '上一次登录时间',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_user_name`(`user_name` ASC) USING BTREE COMMENT '用户名唯一'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '卡商账号表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_channelaccountrecord
-- ----------------------------
DROP TABLE IF EXISTS `t_channelaccountrecord`;
CREATE TABLE `t_channelaccountrecord`  (
  `OrderIndex` bigint NOT NULL COMMENT '平台订单索引',
  `PayChannelID` int NULL DEFAULT NULL COMMENT '渠道ID',
  `OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `ChannelOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '渠道订单号',
  `TransType` int NULL DEFAULT NULL COMMENT '交易方式,0：充值，1：提款',
  `TransAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '交易金额',
  `PaidAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '实际支付金额',
  `SupplierPaidChannelFee` decimal(10, 2) NULL DEFAULT NULL COMMENT '商户需付手续费',
  `PayChannelFee` decimal(10, 2) NULL DEFAULT NULL,
  `SurpplierFee` decimal(10, 2) NULL DEFAULT NULL COMMENT '渠道提供方手续费，比如需向三方通道付手续费，比如平均银行卡磨损费',
  `PreBalance` decimal(14, 2) NULL DEFAULT 0.00 COMMENT '交易前余额',
  `Balance` decimal(14, 2) NULL DEFAULT 0.00 COMMENT '当前余额',
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `SupplierBranchID` int NULL DEFAULT NULL,
  `MerchantID` int NOT NULL DEFAULT 0 COMMENT '渠道ID',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`OrderIndex`) USING BTREE,
  INDEX `paychannelid_index`(`PayChannelID` ASC) USING BTREE,
  INDEX `ChannelOrderID_index`(`ChannelOrderID` ASC, `PayChannelID` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '渠道账变表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_client_version
-- ----------------------------
DROP TABLE IF EXISTS `t_client_version`;
CREATE TABLE `t_client_version`  (
  `BankCode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行类型',
  `Version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '版本号',
  `Md5Key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'md5key',
  `Status` int NOT NULL DEFAULT 1 COMMENT '状态 1 启用 0停用 -1 软删除',
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `CreateBy` int NOT NULL DEFAULT 0 COMMENT '创建人',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `updateBy` int NOT NULL DEFAULT 0 COMMENT '修改人',
  `ActuatorName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '执行器名称英文',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '银行版本号' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_depositorder
-- ----------------------------
DROP TABLE IF EXISTS `t_depositorder`;
CREATE TABLE `t_depositorder`  (
  `OrderIndex` bigint NOT NULL COMMENT '订单索引',
  `OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单号',
  `SupplierBranchID` int NULL DEFAULT NULL COMMENT '分部ID',
  `MerchantID` int NULL DEFAULT NULL COMMENT '商户编号',
  `MerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户订单号最长64位',
  `UserLevel` int NULL DEFAULT NULL COMMENT '商户会员等级',
  `UserID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户会员唯一ID',
  `MerchantOrderTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `PayType` int NULL DEFAULT NULL COMMENT '充值方式,1:网银转账2:快捷支付3:银联支付4:微信5:支付宝6:QQ钱包7:京东支付8:云闪付',
  `ClientType` int NULL DEFAULT NULL COMMENT '客户端类型, 1：PC，2:WAP，4:APP',
  `PayAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '充值金额',
  `PaidAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '实际到账金额',
  `PaidChannelFee` decimal(10, 2) NULL DEFAULT NULL COMMENT '商户需付手续费',
  `SupplierPaidChannelFee` decimal(10, 2) NULL DEFAULT NULL COMMENT '渠道提供方手续费，比如需向三方通道付手续费，比如平均银行卡磨损费',
  `AgencyCommission` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理佣金',
  `NotifyUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户用于接收订单通知的地址',
  `ReturnUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单提交后立即返回的商户页面地址',
  `MerchantRemark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `ChannelOrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '平台提交给充值渠道的订单号, GUID32位',
  `BankCode` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行类型代码',
  `MD5OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'MD5签名',
  `PayChannelID` int NULL DEFAULT NULL COMMENT '处理该订单的充值渠道',
  `BankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理该订单的银行卡',
  `BankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理该订单的银行卡简称',
  `OrderStatus` int NULL DEFAULT 0 COMMENT '订单状态0:创建，默认值;1：处理中;2:清算中;3:正常支付关闭;4：手工确认订单已完成关闭; 5:超时,6:超时关闭；7:取消关闭',
  `OrderPaidStatusChangeTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单状态最后修改时间',
  `OrderNotifyStatus` int NULL DEFAULT NULL COMMENT '通知状态；0:未成功通知；1：已成功通知;2:通知超时异常',
  `OrderNotifyTime` datetime NULL DEFAULT NULL COMMENT '订单通知状态最后修改时间',
  `Remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '附言',
  `Name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `ReadMark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用于标注是否被自动付款取走',
  `CloseReason` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关闭原因',
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `UserIP` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'ip',
  `Operator` int NULL DEFAULT 0 COMMENT '操作员',
  `MemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '会员账号',
  `UserCreditLevel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户层级，根据用户层级等级分配不同支付渠道;多个层级，使用‘,’分割，比如支持5_1,5_2层级，则是5_1,5_2',
  `MemberBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户的存款银行卡',
  `EncryptBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理该订单的银行卡',
  `EncryptBankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理该订单的银行卡简称',
  `EncryptName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员姓名',
  `EncryptMemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员账号',
  `EncryptMemberBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户的存款银行卡',
  `SourceType` tinyint NOT NULL DEFAULT 0 COMMENT '提单方式',
  `ManualRemark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '人工备注',
  `AutoMataType` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '自动机客户端类型  0 PC 1 手机银行',
  `EncryptDepositName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '真实存款人名字',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `VirtualPayAmount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币金额',
  `VirtualPaidAmount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币实际金额',
  `VirtualRate` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币浮动汇率',
  `VirtualBasicRate` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币基础汇率',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据入库时间',
  `IsSmsMatch` tinyint NOT NULL DEFAULT 0 COMMENT '是否短信匹配 0否 1是',
  `appSite` tinyint NOT NULL DEFAULT 0 COMMENT '会员归属类型:0空;1内部为KY,BOB,BW;2外部',
  `risk_type` tinyint NOT NULL DEFAULT 0 COMMENT '风控类型 0:未风控 1:中台风控 2:天下风控',
  `is_face_order` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否为刷脸订单 0否 1是',
  `FromCashier` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否来自卡商 0否 1是',
  `CashierId` bigint NOT NULL DEFAULT 0 COMMENT '卡商ID',
  `shoppingUrl` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商城链接Url',
  `ImageAddr` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '图片地址:英文逗号隔开',
  PRIMARY KEY (`OrderIndex`) USING BTREE,
  UNIQUE INDEX `MD5OrderID_UNIQUE`(`MD5OrderID` ASC) USING BTREE,
  UNIQUE INDEX `uniq_MerchantOrderID_MerchantID_UNIQUE`(`MerchantOrderID` ASC, `MerchantID` ASC) USING BTREE,
  INDEX `SupplierBranchID_Index`(`SupplierBranchID` ASC, `UserID` ASC) USING BTREE,
  INDEX `Name_Remark_Index`(`Remark` ASC, `Name` ASC) USING BTREE,
  INDEX `Name_Index`(`Name` ASC) USING BTREE,
  INDEX `CreateTime_Index`(`CreateTime` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '银行卡充值订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_depositorder_bak
-- ----------------------------
DROP TABLE IF EXISTS `t_depositorder_bak`;
CREATE TABLE `t_depositorder_bak`  (
  `OrderIndex` bigint NOT NULL COMMENT '订单索引',
  `OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单号',
  `SupplierBranchID` int NULL DEFAULT NULL COMMENT '分部ID',
  `MerchantID` int NULL DEFAULT NULL COMMENT '商户编号',
  `MerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户订单号最长64位',
  `UserLevel` int NULL DEFAULT NULL COMMENT '商户会员等级',
  `UserID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户会员唯一ID',
  `MerchantOrderTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `PayType` int NULL DEFAULT NULL COMMENT '充值方式,1:网银转账2:快捷支付3:银联支付4:微信5:支付宝6:QQ钱包7:京东支付8:云闪付',
  `ClientType` int NULL DEFAULT NULL COMMENT '客户端类型, 1：PC，2:WAP，4:APP',
  `PayAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '充值金额',
  `PaidAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '实际到账金额',
  `PaidChannelFee` decimal(10, 2) NULL DEFAULT NULL COMMENT '商户需付手续费',
  `SupplierPaidChannelFee` decimal(10, 2) NULL DEFAULT NULL COMMENT '渠道提供方手续费，比如需向三方通道付手续费，比如平均银行卡磨损费',
  `AgencyCommission` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理佣金',
  `NotifyUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户用于接收订单通知的地址',
  `ReturnUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单提交后立即返回的商户页面地址',
  `MerchantRemark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `ChannelOrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '平台提交给充值渠道的订单号, GUID32位',
  `BankCode` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行类型代码',
  `MD5OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'MD5签名',
  `PayChannelID` int NULL DEFAULT NULL COMMENT '处理该订单的充值渠道',
  `BankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理该订单的银行卡',
  `BankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理该订单的银行卡简称',
  `OrderStatus` int NULL DEFAULT 0 COMMENT '订单状态0:创建，默认值;1：处理中;2:清算中;3:正常支付关闭;4：手工确认订单已完成关闭; 5:超时,6:超时关闭；7:取消关闭',
  `OrderPaidStatusChangeTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单状态最后修改时间',
  `OrderNotifyStatus` int NULL DEFAULT NULL COMMENT '通知状态；0:未成功通知；1：已成功通知;2:通知超时异常',
  `OrderNotifyTime` datetime NULL DEFAULT NULL COMMENT '订单通知状态最后修改时间',
  `Remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '附言',
  `Name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `ReadMark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用于标注是否被自动付款取走',
  `CloseReason` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关闭原因',
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `UserIP` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'ip',
  `Operator` int NULL DEFAULT 0 COMMENT '操作员',
  `MemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '会员账号',
  `UserCreditLevel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户层级，根据用户层级等级分配不同支付渠道;多个层级，使用‘,’分割，比如支持5_1,5_2层级，则是5_1,5_2',
  `MemberBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户的存款银行卡',
  `EncryptBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理该订单的银行卡',
  `EncryptBankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理该订单的银行卡简称',
  `EncryptName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员姓名',
  `EncryptMemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员账号',
  `EncryptMemberBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户的存款银行卡',
  `SourceType` tinyint NOT NULL DEFAULT 0 COMMENT '提单方式',
  `ManualRemark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '人工备注',
  `AutoMataType` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '自动机客户端类型  0 PC 1 手机银行',
  `EncryptDepositName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '真实存款人名字',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `VirtualPayAmount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币金额',
  `VirtualPaidAmount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币实际金额',
  `VirtualRate` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币浮动汇率',
  `VirtualBasicRate` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币基础汇率',
  PRIMARY KEY (`OrderIndex`) USING BTREE,
  UNIQUE INDEX `MD5OrderID_UNIQUE`(`MD5OrderID` ASC) USING BTREE,
  INDEX `SupplierBranchID_Index`(`SupplierBranchID` ASC, `UserID` ASC) USING BTREE,
  INDEX `Name_Remark_Index`(`Remark` ASC, `Name` ASC) USING BTREE,
  INDEX `Name_Index`(`Name` ASC) USING BTREE,
  INDEX `CreateTime_Index`(`CreateTime` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '银行卡充值订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_depositorder_history
-- ----------------------------
DROP TABLE IF EXISTS `t_depositorder_history`;
CREATE TABLE `t_depositorder_history`  (
  `OrderIndex` bigint NOT NULL COMMENT '订单索引',
  `OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单号',
  `SupplierBranchID` int NULL DEFAULT NULL COMMENT '分部ID',
  `MerchantID` int NULL DEFAULT NULL COMMENT '商户编号',
  `MerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户订单号最长64位',
  `UserLevel` int NULL DEFAULT NULL COMMENT '商户会员等级',
  `UserID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户会员唯一ID',
  `MerchantOrderTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `PayType` int NULL DEFAULT NULL COMMENT '充值方式,1:网银转账2:快捷支付3:银联支付4:微信5:支付宝6:QQ钱包7:京东支付8:云闪付',
  `ClientType` int NULL DEFAULT NULL COMMENT '客户端类型, 1：PC，2:WAP，4:APP',
  `PayAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '充值金额',
  `PaidAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '实际到账金额',
  `PaidChannelFee` decimal(10, 2) NULL DEFAULT NULL COMMENT '商户需付手续费',
  `SupplierPaidChannelFee` decimal(10, 2) NULL DEFAULT NULL COMMENT '渠道提供方手续费，比如需向三方通道付手续费，比如平均银行卡磨损费',
  `AgencyCommission` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理佣金',
  `NotifyUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户用于接收订单通知的地址',
  `ReturnUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单提交后立即返回的商户页面地址',
  `MerchantRemark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `ChannelOrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '平台提交给充值渠道的订单号, GUID32位',
  `BankCode` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行类型代码',
  `MD5OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'MD5签名',
  `PayChannelID` int NULL DEFAULT NULL COMMENT '处理该订单的充值渠道',
  `BankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理该订单的银行卡',
  `BankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理该订单的银行卡简称',
  `OrderStatus` int NULL DEFAULT 0 COMMENT '订单状态0:创建，默认值;1：处理中;2:清算中;3:正常支付关闭;4：手工确认订单已完成关闭; 5:超时,6:超时关闭；7:取消关闭',
  `OrderPaidStatusChangeTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单状态最后修改时间',
  `OrderNotifyStatus` int NULL DEFAULT NULL COMMENT '通知状态；0:未成功通知；1：已成功通知;2:通知超时异常',
  `OrderNotifyTime` datetime NULL DEFAULT NULL COMMENT '订单通知状态最后修改时间',
  `Remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '附言',
  `Name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `ReadMark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用于标注是否被自动付款取走',
  `CloseReason` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关闭原因',
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `UserIP` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'ip',
  `Operator` int NULL DEFAULT 0 COMMENT '操作员',
  `MemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '会员账号',
  `UserCreditLevel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户层级，根据用户层级等级分配不同支付渠道;多个层级，使用‘,’分割，比如支持5_1,5_2层级，则是5_1,5_2',
  `MemberBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户的存款银行卡',
  `EncryptBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理该订单的银行卡',
  `EncryptBankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理该订单的银行卡简称',
  `EncryptName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员姓名',
  `EncryptMemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员账号',
  `EncryptMemberBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户的存款银行卡',
  `SourceType` tinyint NOT NULL DEFAULT 0 COMMENT '提单方式',
  `ManualRemark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '人工备注',
  `AutoMataType` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '自动机客户端类型  0 PC 1 手机银行',
  `EncryptDepositName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '真实存款人名字',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `VirtualPayAmount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币金额',
  `VirtualPaidAmount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币实际金额',
  `VirtualRate` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币浮动汇率',
  `VirtualBasicRate` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币基础汇率',
  PRIMARY KEY (`OrderIndex`) USING BTREE,
  UNIQUE INDEX `uniq_MerchantOrderID_MerchantID_UNIQUE`(`MerchantOrderID` ASC, `MerchantID` ASC) USING BTREE,
  UNIQUE INDEX `MD5OrderID_UNIQUE`(`MD5OrderID` ASC) USING BTREE,
  INDEX `SupplierBranchID_Index`(`SupplierBranchID` ASC, `UserID` ASC) USING BTREE,
  INDEX `Name_Remark_Index`(`Remark` ASC, `Name` ASC) USING BTREE,
  INDEX `Name_Index`(`Name` ASC) USING BTREE,
  INDEX `CreateTime_Index`(`CreateTime` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '银行卡充值订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_devicelist
-- ----------------------------
DROP TABLE IF EXISTS `t_devicelist`;
CREATE TABLE `t_devicelist`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主鍵',
  `deviceid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '设备id',
  `worktype` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '工作类型',
  `bankaccount` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行卡简码',
  `devicestatus` int NOT NULL DEFAULT 0 COMMENT '設備狀態',
  `clientid` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '客户端ID',
  `supplierbranchid` int NOT NULL DEFAULT 0 COMMENT '財務分支',
  `devicetype` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '设备类型',
  `platforms` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '平台标识',
  `groupstr` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '分组标识',
  `devicework` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '客户端类型',
  `starttime` timestamp NOT NULL DEFAULT '1987-07-31 16:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '开始时间',
  `createtime` timestamp NOT NULL DEFAULT '1987-07-31 16:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `bankaccount`(`bankaccount` ASC) USING BTREE,
  INDEX `deviceid`(`deviceid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_face_video_addr
-- ----------------------------
DROP TABLE IF EXISTS `t_face_video_addr`;
CREATE TABLE `t_face_video_addr`  (
  `id` bigint NOT NULL,
  `cashier_id` bigint NOT NULL DEFAULT 0 COMMENT '卡商ID',
  `cashier_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '卡商姓名',
  `video_type` tinyint(1) NOT NULL DEFAULT 0 COMMENT '视频类型',
  `video_addr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '视频地址',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标识 0正常 1删除',
  `bank_id` bigint NOT NULL DEFAULT 0 COMMENT '银行卡ID',
  `encrypt_bank_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行卡简码',
  `bank_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行卡类型代码',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `supplier_branch_id` int NOT NULL DEFAULT 0 COMMENT '财务分支',
  `video_status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '视频状态 0禁用 1启用',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '卡视频' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_handrepairorder
-- ----------------------------
DROP TABLE IF EXISTS `t_handrepairorder`;
CREATE TABLE `t_handrepairorder`  (
  `OrderIndex` bigint NOT NULL AUTO_INCREMENT COMMENT '订单索引',
  `MerchantID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户编号',
  `merchantName` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户名称',
  `MatchMerchantID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '关联商户',
  `MatchMerchantName` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '关联商户名称',
  `MerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户订单号最长64位',
  `OrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '平台订单号商户简称（5位字符，不足位0补齐）+ yyyyMMddHHmmssfff（时间17位）+1位字符+库编号(2位数数字，先固定为00）',
  `MatchMerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '匹配的充值订单商户订单号，极速订单使用',
  `PayType` int NOT NULL DEFAULT 1 COMMENT '付款方式,1：银行卡，2：第三方，3：其他方式',
  `PayAmount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '提款金额',
  `UserLevel` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户等级',
  `justifyStatus` int NOT NULL DEFAULT 1 COMMENT '冲正类型 默认0 未冲正 1跨日补分2当日补分3跨日冲正4当日冲正5跨日补款6当日补款',
  `repairType` int NOT NULL DEFAULT 1 COMMENT '补款原因 1补骗分2补出款取消3补卡号信息不符4修改金额5结单',
  `failReason` int NOT NULL DEFAULT 1 COMMENT '失败原因 1截图不符2骗分（极速）3冲正（极速）',
  `imgPath` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '凭证',
  `Remark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `OrderStatus` int NOT NULL DEFAULT 1 COMMENT '订单状态 1创建 2已完结',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `createBy` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `reviewedBy` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '审核人',
  `BankCode` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行类型',
  `EncryptBankOwner` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '提款人',
  `EncryptBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '提款卡号',
  `lockBy` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '锁单人',
  `payModel` int NOT NULL DEFAULT 1 COMMENT '支付类型',
  `repairAmount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '补分金额',
  `Node_ID` tinyint NOT NULL DEFAULT 2 COMMENT '数据来源: 1 skg天下,2 柬天下,3 日本天下',
  `matchMemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '关联会员账号',
  `matchUserCreditLevel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '关联信用层级',
  `SupplierBranchID` int NULL DEFAULT NULL COMMENT '财务分部ID',
  `queryType` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '查单类型：1:业务自查  2: 商户查询',
  PRIMARY KEY (`OrderIndex`) USING BTREE,
  UNIQUE INDEX `uniq_Order_NodeID`(`OrderIndex` ASC, `Node_ID` ASC) USING BTREE,
  INDEX `idx_CreateTime`(`created_at` ASC) USING BTREE,
  INDEX `idx_MerchantOrderID_Index`(`MerchantOrderID` ASC) USING BTREE,
  INDEX `idx_OrderID_IDX`(`OrderID` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '手动补分补款订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_imagecode
-- ----------------------------
DROP TABLE IF EXISTS `t_imagecode`;
CREATE TABLE `t_imagecode`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `bankaccount` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行卡简码',
  `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '图形验证码验证码',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `takeTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上报时间',
  `SupplierBranchID` int NOT NULL DEFAULT 0 COMMENT '财务分支id',
  `bankid` int NOT NULL DEFAULT 0 COMMENT '银行卡id',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '数据状态 0正常 -1 删除',
  `taskid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '任务id',
  `imagecontent` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '图片解析内容',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '图形二维码' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_ipmanager
-- ----------------------------
DROP TABLE IF EXISTS `t_ipmanager`;
CREATE TABLE `t_ipmanager`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '白名单ID',
  `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'IP地址',
  `status` tinyint NOT NULL COMMENT '状态',
  `CreateTime` datetime NOT NULL COMMENT '创建时间',
  `UpdateTime` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `updater` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '更新人',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'IP白名单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_ipwhitelists
-- ----------------------------
DROP TABLE IF EXISTS `t_ipwhitelists`;
CREATE TABLE `t_ipwhitelists`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ipstr` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'ip字符串',
  `status` tinyint NOT NULL DEFAULT 2 COMMENT '状态 0 停用  1启用',
  `service_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '服务名称',
  `remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updateby` int NOT NULL DEFAULT 0 COMMENT '审核更新操作人',
  `createby` int NOT NULL DEFAULT 0 COMMENT '创建人',
  `last_access_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后访问时间',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'ip白名单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchant
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant`;
CREATE TABLE `t_merchant`  (
  `MerchantID` int NOT NULL COMMENT '商户ID',
  `CompanyID` int NULL DEFAULT NULL COMMENT '公司ID',
  `MerchantCode` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户代码',
  `MerchantName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户名称',
  `MD5Key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'MD5加密字符串,32GUID，需加密存储',
  `Balance` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '商户总余额',
  `AvailableBalance` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '商户可用余额',
  `FronzenBalance` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '商户冻结余额',
  `Status` int NULL DEFAULT NULL COMMENT '1：启用，0:停用，-1:软删除',
  `ReMark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `LastUpDateTime` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `Version` bigint NULL DEFAULT NULL,
  `SupplierBranchID` int NOT NULL DEFAULT 0 COMMENT '财务分支',
  `merchantType` int NOT NULL DEFAULT 0 COMMENT '商户类型 0 内部商户 1 外部商户',
  `networkType` int NOT NULL DEFAULT 0 COMMENT '网络类型 0内网 1外网 默认0',
  `servicePublicKey` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '服务端加密公钥',
  `servicePrivateKey` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '服务端解密私钥',
  `clientPublicKey` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '客户端加密公钥',
  `clientPrivateKey` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '客户端解密私钥',
  `QueryStatus` int NOT NULL DEFAULT 0 COMMENT '反查开关  0 关闭（默认）  1 开启',
  `QueryIds` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '反查地址 多个逗号分开',
  `CheckStatus` int NOT NULL DEFAULT 0 COMMENT '对账状态开关  0 关闭（默认）  1 开启',
  `merchantSiteCode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户站点代码',
  `EncryptSupport` int NOT NULL DEFAULT 0 COMMENT '是否支持脱敏，默认为0，不支持脱敏',
  `DirectCallbackIds` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '直连地址 多个逗号分开',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `PaidChannelFee` decimal(18, 4) NULL DEFAULT 0.0000 COMMENT '商户服务费',
  `MD5KeySlave` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'MD5加密字符串,32GUID，需加密 备用密钥',
  `SlaveOff` int NOT NULL DEFAULT 0 COMMENT '是否开启备份密钥  0 关闭（默认）  1 开启',
  `agencyCommission` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理佣金',
  `SplitSwitch` int NOT NULL DEFAULT 0 COMMENT '是否拆单  0 不拆单   1  拆单',
  `WithdrawSwitch` int NOT NULL DEFAULT 0 COMMENT 'API取款开关  0 关闭（默认） 1开启',
  `merchantTag` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户标签',
  `isEncrypt` int NOT NULL DEFAULT 0 COMMENT '是否加密 0 不加密  1  加密',
  PRIMARY KEY (`MerchantID`) USING BTREE,
  UNIQUE INDEX `MerchantCode_UNIQUE`(`MerchantCode` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchant_alarm
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant_alarm`;
CREATE TABLE `t_merchant_alarm`  (
  `ID` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `MerchantID` int NOT NULL DEFAULT 0 COMMENT '商户ID',
  `ThresholdAmount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '告警阈值',
  `AlarmAddress` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '告警地址',
  `AlarmRoom` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '告警房间号',
  `AlarmStatus` tinyint NOT NULL DEFAULT 0 COMMENT '告警状态  0关闭 1开启',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `AlarmLastTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后告警时间',
  `AlarmInterval` int NOT NULL DEFAULT 60 COMMENT '告警区间（分钟）',
  `node_id` int NULL DEFAULT 1 COMMENT '节点ID',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE INDEX `uniq_MerchantID`(`MerchantID` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户告警配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchant_approve
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant_approve`;
CREATE TABLE `t_merchant_approve`  (
  `merchant_approve_id` bigint NOT NULL COMMENT '商户工单表id',
  `merchant_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户名称',
  `apply_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '申请人',
  `audit_type` tinyint NOT NULL DEFAULT 1 COMMENT '审批类型: 默认 1 商户信息录入',
  `audit_status` tinyint NOT NULL DEFAULT 0 COMMENT '审批状态 默认 0 创建 1 审核中 2 通过 3 拒绝 4 退回  ',
  `audit_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审批时间',
  `audit_remack` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '审批备注',
  `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '修改者',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`merchant_approve_id`) USING BTREE,
  UNIQUE INDEX `uniq_merchant_name`(`merchant_name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户审批表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchant_approve_extra
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant_approve_extra`;
CREATE TABLE `t_merchant_approve_extra`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '代付商户信息id',
  `merchant_approve_id` bigint NOT NULL COMMENT '商户审批表id',
  `merchant_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户名称',
  `channel_product` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '通道产品',
  `merchant_type` int NOT NULL DEFAULT 0 COMMENT '商户类型 默认 0 外部代付',
  `product_mode` int NOT NULL DEFAULT 0 COMMENT '产品模式 默认 0 待确认',
  `platform_type` int NOT NULL COMMENT '平台类型 默认 0 内部 1 外部 2 外部极速',
  `merchant_estimated_quantity` int NULL DEFAULT 0 COMMENT '商户预计量',
  `responsible_business` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '负责商务',
  `apply_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `recharge_mode` int NOT NULL DEFAULT 0 COMMENT '充值模式 默认 0  USDT 1 RMB ',
  `original_recharge_price` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '原充值价格',
  `original_pay_rate` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '原代付费率',
  `original_rebate_rate` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '原返佣费率',
  `original_issued_single` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '原下发单笔',
  `original_actual_rate` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '原实际汇率',
  `recharge_single_limit` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '充值单笔限额',
  `current_recharge_price` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '现充值价格',
  `current_pay_rate` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '现代付费率',
  `current_rebate_rate` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '现返佣费率',
  `current_issued_single` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '现下发单笔',
  `current_actual_rate` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '现实际汇率',
  `complaint_freeze` int NOT NULL COMMENT '投诉冻结 默认 0 不包 1 包',
  `back_reconciliation` int NOT NULL COMMENT '后台对账 默认 0 支持 1 不支持',
  `cash_pledge` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '押金',
  `customer_service` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '客服对接 ',
  `technology_service` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '技术对接',
  `is_split` int NOT NULL COMMENT '是否拆单 默认 0 否 1 是',
  `merchant_contact` int NOT NULL COMMENT '商户联系方式 默认 0 纸飞机 1 mango 2 skype 3 WhatsApp',
  `telegram_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '纸飞机名称',
  `telegram_number` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '纸飞机号码',
  `skype_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'skype名称',
  `skype_number` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'skype号码',
  `phone_number` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '电话号码',
  `resource_entry` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '资源入口',
  `payment_method` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '回款方式',
  `wechat_number` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '微信号码',
  `whats_app_number` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'WhatsApp号码',
  `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `recharge_price` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '充值价格',
  `pay_rate` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '代付费率',
  `rebate_rate` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '返点费率',
  `agent_commission` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '代理返佣',
  `actual_rate` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '实际费率',
  `withdraw_amount_range` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0-0' COMMENT '出款金额区间',
  `withdraw_time_limit` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '出款时效',
  `trans_hour` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '交易时间',
  `delivery_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '下发时间',
  `customer_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '客服时间',
  `issue_price` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '下发价格',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_merchant_name`(`merchant_name` ASC) USING BTREE,
  UNIQUE INDEX `uniq_merchant_approve_id`(`merchant_approve_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户审批扩展表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchant_approve_extra_history
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant_approve_extra_history`;
CREATE TABLE `t_merchant_approve_extra_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '代付商户信息id',
  `merchant_approve_id` bigint NOT NULL COMMENT '商户审批表id',
  `merchant_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户名称',
  `channel_product` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '通道产品',
  `product_mode` int NOT NULL DEFAULT 0 COMMENT '产品模式 默认 0 待确认',
  `platform_type` int NOT NULL COMMENT '平台类型 默认 0 内部 1 外部 2 外部极速',
  `merchant_estimated_quantity` int NULL DEFAULT 0 COMMENT '商户预计量',
  `responsible_business` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '负责商务',
  `apply_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `recharge_mode` int NOT NULL DEFAULT 0 COMMENT '充值模式 默认 0  USDT 1 RMB ',
  `original_recharge_price` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '原充值价格',
  `original_pay_rate` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '原代付费率',
  `original_rebate_rate` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '原返佣费率',
  `original_issued_single` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '原下发单笔',
  `original_actual_rate` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '原实际汇率',
  `recharge_single_limit` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '充值单笔限额',
  `current_recharge_price` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '现充值价格',
  `current_pay_rate` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '现代付费率',
  `current_rebate_rate` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '现返佣费率',
  `current_issued_single` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '现下发单笔',
  `current_actual_rate` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '现实际汇率',
  `complaint_freeze` int NOT NULL COMMENT '投诉冻结 默认 0 不包 1 包',
  `back_reconciliation` int NOT NULL COMMENT '后台对账 默认 0 支持 1 不支持',
  `cash_pledge` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '押金',
  `customer_service` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '客服对接 ',
  `technology_service` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '技术对接',
  `is_split` int NOT NULL COMMENT '是否拆单 默认 0 否 1 是',
  `merchant_contact` int NOT NULL COMMENT '商户联系方式 默认 0 纸飞机 1 mango 2 skype 3 WhatsApp',
  `telegram_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '纸飞机名称',
  `telegram_number` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '纸飞机号码',
  `skype_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'skype名称',
  `skype_number` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'skype号码',
  `phone_number` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '电话号码',
  `resource_entry` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '资源入口',
  `payment_method` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '回款方式',
  `wechat_number` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '微信号码',
  `whats_app_number` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'WhatsApp号码',
  `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '备注',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1skg天下,2柬天下,3日本天下,4钱包',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_merchant_approve_id`(`merchant_approve_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户审批扩展历史表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchant_approve_history
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant_approve_history`;
CREATE TABLE `t_merchant_approve_history`  (
  `merchant_approve_id` bigint NOT NULL COMMENT '商户工单表id',
  `merchant_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户名称',
  `apply_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '申请人',
  `audit_type` tinyint NOT NULL DEFAULT 1 COMMENT '审批类型: 默认 1 商户信息录入',
  `audit_status` tinyint NOT NULL DEFAULT 0 COMMENT '审批状态 默认 0 创建 1 审核中 2 通过 3 拒绝 4 退回  ',
  `audit_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审批时间',
  `audit_remack` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '审批备注',
  `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '修改者',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1skg天下,2柬天下,3日本天下,4钱包',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`merchant_approve_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户审批历史表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchant_approve_history_update
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant_approve_history_update`;
CREATE TABLE `t_merchant_approve_history_update`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商户审批历史变更表id',
  `merchant_approve_id` bigint NOT NULL COMMENT '商户审批表id',
  `merchant_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户名称',
  `update_date` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '修改内容',
  `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '修改者',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1skg天下,2柬天下,3日本天下,4钱包',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户审批历史变更表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchant_approve_other
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant_approve_other`;
CREATE TABLE `t_merchant_approve_other`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商户审批其他扩展表id',
  `merchant_approve_id` bigint NOT NULL COMMENT '商户审批表id',
  `resource_entry_by` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '介绍人-资源入口',
  `attribution_department` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '归属部门',
  `job_number` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '人资工号',
  `english_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '英文名',
  `telegram_name_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '介绍人纸飞机名称',
  `telegram_number_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '介绍人纸飞机号码',
  `skype_name_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '介绍人skype名称',
  `skype_number_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '介绍人skype号码',
  `phone_number_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '介绍人电话号码',
  `out_look` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'Outlook',
  `withdrawal_account_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '介绍人提现账号',
  `withdrawal_check_group_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '介绍人提现核对群',
  `agent_rebate` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理返点',
  `phone_number_ag` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理人电话号码',
  `telegram_name_ag` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理人纸飞机名称',
  `telegram_number_ag` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理人纸飞机号码',
  `skype_name_ag` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理人skype名称',
  `skype_number_ag` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理人skype号码',
  `connect_mode_ag` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理人联系方式',
  `withdrawal_account_ag` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理人提现账号',
  `withdrawal_check_group_ag` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理人提现核对群',
  `technology_service_op` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '运营人技术对接人员',
  `merchant_id_op` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '运营人商户号',
  `merchant_docking_group_op` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '运营人商户对接群',
  `online_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上线时间',
  `access_platform_op` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '运营人继接入平台',
  `use_department_op` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '运营人使用部门',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `business_leader_ap` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商务组长签字及意见',
  `withdraw_manage_ap` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代付主管签字及意见',
  `operation_manage_ap` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '运营主管签字及意见',
  `resource_directore_ap` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '资源部总监签字及意见',
  `finance_directore_ap` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '财管总监签字及意见',
  `pay_directore_ap` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '支付总监签字及意见',
  `finance_responsible_ap` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '财务负责人签字及意见',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_merchant_approve_id`(`merchant_approve_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户审批其他扩展表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchant_approve_other_history
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant_approve_other_history`;
CREATE TABLE `t_merchant_approve_other_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商户审批其他扩展表id',
  `merchant_approve_id` bigint NOT NULL COMMENT '商户审批表id',
  `resource_entry_by` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '介绍人-资源入口',
  `attribution_department` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '归属部门',
  `job_number` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '人资工号',
  `english_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '英文名',
  `telegram_name_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '介绍人纸飞机名称',
  `telegram_number_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '介绍人纸飞机号码',
  `skype_name_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '介绍人skype名称',
  `skype_number_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '介绍人skype号码',
  `phone_number_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '介绍人电话号码',
  `out_look` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'Outlook',
  `withdrawal_account_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '介绍人提现账号',
  `withdrawal_check_group_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '介绍人提现核对群',
  `agent_rebate` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理返点',
  `phone_number_ag` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理人电话号码',
  `telegram_name_ag` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理人纸飞机名称',
  `telegram_number_ag` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理人纸飞机号码',
  `skype_name_ag` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理人skype名称',
  `skype_number_ag` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理人skype号码',
  `connect_mode_ag` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '联系方式',
  `withdrawal_account_ag` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理人提现账号',
  `withdrawal_check_group_ag` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理人提现核对群',
  `technology_service_op` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '运营人技术对接人员',
  `merchant_id_op` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '运营人商户号',
  `merchant_docking_group_op` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '运营人商户对接群',
  `online_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上线时间',
  `access_platform_op` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '运营人继接入平台',
  `use_department_op` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '运营人使用部门',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1skg天下,2柬天下,3日本天下,4钱包',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `business_leader_ap` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商务组长签字及意见',
  `withdraw_manage_ap` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代付主管签字及意见',
  `operation_manage_ap` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '运营主管签字及意见',
  `resource_directore_ap` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '资源部总监签字及意见',
  `finance_directore_ap` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '财管总监签字及意见',
  `pay_directore_ap` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '支付总监签字及意见',
  `finance_responsible_ap` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '财务负责人签字及意见',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_merchant_approve_id`(`merchant_approve_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户审批其他扩展历史表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchant_bankcard
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant_bankcard`;
CREATE TABLE `t_merchant_bankcard`  (
  `ID` int NOT NULL COMMENT '银行卡ID',
  `MerchantID` int NOT NULL COMMENT '商户ID',
  `MerchantIdGroup` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户IDs',
  `EncryptBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行卡号码',
  `EncryptBankCode` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行卡简洁代码',
  `BankType` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '银行卡类型代码',
  `EncryptAddress` varchar(360) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '开户地址',
  `EncryptOwnerName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '开户人',
  `WorkType` int NOT NULL DEFAULT 1 COMMENT '工作模式 1 下发',
  `ReMark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `Status` int NOT NULL DEFAULT 0 COMMENT '1：启用，0:停用，-1:软删除',
  `LastUpDateTime` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `user_type` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '00' COMMENT '用户(商户)类型:00-商户,01-代理',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE INDEX `BankNum_UNIQUE`(`EncryptBankNum` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户银行卡' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchant_depositorder
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant_depositorder`;
CREATE TABLE `t_merchant_depositorder`  (
  `OrderIndex` bigint NOT NULL COMMENT '订单索引',
  `OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `MerchantID` int NULL DEFAULT NULL,
  `PayAmount` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '提款金额',
  `PaidAmount` decimal(18, 2) NULL DEFAULT 0.00 COMMENT '实际到账金额',
  `NotifyUrl` varchar(516) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户用于接收订单通知的地址',
  `ReturnUrl` varchar(516) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单提交后立即返回的商户页面地址',
  `MerchantRemark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `MD5OrderID` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'MD5签名',
  `BankNum` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理该订单的银行卡',
  `Owner` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `Address` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `BankAccount` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理该订单的银行卡简称',
  `OrderStatus` int NOT NULL DEFAULT 0 COMMENT '订单状态0:创建，默认值;1：处理中;2:清算中;3:正常支付关闭;4：手工确认订单已完成关闭; 5:超时,6:超时关闭；7:取消关闭',
  `OrderApproveStatus` int NOT NULL DEFAULT 0 COMMENT '订单审核状态 0创建 1处理中 2成功 3失败',
  `OrderPaidStatusChangeTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单状态最后修改时间',
  `OrderNotifyStatus` int NULL DEFAULT 0 COMMENT '通知状态；0:未成功通知；1：已成功通知;2:通知超时异常',
  `OrderNotifyTime` datetime NULL DEFAULT NULL COMMENT '订单通知状态最后修改时间',
  `Remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `Name` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '付款人姓名,\n有姓名时附言和姓名同时匹配，若匹配失败则先用附言匹配，再匹配失败则用姓名匹配',
  `ReadMark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用于标注是否被自动付款取走',
  `CloseReason` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `ApplyMerchant` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户申请人',
  `ApprovaledMerchant` int NULL DEFAULT NULL COMMENT '商户审批人',
  `ApprovaledAdmin` int NULL DEFAULT NULL COMMENT '天下核准人账号',
  `MerchantApproaledTime` datetime NULL DEFAULT NULL COMMENT '商户核准时间',
  `AdminApprovaledTime` datetime NULL DEFAULT NULL COMMENT '平台核准时间',
  `BankType` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '银行卡类型代码',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `RechargerType` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '充值方式',
  `VirtualCurrencyAgreement` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '虚拟币交易协议',
  `MerchantOrderID` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户订单id',
  `UsdtRate` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT 'usdt汇率',
  `UsdtAmount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT 'usdt充值金额',
  `BasicUsdtRate` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '基础的usdt汇率',
  `UsdtPaidAmount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT 'usdt实际金额',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`OrderIndex`) USING BTREE,
  UNIQUE INDEX `MD5OrderID_UNIQUE`(`MD5OrderID` ASC) USING BTREE,
  UNIQUE INDEX `OrderID_UNIQUE`(`OrderID` ASC) USING BTREE,
  INDEX `CreateTime_Index`(`CreateTime` ASC) USING BTREE,
  INDEX `MerchantID_Index`(`MerchantID` ASC) USING BTREE,
  INDEX `Name_Index`(`Name` ASC) USING BTREE,
  INDEX `Name_Remark`(`Remark` ASC, `Name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户充值订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchant_hybrid_config
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant_hybrid_config`;
CREATE TABLE `t_merchant_hybrid_config`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `merchant_id` int NOT NULL COMMENT '商户ID',
  `hybrid_switch` tinyint NOT NULL DEFAULT 0 COMMENT '混淆开关，0：关闭，1：开启',
  `match_weight` decimal(5, 2) NOT NULL DEFAULT 0.00 COMMENT '极速权重',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchant_pay_type_rate
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant_pay_type_rate`;
CREATE TABLE `t_merchant_pay_type_rate`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `pay_type` int NOT NULL DEFAULT 0 COMMENT '支付方式',
  `merchant_id` int NOT NULL DEFAULT 0 COMMENT '商户ID',
  `merchant_deposit_rate` decimal(5, 4) NOT NULL DEFAULT 0.0000 COMMENT '商户收款费率',
  `merchant_withdraw_rate` decimal(5, 4) NOT NULL DEFAULT 0.0000 COMMENT '商户出款费率',
  `agency_deposit_rate` decimal(5, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理返佣收款费率',
  `agency_withdraw_rate` decimal(5, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理返佣出款费率',
  `status` int NOT NULL DEFAULT 0 COMMENT '1：启用，0:停用，-1:软删除',
  `remark` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `merchant_withdraw_fixed_cost` decimal(6, 4) NOT NULL COMMENT '商户提款固定费率',
  `node_id` int NOT NULL DEFAULT 1 COMMENT '数据来源: 1 skg天下,2 柬天下,3 日本天下',
  `agency_withdraw_fixed_cost` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理出款固定费率',
  `recommend_withdraw_rate` decimal(5, 4) NOT NULL DEFAULT 0.0000 COMMENT '推荐人费率',
  `merchant_percent_rate` decimal(6, 4) UNSIGNED ZEROFILL NOT NULL DEFAULT 00.0000 COMMENT '商户百分比返利',
  `merchant_fixed_rate` decimal(6, 2) UNSIGNED ZEROFILL NOT NULL DEFAULT 0000.00 COMMENT '商户固定金额返利',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_PayType_MerchantID`(`pay_type` ASC, `merchant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户支付方式服务费配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchant_payrule_config
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant_payrule_config`;
CREATE TABLE `t_merchant_payrule_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '规则名称',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据入库时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '更新者',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '数据修改时间',
  `supplierbranchid` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '财务分支ID',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态 0开启 -1删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_payrule_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_payrule_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户规则表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchant_payrule_config_group
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant_payrule_config_group`;
CREATE TABLE `t_merchant_payrule_config_group`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `group_id` int NOT NULL DEFAULT 0 COMMENT '规则组ID',
  `config_id` int NOT NULL DEFAULT 0 COMMENT '商户规则ID',
  `date_start` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '时间区间-开始时间',
  `date_end` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '时间区间-结束时间',
  `amount_start` int NOT NULL DEFAULT 0 COMMENT '金额区间-开始值',
  `amount_end` int NOT NULL DEFAULT 0 COMMENT '金额区间-结束值',
  `rate_type` tinyint NOT NULL DEFAULT 0 COMMENT '费率设置类型 0调整费率浮动 1调整目标费率',
  `rate_value` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '费率设置值 正数代表上调，负数代表下调',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据入库时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '数据修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_config_group_config_id`(`config_id` ASC) USING BTREE,
  INDEX `idx_config_group_date_end`(`date_end` ASC) USING BTREE,
  INDEX `idx_config_group_date_start`(`date_start` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户规则组' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchant_payrule_config_merchant
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant_payrule_config_merchant`;
CREATE TABLE `t_merchant_payrule_config_merchant`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `merchant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户ID',
  `config_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '规则id',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据入库时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '数据修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_config_merchant_id`(`merchant_id` ASC) USING BTREE,
  INDEX `idx_merchant_config_id`(`config_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户规则关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchant_revenue_statistics
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant_revenue_statistics`;
CREATE TABLE `t_merchant_revenue_statistics`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `merchant_id` int NOT NULL COMMENT '商户id',
  `merchant_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商户名称',
  `merchant_balance` decimal(14, 2) NOT NULL COMMENT '商户余额',
  `available_balance` decimal(14, 2) NOT NULL COMMENT '有效余额',
  `fronzenb_balance` decimal(14, 2) NOT NULL DEFAULT 0.00 COMMENT '冻结余额',
  `merchant_service_balance` decimal(14, 2) NULL DEFAULT NULL COMMENT '商户服务费',
  `member_deposit_balance` decimal(14, 2) NOT NULL COMMENT '会员充值总额',
  `member_withdraw_balance` decimal(14, 2) NOT NULL COMMENT '会员提款总额',
  `merchant_deposit_balance` decimal(14, 2) NOT NULL COMMENT '商户充值总额',
  `merchant_withdraw_balance` decimal(14, 2) NOT NULL COMMENT '商户提现总额',
  `revenue_balance` decimal(14, 2) NOT NULL COMMENT '营收总额',
  `create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '结算时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `agency_commission` decimal(14, 2) NOT NULL DEFAULT 0.00 COMMENT '返佣总额',
  `node_id` int NOT NULL DEFAULT 1 COMMENT '数据来源: 1 skg天下,2 柬天下,3 日本天下',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_statistics_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户日营收统计记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchant_withdraworder
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant_withdraworder`;
CREATE TABLE `t_merchant_withdraworder`  (
  `ID` bigint NOT NULL DEFAULT 0 COMMENT '主键ID',
  `OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '订单号',
  `ThirdOrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '三方关联订单号',
  `MerchantID` int NOT NULL DEFAULT 0 COMMENT '商户ID',
  `PayAmount` decimal(18, 2) NULL DEFAULT 0.00 COMMENT '提款金额',
  `PaidAmount` decimal(18, 2) NULL DEFAULT 0.00 COMMENT '实际到账金额',
  `PaidChannelFee` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `OrderStatus` int NOT NULL DEFAULT 0 COMMENT '商户提现自定义出款时 0创建 1处理中 2成功 3失败；三方订单状态0:财务待审核，默认值;1：处理中;2:清算中;3:正常支付关闭;5:超时,6:超时关闭；7:取消关闭;9:后台待核;10财务拒绝;11 后台拒绝；12 后台待分配',
  `OrderPaidStatusChangeTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单状态最后修改时间',
  `MerchantBankID` int NOT NULL COMMENT '商户银行卡ID',
  `BankCode` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户银行编码',
  `EncryptBankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户银行卡简码',
  `EncryptBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户银行账号',
  `EncryptBankAddress` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '开户地址',
  `EncryptBankOwner` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '开户人',
  `MD5OrderID` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT '' COMMENT '订单ID加密串',
  `Digest` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'Digest',
  `Remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `CloseReason` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '订单手工关闭原因',
  `OrderType` int NOT NULL DEFAULT 0 COMMENT '0:商户订单',
  `ApprovePersonnel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '一审人员',
  `ApproveStatus` tinyint NOT NULL DEFAULT 0 COMMENT '一审状态',
  `ApproveRemark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '一审备注',
  `ApprovePersonnelTwo` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '二审人员',
  `ApproveStatusTwo` tinyint NOT NULL DEFAULT 0 COMMENT '二审状态',
  `ApproveRemarkTwo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '二审备注',
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `OrderApproveStatus` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '订单审核状态0:商户待审核，1:财务待审核; 3:商户拒绝;4审核通过；5财务拒绝',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `WithdrawChannelType` int NOT NULL COMMENT '出款方式，0系统出款，1自定义出款',
  `WithdrawBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '出款银行卡号',
  `WithdrawBankAddress` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '出款银行卡开户地址',
  `WithdrawBankOwner` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '出款银行卡开户人姓名',
  `WithdrawBankCode` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '出款银行变卡',
  `WithdrawBankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '出款银行卡简码',
  `ApplyMerchant` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '申请人',
  `MerchantWithdrawalType` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '提现方式，0系统银行卡，1自定义银行卡，2虚拟币',
  `VirtualCurrencyAddress` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '虚拟币收款码地址',
  `MerchantOrderID` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户订单id',
  `VirtualCurrencyAgreement` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '虚拟币交易协议',
  `Name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '提现姓名',
  `deduction` tinyint NOT NULL DEFAULT 0 COMMENT '扣费标识，0未扣费，1已扣费',
  `bill_sign` tinyint NOT NULL DEFAULT 0 COMMENT '结算标识，0未结算，1已结算',
  `SupplierPaidChannelFee` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商户服务费',
  `AgencyCommission` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '代理佣金',
  `VirtualRate` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币汇率',
  `VirtualAmount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币充值金额',
  `VirtualPaidAmount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币实际充值金额',
  `BankAccount` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户银行卡简码',
  `BankNum` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户银行账号',
  `BankAddress` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `BankOwner` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `EncryptBankMobile` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '提款人银行预留手机号',
  `EncryptIdNum` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '提款人身份证号',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE INDEX `OrderID_UNIQUE`(`OrderID` ASC) USING BTREE,
  INDEX `CreateTime_Index`(`CreateTime` ASC) USING BTREE,
  INDEX `MerchantID_Index`(`MerchantID` ASC) USING BTREE,
  INDEX `OrderPaidStatusChangeTime_index`(`OrderPaidStatusChangeTime` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户下发订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchant_withdraworder_approve
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant_withdraworder_approve`;
CREATE TABLE `t_merchant_withdraworder_approve`  (
  `ID` bigint NOT NULL DEFAULT 0 COMMENT '主键ID',
  `MerchantOrderID` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户订单id',
  `MerchantID` int NOT NULL DEFAULT 0 COMMENT '商户ID',
  `PayAmount` decimal(18, 4) NOT NULL COMMENT '提款金额',
  `SupplierPaidChannelFee` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '商户服务费',
  `BankCode` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户银行编码',
  `EncryptBankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户银行卡简码',
  `EncryptBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户银行账号',
  `EncryptBankAddress` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '开户地址',
  `EncryptBankOwner` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '开户人',
  `Remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `ApprovePersonnel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '审核人员',
  `ApproveRemark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '审核备注',
  `OrderApproveStatus` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '订单审核状态 0:财务待审核;1财务通过；2财务拒绝',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `ApplyMerchant` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '申请人',
  `MerchantWithdrawalType` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '提现方式，0系统银行卡，1自定义银行卡，2虚拟币',
  `VirtualCurrencyAddress` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '虚拟币收款码地址',
  `VirtualCurrencyAgreement` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '虚拟币交易协议',
  `Name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '提现姓名',
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `PayType` int NULL DEFAULT NULL COMMENT '付款方式,1：银行卡，2：第三方，3：其他方式',
  `AttributesType` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '提单类型：1 XTFY ，2 TSZC ',
  `outDfType` int NOT NULL DEFAULT 0 COMMENT '外部代付类型',
  `EncryptBankMobile` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '提款人银行预留手机号',
  `EncryptIdNum` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '提款人身份证号',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE INDEX `uniq_MerchantOrderID`(`MerchantOrderID` ASC) USING BTREE,
  INDEX `idx_CreateTime`(`CreateTime` ASC) USING BTREE,
  INDEX `idx_MerchantID`(`MerchantID` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户手动API下发订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchant_withdraworder_honour
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant_withdraworder_honour`;
CREATE TABLE `t_merchant_withdraworder_honour`  (
  `ID` bigint NOT NULL DEFAULT 0 COMMENT '主键ID',
  `MerchantOrderID` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户订单id',
  `MerchantID` int NOT NULL DEFAULT 0 COMMENT '商户ID',
  `MerchantWithdrawalType` int NOT NULL DEFAULT 0 COMMENT '提现方式 0银行卡',
  `PayAmount` decimal(18, 4) NOT NULL COMMENT '提款金额',
  `PaidAmount` decimal(18, 4) NOT NULL COMMENT '实际提款金额',
  `USDTPayAmount` decimal(18, 4) NOT NULL COMMENT 'usdt金额',
  `USDTPaidAmount` decimal(18, 4) NOT NULL COMMENT 'usdt实际金额',
  `BankCode` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户银行编码',
  `EncryptBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户银行账号',
  `EncryptBankAddress` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '开户地址',
  `EncryptBankOwner` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '开户人',
  `Remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `ApprovePersonnel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '审核人员',
  `ApproveRemark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '审核备注',
  `ApplyMerchant` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '申请人',
  `Update_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `Create_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `orderStatus` int NOT NULL DEFAULT 0 COMMENT '订单状态',
  `rate` decimal(10, 4) NOT NULL COMMENT '汇率',
  `imgPath` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '凭证',
  `SupplierPaidChannelFee` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '商户服务费',
  `honourMerchant` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '承兑商户',
  `outDfType` int NOT NULL DEFAULT 0 COMMENT '外部代付类型',
  `EncryptBankMobile` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '提款人银行预留手机号',
  `EncryptIdNum` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '提款人身份证号',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE INDEX `uniq_MerchantOrderID`(`MerchantOrderID` ASC) USING BTREE,
  INDEX `idx_CreateTime`(`Create_at` ASC) USING BTREE,
  INDEX `idx_MerchantID`(`MerchantID` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户承兑订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchantaccountrecord
-- ----------------------------
DROP TABLE IF EXISTS `t_merchantaccountrecord`;
CREATE TABLE `t_merchantaccountrecord`  (
  `MerchantID` int NULL DEFAULT NULL,
  `OrderID` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '天下订单号',
  `MerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `TransType` int NOT NULL COMMENT '交易方式,0：充值，1：提款，2 准备金，3 商户下发',
  `TransAmount` decimal(10, 2) NOT NULL COMMENT '交易金额',
  `PaidAmount` decimal(10, 2) NOT NULL COMMENT '实际交易金额',
  `PayChannelFee` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '渠道使用费',
  `SupplierPaidChannelFee` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商户需付使用费',
  `AgencyCommission` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理佣金',
  `PreBalance` decimal(14, 2) NULL DEFAULT 0.00 COMMENT '交易前余额',
  `Balance` decimal(14, 2) NULL DEFAULT 0.00 COMMENT '当前余额',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `SupplierBranchID` int NULL DEFAULT NULL,
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `PreAvailableBalance` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '交易前可用余额',
  `PreFrozenBalance` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '交易前冻结余额',
  `AvailableBalance` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '交易后可用余额',
  `FrozenBalance` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '交易后冻结余额',
  `OrderFinishTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单完成时间',
  PRIMARY KEY (`OrderID`) USING BTREE,
  INDEX `idx_MerchantOrderID`(`MerchantOrderID` ASC) USING BTREE,
  INDEX `idx_CreateTime_TransAmount`(`CreateTime` ASC, `TransAmount` ASC) USING BTREE,
  INDEX `idx_MerchantID`(`MerchantID` ASC, `TransType` ASC, `CreateTime` ASC) USING BTREE,
  INDEX `idx_merchant_create`(`MerchantID` ASC, `CreateTime` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户账变' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchantaccountrecord_new
-- ----------------------------
DROP TABLE IF EXISTS `t_merchantaccountrecord_new`;
CREATE TABLE `t_merchantaccountrecord_new`  (
  `MerchantID` int NULL DEFAULT NULL,
  `OrderID` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '天下订单号',
  `MerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `TransType` int NOT NULL COMMENT '交易方式,0：充值，1：提款，2 准备金，3 商户下发',
  `TransAmount` decimal(10, 2) NOT NULL COMMENT '交易金额',
  `PaidAmount` decimal(10, 2) NOT NULL COMMENT '实际交易金额',
  `PayChannelFee` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '渠道使用费',
  `SupplierPaidChannelFee` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商户需付使用费',
  `AgencyCommission` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理佣金',
  `PreBalance` decimal(14, 2) NULL DEFAULT 0.00 COMMENT '交易前余额',
  `Balance` decimal(14, 2) NULL DEFAULT 0.00 COMMENT '当前余额',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `SupplierBranchID` int NULL DEFAULT NULL,
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `remark` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`OrderID`) USING BTREE,
  INDEX `idx_MerchantOrderID`(`MerchantOrderID` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户账变' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchantcompany
-- ----------------------------
DROP TABLE IF EXISTS `t_merchantcompany`;
CREATE TABLE `t_merchantcompany`  (
  `ID` int NOT NULL COMMENT '公司编号',
  `CompanyName` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公司名称',
  `Status` int NOT NULL COMMENT '状态（-1 删除；0 停用； 1 启用）',
  `CreateBy` int NOT NULL COMMENT '创建人',
  `CreateTime` datetime NOT NULL COMMENT '创建时间',
  `UpdatedBy` int NULL DEFAULT NULL COMMENT '更新人',
  `UpdateTime` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户公司信息 ' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchantpaychannelsetting
-- ----------------------------
DROP TABLE IF EXISTS `t_merchantpaychannelsetting`;
CREATE TABLE `t_merchantpaychannelsetting`  (
  `PayChannelID` int NOT NULL DEFAULT 0 COMMENT '支付渠道ID',
  `MerchantID` int NOT NULL COMMENT '商户ID',
  `DepositRate` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `WithdrawRate` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `Rate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '渠道费率Json格式\n[\n    {\n        \"RateFlag\": \"s:按单笔,i:按百分比\",\n        \"RateValue\": \"2根据RateFla一笔收两元或每笔乘以2%\",\n        \"MinRange\": \"触发条件的最低值\",\n        \"MaxRange\": \"触发条件的最大值\"\n    },\n    {\n        \"RateFlag\": \"s:按单笔,i:按百分比\",\n        \"RateValue\": \"2根据RateFla一笔收两元或每笔乘以2%\",\n        \"MinRange\": \"触发条件的最低值\",\n        \"MaxRange\": \"触发条件的最大值\"\n    }\n]',
  `Status` int NULL DEFAULT NULL COMMENT '1：启用，0:停用，-1:软删除',
  `ReMark` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `LastUpDateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `Node_ID` int NOT NULL DEFAULT 3 COMMENT '数据来源: 1 skg天下,2 柬天下,3 日本天下',
  `PayType` int NOT NULL DEFAULT 0 COMMENT '支付方式',
  `MerchantDepositRate` decimal(6, 4) NOT NULL DEFAULT 0.0000 COMMENT '商户收款费率',
  `MerchantWithdrawRate` decimal(6, 4) NOT NULL DEFAULT 0.0000 COMMENT '商户出款费率',
  `AgencyDepositRate` decimal(6, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理返佣收款费率',
  `AgencyWithdrawRate` decimal(6, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理返佣出款费率',
  UNIQUE INDEX `idx_PayType_MerchantID`(`PayType` ASC, `MerchantID` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户支付渠道手续费配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_merchantpaypool
-- ----------------------------
DROP TABLE IF EXISTS `t_merchantpaypool`;
CREATE TABLE `t_merchantpaypool`  (
  `PayPoolID` int NOT NULL COMMENT '支付池ID',
  `MerchantID` int NOT NULL COMMENT '商户ID',
  `SupplierBranchID` int NOT NULL COMMENT '财务分部ID',
  `UserLevels` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户等级，根据用户等级分配不同支付渠道，默认为0表示不限制;总共分1-10总共10级;多个等级，使用‘，’分割，比如支持1-3层级，则是1，2，3',
  `Status` int NOT NULL DEFAULT 0 COMMENT '1：启用，0:停用，-1:软删除',
  `ReMark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `LastUpDateTime` datetime NULL DEFAULT NULL,
  `SumAmountPerDayTrans` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '每日交易流水限额',
  `AvailableBalance` decimal(14, 2) NULL DEFAULT 0.00 COMMENT '可用余额',
  `RealBalance` decimal(14, 2) NULL DEFAULT 0.00 COMMENT '实际余额',
  `Version` bigint NULL DEFAULT 0,
  `UserCreditLevel` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户层级，根据用户层级等级分配不同支付渠道;多个层级，使用‘，’分割，比如支持5_1,5_2层级，则是5_1,5_2',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`PayPoolID`, `MerchantID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户支付池配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_ocr_certificate_verification
-- ----------------------------
DROP TABLE IF EXISTS `t_ocr_certificate_verification`;
CREATE TABLE `t_ocr_certificate_verification`  (
  `MerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商户订单号',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `id` bigint NOT NULL,
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `OcrAnalysisResults` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'OCR解析后的字符串集合',
  `OcrResults` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'OCR校验结果：1、重复流水\r\n\r\n2、短信校验失败\r\n\r\n3、短信校验通过\r\n\r\n4、凭证时间错误',
  `ImageAddr` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片地址',
  `OcrRule` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '1、交易金额 /存款人姓名/收款卡号\r\n\r\n2、交易金额/收款卡号\r\n\r\n3、交易金额/收款人姓名/银行类型',
  `OcrAmount` decimal(11, 2) NULL DEFAULT NULL COMMENT '交易金额',
  `BankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '收款卡号',
  `EncryptBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '收款卡号加密',
  `EncryptDepositName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '存款人姓名',
  `DepositName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '存款人姓名加密',
  `EncryptBankOwner` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '收款人姓名加密',
  `BankOwner` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '收款人姓名	',
  `OcrTime` timestamp NULL DEFAULT NULL COMMENT '交易时间',
  `BankTypeName` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡类型',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '短信内容',
  `msg_id` bigint NULL DEFAULT NULL COMMENT '短信流水id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `MerchantOrderID_index`(`MerchantOrderID` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单OCR校验结果表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_order_profit
-- ----------------------------
DROP TABLE IF EXISTS `t_order_profit`;
CREATE TABLE `t_order_profit`  (
  `ID` bigint NOT NULL DEFAULT 0 COMMENT '主键ID',
  `OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '订单号',
  `OrderType` int NOT NULL DEFAULT 0 COMMENT '订单类型 1:存款订单,2:提款订单',
  `PayAmount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '订单金额',
  `AgentId` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '卡商代理ID',
  `Ratio` decimal(20, 2) NOT NULL DEFAULT 0.00 COMMENT '费率',
  `ProfitAmount` decimal(20, 2) NOT NULL DEFAULT 0.00 COMMENT '利润金额',
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `BankCode` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行卡编码',
  `BankNum` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行卡号码',
  `ExtraBalance` decimal(20, 2) NOT NULL DEFAULT 0.00 COMMENT '额外支出余额',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE INDEX `uniq_OrderID`(`OrderID` ASC) USING BTREE,
  INDEX `idx_AgentId`(`AgentId` ASC) USING BTREE,
  INDEX `idx_BankCode`(`BankCode` ASC) USING BTREE,
  INDEX `idx_BankNum`(`BankNum` ASC) USING BTREE,
  INDEX `idx_CreateTime`(`CreateTime` ASC) USING BTREE,
  INDEX `idx_OrderID`(`OrderID` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单利润结算表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_paychannel
-- ----------------------------
DROP TABLE IF EXISTS `t_paychannel`;
CREATE TABLE `t_paychannel`  (
  `PayChannelID` int NOT NULL COMMENT '支付渠道ID',
  `SupplierBranchID` int NULL DEFAULT NULL COMMENT '财务分部ID',
  `PayChannelName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付渠道名称',
  `PayType` int NULL DEFAULT NULL COMMENT '支付方式,默认1 银行卡转账',
  `WorkType` int NULL DEFAULT NULL COMMENT '工作模式1:收款,2:付款, 3跨行付款(特用于银行卡), 4收付款，7收款和跨行付款',
  `SumAmountPerDayTrans` decimal(18, 2) NULL DEFAULT 0.00 COMMENT '日限总额',
  `MinAmountPerTrans` decimal(18, 2) NULL DEFAULT 0.00 COMMENT '每笔交易最小额度，默认0不限制',
  `MaxAmountPerTrans` decimal(18, 2) NULL DEFAULT 0.00 COMMENT '每笔交易最大额度，默认0不限制',
  `RiskyBalance` decimal(18, 2) NULL DEFAULT 0.00 COMMENT '风控金额',
  `Balance` decimal(10, 2) NULL DEFAULT NULL,
  `WithdrawBalance` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '已下发总结金额',
  `WithdrawSummary` decimal(12, 2) NULL DEFAULT NULL,
  `DepositRate` varchar(520) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '存款费率',
  `WithdrawRate` varchar(520) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '提款费率',
  `ClientType` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '客户端类型,PC:1,WAP:2,APP:4,其他值建立在基础类型的和，比如5表示PC和APP，6表示WAP和APP',
  `Status` int NULL DEFAULT NULL COMMENT '1：启用，0:停用，-1:软删除,-2:预启用, -3:限额停用 ,-4：风控金额停用',
  `ChannelLevel` int NULL DEFAULT NULL COMMENT '同等支付方式下的优先顺序,越小越优先使用,默认为0',
  `BankCode` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行类型代码冗余字段,固码渠道=GM',
  `ChannelMerchantID` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '渠道商户号',
  `ThirdMerchantKey` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '加密Key',
  `ThirdMerchantPublicKeys` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '加密公共key',
  `MerchantID` int NULL DEFAULT NULL COMMENT '对应 thirdmerchant表',
  `PaymentUrl` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付接口地址',
  `UnAvailableTime` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '不可用时间段说明, HH:MM-HH:MM时间段格式集合Json格式化后存储',
  `Version` bigint NULL DEFAULT 0,
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `LastUpDateTime` datetime NULL DEFAULT NULL,
  `ReMark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `WriteThirdTable` tinyint UNSIGNED NULL DEFAULT 2 COMMENT '是否写入t_thirddeposit表  1：写入   2：不写入',
  `PayPoolID` int NOT NULL DEFAULT 0 COMMENT '支付池ID',
  `surplier_type` int NOT NULL DEFAULT 0 COMMENT '卡类型（0为天下渠道，1为卡商渠道，默认0）',
  `agent_id` bigint(20) UNSIGNED ZEROFILL NOT NULL DEFAULT 00000000000000000000 COMMENT '卡商代理ID',
  `agent_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '代理编码--关联卡商关联表',
  `LinkType` int NOT NULL DEFAULT 0 COMMENT '跳转类型',
  `SourceType` tinyint NOT NULL DEFAULT 0 COMMENT '提单方式',
  `UnAvailableStartTime` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '可用开始时间',
  `UnAvailableEndTime` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '可用结束时间',
  `EncryptPayChannelName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付渠道名称',
  `Protocol` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'usdt的协议',
  `SourceTypes` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '来源类型',
  `ProcessingTimesPerTrans` int NOT NULL DEFAULT 0 COMMENT '接单笔数限制',
  `CallStatus` int NOT NULL DEFAULT 0 COMMENT '是否开启反查 0:不开启(默认),1:开启',
  `user_levels` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户等级，根据用户等级分配不同支付渠道，默认为0表示不限制;总共分1-10总共10级;多个等级，使用‘，’分割，比如支持1-3层级，则是1，2，3',
  `user_credit_levels` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户层级，根据用户层级等级分配不同支付渠道;多个层级，使用‘，’分割，比如支持5_1,5_2层级，则是5_1,5_2',
  `RemarkStatus` int NOT NULL DEFAULT 1 COMMENT '是否开启附言 0 不开启  1开启',
  `template_type` int NOT NULL DEFAULT 0 COMMENT '调用参数的模板通道：1为模板通道，可拷贝',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `fourElementStatus` int NOT NULL DEFAULT 0 COMMENT '是否开启四要素 0 不开启  1开启',
  `decimalPointSwitch` int NOT NULL DEFAULT 1 COMMENT '是否支持小数点 0 不开启，1 开启',
  PRIMARY KEY (`PayChannelID`) USING BTREE,
  INDEX `BankCode_Index`(`BankCode` ASC) USING BTREE,
  INDEX `idx_PayChannelName`(`PayChannelName` ASC) USING BTREE,
  INDEX `WorkType_BankCode_Index`(`WorkType` ASC, `BankCode` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '支付渠道表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_paychannelreport
-- ----------------------------
DROP TABLE IF EXISTS `t_paychannelreport`;
CREATE TABLE `t_paychannelreport`  (
  `ID` int NOT NULL COMMENT 'ID',
  `MerchantID` int NOT NULL COMMENT ' 商户ID',
  `PayPoolID` int NOT NULL COMMENT '渠道ID',
  `PayChannelID` int NOT NULL COMMENT '支付ID',
  `TransType` tinyint NOT NULL DEFAULT 0 COMMENT '交易方式 0存款  1 取款   2下发',
  `OrderNumber` int NOT NULL DEFAULT 0 COMMENT '交易总笔数',
  `OrderSuccessNumber` int NOT NULL DEFAULT 0 COMMENT '成功订单笔数',
  `DepositAmount` decimal(11, 2) NOT NULL DEFAULT 0.00 COMMENT '存款订单总金额',
  `WithdrawAmount` decimal(11, 2) NOT NULL COMMENT '取款订单总金额',
  `UpdateTime` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户支付渠道统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_paychannelsetting
-- ----------------------------
DROP TABLE IF EXISTS `t_paychannelsetting`;
CREATE TABLE `t_paychannelsetting`  (
  `PayChannelID` int NOT NULL COMMENT '支付渠道ID',
  `WithdrawBankPoolID` int NULL DEFAULT NULL COMMENT '下发银行卡池',
  `DepositBankPoolID` int NULL DEFAULT NULL COMMENT '充值银行卡池',
  `MaxBalance` decimal(14, 2) NULL DEFAULT 0.00 COMMENT '最大余额',
  `MinBalance` decimal(10, 2) NULL DEFAULT NULL,
  `DepositStatus` int NULL DEFAULT NULL COMMENT '1：自动充值，0:手工充值，-1：系统外充值',
  `WithdrawStatus` int NULL DEFAULT NULL COMMENT '1：自动下发，0:手工下发，-1：系统外下发',
  `DepositUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '准备金充值地址',
  `WithdrawUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '下发地址',
  `PcBankList` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'pc端银行列表',
  `WapBankList` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'wap端银行列表',
  `AppBankList` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'app端银行列表',
  `PcFixedAmount` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'pc固定金额配置',
  `WapFixedAmount` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'Wap固定金额配置',
  `AppFixedAmount` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'App固定金额配置',
  `iOSBankList` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'iOS端银行列表',
  `AndroidBankList` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'Android端银行列表',
  `iOSFixedAmount` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'iOS固定金额配置',
  `AndroidFixedAmount` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'Android固定金额配置',
  `Currency` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '渠道币种 默认为0 人民币 数值参考bank_type',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `BlacklistBankcardList` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '三方出款渠道黑名单银行卡',
  `ApproveStatus` int NOT NULL DEFAULT 1 COMMENT '审核状态(0:未审核,1:已审核)',
  `ErcAddress` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'ERC20地址',
  `TrcAddress` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'TRC20地址',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`PayChannelID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '支付渠道配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_paychannelsummary
-- ----------------------------
DROP TABLE IF EXISTS `t_paychannelsummary`;
CREATE TABLE `t_paychannelsummary`  (
  `PayChannelID` int NOT NULL,
  `DepositAmount` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `WithdrawAmount` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `Calendar` date NOT NULL,
  `LastUpDateTime` datetime NOT NULL,
  `Version` bigint NOT NULL,
  `DepositTimes` int NOT NULL DEFAULT 0 COMMENT '存款次数',
  `WithdrawTimes` int NOT NULL DEFAULT 0 COMMENT '出款次数',
  `IssuedTimes` int NOT NULL DEFAULT 0 COMMENT '下发次数',
  `TotalCount` int NOT NULL DEFAULT 1 COMMENT '总次数',
  `IssuedAmount` decimal(14, 2) NOT NULL DEFAULT 0.00 COMMENT '下发金额',
  `CalendarOfHour` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '小时时间',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`PayChannelID`, `Calendar`, `CalendarOfHour`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_paypool
-- ----------------------------
DROP TABLE IF EXISTS `t_paypool`;
CREATE TABLE `t_paypool`  (
  `PayPoolID` int NOT NULL COMMENT '支付池ID',
  `SupplierBranchID` int NULL DEFAULT NULL COMMENT '财务分部ID',
  `PayPoolName` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付池名称',
  `Status` int NULL DEFAULT NULL COMMENT '更新人',
  `ReMark` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `LastUpDateTime` datetime NULL DEFAULT NULL COMMENT '最后更改时间',
  `CreateBy` int NOT NULL DEFAULT 0 COMMENT '创建人',
  `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `updateBy` int NOT NULL DEFAULT 0 COMMENT '修改人',
  `pay_pool_type` int NOT NULL DEFAULT 0 COMMENT '支付池类型 (1=三方支付池,2银行卡支付池)',
  `surplier_type` int NOT NULL DEFAULT 0 COMMENT '卡类型（0为天下卡，1为卡商卡，默认0）',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`PayPoolID`) USING BTREE,
  INDEX `SupplierBranchID_Index`(`SupplierBranchID` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '财务分部支付池' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_paypoolchannel
-- ----------------------------
DROP TABLE IF EXISTS `t_paypoolchannel`;
CREATE TABLE `t_paypoolchannel`  (
  `PayPoolID` int NOT NULL COMMENT '乐观锁',
  `PayChannelID` int NOT NULL COMMENT '创建人',
  `ChannelRate` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '费率Json格式,0:2或者1：5表示每笔2元或按额度收取5%',
  `Status` int NULL DEFAULT NULL COMMENT '1:启用；0：停用;-1:删除',
  `ReMark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `LastUpDateTime` datetime NULL DEFAULT NULL,
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`PayPoolID`, `PayChannelID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '支付池支付渠道配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_paytype
-- ----------------------------
DROP TABLE IF EXISTS `t_paytype`;
CREATE TABLE `t_paytype`  (
  `PayTypeID` int NOT NULL COMMENT '支付方式ID',
  `PayTypeName` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付方式名称',
  `PayCode` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付方式代码,比如支付宝=zfb',
  `Status` int NULL DEFAULT NULL COMMENT '状态（1 启用，0 停用）',
  `ReMark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `LastUpDateTime` datetime NULL DEFAULT NULL,
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `IsFastPay` int NULL DEFAULT 0 COMMENT '是否极速支付（0 默认不是，1 是）',
  PRIMARY KEY (`PayTypeID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '支付方式' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_phonemsg
-- ----------------------------
DROP TABLE IF EXISTS `t_phonemsg`;
CREATE TABLE `t_phonemsg`  (
  `id` bigint NOT NULL,
  `cellphone` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号码',
  `SupplierBranchID` int NOT NULL DEFAULT 0 COMMENT '分部ID',
  `SerialNumber` varchar(90) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '序列号',
  `VerificationCode` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '短信验证码',
  `msgtxt` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '短信内容',
  `date_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `bankcode` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行卡简码',
  `create_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `bankcode_index`(`bankcode` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '短信表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_qr
-- ----------------------------
DROP TABLE IF EXISTS `t_qr`;
CREATE TABLE `t_qr`  (
  `QrID` int NOT NULL COMMENT '二维码ID',
  `PayChannelID` int NULL DEFAULT NULL COMMENT '支付渠道ID',
  `SupplierBranchID` int NULL DEFAULT NULL COMMENT '财务分部ID',
  `QrCode` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '二维码简洁代码',
  `QrName` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '二维码名称',
  `QrAddress` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '二维码地址',
  `Balance` decimal(10, 2) NULL DEFAULT NULL COMMENT '余额',
  `MaxBalance` decimal(10, 2) NULL DEFAULT NULL COMMENT '最大余额，超过则要下发,',
  `QrPwd` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `SumAmountPerDayTrans` decimal(10, 2) NULL DEFAULT NULL COMMENT '每日交易总额限额,默认0不限制',
  `SumTimesPerDayTrans` decimal(10, 2) NULL DEFAULT NULL COMMENT '每日交易笔数限额,默认0不限制',
  `MinAmountPerTrans` decimal(10, 2) NULL DEFAULT NULL COMMENT '每笔交易最小额度,默认0不限制',
  `MaxAmountPerTrans` decimal(10, 2) NULL DEFAULT NULL COMMENT '每笔交易最大额度,默认0不限制',
  `Status` int NULL DEFAULT NULL COMMENT '1：启用，0:停用，-1:软删除,-2:预启用, -3:交易限额停用 ,-4:余额超额停用,-5:交易笔数超额停用,-6:无转入转出停用,-7异常，100：在线',
  `Comments` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '人工备注',
  `ReMark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `Version` bigint NULL DEFAULT 0,
  `MD5` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片生成MD5校验值去重',
  `QrType` int NULL DEFAULT 1 COMMENT '二维码类型 1店长  2店员',
  `QrRelation` int NULL DEFAULT 0 COMMENT '店长关联的店员ID，二维码为店员码时此字段为0',
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `LastUpDateTime` datetime NULL DEFAULT NULL,
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`QrID`) USING BTREE,
  UNIQUE INDEX `QrCode_UNIQUE`(`QrCode` ASC) USING BTREE,
  UNIQUE INDEX `QrName_UNIQUE`(`QrName` ASC) USING BTREE,
  INDEX `PayChannelID_Index`(`PayChannelID` ASC) USING BTREE,
  INDEX `SupplierBranchID_Index`(`SupplierBranchID` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '二维码' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_qrbanktrans
-- ----------------------------
DROP TABLE IF EXISTS `t_qrbanktrans`;
CREATE TABLE `t_qrbanktrans`  (
  `ID` bigint NOT NULL COMMENT '流水ID',
  `SupplierBranchID` int NULL DEFAULT NULL COMMENT '财务分部ID',
  `ClientID` int NULL DEFAULT NULL COMMENT '客户端编号',
  `BankCode` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '微信/支付宝/云闪付',
  `BankAccount` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'QrName',
  `BankID` int NULL DEFAULT NULL COMMENT 'QrID',
  `PayChannelID` int NULL DEFAULT NULL COMMENT '渠道编号',
  `TransTime` datetime NULL DEFAULT NULL COMMENT '交易时间',
  `TransType` int NULL DEFAULT NULL COMMENT '交易方式:0支出,1:收入',
  `TransAmount` decimal(10, 2) NULL DEFAULT NULL,
  `TransFee` decimal(10, 2) NULL DEFAULT NULL COMMENT '交易金额',
  `PreBalance` decimal(10, 2) NULL DEFAULT NULL COMMENT '手续费',
  `Balance` decimal(10, 2) NULL DEFAULT NULL COMMENT '当前余额',
  `Remark` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `Name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '交易姓名',
  `TransAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '交易账号',
  `ReadMark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '锁定标记',
  `Status` int NULL DEFAULT NULL COMMENT '处理状态，0：创建，1：处理中，2：已成功处理，3：超时移入异常收支',
  `CreateTime` datetime NULL DEFAULT NULL COMMENT '数据保存时间',
  `PickTime` datetime NULL DEFAULT NULL COMMENT '抓取时间',
  `Md5Sign` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Md5Sign',
  `TransId` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '三方交易流水号',
  `OrderIndex` bigint NULL DEFAULT NULL COMMENT '订单索引',
  `OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '关联三方订单ID',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE INDEX `TransId_UNIQUE`(`TransId` ASC) USING BTREE,
  UNIQUE INDEX `ReadMark_UNIQUE`(`ReadMark` ASC) USING BTREE,
  UNIQUE INDEX `Md5Sign_UNIQUE`(`Md5Sign` ASC) USING BTREE,
  INDEX `CombPrimary_Key`(`BankAccount` ASC, `TransTime` ASC, `TransAmount` ASC) USING BTREE,
  INDEX `CreateTime_Index`(`CreateTime` ASC) USING BTREE,
  INDEX `PickTime_Index`(`PickTime` ASC) USING BTREE,
  INDEX `Trans_type_time`(`TransType` ASC, `SupplierBranchID` ASC) USING BTREE,
  INDEX `Transtime_index`(`TransTime` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '固码流水表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_qrinchannel
-- ----------------------------
DROP TABLE IF EXISTS `t_qrinchannel`;
CREATE TABLE `t_qrinchannel`  (
  `QrID` int NOT NULL,
  `ChannelID` int NOT NULL,
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`QrID`, `ChannelID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_qrpayurl
-- ----------------------------
DROP TABLE IF EXISTS `t_qrpayurl`;
CREATE TABLE `t_qrpayurl`  (
  `QrID` int NULL DEFAULT NULL COMMENT 'QrID,来自Qr表',
  `Amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '对应金额',
  `RealAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '实际金额',
  `Url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '超链接',
  `Status` int NULL DEFAULT NULL COMMENT '状态1：启用，0：停用',
  `MD5Key` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'url MD5值',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`MD5Key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '二维码对应码链接' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_qrsummary
-- ----------------------------
DROP TABLE IF EXISTS `t_qrsummary`;
CREATE TABLE `t_qrsummary`  (
  `QrID` int NOT NULL,
  `DepositAmount` decimal(10, 2) NOT NULL,
  `Calendar` date NOT NULL,
  `LastUpDateTime` datetime NOT NULL,
  `DepositTimes` int NULL DEFAULT NULL,
  `Version` bigint NOT NULL,
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`QrID`, `Calendar`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_request_domain
-- ----------------------------
DROP TABLE IF EXISTS `t_request_domain`;
CREATE TABLE `t_request_domain`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `cdn_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'CDN厂商名',
  `domain_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '域名地址',
  `user_level` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户等级，多个使用，分割 ',
  `user_credit_level` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户层级，多个使用，分割',
  `status` int NOT NULL DEFAULT 0 COMMENT '服务状态(-1:删除,0:停用,1:启用)',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '修改者',
  `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '创建者',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `backup_domain_url` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备份域名地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '三方域名请求配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_service_charge_daily_record
-- ----------------------------
DROP TABLE IF EXISTS `t_service_charge_daily_record`;
CREATE TABLE `t_service_charge_daily_record`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `agent_id` bigint NOT NULL COMMENT '代理ID',
  `merchant_id` bigint NOT NULL COMMENT '商户ID',
  `deposit_pay_amount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '入款下单金额',
  `deposit_paid_amount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '入款实际金额',
  `deposit_supplier_charge` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '入款商户服务费',
  `deposit_agent_charge` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '入款佣金',
  `withdraw_pay_amount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '出款下单金额',
  `withdraw_paid_amount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '出款实际金额',
  `withdraw_supplier_charge` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '出款商户服务费',
  `withdraw_agent_charge` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '出款佣金',
  `statistic_date` date NOT NULL COMMENT '统计时间',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '节点ID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_time_agent_merchant`(`statistic_date` ASC, `agent_id` ASC, `merchant_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '日结佣金收益表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_serviceaddress
-- ----------------------------
DROP TABLE IF EXISTS `t_serviceaddress`;
CREATE TABLE `t_serviceaddress`  (
  `ServiceKey` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务关键字',
  `ServiceName` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务名称',
  `ServiceAddress` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '服务地址',
  `Status` int NULL DEFAULT 0 COMMENT '服务状态，1：启用，0：停用',
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `UpdateTime` datetime NULL DEFAULT NULL,
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`ServiceKey`, `ServiceAddress`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '服务地址配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_surppilerbranch
-- ----------------------------
DROP TABLE IF EXISTS `t_surppilerbranch`;
CREATE TABLE `t_surppilerbranch`  (
  `ID` int NOT NULL COMMENT 'ID',
  `CompanyID` int NULL DEFAULT NULL,
  `BranchName` varchar(180) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '财务分支名称',
  `Status` int NULL DEFAULT 0 COMMENT '状态,1:启用，0：停用，-1：删除',
  `CreateBy` int NULL DEFAULT NULL COMMENT '更新人',
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `UpdateBy` int NULL DEFAULT NULL,
  `UpdateTime` datetime NULL DEFAULT NULL,
  `parent_id` int NOT NULL DEFAULT 0 COMMENT '父级财务id',
  `decrypt_support` int NOT NULL DEFAULT 0 COMMENT '是否支持脱敏，默认为0，不支持脱敏',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '供应商公司信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_surppliercompany
-- ----------------------------
DROP TABLE IF EXISTS `t_surppliercompany`;
CREATE TABLE `t_surppliercompany`  (
  `ID` int NOT NULL COMMENT '公司编号',
  `CompanyName` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公司名称',
  `Status` int NOT NULL DEFAULT 0 COMMENT '状态，1：启用，0：停用，-1：软删除',
  `CreateBy` int NOT NULL COMMENT '创建人',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UpdateBy` int NULL DEFAULT NULL COMMENT '更新人',
  `UpdateTime` datetime NULL DEFAULT NULL COMMENT '最后更新时间',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`ID`, `CompanyName`, `Status`, `CreateBy`, `CreateTime`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '财务分部公司信息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_sys_bankcard
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_bankcard`;
CREATE TABLE `t_sys_bankcard`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '银行卡卡号',
  `bankcard_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行卡卡号',
  `bankcard_owner` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行卡所属人姓名',
  `bank_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行类型编码',
  `bankcard_address` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '开户行地址',
  `balance` decimal(12, 2) NOT NULL DEFAULT 0.00 COMMENT '银行卡余额',
  `card_status` tinyint NOT NULL DEFAULT 1 COMMENT '状态 0可用 1启用 2禁用 9删除',
  `insert_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `update_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `last_order_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上次分配充值订单时间',
  `last_money_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上次收款时间',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_sys_bankcard_banknumber`(`bankcard_number` ASC) USING BTREE,
  INDEX `idx_status`(`card_status` ASC) USING BTREE,
  INDEX `idx_time`(`last_order_time` ASC, `last_money_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统收款银行卡' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_third_merchant_withdraw_order
-- ----------------------------
DROP TABLE IF EXISTS `t_third_merchant_withdraw_order`;
CREATE TABLE `t_third_merchant_withdraw_order`  (
  `ID` bigint NOT NULL DEFAULT 0 COMMENT '主键ID',
  `OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '订单号',
  `MerchantID` int NOT NULL DEFAULT 0 COMMENT '商户ID',
  `SupplierBranchID` int NOT NULL DEFAULT 0 COMMENT '财务分部ID',
  `ChannelOrderID` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '平台提交给充值渠道的订单号',
  `PayChannelID` int NOT NULL DEFAULT 0 COMMENT '渠道ID',
  `PayAmount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '提款金额',
  `PaidAmount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '实际到账金额',
  `PaidChannelFee` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '支付费用',
  `NotifyUrl` varchar(516) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户用于接收订单通知的地址',
  `ReturnUrl` varchar(516) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '订单提交后立即返回的商户页面地址',
  `OrderStatus` int NOT NULL DEFAULT 0 COMMENT '订单状态0:财务待审核，默认值;1：处理中;2:清算中;3:正常支付关闭;5:超时,6:超时关闭；7:取消关闭;9:后台待核;10财务拒绝;11 后台拒绝；12 后台待分配',
  `OrderPaidStatusChangeTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单状态最后修改时间',
  `MerchantBankID` int NOT NULL DEFAULT 0 COMMENT '商户银行卡ID',
  `BankCode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户银行编码',
  `BankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户银行卡简码',
  `BankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户银行账号',
  `BankAddress` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '开户行',
  `BankOwner` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '开户姓名',
  `MD5OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '订单ID加密串',
  `Digest` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'Digest',
  `Remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `CloseReason` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '订单手工关闭原因',
  `OrderType` int NOT NULL DEFAULT 0 COMMENT '0:商户订单',
  `ApprovePersonnel` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '一审人员',
  `ApproveStatus` tinyint NOT NULL DEFAULT 0 COMMENT '一审状态',
  `ApproveRemark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '一审备注',
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UserId` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '会员ID',
  `UserIP` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '会员IP',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE INDEX `uniq_OrderID`(`OrderID` ASC) USING BTREE,
  UNIQUE INDEX `uniq_ChannelOrderID`(`ChannelOrderID` ASC) USING BTREE,
  INDEX `idx_CreateTime`(`CreateTime` ASC) USING BTREE,
  INDEX `idx_MerchantID`(`MerchantID` ASC) USING BTREE,
  INDEX `idx_OrderPaidStatusChangeTime`(`OrderPaidStatusChangeTime` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '三方聚合下发订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_third_risk_order
-- ----------------------------
DROP TABLE IF EXISTS `t_third_risk_order`;
CREATE TABLE `t_third_risk_order`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `branch_id` int NOT NULL DEFAULT 0 COMMENT '财务分支ID',
  `third_merchant_id` int NOT NULL DEFAULT 0 COMMENT '三方商户表(t_thirdmerchant)的ID',
  `pay_type` int NOT NULL DEFAULT 0 COMMENT '充值方式 0：提款 1：支付宝转银行卡 2：银行卡大额充值 3：云闪付转卡 4：支付宝转卡H5 5：微信转卡 6：银联代付 7：快捷支付 8：银联扫码 9：网银支付 10：微信扫码 11：微信H5 12：支付宝H5 13：支付宝扫码 14：QQ扫码 15：QQH5 16：京东扫码 17：云闪付 18：内转代付 19：极速支付宝 20：极速微信 21：极速支付 22：抖音支付 23：三方银行卡转卡 24：三方支付宝转账 25：三方大额充值 26：三方云闪付转卡 27：小额三方银行卡转卡 28：USDT扫码 29：虚拟币交易 30：买币支付',
  `order_type` tinyint NOT NULL DEFAULT 1 COMMENT '订单类型（1充值，2提款）',
  `merchant_order_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '我方订单号',
  `order_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '三方商户的订单号',
  `member_account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '会员账号',
  `bank_num` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '卡号 提款的时候是会员的卡号，充值的时候是给会员支付的卡号',
  `card_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '卡住姓名',
  `order_amount` decimal(14, 2) NOT NULL DEFAULT 0.00 COMMENT '订单金额',
  `paid_amount` decimal(14, 2) NOT NULL DEFAULT 0.00 COMMENT '真实支付金额',
  `order_status` tinyint NOT NULL DEFAULT 0 COMMENT '订单状态 -1异常 0创建 1成功 2失败',
  `complete_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '三方商户订单完成时间',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_order_index`(`merchant_order_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '风控-三方商户对账订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_third_withdraw_order
-- ----------------------------
DROP TABLE IF EXISTS `t_third_withdraw_order`;
CREATE TABLE `t_third_withdraw_order`  (
  `OrderIndex` bigint NOT NULL COMMENT '订单索引',
  `OrderID` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '平台订单号商户简称（5位字符，不足位0补齐）+ yyyyMMddHHmmssfff（时间17位）+1位字符+库编号(2位数数字，先固定为00）',
  `SupplierBranchID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '财务分部ID',
  `ParentID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '父订单ID',
  `ChannelOrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '三方平台返回的订单号',
  `MerchantID` int NULL DEFAULT NULL COMMENT '商户编号',
  `MerchantOrderID` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户订单号',
  `UserLevel` int NULL DEFAULT 0,
  `PayType` int NULL DEFAULT NULL COMMENT '付款方式,1：银行卡，2：第三方，3：其他方式',
  `PayAmount` decimal(18, 2) NULL DEFAULT 0.00 COMMENT '订单提款金额',
  `PaidAmount` decimal(18, 2) NULL DEFAULT 0.00 COMMENT '实际到账金额',
  `PaidChannelFee` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '支付给三方渠道的手续费',
  `SupplierPaidChannelFee` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '商户需付手续费',
  `AgencyCommission` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理佣金',
  `NotifyUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户用于接收订单通知的地址',
  `ReturnUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单提交后立即返回的商户页面地址',
  `MerchantRemark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户备注',
  `PayChannelID` int NULL DEFAULT NULL COMMENT '处理该订单的充值渠道',
  `OrderPaidStatusChangeTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `OrderNotifyStatus` int NULL DEFAULT 0 COMMENT '通知状态；0:未成功通知；1：已成功通知;2:通知超时异常',
  `OrderNotifyTime` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '订单通知状态最后修改时间',
  `Remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `BankCode` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '取款银行类型',
  `BankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `BankAddress` varchar(270) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `BankOwner` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `ReadMark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `MD5OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `OrderStatus` int NULL DEFAULT 0 COMMENT '订单状态0:创建，默认值;1：处理中;2:清算中;3:正常支付关闭;4：手工确认订单已完成关闭; 5:超时,6:超时关闭；7:取消关闭',
  `Digest` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `CloseReason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '关闭原因',
  `OrderType` int NULL DEFAULT NULL,
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `BankAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `UserId` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员ID',
  `UserIP` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员IP',
  `Operator` int NULL DEFAULT 0 COMMENT '操作员',
  `MemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员账号',
  `UserCreditLevel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户层级，根据用户层级等级分配不同支付渠道;多个层级，使用‘,’分割，比如支持5_1,5_2层级，则是5_1,5_2',
  `EncryptBankNum` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡号',
  `EncryptBankAddress` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开户地址',
  `EncryptBankOwner` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '提款人',
  `EncryptBankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡简码',
  `EncryptMemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员账号',
  `Protocol` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'usdt的协议',
  `transferOrderTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '首次创建时间',
  `MatchMerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '匹配的充值订单商户订单号，极速订单使用',
  `FastToNormalStatus` int NOT NULL DEFAULT 0 COMMENT '极速提款订单转普通订单状态 0 默认，1: 转普通订单成功；2：已成功通知',
  `MatchPlatformOrderId` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '匹配充值订单平台订单号，极速订单使用',
  `Currency` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '渠道币种 默认为空 数值参考bank_type',
  `RequestSource` int NOT NULL DEFAULT 0 COMMENT '操作来源（极速支付使用）',
  `Rate` decimal(10, 4) NOT NULL DEFAULT 1.0000 COMMENT '商户汇率',
  `AmountType` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'CNY' COMMENT '金额类型 默认CNY 具体数值参考币种',
  `HashTransID` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'USDT虚拟币交易id 用于查询交易金额',
  `VirtualPayAmount` decimal(11, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币金额',
  `VirtualPaidAmount` decimal(11, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币金额',
  `VirtualPayType` tinyint NOT NULL DEFAULT 0 COMMENT '虚拟币支付类型 1易币付',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `IsFastPay` int NOT NULL DEFAULT 0 COMMENT '默认0，0：普通订单，1：极速订单，10：极速父订单，11:极速子订单',
  `MatchTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '匹配时间',
  `ParentOrderId` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '极速父订单ID',
  `DepositConfirmTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '充值确认时间',
  `DepositName` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '存款姓名（极速）',
  `deduction` tinyint NOT NULL DEFAULT 0 COMMENT '扣费标识，0未扣费，1已扣费',
  `bill_sign` tinyint NOT NULL DEFAULT 0 COMMENT '结算标识，0未结算，1已结算',
  `process_pay_type` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '最初处理该订单的支付方式',
  `PriorityMatchStatus` int NULL DEFAULT 0 COMMENT '优先匹配状态 0未匹配 1已匹配',
  `ParentOrderStatus` tinyint NOT NULL DEFAULT 1 COMMENT '急速父订单状态 1 处理中 3 成功 7失败',
  `OrderJustifyTime` datetime NULL DEFAULT NULL COMMENT '订单抓取时间',
  `withdrawType` int NOT NULL DEFAULT 0 COMMENT '取款类型',
  `ImageAddr` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '图片地址 英文逗号隔开',
  `order_handle_type` int NOT NULL DEFAULT 0 COMMENT '钱包：订单处理类型 0 自动  1 手动',
  `MostPriorityMatchStatus` int NOT NULL DEFAULT 0 COMMENT '最优匹配状态 0否 1是',
  `betType` int NOT NULL DEFAULT 0 COMMENT '0 不投注 1 投注',
  `certified_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '认证姓名',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据入库时间',
  `HankType` int NOT NULL DEFAULT 0,
  `withdrawSuccessNum` int NOT NULL DEFAULT 0 COMMENT '提款成功次数',
  `split_order` int NOT NULL DEFAULT 0 COMMENT '是否转三方 1不转，中台不拆单（会员账号高亮标记） 0 可转，中台拆单（会员账号不标记）',
  `two_matched_flag` int NOT NULL DEFAULT 1 COMMENT '默认1,否则为11代表二次匹配过',
  `apiRemark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户API提现备注',
  `businessGroup` tinyint NOT NULL DEFAULT 0 COMMENT '业务组别:0未知;1三方;2外部代付',
  `appSite` tinyint NOT NULL DEFAULT 0 COMMENT '会员归属类型:0空;1内部为KY,BOB,BW;2外部',
  `user_address` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户地址',
  `is_dc` int NOT NULL DEFAULT 0 COMMENT '是否dc订单 0否 1是',
  `is_change` tinyint NULL DEFAULT NULL COMMENT '极速取款时是否为零钱匹配 0 否 1 是',
  `settlementSite` int NULL DEFAULT NULL COMMENT '结算站点',
  `SplitSubsidyAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '拆单补贴金额',
  `merchant_total_rebate` decimal(14, 4) NULL DEFAULT 0.0000 COMMENT '商户返利总金额=百分比返利金额*成交金额+固定金额返利',
  `EncryptBankMobile` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '提款人银行预留手机号',
  `EncryptIdNum` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '提款人身份证号',
  PRIMARY KEY (`OrderIndex`) USING BTREE,
  UNIQUE INDEX `ReadMark_UNIQUE`(`ReadMark` ASC) USING BTREE,
  UNIQUE INDEX `ChannelOrderID_UNIQUE`(`ChannelOrderID` ASC) USING BTREE,
  INDEX `idx_merchantid_status_time`(`MerchantID` ASC, `OrderStatus` ASC, `FastToNormalStatus` ASC, `OrderPaidStatusChangeTime` ASC) USING BTREE,
  INDEX `idx_tmp2`(`FastToNormalStatus` ASC) USING BTREE,
  INDEX `idx_tmp3`(`PayType` ASC) USING BTREE,
  INDEX `MerchantID`(`MerchantID` ASC, `OrderStatus` ASC, `FastToNormalStatus` ASC, `PayType` ASC, `OrderPaidStatusChangeTime` ASC) USING BTREE,
  INDEX `idx_ParentOrderID_isFastPay`(`ParentOrderId` ASC, `IsFastPay` ASC) USING BTREE,
  INDEX `idx_memaccount_time`(`EncryptMemberAccount` ASC, `OrderPaidStatusChangeTime` ASC) USING BTREE,
  INDEX `idx_MD5OrderID`(`MD5OrderID` ASC) USING BTREE,
  INDEX `idx_OrderID_IDX`(`OrderID` ASC) USING BTREE,
  INDEX `idx_MerchantOrderID_Index`(`MerchantOrderID` ASC) USING BTREE,
  INDEX `idx_fast`(`FastToNormalStatus` ASC, `PayType` ASC) USING BTREE,
  INDEX `Digest_Index`(`Digest` ASC) USING BTREE,
  INDEX `CreateTime_Index`(`CreateTime` ASC) USING BTREE,
  INDEX `idx_merchantid_time`(`MerchantID` ASC, `OrderStatus` ASC, `OrderPaidStatusChangeTime` ASC) USING BTREE,
  INDEX `OrderPaidStatusChangeTime_index`(`OrderPaidStatusChangeTime` ASC) USING BTREE,
  INDEX `OrderType_Index`(`OrderType` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '三方渠道提款订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_thirdchannel_call_params
-- ----------------------------
DROP TABLE IF EXISTS `t_thirdchannel_call_params`;
CREATE TABLE `t_thirdchannel_call_params`  (
  `call_param_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `merchant_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '三方商户编码',
  `channel_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '渠道编码：商户渠道号',
  `param_type` int NOT NULL DEFAULT 0 COMMENT '参数类型：10：下单、20查单、30通知、40查余额',
  `param_en_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '渠道参数变量名称',
  `success_res_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '渠道调用成功响应码',
  `fail_res_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '-1' COMMENT '渠道调用失败响应码',
  `param_value_exp` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '参数值为表达式',
  `work_type` int NOT NULL DEFAULT 0 COMMENT '工作模式',
  `order_no` int NOT NULL DEFAULT 1 COMMENT '做为加密排序规则',
  `status` int NOT NULL DEFAULT 1 COMMENT '-1为软删除，0停用，1启用',
  `pay_type` int NOT NULL COMMENT '支付方式',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`call_param_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '渠道调用参数映射表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_thirdchannel_config_params
-- ----------------------------
DROP TABLE IF EXISTS `t_thirdchannel_config_params`;
CREATE TABLE `t_thirdchannel_config_params`  (
  `config_param_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `param_cn_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '订单变量中文名称',
  `param_en_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '订单变量英文名称',
  `param_type` int NOT NULL DEFAULT 0 COMMENT '参数类型：0：公共参数  10：下单、20查单、50查余额',
  `merchant_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '参数所属商户编码',
  `merchant_type` bigint NOT NULL DEFAULT 0 COMMENT '参数类型：0：公共参数，1:三方聚合参数，2:天下参数，3：商户参数',
  `order_no` bigint NOT NULL DEFAULT 0 COMMENT '排序',
  `work_type` int NOT NULL DEFAULT 0 COMMENT '可用的工作模式，1充值2提款，默认0都可用',
  `status` int NOT NULL DEFAULT 1 COMMENT '-1为软删除，0停用，1启用',
  `pay_type` int NOT NULL COMMENT '支付方式',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`config_param_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '三方商户-支付订单参数配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_thirdchannel_notfy_log
-- ----------------------------
DROP TABLE IF EXISTS `t_thirdchannel_notfy_log`;
CREATE TABLE `t_thirdchannel_notfy_log`  (
  `id` int UNSIGNED NOT NULL COMMENT 'id',
  `merchant_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商户code',
  `order_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单id',
  `EncryptData` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '回调所有数据',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_merchant_order_id`(`merchant_code` ASC, `order_id` ASC) USING BTREE,
  INDEX `idx_merchang_code`(`merchant_code` ASC) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '三方关键日志记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_thirdchannelissuedrecord
-- ----------------------------
DROP TABLE IF EXISTS `t_thirdchannelissuedrecord`;
CREATE TABLE `t_thirdchannelissuedrecord`  (
  `OrderIndex` int NOT NULL AUTO_INCREMENT COMMENT '平台订单索引',
  `PayChannelID` int NULL DEFAULT NULL COMMENT '渠道ID',
  `OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '订单号',
  `ChannelOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '渠道订单号',
  `TransType` int NULL DEFAULT NULL COMMENT '交易方式,0：充值，1：提款',
  `TransAmount` decimal(10, 2) NULL DEFAULT 0.00,
  `PaidAmount` decimal(10, 2) NULL DEFAULT 0.00,
  `SupplierPaidChannelFee` decimal(10, 2) NULL DEFAULT 0.00,
  `PayChannelFee` decimal(10, 2) NULL DEFAULT 0.00,
  `SurpplierFee` decimal(10, 2) NULL DEFAULT 0.00,
  `PreBalance` decimal(14, 2) NULL DEFAULT 0.00 COMMENT '交易前余额',
  `Balance` decimal(14, 2) NULL DEFAULT 0.00 COMMENT '当前余额',
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `SupplierBranchID` int NULL DEFAULT NULL,
  `MerchantID` int NULL DEFAULT NULL COMMENT '商户ID ',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`OrderIndex`) USING BTREE,
  INDEX `ChannelOrderID_index`(`ChannelOrderID` ASC, `PayChannelID` ASC) USING BTREE,
  INDEX `paychannelid_index`(`PayChannelID` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '三方渠道下发账变表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_thirdchannelwithdraworder
-- ----------------------------
DROP TABLE IF EXISTS `t_thirdchannelwithdraworder`;
CREATE TABLE `t_thirdchannelwithdraworder`  (
  `OrderIndex` bigint NOT NULL COMMENT '订单索引',
  `OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `SupplierBranchID` int NULL DEFAULT NULL COMMENT '财务分部ID',
  `ChannelOrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '三方渠道返回的订单号',
  `PayChannelID` int NULL DEFAULT NULL COMMENT '渠道ID',
  `PayAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '提款金额',
  `PaidAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '实际到账金额',
  `PaidChannelFee` decimal(10, 2) NULL DEFAULT NULL COMMENT '交易手续费',
  `NotifyUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户用于接收订单通知的地址',
  `ReturnUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单提交后立即返回的商户页面地',
  `MerchantRemark` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户附言, 当支付方式是0网银转账时，可由商户发送附言\n否则系统提供附言',
  `OrderStatus` int NULL DEFAULT NULL COMMENT '订单状态0:创建，默认值;1：处理中;2:清算中;3:正常支付关闭;4：手工确认订单已完成关闭; 5:超时,6:超时关闭；7:取消关闭',
  `OrderPaidStatusChangeTime` datetime NULL DEFAULT NULL COMMENT '订单状态最后修改时间',
  `OrderNotifyStatus` int NULL DEFAULT NULL COMMENT '通知状态；0:未成功通知；1：已成功通知;2:通知超时异常',
  `OrderNotifyTime` datetime NULL DEFAULT NULL COMMENT '订单通知状态最后修改时间',
  `Remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `BankCode` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '取款银行类型',
  `BankNum` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '取款银行编码',
  `BankAddress` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '取款银行卡开户地址',
  `BankOwner` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '取款人名字',
  `ReadMark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用于标注是否被取款任务取走',
  `MD5OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `Digest` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `CloseReason` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单手工关闭原因',
  `OrderType` int NULL DEFAULT NULL COMMENT '0:商户订单，1:超额内转订单，2低额内转订单，3，充值测试转账订单,4提款测试转账订单,5卡异常转出金额,100父订单',
  `CreateTime` datetime NULL DEFAULT NULL,
  `CloseType` int UNSIGNED NULL DEFAULT 0 COMMENT '处理方式 0:自动 默认值;1:手动',
  `MerchantID` int NULL DEFAULT NULL COMMENT '商户ID',
  `Operator` int NOT NULL DEFAULT 0 COMMENT '操作人ID',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`OrderIndex`) USING BTREE,
  UNIQUE INDEX `OrderID_UNIQUE`(`OrderID` ASC) USING BTREE,
  UNIQUE INDEX `ChannelOrderID_UNIQUE`(`ChannelOrderID` ASC) USING BTREE,
  INDEX `OrderType_Index`(`OrderType` ASC) USING BTREE,
  INDEX `OrderPaidStatusChangeTime_index`(`OrderPaidStatusChangeTime` ASC) USING BTREE,
  INDEX `Digest_Index`(`Digest` ASC) USING BTREE,
  INDEX `CreateTime_Index`(`CreateTime` ASC) USING BTREE,
  INDEX `ReadMark_Index`(`ReadMark` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '三方渠道下发订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_thirddepositorder
-- ----------------------------
DROP TABLE IF EXISTS `t_thirddepositorder`;
CREATE TABLE `t_thirddepositorder`  (
  `OrderIndex` bigint NOT NULL COMMENT '订单索引',
  `OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `SupplierBranchID` int NULL DEFAULT NULL COMMENT '财务分部ID',
  `ChannelOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '三方平台返回的订单号',
  `MerchantID` int NULL DEFAULT NULL COMMENT '商户编号',
  `MerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户订单号最长64位',
  `MerchantOrderTime` datetime NULL DEFAULT NULL COMMENT '商户提交订单时间',
  `PayType` int NULL DEFAULT NULL COMMENT '充值方式,1:网银转账2:快捷支付3:银联支付4:微信5:支付宝6:QQ钱包7:京东支付8:云闪付',
  `ClientType` int NULL DEFAULT NULL COMMENT '客户端类型, 1：PC，2:WAP，4:APP',
  `PayAmount` decimal(18, 2) NULL DEFAULT 0.00 COMMENT '充值金额',
  `PaidAmount` decimal(18, 2) NULL DEFAULT NULL COMMENT '实际到账金额',
  `PaidChannelFee` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付给三方渠道的手续费',
  `SupplierPaidChannelFee` decimal(10, 2) NULL DEFAULT NULL COMMENT '商户需付手续费',
  `AgencyCommission` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理佣金',
  `NotifyUrl` varchar(516) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户用于接收订单通知的地址',
  `ReturnUrl` varchar(516) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单提交后立即返回的商户页面地址',
  `PayChannelID` int NULL DEFAULT NULL COMMENT '处理该订单的充值渠道',
  `MD5OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'MD5签名',
  `BankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '考虑到风云这些第三方卡系统',
  `OrderStatus` int NULL DEFAULT 0 COMMENT '态0:创建，默认值;1：处理中;2:清算中;3:正常支付关闭;4：手工确认订单已完成关闭; 5:超时,6:超时关闭；7:取消关闭',
  `OrderPaidStatusChangeTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `OrderNotifyStatus` int NULL DEFAULT 0 COMMENT '通知状态；0:未成功通知；1：已成功通知;2:通知超时异常',
  `OrderNotifyTime` datetime NULL DEFAULT NULL COMMENT '订单通知状态最后修改时间',
  `Remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `Name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '付款人姓名,\n有姓名时附言和姓名同时匹配，若匹配失败则先用附言匹配，再匹配失败则用姓名匹配',
  `ReadMark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用于标注是否被自动付款取走',
  `CloseReason` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '关闭原因',
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `UserID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户会员ID',
  `UserIP` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户会员IP',
  `PrePaidAmount` decimal(10, 2) NULL DEFAULT NULL,
  `OrderType` int NULL DEFAULT 0 COMMENT '商户订单',
  `UserLevel` int NULL DEFAULT NULL COMMENT '商户会员等级',
  `Operator` int NULL DEFAULT 0 COMMENT '操作员',
  `MemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '会员账号',
  `BankCode` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '银行编码',
  `UserCreditLevel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户层级，根据用户层级等级分配不同支付渠道;多个层级，使用‘,’分割，比如支持5_1,5_2层级，则是5_1,5_2',
  `EncryptBankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '考虑到风云这些第三方卡系统',
  `SourceType` tinyint NOT NULL DEFAULT 0 COMMENT '提单方式',
  `EncryptName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '付款人姓名,\n有姓名时附言和姓名同时匹配，若匹配失败则先用附言匹配，再匹配失败则用姓名匹配',
  `EncryptMemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '会员账号',
  `Protocol` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'usdt的协议',
  `EncryptBankOwner` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '收款卡姓名',
  `EncryptBankNum` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '收款卡号',
  `EncryptBankAddress` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '开户地址',
  `EncryptDepositName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '真实存款人名字',
  `MatchMerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '匹配提款订单商户订单号，极速订单使用',
  `MatchPlatformOrderId` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '匹配提款订单平台订单号，极速订单使用',
  `Currency` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '渠道币种 默认为空 人民币 数值参考bank_type',
  `RequestSource` int NOT NULL DEFAULT 0 COMMENT '操作来源（极速支付使用）',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `OrderExchangeRate` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '汇率',
  `IsFastPay1` int NULL DEFAULT NULL COMMENT '是否极速支付（0 默认不是，1 是）',
  `IsFastPay` int NULL DEFAULT 0 COMMENT '是否极速支付（0 默认不是，1 是）',
  `DepositConfirmTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '充值确认时间',
  `ImageAddr` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '图片地址 英文逗号隔开',
  `VirtualPayAmount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币金额',
  `VirtualPaidAmount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币实际金额',
  `VirtualBasicRate` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币基础汇率',
  `VirtualRate` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币浮动汇率',
  `FromAddress` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '虚拟币会员存款地址',
  `certified_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '认证姓名',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据入库时间',
  `blank_order_flag` int NOT NULL DEFAULT 0 COMMENT '是否为空单  1 空单 0 不是空单',
  `IsSmsMatch` tinyint NOT NULL DEFAULT 0 COMMENT '是否短信匹配 0否 1是',
  `player_device_number` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '会员设备号',
  `player_level` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '会员等级',
  `appSite` tinyint NOT NULL DEFAULT 0 COMMENT '会员归属类型:0空;1内部为KY,BOB,BW;2外部',
  `risk_type` tinyint NOT NULL DEFAULT 0 COMMENT '风控类型 0:未风控 1:中台风控 2:天下风控',
  `shoppingUrl` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商城链接Url',
  `user_address` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户地址',
  PRIMARY KEY (`OrderIndex`) USING BTREE,
  UNIQUE INDEX `uniq_OrderID`(`OrderID` ASC) USING BTREE,
  UNIQUE INDEX `ReadMark_UNIQUE`(`ReadMark` ASC) USING BTREE,
  UNIQUE INDEX `MerchantOrderStatus_Index`(`MerchantOrderID` ASC, `OrderStatus` ASC) USING BTREE,
  UNIQUE INDEX `MD5OrderID_UNIQUE`(`MD5OrderID` ASC) USING BTREE,
  INDEX `PayChannelID_IDX`(`PayChannelID` ASC) USING BTREE,
  INDEX `idx_CreateTime_MemberAccount`(`CreateTime` ASC, `MemberAccount` ASC) USING BTREE,
  INDEX `idx_ChangeTime_Supplier_MemberAccount`(`OrderPaidStatusChangeTime` ASC, `SupplierBranchID` ASC, `MemberAccount` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '三方充值订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_thirddepositorder_bak
-- ----------------------------
DROP TABLE IF EXISTS `t_thirddepositorder_bak`;
CREATE TABLE `t_thirddepositorder_bak`  (
  `OrderIndex` bigint NOT NULL COMMENT '订单索引',
  `OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `SupplierBranchID` int NULL DEFAULT NULL COMMENT '财务分部ID',
  `ChannelOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '三方平台返回的订单号',
  `MerchantID` int NULL DEFAULT NULL COMMENT '商户编号',
  `MerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户订单号最长64位',
  `MerchantOrderTime` datetime NULL DEFAULT NULL COMMENT '商户提交订单时间',
  `PayType` int NULL DEFAULT NULL COMMENT '充值方式,1:网银转账2:快捷支付3:银联支付4:微信5:支付宝6:QQ钱包7:京东支付8:云闪付',
  `ClientType` int NULL DEFAULT NULL COMMENT '客户端类型, 1：PC，2:WAP，4:APP',
  `PayAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '充值金额',
  `PaidAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '实际到账金额',
  `PaidChannelFee` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付给三方渠道的手续费',
  `SupplierPaidChannelFee` decimal(10, 2) NULL DEFAULT NULL COMMENT '商户需付手续费',
  `AgencyCommission` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理佣金',
  `NotifyUrl` varchar(516) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户用于接收订单通知的地址',
  `ReturnUrl` varchar(516) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单提交后立即返回的商户页面地址',
  `PayChannelID` int NULL DEFAULT NULL COMMENT '处理该订单的充值渠道',
  `MD5OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'MD5签名',
  `BankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '考虑到风云这些第三方卡系统',
  `OrderStatus` int NULL DEFAULT 0 COMMENT '态0:创建，默认值;1：处理中;2:清算中;3:正常支付关闭;4：手工确认订单已完成关闭; 5:超时,6:超时关闭；7:取消关闭',
  `OrderPaidStatusChangeTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `OrderNotifyStatus` int NULL DEFAULT 0 COMMENT '通知状态；0:未成功通知；1：已成功通知;2:通知超时异常',
  `OrderNotifyTime` datetime NULL DEFAULT NULL COMMENT '订单通知状态最后修改时间',
  `Remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `Name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '付款人姓名,\n有姓名时附言和姓名同时匹配，若匹配失败则先用附言匹配，再匹配失败则用姓名匹配',
  `ReadMark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用于标注是否被自动付款取走',
  `CloseReason` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '关闭原因',
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `UserID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户会员ID',
  `UserIP` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户会员IP',
  `PrePaidAmount` decimal(10, 2) NULL DEFAULT NULL,
  `OrderType` int NULL DEFAULT 0 COMMENT '商户订单',
  `UserLevel` int NULL DEFAULT NULL COMMENT '商户会员等级',
  `Operator` int NULL DEFAULT 0 COMMENT '操作员',
  `MemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '会员账号',
  `BankCode` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '银行编码',
  `UserCreditLevel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户层级，根据用户层级等级分配不同支付渠道;多个层级，使用‘,’分割，比如支持5_1,5_2层级，则是5_1,5_2',
  `EncryptBankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '考虑到风云这些第三方卡系统',
  `SourceType` tinyint NOT NULL DEFAULT 0 COMMENT '提单方式',
  `EncryptName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '付款人姓名,\n有姓名时附言和姓名同时匹配，若匹配失败则先用附言匹配，再匹配失败则用姓名匹配',
  `EncryptMemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '会员账号',
  `Protocol` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'usdt的协议',
  `EncryptBankOwner` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '收款卡姓名',
  `EncryptBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '收款卡号',
  `EncryptBankAddress` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '开户地址',
  `EncryptDepositName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '真实存款人名字',
  `MatchMerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '匹配提款订单商户订单号，极速订单使用',
  `MatchPlatformOrderId` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '匹配提款订单平台订单号，极速订单使用',
  `Currency` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '渠道币种 默认为空 人民币 数值参考bank_type',
  `RequestSource` int NOT NULL DEFAULT 0 COMMENT '操作来源（极速支付使用）',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `OrderExchangeRate` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '汇率',
  `IsFastPay1` int NULL DEFAULT NULL COMMENT '是否极速支付（0 默认不是，1 是）',
  `IsFastPay` int NULL DEFAULT 0 COMMENT '是否极速支付（0 默认不是，1 是）',
  `DepositConfirmTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '充值确认时间',
  `ImageAddr` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '图片地址 英文逗号隔开',
  `VirtualPayAmount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币金额',
  `VirtualPaidAmount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币实际金额',
  `VirtualBasicRate` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币基础汇率',
  `VirtualRate` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币浮动汇率',
  `FromAddress` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '虚拟币会员存款地址',
  `certified_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '认证姓名',
  PRIMARY KEY (`OrderIndex`) USING BTREE,
  UNIQUE INDEX `uniq_OrderID`(`OrderID` ASC) USING BTREE,
  UNIQUE INDEX `ReadMark_UNIQUE`(`ReadMark` ASC) USING BTREE,
  UNIQUE INDEX `MerchantOrderStatus_Index`(`MerchantOrderID` ASC, `OrderStatus` ASC) USING BTREE,
  UNIQUE INDEX `MD5OrderID_UNIQUE`(`MD5OrderID` ASC) USING BTREE,
  INDEX `PayChannelID_IDX`(`PayChannelID` ASC) USING BTREE,
  INDEX `idx_CreateTime_MemberAccount`(`CreateTime` ASC, `MemberAccount` ASC) USING BTREE,
  INDEX `idx_ChangeTime_Supplier_MemberAccount`(`OrderPaidStatusChangeTime` ASC, `SupplierBranchID` ASC, `MemberAccount` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '三方充值订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_thirddepositorder_history
-- ----------------------------
DROP TABLE IF EXISTS `t_thirddepositorder_history`;
CREATE TABLE `t_thirddepositorder_history`  (
  `OrderIndex` bigint NOT NULL COMMENT '订单索引',
  `OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `SupplierBranchID` int NULL DEFAULT NULL COMMENT '财务分部ID',
  `ChannelOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '三方平台返回的订单号',
  `MerchantID` int NULL DEFAULT NULL COMMENT '商户编号',
  `MerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户订单号最长64位',
  `MerchantOrderTime` datetime NULL DEFAULT NULL COMMENT '商户提交订单时间',
  `PayType` int NULL DEFAULT NULL COMMENT '充值方式,1:网银转账2:快捷支付3:银联支付4:微信5:支付宝6:QQ钱包7:京东支付8:云闪付',
  `ClientType` int NULL DEFAULT NULL COMMENT '客户端类型, 1：PC，2:WAP，4:APP',
  `PayAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '充值金额',
  `PaidAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '实际到账金额',
  `PaidChannelFee` decimal(10, 2) NULL DEFAULT NULL COMMENT '支付给三方渠道的手续费',
  `SupplierPaidChannelFee` decimal(10, 2) NULL DEFAULT NULL COMMENT '商户需付手续费',
  `AgencyCommission` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理佣金',
  `NotifyUrl` varchar(516) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户用于接收订单通知的地址',
  `ReturnUrl` varchar(516) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单提交后立即返回的商户页面地址',
  `PayChannelID` int NULL DEFAULT NULL COMMENT '处理该订单的充值渠道',
  `MD5OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'MD5签名',
  `BankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '考虑到风云这些第三方卡系统',
  `OrderStatus` int NULL DEFAULT 0 COMMENT '态0:创建，默认值;1：处理中;2:清算中;3:正常支付关闭;4：手工确认订单已完成关闭; 5:超时,6:超时关闭；7:取消关闭',
  `OrderPaidStatusChangeTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `OrderNotifyStatus` int NULL DEFAULT 0 COMMENT '通知状态；0:未成功通知；1：已成功通知;2:通知超时异常',
  `OrderNotifyTime` datetime NULL DEFAULT NULL COMMENT '订单通知状态最后修改时间',
  `Remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `Name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '付款人姓名,\n有姓名时附言和姓名同时匹配，若匹配失败则先用附言匹配，再匹配失败则用姓名匹配',
  `ReadMark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用于标注是否被自动付款取走',
  `CloseReason` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '关闭原因',
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `UserID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户会员ID',
  `UserIP` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户会员IP',
  `PrePaidAmount` decimal(10, 2) NULL DEFAULT NULL,
  `OrderType` int NULL DEFAULT 0 COMMENT '商户订单',
  `UserLevel` int NULL DEFAULT NULL COMMENT '商户会员等级',
  `Operator` int NULL DEFAULT 0 COMMENT '操作员',
  `MemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '会员账号',
  `BankCode` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '银行编码',
  `UserCreditLevel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户层级，根据用户层级等级分配不同支付渠道;多个层级，使用‘,’分割，比如支持5_1,5_2层级，则是5_1,5_2',
  `EncryptBankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '考虑到风云这些第三方卡系统',
  `SourceType` tinyint NOT NULL DEFAULT 0 COMMENT '提单方式',
  `EncryptName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '付款人姓名,\n有姓名时附言和姓名同时匹配，若匹配失败则先用附言匹配，再匹配失败则用姓名匹配',
  `EncryptMemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '会员账号',
  `Protocol` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'usdt的协议',
  `EncryptBankOwner` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '收款卡姓名',
  `EncryptBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '收款卡号',
  `EncryptBankAddress` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '开户地址',
  `EncryptDepositName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '真实存款人名字',
  `MatchMerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '匹配提款订单商户订单号，极速订单使用',
  `MatchPlatformOrderId` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '匹配提款订单平台订单号，极速订单使用',
  `Currency` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '渠道币种 默认为空 人民币 数值参考bank_type',
  `RequestSource` int NOT NULL DEFAULT 0 COMMENT '操作来源（极速支付使用）',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `OrderExchangeRate` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '汇率',
  `IsFastPay1` int NULL DEFAULT NULL COMMENT '是否极速支付（0 默认不是，1 是）',
  `IsFastPay` int NULL DEFAULT 0 COMMENT '是否极速支付（0 默认不是，1 是）',
  `DepositConfirmTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '充值确认时间',
  `ImageAddr` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '图片地址 英文逗号隔开',
  `VirtualPayAmount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币金额',
  `VirtualPaidAmount` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币实际金额',
  `VirtualBasicRate` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币基础汇率',
  `VirtualRate` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币浮动汇率',
  `FromAddress` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '虚拟币会员存款地址',
  `certified_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '认证姓名',
  PRIMARY KEY (`OrderIndex`) USING BTREE,
  UNIQUE INDEX `uniq_OrderID`(`OrderID` ASC) USING BTREE,
  UNIQUE INDEX `ReadMark_UNIQUE`(`ReadMark` ASC) USING BTREE,
  UNIQUE INDEX `MerchantOrderStatus_Index`(`MerchantOrderID` ASC, `OrderStatus` ASC) USING BTREE,
  UNIQUE INDEX `MD5OrderID_UNIQUE`(`MD5OrderID` ASC) USING BTREE,
  INDEX `PayChannelID_IDX`(`PayChannelID` ASC) USING BTREE,
  INDEX `idx_CreateTime_MemberAccount`(`CreateTime` ASC, `MemberAccount` ASC) USING BTREE,
  INDEX `idx_ChangeTime_Supplier_MemberAccount`(`OrderPaidStatusChangeTime` ASC, `SupplierBranchID` ASC, `MemberAccount` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '三方充值订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_thirdmerchant
-- ----------------------------
DROP TABLE IF EXISTS `t_thirdmerchant`;
CREATE TABLE `t_thirdmerchant`  (
  `MerchantID` int NOT NULL COMMENT '三方商户ID',
  `SupplierBranchID` int NULL DEFAULT NULL COMMENT '财务分部ID',
  `MerchantName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '三方名称',
  `MerchantCode` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '三方代码',
  `WithdrawBankPoolID` int NULL DEFAULT NULL COMMENT '下发银行卡池',
  `DepositBankPoolID` int NULL DEFAULT NULL COMMENT '充值银行卡池',
  `DepositUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '充值地址',
  `WithdrawUrl` varchar(516) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '下发地址',
  `DepositStatus` int NULL DEFAULT NULL COMMENT '1：自动充值，0:手工充值，-1：系统外充值',
  `WithdrawStatus` int NULL DEFAULT NULL COMMENT '1：自动下发，0:手工下发，-1：系统外下发',
  `Status` int NULL DEFAULT NULL COMMENT '1：启用，0:停用，-1:软删除',
  `ReMark` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `CreateTime` datetime NULL DEFAULT NULL,
  `LastUpDateTime` datetime NULL DEFAULT NULL,
  `callback_domain_ids` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '回调域名ids',
  `withdraw_query_order_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '提款查单URL',
  `deposit_query_order_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '充值查单地址',
  `balance_query_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '余额查询地址',
  `batch_query_url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '批量查单地址',
  `account_reconciliation_switch` int NOT NULL DEFAULT 0 COMMENT '对账开关 0：关 1：开',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`MerchantID`) USING BTREE,
  INDEX `SupplierBranchID_Index`(`SupplierBranchID` ASC) USING BTREE,
  INDEX `idx_MerchantName`(`MerchantName` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '三方商户' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_thirdmerchant_monitor_alarm
-- ----------------------------
DROP TABLE IF EXISTS `t_thirdmerchant_monitor_alarm`;
CREATE TABLE `t_thirdmerchant_monitor_alarm`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `monitor_id` bigint NOT NULL DEFAULT 0 COMMENT '监控源ID',
  `third_merchant_id` int NOT NULL DEFAULT 0 COMMENT '第三方商户ID',
  `pay_channel_merchant_no` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '支付渠道商户号',
  `pay_channel_status_switch` int NOT NULL DEFAULT 1 COMMENT '渠道提高至开关: 1:关闭 2:开启',
  `limit_count_alarm` int NULL DEFAULT 0 COMMENT '告警次数限制',
  `threshol_offset_start_time` int NULL DEFAULT 0 COMMENT '监控阈值-初期偏移时间(秒)',
  `threshol_offset_end_time` int NULL DEFAULT 0 COMMENT '监控阈值-末期偏移时间(秒)',
  `threshol_offset_limit_money` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '监控阈值-金额限制(元)',
  `merch_channe_balance` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '商户渠道余额',
  `ty_channe_balance` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '天下渠道余额',
  `ty_channe_init_balance` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '天下渠道初始化余额',
  `monitor_money_result` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '监控结果集',
  `total_money_deposit` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '收款总额',
  `total_money_withdraw` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '出款总额',
  `total_money_cash` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '下发总额',
  `total_order_count_deposit` int NULL DEFAULT 0 COMMENT '收款订单总数',
  `total_order_count_withdraw` int NULL DEFAULT 0 COMMENT '出款订单总数',
  `total_order_count_cash` int NULL DEFAULT 0 COMMENT '下发订单总数',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '告警内容',
  `remark` varchar(520) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  `start_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '监控开始时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` int NULL DEFAULT 0 COMMENT '修改人',
  `create_by` int NULL DEFAULT 0 COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '三方商户余额监控告警表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_thirdmerchant_monitor_alarm_detail
-- ----------------------------
DROP TABLE IF EXISTS `t_thirdmerchant_monitor_alarm_detail`;
CREATE TABLE `t_thirdmerchant_monitor_alarm_detail`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `alarm_id` bigint NOT NULL DEFAULT 0 COMMENT '告警源ID',
  `pay_channel_id` int NULL DEFAULT NULL COMMENT '支付渠道ID',
  `supplier_branch_id` int NOT NULL DEFAULT 0 COMMENT '财务分部ID',
  `pay_channel_merchant_no` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '支付渠道商户号',
  `total_money_deposit` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '收款总额',
  `total_money_withdraw` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '出款总额',
  `total_money_cash` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '下发总额',
  `total_order_count_deposit` int NULL DEFAULT 0 COMMENT '收款订单总数',
  `total_order_count_withdraw` int NULL DEFAULT 0 COMMENT '出款订单总数',
  `total_order_count_cash` int NULL DEFAULT 0 COMMENT '下发订单总数',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `remark` varchar(520) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  `start_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '监控开始时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` int NULL DEFAULT 0 COMMENT '修改人',
  `create_by` int NULL DEFAULT 0 COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '三方商户余额监控告警明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_thirdmerchant_monitor_detail
-- ----------------------------
DROP TABLE IF EXISTS `t_thirdmerchant_monitor_detail`;
CREATE TABLE `t_thirdmerchant_monitor_detail`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `monitor_id` bigint NOT NULL DEFAULT 0 COMMENT '监控源ID',
  `pay_channel_id` int NULL DEFAULT NULL COMMENT '支付渠道ID',
  `supplier_branch_id` int NOT NULL DEFAULT 0 COMMENT '财务分部ID',
  `pay_channel_merchant_no` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '支付渠道商户号',
  `total_money_deposit` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '收款总额',
  `total_money_withdraw` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '出款总额',
  `total_money_cash` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '下发总额',
  `total_order_count_deposit` int NULL DEFAULT 0 COMMENT '收款订单总数',
  `total_order_count_withdraw` int NULL DEFAULT 0 COMMENT '出款订单总数',
  `total_order_count_cash` int NULL DEFAULT 0 COMMENT '下发订单总数',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `remark` varchar(520) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  `start_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '监控开始时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` int NULL DEFAULT 0 COMMENT '修改人',
  `create_by` int NULL DEFAULT 0 COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '三方商户余额监控明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_thirdmerchant_monitor_info
-- ----------------------------
DROP TABLE IF EXISTS `t_thirdmerchant_monitor_info`;
CREATE TABLE `t_thirdmerchant_monitor_info`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键自增',
  `third_merchant_id` int NOT NULL DEFAULT 0 COMMENT '第三方商户ID',
  `pay_channel_merchant_no` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '支付渠道商户号',
  `pay_channel_status_switch` int NOT NULL DEFAULT 1 COMMENT '渠道提高至开关: 1:关闭 2:开启',
  `monitor_status` int NOT NULL DEFAULT 1 COMMENT '监控状态: 1:停用 2:启用 -1:删除',
  `monitor_sort` int NULL DEFAULT 1 COMMENT '排序',
  `limit_count_alarm` int NULL DEFAULT 0 COMMENT '告警次数限制',
  `threshol_offset_start_time` int NULL DEFAULT 0 COMMENT '监控阈值-初期偏移时间(秒)',
  `threshol_offset_end_time` int NULL DEFAULT 0 COMMENT '监控阈值-末期偏移时间(秒)',
  `threshol_offset_limit_money` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '监控阈值-金额限制(元)',
  `merch_channe_balance` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '商户渠道余额',
  `ty_channe_balance` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '天下渠道余额',
  `ty_channe_init_balance` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '天下渠道初始化余额',
  `monitor_money_result` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '监控结果集',
  `balance_diff` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '余额差额',
  `total_money_deposit` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '收款总额',
  `total_money_withdraw` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '出款总额',
  `total_money_cash` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '下发总额',
  `remark` varchar(520) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  `start_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '监控开始时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` int NULL DEFAULT 0 COMMENT '修改人',
  `create_by` int NULL DEFAULT 0 COMMENT '创建人',
  `offline_recharge_amount` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '线下充值金额',
  `offline_issued_amount` decimal(11, 2) NULL DEFAULT 0.00 COMMENT '线下下发金额',
  `total_order_count_deposit` int NULL DEFAULT 0 COMMENT '收款订单数',
  `total_order_count_withdraw` int NULL DEFAULT 0 COMMENT '出款订单数',
  `total_order_count_cash` int NULL DEFAULT 0 COMMENT '下发订单数',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_pay_channel_merchant_no`(`pay_channel_merchant_no` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '三方商户余额监控信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_thirdorderstatus_log
-- ----------------------------
DROP TABLE IF EXISTS `t_thirdorderstatus_log`;
CREATE TABLE `t_thirdorderstatus_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT ' 平台订单号商户简称（5位字符，不足位0补齐）+ yyyyMMddHHmmssfff（时间17位）+1位字符+库编号(2位数数字，先固定为00）',
  `operator` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '操作人账户名称',
  `resource` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '来源',
  `order_status` int NOT NULL DEFAULT 0 COMMENT '订单状态',
  `order_type` tinyint NOT NULL DEFAULT 1 COMMENT '1提款2充值',
  `operator_type` int NOT NULL DEFAULT 0 COMMENT '操作类型',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_type`(`order_id` ASC, `order_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '极速充提订单日志' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_thirdwithdraworder_bak
-- ----------------------------
DROP TABLE IF EXISTS `t_thirdwithdraworder_bak`;
CREATE TABLE `t_thirdwithdraworder_bak`  (
  `OrderIndex` bigint NOT NULL COMMENT '订单索引',
  `OrderID` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '平台订单号商户简称（5位字符，不足位0补齐）+ yyyyMMddHHmmssfff（时间17位）+1位字符+库编号(2位数数字，先固定为00）',
  `SupplierBranchID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '财务分部ID',
  `ParentID` bigint NULL DEFAULT NULL,
  `ChannelOrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '三方平台返回的订单号',
  `MerchantID` int NULL DEFAULT NULL COMMENT '商户编号',
  `MerchantOrderID` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户订单号',
  `UserLevel` int NULL DEFAULT 0,
  `PayType` int NULL DEFAULT NULL COMMENT '付款方式,1：银行卡，2：第三方，3：其他方式',
  `PayAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '提款金额',
  `PaidAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '实际到账金额',
  `PaidChannelFee` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '支付给三方渠道的手续费',
  `SupplierPaidChannelFee` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '商户需付手续费',
  `AgencyCommission` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理佣金',
  `NotifyUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户用于接收订单通知的地址',
  `ReturnUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单提交后立即返回的商户页面地址',
  `MerchantRemark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户附言, 当支付方式是0网银转账时，可由商户发送附言\n否则系统提供附言。\n',
  `PayChannelID` int NULL DEFAULT NULL COMMENT '处理该订单的充值渠道',
  `OrderPaidStatusChangeTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `OrderNotifyStatus` int NULL DEFAULT 0 COMMENT '通知状态；0:未成功通知；1：已成功通知;2:通知超时异常',
  `OrderNotifyTime` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '订单通知状态最后修改时间',
  `Remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `BankCode` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '取款银行类型',
  `BankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `BankAddress` varchar(270) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `BankOwner` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `ReadMark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `MD5OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `OrderStatus` int NULL DEFAULT 0 COMMENT '订单状态0:创建，默认值;1：处理中;2:清算中;3:正常支付关闭;4：手工确认订单已完成关闭; 5:超时,6:超时关闭；7:取消关闭',
  `Digest` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `CloseReason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '关闭原因',
  `OrderType` int NULL DEFAULT NULL,
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `BankAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `UserId` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员ID',
  `UserIP` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员IP',
  `Operator` int NULL DEFAULT 0 COMMENT '操作员',
  `MemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员账号',
  `UserCreditLevel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户层级，根据用户层级等级分配不同支付渠道;多个层级，使用‘,’分割，比如支持5_1,5_2层级，则是5_1,5_2',
  `EncryptBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡号',
  `EncryptBankAddress` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开户地址',
  `EncryptBankOwner` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '提款人',
  `EncryptBankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡简码',
  `EncryptMemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员账号',
  `Protocol` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'usdt的协议',
  `transferOrderTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '首次创建时间',
  `MatchMerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '匹配的充值订单商户订单号，极速订单使用',
  `FastToNormalStatus` int NOT NULL DEFAULT 0 COMMENT '极速提款订单转普通订单状态 0 默认，1: 转普通订单成功；2：已成功通知',
  `MatchPlatformOrderId` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '匹配充值订单平台订单号，极速订单使用',
  `Currency` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '渠道币种 默认为空 人民币 数值参考bank_type',
  `RequestSource` int NOT NULL DEFAULT 0 COMMENT '操作来源（极速支付使用）',
  `Rate` decimal(10, 4) NOT NULL DEFAULT 1.0000 COMMENT '商户汇率',
  `AmountType` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'CNY' COMMENT '金额类型 默认CNY 具体数值参考币种',
  `HashTransID` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'USDT虚拟币交易id 用于查询交易金额',
  `VirtualPayAmount` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币金额',
  `VirtualPaidAmount` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币金额',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `IsFastPay` int NOT NULL DEFAULT 0 COMMENT '默认0，0：普通订单，1：极速订单，10：极速父订单，11:极速子订单',
  `MatchTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '匹配时间',
  `ParentOrderId` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '极速父订单ID',
  `DepositConfirmTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '充值确认时间',
  `DepositName` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '存款姓名（极速）',
  `deduction` tinyint NOT NULL DEFAULT 0 COMMENT '扣费标识，0未扣费，1已扣费',
  `bill_sign` tinyint NOT NULL DEFAULT 0 COMMENT '结算标识，0未结算，1已结算',
  `process_pay_type` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '最初处理该订单的支付方式',
  `PriorityMatchStatus` int NULL DEFAULT 0 COMMENT '优先匹配状态 0未匹配 1已匹配',
  `ParentOrderStatus` tinyint NOT NULL DEFAULT 1 COMMENT '急速父订单状态 1 处理中 3 成功 7失败',
  `OrderJustifyTime` datetime NULL DEFAULT NULL COMMENT '订单抓取时间',
  `withdrawType` int NOT NULL DEFAULT 0 COMMENT '取款类型',
  `ImageAddr` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '图片地址 英文逗号隔开',
  `MostPriorityMatchStatus` int NOT NULL DEFAULT 0 COMMENT '最优匹配状态 0否 1是',
  `betType` int NOT NULL DEFAULT 0 COMMENT '0 不投注 1 投注',
  `certified_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '认证姓名',
  `member_real_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '会员真实姓名',
  PRIMARY KEY (`OrderIndex`) USING BTREE,
  UNIQUE INDEX `ChannelOrderID_UNIQUE`(`ChannelOrderID` ASC) USING BTREE,
  UNIQUE INDEX `ReadMark_UNIQUE`(`ReadMark` ASC) USING BTREE,
  INDEX `idx_fast`(`FastToNormalStatus` ASC, `PayType` ASC) USING BTREE,
  INDEX `idx_MD5OrderID`(`MD5OrderID` ASC) USING BTREE,
  INDEX `idx_memaccount_time`(`EncryptMemberAccount` ASC, `OrderPaidStatusChangeTime` ASC) USING BTREE,
  INDEX `idx_merchantid_status_time`(`MerchantID` ASC, `OrderStatus` ASC, `FastToNormalStatus` ASC, `OrderPaidStatusChangeTime` ASC) USING BTREE,
  INDEX `idx_merchantid_time`(`MerchantID` ASC, `OrderStatus` ASC, `OrderPaidStatusChangeTime` ASC) USING BTREE,
  INDEX `idx_OrderID_IDX`(`OrderID` ASC) USING BTREE,
  INDEX `idx_ParentOrderID_isFastPay`(`ParentOrderId` ASC, `IsFastPay` ASC) USING BTREE,
  INDEX `idx_tmp2`(`FastToNormalStatus` ASC) USING BTREE,
  INDEX `idx_tmp3`(`PayType` ASC) USING BTREE,
  INDEX `MerchantID`(`MerchantID` ASC, `OrderStatus` ASC, `FastToNormalStatus` ASC, `PayType` ASC, `OrderPaidStatusChangeTime` ASC) USING BTREE,
  INDEX `OrderPaidStatusChangeTime_index`(`OrderPaidStatusChangeTime` ASC) USING BTREE,
  INDEX `OrderType_Index`(`OrderType` ASC) USING BTREE,
  INDEX `idx_MerchantOrderID_Index`(`MerchantOrderID` ASC) USING BTREE,
  INDEX `Digest_Index`(`Digest` ASC) USING BTREE,
  INDEX `CreateTime_Index`(`CreateTime` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '三方渠道提款订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_thirdwithdraworder_history
-- ----------------------------
DROP TABLE IF EXISTS `t_thirdwithdraworder_history`;
CREATE TABLE `t_thirdwithdraworder_history`  (
  `OrderIndex` bigint NOT NULL COMMENT '订单索引',
  `OrderID` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '平台订单号商户简称（5位字符，不足位0补齐）+ yyyyMMddHHmmssfff（时间17位）+1位字符+库编号(2位数数字，先固定为00）',
  `SupplierBranchID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '财务分部ID',
  `ParentID` bigint NULL DEFAULT NULL,
  `ChannelOrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '三方平台返回的订单号',
  `MerchantID` int NULL DEFAULT NULL COMMENT '商户编号',
  `MerchantOrderID` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户订单号',
  `UserLevel` int NULL DEFAULT 0,
  `PayType` int NULL DEFAULT NULL COMMENT '付款方式,1：银行卡，2：第三方，3：其他方式',
  `PayAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '提款金额',
  `PaidAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '实际到账金额',
  `PaidChannelFee` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '支付给三方渠道的手续费',
  `SupplierPaidChannelFee` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '商户需付手续费',
  `AgencyCommission` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理佣金',
  `NotifyUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户用于接收订单通知的地址',
  `ReturnUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单提交后立即返回的商户页面地址',
  `MerchantRemark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户附言, 当支付方式是0网银转账时，可由商户发送附言\n否则系统提供附言。\n',
  `PayChannelID` int NULL DEFAULT NULL COMMENT '处理该订单的充值渠道',
  `OrderPaidStatusChangeTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `OrderNotifyStatus` int NULL DEFAULT 0 COMMENT '通知状态；0:未成功通知；1：已成功通知;2:通知超时异常',
  `OrderNotifyTime` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '订单通知状态最后修改时间',
  `Remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `BankCode` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '取款银行类型',
  `BankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `BankAddress` varchar(270) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `BankOwner` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `ReadMark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `MD5OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `OrderStatus` int NULL DEFAULT 0 COMMENT '订单状态0:创建，默认值;1：处理中;2:清算中;3:正常支付关闭;4：手工确认订单已完成关闭; 5:超时,6:超时关闭；7:取消关闭',
  `Digest` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `CloseReason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '关闭原因',
  `OrderType` int NULL DEFAULT NULL,
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `BankAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `UserId` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员ID',
  `UserIP` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员IP',
  `Operator` int NULL DEFAULT 0 COMMENT '操作员',
  `MemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员账号',
  `UserCreditLevel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户层级，根据用户层级等级分配不同支付渠道;多个层级，使用‘,’分割，比如支持5_1,5_2层级，则是5_1,5_2',
  `EncryptBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡号',
  `EncryptBankAddress` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开户地址',
  `EncryptBankOwner` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '提款人',
  `EncryptBankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡简码',
  `EncryptMemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员账号',
  `Protocol` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'usdt的协议',
  `transferOrderTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '首次创建时间',
  `MatchMerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '匹配的充值订单商户订单号，极速订单使用',
  `FastToNormalStatus` int NOT NULL DEFAULT 0 COMMENT '极速提款订单转普通订单状态 0 默认，1: 转普通订单成功；2：已成功通知',
  `MatchPlatformOrderId` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '匹配充值订单平台订单号，极速订单使用',
  `Currency` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '渠道币种 默认为空 人民币 数值参考bank_type',
  `RequestSource` int NOT NULL DEFAULT 0 COMMENT '操作来源（极速支付使用）',
  `Rate` decimal(10, 4) NOT NULL DEFAULT 1.0000 COMMENT '商户汇率',
  `AmountType` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'CNY' COMMENT '金额类型 默认CNY 具体数值参考币种',
  `HashTransID` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'USDT虚拟币交易id 用于查询交易金额',
  `VirtualPayAmount` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币金额',
  `VirtualPaidAmount` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币金额',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `IsFastPay` int NOT NULL DEFAULT 0 COMMENT '默认0，0：普通订单，1：极速订单，10：极速父订单，11:极速子订单',
  `MatchTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '匹配时间',
  `ParentOrderId` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '极速父订单ID',
  `DepositConfirmTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '充值确认时间',
  `DepositName` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '存款姓名（极速）',
  `deduction` tinyint NOT NULL DEFAULT 0 COMMENT '扣费标识，0未扣费，1已扣费',
  `bill_sign` tinyint NOT NULL DEFAULT 0 COMMENT '结算标识，0未结算，1已结算',
  `process_pay_type` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '最初处理该订单的支付方式',
  `PriorityMatchStatus` int NULL DEFAULT 0 COMMENT '优先匹配状态 0未匹配 1已匹配',
  `ParentOrderStatus` tinyint NOT NULL DEFAULT 1 COMMENT '急速父订单状态 1 处理中 3 成功 7失败',
  `OrderJustifyTime` datetime NULL DEFAULT NULL COMMENT '订单抓取时间',
  `withdrawType` int NOT NULL DEFAULT 0 COMMENT '取款类型',
  `ImageAddr` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '图片地址 英文逗号隔开',
  `player_level` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '会员等级',
  `player_device_number` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '会员设备号',
  `order_handle_type` int NOT NULL DEFAULT 0 COMMENT '钱包：订单处理类型 0 自动  1 手动',
  `MostPriorityMatchStatus` int NOT NULL DEFAULT 0 COMMENT '最优匹配状态 0否 1是',
  `betType` int NOT NULL DEFAULT 0 COMMENT '0 不投注 1 投注',
  `certified_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '认证姓名',
  `member_real_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '会员真实姓名',
  PRIMARY KEY (`OrderIndex`) USING BTREE,
  UNIQUE INDEX `ReadMark_UNIQUE`(`ReadMark` ASC) USING BTREE,
  UNIQUE INDEX `ChannelOrderID_UNIQUE`(`ChannelOrderID` ASC) USING BTREE,
  INDEX `idx_fast`(`FastToNormalStatus` ASC, `PayType` ASC) USING BTREE,
  INDEX `CreateTime_Index`(`CreateTime` ASC) USING BTREE,
  INDEX `Digest_Index`(`Digest` ASC) USING BTREE,
  INDEX `idx_MD5OrderID`(`MD5OrderID` ASC) USING BTREE,
  INDEX `idx_memaccount_time`(`EncryptMemberAccount` ASC, `OrderPaidStatusChangeTime` ASC) USING BTREE,
  INDEX `idx_merchantid_status_time`(`MerchantID` ASC, `OrderStatus` ASC, `FastToNormalStatus` ASC, `OrderPaidStatusChangeTime` ASC) USING BTREE,
  INDEX `idx_merchantid_time`(`MerchantID` ASC, `OrderStatus` ASC, `OrderPaidStatusChangeTime` ASC) USING BTREE,
  INDEX `idx_MerchantOrderID_Index`(`MerchantOrderID` ASC) USING BTREE,
  INDEX `idx_OrderID_IDX`(`OrderID` ASC) USING BTREE,
  INDEX `idx_ParentOrderID_isFastPay`(`ParentOrderId` ASC, `IsFastPay` ASC) USING BTREE,
  INDEX `idx_tmp2`(`FastToNormalStatus` ASC) USING BTREE,
  INDEX `idx_tmp3`(`PayType` ASC) USING BTREE,
  INDEX `MerchantID`(`MerchantID` ASC, `OrderStatus` ASC, `FastToNormalStatus` ASC, `PayType` ASC, `OrderPaidStatusChangeTime` ASC) USING BTREE,
  INDEX `OrderPaidStatusChangeTime_index`(`OrderPaidStatusChangeTime` ASC) USING BTREE,
  INDEX `OrderType_Index`(`OrderType` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '三方渠道提款订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_thirdwithdraworder_match_status
-- ----------------------------
DROP TABLE IF EXISTS `t_thirdwithdraworder_match_status`;
CREATE TABLE `t_thirdwithdraworder_match_status`  (
  `OrderID` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '平台订单号商户简称（5位字符，不足位0补齐）+ yyyyMMddHHmmssfff（时间17位）+1位字符+库编号(2位数数字，先固定为00）',
  `MostPriorityMatchStatus` int NOT NULL DEFAULT 0 COMMENT '最优匹配状态 0否 1是',
  `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '修改者',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`OrderID`) USING BTREE,
  INDEX `idx_updated_MostPriorityMatchStatus_Index`(`updated_at` ASC, `MostPriorityMatchStatus` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '极速提款最优匹配状态表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_trans_phone_msg
-- ----------------------------
DROP TABLE IF EXISTS `t_trans_phone_msg`;
CREATE TABLE `t_trans_phone_msg`  (
  `id` bigint NOT NULL COMMENT 'id',
  `supplier_branch_id` int NOT NULL DEFAULT 0 COMMENT '财务分部ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户名',
  `trans_type` tinyint NOT NULL DEFAULT 0 COMMENT '交易类型  0存款 1取款',
  `trans_amount` decimal(14, 3) NOT NULL DEFAULT 0.000 COMMENT '交易金额',
  `phone_num` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '手机号码',
  `msgtxt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '短信内容',
  `send_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `insert_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `update_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `md5_sign` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'md5后的唯一编码',
  `version` int NOT NULL DEFAULT 0 COMMENT '版本号',
  `order_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '平台订单号',
  `mch_order_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户订单号',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '处理状态，0：创建，1：处理中，2：已成功处理，3：异常',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `encryptDepositName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `encryptBankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '银行卡简称',
  `balance` decimal(16, 2) NOT NULL DEFAULT 0.00 COMMENT '余额',
  `encryptAppBankNum` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'App上报银行卡号',
  `manual_remark` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否人工备注 0否 1是',
  `operator` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '操作人',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_md5_sign`(`md5_sign` ASC) USING BTREE,
  INDEX `idx_phone_num`(`phone_num` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '短信交易表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_uphone
-- ----------------------------
DROP TABLE IF EXISTS `t_uphone`;
CREATE TABLE `t_uphone`  (
  `ID` int NOT NULL,
  `SupplierBranchID` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '分部ID',
  `PhoneID` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机编号',
  `PhoneNumber` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `OperatorType` int NULL DEFAULT NULL COMMENT '运营商 0:中国移动,1:中国联通,2:中国电信',
  `PhoneFee` decimal(10, 2) NULL DEFAULT NULL COMMENT '资费',
  `Balance` decimal(10, 2) NULL DEFAULT NULL COMMENT '余额',
  `Signal` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '信号',
  `ServicePWD` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服务密码',
  `Name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
  `Identity` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证号码',
  `Gender` int NULL DEFAULT NULL COMMENT '性别 0:男,1:女',
  `CardReader` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '测卡人',
  `Administrator` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '管理人',
  `Archive` int NULL DEFAULT NULL COMMENT '是否归档 0:无,1:是,2:否',
  `ReaderTime` datetime NULL DEFAULT NULL COMMENT '测卡日期',
  `StautsType` int NULL DEFAULT NULL COMMENT '状态 0:正常,1:异常,2:已提走,3:补卡换卡,4:外借,5:停用,6:删除',
  `Remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `CreateTime` datetime NULL DEFAULT NULL,
  `LastUpDateTime` datetime NULL DEFAULT NULL,
  `PhoneType` int NULL DEFAULT NULL COMMENT '手机类型: 1个人  2企业',
  `GuidangTime` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '归档日期',
  `TizouTime` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '提走日期',
  `WaijieTime` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '外借日期',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE INDEX `PhoneNumber_UNIQUE`(`PhoneNumber` ASC) USING BTREE,
  INDEX `PhoneType_key`(`PhoneType` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '手机管理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_usbkey
-- ----------------------------
DROP TABLE IF EXISTS `t_usbkey`;
CREATE TABLE `t_usbkey`  (
  `ID` int NOT NULL COMMENT 'ID',
  `BankID` int NOT NULL COMMENT '银行卡ID',
  `Userial` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'U盾序列号',
  `BankType` int NOT NULL DEFAULT 0 COMMENT '帐户类型 0:个人,1:企业',
  `Remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `Status` tinyint NOT NULL DEFAULT 1 COMMENT '状态',
  `LastUpDateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `BankID_index`(`BankID` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'USB管理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_credit_amount
-- ----------------------------
DROP TABLE IF EXISTS `t_user_credit_amount`;
CREATE TABLE `t_user_credit_amount`  (
  `id` bigint NOT NULL COMMENT '主键id',
  `user_id` bigint NOT NULL DEFAULT 0 COMMENT '用户ID',
  `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户账号',
  `role_id` bigint NOT NULL DEFAULT 0 COMMENT '角色ID',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态',
  `credit_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '总额度',
  `remaining_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '剩余额度',
  `lock_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '冻结额度',
  `remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '操作人',
  `node_id` tinyint NOT NULL DEFAULT 1 COMMENT '数据来源1,2,3 天下节点 ',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户额度信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_credit_amount_log
-- ----------------------------
DROP TABLE IF EXISTS `t_user_credit_amount_log`;
CREATE TABLE `t_user_credit_amount_log`  (
  `id` bigint NOT NULL COMMENT '主键id',
  `user_id` bigint NOT NULL DEFAULT 0 COMMENT '用户ID',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态 0-申请中, 1-申请通过, 2-申请拒绝',
  `add_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '申请增加的额度',
  `remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `review_remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '审核备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `review_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '操作人',
  `node_id` tinyint NOT NULL DEFAULT 1 COMMENT '数据来源1,2,3 天下节点 ',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户额度申请记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_credit_merchant_verify
-- ----------------------------
DROP TABLE IF EXISTS `t_user_credit_merchant_verify`;
CREATE TABLE `t_user_credit_merchant_verify`  (
  `id` bigint NOT NULL COMMENT '主键id',
  `order_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '额度使用单号',
  `merchant_order_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户订单号',
  `user_id` bigint NOT NULL DEFAULT 0 COMMENT '用户ID',
  `merchant_id` int NOT NULL DEFAULT 0 COMMENT '商户ID',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态 0-申请中, 1-申请通过, 2-申请拒绝',
  `adjust_credit` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '用户调整额度',
  `adjust_balance` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商户调整总余额',
  `adjust_available_balance` decimal(10, 2) NOT NULL COMMENT '商户调整可用余额',
  `adjust_fronzen_balance` decimal(10, 2) NOT NULL COMMENT '商户调账冻结余额',
  `adjust_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '申请人',
  `remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `review_remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '审批备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `review_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '审核人',
  `node_id` tinyint NOT NULL DEFAULT 1 COMMENT '数据来源1,2,3 天下节点 ',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商户余额变动审核表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_wallet
-- ----------------------------
DROP TABLE IF EXISTS `t_wallet`;
CREATE TABLE `t_wallet`  (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '钱包地址ID',
  `SupplierBranchID` int NOT NULL DEFAULT 0 COMMENT '分支ID',
  `CurrencyType` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '币种',
  `Protocol` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '支付协议',
  `Status` int NOT NULL DEFAULT 1 COMMENT '服务状态  0启用 1 禁用',
  `WalletType` int NOT NULL DEFAULT 0 COMMENT '钱包类型',
  `WalletAddress` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '钱包地址',
  `WalletAddressCode` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '钱包地址简码',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `UpdateBy` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '更新人',
  `CreateBy` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '创建人',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '虚拟币钱包' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_withdraw_order
-- ----------------------------
DROP TABLE IF EXISTS `t_withdraw_order`;
CREATE TABLE `t_withdraw_order`  (
  `OrderIndex` bigint NOT NULL COMMENT '订单索引',
  `OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `OrderNotifyTime` datetime NULL DEFAULT NULL,
  `SupplierBranchID` int NULL DEFAULT NULL COMMENT '财务分部ID',
  `ParentID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '父订单ID',
  `MerchantID` int NULL DEFAULT NULL COMMENT '商户编号',
  `MerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `UserLevel` int NULL DEFAULT -1,
  `PayType` int NULL DEFAULT NULL COMMENT '付款方式,1：银行卡，2：第三方，3：其他方式',
  `PayAmount` decimal(10, 2) NULL DEFAULT 0.00,
  `PaidAmount` decimal(10, 2) NULL DEFAULT 0.00,
  `PaidChannelFee` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '商户支付手续费',
  `SupplierPaidChannelFee` decimal(10, 2) NULL DEFAULT NULL COMMENT '渠道提供方手续费，比如需向三方通道付手续费，比如平均银行卡磨损费',
  `AgencyCommission` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理佣金',
  `NotifyUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `ReturnUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `MerchantRemark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户备注',
  `PayChannelID` int NULL DEFAULT NULL COMMENT '处理该订单的充值渠道',
  `BankAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `OrderStatus` int NULL DEFAULT 0,
  `OrderPaidStatusChangeTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单状态最后修改时间',
  `OrderNotifyStatus` int NULL DEFAULT 0,
  `Remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `BankCode` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `BankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `BankAddress` varchar(270) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `BankOwner` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `ReadMark` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用于标注是否被自动付款取走',
  `MD5OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `Digest` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `CloseReason` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `OrderType` int NULL DEFAULT 0,
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `UserId` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员ID',
  `UserIP` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员IP',
  `Operator` int NULL DEFAULT 0 COMMENT '操作员',
  `MemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员账号',
  `allotuserid` int NOT NULL DEFAULT 0 COMMENT '分配人员id',
  `allotstatus` int NOT NULL DEFAULT 0 COMMENT '0:未分配,1:已分配',
  `allottime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
  `UserCreditLevel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户层级，根据用户层级等级分配不同支付渠道;多个层级，使用‘,’分割，比如支持5_1,5_2层级，则是5_1,5_2',
  `OperationType` int NOT NULL DEFAULT 0 COMMENT '0:正常订单,1:直接手出',
  `HankType` int NOT NULL DEFAULT 0 COMMENT '手动出款类型 0正常出款 ',
  `PickTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单抓取时间',
  `EncryptBankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户银行卡简码',
  `EncryptBankNum` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡号码',
  `EncryptBankOwner` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '提款人',
  `EncryptMemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员账号',
  `transferOrderTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '首次创建时间',
  `AutoMataType` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '自动机客户端类型  0 PC 1 手机银行',
  `Protocol` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'usdt的协议',
  `EncryptBankAddress` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '开户地址',
  `Expedited` int NOT NULL DEFAULT 0 COMMENT '是否加急（0、未加急，1、加急 默认0）',
  `assignor` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '分配人',
  `AssignorStatus` int NOT NULL DEFAULT 0 COMMENT '分配状态 0未分配  1 分配处理中',
  `Currency` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '渠道币种 默认为0 人民币 数值参考bank_type',
  `Rate` decimal(10, 4) NOT NULL DEFAULT 1.0000 COMMENT '商户汇率',
  `AmountType` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'CNY' COMMENT '金额类型 默认CNY 具体数值参考币种',
  `VirtualPayAmount` decimal(11, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币金额',
  `VirtualPaidAmount` decimal(11, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币金额',
  `VirtualPayType` tinyint NOT NULL DEFAULT 0 COMMENT '虚拟币支付类型 1易币付',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `deduction` tinyint NOT NULL DEFAULT 0 COMMENT '扣费标识，0未扣费，1已扣费',
  `bill_sign` tinyint NOT NULL DEFAULT 0 COMMENT '结算标识，0未结算，1已结算',
  `process_pay_type` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '最初处理该订单的支付方式',
  `LockStatus` int NULL DEFAULT 0 COMMENT '订单锁定状态 0未锁定 1已锁定',
  `OrderJustifyTime` datetime NULL DEFAULT NULL COMMENT '订单冲正时间',
  `withdrawType` int NOT NULL DEFAULT 0 COMMENT '取款类型',
  `betType` int NOT NULL DEFAULT 0 COMMENT '0 不投注 1 投注',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据入库时间',
  `withdrawSuccessNum` int NOT NULL DEFAULT 0 COMMENT '提款成功次数',
  `split_order` int NOT NULL DEFAULT 0 COMMENT '是否转三方 1不转，中台不拆单（会员账号高亮标记） 0 可转，中台拆单（会员账号不标记）',
  `apiRemark` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '商户API提现备注',
  `appSite` tinyint NOT NULL DEFAULT 0 COMMENT '会员归属类型:0空;1内部为KY,BOB,BW;2外部',
  `is_face_order` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否为刷脸订单 0否 1是',
  `FromCashier` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否来自卡商 0否 1是',
  `CashierId` bigint NOT NULL DEFAULT 0 COMMENT '卡商ID',
  `settlementSite` int NULL DEFAULT NULL COMMENT '结算站点',
  `is_dc` int NOT NULL DEFAULT 0 COMMENT '是否dc订单 0否 1是',
  `ImageAddr` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片地址:英文逗号隔开',
  `SplitSubsidyAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '拆单补贴金额',
  `merchant_total_rebate` decimal(14, 4) NULL DEFAULT 0.0000 COMMENT '商户返利总金额=百分比返利金额*成交金额+固定金额返利',
  `EncryptBankMobile` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '提款人银行预留手机号',
  `EncryptIdNum` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '提款人身份证号',
  PRIMARY KEY (`OrderIndex`) USING BTREE,
  UNIQUE INDEX `ReadMark_UNIQUE`(`ReadMark` ASC) USING BTREE,
  UNIQUE INDEX `OrderID_UNIQUE`(`OrderID` ASC) USING BTREE,
  INDEX `SupplierBranchID_Index`(`SupplierBranchID` ASC) USING BTREE,
  INDEX `idx_OrderPaidStatusChangeTime`(`OrderPaidStatusChangeTime` ASC) USING BTREE,
  INDEX `idx_MerchantOrderID_PayAmount`(`MerchantOrderID` ASC, `PayAmount` ASC) USING BTREE,
  INDEX `idx_BankAccount`(`BankAccount` ASC) USING BTREE,
  INDEX `Digest_Index`(`Digest` ASC) USING BTREE,
  INDEX `CreateTime_Index`(`CreateTime` ASC) USING BTREE,
  INDEX `create_ordernotify_ordertype`(`OrderType` ASC, `OrderNotifyStatus` ASC, `CreateTime` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '充值订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_withdraw_order_rate
-- ----------------------------
DROP TABLE IF EXISTS `t_withdraw_order_rate`;
CREATE TABLE `t_withdraw_order_rate`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `order_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单ID',
  `merchant_withdraw_rate` decimal(5, 4) NOT NULL DEFAULT 0.0000 COMMENT '商户出款费率',
  `merchant_withdraw_fixed_cost` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '商户提款固定费率',
  `agency_withdraw_rate` decimal(5, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理返佣出款费率',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `node_id` int NOT NULL DEFAULT 1 COMMENT '数据来源: 1 skg天下,2 柬天下,3 日本天下',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_order_id`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单服务费率' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_withdraworder_audit
-- ----------------------------
DROP TABLE IF EXISTS `t_withdraworder_audit`;
CREATE TABLE `t_withdraworder_audit`  (
  `OrderIndex` bigint NOT NULL COMMENT '订单索引',
  `OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '平台订单号商户简称（5位字符，不足位0补齐）+ yyyyMMddHHmmssfff（时间17位）+1位字符+库编号(2位数数字，先固定为00）',
  `SupplierBranchID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '财务分部ID',
  `ParentID` bigint NULL DEFAULT NULL COMMENT '父id',
  `ChannelOrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '三方平台返回的订单号',
  `MerchantID` int NULL DEFAULT NULL COMMENT '商户编号',
  `MerchantOrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户订单号最长64位',
  `UserLevel` int NULL DEFAULT 0 COMMENT '用户等级',
  `PayType` int NULL DEFAULT NULL COMMENT '付款方式,1：银行卡，2：第三方，3：其他方式',
  `PayAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '提款金额',
  `PaidAmount` decimal(10, 2) NULL DEFAULT NULL COMMENT '实际到账金额',
  `PaidChannelFee` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '支付给三方渠道的手续费',
  `SupplierPaidChannelFee` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '商户需付手续费',
  `AgencyCommission` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理佣金',
  `NotifyUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户用于接收订单通知的地址',
  `ReturnUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '订单提交后立即返回的商户页面地址',
  `MerchantRemark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户附言, 当支付方式是0网银转账时，可由商户发送附言\n否则系统提供附言。\n',
  `PayChannelID` int NULL DEFAULT NULL COMMENT '处理该订单的充值渠道',
  `OrderPaidStatusChangeTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单完成时间',
  `OrderNotifyStatus` int NULL DEFAULT 0 COMMENT '通知状态；0:未成功通知；1：已成功通知;2:通知超时异常',
  `OrderNotifyTime` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '订单通知状态最后修改时间',
  `Remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `BankCode` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '取款银行类型',
  `BankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡号',
  `BankAddress` varchar(270) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行地址',
  `BankOwner` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡户名',
  `ReadMark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标记',
  `MD5OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'md5id',
  `OrderStatus` int NULL DEFAULT 0 COMMENT '订单状态0:创建，默认值;1：处理中;2:清算中;3:正常支付关闭;4：手工确认订单已完成关闭; 5:超时,6:超时关闭；7:取消关闭',
  `Digest` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '我也不知道是什么字段',
  `CloseReason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '关闭原因',
  `OrderType` int NULL DEFAULT NULL COMMENT '订单类型',
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `BankAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行账户名',
  `UserId` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员ID',
  `UserIP` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员IP',
  `Operator` int NULL DEFAULT 0 COMMENT '操作员',
  `MemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员账号',
  `UserCreditLevel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户层级，根据用户层级等级分配不同支付渠道;多个层级，使用‘,’分割，比如支持5_1,5_2层级，则是5_1,5_2',
  `EncryptBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡号',
  `EncryptBankAddress` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开户地址',
  `EncryptBankOwner` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '提款人',
  `EncryptBankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡简码',
  `EncryptMemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员账号',
  `Protocol` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'usdt的协议',
  `transferOrderTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '首次创建时间',
  `MatchMerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '匹配的充值订单商户订单号，极速订单使用',
  `FastToNormalStatus` int NOT NULL DEFAULT 0 COMMENT '极速提款订单转普通订单状态 0 默认，1: 转普通订单成功；2：已成功通知',
  `MatchPlatformOrderId` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '匹配充值订单平台订单号，极速订单使用',
  `Currency` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '渠道币种 默认为空 人民币 数值参考bank_type',
  `RequestSource` int NOT NULL DEFAULT 0 COMMENT '操作来源（极速支付使用）',
  `Rate` decimal(10, 4) NOT NULL DEFAULT 1.0000 COMMENT '商户汇率',
  `AmountType` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'CNY' COMMENT '金额类型 默认CNY 具体数值参考币种',
  `HashTransID` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'USDT虚拟币交易id 用于查询交易金额',
  `VirtualPayAmount` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币金额',
  `VirtualPaidAmount` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币金额',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `IsFastPay` int NOT NULL DEFAULT 0 COMMENT '默认0，0：普通订单，1：极速订单，10：极速父订单，11:极速子订单',
  `MatchTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '匹配时间',
  `ParentOrderId` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '极速父订单ID',
  `DepositConfirmTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '充值确认时间',
  `DepositName` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '存款姓名（极速）',
  `deduction` tinyint NOT NULL DEFAULT 0 COMMENT '扣费标识，0未扣费，1已扣费',
  `bill_sign` tinyint NOT NULL DEFAULT 0 COMMENT '结算标识，0未结算，1已结算',
  `process_pay_type` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '最初处理该订单的支付方式',
  `PriorityMatchStatus` int NULL DEFAULT 0 COMMENT '优先匹配状态 0未匹配 1已匹配',
  `ParentOrderStatus` tinyint NOT NULL DEFAULT 1 COMMENT '急速父订单状态 1 处理中 3 成功 7失败',
  `OrderJustifyTime` datetime NULL DEFAULT NULL COMMENT '订单冲正时间',
  `withdrawType` int NOT NULL DEFAULT 0 COMMENT '取款类型',
  `RiskStatus` int NOT NULL DEFAULT 0 COMMENT '风控状态|1:黑名单拦截|2:规则拦截|3:待审核订单|4:人工审核通过|5:人工审核不通过',
  `RiskTime` datetime NULL DEFAULT NULL COMMENT '风控时间',
  `RiskMark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '风控标识',
  PRIMARY KEY (`OrderIndex`) USING BTREE,
  UNIQUE INDEX `uniq_ReadMark_UNIQUE`(`ReadMark` ASC) USING BTREE,
  UNIQUE INDEX `uniq_ChannelOrderID_UNIQUE`(`ChannelOrderID` ASC) USING BTREE,
  INDEX `idx_uniq_CreateTime_Index`(`CreateTime` ASC) USING BTREE,
  INDEX `idx_uniq_OrderType_Index`(`OrderType` ASC) USING BTREE,
  INDEX `idx_uniq_OrderPaidStatusChangeTime_index`(`OrderPaidStatusChangeTime` ASC) USING BTREE,
  INDEX `idx_uniq_Digest_Index`(`Digest` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '风控待审核提款订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_withdraworder_bak
-- ----------------------------
DROP TABLE IF EXISTS `t_withdraworder_bak`;
CREATE TABLE `t_withdraworder_bak`  (
  `OrderIndex` bigint NOT NULL COMMENT '订单索引',
  `OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `OrderNotifyTime` datetime NULL DEFAULT NULL,
  `SupplierBranchID` int NULL DEFAULT NULL COMMENT '财务分部ID',
  `ParentID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '父订单ID',
  `MerchantID` int NULL DEFAULT NULL COMMENT '商户编号',
  `MerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `UserLevel` int NULL DEFAULT -1,
  `PayType` int NULL DEFAULT NULL COMMENT '付款方式,1：银行卡，2：第三方，3：其他方式',
  `PayAmount` decimal(10, 2) NULL DEFAULT 0.00,
  `PaidAmount` decimal(10, 2) NULL DEFAULT 0.00,
  `PaidChannelFee` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '商户支付手续费',
  `SupplierPaidChannelFee` decimal(10, 2) NULL DEFAULT NULL COMMENT '渠道提供方手续费，比如需向三方通道付手续费，比如平均银行卡磨损费',
  `AgencyCommission` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理佣金',
  `NotifyUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `ReturnUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `MerchantRemark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `PayChannelID` int NULL DEFAULT NULL COMMENT '处理该订单的充值渠道',
  `BankAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `OrderStatus` int NULL DEFAULT 0,
  `OrderPaidStatusChangeTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单状态最后修改时间',
  `OrderNotifyStatus` int NULL DEFAULT 0,
  `Remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `BankCode` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `BankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `BankAddress` varchar(270) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `BankOwner` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `ReadMark` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用于标注是否被自动付款取走',
  `MD5OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `Digest` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `CloseReason` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `OrderType` int NULL DEFAULT 0,
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `UserId` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员ID',
  `UserIP` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员IP',
  `Operator` int NULL DEFAULT 0 COMMENT '操作员',
  `MemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员账号',
  `allotuserid` int NOT NULL DEFAULT 0 COMMENT '分配人员id',
  `allotstatus` int NOT NULL DEFAULT 0 COMMENT '0:未分配,1:已分配',
  `allottime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
  `UserCreditLevel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户层级，根据用户层级等级分配不同支付渠道;多个层级，使用‘,’分割，比如支持5_1,5_2层级，则是5_1,5_2',
  `OperationType` int NOT NULL DEFAULT 0 COMMENT '0:正常订单,1:直接手出',
  `HankType` int NOT NULL DEFAULT 0 COMMENT '手动出款类型 0正常出款 ',
  `PickTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单抓取时间',
  `EncryptBankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户银行卡简码',
  `EncryptBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡号码',
  `EncryptBankOwner` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '提款人',
  `EncryptMemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员账号',
  `transferOrderTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '首次创建时间',
  `AutoMataType` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '自动机客户端类型  0 PC 1 手机银行',
  `Protocol` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'usdt的协议',
  `EncryptBankAddress` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '开户地址',
  `Expedited` int NOT NULL DEFAULT 0 COMMENT '是否加急（0、未加急，1、加急 默认0）',
  `assignor` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '分配人',
  `AssignorStatus` int NOT NULL DEFAULT 0 COMMENT '分配状态 0未分配  1 分配处理中',
  `Currency` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '渠道币种 默认为0 人民币 数值参考bank_type',
  `Rate` decimal(10, 4) NOT NULL DEFAULT 1.0000 COMMENT '商户汇率',
  `AmountType` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'CNY' COMMENT '金额类型 默认CNY 具体数值参考币种',
  `VirtualPayAmount` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币金额',
  `VirtualPaidAmount` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币金额',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `deduction` tinyint NOT NULL DEFAULT 0 COMMENT '扣费标识，0未扣费，1已扣费',
  `bill_sign` tinyint NOT NULL DEFAULT 0 COMMENT '结算标识，0未结算，1已结算',
  `process_pay_type` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '最初处理该订单的支付方式',
  `LockStatus` int NULL DEFAULT 0 COMMENT '订单锁定状态 0未锁定 1已锁定',
  `OrderJustifyTime` datetime NULL DEFAULT NULL COMMENT '订单冲正时间',
  `withdrawType` int NOT NULL DEFAULT 0 COMMENT '取款类型',
  `betType` int NOT NULL DEFAULT 0 COMMENT '0 不投注 1 投注',
  PRIMARY KEY (`OrderIndex`) USING BTREE,
  UNIQUE INDEX `ReadMark_UNIQUE`(`ReadMark` ASC) USING BTREE,
  UNIQUE INDEX `OrderID_UNIQUE`(`OrderID` ASC) USING BTREE,
  INDEX `SupplierBranchID_Index`(`SupplierBranchID` ASC) USING BTREE,
  INDEX `idx_OrderPaidStatusChangeTime`(`OrderPaidStatusChangeTime` ASC) USING BTREE,
  INDEX `idx_MerchantOrderID_PayAmount`(`MerchantOrderID` ASC, `PayAmount` ASC) USING BTREE,
  INDEX `idx_BankAccount`(`BankAccount` ASC) USING BTREE,
  INDEX `Digest_Index`(`Digest` ASC) USING BTREE,
  INDEX `CreateTime_Index`(`CreateTime` ASC) USING BTREE,
  INDEX `create_ordernotify_ordertype`(`OrderType` ASC, `OrderNotifyStatus` ASC, `CreateTime` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '充值订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_withdraworder_history
-- ----------------------------
DROP TABLE IF EXISTS `t_withdraworder_history`;
CREATE TABLE `t_withdraworder_history`  (
  `OrderIndex` bigint NOT NULL COMMENT '订单索引',
  `OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `OrderNotifyTime` datetime NULL DEFAULT NULL,
  `SupplierBranchID` int NULL DEFAULT NULL COMMENT '财务分部ID',
  `ParentID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '父订单ID',
  `MerchantID` int NULL DEFAULT NULL COMMENT '商户编号',
  `MerchantOrderID` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `UserLevel` int NULL DEFAULT -1,
  `PayType` int NULL DEFAULT NULL COMMENT '付款方式,1：银行卡，2：第三方，3：其他方式',
  `PayAmount` decimal(10, 2) NULL DEFAULT 0.00,
  `PaidAmount` decimal(10, 2) NULL DEFAULT 0.00,
  `PaidChannelFee` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '商户支付手续费',
  `SupplierPaidChannelFee` decimal(10, 2) NULL DEFAULT NULL COMMENT '渠道提供方手续费，比如需向三方通道付手续费，比如平均银行卡磨损费',
  `AgencyCommission` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '代理佣金',
  `NotifyUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `ReturnUrl` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `MerchantRemark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `PayChannelID` int NULL DEFAULT NULL COMMENT '处理该订单的充值渠道',
  `BankAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `OrderStatus` int NULL DEFAULT 0,
  `OrderPaidStatusChangeTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单状态最后修改时间',
  `OrderNotifyStatus` int NULL DEFAULT 0,
  `Remark` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `BankCode` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `BankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `BankAddress` varchar(270) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `BankOwner` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `ReadMark` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用于标注是否被自动付款取走',
  `MD5OrderID` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `Digest` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `CloseReason` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `OrderType` int NULL DEFAULT 0,
  `CreateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `UserId` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员ID',
  `UserIP` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员IP',
  `Operator` int NULL DEFAULT 0 COMMENT '操作员',
  `MemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员账号',
  `allotuserid` int NOT NULL DEFAULT 0 COMMENT '分配人员id',
  `allotstatus` int NOT NULL DEFAULT 0 COMMENT '0:未分配,1:已分配',
  `allottime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
  `UserCreditLevel` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户层级，根据用户层级等级分配不同支付渠道;多个层级，使用‘,’分割，比如支持5_1,5_2层级，则是5_1,5_2',
  `OperationType` int NOT NULL DEFAULT 0 COMMENT '0:正常订单,1:直接手出',
  `HankType` int NOT NULL DEFAULT 0 COMMENT '手动出款类型 0正常出款 ',
  `PickTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '订单抓取时间',
  `EncryptBankAccount` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户银行卡简码',
  `EncryptBankNum` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '银行卡号码',
  `EncryptBankOwner` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '提款人',
  `EncryptMemberAccount` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会员账号',
  `transferOrderTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '首次创建时间',
  `AutoMataType` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '自动机客户端类型  0 PC 1 手机银行',
  `Protocol` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'usdt的协议',
  `EncryptBankAddress` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '开户地址',
  `Expedited` int NOT NULL DEFAULT 0 COMMENT '是否加急（0、未加急，1、加急 默认0）',
  `assignor` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '分配人',
  `AssignorStatus` int NOT NULL DEFAULT 0 COMMENT '分配状态 0未分配  1 分配处理中',
  `Currency` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '渠道币种 默认为0 人民币 数值参考bank_type',
  `Rate` decimal(10, 4) NOT NULL DEFAULT 1.0000 COMMENT '商户汇率',
  `AmountType` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'CNY' COMMENT '金额类型 默认CNY 具体数值参考币种',
  `VirtualPayAmount` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币金额',
  `VirtualPaidAmount` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '虚拟币金额',
  `node_id` int NOT NULL DEFAULT 2 COMMENT '数据来源,1天下开发,2天下测试',
  `deduction` tinyint NOT NULL DEFAULT 0 COMMENT '扣费标识，0未扣费，1已扣费',
  `bill_sign` tinyint NOT NULL DEFAULT 0 COMMENT '结算标识，0未结算，1已结算',
  `process_pay_type` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '最初处理该订单的支付方式',
  `LockStatus` int NULL DEFAULT 0 COMMENT '订单锁定状态 0未锁定 1已锁定',
  `OrderJustifyTime` datetime NULL DEFAULT NULL COMMENT '订单冲正时间',
  `withdrawType` int NOT NULL DEFAULT 0 COMMENT '取款类型',
  `betType` int NOT NULL DEFAULT 0 COMMENT '0 不投注 1 投注',
  PRIMARY KEY (`OrderIndex`) USING BTREE,
  UNIQUE INDEX `OrderID_UNIQUE`(`OrderID` ASC) USING BTREE,
  UNIQUE INDEX `ReadMark_UNIQUE`(`ReadMark` ASC) USING BTREE,
  INDEX `create_ordernotify_ordertype`(`OrderType` ASC, `OrderNotifyStatus` ASC, `CreateTime` ASC) USING BTREE,
  INDEX `CreateTime_Index`(`CreateTime` ASC) USING BTREE,
  INDEX `Digest_Index`(`Digest` ASC) USING BTREE,
  INDEX `idx_BankAccount`(`BankAccount` ASC) USING BTREE,
  INDEX `idx_MerchantOrderID_PayAmount`(`MerchantOrderID` ASC, `PayAmount` ASC) USING BTREE,
  INDEX `idx_OrderPaidStatusChangeTime`(`OrderPaidStatusChangeTime` ASC) USING BTREE,
  INDEX `SupplierBranchID_Index`(`SupplierBranchID` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '充值订单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for third_merchant_balance_alarm_config
-- ----------------------------
DROP TABLE IF EXISTS `third_merchant_balance_alarm_config`;
CREATE TABLE `third_merchant_balance_alarm_config`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `pay_channel_id` int NOT NULL COMMENT '支付渠道ID',
  `alarm_threshold` decimal(18, 4) NOT NULL DEFAULT 0.0000 COMMENT '余额告警阈值',
  `alarm_period` int NOT NULL DEFAULT 0 COMMENT '余额告警周期',
  `alarm_frequency` int NOT NULL DEFAULT 0 COMMENT '余额告警频率',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` int NOT NULL DEFAULT 0 COMMENT '创建人',
  `update_by` int NOT NULL DEFAULT 0 COMMENT '修改人',
  `node_id` int NOT NULL DEFAULT 1 COMMENT '数据来源: 1 skg天下,2 柬天下,3 日本天下',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uniq_channel_id`(`pay_channel_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '三方商户告警配置表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
