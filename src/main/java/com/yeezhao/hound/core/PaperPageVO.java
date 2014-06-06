package com.yeezhao.hound.core;

public class PaperPageVO {
	
	private String scholarId;
	private String title;
	private String authors;
	private String scholarName;
	private String ptime;
	private String publishUrl;
	private String pdfUrl;
	private int citations;
	private String url;
	private String journal; 
	
	public String getJournal() {
		return journal;
	}
	public void setJournal(String journal) {
		this.journal = journal;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthors() {
		return authors;
	}
	public void setAuthors(String authors) {
		this.authors = authors;
	}
	public String getPtime() {
		return ptime;
	}
	public void setPtime(String ptime) {
		this.ptime = ptime;
	}
	public String getPublishUrl() {
		return publishUrl;
	}
	public void setPublishUrl(String publishUrl) {
		this.publishUrl = publishUrl;
	}
	public String getPdfUrl() {
		return pdfUrl;
	}
	public void setPdfUrl(String pdfUrl) {
		this.pdfUrl = pdfUrl;
	}
	
	public String getScholarId() {
		return scholarId;
	}
	public void setScholarId(String scholarId) {
		this.scholarId = scholarId;
	}
	public int getCitations() {
		return citations;
	}
	public void setCitations(int citations) {
		this.citations = citations;
	}
	public String getScholarName() {
		return scholarName;
	}
	public void setScholarName(String scholarName) {
		this.scholarName = scholarName;
	}
}
