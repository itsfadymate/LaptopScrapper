package main.scrappers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	private static final String cookieHeaders = "ubid-main=133-7150597-4080139;session-token=+ZSVGOhE3aDsqQpVdVqbXK8/MpK41j7DO3+lkyvvVGe4Eg5hXtaaH3lfg8LWcf92WldpQDdMt1HuLu6QLGV6f7VcdQEBovFRbpxKwx247A+A2dn5gzindZzB+n1OvTvt2slpxHQOcZ7+XUTNnrbsojeHm5xVtzjFHgN2c5AP4lx2C7ujh4h+bT4tPOv2wf/cEDNQRteENfdqe0P104sPEKaoQVwbSdNd+f0OUY0H/ghnLdHwc1xbIGr0tD20qHfqRzJkTttmYHwHXEFk1Oa4h/wD7k3/k9Q9Af2pN/HAgaYmKZxQ5AaCRQPBZnR1c6JuhQnD+mkcKqt/Iz+QQzG2n6zrjztBNjOn;i18n-prefs=USD;csm-hit=adb:adblk_yes&t:1730814478132&tb:HYAE08BEHASPNWRR5F55+sa-HYAE08BEHASPNWRR5F55-7YHPYOSBM00TQPGLVVRP|1730814478042;session-id-time=2082787201l;lc-main=en_US;session-id=139-7777589-5983951;skin=noskin";
	public static List<LaptopData> readProduct() {
		Utilities.extractCookies(cookieHeaders);
		try {
			readNextPage(gamingLaptopsURL);
		} catch (IOException e) {
			System.out.println("couldn't connect to page");
			e.printStackTrace();
		}
		return laptops;
	}
	private static int pageCtr =1;
	private static void readNextPage(String gaminglaptopsurl) throws IOException  {
		System.out.println("reading page " + (pageCtr++));
		Document doc = Jsoup.connect(gaminglaptopsurl).get();
		readPageProducts(gaminglaptopsurl,doc);
		Element nextButton = doc.selectFirst(".s-pagination-next");
		if (!nextButton.tagName().equals("a"))return;
		String nextPageURL = nextButton.attr("href");
		readNextPage(nextPageURL);
	}

	private static void readPageProducts(String gaminglaptopsurl,Document doc) {


	}
	
	private static LaptopData getLaptopData(String laptopUrl) throws IOException {
		LaptopData laptop = new LaptopData(Currency.USD);
		Document doc = Jsoup.connect(laptopUrl).get();
		System.out.println(doc);
		laptop.setPurchaseLink(laptopUrl);
		String name = Utilities.extractName(doc.getElementById("productTitle").text());
		laptop.setName(name);
		String  priceString = doc.selectFirst("#corePrice_feature_div span.a-price-whole").text();
		laptop.setPrice(Integer.parseInt(priceString));
		Elements tableRows = doc.select("table.a-keyvalue.prodDetTable tbody tr");	
		for (Element tableRow : tableRows) {
			final String th = tableRow.selectFirst("th").text();
			final String td = tableRow.selectFirst("td").text();
			try {
				if (th.contains("CPU Model Number")) {
					Element procSeriesData = tableRow.nextElementSibling().selectFirst("td");
					laptop.setCpu(Utilities.extractcpu(procSeriesData.text() + td));
				}else if (th.contains("Graphics Coprocessor")) {
					laptop.setGpu(Utilities.extractGpu(td));
				}else if (th.contains("Memory Installed")) {
					laptop.setRam(Utilities.extractRam(td));
				}else if (th.contains("Hard Disk Description")) {
					String storage = doc.selectFirst("tr:has(th:contains(Hard-Drive Size)) td").text();
					if (td.toLowerCase().equals("hdd")) {
						laptop.setHdd(Utilities.extractHDD(storage));
					}else {
						laptop.setHdd(Utilities.extractSSD(storage));
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
		try {
			System.out.println(getLaptopData("https://www.amazon.com/MSI-GF63-i5-11400H-Windows-Aluminum/dp/B0B7D19CDC/ref=sr_1_6?s=electronics&sr=1-6"));
		}catch (Exception e) {
			System.out.println("amazon main halted");
			e.printStackTrace();
		}

	}

}
