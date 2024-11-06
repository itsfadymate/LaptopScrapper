package main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.Exceptions.ExctractionFailedException;
import java.util.*;

public class Utilities {
	public static final String debugginString = "-------------------------------------";
	private static String namePattern = "(?i)^.*(gaming\\s*)?laptop";
	private static String gpuPattern = "((rtx\\s*\\d0\\d0)\\s*(ti)? | (gtx\\s*\\d{3}0\\s*(ti)?) )";
	private static String cpuPattern = "(core\\s*i\\d\\s*\\-?\\s*?\\d{5}\\s*[hfux]?)| (Ryzen\\s*\\d\\s*\\d{4}(HS)?)";
	private static String ramPattern = "(4|8|12|16|24|32)\\s*gb";
	private static String storagePattern = "(128|256|512|1|2)\\s*[tg]b";
	public static String extractName(String title) {
		
		Pattern p = Pattern.compile(namePattern,Pattern.CASE_INSENSITIVE);
		Matcher matcher = p.matcher(title);
		if (!matcher.find()) {
			System.out.println("failure extracting name");
			return title;
		}
		return matcher.group().replaceAll("(?i)(gaming\\s*)?laptop.*","");
	}
	
	public static String extractGpu(String gpu)throws ExctractionFailedException {
		
		Pattern p = Pattern.compile(gpuPattern,Pattern.CASE_INSENSITIVE);
		Matcher matcher  = p.matcher(gpu);
		if (!matcher.find()) {
			throw new ExctractionFailedException();
		}
		return matcher.group();
	}
	public static String extractcpu(String cpu)throws ExctractionFailedException {
		Pattern p = Pattern.compile(cpuPattern,Pattern.CASE_INSENSITIVE);
		Matcher matcher  = p.matcher(cpu);
		if (!matcher.find()) {
			throw new ExctractionFailedException();
		}
		return matcher.group();
	}
	public static int extractRam(String Memory)throws ExctractionFailedException {
		Pattern p = Pattern.compile(ramPattern,Pattern.CASE_INSENSITIVE);
		Matcher matcher  = p.matcher(Memory);
		if (!matcher.find()) {
			throw new ExctractionFailedException();
		}
		return Integer.parseInt(matcher.group().replaceAll("\\D",""));
	}
	public static int extractSSD(String Storage) {
		Pattern p = Pattern.compile(storagePattern,Pattern.CASE_INSENSITIVE);
		Matcher matcher  = p.matcher(Storage);
		if (!matcher.find())return 0;
		String match = matcher.group();
		int ans = Integer.parseInt(match.replaceAll("\\D",""));
		if (match.toLowerCase().contains("tb")) {ans*=1000;}
		return ans;
	}
	public static int extractHDD(String Storage) {
		if (Storage.toLowerCase().equals("no")) return 0;
		return extractSSD(Storage);
	}
	

	public static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    Pattern pattern = Pattern.compile("[0-9]*");
	    Matcher matcher = pattern.matcher(strNum);
	    return matcher.matches();
	}

	public static Map<String,String> extractCookies(String cookieheaders) {
		String[] cookiePairs = cookieheaders.split(";");

     
        Map<String, String> cookies = new HashMap<>();

        
        for (String cookie : cookiePairs) {
            String[] parts = cookie.trim().split("=", 2);
            if (parts.length == 2) {
                String name = parts[0];
                String value = parts[1];
                cookies.put(name, value); 
            }
        }

        
   
		return cookies;
		
		
	}
	public static void main(String[] args) {
		Map<String,String> cookies = extractCookies(
		"ubid-main=133-7150597-4080139;session-token=+ZSVGOhE3aDsqQpVdVqbXK8/MpK41j7DO3+lkyvvVGe4Eg5hXtaaH3lfg8LWcf92WldpQDdMt1HuLu6QLGV6f7VcdQEBovFRbpxKwx247A+A2dn5gzindZzB+n1OvTvt2slpxHQOcZ7+XUTNnrbsojeHm5xVtzjFHgN2c5AP4lx2C7ujh4h+bT4tPOv2wf/cEDNQRteENfdqe0P104sPEKaoQVwbSdNd+f0OUY0H/ghnLdHwc1xbIGr0tD20qHfqRzJkTttmYHwHXEFk1Oa4h/wD7k3/k9Q9Af2pN/HAgaYmKZxQ5AaCRQPBZnR1c6JuhQnD+mkcKqt/Iz+QQzG2n6zrjztBNjOn;i18n-prefs=USD;csm-hit=adb:adblk_yes&t:1730814478132&tb:HYAE08BEHASPNWRR5F55+sa-HYAE08BEHASPNWRR5F55-7YHPYOSBM00TQPGLVVRP|1730814478042;session-id-time=2082787201l;lc-main=en_US;session-id=139-7777589-5983951;skin=noskin"
		);
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }	
	}

	
	
}
