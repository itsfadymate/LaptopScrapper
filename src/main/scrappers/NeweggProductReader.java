package main.scrappers;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import main.Currency;
import main.LaptopData;
import main.Utilities;
import main.Exceptions.ExctractionFailedException;

public class NeweggProductReader {

	public static LaptopData getLaptopData(String url) throws IOException,ExctractionFailedException {
		LaptopData laptop = new LaptopData(Currency.USD);
		laptop.setPurchaseLink(url);
		Document doc = Jsoup.connect(url).get();
		Elements tables = doc.select("table.table-horizontal").select("tr");
		Elements priceStrong = doc.select("div.price-current").select("Strong");
		System.out.println("priceStrong: " + priceStrong);
		laptop.setPrice(Integer.parseInt(priceStrong.text().replaceAll("\\D","")));

		for (Element tr :tables) {
			String tableHeader = tr.select("th").text().toLowerCase().replace(" ","");
			String tableData = tr.select("td").text();
			try {
				System.out.println("TH: " + tableHeader +"\n	 "+ tableData);
				if (tableHeader.contains("gpu") || tableHeader.contains("graphics")) {
					String gpu = Utilities.extractGpu(tableData);
					laptop.setGpu(gpu);
					System.out.println(gpu + Utilities.debugginString);
				}else if(tableHeader.equals("cpu")|| tableHeader.equals("cpuname") || tableHeader.contains("proccessor")) {
					String cpu = Utilities.extractcpu(tableData);
					laptop.setCpu(cpu);
					System.out.println(cpu + Utilities.debugginString);
				}else if(tableHeader.equals("memory") || tableHeader.equals("ram") ) {
					int ram = Utilities.extractRam(tableData);
					laptop.setRam(ram);
					System.out.println(ram + Utilities.debugginString);
				}else if(tableHeader.contains("ssd") || tableHeader.contains("storage") ) {
					int ssd = Utilities.extractSSD(tableData);
					laptop.setSsd(ssd);
					System.out.println(ssd + Utilities.debugginString);
				}else if (tableHeader.contains("HDD")) {
					int hdd = Utilities.extractHDD(tableData);
					laptop.setSsd(hdd);
					System.out.println(hdd + Utilities.debugginString);
				}
			}catch (ExctractionFailedException exc) {
				System.out.println("failed to find data from " + tableData + " with header: " + tableHeader);
				exc.printStackTrace();
			}
		}
		System.out.println(laptop);
		return laptop;
	}
	public static void main(String[] args) {
		try {
			getLaptopData("https://www.newegg.com/msi-thin-a15-b8vf-270us-15-6-amd-ryzen-9-8945hs-16gb-geforce-rtx-4060-1tb-pcie-black/p/N82E16834156648?Item=N82E16834156648");
			getLaptopData("https://www.newegg.com/acer-ph16-71-71av-16-0-intel-core-i7-13700hx-16gb-geforce-rtx-4060-1tb-pcie-black/p/N82E16834360249?Item=N82E16834360249");
		} catch (Exception e) {
			// TODO Auto-generated catcXh block
			e.printStackTrace();
		}
	}
}
