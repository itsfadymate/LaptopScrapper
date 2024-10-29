package main.scrappers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SoupTest {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			String gpu="",cpu="";
			Integer ram,hdd,ssd;
			Document doc = Jsoup.connect("https://www.newegg.com/acer-anv15-51-75he-15-6-intel-core-i7-13620h-16gb-geforce-rtx-4050-1tb-pcie-black/p/N82E16834360319").get();
			Elements allE = doc.select("div.product-wrap");
			Elements e2 = allE.select("div.product-bullets").select("li");
			for (Element e : e2) {
				System.out.println(e.text());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("couldn't connect");
			e.printStackTrace();
		}
	}
	

}
