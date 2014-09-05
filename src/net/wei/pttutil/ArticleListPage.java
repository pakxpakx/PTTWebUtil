package net.wei.pttutil;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ArticleListPage {
	private String boardName;
	private String html;

	public ArticleListPage(String boardName, String html) {
		this.boardName = boardName;
		this.html = html;
	}

	public List<PTTArticle> listArticles() {
		List<PTTArticle> result = new ArrayList<PTTArticle>();
		Document doc = Jsoup.parse(html);
		Element m = doc.getElementById("main-container");
		Element child = m.child(1);
		List<Element> articleList = child.children();
		for (Element e : articleList) {
			String mark = e.child(1).text();
			String title = e.child(2).text();
			String author = e.child(3).child(1).text();

			if (!title.substring(0, 2).equals("Re")
					&& !title.contains("(本文已被刪除)")&&!author.equals("-")) {

				String[] date = e.child(3).child(0).text().split("/");
				int dateMonth = Integer.parseInt(date[0]);
				int dateDay = Integer.parseInt(date[1]);
				String href = e.child(2).child(0).attr("href");

				PTTArticle article = new PTTArticle(title, author, dateMonth,
						dateDay, PTTWebUtil.PTTSITE + href);
				result.add(article);

			}

		}
		return result;
	}

	public String extractPreviousListPage() {
		Document doc = Jsoup.parse(html);
		Element m = doc.getElementById("main-container");
		Element child = m.child(0).child(0).child(1).child(1);
		return child.attr("href");
	}

	public String getBoardName() {
		return boardName;
	}

	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

}
