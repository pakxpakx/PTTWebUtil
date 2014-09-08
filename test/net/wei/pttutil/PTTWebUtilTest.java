package net.wei.pttutil;

import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

public class PTTWebUtilTest {

	@Test
	public void testListPTTArticle() {
		List<PTTArticle> articles = PTTWebUtil.listPTTArticle(50, "NTU");
		for (PTTArticle article : articles) {
			System.out.println(article.getTitle());
		}
	}

	@Test
	public void testListPTTArticleByDate() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -7);// 关键是这个7天前....
		List<PTTArticle> articles = PTTWebUtil.listPTTArticle(c.getTime(),
				"NTU");
		for (PTTArticle article : articles) {
			if (article.getTitle().contains("食物")) {
				System.out.println(article.getPostMonth() + "/"
						+ article.getPostDay() + article.getTitle());

			}
		}
	}

}
