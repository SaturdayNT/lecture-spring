package study.spring.mysite.controller.member;

import java.util.List;
import java.util.Locale;
import java.util.Map;

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

import study.spring.helper.FileInfo;
import study.spring.helper.RegexHelper;
import study.spring.helper.UploadHelper;
import study.spring.helper.WebHelper;
import study.spring.mysite.controller.Download;
import study.spring.mysite.model.Member;
import study.spring.mysite.service.MemberService;

@Controller
public class EditOk {

	/** (1) 사용하고자 하는 Helper + Service 객체 선언 */
	// --> import org.apache.logging.log4j.Logger;
	private static Logger logger = LoggerFactory.getLogger(Download.class);
	@Autowired
	SqlSession sqlSession;
	@Autowired
	WebHelper web;
	@Autowired
	RegexHelper regex;
	@Autowired
	UploadHelper upload;
	@Autowired
	MemberService memberService;
	
	@RequestMapping(value = "/member/edit_ok.do")
	public ModelAndView doRun(Locale locale, Model model, HttpServletRequest request, HttpServletResponse response) {

		web.init();

		/** (3) 로그인 여부 검사 */
		Member loginInfo = (Member) web.getSession("loginInfo");
		// 로그인 중이라면 이 페이지를 동작시켜서는 안된다.
		if (loginInfo == null) {
			
			return web.redirect(web.getRootPath() + "/index.do", "로그인 후에 이용 가능합니다.");
			
		}

		/** (4) 파일이 포함된 POST 파라미터 받기 */
		try {
			upload.multipartRequest();
		} catch (Exception e) {
			
			return web.redirect(null, "multipart 데이터가 아닙니다.");
			
		}

		// UploadHelper에서 텍스트 형식의 파라미터를 분류한 Map을 리턴받아서 값을 추출한다.
		Map<String, String> paramMap = upload.getParamMap();
		String userPw = paramMap.get("user_pw");
		String newUserPw = paramMap.get("new_user_pw");
		String newUserPwRe = paramMap.get("new_user_pw_re");
		String name = paramMap.get("name");
		String email = paramMap.get("email");
		String tel = paramMap.get("tel");
		String birthdate = paramMap.get("birthdate");
		String gender = paramMap.get("gender");
		String postcode = paramMap.get("postcode");
		String addr1 = paramMap.get("addr1");
		String addr2 = paramMap.get("addr2");
		// 추가 - 이미지 삭제 여부에 대한 체크박스
		String imgDel = paramMap.get("img_del");

		// 전달받은 파라미터는 값의 정상여부 확인을 위해서 로그로 확인
		logger.debug("userPw=" + userPw);
		logger.debug("newUserPw=" + newUserPw);
		logger.debug("newUserPwRe=" + newUserPwRe);
		logger.debug("name=" + name);
		logger.debug("email=" + email);
		logger.debug("tel=" + tel);
		logger.debug("birthdate=" + birthdate);
		logger.debug("gender=" + gender);
		logger.debug("postcode=" + postcode);
		logger.debug("addr1=" + addr1);
		logger.debug("addr2=" + addr2);
		logger.debug("img_del=" + imgDel);

		/** (5) 입력값의 유효성 검사 (아이디 검사 수행안함) */
		// 현재 비밀번호 검사
		if (!regex.isValue(userPw)) {
			
			return web.redirect(null, "현재 비밀번호를 입력하세요.");
			
		}
		
		// 신규 비밀번호 검사
		// --> 신규 비밀번호가 입력된 경우는 변경으로 간주하고, 입력하지 않은 경우는
		//     변경하지 않도록 처리한다. 그러므로 입력된 경우만 검사해야 한다.
		if (regex.isValue(newUserPw)) {
			if (!regex.isEngNum(newUserPw) || newUserPw.length() > 20) {
				
				return web.redirect(null, "새로운 비밀번호는 숫자와 영문의 조합으로 20자까지만 가능합니다.");
				
			}
	
			// 비밀번호 확인
			if (!newUserPw.equals(newUserPwRe)) {
				
				return web.redirect(null, "비밀번호 확인이 잘못되었습니다.");
				
			}
		}

		// 이름 검사
		if (!regex.isValue(name)) {
			
			return web.redirect(null, "이름을 입력하세요.");
			
		}

		if (!regex.isKor(name)) {
			
			return web.redirect(null, "이름은 한글만 입력 가능합니다.");
			
		}

		if (name.length() < 2 || name.length() > 5) {
			
			return web.redirect(null, "이름은 2~5글자 까지만 가능합니다.");
			
		}

		// 이메일 검사
		if (!regex.isValue(email)) {
			
			return web.redirect(null, "이메일을 입력하세요.");
			
		}

		if (!regex.isEmail(email)) {
			
			return web.redirect(null, "이메일의 형식이 잘못되었습니다.");
			
		}

		// 연락처 검사
		if (!regex.isValue(tel)) {
			
			return web.redirect(null, "연락처를 입력하세요.");
			
		}

		if (!regex.isCellPhone(tel) && !regex.isTel(tel)) {
			
			return web.redirect(null, "연락처의 형식이 잘못되었습니다.");
			
		}

		// 생년월일 검사
		if (!regex.isValue(birthdate)) {
			
			return web.redirect(null, "생년월일을 입력하세요.");
			
		}

		// 성별검사
		if (!regex.isValue(gender)) {
			
			return web.redirect(null, "성별을 입력하세요.");
			
		}

		if (!gender.equals("M") && !gender.equals("F")) {
			
			return web.redirect(null, "성별이 잘못되었습니다.");
			
		}
		
		
		/** (6) 프로필 사진의 삭제가 요청된 경우 */
		if (regex.isValue(imgDel) && imgDel.equals("Y")) {
			// 세션에 보관되어 있는 이미지 경로를 취득
			upload.removeFile(loginInfo.getProfileImg());
		}

		/** (7) 업로드 된 파일 정보 추출 */
		// --> 이미지 수정을 원하지 않는 경우, 삭제만 원하는 경우
		//     데이터 없음
		List<FileInfo> fileList = upload.getFileList();
		// 업로드 된 프로필 사진 경로가 저장될 변수
		String profileImg = null;

		// 업로드 된 파일이 존재할 경우만 변수값을 할당한다.
		if (fileList.size() > 0) {
			// 단일 업로드이므로 0번째 항목만 가져온다.
			FileInfo info = fileList.get(0);
			profileImg = info.getFileDir() + "/" + info.getFileName();
		}

		// 파일경로를 로그로 기록
		logger.debug("profileImg=" + profileImg);

		/** (8) 전달받은 파라미터를 Beans 객체에 담는다. */
		//  아이디는 변경할 수 없으므로 제외한다.
		Member member = new Member();
		// WHERE절에 사용할 회원번호는 세션에서 취득
		member.setId(loginInfo.getId());
		member.setUserPw(userPw);
		member.setName(name);
		member.setEmail(email);
		member.setTel(tel);
		member.setBirthdate(birthdate);
		member.setGender(gender);
		member.setPostcode(postcode);
		member.setAddr1(addr1);
		member.setAddr2(addr2);
		// 변경할 신규 비밀번호
		member.setNewUserPw(newUserPw);
		
		if (profileImg != null) {
			// 이미지가 업로드 되었다면?
			// --> 이미지 교체를 위해서 업로드 된 파일의 정보를 Beans에 등록
			member.setProfileImg(profileImg);
		} else if (profileImg == null) {
			// 이미지가 업로드 되지 않았다면?
			// --> 삭제만 체크했을 경우
			if (imgDel != null && imgDel.equals("Y")) {
				// SQL에서는 공백일 경우 null로 업데이트 하도록 분기하고 있다.
				member.setProfileImg("");
			}
		}

		/** (9) Service를 통한 데이터베이스 저장 처리 */
		// 변경된 정보를 저장하기 위한 객체
		Member editInfo = null;
		try {
			memberService.selectMemberPasswordCount(member);
			memberService.updateMember(member);
			editInfo = memberService.selectMember(member);
		} catch (Exception e) {
			return web.redirect(null, e.getLocalizedMessage());
			
		} finally {
			
		}
		
		/** (10) 세션, 쿠키 갱신 */
		// 일단 쿠키의 썸네일 정보를 삭제한다.
		if (imgDel != null && imgDel.equals("Y")) {
			web.removeCookie("profileThumbnail");
		}
		
		// 프로필 이미지가 있을 경우 썸네일을 생성하여 쿠키에 별도로 저장
		String newProfileImg = editInfo.getProfileImg();
		if (newProfileImg != null) {
			try {
				String profileThumbnail = upload.createThumbnail(newProfileImg, 40, 40, true);
				web.setCookie("profileThumbnail", profileThumbnail, -1);
			} catch (Exception e) {
				return web.redirect(null, e.getLocalizedMessage());
				
			}
		}
		
		// 세션을 갱신한다.
		web.removeSession("loginInfo");
		web.setSession("loginInfo", editInfo);

		/** (11) 수정이 완료되었으므로 다시 수정페이지로 이동 */
		return web.redirect(web.getRootPath() + "/member/edit.do", 
				"회원정보가 수정되었습니다.");

		
		// INSERT,UPDATE,DELETE 처리를 수행하는 action 페이지들은 자체적으로 View를
		// 갖지 않고 결과를 확인할 수 있는 다른 페이지로 강제 이동시켜야 한다. 
		// 그러므로 View의 경로를 리턴하지 않는다.(중복실행 방지)
		
	}

}
