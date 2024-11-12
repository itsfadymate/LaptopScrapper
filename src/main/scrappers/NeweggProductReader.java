package main.scrappers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
	private static final List<String> failedUrls = new ArrayList<>();
	private static int pageCtr =1;
	private static  final Random r = new Random();
    public  static List<LaptopData> readProducts(boolean allowRefurbished) throws IOException{
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

	private static void readNextPage(String currUrl, List<LaptopData> products,boolean allowRefurbished) throws IOException {
		System.out.println("reading page " + (pageCtr)+"\nurl: "+currUrl);
		Document doc = Jsoup.connect(currUrl).get();
		Element nextDiv = doc.select("div.btn-group-cell").last();
		if (nextDiv==null){
			System.out.println("script got blocked by website");return;}
		products.addAll((readPageProducts(currUrl,allowRefurbished)));
		if (nextDiv.selectFirst("a[title=Next].is-disabled")!=null)
			return;
		pageCtr++;
		readNextPage(nextDiv.select("a").first().attr("href"),products,allowRefurbished);
		
		
	}
	private static  List<LaptopData> readPageProducts(String url,boolean allowRefurbished) throws IOException{
		Document doc = Jsoup.connect(url).get();
		List<LaptopData> products = new ArrayList<>();
		Elements items = doc.select("div.item-cell");
		System.out.println("	page has : " + items.size() + " products");

		for (Element item : items) {
			if (!allowRefurbished && item.select("span.item-open-box-italic").text().length()==0)continue;
			try {
				int delay = 2000 + r.nextInt(8000);
				System.out.println("artificial delay before visiting link in page "+pageCtr+" for: " + delay + " ms\n" + Utilities.debugginString);
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				System.out.println("couldn't delay thread");
				e.printStackTrace();
			}
			String itemUrl = item.select("a").first().attr("href");
			try {
				LaptopData lapData = getLaptopData(itemUrl);
				if (lapData.getCpu()!=null && lapData.getGpu()!=null && lapData.getRam()!=0 && lapData.getPrice()!=0){
					products.add(lapData);
					System.out.println(lapData);
				}
			}catch (Exception e) {
				System.out.println("couldn't extract item from " + itemUrl);
				e.printStackTrace();
				System.out.println(Utilities.debugginString);
			}
		}
		System.out.println("page "+pageCtr+" has been read");
		return products;
		
	}

	public static LaptopData getLaptopData(String url) throws IOException,ExctractionFailedException {
		LaptopData laptop = new LaptopData(Currency.USD);
		laptop.setPurchaseLink(url);
		Document doc = Jsoup.connect(url).get();
		Elements tables = doc.select("table.table-horizontal").select("tr");
		Elements name = doc.select("h1.product-title");
		//System.out.println(name);
		laptop.setName(Utilities.extractName(name.text()));
		
		
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
					String cpu = Utilities.extractCpu(tableData);
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
		if ( laptop.getGpu()==null || laptop.getRam()==0 || laptop.getPrice()==0 || (laptop.getSsd()+laptop.getHdd()==0)){
			throw new ExctractionFailedException();
		}
		return laptop;
	}
	public static void main(String[] args) {
		try {
			/*getLaptopData("https://www.newegg.com/aorus-16-bsf-73us654sh-16-0-intel-core-i7-13700h-16gb-geforce-rtx-4070-1tb-pcie-black/p/N82E16834725221");
			getLaptopData("https://www.newegg.com/p/1TS-000X-05PZ5");
			getLaptopData("https://www.newegg.com/black-asus-tuf-gaming-fx705dt-fx705dt/p/2WC-000N-0EJ63?Item=9SIA5WMKBC7023");
			getLaptopData("https://www.newegg.com/black-lenovo-ideapad-l340/p/2WC-000J-00S27?Item=9SIA8TKHUG2051");
			getLaptopData("https://www.newegg.com/black-lenovo-ideapad-l340/p/2WC-000J-00S49?Item=9SIA8TKHUG2056");
		*/
			List<LaptopData> l = readProducts(true);
			System.out.println("successful: " + l.size());
			System.out.println("failed: " + failedUrls.size());
			failedUrls.forEach(System.out::println);
	
			} catch (Exception e) {
			// TODO Auto-generated catcXh block
				System.out.println("main halted due exception");
			e.printStackTrace();
		}
	}
}
