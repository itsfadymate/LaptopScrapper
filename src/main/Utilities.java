package main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.Exceptions.ExctractionFailedException;

public class Utilities {
	public static final String debugginString = "-------------------------------------";
	private static String gpuPattern = "((rtx\\s*\\d0\\d0)\\s*(ti)? | (gtx\\s*\\d{3}0\\s*(ti)?) )";
	private static String cpuPattern = "(core\\s*i\\d\\s*\\-\\s*?\\d{5}\\s*[hfux]?)| (Ryzen\\s*\\d\\s*\\d{4}(HS)?)";
	private static String ramPattern = "(4|8|12|16|24|32)\\s*gb";
	private static String storagePattern = "(128|256|512|1|2)\\s*[tg]b";
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
}
