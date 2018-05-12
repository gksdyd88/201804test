package no1s.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler {
	public static final String DOMAIN = "https://no1s.biz/";
	public static ArrayList<String> aTagList = new ArrayList<String>();
	public static ArrayList<String> titleTagList = new ArrayList<String>();
	
	public static void main(String... args) {
		// パターン生成 aタグ titleタグ
		Pattern aPattern = Pattern.compile("<a.*?href=\"(.*?)\".*?>", Pattern.DOTALL);
		Pattern titlePattern = Pattern.compile("<title>(.*?)</title>", Pattern.DOTALL);
		
		// メインページから収集開始
		aTagList.add(DOMAIN);
		
		// 拡張for文だと例外スロー
		// JAVADOC : https://docs.oracle.com/javase/jp/8/docs/api/java/util/ConcurrentModificationException.html
		// 参照URL : http://futurismo.biz/archives/2811/
		System.out.print("クローリング中です");
		for(int i = 0; i < aTagList.size(); i++) {
			// html取得
			String html = getHtml(aTagList.get(i));
			// マッチする文字列を収集
			collectMatches(aPattern, titlePattern, html);
			System.out.print(".");
		}
		System.out.println("");
		
		// 正常終了
		if(aTagList.size() == titleTagList.size()) {
			System.out.println("クローリング完了、結果を出力します");
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < aTagList.size(); i++) {
				sb.append(aTagList.get(i));
				sb.append("\t");
				sb.append(titleTagList.get(i));
				sb.append("\r\n");
			}
			System.out.println(sb.toString());
		} else {
			System.out.println("urlまたはtitleの収集に失敗しました");
			System.out.println("もう一度お試しください");
		}
	}
	
	private static String getHtml(String url) {
		URLConnection con = null;
		InputStream is = null;
		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();
        
		try {
			con = new URL(url).openConnection();
			con.setRequestProperty("User-agent","Mozilla/5.0");        
	        is = con.getInputStream();
	        reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	
	        String s = null;
	        while ((s = reader.readLine()) != null) {
	        	sb.append(s);
	        	sb.append("\r\n");
	        }
		} catch(IOException e) {
			System.out.println("getResponse occured IOException!! check URL!!");
			e.printStackTrace();
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	        
		return sb.toString();
	}
	
	private static void collectMatches(Pattern aPattern, Pattern titlePattern, String html) {
		Matcher matcher = aPattern.matcher(html);

		while (matcher.find()) {
			String href = matcher.group(1).replaceAll("\\s", "");
		    if(href.contains(DOMAIN)) {
		    	if(!aTagList.contains(href)) {
			    	aTagList.add(href);
		    	}
		    }
		}
		
		matcher = titlePattern.matcher(html);
		
		while (matcher.find()) {
			String title = matcher.group(1).replaceAll("\\s", "");
			titleTagList.add(title);
		}
	}
}
