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

public class NeweggProductReader {
	private final static String gamingLaptopsURL ="https://www.newegg.com/p/pl?N=100157995%20601360966%20601411482%20601328394%20601333544%204131&d=gaming+laptop&Order=1&isdeptsrh=1&LeftPriceRange=300+700";
	private static List<String> failedUrls = new ArrayList<>(); 
    public static List<LaptopData> readProducts(boolean allowRefurbished) throws IOException{
		List<LaptopData> products = new ArrayList<>();
		Document doc = Jsoup.connect(gamingLaptopsURL).get();
		try {
			String pageCount = doc.select("span.list-tool-pagination-text").first().select("strong").text().split("/")[1];
			System.out.println("NewEgg has " + pageCount + " pages");
		}catch (Exception e) {
			System.out.println("NewEgg has ?? pages");
		}
		readNextPage(gamingLaptopsURL,products,allowRefurbished);
		return products;
	}
    private static int ctr=1;
	private static void readNextPage(String currUrl, List<LaptopData> products,boolean allowRefurbished) throws IOException {
		System.out.println("reading page " + (ctr++));
		Document doc = Jsoup.connect(gamingLaptopsURL).get();
		Element nextDiv = doc.select("div.btn-group-cell").last();
		if (nextDiv==null)
			System.out.println("script got blocked by website");
		products.addAll((readPageProducts(currUrl,allowRefurbished)));
		if (nextDiv.select("a.btn is-disabled").size()!=0)
			return;
		readNextPage(nextDiv.select("a").first().attr("href"),products,allowRefurbished);
		
		
	}
	private static  List<LaptopData> readPageProducts(String url,boolean allowRefurbished) throws IOException{
		Document doc = Jsoup.connect(url).get();
		List<LaptopData> products = new ArrayList<>();
		Elements items = doc.select("div.item-cell");
		for (Element item : items) {
			if (doc.select("span.item-open-box-italic").text().length()!=0)continue;
			String itemUrl = item.select("a").first().attr("href");
			try {
				LaptopData lapData = getLaptopData(itemUrl);
				if (lapData.getCpu()!=null && lapData.getGpu()!=null && lapData.getRam()!=0 && lapData.getPrice()!=0)
					products.add(lapData);
			}catch (Exception e) {
				System.out.println("couldn't extract item from " + itemUrl);
				e.printStackTrace();
			}
		}
		return products;
		
	}

	public static LaptopData getLaptopData(String url) throws IOException,ExctractionFailedException {
		LaptopData laptop = new LaptopData(Currency.USD);
		laptop.setPurchaseLink(url);
		Document doc = Jsoup.connect(url).get();
		Elements tables = doc.select("table.table-horizontal").select("tr");
		Elements name = doc.select("h1.product-title");
		//System.out.println(name);
		laptop.setName(name.text());
		
		Elements priceStrong = doc.select("div.price-current").select("Strong");
		//System.out.println("priceStrong: " + priceStrong.first());
		laptop.setPrice(Integer.parseInt(priceStrong.first().text().replaceAll("\\D","")));
		if (doc.select("div.product-price").select("div.tag-list").select("div.tag-text").size()!=0) {
			//System.out.println(doc.select("div.product-price").select("div.tag-list").select("div.tag-text"));
			laptop.setInSale(true);
		}
		

		for (Element tr :tables) {
			String tableHeader = tr.select("th").text().toLowerCase().replace(" ","");
			String tableData = tr.select("td").text();
			try {
				//System.out.println("TH: " + tableHeader +"\n	 "+ tableData);
				if (tableHeader.contains("gpu") || tableHeader.contains("graphics")) {
					String gpu = Utilities.extractGpu(tableData);
					laptop.setGpu(gpu);
					//System.out.println(gpu + Utilities.debugginString);
				}else if(tableHeader.equals("cpu")|| tableHeader.equals("cpuname") || tableHeader.contains("proccessor")) {
					String cpu = Utilities.extractcpu(tableData);
					laptop.setCpu(cpu);
					//System.out.println(cpu + Utilities.debugginString);
				}else if(tableHeader.equals("memory") || tableHeader.equals("ram") ) {
					int ram = Utilities.extractRam(tableData);
					laptop.setRam(ram);
					//System.out.println(ram + Utilities.debugginString);
				}else if(tableHeader.contains("ssd") || tableHeader.contains("storage") ) {
					int ssd = Utilities.extractSSD(tableData);
					laptop.setSsd(ssd);
					//System.out.println(ssd + Utilities.debugginString);
				}else if (tableHeader.contains("HDD")) {
					int hdd = Utilities.extractHDD(tableData);
					laptop.setSsd(hdd);
					//System.out.println(hdd + Utilities.debugginString);
				}
			}catch (ExctractionFailedException exc) {
				//System.out.println("failed to find data from " + tableData + " with header: " + tableHeader);
				//System.out.println("url: " + url);
				failedUrls.add(url);
				//exc.printStackTrace();
			}
		}
		System.out.println(laptop);
		
		return laptop;
	}
	public static void main(String[] args) {
		try {
			getLaptopData("https://www.newegg.com/aorus-16-bsf-73us654sh-16-0-intel-core-i7-13700h-16gb-geforce-rtx-4070-1tb-pcie-black/p/N82E16834725221");
			getLaptopData("https://www.newegg.com/p/1TS-000X-05PZ5");
			getLaptopData("https://www.newegg.com/p/2WC-0009-02SV6?Item=9SIA6V6KBF0591&cm_sp=SP-_-2576436-_-0-_-2-_-9SIA6V6KBF0591-_-gaming laptop-_-gaming|laptop-_-6");
		
			/*List<LaptopData> l = readProducts();
			System.out.println("successful: " + l.size());
			System.out.println("failed: " + failedUrls.size());
			failedUrls.stream().forEach(e->System.out.println(e));*/
	
			} catch (Exception e) {
			// TODO Auto-generated catcXh block
				System.out.println("main halted due exception");
			e.printStackTrace();
		}
	}
}
