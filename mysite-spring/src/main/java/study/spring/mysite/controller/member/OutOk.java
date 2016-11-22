package study.spring.mysite.controller.member;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import study.spring.helper.UploadHelper;
import study.spring.helper.WebHelper;
import study.spring.mysite.controller.Download;
import study.spring.mysite.model.BbsComment;
import study.spring.mysite.model.BbsDocument;
import study.spring.mysite.model.Member;
import study.spring.mysite.service.BbsCommentService;
import study.spring.mysite.service.BbsDocumentService;
import study.spring.mysite.service.MemberService;

@Controller
public class OutOk {
	/** (1) 사용하고자 하는 Helper 객체 선언 */
	// --> import org.apache.logging.log4j.Logger;
	private static Logger logger = LoggerFactory.getLogger(Download.class);
	@Autowired
	SqlSession sqlSession;
	@Autowired
	WebHelper web;
	@Autowired
	UploadHelper upload;
	@Autowired
	MemberService memberService;
	@Autowired
	BbsDocumentService bbsDocumentService;
	@Autowired
	BbsCommentService bbsCommentService;

	@RequestMapping(value = "/member/out_ok.do")
	public ModelAndView doRun(Locale locale, Model model, HttpServletRequest request, HttpServletResponse response) {

		web.init();

		/** (3) 로그인 여부 검사 */
		// 로그인 중이 아니라면 탈퇴할 수 없다.
		if (web.getSession("loginInfo") == null) {
			return web.redirect(web.getRootPath() + "/index.do", "로그인 후에 이용 가능합니다.");
		}

		/** (4) 파라미터 받기 */
		String userPw = web.getString("user_pw");
		logger.debug("userPw=" + userPw);

		if (userPw == null) {
			return web.redirect(null, "비밀번호를 입력하세요.");
		}

		/** (5) 서비스에 전달하기 위하여 파라미터를 Beans로 묶는다. */
		// 회원번호는 세션을 통해서 획득한 로그인 정보에서 취득.
		Member loginInfo = (Member) web.getSession("loginInfo");
		Member member = new Member();
		member.setId(loginInfo.getId());
		member.setUserPw(userPw);

		// 게시판과의 참조 관계 해제를 위한 조건값 설정
		BbsDocument document = new BbsDocument();
		document.setMemberId(loginInfo.getId());

		// 덧글과의 참조 관계 해제를 위한 조건값 설정
		BbsComment comment = new BbsComment();
		comment.setMemberId(loginInfo.getId());

		/** (6) Service를 통한 탈퇴 시도 */
		try {
			// 참조관계 해제
			bbsDocumentService.updateDocumentMemberOut(document);
			bbsCommentService.updateCommentMemberOut(comment);
			// 비밀번호 검사 --> 비밀번호가 잘못된 경우 예외발생
			memberService.selectMemberPasswordCount(member);
			// 탈퇴처리
			memberService.deleteMember(member);
		} catch (Exception e) {
			return web.redirect(null, e.getLocalizedMessage());
		}

		// 탈퇴되었다면 프로필 이미지를 삭제한다.
		upload.removeFile(loginInfo.getProfileImg());

		/** (7) 정상적으로 탈퇴된 경우 강제 로그아웃 및 페이지 이동 */
		web.removeAllSession();
		return web.redirect(web.getRootPath() + "/index.do", "탈퇴되었습니다.");
	}

}
