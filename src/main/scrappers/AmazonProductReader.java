package main.scrappers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import main.Currency;
import main.LaptopData;
import main.Utilities;
import main.Exceptions.ExctractionFailedException;

public class AmazonProductReader {
	private final static String gamingLaptopsURL = "https://www.amazon.com/s?k=gaming+laptop&i=electronics&rh=n%3A172282%2Cp_n_feature_twenty_browse-bin%3A76501034011%7C76501072011%7C76501089011%7C76501098011%7C76501116011%7C76501125011%2Cp_n_condition-type%3A2224371011&dc=&low-price=&high-price=710";
	private final static List<LaptopData> laptops = new ArrayList<>();
	private final static List<String> failedUrls = new ArrayList<>();
	private static final String cookieHeaders = "ubid-main=133-7150597-4080139;session-token=+ZSVGOhE3aDsqQpVdVqbXK8/MpK41j7DO3+lkyvvVGe4Eg5hXtaaH3lfg8LWcf92WldpQDdMt1HuLu6QLGV6f7VcdQEBovFRbpxKwx247A+A2dn5gzindZzB+n1OvTvt2slpxHQOcZ7+XUTNnrbsojeHm5xVtzjFHgN2c5AP4lx2C7ujh4h+bT4tPOv2wf/cEDNQRteENfdqe0P104sPEKaoQVwbSdNd+f0OUY0H/ghnLdHwc1xbIGr0tD20qHfqRzJkTttmYHwHXEFk1Oa4h/wD7k3/k9Q9Af2pN/HAgaYmKZxQ5AaCRQPBZnR1c6JuhQnD+mkcKqt/Iz+QQzG2n6zrjztBNjOn;i18n-prefs=USD;csm-hit=adb:adblk_yes&t:1730814478132&tb:HYAE08BEHASPNWRR5F55+sa-HYAE08BEHASPNWRR5F55-7YHPYOSBM00TQPGLVVRP|1730814478042;session-id-time=2082787201l;lc-main=en_US;session-id=139-7777589-5983951;skin=noskin";
	private static final Map<String,String> cookies = Utilities.extractCookies(cookieHeaders);
	

	public static List<LaptopData> readProduct() {
		try {
			readNextPage(gamingLaptopsURL);
		} catch (IOException e) {
			System.out.println("couldn't connect to page");
			e.printStackTrace();
		}
		return laptops;
	}
	private static int pageCtr =1;
	private static final Random r = new Random();
	private static void readNextPage(String gaminglaptopsurl) throws IOException  {
		System.out.println("reading page " + (pageCtr));
		Document doc = Jsoup.connect(gaminglaptopsurl).cookies(cookies)
		        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.75 Safari/537.36 Brave/1.36.114")
		        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
		        .header("Accept-Language", "en-US,en;q=0.5")
		        .header("Referer", "https://www.amazon.com/")
		        .timeout(30000)
		        .get();
		readPageProducts(gaminglaptopsurl,doc);
		Element nextButton = doc.selectFirst(".s-pagination-next");
		if (!nextButton.tagName().equals("a"))return;
		String nextPageURL = "https://www.amazon.com" +nextButton.attr("href");
		System.out.println("next page (page: "+(pageCtr+1)+ ") url: " + nextPageURL );
		pageCtr++;
		readNextPage(nextPageURL);
	}

	private static void readPageProducts(String gaminglaptopsurl,Document doc) {
		Elements linksWithImages = doc.select("a:has(img.s-image)");
		for (Element aTag: linksWithImages) {
			String url ="https://www.amazon.com"+ aTag.attr("href");
			try {
				int delay = 2000 + r.nextInt(8000);
				System.out.println("artifical delay before visiting link in page "+pageCtr+" for: " + delay + " ms\n" + Utilities.debugginString);
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				System.out.println("couldn't delay thread");
				e.printStackTrace();
			}
			try {
				LaptopData data = getLaptopData(url);
				System.out.println(data);
			   laptops.add(data);
			}catch (Exception e) {
				System.out.println("failed to extract laptop data from: " + url);
				failedUrls.add(url);
			}
		}
	}
	
	private static LaptopData getLaptopData(String laptopUrl) throws IOException, ExctractionFailedException {
		LaptopData laptop = new LaptopData(Currency.USD);
		Document doc =Jsoup.connect(laptopUrl)
        .cookies(cookies)
        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.75 Safari/537.36 Brave/1.36.114")
        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
        .header("Accept-Language", "en-US,en;q=0.5")
        .header("Referer", "https://www.amazon.com/")
        .timeout(30000)
        .get();
		//System.out.println(doc);
		laptop.setPurchaseLink(laptopUrl);
		String title = doc.getElementById("productTitle").text();
		String name = Utilities.extractName(title);
		laptop.setName(name);
		String  priceString = doc.selectFirst("#corePrice_feature_div span.a-price-whole").text();
		laptop.setPrice(Utilities.extractPrice(priceString));
		Elements tableRows = doc.select("table.a-keyvalue.prodDetTable tbody tr");	
		laptop.setCpu(Utilities.extractCpu(title));
		for (Element tableRow : tableRows) {
			final String th = tableRow.selectFirst("th").text().toLowerCase();
			final String td = tableRow.selectFirst("td").text();
			try {
				/*if (th.contains("CPU Model Number")) {
					Element procSeriesData = tableRow.nextElementSibling().selectFirst("td");
					laptop.setCpu(Utilities.extractCpu(procSeriesData.text() + td));
				}else */
				if (th.contains("graphics coprocessor")) {
					laptop.setGpu(Utilities.extractGpu(td));
				}else if (th.contains("memory installed") && !th.contains("cache")) {
					laptop.setRam(Utilities.extractRam(td));
				}else if (th.contains("hard disk description")) {
					String storage = doc.selectFirst("tr:has(th:contains(Hard-Drive Size)) td").text();
					if (td.toLowerCase().equals("hdd")) {
						laptop.setHdd(Utilities.extractHDD(storage));
					}else {
						laptop.setSsd(Utilities.extractSSD(storage));
					}
				}
			}catch (Exception e) {
				System.out.println("couldn't read table row with th: " + th + " td: " + td);
				e.printStackTrace();
			}
		}
		return laptop;
	}

	

	public static void main(String[] args) {
		/*try {
		  	
			System.out.println(getLaptopData("https://www.amazon.com/MSI-GF63-i5-11400H-Windows-Aluminum/dp/B0B7D19CDC/ref=sr_1_6?s=electronics&sr=1-6"));
		}catch (Exception e) {
			System.out.println("amazon main halted");
			e.printStackTrace();
		}
		try { 	
			System.out.println(getLaptopData("https://www.amazon.com/MSI-GF63-Thin-11UC-i5-11400H/dp/B0CDJBWY46/ref=sr_1_81?dib=eyJ2IjoiMSJ9.sEO4k7Eq-R8vFXh8ETmUBgz9Wtv-bq9d0r99TAo2S22cDz6_lWEZLT89TJHoGpaUqXG2Yx6oEIyEJGYvZR1y94PnneCMfAFHk4dyE6QYESBLgx60KV5Uzw_9KEOEsAEXtwxN-FJO-1asqg9VyDaUJyWig-3yiTuO_N4OtHeAC6DeZE2D4Ajs0_jLg2ZqIX5D83S48CCDlQ_xlwdoCwkwkNyZD9fTeGlwsj9Oa-6lJnQO4Fav2WbhKCB9nlyAnlx1xarek94el0nklqQbOzQUerKh6zbTHyGrR_FPOtAWXgo.s-w_--GSbUcfOuNwxXekOjJT-T50xHLPeIDMiUSugJ4&dib_tag=se&keywords=gaming+laptop&qid=1730888988&refinements=p_n_feature_twenty_browse-bin%3A76501034011%7C76501072011%7C76501089011%7C76501098011%7C76501116011%7C76501125011%2Cp_n_condition-type%3A2224371011%2Cp_36%3A-71000&s=electronics&sr=1-81"));
		}catch (Exception e) {
			System.out.println("amazon main halted");
			e.printStackTrace();
		}*/
		Long startTime = System.currentTimeMillis();
		try {
			AmazonProductReader.readProduct();
		}catch(Exception e) {
			System.out.println("main halted");
			e.printStackTrace();
		}finally {
			System.out.println("\n\n\n\nrunTime = " + ((System.currentTimeMillis() -startTime)/1000) + " seconds" );
			System.out.println("successfuly scrapped : " + laptops.size() + " laptops\n failed scrapping: " + failedUrls.size() + " laptops");
			failedUrls.stream().forEach(f->System.out.println(f));
		}

	}

}
