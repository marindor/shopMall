package com.bookshop01.test;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class TestService{
	
	public void keyin() {
		try {
			
			
			String id = "himedia"; //강사가 교육용으로 등록한 계정 id
			String base = "https://api.testpayup.co.kr";//페이업 문서상의 url
			String path = "/v2/api/payment/"+ id + "/keyin2";
			
			String url = base + path;
			//https://api.testpayup.co.kr/v2/api/payment/himeda/keyin2
			//request할 url
			String  apiCertKey = "ac805b30517f4fd08e3e80490e559f8e";
			//파라미터로 사용할 맵
			Map<String,String> map = new HashMap<String,String>();
			map.put("orderNumber", "ZP2017090911");//json 타입 대신 이렇게 편하게 쓰려고 한다.
			map.put("cardNo", "123456789102");//json 타입 대신 이렇게 편하게 쓰려고 한다.
			map.put("expireMonth", "03");//json 타입 대신 이렇게 편하게 쓰려고 한다.
			map.put("expireYear", "25");//json 타입 대신 이렇게 편하게 쓰려고 한다.
			map.put("birthday", "820127");//json 타입 대신 이렇게 편하게 쓰려고 한다.
			map.put("cardPw", "11");//json 타입 대신 이렇게 편하게 쓰려고 한다.
			map.put("amount", "1004");//json 타입 대신 이렇게 편하게 쓰려고 한다.
			map.put("quota", "0");//json 타입 대신 이렇게 편하게 쓰려고 한다.
			map.put("itemName", "testItem");//json 타입 대신 이렇게 편하게 쓰려고 한다.
			map.put("userName", "user01");//json 타입 대신 이렇게 편하게 쓰려고 한다.
			map.put("mobileNumber", "01077779999");//json 타입 대신 이렇게 편하게 쓰려고 한다.
			map.put("kakaoSend", "test");//json 타입 대신 이렇게 편하게 쓰려고 한다.
			map.put("userEmail", "test@gmail.com");//json 타입 대신 이렇게 편하게 쓰려고 한다.
			//map.put("signature", "d186b8fedccffd291e0619fbf8b247c3f283e3769571eb57b68a50b46af23b75");//json 타입 대신 이렇게 편하게 쓰려고 한다.
			map.put("timestamp", "20170910132060");//json 타입 대신 이렇게 편하게 쓰려고 한다.
			map.put("taxFlag", "1");//json 타입 대신 이렇게 편하게 쓰려고 한다.
			map.put("taxAmount", "10");//json 타입 대신 이렇게 편하게 쓰려고 한다.
			map.put("freeAmount", "10");//json 타입 대신 이렇게 편하게 쓰려고 한다.
			
			
//			({merchantId}|{orderNumber}|{amount}|{apiCertKey}|{timestamp}
			
			String signature = encrypt(id+"|"+map.get("orderNumber")+"|"+map.get("amount")+"|"+apiCertKey+"|"+map.get("timestamp"));
			
		 	//sha256_hash({merchantId}|{orderNumber}|{amount}|{apiCertKey}|{timestamp})
			map.put("signature", signature);
			System.out.println(signature);
			
			//JSON string 데이터
			//map을 json으로 변환하는 구간(라이브러리 사용)
			String param = "";
			ObjectMapper mapper = new ObjectMapper();
			param = mapper.writeValueAsString(map);//map 구조를 string으로 바꾸는 문장
			
			//여기는 우리가 변경해야 하는 값
			
			//여기는 고정 값
			//Okhttp 사용==========<기본틀>=====================================
			//Okhttp = REST API , HTTP 통신을 간편하게 사용할 수 있도록 만든 라이브러리
			OkHttpClient client = new OkHttpClient();
			
			MediaType mediaType = MediaType.parse("application/json");//application/json <--중요
			
			RequestBody body = RequestBody.create(mediaType, param);
			Request request = new Request.Builder().url(url).post(body).addHeader("cache-control","no-cache").build();//보내는 문장
			
			//결과값 받기
			Response response = client.newCall(request).execute(); //받는 문장.
			String result = response.body().string();//response로 받은 값을 string으로 빼서 활용할 것이다.
			//여기는 고정 값(여기까지)
			
			//결과
			System.out.println(result);
			
			
			//signature 문자열을 다시 key, value 쌍으로 펼쳐보기
			ObjectMapper resultMapper = new ObjectMapper();
			Map<String,Object> resultMap = resultMapper.readValue(signature, Map.class);
			System.out.println(resultMap.toString());
			System.out.println("responseCode =" + resultMap.get("responseCode"));
			System.out.println("requestCode ="+ resultMap.get("responseMsg"));
			//==============================================================
	
			
		}catch(Exception e) {
			
			
		}
	}

	private String encrypt(String string) {
			StringBuffer hexString = new StringBuffer();			
			try {
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
		    	byte[] hash = digest.digest(string.getBytes("UTF-8"));
		    	
		    	
		    	for(int i=0; i < hash.length; i++){
		    		String hex = Integer.toHexString(0xff & hash[i]);
		    		if(hex.length() == 1) hexString.append('0');
		    		
		    		hexString.append(hex);
		    	}
			}catch(Exception e){
			}
			return hexString.toString();
		}

	
}