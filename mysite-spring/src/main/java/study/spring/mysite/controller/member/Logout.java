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
import study.spring.mysite.model.Member;

@Controller
public class Logout {
	/** (1) 사용하고자 하는 Helper + Service 객체 선언 */
	@Autowired
	WebHelper web;
	
	@RequestMapping(value = "/member/logout.do")
	public ModelAndView doRun(Locale locale, Model model, HttpServletRequest request, HttpServletResponse response) {
		
		/** (2) 필요한 헬퍼 객체 생성 */
		web.init();

		/** (3) 로그인 여부 검사 */
		// 로그인중인 회원 정보 가져오기
		Member loginInfo = (Member) web.getSession("loginInfo");
		// 로그인 중이 아니라면 이 페이지를 동작시켜서는 안된다.
		if (loginInfo == null) {
			return web.redirect(web.getRootPath() + "/index.do", "로그인 후에 이용 가능합니다.");
			
		}

		/** (4) 로그아웃 */
		// 로그아웃은 모든 세션 정보를 삭제하는 처리.
		web.removeAllSession();

		/** (5) 페이지 이동 */
		return web.redirect(web.getRootPath() + "/index.do", "로그아웃 되었습니다.");
	}

}
