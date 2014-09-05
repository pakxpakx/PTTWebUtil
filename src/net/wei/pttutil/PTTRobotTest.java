package net.wei.pttutil;

import java.util.List;

import org.junit.Test;

public class PTTRobotTest {

	@Test
	public void testListPTTArticle() {
		List<PTTArticle> articles = PTTWebUtil.listPTTArticle(50, "CATCH");
		for (PTTArticle article : articles){
			System.out.println(article.getTitle());
		}
	}

}
