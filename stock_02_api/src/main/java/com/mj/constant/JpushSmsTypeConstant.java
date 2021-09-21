package com.mj.constant;

/**
 * jpush推送伴随短信参数：提醒事项文案
 * @author fhw
 *
 */
public class JpushSmsTypeConstant {

	//---------------------------------- OA待审批提醒 --------------------------------------
	//审批事项-请假
	public final static String TYPE_APPROVE_LEAVE = "请假申请";
	//审批事项-借据
	public final static String TYPE_APPROVE_BORROW = "借据[{0}]";
	//审批事项-付款申请书
	public final static String TYPE_APPROVE_PAYMENT = "付款申请书[{0}]";
	//审批事项-领据
	public final static String TYPE_APPROVE_RECEIVE = "领据[{0}]";
	//审批事项-报销单
	public final static String TYPE_APPROVE_REIMBURSEMENT = "报销单[{0}]";
	//审批事项-物品申领单
	public final static String TYPE_APPROVE_GOODS = "物品申领单[{0}]";
	//审批事项-合同批签
	public final static String TYPE_APPROVE_CONTRACT = "合同批签[{0}]";
	
	//---------------------------------- OA审批结果通知 --------------------------------------
	//审批事项-请假，通过
	public final static String TYPE_LEAVE_RET_OK = "请假申请";
	//审批事项-请假，未通过
	public final static String TYPE_LEAVE_RET_FAIL = "请假申请";
	//审批事项-借据，通过
	public final static String TYPE_BORROW_RET_OK = "借据[{0}]";
	//审批事项-借据，未通过
	public final static String TYPE_BORROW_RET_FAIL = "借据[{0}]";
	//审批事项-付款申请书，通过
	public final static String TYPE_PAYMENT_RET_OK = "付款申请书[{0}]";
	//审批事项-付款申请书，未通过
	public final static String TYPE_PAYMENT_RET_FAIL = "付款申请书[{0}]";
	//审批事项-领据，通过
	public final static String TYPE_RECEIVE_RET_OK = "领据[{0}]";
	//审批事项-领据，未通过
	public final static String TYPE_RECEIVE_RET_FAIL = "领据[{0}]";
	//审批事项-报销单，通过
	public final static String TYPE_REIMBURSEMENT_RET_OK = "报销单[{0}]";
	//审批事项-报销单，未通过
	public final static String TYPE_REIMBURSEMENT_RET_FAIL = "报销单[{0}]";
	//审批事项-物品申领单，通过
	public final static String TYPE_GOODS_RET_OK = "物品申领单[{0}]";
	//审批事项-物品申领单，未通过
	public final static String TYPE_GOODS_RET_FAIL = "物品申领单[{0}]";
	//审批事项-合同批签，通过
	public final static String TYPE_CONTRACT_RET_OK = "合同批签[{0}]";
	//审批事项-合同批签，未通过
	public final static String TYPE_CONTRACT_RET_FAIL = "合同批签[{0}]";
	
	//---------------------------------- OA打款拒绝提醒 --------------------------------------
	//审批事项-借据
	public final static String TYPE_APPROVE_BORROW_PAY_REFUSE = "借据[{0}]";
	//审批事项-付款申请书
	public final static String TYPE_APPROVE_PAYMENT_PAY_REFUSE = "付款申请书[{0}]";
	//审批事项-领据
	public final static String TYPE_APPROVE_RECEIVE_PAY_REFUSE = "领据[{0}]";
	//审批事项-报销单
	public final static String TYPE_APPROVE_REIMBURSEMENT_PAY_REFUSE = "报销单[{0}]";
	
}
