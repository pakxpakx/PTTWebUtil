package net.wei.pttutil;

public class PTTArticle {
	private String author;
	private int postMonth;
	private int postDay;
	private String title;
	private String href;
	
	public PTTArticle(String title, String author, int dateMonth, int dateDay, String href) {
		this.author = author;
		this.title = title;
		this.postMonth = dateMonth;
		this.postDay = dateDay;
		this.href = href;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getPostMonth() {
		return postMonth;
	}

	public void setPostMonth(int postMonth) {
		this.postMonth = postMonth;
	}

	public int getPostDay() {
		return postDay;
	}

	public void setPostDay(int postDay) {
		this.postDay = postDay;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}
	
	
}
