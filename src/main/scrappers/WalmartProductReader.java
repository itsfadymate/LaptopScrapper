package main.scrappers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import main.Utilities;

public class WalmartProductReader {

	public static void main(String[] args) {
try {
	 
	Document doc = Jsoup.connect("https://www.walmart.com/search?q=gaming+laptop&typeahead=gaming+laptop&max_price=713&min_price=460&facet=graphic_card%3ANVIDIA+GeForce+RTX+3050%7C%7Cgraphic_card%3ANVIDIA+GeForce+RTX+3050+Ti%7C%7Cgraphic_card%3ANVIDIA+GeForce+RTX+2050%7C%7Cgraphic_card%3ANVIDIA+GeForce+GTX+1650%7C%7Cgraphic_card%3ANVIDIA+GeForce+RTX+2060%7C%7Cgraphic_card%3ANVIDIA+GeForce+RTX+4050&sort=price_low")
			       .get();
	Elements prodTitle = doc.select("div[data-testid=list-view] span[data-automation-id=product-title]");
	System.out.println(prodTitle.size());
	prodTitle.stream().forEach(e->System.out.println(e.text() +"\n"+ Utilities.debugginString));	
	} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}

	}

}
