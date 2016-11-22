package study.spring.mysite.controller.bbs;

import java.io.IOException;
import java.util.List;
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

import study.spring.helper.PageHelper;
import study.spring.helper.UploadHelper;
import study.spring.helper.WebHelper;
import study.spring.mysite.controller.Download;
import study.spring.mysite.model.BbsDocument;
import study.spring.mysite.service.BbsDocumentService;

@Controller
public class DocumentList {
	/** (1) 사용하고자 하는 Helper 객체 선언 */
	// --> import org.apache.logging.log4j.Logger;
	private static Logger logger = LoggerFactory.getLogger(Download.class);
	@Autowired
	SqlSession sqlSession;
	@Autowired
	WebHelper web;
	@Autowired
	BBSCommon bbs;
	@Autowired
	BbsDocumentService bbsDocumentService;
	@Autowired
	PageHelper pageHelper;
	@Autowired
	UploadHelper upload;

	@RequestMapping(value = "/bbs/document_list.do")
	public ModelAndView doRun(Locale locale, Model model, HttpServletRequest request, HttpServletResponse response) {
	
		web.init();

		/** (3) 게시판 카테고리 값을 받아서 View에 전달 */
		String category = web.getString("category");
		request.setAttribute("category", category);

		/** (4) 존재하는 게시판인지 판별하기 */
		try {
			String bbsName = bbs.getBbsName(category);
			request.setAttribute("bbsName", bbsName);
		} catch (Exception e) {
			return web.redirect(null, e.getLocalizedMessage());
		}

		/** (5) 조회할 정보에 대한 Beans 생성 */
		// 검색어
		String keyword = web.getString("keyword");

		BbsDocument document = new BbsDocument();
		document.setCategory(category);

		// 현재 페이지 수 --> 기본값은 1페이지로 설정함
		int page = web.getInt("page", 1);

		// 제목과 내용에 대한 검색으로 활용하기 위해서 입력값을 설정한다.
		document.setSubject(keyword);
		document.setContent(keyword);

		/** (6) 게시글 목록 조회 */
		int totalCount = 0;
		List<BbsDocument> documentList = null;
		
		// 게시판 종류가 갤러리인 경우 사진목록을 함께 조회함을 요청
		document.setGallery(category.equals("gallery"));
		
		try {
			// 전체 게시물 수
			totalCount = bbsDocumentService.selectDocumentCount(document);
			
			// 나머지 페이지 번호 계산하기
			// --> 현재 페이지, 전체 게시물 수, 한 페이지의 목록 수, 그룹갯수
			pageHelper.pageProcess(page, totalCount, 12, 5);

			// 페이지 번호 계산 결과에서 Limit절에 필요한 값을 Beans에 추가
			document.setLimitStart(pageHelper.getLimitStart());
			document.setListCount(pageHelper.getListCount());
			
			documentList = bbsDocumentService.selectDocumentList(document);
		} catch (Exception e) {
			return web.redirect(null, e.getLocalizedMessage());
		}
		
		// 조회결과가 존재할 경우 --> 갤러리라면 이미지 경로를 썸네일로 교체
		if (document.isGallery() && documentList != null) {
			for (int i=0; i<documentList.size(); i++) {
				BbsDocument item = documentList.get(i);
				String imagePath = item.getImagePath();
				if (imagePath != null) {
					String thumbPath = null;
					try {
						thumbPath = upload.createThumbnail(imagePath, 480, 320, true);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// 글 목록 컬렉션 내의 Beans 객체가 갖는 이미지 경로를 썸네일로 변경한다.
					item.setImagePath(thumbPath);
					logger.debug("thumbnail create > " + item.getImagePath());
				}
			}
		}

		/** (7) 조회 결과를 View에 전달 */
		request.setAttribute("documentList", documentList);
		// 사용자가 입력한 검색어를 View에 되돌려준다. --> 자동완성 구현을 위함
		request.setAttribute("keyword", keyword);
		// 페이지 번호 계산 결과를 View에 전달
		request.setAttribute("pageHelper", pageHelper);
		
		// 갤러리 종류라면 View의 이름을 다르게 설정한다.
		String view = "bbs/document_list";
		if (document.isGallery()) {
			view = "bbs/gallery_list";
		}

		return new ModelAndView(view);
	}
}
