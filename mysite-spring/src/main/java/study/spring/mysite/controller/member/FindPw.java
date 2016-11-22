package study.spring.mysite.controller.member;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import study.spring.helper.WebHelper;

@Controller
public class FindPw {
	//private static Logger logger = LoggerFactory.getLogger(FindPw.class);

	@Autowired
	WebHelper web;
	
	@RequestMapping(value = "/member/find_pw.do")
	public ModelAndView doRun(Locale locale, Model model, HttpServletRequest request, HttpServletResponse response) {

		web.init();

		/** (3) 로그인 여부 검사 */
		// 로그인 중이라면 이 페이지를 이용할 수 없다.
		if (web.getSession("loginInfo") != null) {
			return web.redirect(web.getRootPath() + "/index.do", "이미 로그인 중입니다.");
		}
		
		/** (4) 사용할 View의 이름 리턴 */
		return new ModelAndView("member/find_pw");
	}

}
