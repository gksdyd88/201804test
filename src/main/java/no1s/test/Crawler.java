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
		// �p�^�[������ a�^�O title�^�O
		Pattern aPattern = Pattern.compile("<a.*?href=\"(.*?)\".*?>", Pattern.DOTALL);
		Pattern titlePattern = Pattern.compile("<title>(.*?)</title>", Pattern.DOTALL);
		
		// ���C���y�[�W������W�J�n
		aTagList.add(DOMAIN);
		
		// �g��for�����Ɨ�O�X���[
		// JAVADOC : https://docs.oracle.com/javase/jp/8/docs/api/java/util/ConcurrentModificationException.html
		// �Q��URL : http://futurismo.biz/archives/2811/
		System.out.print("�N���[�����O���ł�");
		for(int i = 0; i < aTagList.size(); i++) {
			// html�擾
			String html = getHtml(aTagList.get(i));
			// �}�b�`���镶��������W
			collectMatches(aPattern, titlePattern, html);
			System.out.print(".");
		}
		System.out.println("");
		
		// ����I��
		if(aTagList.size() == titleTagList.size()) {
			System.out.println("�N���[�����O�����A���ʂ��o�͂��܂�");
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < aTagList.size(); i++) {
				sb.append(aTagList.get(i));
				sb.append("\t");
				sb.append(titleTagList.get(i));
				sb.append("\r\n");
			}
			System.out.println(sb.toString());
		} else {
			System.out.println("url�܂���title�̎��W�Ɏ��s���܂���");
			System.out.println("������x��������������");
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
