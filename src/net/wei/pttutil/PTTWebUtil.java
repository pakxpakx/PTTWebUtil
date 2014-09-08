package net.wei.pttutil;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class PTTWebUtil {

	public static final String PTTSITE = "https://www.ptt.cc";
	private static SimpleDateFormat formatter = new SimpleDateFormat(
			"yyyy/MMM/dd HH:mm:ss", new Locale("en"));

	public static ArticleListPage loadLastListPage(String boardName) {

		String resultText = "failed to fetch page";

		WebClient webClient = new WebClient();

		ArticleListPage result = null;

		try {
			webClient.getOptions().setUseInsecureSSL(true);
			HtmlPage page = webClient.getPage(PTTSITE + "/bbs/" + boardName
					+ "/index.html");
			result = new ArticleListPage(boardName, page.asXml());

		} catch (FailingHttpStatusCodeException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static ArticleListPage loadLastListPage(String boardName,
			String indexAddr) {
		WebClient webClient = new WebClient();

		ArticleListPage result = null;

		try {
			webClient.getOptions().setUseInsecureSSL(true);
			HtmlPage page = webClient.getPage(PTTSITE + indexAddr);
			result = new ArticleListPage(boardName, page.asXml());

		} catch (FailingHttpStatusCodeException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static List<PTTArticle> listPTTArticle(int number, String boardName) {
		CrawlerStoper stoper = new ArticleNumberCrawlerStoper(number);
		return listPTTArticle(stoper, boardName);
	}

	public static List<PTTArticle> listPTTArticle(CrawlerStoper stoper,
			String boardName) {
		boolean stopExtract = false;
		List<PTTArticle> articleList = new ArrayList<PTTArticle>();
		String tempForPreviousPageURI = null;

		boolean firstFetch = true;

		while (stopExtract == false) {
			ArticleListPage articleListPage = null;
			if (firstFetch) {
				articleListPage = PTTWebUtil.loadLastListPage(boardName);
				firstFetch = false;
			} else {
				articleListPage = PTTWebUtil.loadLastListPage(boardName,
						tempForPreviousPageURI);

			}

			List<PTTArticle> temp = articleListPage.listArticles();
			Collections.reverse(temp);
			if (stoper.isStop(temp, articleList)) {
				stopExtract = true;
				stoper.afterStop(temp, articleList);

			} else if (articleListPage.isTop()) { // craw to the first article
				stopExtract = true;
			} else {
				for (PTTArticle article : temp) {
					articleList.add(article);

				}
				tempForPreviousPageURI = articleListPage
						.extractPreviousListPage();
			}
		}
		Collections.reverse(articleList);
		return articleList;

	}

	public static List<PTTArticle> listPTTArticle(Date date, String boardName) {

		CrawlerStoper stoper = new DateCrawlerStoper(date);
		return listPTTArticle(stoper, boardName);
	}

	interface CrawlerStoper {
		boolean isStop(List<PTTArticle> crawedArticleList,
				List<PTTArticle> storedArticleList);

		void afterStop(List<PTTArticle> crawedArticleList,
				List<PTTArticle> storedArticleList);
	}

	public static class ArticleNumberCrawlerStoper implements CrawlerStoper {

		private int articleNumber;

		ArticleNumberCrawlerStoper(int articleNumber) {
			this.articleNumber = articleNumber;
		}

		@Override
		public boolean isStop(List<PTTArticle> crawedArticleList,
				List<PTTArticle> storedArticleList) {
			return (crawedArticleList.size() + storedArticleList.size()) >= articleNumber;
		}

		@Override
		public void afterStop(List<PTTArticle> crawedArticleList,
				List<PTTArticle> storedArticleList) {
			for (PTTArticle article : crawedArticleList) {
				if (storedArticleList.size() < articleNumber) {
					storedArticleList.add(article);

				} else {
					break;
				}

			}
		}

	}

	public static class DateCrawlerStoper implements CrawlerStoper {
		// TODO: the constructor and all the methods
		private Date date;

		public DateCrawlerStoper(Date date) {

			this.date = date;
		}

		@Override
		public boolean isStop(List<PTTArticle> crawedArticleList,
				List<PTTArticle> storedArticleList) {

			boolean isStop = false;

			try {
				if (crawedArticleList.size() == 1) {
					Date articlePostTime = PTTWebUtil.fetchDate(crawedArticleList
							.get(0).getHref());
					if (articlePostTime.getTime() < this.date.getTime()) {
						isStop = true;
					}
				} else if (crawedArticleList.size() > 1) {
					Date articlePostTime = PTTWebUtil.fetchDate(crawedArticleList
							.get(crawedArticleList.size() - 1).getHref());

					Date tempTime = PTTWebUtil.fetchDate(crawedArticleList.get(0)
							.getHref());

					if (tempTime.getTime() < articlePostTime.getTime()) {
						articlePostTime = tempTime;
					}

					if (articlePostTime.getTime() < this.date.getTime()) {
						isStop = true;
					}
				}
			} catch (NullPointerException e) {

				e.printStackTrace();
			}
			return isStop;
		}

		@Override
		public void afterStop(List<PTTArticle> crawedArticleList,
				List<PTTArticle> storedArticleList) {
			boolean needCheck = true;
			List<PTTArticle> tempList = new ArrayList<PTTArticle>();
			for (int i = crawedArticleList.size() - 1; i >= 0; i--) {
				PTTArticle article = crawedArticleList.get(i);
				if (needCheck) {

					Date postDate = fetchDate(article.getHref());
					if (postDate.before(this.date)) {
						continue;
					} else {
						needCheck = false;
						tempList.add(article);
					}

				} else {
					tempList.add(article);

				}
			}
			Collections.reverse(tempList);
			storedArticleList.addAll(tempList);

		}

	}

	public static Date fetchDate(String href) {
		Calendar c = Calendar.getInstance();
		c.clear();

		WebClient webClient = new WebClient();

		Date postDate = null;
		HtmlPage page = null;
		try {
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setJavaScriptEnabled(false);
			page = webClient.getPage(href);
			String html = page.asXml();
			Document doc = Jsoup.parse(html);
			Element m = doc.getElementById("main-content");
			String dateText = m.child(3).child(1).text();
			String temp[] = dateText.split(" ");
			String month = temp[1];
			String day = temp[2];
			String time = temp[3];
			String year = temp[4];

			postDate = formatter.parse(year + "/" + month + "/" + day + " "
					+ time);

		} catch (FailingHttpStatusCodeException | IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			System.out.println(href);
		}
		return postDate;
	}
}
