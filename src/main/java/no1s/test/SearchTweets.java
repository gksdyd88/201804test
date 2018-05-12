package no1s.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Iterator;

import twitter4j.MediaEntity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class SearchTweets {
	private static final String SEARCH_WORD = "JustinBieber";
	private static Twitter twitter;
	
	public static void main(String... args) {
		if(args.length < 4) {
			System.out.println("Warning! please enter access info.");
			System.out.println("args1 : CONSUMER_KEY, args2 : CONSUMER_SECRET, "
					+ "args3 : ACCESS_TOKEN, args4 : ACCESS_TOKEN_SECRET");
			System.exit(0);
		}
		System.out.println("検索を開始します");
		
		// 初期化
		initTwitter(args);
		
		int searchCount = 0;
		long resultId = 0;
		ArrayList<String> urlList = new ArrayList<String>();
		
		// 画像が10毎になるまで検索を繰り返し
		// なるべく無駄な通信を発生させないようにcount指定
		searchLoop:
		while(true) {
			QueryResult result = null;
			try {
				Query query = new Query(SEARCH_WORD);
				query.setCount(25);
				if(urlList.size() >= 1 && searchCount >= 1) {
					query.setMaxId(resultId - 1);
				}
				result = twitter.search(query);
				searchCount++;
			} catch (TwitterException e) {
				System.out.println("Twitter API error!! check access info!!");
				e.printStackTrace();
			}
			
			Iterator<Status> itr = result.getTweets().iterator();
			while(itr.hasNext()) {
				Status status = (Status) itr.next();
				MediaEntity[] mediaArray = status.getMediaEntities();
				for(MediaEntity media : mediaArray) {
					resultId = status.getId();
					urlList.add(media.getMediaURL());
					if(urlList.size() >= 10) {
						break searchLoop;
					}
				}
			}
		}
		
		// urlから画像を生成
		int imageNum = 1;
		for(String urlStr : urlList) {
			String fileName = generateFileName(urlStr, imageNum);
			FileOutputStream fos = null;			
			try {
				URL url = new URL(urlStr);
				ReadableByteChannel rbc = Channels.newChannel(url.openStream());
				fos = new FileOutputStream(fileName);
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			} catch (IOException e) {
				System.out.println("occured IOException!!");
				e.printStackTrace();
			} finally {
				if(fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			imageNum++;
		}
		System.out.println("検索した画像の保存が終了しました");
	}
	
	// 認証情報を設定
	private static void initTwitter(String... args) {
		if(twitter == null) {
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true)
				.setOAuthConsumerKey(args[0])
				.setOAuthConsumerSecret(args[1])
				.setOAuthAccessToken(args[2])
				.setOAuthAccessTokenSecret(args[3]);
			TwitterFactory tf = new TwitterFactory(cb.build());
			twitter = tf.getInstance();
		}
	}

	// ファイル名生成
	private static String generateFileName(String fileUrl, int num) {
		String[] splitUrl = fileUrl.split("\\.");
		return "JustinBieber_" + num + "." + splitUrl[splitUrl.length -1];
	}
}
