package main;
//TODO: Setter methods need to reject new sets if not valid form
public class LaptopData {
	String purchaseLink;
	String gpu;
	String cpu;
	int hdd;
	int ssd;
	int ram;
	double price;
	Currency c;
	boolean inStock;
	boolean inSale;
	String description="";
	public String toString() {
		return "purchase link: " + purchaseLink +
				"\ngpu: " +gpu +
				"\ncpu: " + cpu+
				"\nhdd: " + hdd+
				"\nssd: " + ssd+
				"\nram: " + ram+
				"\nprice: " + price+
				"\ncurrency: " +c +
				"\ninStock: " + inStock+
				"\ninSale: " +inSale +
				"\ndescription: " + description;
	}	
	public LaptopData(Currency c) {
		this.c = c;
		this.inStock = true;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPurchaseLink() {
		return purchaseLink;
	}
	public void setPurchaseLink(String purchaseLink) {
		this.purchaseLink = purchaseLink;
	}
	public String getGpu() {
		return gpu;
	}
	public void setGpu(String gpu) {
		if (gpu==null)return;
		if (this.gpu!=null)return;
		this.gpu = gpu;
	}
	public String getCpu() {
		return cpu;
	}
	public void setCpu(String cpu) {
		if (cpu==null)return;
		if (this.cpu!=null)return;
		this.cpu = cpu;
	}
	public int getHdd() {
		return hdd;
	}
	public void setHdd(int hdd) {
		if (this.hdd==0)
		this.hdd = hdd;
	}
	public int getSsd() {
		return ssd;
	}
	public void setSsd(int ssd) {
		if (this.ssd==0)
		this.ssd = ssd;
	}
	public int getRam() {
		return ram;
	}
	public void setRam(int ram) {
		this.ram = ram;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public Currency getC() {
		return c;
	}
	public void setC(Currency c) {
		this.c = c;
	}
	public boolean isInStock() {
		return inStock;
	}
	public void setInStock(boolean inStock) {
		this.inStock = inStock;
	}
	public boolean isInSale() {
		return inSale;
	}
	public void setInSale(boolean inSale) {
		this.inSale = inSale;
	}
}
