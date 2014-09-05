package net.wei.pttutil;

import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class PTTWebUtil {

	public static final String PTTSITE = "https://www.ptt.cc";
	
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

	public static ArticleListPage loadLastListPage(String boardName, String indexAddr) {
		WebClient webClient = new WebClient();

		ArticleListPage result = null;

		try {
			webClient.getOptions().setUseInsecureSSL(true);
			HtmlPage page = webClient.getPage(PTTSITE+indexAddr);
			result = new ArticleListPage(boardName, page.asXml());

		} catch (FailingHttpStatusCodeException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<PTTArticle> listPTTArticle(int number, String boardName) {
		boolean stopExtract = false;
		List<PTTArticle> articleList = new ArrayList<PTTArticle>();
		String tempForLastPageURI = null;

		while (stopExtract == false) {
			ArticleListPage articleListPage = null;
			if (articleList.size() == 0) {
				articleListPage = PTTWebUtil.loadLastListPage(boardName);
			} else {
				articleListPage = PTTWebUtil.loadLastListPage(boardName,
						tempForLastPageURI);

			}

			List<PTTArticle> temp = articleListPage.listArticles();
			Collections.reverse(temp);
			if (temp.size() + articleList.size() >= number) {
				stopExtract = true;
				for (PTTArticle article : temp){
					if (articleList.size() < number){
						articleList.add(article);

					}else {
						break;
					}

				}
				
			} else if (temp.size() == 0) { // craw to the first article
				stopExtract = true;
			} else {
				for (PTTArticle article : temp){
					articleList.add(article);

				}
				tempForLastPageURI = articleListPage.extractPreviousListPage();
			}
		}
		Collections.reverse(articleList);
		return articleList;

	}
}
