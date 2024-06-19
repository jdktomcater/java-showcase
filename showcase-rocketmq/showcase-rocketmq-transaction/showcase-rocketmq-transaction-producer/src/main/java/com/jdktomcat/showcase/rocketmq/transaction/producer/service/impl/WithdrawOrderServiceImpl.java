package com.jdktomcat.showcase.rocketmq.transaction.producer.service.impl;

import com.jdktomcat.showcase.rocketmq.transaction.producer.mapper.WithdrawOrderMapper;
import com.jdktomcat.showcase.rocketmq.transaction.producer.model.ThirdWithdrawOrder;
import com.jdktomcat.showcase.rocketmq.transaction.producer.service.IWithdrawOrderService;
import com.jdktomcat.showcase.rocketmq.transaction.producer.util.OrderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WithdrawOrderServiceImpl implements IWithdrawOrderService {

    @Autowired
    private WithdrawOrderMapper withdrawOrderMapper;

    @Override
    public ThirdWithdrawOrder createOrder() {
        ThirdWithdrawOrder thirdWithdrawOrder = new ThirdWithdrawOrder();
        try {
            thirdWithdrawOrder.setOrderId(OrderUtil.getPltOrderID(prefix, tMerchant.getMerchantCode(), 4));
            thirdWithdrawOrder.setMerchantId(withdrawParms.getMerchant_id());
            thirdWithdrawOrder.setMerchantOrderId(withdrawParms.getMerchant_order_id());
            thirdWithdrawOrder.setPayType(withdrawParms.getPay_type());
            thirdWithdrawOrder.setPayChannelID(tPaychannel.getPayChannelID());
            //1.更新商户玩家的userId
            thirdWithdrawOrder.setUserId(withdrawParms.getUser_id());
            if (org.apache.commons.lang.StringUtils.isNotEmpty(withdrawParms.getUser_id()) && null != withdrawParms.getMerchant_id()) {
                withdrawParms.setUser_id(withdrawParms.getMerchant_id() + "_" + withdrawParms.getUser_id());
            }
            tThirdwithdraworder.setUserIP(withdrawParms.getUser_ip());
            tThirdwithdraworder.setUserLevel(withdrawParms.getUser_level());
            tThirdwithdraworder.setUserCreditLevel(withdrawParms.getUser_credit_level());
            tThirdwithdraworder.setPayChannelName(tPaychannel.getPayChannelName());
            tThirdwithdraworder.setPayAmount(withdrawParms.getPay_amt());
            tThirdwithdraworder.setNotifyUrl(withdrawParms.getNotify_url());
            tThirdwithdraworder.setReturnUrl(withdrawParms.getReturn_url());
            tThirdwithdraworder.setMerchantRemark(withdrawParms.getRemark());
            tThirdwithdraworder.setMD5OrderID(DigestUtils.md5DigestAsHex(tThirdwithdraworder.getOrderId().getBytes(StandardCharsets.UTF_8)));
            tThirdwithdraworder.setBankCode(withdrawParms.getBank_code());
            tThirdwithdraworder.setBankNum(withdrawParms.getBank_num());
            tThirdwithdraworder.setMemberAccount(withdrawParms.getMember_account());
            if(null != withdrawParms.getCreateTime()){
                tThirdwithdraworder.setCreateTime(withdrawParms.getCreateTime());
            }else {
                tThirdwithdraworder.setCreateTime(DateUtil.formatMillOfDate(new Date()));
            }
            tThirdwithdraworder.setBankAddress(withdrawParms.getBank_address());
            tThirdwithdraworder.setBankOwner(withdrawParms.getBank_owner());
            tThirdwithdraworder.setSupplierBranchId(tPaychannel.getSupplierBranchID());
            tThirdwithdraworder.setWithdrawType(withdrawParms.getWithdraw_type());
            tThirdwithdraworder.setProtocol(withdrawParms.getProtocol());
            String Digest = DigestUtils.md5DigestAsHex((withdrawParms.getPay_type() + withdrawParms.getPay_amt().toString() + withdrawParms.getBank_code() + withdrawParms.getBank_num() + withdrawParms.getBank_owner() + withdrawParms.getBank_address()).getBytes(StandardCharsets.UTF_8)).toLowerCase().trim();
            tThirdwithdraworder.setDigest(Digest);
            tThirdwithdraworder.setProcessPayType(withdrawParms.getProcessPayType());
            //操作来源:提款订单-会员
            tThirdwithdraworder.setRequestSource(11);
            tThirdwithdraworder.setWithdrawType(withdrawParms.getWithdraw_type());
            tThirdwithdraworder.setIsFastPay(1);//极速订单
            // 设置订单类型
            tThirdwithdraworder.setIsChange(WithdrawOrderTypeEnum.FIXED.getType());

            // 先扣减余额再创建订单，余额扣减失败直接抛异常
            TChannelaccountrecord tChannelaccountrecord = new TChannelaccountrecord(tThirdwithdraworder, tPaychannel, 1);
            payChannelService.sumPayChannelByRecord(tChannelaccountrecord,tPaychannel);
            BigDecimal merchantFee = merchantService.merchantWithdraw(tMerchant ,tThirdwithdraworder, true);
            tThirdwithdraworder.setSupplierPaidChannelFee(merchantFee);
            tThirdwithdraworder.setDeduction(1);
            tThirdwithdraworder.setWithdrawSuccessNum(withdrawParms.getWithdraw_success_num());
            tThirdwithdraworder.setSplitOrder(withdrawParms.getSplit_order());
            tThirdwithdraworder.setApiRemark(withdrawParms.getApiRemark());
            tThirdwithdraworder.setIsDc(withdrawParms.getIs_dc());
            if (null!=withdrawParms.getApp_site() && !"".equals(withdrawParms.getApp_site())){
                tThirdwithdraworder.setAppSite(AppSiteType.getAppSiteType(withdrawParms.getApp_site()).getCode());
            }
            tThirdwithdraworder.setUserAddress(withdrawParms.getUser_address());
            tThirdwithdraworder.setIdNum(withdrawParms.getId_num());
            tThirdwithdraworder.setBankMobile(withdrawParms.getBank_mobile());
            if (withdrawParms.isBlackUser()){
                tThirdwithdraworder.setHankType(HankTypeConstant.BLACK_USER);
            }else if (RiskStatus.isMobileOrIDError(withdrawParms.getOutDfType())){
                tThirdwithdraworder.setHankType(HankTypeConstant.RISK_PHONE_ID_ERROR);
            }
            //外部极速商户需要计算商户返利金额，以实际金额，订单创建时商户费率为准
            log.info("当前商户是否为外部极速商户:{}",tMerchant.getMerchantType() ==2);
            if(tMerchant.getMerchantType() ==2){
                log.info("当前商户:{}，支付方式:{}",tThirdwithdraworder.getMerchantId(),tThirdwithdraworder.getPayType());
                MerchantPayTypeRate rate=rateService.getSetting(tThirdwithdraworder.getPayType(),tThirdwithdraworder.getMerchantId());
                log.info("商户费率配置为:{}",rate);
                if(ObjectUtil.isNotEmpty(rate)){
                    tThirdwithdraworder.setMerchantTotalRebate(RebateUtils.setRebate(rate));
                }else{
                    log.info("商户订单号:{}，支付方式:{},商户ID：{}，没有找到对应的支付方式服务费率",tThirdwithdraworder.getMerchantOrderId(),tThirdwithdraworder.getPayType(),tThirdwithdraworder.getMerchantId());
                }
            }
            if (RiskStatus.isMobileOrIDError(withdrawParms.getOutDfType())){
                tThirdwithdraworder.setRemark(RiskStatus.getRiskStatus(withdrawParms.getOutDfType()).getReason());
            }
            tThirdwithdraworder.setRiskOrderType(withdrawParms.getRiskOrderType());
            int count = tThirdwithdraworderService.saveWotjdrawOrder(tThirdwithdraworder);
            if (count == 0) {
                log.info("下单失败, 订单号 {}", tThirdwithdraworder.getMerchantOrderId());
                throw new OrderCreateException("订单参数有误，下单异常");
            }
        } catch (Exception e) {
            log.info("下单失败:{}", withdrawParms.getMerchant_order_id(), e);
            delRedisOrder(withdrawParms.getMerchant_order_id());
            throw new OrderCreateException("订单参数有误，下单异常", e);
        }
        try {
            log.info("入库订单成功，开始请求极速支付通道 {} {},FixedAmt:{}", withdrawParms.getMerchant_order_id(), withdrawParms,withdrawParms.getFixedAmt());
            //5.请求极速支付充值撮合
            String strResult = matchupPayService.requestWithdrawOnMatchupPay(withdrawParms, tPaychannel, tThirdwithdraworder.getOrderId());
            JSONObject jSONObject = JSON.parseObject(strResult);
            String status = jSONObject.getString("status");
            boolean  newUserPriorFlag=false ;
            log.info("极速支付提款通道返回 OrderIndex:{}  返回 :{}", tThirdwithdraworder.getOrderIndex(),strResult);
            if ("200".equals(status)) {
                String newUserWithdrawCount = serviceConfigService.getConfig(ServiceKey.NEW_USER_WITHDRAW_SET+tPaychannel.getSupplierBranchID());
                log.info("当前该渠道财务分支key:{} 设置次数 {} 入参Withdrawsuccessnum {} ",ServiceKey.NEW_USER_WITHDRAW_SET+tPaychannel.getSupplierBranchID(),newUserWithdrawCount ,withdrawParms.getWithdraw_success_num());
                if (StringUtils.isNotEmpty(newUserWithdrawCount)){
                    if ( null!=withdrawParms.getWithdraw_success_num() && -1!=withdrawParms.getWithdraw_success_num() ){
                        if(withdrawParms.getWithdraw_success_num()<Integer.valueOf(newUserWithdrawCount)){
                            newUserPriorFlag=true;
                            TThirdwithdraworder updateOrder = new TThirdwithdraworder();
                            updateOrder.setOrderIndex(tThirdwithdraworder.getOrderIndex());
                            updateOrder.setPriorityMatchStatus(1);
                            tThirdwithdraworderMapper.updateById(updateOrder);
                            log.info("更新当前订单为优先标记OrderIndex:{}", tThirdwithdraworder.getOrderIndex());
                        }
                    }
                }
            }

            if (!"200".equals(status)) {
                if("3114".equals(status)){
                    withdrawParms.setExpCode("3114");
                }
                String msg = jSONObject.getString("msg");
                // log.info("极速下单无法匹配1:{}",withdrawParms.getMerchant_order_id());
                // log.error("请求极速支付通道失败 mOrderId: {} result: {}",tThirdwithdraworder.getMerchantOrderID(),strResult);
                throw new BaseServiceException(msg, ResponseStatus.ERROR);
            }else{
                //生成子提款订单逻辑 add  2021-10-24
                String jsonChildOrder = jSONObject.getString("data");
                Integer isChange = jSONObject.getInteger("isChange");
                if(StringUtils.isNotEmpty(jsonChildOrder)){
                    List<MatchupSplitWithdrawVO> lstChildOrder=JSON.parseArray(jsonChildOrder, MatchupSplitWithdrawVO.class);
                    if(!CollectionUtils.isEmpty(lstChildOrder)){
                        tThirdwithdraworder.setIsFastPay(10);
                        tThirdwithdraworder.setOrderStatus(-1);
                        List<MatchupSplitWithdrawVO> subsidyList = lstChildOrder.stream().filter(e -> !ObjectUtils.isEmpty(e.getSplit_subsidy_amt()) && e.getSplit_subsidy_amt().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());
                        if (!subsidyList.isEmpty()) {
                            tThirdwithdraworder.setSplitSubsidyAmount(subsidyList.get(0).getSplit_subsidy_amt());
                        }
                        tThirdwithdraworderMapper.updateById(tThirdwithdraworder);
                        tThirdwithdraworder.setOrderStatus(0);
                        tThirdwithdraworder.setParentOrderId(tThirdwithdraworder.getOrderId());
                        List<TThirdwithdraworder> thirdChildren = Lists.newArrayList();
                        for(MatchupSplitWithdrawVO childOrder:lstChildOrder){
                            TThirdwithdraworder childWithdrawOrder = new TThirdwithdraworder();
                            BeanUtils.copyProperties(tThirdwithdraworder,childWithdrawOrder);
                            childWithdrawOrder.setOrderId(childOrder.getTypay_order_id());
                            childWithdrawOrder.setMD5OrderID(DigestUtils.md5DigestAsHex(childOrder.getTypay_order_id().getBytes(StandardCharsets.UTF_8)));
                            childWithdrawOrder.setPayAmount(childOrder.getPay_amt());
                            if (!subsidyList.isEmpty()) {
                                childWithdrawOrder.setSplitSubsidyAmount(childOrder.getSplit_subsidy_amt());
                            }
                            childWithdrawOrder.setCreateTime(DateUtil.formatMillOfDate(new Date()));
                            childWithdrawOrder.setIsFastPay(11);
                            if (newUserPriorFlag)  childWithdrawOrder.setPriorityMatchStatus(1);
                            childWithdrawOrder.setIsChange(ObjectUtils.isEmpty(childOrder.getIs_change()) ? 0 : childOrder.getIs_change());
                            thirdChildren.add(childWithdrawOrder);
                        }
                        // 按比例分配服务费
                        commonLiquidateService.calcSupplierCharge(tThirdwithdraworder.getPayAmount(), null, thirdChildren, tThirdwithdraworder.getSupplierPaidChannelFee(), MerchantFeeCalcMode.DEDUCTION);
                        for (TThirdwithdraworder thirdChild : thirdChildren) {
                            int count = tThirdwithdraworderService.saveWotjdrawOrder(thirdChild);
                            if (count == 0) {
                                log.error("创建提款子订单失败,商户订单ID {} {}", tThirdwithdraworder.getMerchantOrderId(),thirdChild.getOrderId());
                                throw new OrderCreateException("保存提款子订单异常，拆单失败:"+tThirdwithdraworder.getMerchantOrderId());
                            }else{
                                log.info("创建提款子订单成功,商户订单ID {} {}", tThirdwithdraworder.getMerchantOrderId(),thirdChild.getOrderId());
                            }
                        }
                    }
                    //设置子订单列表
                    tThirdwithdraworder.setSubOrderList(lstChildOrder);
                } else if (isChange != null && isChange == 1){
                    TThirdwithdraworder withdrawOrder = new TThirdwithdraworder();
                    withdrawOrder.setIsChange(1);
                    tThirdwithdraworderMapper.update(withdrawOrder, new UpdateWrapper<TThirdwithdraworder>().eq("OrderID", tThirdwithdraworder.getOrderId()));
                }
            }
        } catch (Throwable e) {
            delRedisOrder(withdrawParms.getMerchant_order_id());
            log.info("极速下单无法匹配:{}", withdrawParms.getMerchant_order_id());
            throw new RuntimeException("请求极速支付通道异常:" + e.getMessage(), e);
        }
        return thirdWithdrawOrder;
    }
}
