package com.practice.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

	@ResponseBody
	@GetMapping(value = "/api/test")
	public TestVO Home() {
        TestVO tv = new TestVO();
        tv.setId(1);
        tv.setName("123");
        return tv;  // "home" 뷰 이름을 반환
	}
}
