package com.bookshop01.order.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.bookshop01.common.base.BaseController;
import com.bookshop01.goods.vo.GoodsVO;
import com.bookshop01.member.vo.MemberVO;
import com.bookshop01.order.service.OrderService;
import com.bookshop01.order.vo.OrderVO;
import com.bookshop01.test.ApiService;

@Controller("orderController")
@RequestMapping(value="/order")
public class OrderControllerImpl extends BaseController implements OrderController {
	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderVO orderVO;
	@Autowired
	private ApiService apiService;
	
	@RequestMapping(value="/orderEachGoods.do" ,method = RequestMethod.POST)
	public ModelAndView orderEachGoods(@ModelAttribute("orderVO") OrderVO _orderVO,
			                       HttpServletRequest request, HttpServletResponse response)  throws Exception{
		
		request.setCharacterEncoding("utf-8");
		HttpSession session=request.getSession();
		session=request.getSession();
		
		Boolean isLogOn=(Boolean)session.getAttribute("isLogOn");
		String action=(String)session.getAttribute("action");
		//�α��� ���� üũ
		//������ �α��� ������ ���� �ֹ����� ����
		//�α׾ƿ� ������ ��� �α��� ȭ������ �̵�
		if(isLogOn==null || isLogOn==false){
			session.setAttribute("orderInfo", _orderVO);
			session.setAttribute("action", "/order/orderEachGoods.do");
			return new ModelAndView("redirect:/member/loginForm.do");
		}else{
			 if(action!=null && action.equals("/order/orderEachGoods.do")){
				orderVO=(OrderVO)session.getAttribute("orderInfo");
				session.removeAttribute("action");
			 }else {
				 orderVO=_orderVO;
			 }
		 }
		
		String viewName=(String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		
		List myOrderList=new ArrayList<OrderVO>();
		myOrderList.add(orderVO);

		MemberVO memberInfo=(MemberVO)session.getAttribute("memberInfo");
		
		session.setAttribute("myOrderList", myOrderList);
		session.setAttribute("orderer", memberInfo);
		return mav;
	}
	
	@RequestMapping(value="/orderAllCartGoods.do" ,method = RequestMethod.POST)
	public ModelAndView orderAllCartGoods( @RequestParam("cart_goods_qty")  String[] cart_goods_qty,
			                 HttpServletRequest request, HttpServletResponse response)  throws Exception{
		String viewName=(String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		HttpSession session=request.getSession();
		Map cartMap=(Map)session.getAttribute("cartMap");
		List myOrderList=new ArrayList<OrderVO>();
		
		List<GoodsVO> myGoodsList=(List<GoodsVO>)cartMap.get("myGoodsList");
		MemberVO memberVO=(MemberVO)session.getAttribute("memberInfo");
		
		for(int i=0; i<cart_goods_qty.length;i++){
			String[] cart_goods=cart_goods_qty[i].split(":");
			for(int j = 0; j< myGoodsList.size();j++) {
				GoodsVO goodsVO = myGoodsList.get(j);
				int goods_id = goodsVO.getGoods_id();
				if(goods_id==Integer.parseInt(cart_goods[0])) {
					OrderVO _orderVO=new OrderVO();
					String goods_title=goodsVO.getGoods_title();
					int goods_sales_price=goodsVO.getGoods_sales_price();
					String goods_fileName=goodsVO.getGoods_fileName();
					_orderVO.setGoods_id(goods_id);
					_orderVO.setGoods_title(goods_title);
					_orderVO.setGoods_sales_price(goods_sales_price);
					_orderVO.setGoods_fileName(goods_fileName);
					_orderVO.setOrder_goods_qty(Integer.parseInt(cart_goods[1]));
					myOrderList.add(_orderVO);
					break;
				}
			}
		}
		session.setAttribute("myOrderList", myOrderList);
		session.setAttribute("orderer", memberVO);
		return mav;
	}	
	
	//최종결제하기 버튼을 눌렀을때
	@RequestMapping(value="/payToOrderGoods.do" ,method = RequestMethod.POST)
	public ModelAndView payToOrderGoods(@RequestParam Map<String, String> receiverMap,
			                       HttpServletRequest request, HttpServletResponse response)  throws Exception{
		String viewName=(String)request.getAttribute("viewName");
		ModelAndView mav = new ModelAndView(viewName);
		
		
		//1. 주문 데이터 생성 (처음부터 있던 소스)
		HttpSession session=request.getSession();
		MemberVO memberVO=(MemberVO)session.getAttribute("orderer");
		String member_id=memberVO.getMember_id();
		String orderer_name=memberVO.getMember_name();
		String orderer_hp = memberVO.getHp1()+"-"+memberVO.getHp2()+"-"+memberVO.getHp3();
		List<OrderVO> myOrderList=(List<OrderVO>)session.getAttribute("myOrderList");
		
		for(int i=0; i<myOrderList.size();i++){
			OrderVO orderVO=(OrderVO)myOrderList.get(i);
			orderVO.setMember_id(member_id);
			orderVO.setOrderer_name(orderer_name);
			orderVO.setReceiver_name(receiverMap.get("receiver_name"));
			
			orderVO.setReceiver_hp1(receiverMap.get("receiver_hp1"));
			orderVO.setReceiver_hp2(receiverMap.get("receiver_hp2"));
			orderVO.setReceiver_hp3(receiverMap.get("receiver_hp3"));
			orderVO.setReceiver_tel1(receiverMap.get("receiver_tel1"));
			orderVO.setReceiver_tel2(receiverMap.get("receiver_tel2"));
			orderVO.setReceiver_tel3(receiverMap.get("receiver_tel3"));
			
			orderVO.setDelivery_address(receiverMap.get("delivery_address"));
			orderVO.setDelivery_message(receiverMap.get("delivery_message"));
			orderVO.setDelivery_method(receiverMap.get("delivery_method"));
			orderVO.setGift_wrapping(receiverMap.get("gift_wrapping"));
			orderVO.setPay_method(receiverMap.get("pay_method"));
			orderVO.setCard_com_name(receiverMap.get("card_com_name"));
			orderVO.setCard_pay_month(receiverMap.get("card_pay_month"));
			orderVO.setPay_orderer_hp_num(receiverMap.get("pay_orderer_hp_num"));	
			orderVO.setOrderer_hp(orderer_hp);
			myOrderList.set(i, orderVO); //�� orderVO�� �ֹ��� ������ ������ �� �ٽ� myOrderList�� �����Ѵ�.
		}//end for
		
	    orderService.addNewOrder(myOrderList); //이게 주문 데이터 생성하는 서비스 
	    //*T_SHOPPING_ORDER 테이블에 데이터가 들어감
	    
	    //1번 끝
	    
	    
	    //2. 신용카드 결제 요청 (수기결제 구인증방식)
	    
	    String orderNumber = "";
	    String amount = "";
	    String itemName = "";
	    String userName = "";
	    
	    for(OrderVO vo : myOrderList) {
	    	orderNumber = String.valueOf(vo.getOrder_seq_num()); 
	    	amount = String.valueOf(vo.getGoods_sales_price());
	    	itemName = vo.getGoods_title();
	    	userName = vo.getOrderer_name();
	    }
	    
	    String id = "himedia"; //발급된 계정
		String base = "https://api.testpayup.co.kr";
		String path = "/v2/api/payment/"+id+"/keyin2";
		
		String url = base+path;
		//url = https://api.testpayup.co.kr/v2/api/payment/himedia/keyin2
		
		//파라미터로 사용할 맵
		Map<String,String> map = new HashMap<String,String>();
		String signature = "";
		map.put("orderNumber",orderNumber);
		map.put("cardNo",receiverMap.get("cardNo")); //화면에서 받은 카드번호..
		map.put("expireMonth",receiverMap.get("expireMonth"));
		map.put("expireYear",receiverMap.get("expireYear"));
		map.put("birthday",receiverMap.get("birthday"));
		map.put("cardPw",receiverMap.get("cardPw"));
		map.put("amount",amount);
		map.put("quota","0");
		map.put("itemName",itemName);
		map.put("userName",userName);
		map.put("timestamp","20221010000000");
		
		signature = apiService.encrypt(id+"|"+map.get("orderNumber")+"|"+map.get("amount")+"|ac805b30517f4fd08e3e80490e559f8e|"+map.get("timestamp"));
		
		map.put("signature",signature);
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap = apiService.restApi(map, url);
		// 2번 끝
		
		
		
		//3. 결제 응답 정보를가지고 성공/실패에 따른 프로세스짜기
		String responseCode = (String) resultMap.get("responseCode");
		
		
	    
//		if("0000" == responseCode) (X)
	    if("0000".equals(responseCode)) {
	    	//여기는 결제가 성공했을 때
	    	
	    	//DB데이터 업데이트 (스킵)
	    	
	    	
	    	mav.setViewName("/order/payToOrderGoods");
	    	
	    	//결제정보 보내기
	    	mav.addObject("responseCode",resultMap.get("responseCode"));
	    	mav.addObject("responseMsg",resultMap.get("responseMsg"));
	    	mav.addObject("cardName",resultMap.get("cardName"));//카드사명
	    	mav.addObject("authNumber",resultMap.get("authNumber"));//카드승인번호
	    	mav.addObject("authDateTime",resultMap.get("authDateTime"));//승인시간
	    	
	    }else {
	    	//여기는 결제가 실패했을 때
	    	
	    	//DB데이터 업데이트 (스킵)
	    	
	    	
	    	//JSP 변경
	    	mav.setViewName("/order/orderResultFail");
	    	
	    	//결과 코드,메시지 보내기
	    	mav.addObject("responseCode",resultMap.get("responseCode"));
	    	mav.addObject("responseMsg",resultMap.get("responseMsg"));
	    }
		//3번끝
		
		
		mav.addObject("myOrderInfo",receiverMap);//OrderVO�� �ֹ���� ��������  �ֹ��� ������ ǥ���Ѵ�.
		mav.addObject("myOrderList", myOrderList);
		
		return mav;
	}

	@Override
	@RequestMapping(value="/kakaoPay.do" ,method = RequestMethod.POST)
	public ModelAndView kakaoPay(@RequestParam Map<String, String> orderMap, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModelAndView mav = new ModelAndView();

//		{ordr_idxx=20221014105507KK0967, good_name=����ũ����Ʈ ������ �����ϱ�, good_mny=100, buyr_name=�׽�Ʈ��, site_cd=A8QOB, req_tx=pay, pay_method=100000000000, currency=410, kakaopay_direct=Y, module_type=01, ordr_chk=20221014105507KK0967|100, param_opt_1=, param_opt_2=, param_opt_3=, res_cd=0000, res_msg=����, enc_info=3tixNcO.jjGNjDWXYYY5HomxJ61WYrke66eNJZ.05Up7g.FPtGVdO20M6tvz.LS.jknPlRPdFO4lPKMqWAM8u2OpYRrFnYX.GTEEwwIbwwd4T4dOB8WIFsv4UqhxMxOMDQ0cMNcwMaSAD8ESZeQ0j9Id2bRGPexfmg-2ii1gTAfe10mO4YvTJeHrpnXEKvtJtk4ri-v8Uxv__, enc_data=0YvO6yx5I6ldejeiZHiU.3YOio0RHGMdYDhI5AVz.3kxy4.llUFeAdjwXOFuHiIuh4AtJ-kzGc09zvZgAWelFUCWH-rUgQkdAytijn-z2T-U4xMYluywDHz.KUu6HOZ3Ox5e9mBauN.Pi4KJ.lEyClqm3zqo262O9p9SaW.vCJmg6jrIMpZsnTA41NAp6yyCbVsH0lH6gvAEZv7P9Za1WxvAYeax-j1QiK3S3AJOuKsG.Yq7-iJ6bhs42Ix8XVli.vVOz4JyaGEIr3bYWjtDH6bW4NFKbhxH6XQ4xGRIHUdoBhjgARTFC64dQRP442acx5yDUf1SRL85VNNlrRGy0W-TEiqy9gXT3p.JTYeAICf5p3vJvVGprFF5A7kbr7Qn1GO92VgdH2n6hDGNbAVxSTqG7faQrNokOokiG32CNw3Uyitm0JGlnsPs1GcAB632WSVnoGNc5qjLNn90W-CPETD96EqpsX0GM.A4gUjGEjW6nDTL5f1R4foJX.Y8OoL0mAzaqtSps3lW1IOML6ZXYr0Cy..pSemVMYJWAlgyiL0gVYM-00axWLl8sfir6TJo-Dq7gTsh7cA8xHqrANa.uVzhu0dbi3eKURTcgEqQTEuaus03mIWOHVaBIyggpQyswIIYGi-5Fe07w3pCxmubTVnAdag2DRjHC8.kB4C7X3dMLTkO9951yIDmmgBTlx599, ret_pay_method=CARD, tran_cd=00100000, use_pay_method=100000000000, card_pay_method=KAKAO_MONEY}
		System.out.println("카카오페이 인증 데이터 확인 = " + orderMap.toString());
		
		
		//결제요청 API 사용
		
		String url = "https://api.testpayup.co.kr/ep/api/kakao/himedia/pay";
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		resultMap = apiService.restApi(orderMap, url);
		
		//결과값 
		String responseCode = (String) resultMap.get("responseCode");
		
		//결과값에 따른 프로세스 변경
		if("0000".equals(responseCode)) {
			//성공
			mav.setViewName("/order/payToOrderGoodsKakao");
			
		}else {
			//실패
			mav.setViewName("/order/orderResultFail");
		}
		
		
		return mav;
	}
}
