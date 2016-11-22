package study.spring.mysite.model;

public class BbsFile {
	private int id;
	private String originName;
	private String fileDir;
	private String fileName;
	private String contentType;
	private long fileSize;
	private String regDate;
	private String editDate;
	private int bbsDocumentId;

	/** getter, setter, toString() 추가 - 코드 제시는 생략합니다. */
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOriginName() {
		return originName;
	}

	public void setOriginName(String originName) {
		this.originName = originName;
	}

	public String getFileDir() {
		return fileDir;
	}

	public void setFileDir(String fileDir) {
		this.fileDir = fileDir;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getRegDate() {
		return regDate;
	}

	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}

	public String getEditDate() {
		return editDate;
	}

	public void setEditDate(String editDate) {
		this.editDate = editDate;
	}

	public int getBbsDocumentId() {
		return bbsDocumentId;
	}

	public void setBbsDocumentId(int bbsDocumentId) {
		this.bbsDocumentId = bbsDocumentId;
	}

	@Override
	public String toString() {
		return "BbsFile [id=" + id + ", originName=" + originName + ", fileDir=" + fileDir + ", fileName=" + fileName
				+ ", contentType=" + contentType + ", fileSize=" + fileSize + ", regDate=" + regDate + ", editDate="
				+ editDate + ", bbsDocumentId=" + bbsDocumentId + "]";
	}
}
